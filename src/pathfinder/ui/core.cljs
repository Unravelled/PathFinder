(ns ^:figwheel-always pathfinder.ui.core
  (:require-macros [cljs.core.async.macros :as am])
    (:require[om.core :as om :include-macros true]
             [om.dom :as dom :include-macros true]
             [pathfinder.ui.render :as render]
             [cljs.core.async :as a]
             [ajax.core :refer [GET]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defn update-state
  [app display-text]
  (let [new-state (swap! (:state app) assoc :search-results display-text)]
    (render/request-render app)))

(defn query-server
  [app val]
  (GET (str (:server-uri app) "/projects?q=" val)
       {:handler (partial update-state app)
        :response-format :json
        :keywords? true}))

(defn init-state
  "Startup the application state, including channels"
  []
  {:state (atom {:search-results ""})
   :server-uri "http://localhost:9400"
   :channels {:submit (a/chan)}})

(defn init-update
  "Sets up the channels with the appropriate functions"
  [app]
  (let [chans (:channels app)]
    (am/go (while true
             (let [val (a/<! (:submit chans))]
               (query-server app val))))))


(let [app (init-state)]
  (init-update app)
  (render/request-render app))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

