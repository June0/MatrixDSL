(ns sandbox.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(java.util.Date.)

(if (Boolean. true) "logical true" "logical false")
(if (boolean (Boolean. false)) "logical true" "logical false")
(if (Boolean/valueOf (Boolean. false)) "logical true" "logical false")

(macroexpand
  '(when true (print "Hello") (print "Clojure !")))

(macroexpand '(condp = value
               1 "one"
               2 "two"
               3 "three"
               (str "unexpected value, \"" value \")))

(defn sum [xs] (if (> (count xs) 0) (+ (first xs) (sum (rest xs))) 0))
(sum [1 2 3])

{ :k "four" :i 2.5 :j 1}

(map inc [1 2 3 4 5])
(map + [1 2 3] [4 5 6])
(reduce + [1 2 3 4 5])
(reduce + -1 [1 2 3 4 5])

(filter 0 (range 10))

(doc range)

(set! *warn-on-reflection* false)
(ns sandbox.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(java.util.Date.)

(if (Boolean. true) "logical true" "logical false")
(if (boolean (Boolean. false)) "logical true" "logical false")
(if (Boolean/valueOf (Boolean. false)) "logical true" "logical false")

(macroexpand
  '(when true (print "Hello") (print "Clojure !")))

(macroexpand '(condp = value
               1 "one"
               2 "two"
               3 "three"
               (str "unexpected value, \"" value \")))

(defn sum [xs] (if (> (count xs) 0) (+ (first xs) (sum (rest xs))) 0))
(sum [1 2 3])

{ :k "four" :i 2.5 :j 1}

(map inc [1 2 3 4 5])
(map + [1 2 3] [4 5 6])
(reduce + [1 2 3 4 5])
(reduce + -1 [1 2 3 4 5])

(filter 0 (range 10))

(doc range)

(set! *warn-on-reflection* false)
(defn len [x]
  (.length x))
(len [1 2 2])

(defn len2 [^String x]
  (.length x))
(len2 "ok ok")

(time (reduce + (map len (repeat 100000 "asdf"))))

(defn adder [n]
  #(+ n %1))

(def add2 (adder 2))

((adder 2) 2)

(add2 3)

(adder 2)

(defn make-foo []
  (let [counter (atom 0)]
   #(do (swap! counter inc) @counter)))
(make-foo)


(def foo (make-foo))
(def foo2 (make-foo))

(foo)
(foo2)

(class [1 2 3])
(eval '(+ 1 2))
'(+ 1 2)
(take 10 (range))
(class (take 10 (range)))

(def keymap {:a 1 :b 2 :c 3})
(keymap :a)
(dissoc keymap :a :b)

(seq (1))
(defn len [x]
  (.length x))
(len [1 2 2])

(defn len2 [^String x]
  (.length x))
(len2 "ok ok")

(time (reduce + (map len (repeat 100000 "asdf"))))

(defn adder [n]
  #(+ n %1))

(def add2 (adder 2))

((adder 2) 2)

(add2 3)

(adder 2)

(defn make-foo []
  (let [counter (atom 0)]
   #(do (swap! counter inc) @counter)))
(make-foo)


(def foo (make-foo))
(def foo2 (make-foo))

(foo)
(foo2)

(class [1 2 3])
(eval '(+ 1 2))
'(+ 1 2)
(take 10 (range))
(class (take 10 (range)))

(def keymap {:a 1 :b 2 :c 3})
(keymap :a)
(dissoc keymap :a :b)

(seq (1))
