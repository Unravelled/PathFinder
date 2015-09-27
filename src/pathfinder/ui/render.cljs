(ns pathfinder.ui.render
  (:require [cljs.core.async :as a]
            [quiescent.core :as q :include-macros true]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :as am]))

(defn enter-key?
  "Returns true if an even was the enter key"
  [evt]
  (= 13 (.-keyCode evt)))


(q/defcomponent Header
  "The page header, home of the search box"
  [_ submit-ch]
  (d/header {:id "header"}
            (d/h1 {:className "page-header"} "Pathfinder")
            (d/input {:id "search"
                      :placeHolder "Search..."
                      :onKeyDown
                      (fn [evt]
                        (when (enter-key? evt)
                          (let [v (.value (.-target evt))]
                            (am/go (a/>! submit-ch v))
                            (set! (.-value (.-target evt)) ""))))
                      :autoFocus true})))

(q/defcomponent Code-View
  "Renders the found text"
  [app]
  (d/div {:id "code"
          :className "code-view"}
         (:code-text app)))

(q/defcomponent App
  "The root of the application"
  [app channels]
  (d/div {}
         (Header nil (:submit channels))
         (d/section {:id "main"}
                    (Code-View app))))

(defn simple-render
  []
  (.log js/console "Test")
  (q/render (d/h1 "HELLO!") (.getElementById js/document "app")))

;; Here we use an atom to tell us if we already have a render queued
;; up; if so, requesting another render is a no-op
(let [render-pending? (atom false)]
  (defn request-render
    "Render the given application state tree."
    [app]
    (when (compare-and-set! render-pending? false true)
      (.requestAnimationFrame js/window
                              (fn []
                                (.log js/console (str  "rendering page" @(:state app)))
                                (q/render (App @(:state app) (:channels app))
                                          (.getElementById js/document "app"))
                                (reset! render-pending? false))))))
