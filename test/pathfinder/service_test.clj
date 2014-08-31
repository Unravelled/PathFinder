(ns pathfinder.service-test
  (:use midje.sweet
        pathfinder.core
        pathfinder.service
        compojure.core
        [ring.mock.request :only [request header]])
  (:require [compojure.handler :as handler]))

(facts "about the main routes"
  "Bare request returns hello world"
  ((handler/site main-routes) {:uri "/" :request-method :get})
  => (contains {:status 404})
  ((handler/site main-routes) {:uri "/project" :request-method :get})
  => (contains {:status 200})
  ((handler/site main-routes) {:uri "/file" :request-method :get})
  => (contains {:status 200})
  ((handler/site main-routes) {:uri "/query" :request-method :get})
  => (contains {:status 200})
  )


