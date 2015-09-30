(ns pathfinder.service
  (:require [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [liberator.core :refer [resource defresource]]
            [pathfinder.analyze :as analyze]
            [pathfinder.data.data :as data]
            [pathfinder.query :as query]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [schema.core :as s]))

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

(def search-results {:results [data/doc-schema]
                     :found s/Int})

(defn build-service [data]
  (letfn [(present [in] ;; TODO: I wouldn't expect presentation to live here long
            (-> in
                json/write-str
                response/response
                (response/content-type "application/json")
                (response/header "Access-Control-Allow-Origin" "*")))

          (store-file [project path body]
            (-> body
                (analyze/analyze {:project project :path path})
                (->> (data/stash! data))
                present))]
    (let [project-routes (routes
                          (PUT "/:project/*" {body :body {project :project path :*} :params}
                               (store-file project path (slurp body)))
                          (GET "/" {{query :q} :params}
                               (->> (query/parse query)
                                    (data/search data)
                                    (s/validate search-results)
                                    present))
                          (GET "/:project" {{project :project query :q} :params}
                               (->> (query/parse query)
                                    (merge {:project project})
                                    (data/search data)
                                    (s/validate search-results)
                                    present))
                          (GET "/:project/*" {{project :project path :* query :q} :params}
                               (->> (query/parse query)
                                    (merge {:project project :path path})
                                    (data/search data)
                                    (s/validate search-results)
                                    present)))]
      (routes
       (context "/projects" [] project-routes)
       (route/files "/" {:root "resources/public"})
       (route/not-found "Not Found")))))
