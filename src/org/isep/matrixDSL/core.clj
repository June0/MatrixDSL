(ns org.isep.matrixDSL.core
  (:require [instaparse.core :as insta]))

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

(def vector-exp (vectorParser "[10,%2] + [10,%1]"))
(defn compile-exp [class-name exp] 
  (let [compiled (.compileExpression (PersistentVectorCompiler.) exp class-name)
        cl (clojure.lang.DynamicClassLoader.)]
         (.defineClass cl class-name compiled nil))
  (fn [& args] 
    (clojure.lang.Reflector/invokeStaticMethod class-name "run" (into-array args))))

(def dsl (compile-exp "matrix-dsl" vector-exp))

(dsl (int-array [1 2 3 4 5 4 5 7 9 5]) (int-array [4 5 7 9 5 1 2 3 4 5]))


(comment
	(def vector-arith
	  (insta/parser
	   "prog = expr space (<';'> space expr)*
	    <expr> = assig | addsub
	    assig = varname space <'='> space expr
	    <addsub> = multdiv | add | sub
	    <space> = <#'\\s*'>"))
	(vector-arith "a=[3,%1];b=a+[3,%2];b+a"))
