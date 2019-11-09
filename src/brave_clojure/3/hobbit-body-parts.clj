(def asym-hobbit-body-parts
  [{:name "head" :size 3}
   {:name "left-eye" :size 1}
   {:name "left-ear" :size 1}
   {:name "mouth" :size 1}
   {:name "nose" :size 1}
   {:name "neck" :size 2}
   {:name "left-shoulder" :size 3}
   {:name "left-upper-arm" :size 3}
   {:name "chest" :size 10}
   {:name "back" :size 10}
   {:name "left-forearm" :size 3}
   {:name "abdomen" :size 6}
   {:name "left-kidney" :size 1}
   {:name "left-hand" :size 2}
   {:name "left-knee" :size 2}
   {:name "left-thigh" :size 4}
   {:name "left-lower-leg" :size 3}
   {:name "left-achilles" :size 1}
   {:name "left-foot" :size 2}])

(defn make-other-part-name [part-name]
  (clojure.string/replace part-name #"^left-" "right-"))

(defn match-part
  [part]
  (let [other-part-name (make-other-part-name (:name part))
        other-size (* (:size part) 2)]
    {:name other-part-name :size other-size}))

;(println (symmetrize asym-hobbit-body-parts))

(defn symmetrize
  [parts]
  (loop [parts-to-match parts
         matched-parts []]
    (if (empty? parts-to-match)
      matched-parts
      (let [[curr-part & rest-parts] parts-to-match
            sym-parts-set (set [curr-part (match-part curr-part)])]
        (recur rest-parts
          (into matched-parts sym-parts-set))))))

(defn reduce-fn
  acc [maybe-curr]
  (let [curr maybe-curr
        matched-curr-set (set [curr (match-part curr)])]
    (into acc matched-curr-set)))

(defn symmetrize-by-reduce
  [parts]
  (reduce reduce-fn [] parts))

(println (symmetrize-by-reduce asym-hobbit-body-parts))

(defn hit
  [asym-body-parts]
  (let [sym-body-parts (symmetrize-by-reduce asym-body-parts)
        sym-body-parts-sum (reduce + (map :size sym-body-parts))
        target-sum (rand sym-body-parts-sum)]
        ()))
