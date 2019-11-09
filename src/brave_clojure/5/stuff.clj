(ns brave-clojure.5.stuff)

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})
(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-dex (comp :dexterity :attributes))

;; (c-int character)
;; (c-str character)
;; (c-dex character)

(defn spell-slots
  [char]
  (int (inc (/ (c-int char) 2))))
(spell-slots character)

(defn spell-slots-via-partial
  [character]
  (comp int inc #(/ % 2) c-int))
(spell-slots character)
