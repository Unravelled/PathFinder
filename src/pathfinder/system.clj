(ns pathfinder.system
  (:require [ring.adapter.jetty :as jetty]
            [pathfinder.service :as service]))

(defn system
  "Create an application context."
  [config]
  {:server (service/app)
   :config config})

(defn start [system]
  (update-in system [:server] jetty/run-jetty (get-in system [:config :jetty] {})))

(defn stop [system]
  (.stop (:server system))
  system)
