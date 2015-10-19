(ns pathfinder.data.elasticsearch
  (:require [clojure.string :as str]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest
             [document :as esd]
             [index :as esi]]
            [pathfinder.data.data :as data :refer [Data]]
            [schema
             [coerce :as coerce]
             [core :as s]]))

;;; TODO: support the remaining query filters
(defn- es-search-query [{:keys [:project :path :terms :filetype :definition]}]
  {:bool {:must (as-> [] $
                  (if project (conj $ {:term {:project project}}) $)
                  (if filetype (conj $ {:term {:meta.type (name filetype)}}) $)
                  (if definition (conj $ {:match {:definitions.name definition}}) $)
                  ;; TODO: support globs -- do this with multiple path
                  ;; expressions, need to change indexing strategy too
                  (if path (conj $ {:prefix {:path path}}) $)
                  (if-not (empty? terms) (conj $ {:match {:_all (str/join " " terms)}}) $))}})

(defn- doc-id [data]
  (format "%s/%s"
          (get-in data [:meta :project] "UNKNOWN")
          (get-in data [:meta :path] "UNKNOWN")))

(def coerce-result (coerce/coercer [data/doc-schema] coerce/json-coercion-matcher))

(defn- extract-results [results]
  {:found (get-in results [:hits :total])
   :results (->> results :hits :hits (map :_source) coerce-result)})

(deftype ElasticSearch [conn]
  Data
  (stash! [this data]
    (esd/create conn "docs" "doc"
                (s/validate data/doc-schema data)
                :id (doc-id data))) ;; TODO: set a ttl?
  (search [this query]
    (extract-results (esd/search conn "docs" "doc" :query (es-search-query query)))))

(defn- setup-index! [conn]
  (let [mappings {:doc {:properties
                        {:meta {:properties
                                {:path {:type "string" :index "not_analyzed"}
                                 :project {:type "string" :index "not_analyzed"}}}}}}]
    (when-not (esi/exists? conn "docs")
      (esi/create conn "docs" :mappings mappings))))

(defn ->ElasticSearch [es-config]
  (let [es (esr/connect (:endpoint es-config))]
    (setup-index! es)
    (ElasticSearch. es)))
