(ns brave-clojure.3.exercises)

(defn add100
  "Adds 100 to a number"
  [x]
  (+ x 100))
(println (add100 5))

(defn dec-maker
  "Returns a function that decrements a number n times"
  [n]
  (fn [x] (- x n)))
(def dec9 (dec-maker 9))
(println (dec9 10))

(defn mapset
  "Like the arr map but returns a set"
  [f xs]
  (into #{} (map f xs)))
(println (mapset inc [1 1 2 2]))

(defn replicate-body-part
  "A body part will be replicated n times if it contains digit/s in the end of its name"
  [body-part n]
  (let [pattern #"-\d+$"
        body-part-name (:name body-part)]
    (if (re-find pattern body-part-name)
      ;; Replicate body-part n times
      (let [part-count (range 1 (+ n 1))]
        (map (fn [i]
               {:name (clojure.string/replace body-part-name pattern (str "-" i))
                :size (* i (:size body-part))})
             part-count))
      ;; No need to replicate body-part, just return it as a singleton array
      [body-part])))

(println (replicate-body-part {:name "eye-2" :size 1} 3))
