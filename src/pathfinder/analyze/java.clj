(ns pathfinder.analyze.java
  (:require [pathfinder.analyze.analyzer :refer [Analyzer]])
  (:import [com.github.javaparser JavaParser]))

(defn- extract-pos-from-node [node]
  {:line (.getBeginLine node)
   :column (.getBeginColumn node)
   :end-line (.getEndLine node)
   :end-column (.getEndColumn node)})

(defmulti extract-defn
  "Extract metadata from a node of the AST."
  (fn [context type] (class type)))

(defmethod extract-defn com.github.javaparser.ast.CompilationUnit
  [_ compilation-unit]
  ;; JLS specifies zero-or-one package decls per file
  (let [pkg (-> compilation-unit .getPackage)
        pkg-name (if pkg (str (.getName pkg)) "")]
    (->> compilation-unit
         .getTypes
         (mapcat (partial extract-defn pkg-name)))))

(defmethod extract-defn com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
  [pkg-name type]
  (let [class-name (str pkg-name (if (empty? pkg-name) "" ".") (.getName type))]
    (conj
     (mapcat (fn [member] (extract-defn class-name member)) (.getMembers type))
     {:name class-name
      :type (if (.isInterface type) :interface :class)
      :pos (extract-pos-from-node type)})))

(defmethod extract-defn com.github.javaparser.ast.body.MethodDeclaration
  [class-name method]
  [{:name (str class-name "." (.getName method))
    :type :method
    :pos (extract-pos-from-node method)}])

(defmethod extract-defn com.github.javaparser.ast.body.ConstructorDeclaration
  [class-name constructor]
  [{:name (str class-name "." (.getName constructor))
    :type :constructor
    :pos (extract-pos-from-node constructor)}])

(defmethod extract-defn com.github.javaparser.ast.body.EnumDeclaration
  [name enum]
  [{:name (str name "." (.getName enum))
    :type :enum
    :pos (extract-pos-from-node enum)}])

(defmethod extract-defn com.github.javaparser.ast.body.AnnotationDeclaration
  [name annotation]
  [{:name (str name "." (.getName annotation))
    :type :annotation
    :pos (extract-pos-from-node annotation)}])

(defmethod extract-defn :default [_ _] [])

(defn- extract-usages [node]
  (if (instance? com.github.javaparser.ast.expr.MethodCallExpr node)
    (conj
     (mapcat extract-usages (.getChildrenNodes node))
     {:name (.getName node)
      :pos (extract-pos-from-node node)})
    (mapcat extract-usages (.getChildrenNodes node))))

(defn- group-usages [usages]
  (->> usages
       (group-by :name)
       (map (fn [[k v]] {:name k :pos (map :pos v)}))))

(defn- parse
  "Take source as a string and extract as much info as possible from
  it."
  [source]
  (let [compilation-unit (-> source
                             (.getBytes "UTF-8")
                             java.io.ByteArrayInputStream.
                             (JavaParser/parse "UTF-8"))]
    {:definitions (extract-defn nil compilation-unit)
     :usages (-> compilation-unit extract-usages group-usages)}))

(deftype JavaHeuristicAnalyzer []
  Analyzer
  (analyze [this source meta]
    (-> source
        parse
        (merge {:source source
                :meta meta}))))
