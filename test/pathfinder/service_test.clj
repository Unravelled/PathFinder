(ns pathfinder.service-test
  (:use midje.sweet
        pathfinder.service
        compojure.core
        [ring.mock.request :only [request header]])
  (:require [compojure.handler :as handler]))

(facts "about the main routes"
  "Bare request returns hello world"
  ((handler/site main-routes) {:uri "/" :request-method :get})
  => (contains {:status 200
                :body "Hello World"}))
