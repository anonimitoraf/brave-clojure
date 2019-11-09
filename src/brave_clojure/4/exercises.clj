(ns brave-clojure.4.exercises)

(defn vectorMap
  "Crappy vector implementation of map (since I don't know how seqs work yet)"
  [f [x & xs]]
  ;; (cons (f x) xs)
  (if (= x nil)

    (if (not (empty? xs))
      (into (vectorMap f xs) [])
      ;; Recursion base case
      [])

     ;; TODO: Fix this repetition
    (cons (f x) (vectorMap f xs)))
  )
; (vectorMap #(* % 2) [1 2 3])

(defn append [xs x] (into xs [x]))
;(append [1 2 3] 4)
