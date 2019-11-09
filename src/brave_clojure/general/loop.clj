(defn byLoop
  [max-iterations]
  (loop [iteration 0]
    (println (str "Iteration via loop " iteration))
    (if (> iteration max-iterations)
      (println "Goodbye")
      (recur (inc iteration)))))

(byLoop 4)

(defn byOverloadRecursion
  [max-iterations]
  (let [recurse-fn
        (fn recurse-fn-inner
          ([] (recurse-fn-inner 0))
          ([iteration]
           (println (str "Iteration via overload recursion " iteration))
           (if (> iteration max-iterations)
             (println "Goodbye")
             (recurse-fn-inner (inc iteration)))))]
    (recurse-fn)))

(byOverloadRecursion 4)