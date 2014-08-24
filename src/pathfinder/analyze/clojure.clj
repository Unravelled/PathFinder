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

(defn- extract-data-model [ast])

(deftype CljAnalyzer []
  Analyzer
  (analyze [this source meta]
    (-> source
        read-forms
        (->> (map jvm-analyzer/analyze))
        #_extract-data-model)))

(deftype CljHeuristicAnalyzer []
  ;; guess without full parse and eval context
  Analyzer
  (analyze [this source meta]
    (-> source
        read-forms)))
