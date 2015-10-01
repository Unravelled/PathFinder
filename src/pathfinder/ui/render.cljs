(ns pathfinder.ui.render
  (:require-macros [cljs.core.async.macros :as am])
  (:require [cljs.core.async :as a]
            [quiescent.core :as q]
            [quiescent.dom :as d]))

(defn enter-key?
  "Returns true if an event was the enter key"
  [evt]
  (= 13 (.-keyCode evt)))


(q/defcomponent Header
  "The page header, home of the search box"
  [_ submit-ch]
  (d/header {:id "header"
             :className "row"}
            (d/h1 {:className "page-header col-md-4"} "Pathfinder")
            (d/input {:id "search"
                      :placeholder "Search..."
                      :className "main-text form-control col-md-4"
                      :onKeyDown
                      (fn [evt]
                        (when (enter-key? evt)
                          (let [v (.-value (.-target evt))]
                            (am/go (a/>! submit-ch v)))))
                      :autoFocus true})))

(q/defcomponent Code-block
  :on-render #(.highlightBlock js/hljs %)
  [source]
  (d/pre nil (d/code nil source)))

(q/defcomponent Result-block
  [result]
  (d/div nil
         (d/h4 nil (str "package: " (get-in result [:meta :project])))
         (d/h4 nil (str "path: " (get-in result [:meta :path])))
         (Code-block (:source result))))

(q/defcomponent Result-view
  "Renders the found text"
  [app]
  (let [search-results (get-in app [:search-results :results])
        found-count (get-in app [:search-results :found])]
    (d/div nil 
           (d/div {:className (when (nil? found-count) "hidden")}
                  (str "Found " found-count " results"))
           (d/div {:id "code"
                   :className "code-view"}
                  (map #(Result-block %) search-results)))))

(q/defcomponent App
  "The root of the application"
  [app channels]
  (d/div {}
         (Header nil (:submit channels))
         (d/section {:id "main"}
                    (Result-view app))))

;; Here we use an atom to tell us if we already have a render queued
;; up; if so, requesting another render is a no-op
(let [render-pending? (atom false)]
  (defn request-render
    "Render the given application state tree."
    [app]
    (when (compare-and-set! render-pending? false true)
      (.requestAnimationFrame js/window
                              (fn []
                                (q/render (App @(:state app) (:channels app))
                                          (.getElementById js/document "app"))
                                (reset! render-pending? false))))))
