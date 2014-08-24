(ns pathfinder.analyze.analyzer)

(defprotocol Analyzer
  (analyze [this source meta]))
