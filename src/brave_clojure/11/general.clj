(ns brave-clojure.11.general
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(def echo-chan (chan))

(defn hotdog-machine
  []
  (let [in (chan)
        out (chan)]
    (go (<! in)
        (>! out "hot dog"))
    [in out]))

(defn hotdog-machine-v2
  "Only accepts money"
  [hotdog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hotdog-count]
          (if (> hc 0)
            (let [input (<! in)]
              (if (= input "money")
                (do (>! out "hotdog-v2")
                    (recur (dec hc)))
                (do (>! out "non-hotdog-v2")
                    (recur hc))))
            ;; No more hotdogs left
            (do (close! in)
                (close! out)))))
    [in out]))

(defn upload
  [picture channel]
  (go (Thread/sleep (rand 500))
      (>! channel picture)))

(defn append-to-file
  "Write a string to the end of a file"
  [filename s]
  (spit filename s :append true))

(defn format-quote
  "Delineate the beginning and end of a quote because it's convenient"
  [quote]
  (str "=== BEGIN QUOTE ===\n" quote "=== END QUOTE ===\n\n"))

(defn random-quote
  "Retrieve a random quote and format it"
  []
  (format-quote (slurp "https://www.braveclojure.com/random-quote")))

(defn snag-quotes
  [filename num-quotes]
  (let [c (chan)]
    (go (while true (append-to-file filename (<! c))))
    (dotimes [n num-quotes] (go (>! c (random-quote))))))

(defn upper-caser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/upper-case (<! in)))))
    out))

(defn reverser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/reverse (<! in)))))
    out))

(defn printer
  [in]
  (go (while true (println (<! in)))))

;;
;; Tests
;; _________________________________________
(defn tests
  []
  ;; Basic channel example
  (go (println (<! echo-chan)))
  (>!! echo-chan "ketchup")

  ;; Using the hotdog machine
  (let [[in out] (hotdog-machine)]
    (>!! in "pocket lint")
    (println (<!! out)))

  ;; Using the hotdog machine v2
  (let [[in out] (hotdog-machine-v2 2)]
    (>!! in "pocket lint")
    (println (<!! out))

    (>!! in "money")
    (println (<!! out))

    (>!! in "money")
    (println (<!! out))

    (>!! in "money")
    (println (<!! out)))

  ;; Using channels as input-output pipelines
  (let [c1 (chan)
        c2 (chan)
        c3 (chan)]
    (go (>! c2 (clojure.string/upper-case (<! c1))))
    (go (>! c3 (clojure.string/reverse (<! c2))))
    (go (println (<! c3)))
    (>!! c1 "redrum"))

  ;; Using alts to return the first successful take
  (let [c1 (chan)
        c2 (chan)
        c3 (chan)]
    (upload "serious.jpg" c1)
    (upload "fun.jpg" c2)
    (upload "sassy.jpg" c3)
    ;; Timeout returns a channel that closes within ms
    (let [[picture channel] (alts!! [c1 c2 c3 (timeout 100)])]
      (if picture
        (println "Sending picture notification for" picture)
        (println "Timed out"))))

  ;; Using alts to either take or put into a channel
  (let [c1 (chan)
        c2 (chan)]
    (go (<! c2))
    (let [[value channel] (alts!! [c1 [c2 "put!"]])]
      (println value)
      (= channel c2)))

  ;; Using a queue to retrieve random quotes and append to a file
  (snag-quotes "quotes.txt" 3)

  (let [in-chan (chan)
        upper-caser-out (upper-caser in-chan)
        reverser-out (reverser upper-caser-out)]
    ;; Not in the let-statement since this
    ;; is purely for side-effects
    (printer reverser-out)
    (>!! in-chan "repaid"))
  )

;; (tests)
