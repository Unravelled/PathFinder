(ns pathfinder.analyze.clojure
  (:require [pathfinder.analyze.analyzer :refer [Analyzer]]
            [clojure.java.io :as io]
            [clojure.tools.reader.reader-types :as readers]
            [clojure.tools.reader :as reader]
            [clojure.tools.analyzer.jvm :as jvm-analyzer]))

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

(defn- extract-ns [state form]
  (if (and (list? form) (= (first form) 'ns))
    (assoc state :ns (second form))
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
      (update-in state [:model :definitions]
                 conj {:name (str (:ns state) (if (:ns state) "/" "") (second form))
                       :type (get types (first form) :unknown)
                       :pos (select-keys (meta form) [:column :line :end-column :end-line])})
      state)))

;;; TODO:
(defn- extract-usages [state form] state)

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
