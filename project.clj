(defproject pathfinder "0.1.0-SNAPSHOT"
  :description "A code search application"
  :url "https://github.com/IHopeYouCanChangeThis/PathFinder"
  :license {:name "Apache License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies [[clojurewerkz/elastisch "2.1.0-beta9"]
                 [com.github.javaparser/javaparser-core "2.0.0"]
                 [compojure "1.1.8"]
                 [instaparse "1.4.1"]
                 [liberator "0.10.0"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3297"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/tools.analyzer.jvm "0.5.4"]
                 [org.clojure/tools.reader "0.10.0-alpha1"]
                 [org.omcljs/om "0.8.8"]
                 [prismatic/schema "0.4.3"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-devel "1.2.1"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 [sablono "0.3.4"]
                 [quiescent "0.2.0-RC2"]
                 [cljs-ajax "0.3.13"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.5"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main pathfinder.ui.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/pf.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :none
                                   :source-map-timestamp true }}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/pf.js"
                                   :main pathfinder.ui.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]
             :nrepl-port 7888}

  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [midje "1.6.3"]
                                  [org.clojure/tools.namespace "0.2.5"]
                                  [ring-mock "0.1.5"]]
                   :plugins [[lein-midje "3.1.3"]]
                   :source-paths ["dev"]}
             :uberjar {:aot :all}})
