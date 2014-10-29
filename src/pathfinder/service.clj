(ns pathfinder.service
  (:require [liberator.core :refer [resource defresource]]
            [compojure.core :refer :all]
            [ring.middleware.json as middleware]
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
  "file")

(defn store-file [path body]
  "I am full")

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
  (context "/file/:path" [path] (defoutes file-routes
                              (GET "/" [] (get-file path))
                              (POST "/" {body :body} (store-file path body))
                              (PUT "/" {body :body} (update-file path body))
                              (DELETE "/" [] (delete-file path))))

  (route/not-found "Not Found"))


(def app
  (-> (handler/api main-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response))
