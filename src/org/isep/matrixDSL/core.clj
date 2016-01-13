(ns org.isep.matrixDSL.core
  (:require [instaparse.core :as insta]))

(def vectorParser
  (insta/parser
   "S = addsub space (addsub)*
    <addsub> = add | sub | vector
    add = addsub space <'+'> space vector
    sub = addsub <'-'> vector
    <space> = <#'\\s*'>
    vector = <'['> vsize <','> argument <']'>
    vsize = #'\\d+'
    argument= <'%'>#'[0-9]+'"))
(vectorParser "[3,%1] + [3,%2]")

(comment
	(def vector-arith
	  (insta/parser
	   "prog = expr space (<';'> space expr)*
	    <expr> = assig | addsub
	    assig = varname space <'='> space expr
	    <addsub> = multdiv | add | sub
	    <space> = <#'\\s*'>"))
	(vector-arith "a=[3,%1];b=a+[3,%2];b+a"))
