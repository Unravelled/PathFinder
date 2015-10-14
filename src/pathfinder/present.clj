(ns pathfinder.present
  (:require [clojure.data.json :as json]
            [pathfinder.data.data :as data]
            [ring.util.response :as response]
            [schema.core :as s]))

(def error-result {:msg s/Str})

(def document-stash-result {})

(def search-results {:results [data/doc-schema]
                     :found s/Int
                     :time s/Int})

(def document-result {:document s/Str})

;;; TODO: we output in latin 1 (on my mac anyway, maybe default
;;; encoding?!). This is added to the content-type presumably by ring?

;;; TODO: generate these presentation functions?

(defn exception [ex]
  (-> {:msg (.getMessage ex)}
      (->> (s/validate error-result))
      json/write-str
      response/response
      (response/status 500)
      (response/content-type "application/vnd.pf.error+json; version=1.0")))

(defn document-stash [stash-result]
  (-> {}
      (->> (s/validate document-stash-result))
      json/write-str
      response/response
      (response/status 204)
      ;; TODO: set location header
      (response/content-type "application/vnd.pf.stash+json; version=1.0")))

(defn query-results [results]
  (-> results
      (->> (s/validate search-results))
      json/write-str
      response/response
      (response/status 200)
      (response/content-type "application/vnd.pf.results+json; version=1.0")))

(defn document [document]
  (-> document
      (->> (s/validate document-result))
      json/write-str
      response/response
      (response/status 200)
      (response/content-type "application/vnd.pf.document+json; version=1.0")))
