(ns pathfinder.analyze.clojure-test
  (:use midje.sweet
        pathfinder.analyze.analyzer
        pathfinder.analyze.clojure))

(facts "about heuristic analysis"
       (fact "should keep the source"
             (analyze (->CljHeuristicAnalyzer) "foo" {}) => (contains {:source "foo"}))
       (fact "should keep the metadata"
             (analyze (->CljHeuristicAnalyzer) "foo" {:a :b}) => (contains {:meta {:a :b}}))
       (fact "should extract definitions"
             (:definitions (analyze (->CljHeuristicAnalyzer) "(defn foo [] 3)" {}))
             => (contains (contains {:name "foo"})))
       (fact "should extract definitions, namespaced"
             (:definitions (analyze (->CljHeuristicAnalyzer) "(ns test) (defn foo [] 3)" {}))
             => (contains (contains {:name "test/foo"}))))
