(defn custom-reduce
  ([f initial collection]
   (loop [output initial
          remaining collection]
     (if (empty? remaining)
       output
         (recur (f output (first remaining)) (rest remaining)))))
  ([f [head & tail]]
    (custom-reduce f head tail)))

(println (custom-reduce + 10 [1 2 3]))
(println (custom-reduce + [1 2 3]))
(println (custom-reduce + [1]))