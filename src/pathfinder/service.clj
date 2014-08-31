(ns pathfinder.service
  (:require [liberator.core :refer [resource defresource]]
            [compojure.core :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.route :as route]))

(defresource project []
  :available-media-types ["application/json"]
  :handle-ok (fn [_] "Hello, Project"))

(defresource file []
  :available-media-types ["application/json"]
  :handle-ok (fn [_] "Hello, File"))

(defresource query []
  :available-media-types ["application/json"]
  :handle-ok (fn [_] "Hello, Query"))

(defroutes main-routes
  (GET "/project" [] (project))
  (GET "/file" [] (file))
  (GET "/query" [] (query))
  (route/resources "/")
  (route/not-found "Not Found"))

