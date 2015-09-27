(ns ^:figwheel-always pathfinder.ui.core
    (:require-macros [cljs.core.async.macros :as am])
    (:require[om.core :as om :include-macros true]
             [om.dom :as dom :include-macros true]
             [pathfinder.ui.render :as render]
             [cljs.core.async :as a]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defn dummy-update
  [val]
  {:code-text (str "Thanks for submitting " val)})

(defn init-state
  "Startup the application state, including channels"
  []
  {:state (atom {:code-text ""})
   :channels {:submit (a/chan)}})

(defn init-update
  "Sets up the channels with the appropriate functions"
  [app]
  (let [chans (:channels app)]
    (am/go (while true
             (let [val (a/<! (:submit chans))
                   _ (.log js/console (str "on channel submit, receive value [" val "]"))
                   new-state (swap! (:state app) dummy-update val)]
               (render/request-render app))))))


#_(let [app (init-state)]
  (init-update app)
  (render/request-render app))

(render/simple-render)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

