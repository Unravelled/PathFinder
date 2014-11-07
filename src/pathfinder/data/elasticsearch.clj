(ns pathfinder.data.elasticsearch
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [pathfinder.data.data :refer [Data]]))

(deftype ElasticSearch [conn]
  Data
  (stash [this data]
    (esd/create conn "docs" "doc" data)))

(defn ->ElasticSearch [es-config]
  (ElasticSearch. (esr/connect (:endpoint es-config))))
