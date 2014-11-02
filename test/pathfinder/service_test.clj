(ns pathfinder.service-test
  (:use midje.sweet
        pathfinder.core
        pathfinder.service
        compojure.core
        [ring.mock.request :only [request header]])
  (:require [compojure.handler :as handler]))

(facts "about the main routes"
  "Bare request returns hello world"
  (app {:uri "/" :request-method :get})
  => (contains {:status 404})
  (app {:uri "/projects" :request-method :get})
  => (contains {:status 200})
  (app {:uri "/projects" :request-method :post})
  => (contains {:status 200})
  (app {:uri "/projects/123" :request-method :get})
  => (contains {:status 200})
  (app {:uri "/projects/123" :request-method :put})
  => (contains {:status 200})
  (app {:uri "/file" :request-method :get})
  => (contains {:status 200})
  (app {:uri "/file" :params {:path "/some/path"} :body "test" :request-method :post})
  => (contains {:status 200 :body "I am full path:/some/path body: test"})
  )


