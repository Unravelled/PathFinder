(ns pathfinder.service
  (:require [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [liberator.core :refer [resource defresource]]
            [pathfinder.analyze :as analyze]
            [pathfinder.data.data :as data]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]))

(defn get-all-projects []
  "Sure")

(defn index-project [body]
  "Sounds good")

(defn get-project [id]
  "No")

(defn update-project [id body]
  "Bad idea")

(defn delete-project [id]
  "Really bad idea")

(defn get-file [path]
  (str "file path:" path))

(defn update-file [path body]
  "Are you sure?")

(defn delete-file [path]
  "Shouldn't")

;;; TODO: restore the unimplemented routes

(defn build-service [data]
  (letfn [(present [in] ;; TODO: I wouldn't expect presentation to live here long
            (-> in
                json/write-str
                response/response
                (response/content-type "application/json")))

          (store-file [project path body]
            (-> body
                (analyze/analyze {:project project :path path})
                (->> (data/stash data))
                present))

          (project-routes [project]
            (routes
             (PUT "/*" {body :body {path :*} :params}
                  (store-file project path (slurp body)))))]
    (routes
     (context "/projects/:project" [project] (project-routes project))
     (route/not-found "Not Found"))))
