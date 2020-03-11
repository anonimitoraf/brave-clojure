(ns brave-clojure.general.threading)

(defn transform [person]
  (-> person
      (assoc :hair-color :gray)
      (update :age inc)))

(transform {:age 1})
