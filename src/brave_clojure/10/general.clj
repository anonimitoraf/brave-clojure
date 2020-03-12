(ns brave-clojure.10.general)

(def sock-varieties
  #{"darned" "argyle" "wool" "horsehair" "mulleted"
    "passive-aggressive" "striped" "polka-dotted"
    "athletic" "business" "power" "invisible" "gollumed"})

(defn make-sock-details
  [sock-variety count]
  {:variety sock-variety
   :count count})

(defn make-sock-gnome
  "Create an initial sock gnome state with no socks"
  [name]
  {:name name
   :socks #{}})

(def sock-gnome (ref (make-sock-gnome "Barumpharumph")))

(def dryer (ref {:name "LG 1337"
                 :socks (set (map #(make-sock-details % 2) sock-varieties))}))

(defn steal-sock
  "Gives the gnome a random sock from a sock pair in the dryer"
  [sock-gnome dryer]
  (dosync
   (when-let [sock (some #(if (= 2 (:count %)) %) (:socks @dryer))]
     (alter sock-gnome update :socks conj sock)
     (alter dryer update :socks disj sock)
     (alter dryer update :socks conj (update sock :count dec))
     ))
  )

(defn similar-socks
  [target-sock sock-set]
  (filter #(= (:variety %) (:variety target-sock)) sock-set))

(defn tests
  []
  (:socks @dryer)

  (steal-sock sock-gnome dryer)
  (:socks @sock-gnome)
  (:socks @dryer)

  (similar-socks (first (:socks @sock-gnome)) (:socks @dryer))
  )
;; (tests)
