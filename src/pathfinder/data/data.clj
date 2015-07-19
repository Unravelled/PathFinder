(ns pathfinder.data.data
  (:require [schema.core :as s]))

(defprotocol Data
  (stash! [this data])
  (search [this params]))

(def pos-schema {:end-column s/Int
                 :column s/Int
                 :line s/Int
                 :end-line s/Int})

(def definition-types (s/enum :annotation
                              :class
                              :constructor
                              :definition
                              :enum
                              :function
                              :interface
                              :macro
                              :method
                              :multimethod
                              :protocol
                              :record
                              :struct))

(def doc-schema {:meta {:type s/Keyword
                        :project s/Str
                        :path s/Str}
                 :definitions [{:pos pos-schema
                                :type definition-types
                                :name s/Str}]
                 :usages [{:pos [pos-schema]
                           :name s/Str}]
                 :source s/Str})
