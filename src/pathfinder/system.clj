(ns pathfinder.system
  (:require [compojure.handler :as handler]
            [pathfinder.data.elasticsearch :as es]
            [pathfinder.present :as present]
            [pathfinder.service :as service]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.stacktrace :as trace]))

(defn present-exception [handler]
  (fn [request]
    (try (handler request)
         (catch Exception ex (present/exception ex)))))

(defn system
  "Create an application context."
  [config]
  (let [data (es/->ElasticSearch (:elasticsearch config))]
    {:handler (-> data
                  service/build-service
                  handler/api
                  present-exception)
     :data data
     :config config}))

(defn start [system]
  (assoc-in system [:server] (jetty/run-jetty (:handler system)
                                              (get-in system [:config :jetty] {}))))

(defn stop [system]
  (.stop (:server system))
  system)
