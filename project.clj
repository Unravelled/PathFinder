(defproject pathfinder "0.1.0-SNAPSHOT"
  :description "A code search application"
  :url "https://github.com/IHopeYouCanChangeThis/PathFinder"
  :license {:name "Apache License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.reader "0.8.7"]
                 [org.clojure/tools.analyzer.jvm "0.5.4"]
                 [clojurewerkz/elastisch "2.1.0-beta4"]
                 [compojure "1.1.8"]
                 [liberator "0.10.0"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.3.1"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]
                                  [org.clojure/tools.namespace "0.2.5"]]
                   :plugins [[lein-midje "3.1.3"]]
                   :source-paths ["dev"]}
             :uberjar {:aot :all}})
