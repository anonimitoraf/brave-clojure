(ns brave-clojure.13.general)

;;
;; Were-multimethods
;; ------------------------
(defmulti full-moon-behavior (fn [were-creature] (:were-type were-creature)))
(defmethod full-moon-behavior :wolf
  [were-creature]
  (str (:name were-creature) " will howl and murder"))
(defmethod full-moon-behavior :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people and sweat to the oldies"))
(defmethod full-moon-behavior nil
  [were-creature]
  (str (:name were-creature) " will stay at home and eat ice cream"))
(defmethod full-moon-behavior :default
  [were-creature]
  (str (:name were-creature) " will stay up all night fantasy footballing"))

;;
;; Inferring types during runtime
;; ------------------------
(defmulti types (fn [x y] [(class x) (class y)]))
(defmethod types [java.lang.String java.lang.String]
  [x y]
  "Two strings!")

;; TODO Put examples of heirarchical multimethods
;; See http://clojure.org/multimethods/

;;
;; Protocols
;; ------------------------
(defprotocol Psychodynamics
  "Plumb the inner depths of your data types"
  (thoughts [x] "The data type's innermost thoughts")
  (feelings-about [x] [x y] "Feelings about self or other"))

;; Implement Psychodynamics for when the first arg is a string
(extend-type java.lang.String
  Psychodynamics
  (thoughts [x] (str x " thinks, 'Truly, the character defines the data type'"))
  (feelings-about
    ([x] (str x " is longing for a simpler way of life"))
    ([x y] (str x " is envious of " y "'s simpler way of life"))))

;; When the first arg is a number
(extend-type java.lang.Number
  Psychodynamics
  (thoughts [x] (str "Your thought is a number with value: " x))
  (feelings-about
    ([x] (str "You have a good feeling about the number: " x))
    ([x y] (str "You used to like the number: " x " but " y " seems like the hip number nowadays"))))

;; Implement multiple types for a protocol at once
(extend-protocol Psychodynamics
  java.lang.Boolean
  (thoughts [x] "Truly, the character defines the data type")
  (feelings-about
    ([x] "longing for a simpler way of life")
    ([x y] (str "envious of " y "'s simpler way of life")))

  java.lang.Object
  (thoughts [x] "Maybe the Internet is just a vector for toxoplasmosis")
  (feelings-about
    ([x] "meh")
    ([x y] (str "meh about " y))))

;;
;; Records
;; ------------------------
(defprotocol WereCreature
  (full-moon-behavior-2 [x]))

(defrecord WereWolf [name title]
  WereCreature
  (full-moon-behavior-2 [x]
    (str name " will howl and murder")))

;;
;; Tests
;; ------------------------
(defn tests
  []

  (println) (str) (+ 1 2)

  (println (full-moon-behavior {:were-type :wolf
                                :name "Rachel from next door"}))
  (println (full-moon-behavior {:were-type :simmons
                                :name "Andy the baker"}))
  (println (full-moon-behavior {:were-type nil
                                :name "Martin the nurse"}))
  (println (full-moon-behavior {:were-type :office-worker
                                :name "Jimmy from sales"}))
  (println (types "String 1" "String 2"))

  (println (thoughts "blorb"))
  (println (feelings-about "schmorb"))
  (println (feelings-about "schmorb" 2))

  (println (thoughts 1))
  (println (feelings-about 2))
  (println (feelings-about 2 3))


  (println (thoughts false))
  (println (feelings-about true))
  (println (feelings-about false false))

  ;; 3 different ways to create a record
  (println (WereWolf. "David" "London Tourist"))
  (println (->WereWolf "Jacob" "Lead Shirt Discarder"))
  (println (map->WereWolf {:name "Lucian" :title "CEO of Melodrama"}))

  (println (full-moon-behavior-2 (map->WereWolf {:name "Lucian" :title "CEO of Melodrama"}))))

;; (tests)
