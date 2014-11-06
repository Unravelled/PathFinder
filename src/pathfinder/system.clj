(ns pathfinder.system
  (:require [compojure.handler :as handler]
            [pathfinder.data.elasticsearch :as es]
            [pathfinder.service :as service]
            [ring.adapter.jetty :as jetty]))

(defn system
  "Create an application context."
  [config]
  (let [data (es/->ElasticSearch (:elasticsearch config))]
    {:server (-> data
                 service/build-service
                 handler/api)
     :data data
     :config config}))

(defn start [system]
  (update-in system [:server] jetty/run-jetty (get-in system [:config :jetty] {})))

(defn stop [system]
  (.stop (:server system))
  system)
