(ns pathfinder.analyze.java
  (:require [pathfinder.analyze.analyzer :refer [Analyzer]])
  (:import [com.github.javaparser JavaParser]))

;;; TODO: methods
;;; TODO: how do inner classes/interfaces work?
;;; TODO: constructors as a different type?
(defn- extract-definitions [compilation-unit]
  (let [pkg (-> compilation-unit .getPackage)
        pkg-prefix (if pkg (str (.getName pkg) ".") "")]
    (->> compilation-unit
         .getTypes
         (map (fn [type]
                {:name (str pkg-prefix (.getName type))
                 :type (if (.isInterface type) :interface :class)
                 :pos {:line (.getBeginLine type)
                       :column (.getBeginColumn type)
                       :end-line (.getEndLine type)
                       :end-column (.getEndColumn type)}})))))

(defn- extract
  "Take source as a string and extract as much info as possible from
  it."
  [source]
  (let [compilation-unit (-> source
                             (.getBytes "UTF-8")
                             java.io.ByteArrayInputStream.
                             (JavaParser/parse "UTF-8"))]
    {:definitions (extract-definitions compilation-unit)
     ;; TODO:
     :usages []}))

(deftype JavaHeuristicAnalyzer []
  Analyzer
  (analyze [this source meta]
    (-> source
        extract
        (merge {:source source
                :meta meta}))))
