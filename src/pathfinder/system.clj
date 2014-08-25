(ns pathfinder.system
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [pathfinder.service :as service]))

(defn system
  "Create an application context."
  [config]
  {:server (handler/site service/main-routes)
   :config config})

(defn start [system]
  (update-in system [:server] jetty/run-jetty (get-in system [:config :jetty] {})))

(defn stop [system]
  (.stop (:server system))
  system)
