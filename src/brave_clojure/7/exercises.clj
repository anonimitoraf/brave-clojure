(ns brave-clojure.7.exercises)

(defmacro custom-infix
  [[a op b]]
    (list op a b))
;; (custom-infix (1 + 2))

(defn custom-last
  [coll]
  [(drop-last 1 coll) (last coll)])
;; (prn (custom-last [1 2 3 4 5]))

(defn custom-first
  [coll]
  [(drop 1 coll) (first coll)])
;; (prn (custom-first [1 2 3 4 5]))

(def op-precedences {'* 1
                     '/ 1
                     '+ 2
                     '- 2})
(def max-op-precedence (apply max (map val op-precedences)))

(defmacro custom-infix-calculator
  ([expr] `(custom-infix-calculator [] ~expr 1))
  ([checked-terms [term & terms] precedence]
   (if (> precedence max-op-precedence)
     term
     (if (nil? terms)
       ;; We are processing the last term. Just append to the checked terms
       ;; then start the next pass
       `(custom-infix-calculator [] ~(concat checked-terms [term]) ~(inc precedence))
       (let [op-precedence (get op-precedences term)]
         ;; Non-operators or operators with a precedence that
         ;; we're currently not after just get pushed to the stack
         (if (not= op-precedence precedence)
           `(custom-infix-calculator ~(concat checked-terms [term]) ~terms ~precedence)
           ;; Otherwise, some complicated logic to generate an expression
           (let [[remaining-checked-terms operand1] (custom-last checked-terms)
                 [remaining-terms operand2] (custom-first terms)
                 operator term
                 new-expr (list term operand1 operand2)]
             `(custom-infix-calculator ~(concat remaining-checked-terms [new-expr])
                                       ~remaining-terms
                                       ~precedence))))))))

(prn (custom-infix-calculator (5 + 10 * 2 - 30 / 2)))
