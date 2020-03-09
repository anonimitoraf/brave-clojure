(ns brave-clojure.9.butter)

(def yak-butter-international
  {:store "Yak Butter International"
   :price 90
   :smoothness 90})
(def butter-than-nothing
  {:store "Butter Than Nothing"
   :price 150
   :smoothness 83})
;; This is the butter that meets our requirements
(def baby-got-yak
  {:store "Baby Got Yak"
   :price 94
   :smoothness 99})

(defn mock-api-call
  [result]
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  "If the butter meets our criteria, return the butter, else return false"
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

(time
 (let [butter-shops [yak-butter-international butter-than-nothing baby-got-yak]]
   (some (comp satisfactory? mock-api-call) butter-shops)))

(time (let [shop-promise (promise)]
   (doseq [shop [yak-butter-international butter-than-nothing baby-got-yak]]
     (future (if (satisfactory? (mock-api-call shop))
               (deliver shop-promise shop))))
   (deref shop-promise 2000 "timed out")))
