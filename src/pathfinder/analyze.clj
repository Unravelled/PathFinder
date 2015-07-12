(ns pathfinder.analyze
  (:require [pathfinder.analyze.analyzer :as a]
            [pathfinder.analyze.clojure :refer [->CljHeuristicAnalyzer]]
            [pathfinder.analyze.java :refer [->JavaHeuristicAnalyzer]]))

(deftype IdentityAnalyzer []
  a/Analyzer
  (analyze [this source meta]
    {:source source
     :meta meta
     :definitions []
     :usages []}))

(def analyzers {:clojure (->CljHeuristicAnalyzer)
                :java    (->JavaHeuristicAnalyzer)})

(defn- detect-type [path]
  (cond (.endsWith path ".clj") :clojure
        (.endsWith path ".java") :java
        :else :unknown))

(deftype RoutingAnalyzer []
  a/Analyzer
  (analyze [this source meta]
    (let [type (detect-type (:path meta))]
      (a/analyze (get analyzers type (->IdentityAnalyzer))
                 source (merge {:type type} meta)))))

(defn analyze
  "Take a source file as a string and a map of metadata and produce a
  map containing the analysis of the source."
  [source meta]
  (a/analyze (->RoutingAnalyzer) source meta))
