(ns brave-clojure.6.exercises)

(defmacro simple-infix
  "Evaluates infix expressions, although brackets are not supported"
  ([expr] '(simple-infix expr [] []))
  ([[token expr-rest] operands operators]
   (if-not (number? token)
     ;; Encountered an operator, just push to operator stack
     '(simple-infix operands (conj operators token))
     ;; Check if we should evaluate * or /
     (if-let [operator(or
                       (= (first operators) * )
                       (= (first operators) / ))]
       (let [first-operand (first operands)
             second-operand token]
         '(simple-infix expr-rest
                        (conj operands (operator first-operand second-operand))
                        operators))
       '(simple-infix expr-rest (conj operands token) operators)))))
