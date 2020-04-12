(ns brave-clojure.13.exercises
  (:require [brave-clojure.13.general :refer [full-moon-behavior
                                              WereCreature]]))

;; Extend the full-moon-behavior multimethod to add behavior for your own kind of were-creature.
(defmethod full-moon-behavior :raf
  [were-creature]
  (str (:name were-creature) " will code all night"
       full-moon-behavior-2))

;; Create a WereSimmons record type, and then extend the WereCreature protocol.
(defrecord WereRaf [name title]
  WereCreature
  (full-moon-behavior-2 [record]
    (str (:name record) " the " (:title record) " will code all night again!")))

;; Create your own protocol, and then extend it using extend-type and extend-protocol.
(defprotocol Concatenatable
  (custom-concat [x y] "Concatenates"))

(extend-type java.lang.String
  Concatenatable
  (custom-concat [x y] (str x y)))

(extend-type java.lang.Number
  Concatenatable
  (custom-concat [x y] (+ x y)))

(extend-protocol Concatenatable
  java.lang.Boolean
  (custom-concat [x y] (and x y)))

(defn tests
  []
  (println (full-moon-behavior {:were-type :raf
                                :name "Raf"}))

  (println (full-moon-behavior-2 (->WereRaf "Raf" "programmer")))

  (println (custom-concat "a" "b"))
  (println (custom-concat 1 2))
  (println (custom-concat true false))
  (println (custom-concat true true)))

;; TODO Create a role-playing game that implements behavior using multiple dispatch.

;; (tests)
