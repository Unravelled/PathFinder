(ns pathfinder.service
  (:require [liberator.core :refer [resource defresource]]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
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

(defn store-file [path body]
  (str "I am full path:" path " body: " body))

(defn update-file [path body]
  "Are you sure?")

(defn delete-file [path]
  "Shouldnt")

(defroutes main-routes
  (context "/projects" [] (defroutes project-routes
                            (GET "/" [] (get-all-projects))
                            (POST "/" {body :body} (index-project body))
                            (context "/:id" [id] (defroutes project-routes
                                                   (GET "/" [] (get-project id))
                                                   (PUT "/" {body :body} (update-project id body))
                                                   (DELETE "/" [] (delete-project id))))))
  (context "/file" [path] (defroutes file-routes
                              (GET "/" [] (get-file path))
                              (POST "/" {body :body} (store-file path body))
                              (PUT "/" {body :body} (update-file path body))
                              (DELETE "/" [] (delete-file path))))

  (route/not-found "Not Found"))


(def app
  (wrap-params (handler/api main-routes)))
