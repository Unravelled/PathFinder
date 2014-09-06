(ns pathfinder.analyze.clojure
  (:require [clojure.string :as str]
            [clojure.tools.analyzer.jvm :as jvm-analyzer]
            [clojure.tools.reader :as reader]
            [clojure.tools.reader.reader-types :as readers]
            [pathfinder.analyze.analyzer :refer [Analyzer]]))

(defmacro def- [symbol init?]
  `(def ^:private ~symbol ~init?))

;; TODO: arbitrary code can be executed during read (and obviously
;; evaluation) of clojure code. Currently, these analyzers should only
;; be used for trusted source. We should look in to sandboxing this
;; process and probably killing it after a timeout.

(defn- read-forms [source]
  (let [pbr (readers/indexing-push-back-reader source)
        eof (Object.)]
    (take-while #(not (identical? % eof))
                (repeatedly #(reader/read pbr false eof)))))

(defn- extract-data-model [ast meta])

;; TODO: using jvm-analyzer we can do a full parse and build a
;; complete, perfectly accurate, ast. This is essentially the front half
;; of the clojure compiler. It would imply loading code in at the
;; granularity of a project. We would probably need to understand the
;; build system too. For now I've hacked up a fairly basic parser based
;; on heuristics, see CljHeuristicAnalyzer below.

(deftype CljAnalyzer []
  Analyzer
  (analyze [this source meta]
    (-> source
        read-forms
        (->> (map jvm-analyzer/analyze))
        #_(extract-data-model meta))))

(defn extract-pos-from-meta [form]
  (select-keys (meta form) [:column :line :end-column :end-line]))

(defn- prefix-spec? [form]
  (and (sequential? form) ; should be a list, but often is not
       (symbol? (first form))
       (not-any? keyword? form)
       (< 1 (count form)))) ; not a bare vector like [foo]

(defn- option-spec? [form]
  (and (sequential? form) ; should be a vector, but often is not
       (symbol? (first form))
       (or (keyword? (second form)) ; vector like [foo :as f]
           (= 1 (count form))))) ; bare vector like [foo]

(defn- reduce-libspecs [option-reducer]
  (fn help
    ([libspecs] (help nil libspecs))
    ([prefix libspecs]
       (reduce (fn [acc spec]
                 (cond (prefix-spec? spec) (help (first spec) (rest spec))
                       (option-spec? spec) (option-reducer acc prefix spec)
                       :else acc))
               []
               libspecs))))

(def- find-aliases
  (reduce-libspecs (fn [acc prefix spec]
                     (concat acc
                             (map vector (->> (rest spec)
                                              (partition 2)
                                              (filter #(= :as (first %)))
                                              (map second))
                                  (repeat (str (when prefix (str prefix "."))
                                               (first spec))))))))

(def- find-refers
  (reduce-libspecs (fn [acc prefix spec]
                     (->> (rest spec)
                          (partition 2)
                          (filter #(= :refer (first %)))
                          (mapcat second)
                          (map (fn [f] [f (str (when prefix (str prefix "."))
                                               (first spec) "/" f)]))
                          (concat acc)))))

(defn- extract-require-forms [coll]
  (->> coll
       (filter #(and (list? %) (= :require (first %))))
       (mapcat rest)))

(defn- extract-ns [state form]
  (if (and (list? form) (= (first form) 'ns))
    (let [require-forms (extract-require-forms form)]
      (assoc state :ns
             {:name (second form)
              :aliases (into {} (find-aliases require-forms))
              :refers (into {} (find-refers require-forms))}))
    state))

(defn- extract-definition [state form]
  ;; support only the clojure.core types for now
  (let [types '{def :definition
                definline :function
                definterface :interface
                defmacro :macro
                defmethod :method
                defmulti :multimethod
                defn :function
                defn- :function
                defprotocol :protocol
                defrecord :record
                defstruct :struct
                deftype :class}]
    (if (and (list? form) (.startsWith (str (first form)) "def")) ;crude
      (let [ns (get-in state [:ns :name])]
        (update-in state [:model :definitions]
                   conj {:name (str ns (if ns "/" "") (second form))
                         :type (get types (first form) :unknown)
                         :pos (extract-pos-from-meta form)}))
      state)))

(defn- merge-with-key
  "Merge maps with a common scalar key. All other values should be
  seqs."
  [key maps]
  (->> maps
       (group-by key)
       (map (fn [[key-value maps]]
              (->> maps
                   (map #(dissoc % key))
                   (apply merge-with concat)
                   (#(assoc % key key-value)))))))

(defn- find-usages [interesting-usage? build-usage form]
  (letfn [(reduce-form [form]
            (when (list? form)
              (cond
               (empty? form) nil
               (= (first form) 'quote) nil
               :else (if (interesting-usage? (first form))
                       (cons (build-usage (first form)) (mapcat reduce-form (rest form)))
                       (mapcat reduce-form (rest form))))))]
    (reduce-form form)))

(defn- split-f-name [f-name] (str/split (str f-name) #"/" 2))

(defn- build-usage [ns]
  (fn [f-name]
    (let [name (split-f-name f-name)
          resolved-name (if (= (count name) 2)
                          (str (get (:aliases ns) (symbol (first name))) "/" (second name))
                          (get (:refers ns) (symbol (first name))
                               ;; if not a referred fn then must be defined in this ns
                               (str (:name ns) "/" (first name))))]
      {:name resolved-name
       :pos [(extract-pos-from-meta f-name)]})))

(defn- explicitly-required [ns]
  (fn [f-name]
    (let [name (split-f-name f-name)]
      (if (= (count name) 2)
        (contains? (:aliases ns) (symbol (first name)))
        (contains? (:refers ns) (symbol (first name)))))))

(defn- defined-in-ns [definitions]
  (fn [f-name]
    (let [lookup (->> definitions
                      (map (comp second split-f-name :name))
                      (into #{}))]
      (lookup (str f-name)))))

(defn- any [& fns]
  (fn [x]
    (reduce #(or %1 (%2 x)) false fns)))

(defn- extract-usages [state form]
  (update-in state [:model :usages] (comp (partial merge-with-key :name) concat)
             (find-usages (any (explicitly-required (:ns state))
                               (defined-in-ns (get-in state [:model :definitions])))
                          (build-usage (:ns state))
                          form)))

(defn- guess-data-model [forms]
  (->> forms
       (reduce (fn [state form]
                 (-> state
                     (extract-ns form)
                     (extract-definition form)
                     (extract-usages form)))
               {:ns nil :model {}})
       :model))

(deftype CljHeuristicAnalyzer []
  ;; guess without full parse and eval context
  Analyzer
  (analyze [this source meta]
    (-> source
        read-forms
        guess-data-model
        (merge {:source source
                :meta meta}))))
