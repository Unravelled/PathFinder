(ns pathfinder.service
  (:require [liberator.core :refer [resource defresource]]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [pathfinder.analyze :as analyze]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.route :as route]))

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

;;; TODO:
(defn stash [output] output)
;;; TODO:
(defn present [output] (str output))

(defn store-file [project path body]
  (-> body
      (analyze/analyze {:project project
                        :path path
                        ;; TODO: the body should probably be a json
                        ;; wrapper containing both the content and
                        ;; metadata
                        :type :clojure})
      stash
      present))

(defn project-routes [project]
  (routes
   (PUT "/*" {body :body {path :*} :params}
        (store-file project path (slurp body)))))

(defroutes main-routes
  (context "/projects/:project" [project] (project-routes project))
  (route/not-found "Not Found"))
