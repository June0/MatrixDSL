(ns org.isep.matrixDSL.matrix
  (:require [instaparse.core :as insta]))

(import org.isep.matrixDSL.java.PersistentVectorCompiler)
(def matrixParser
  (insta/parser
   "S = addsub space (addsub)*
    <addsub> = add | sub | matrix
    add = addsub space <'+'> space matrix
    sub = addsub space <'-'> space matrix
    <space> = <#'\\s*'>
    matrix = <'['> vsize <','> argument <']'>
    vsize = #'\\d+'
    argument= <'%'>#'[0-9]+'"))

(def matrix-exp (matrixParser "[4,%2] + [4,%1]"))
(print matrix-exp)

(defn compile-exp [class-name exp] 
  (let [compiled (.compileExpression (PersistentVectorCompiler.) exp class-name)
        cl (clojure.lang.DynamicClassLoader.)]
         (.defineClass cl class-name compiled nil))
  (fn [& args] 
    (clojure.lang.Reflector/invokeStaticMethod class-name "run" (into-array args))))

(def dsl (compile-exp "MatrixDSL" matrix-exp))

(defn create-matrix [& args]
  (into-array (map int-array args)))

;; represent this matrix
;; (1 3)
;; (2 4)
(eval (create-matrix [1 2] [3 4]))

(dsl (create-matrix [1 2] [3 4]) (create-matrix [3 4] [6 7]))

(import org.isep.matrixDSL.java.PersistentVectorCompiler)
(def vectorParser
  (insta/parser
   "S = addsub space (addsub)*
    <addsub> = add | sub | vector
    add = addsub space <'+'> space vector
    sub = addsub space <'-'> space vector
    <space> = <#'\\s*'>
    vector = <'['> vsize <','> argument <']'>
    vsize = #'\\d+'
    argument= <'%'>#'[0-9]+'"))

(def vector-exp (vectorParser "[4,%2] - [4,%1] - [4,%3] + [4,%2]"))
(print vector-exp)
(defn compile-exp [class-name exp] 
  (let [compiled (.compileExpression (PersistentVectorCompiler.) exp class-name)
        cl (clojure.lang.DynamicClassLoader.)]
         (.defineClass cl class-name compiled nil))
  (fn [& args] 
    (clojure.lang.Reflector/invokeStaticMethod class-name "run" (into-array args))))

(def dsl (compile-exp "matrix-dsl" vector-exp))

(dsl (int-array [1 2 3 4]) (int-array [4 5 7 2]) (int-array [4 5 7 9]))
