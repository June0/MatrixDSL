(ns org.isep.matrixDSL.matrix
  (:require [instaparse.core :as insta]))

(import org.isep.matrixDSL.java.MatrixCompiler)
(def matrixParser
  (insta/parser
   "S = addsub space (addsub)*
    <addsub> = add | sub | matrix
    add = addsub space <'+'> space matrix
    sub = addsub space <'-'> space matrix
    <space> = <#'\\s*'>
    matrix = <'['> width <','> height <','> argument <']'>
    width = #'\\d+'
    height = #'\\d+'
    argument= <'%'>#'[0-9]+'"))

(def matrix-exp (matrixParser "[2,3,%2] + [2,3,%1] + [2,3,%3] + [2,3,%1]"))
;;(print matrix-exp)

(defn compile-exp [class-name exp] 
  (let [compiled (.compileExpression (MatrixCompiler.) exp class-name)
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

(dsl (create-matrix [1 2 3 ] [3 4 5]) (create-matrix [3 4 5] [6 7 8]) (create-matrix [3 4 5] [6 7 8]))

