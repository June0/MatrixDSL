(ns sandbox.instaparse
  (:require [instaparse.core :as insta]))

(def as-and-bs
  (insta/parser
   "S = AB*; AB = A B; A = 'a'+; B = 'b'+"))

(as-and-bs "aaabbaabb")

(insta/transform
 {:A vector
  :AB vector}
 (as-and-bs "aaabbaabb"))

(def parse-int
  (insta/parser
   "S = (number <whitespace>)*; number = #'\\d+'; whitespace = #'\\s*'"))

(parse-int "1 2 3 44 5 6")
((parse-int "1 2 3 44 5 6") 0)

(def parse-number
  (insta/parser
   "S = ((integer | decimal) <whitespace>)*; integer = #'\\d+'; decimal = #'\\d+\\.\\d+'; whitespace = #'\\s*'"))

(parse-number "1 2 3.3 2 45.124")

(insta/transform
 {:S vector
  :number #(Integer/parseInt %)}
 (parse-int "1 2 3 44 5 6"))

(def parse-group-int
  (insta/parser
   "S = ((number | vector) whitespace)*;
vector = <'['> ((vector whitespace) | (number whitespace))+ <']'>;
number = #'\\d+'; <whitespace> = <#'\\s*'>"))

(def ref (parse-group-int "1 [4 [4 7 8] 2 5] 2 4 8123"))
(parse-group-int "1 [4 [4 7 8] 2 5] 2 4 8123")

(defn transform-group [to-transform]
  (insta/transform
   {:S vector
    :vector vector
    :number #(Integer/parseInt %)}
   to-transform))

(->> "1 [4 [4 7 8] 2 5] 2 4 8123" parse-group-int transform-group)
(parse-group-int "1 [4 [4 7 8] 2 5] 2 4 8123")

;; TODO : parser arithmetic expressions
;; TODO : check priority
(def alg-parser
  (insta/parser
   "S = add-exp
    add-exp = fact-exp (whitespace* <add-op> whitespace* fact-exp)*
    fact-exp = factor-par (whitespace* <fact-op> whitespace* factor-par)*
    <factor-par> = integer | <'('> whitespace* add-exp whitespace* <')'>
    <add-op> = (plus | minus)
    <fact-op> = (factor | divide)
    <op> = (plus | minus | factor | divide)
    integer = #'\\d+'
    plus = '+'
    minus = '-'
    factor = '*'
    divide = '/'
    <whitespace> = <#'\\s'>"))
(alg-parser "1 * (3 + 2) * 3 / 6")

(def arithmetic
  (insta/parser
   "expr = add-sub
    <add-sub> = mul-div | add | sub
    add = add-sub space* <'+'> space* mul-div
    sub = add-sub space* <'-'> space* mul-div
    <mul-div> = term | mul | div
    mul = mul-div space* <'*'> space* term
    div = mul-div space* <'/'> space* term
    <term> = number | space* <'('> space* add-sub space* <')'> space*
    number = #'\\d+'
    <space> = <#'\\s'>"))
(defn transform-arithmetic [to-transform]
  (insta/transform
   {:expr identity
    :number #(Integer/parseInt %)
    :add #(+ %1 %2)
    :sub -
    :mul *
    :div /}
   to-transform))
(arithmetic "(1 + 2) * 3 + 4 - 6 / 2")
(transform-arithmetic (arithmetic "(1 + 2) * 3 + 4 - 6 / 2"))

(def assign
  (insta/parser
   {}))

