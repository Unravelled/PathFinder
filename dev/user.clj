(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [pathfinder.system :as sys]
            [midje.repl :refer (autotest)]
            [pathfinder.data.data :as data]))

;;; this file will be loaded by the repl automatically on start up

;;; lifecycle

(def system
  "Entry point in to the current system state."
  nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'system
                  (constantly (sys/system {:jetty {:port 9400 :join? false}
                                           :elasticsearch {:endpoint "http://localhost:9200"}}))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system sys/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s] (when s (sys/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

;;; utilities
