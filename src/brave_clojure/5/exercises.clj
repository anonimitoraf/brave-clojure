(ns brave-clojure.5.exercises)

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence {:value 10}
                :strength {:value 4}
                :dexterity {:value 5}}})

(def attr #(comp % :attributes))
(def c2-int (attr :intelligence))
(c2-int character)

;; TODO Can this be implemented via recursion?
(defn custom-comp
  ([f] f)
  ([f g] (fn [x] (f (g x))))
  ([f g & fs]
   (let [collapsed-fs (fn [x]
                        (reduce (fn [v f] (f v))
                                ((first fs) x)
                                (rest fs)))
         ]
     (custom-comp f (custom-comp g collapsed-fs)))))

;; Tests
((custom-comp :value :intelligence :attributes) character)
((custom-comp :intelligence :attributes) character)
((custom-comp :attributes) character)

(defn custom-assoc-in
  [m [k & ks] v]
  (if (nil? ks)
      (assoc m k v)
      (assoc m k (custom-assoc-in (get m k {}) ks v))))

(defn custom-update-in
  [m [k & ks] f & args]
  (if (nil? ks)
    (let [v (get m k)
          g (partial f v)]
      (assoc m k (apply g args)))
    (let [sub-map (get m k {})
          g (partial custom-update-in sub-map ks f)]
      (assoc m k (apply g args)))))

;; Tests
(custom-update-in {:a {:b {:c 1}}} [:a :b :c] inc)
(custom-update-in {:a 1} [:a] inc)
(custom-update-in {:a {:b {:c 2}}} [:a :b :c] (partial #(* % %)))
(custom-update-in {:b 1} [:a] nil?)
