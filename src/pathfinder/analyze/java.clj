(ns pathfinder.analyze.java
  (:require [pathfinder.analyze.analyzer :refer [Analyzer]])
  (:import [com.github.javaparser JavaParser]))

(defn- extract-from-node [node]
  {:pos {:line (.getBeginLine node)
         :column (.getBeginColumn node)
         :end-line (.getEndLine node)
         :end-column (.getEndColumn node)}})

(defmulti extract
  "Extract metadata from a node of the AST."
  (fn [context type] (class type)))

(defmethod extract com.github.javaparser.ast.CompilationUnit
  [_ compilation-unit]
  ;; JLS specifies zero-or-one package decls per file
  (let [pkg (-> compilation-unit .getPackage)
        pkg-name (if pkg (str (.getName pkg)) "")]
    (->> compilation-unit
         .getTypes
         (mapcat (partial extract pkg-name)))))

(defmethod extract com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
  [pkg-name type]
  (let [class-name (str pkg-name (if (empty? pkg-name) "" ".") (.getName type))]
    (conj
     (mapcat (fn [member] (extract class-name member)) (.getMembers type))
     (merge {:name class-name
             :type (if (.isInterface type) :interface :class)}
            (extract-from-node type)))))

(defmethod extract com.github.javaparser.ast.body.MethodDeclaration
  [class-name method]
  [(merge {:name (str class-name "." (.getName method))
           :type :method}
          (extract-from-node method))])

(defmethod extract com.github.javaparser.ast.body.ConstructorDeclaration
  [class-name constructor]
  [(merge {:name (str class-name "." (.getName constructor))
           :type :constructor}
          (extract-from-node constructor))])

(defmethod extract com.github.javaparser.ast.body.EnumDeclaration
  [name enum]
  [(merge {:name (str name "." (.getName enum))
           :type :enum}
          (extract-from-node enum))])

(defmethod extract com.github.javaparser.ast.body.AnnotationDeclaration
  [name annotation]
  [(merge {:name (str name "." (.getName annotation))
           :type :annotation}
          (extract-from-node annotation))])

(defmethod extract :default [_ _] [])

(defn- parse
  "Take source as a string and extract as much info as possible from
  it."
  [source]
  (let [compilation-unit (-> source
                             (.getBytes "UTF-8")
                             java.io.ByteArrayInputStream.
                             (JavaParser/parse "UTF-8"))]
    {:definitions (extract nil compilation-unit)
     ;; TODO:
     :usages []}))

(deftype JavaHeuristicAnalyzer []
  Analyzer
  (analyze [this source meta]
    (-> source
        parse
        (merge {:source source
                :meta meta}))))
