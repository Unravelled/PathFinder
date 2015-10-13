(ns pathfinder.query
  (:require [clojure.set :as set]
            [instaparse.core :as insta]
            [pathfinder.data.data :as data]
            [schema.core :as s]))

;;; TODO: include the result of parsing in the response so the user
;;; can validate their query. Need a way of mapping from the output
;;; struct to a user-friendly string.

(def query-schema {(s/required-key :terms) [s/Str]
                   (s/optional-key :project) s/Str
                   (s/optional-key :path) s/Str
                   (s/optional-key :filetype) s/Keyword
                   (s/optional-key :definition) s/Str
                   (s/optional-key :definition-type) data/definition-types
                   (s/optional-key :usage) s/Str})

(def ^:private parser
  (insta/parser
   "S        = ws? ((word | tagged) ws?)*
    tagged   = tag ws? word
    tag      = tag-word ':'
    tag-word = 'ft' | 'filetype' | 'proj' | 'project' | 'path' | 'type' | 'def' | 'definition' | 'usage'
    word     = #'\\S+'
    ws       = #'\\s+'"))

(defn- keep-tag [tag nodes]
  (->> nodes (filter (comp #{tag} first))))

(defn- select-child-1
  ([node] (second node))
  ([tag node] (->> node rest (keep-tag tag) first)))

(defn- extract-tagged [tagged-nodes]
  {(->> tagged-nodes (select-child-1 :tag) (select-child-1 :tag-word) select-child-1 keyword)
   (->> tagged-nodes (select-child-1 :word) select-child-1)})

(defn- normalize-keys [m]
  (let [key-map {:ft :filetype
                 :proj :project
                 :type :definition-type
                 :def :definition}]
    (set/rename-keys m key-map)))

(defn- normalize-file-type [m]
  (if-not (:filetype m) m
          (update-in m [:filetype] keyword)))

(defn- normalize-def-type [m]
  (if-not (:definition-type m) m
          (update-in m [:definition-type] keyword)))

(defn parse [input]
  (let [tree (->> (rest (parser input))
                  (remove (comp #{:ws} first)))
        tagged (->> tree
                    (keep-tag :tagged)
                    (map extract-tagged)
                    (reduce merge))]
    (->> tagged
         (merge {:terms (->> tree (keep-tag :word) (mapv second))})
         normalize-keys
         normalize-file-type
         normalize-def-type
         (s/validate query-schema))))
