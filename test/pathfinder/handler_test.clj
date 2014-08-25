(ns pathfinder.handler-test
  (:use midje.sweet
        pathfinder.core
        pathfinder.handler
        compojure.core
        [ring.mock.request :only [request header]]))

(facts "about the main routes"
  "Bare request returns hello world"
  (app {:uri "/" :request-method :get})
  => (contains {:status 200
                :body "Hello World"}))
