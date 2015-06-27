(ns pathfinder.data.elasticsearch
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.rest.index :as esi]
            [pathfinder.data.data :refer [Data] :as data]
            [schema.coerce :as coerce]
            [schema.core :as s]))

(defn- search-query [params]
  {:bool
   {:must [{:term {:project (:project params)}}
           {:prefix {:path (:path params)}}]}})

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
  (search [this params]
    (extract-results (esd/search conn "docs" "doc" :query (search-query params)))))

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
