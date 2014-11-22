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
             => (contains (contains {:name "test/foo"})))
       (fact "should only extract usages that have been explicitly brought in by ns"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns blah) (map inc [1 2 3])" {}))
             =not=> (contains (contains {:name "map"})))
       (fact "should only extract real usages, not quoted"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns blah) '(map inc [1 2 3])" {}))
             =not=> (contains (contains {:name "map"})))
       (fact "should extract interesting referred usages"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns test (:require [foo.bar :refer [baz]])) (+ (baz 6) 7)" {}))
             => (contains (contains {:name "foo.bar/baz"})))
       (fact "should not explode on :refer :all"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns test (:require [foo.bar :refer :all])) (+ (baz 6) 7)" {}))
             =not=> (contains (contains {:name "foo.bar/baz"})))
       (fact "should extract interesting aliased usages"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns test (:require [foo.bar :as f])) (+ (f/baz 6) 7)" {}))
             => (contains (contains {:name "foo.bar/baz"})))
       (fact "should extract multiple usages"
             (-> (analyze (->CljHeuristicAnalyzer)
                          "(ns test (:require [foo :as f])) (f/bar 3) (f/bar 6)" {})
                 :usages first :pos)
             => (every-checker sequential? #(> (count %) 1)))
       (fact "should extract usages inside literal vectors"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns blah (:require [foo.bar :refer [baz]])) [:x (baz 6)]" {}))
             => (contains (contains {:name "foo.bar/baz"})))
       (fact "should extract usages inside literal maps"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns blah (:require [foo.bar :refer [baz]])) {:x (baz 6)}" {}))
             => (contains (contains {:name "foo.bar/baz"})))
       (fact "should extract interesting referred usages with a nested prefix"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns blah (:require [prefix [foo :refer [bar]]])) (bar 2)" {}))
             => (contains (contains {:name "prefix.foo/bar"})))
       (fact "should extract interesting aliased usages with a nested prefix"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns blah (:require [prefix [foo :as f]])) (f/bar 2)" {}))
             => (contains (contains {:name "prefix.foo/bar"})))
       (fact "should extract usages of functions defined within same ns"
             (:usages (analyze (->CljHeuristicAnalyzer)
                               "(ns blah) (defn foo [x] (+ x 1)) (foo 6)" {}))
             => (contains (contains {:name "blah/foo"}))))
