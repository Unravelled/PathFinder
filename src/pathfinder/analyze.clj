(ns pathfinder.analyze
  (:require [pathfinder.analyze.analyzer :as a]
            [pathfinder.analyze.clojure :refer [->CljHeuristicAnalyzer]]))

(deftype IdentityAnalyzer []
  a/Analyzer
  (analyze [this source meta]
    {:source source
     :meta meta}))

(def analyzers {:clojure (->CljHeuristicAnalyzer)})

(deftype RoutingAnalyzer []
  a/Analyzer
  (analyze [this source meta]
    (a/analyze (get analyzers (:type meta) (->IdentityAnalyzer))
             source meta)))

(defn analyze
  "Take a source file as a string and a map of metadata and produce a
  map containing the analysis of the source."
  [source meta]
  (a/analyze (->RoutingAnalyzer) source meta))
