(defproject pathfinder "0.1.0-SNAPSHOT"
  :description "A code search application"
  :url "https://github.com/IHopeYouCanChangeThis/PathFinder"
  :license {:name "Apache License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main pathfinder.core
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}
             :uberjar {:aot :all}})
