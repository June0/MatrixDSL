(ns org.isep.matrixDSL.vector
  (:require [instaparse.core :as insta]))

(import org.isep.matrixDSL.java.VectorCompiler)
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

(def vector-exp (vectorParser "[6,%2] - [6,%1] - [6,%3] + [6,%2]"))
(print vector-exp)
(defn compile-exp [class-name exp] 
  (let [compiled (.compileExpression (VectorCompiler.) exp class-name)
        cl (clojure.lang.DynamicClassLoader.)]
         (.defineClass cl class-name compiled nil))
  (fn [& args] 
    (clojure.lang.Reflector/invokeStaticMethod class-name "run" (into-array args))))

(def dsl (compile-exp "matrix-dsl" vector-exp))

(dsl (int-array [1 2 3 4 5 7]) (int-array [4 5 7 2 7 9]) (int-array [4 5 7 9 1 2]))