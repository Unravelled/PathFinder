(ns pathfinder.query-test
  (:require [pathfinder.query :refer :all]
            [midje.sweet :refer :all]))

;;; query language that is:
;;; - easy to remember
;;; - easy and quick to type
;;; - able to query any aspect of our analyzed data

(facts "about edges"
       (fact "should accept an empty search"
             (parse "") => {:terms []}
             (parse "   ") => {:terms []}))

(facts "about simple query expressions"
       (fact "should search everything for an arbitrary string"
             (parse "foo") => {:terms ["foo"]})
       (fact "should search everything for an arbitrary string with spaces"
             (parse "foo bar baz") => {:terms ["foo" "bar" "baz"]})
       (fact "should support literals with either quote"))

(facts "about document narrowing by global metadata"
       (fact "should narrow by file type"
             (parse "ft: clojure") => (contains {:filetype :clojure}))
       (fact "should narrow by project"
             (parse " proj: projectx") => (contains {:project "projectx"})
             (parse "proj: projectx") => (contains {:project "projectx"})
             (parse "project: projectx") => (contains {:project "projectx"}))
       (fact "should narrow by explicit path"
             (parse "path: /foo/bar/baz.clj") => (contains {:path "/foo/bar/baz.clj"})
             (parse "path: foo.clj") => (contains {:path "foo.clj"})))

(facts "about finding definitions"
       (fact "should allow searching for a definition by name"
             (parse "def: foo_bar") => (contains {:definition "foo_bar"}))
       (fact "should allow searching for a definition by type and name"
             (parse "def: foo_bar type: method") =>
             (contains {:definition "foo_bar" :definition-type :method}))
       (fact "should fail if type is invalid"
             (parse "type: flub") => (throws)))

(facts "about finding usages"
       (fact "should allow searching for usages by name"
             (parse "usage: foo_bar") => (contains {:usage "foo_bar"})))

;;; TODO:

(facts "about specifying literals using quotes"
       (fact "should handle single quotes")
       (fact "should handle double quotes")
       (fact "should accept a single quote"
             (parse "'symbol") => {:terms ["'symbol"]})
       (fact "should accept a single double quote"
             (parse "\"symbol") => {:terms ["\"symbol"]}))

(facts "about combinators"
       (fact "should "))
