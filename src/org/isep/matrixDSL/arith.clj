(ns org.isep.matrixDSL.arith
  (:require [instaparse.core :as insta])
  (:import (clojure.asm         Opcodes Type ClassWriter)
           (clojure.asm.commons Method GeneratorAdapter)))
 
(def arith
  "grammar parsing our language. A programe is a sequence of instructions separated by a semi-colon.
  It takes arguments %1 ..%n.
  It returns the value of the last instruction."
  (insta/parser
   "prog = expr space (<';'> space expr)*
    <expr> = assig | addsub
    assig = varname space <'='> space expr
    <addsub> = multdiv | add | sub
    add = addsub space <'+'> space multdiv
    sub = addsub space <'-'> space multdiv
    <multdiv> = factor | mult |div
    mult = multdiv space <'*'> space factor
    div = multdiv space <'/'> space factor
    <factor> = number | <'('> space expr space <')'> | varget |assig
    <space> = <#'\\s*'>
    number = #'[0-9]+'
    varget = varname | argument
    varname = #'[a-zA-Z]\\w*'
    argument= <'%'>#'[0-9]+'"))

(defn interpret-instr [env ast]
  "Interpret an instruction with a given environnement.
  The result is stored in a special :res_ key of the environnement."
  (let [op-fun (fn [op]
                 (fn[{v1 :res_ :as env1} {v2 :res_ :as env2}]
                   (assoc (merge env1 env2) :res_ (op v1 v2))))]
    (insta/transform {:assig (fn[{varname :res_ :as env1} {value :res_ :as env2}]
                               (assoc (merge env1 env2) varname value :res_ value))
                      :add (op-fun +)
                      :sub (op-fun -)
                      :mult (op-fun *)
                      :div (op-fun quot)
                      :number #(assoc env :res_ (Integer/parseInt %)) 
                      :varname #(assoc env :res_ (keyword %)) 
                      :argument #(assoc env :res_ (keyword (str "%" %)))
                      :varget (fn [{varname :res_ :as env1}]
                                (assoc env1 :res_ (get env1 varname 0)))}
                     ast)))

(defn interpret [prog & args]
  "Interpret a given program with given arguments."
  (:res_ (let [env (into {} (map-indexed #(vector (->> %1 inc (str "%") keyword) %2) args))]
           (reduce interpret-instr env (rest prog))))) ;; rest to skip :prog tag

(comment
  (interpret (arith "a=%2*3;b=a+2*4+%1;b+2") 1 3)
  )

(arith "a=%2*3;b=a+2*4+%1;b+2")

(interpret (arith "a=%2*3;b=a+2*4+%1;b+2") 1 3)

(import org.isep.compilation.Hello)
(Hello.)
(Hello/hello 1)
(Hello/hey 1)


(defn varnames-to-indices [ast]
  "Builds a mapping of variables and argument names to local indices.
  The arguments have the first indices."
  (let [init {:nb-args 0 :vars #{}}
        merge-res (fn [& maps]
                    (hash-map :nb-args (apply max (map :nb-args maps) )
                              :vars (apply clojure.set/union (map :vars maps))))
        info (insta/transform {:prog merge-res
                               :assig merge-res
                               :add merge-res
                               :sub merge-res
                               :mult merge-res
                               :div merge-res
                               :number (fn [num] {:nb-args 0 :vars #{}})
                               :varname (fn [name] {:nb-args 0 :vars #{ name }} )
                               :varget merge-res
                               :argument (fn [arg] {:nb-args (Integer/parseInt arg) :vars #{}})
                               }                      
                              ast)]
    (->>
     info
     :vars
     (reduce (fn [[idx m] v]
               [(inc idx) (assoc m v idx)])
             [(:nb-args info) {:nb-args (:nb-args info)}])
     second)))

(defn generate-instr [mv instr]
"Generate the method call to an  org.objectweb.asm.MethodVisitor for a given instruction."
  (condp = (first instr)
    :load (.visitVarInsn mv Opcodes/ILOAD (int (second instr)))
    :store (.visitVarInsn mv Opcodes/ISTORE (int (second instr)))
    :loadi (.visitLdcInsn mv (int (second instr)))
    :addi (.visitInsn mv Opcodes/IADD)
    :subi (.visitInsn mv Opcodes/ISUB)
    :multi (.visitInsn mv Opcodes/IMUL)
    :divi (.visitInsn mv Opcodes/IDIV)
    :reti (.visitInsn mv Opcodes/IRETURN)
    )
  mv)

(defn compile-ast [name ast]
"Generate a class of given name with a run method implementing the given ast."
  (let [vars (varnames-to-indices ast)
        op-fun (fn[op]
                 (fn[instrs-v0 instrs-v1]
                   (conj (into instrs-v0 instrs-v1) [op])))
        prog (insta/transform {
                               :prog (fn[ & instrs] (conj (reduce into [[:loadi 0]] instrs) [:reti]))
                               :assig (fn[name instrs-val]
                                        (conj instrs-val [:store name]))
                               :add (op-fun :addi)
                               :sub (op-fun :subi)
                               :mult (op-fun :multi)
                               :div (op-fun :divi)
                               :number #(vector [:loadi (Integer/parseInt %)]) 
                               :varname #(vars %)
                               :argument #(dec (Integer/parseInt %))
                               :varget #(vector [:load %])}
                              ast)
        generate-prog #(reduce generate-instr % prog)
        cw (ClassWriter. (+ ClassWriter/COMPUTE_FRAMES ClassWriter/COMPUTE_MAXS ))
        init (Method/getMethod "void <init>()")
        meth-name "run"
        meth-sig (str \( (apply str (repeat (:nb-args vars) \I)) ")I")]
      (.visit cw Opcodes/V1_6 Opcodes/ACC_PUBLIC (.replace name \. \/) nil "java/lang/Object" nil)
      (doto (GeneratorAdapter. Opcodes/ACC_PUBLIC init nil nil cw)
        (.visitCode)
        (.loadThis)
        (.invokeConstructor (Type/getType Object) init)
        (.returnValue)
        (.endMethod))
      (doto (.visitMethod cw (+ Opcodes/ACC_PUBLIC Opcodes/ACC_STATIC) meth-name meth-sig nil nil )
        (.visitCode)
        (generate-prog)
        (.visitMaxs 0 0 )
        (.visitEnd))
      (.visitEnd cw)
      (let [b (.toByteArray cw)
            cl (clojure.lang.DynamicClassLoader.)]
        (.defineClass cl name b nil))
      (fn [& args] (clojure.lang.Reflector/invokeStaticMethod name meth-name (into-array args)))))

(comment
  (def compiled (compile-ast "Compiled" (arith "a=%2+2;

a=a+1;

b=   %1*3+1+a;(b+a)*(2+1)")))
  (compiled 1 2)
  )

(def compiled (compile-ast "Compiled" (arith "a=%2+2;

a=a+1;

b=   %1*3+1+a;(b+a)*(2+1)")))

(compiled 1 2)
