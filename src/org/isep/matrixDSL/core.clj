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

(def vector (vectorParser "[3,%2] + [3,%1] + [3,%3] + [3,%5] - [3,%6] + [3,%4]"))

(def dslmethod (let [compiled (let [compiler (PersistentVectorCompiler.)]
                                (.compileExpression compiler vector "DSL"))
                     cl (clojure.lang.DynamicClassLoader.)]
                       (.defineClass cl "DSL" compiled nil)))



(fn [& args] (clojure.lang.Reflector/invokeStaticMethod name meth-name (into-array args))

(PersistentVectorCompiler/test (vectorParser "[3,%2] + [3,%2] + [3,%3] + [3,%4] - [3,%10]"))

(comment
	(def vector-arith
	  (insta/parser
	   "prog = expr space (<';'> space expr)*
	    <expr> = assig | addsub
	    assig = varname space <'='> space expr
	    <addsub> = multdiv | add | sub
	    <space> = <#'\\s*'>"))
	(vector-arith "a=[3,%1];b=a+[3,%2];b+a"))
