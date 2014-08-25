(ns pathfinder.service
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes main-routes
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))
