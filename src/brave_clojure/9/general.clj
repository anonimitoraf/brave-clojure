(ns brave-clojure.9.general)

;; ---
(future (Thread/sleep 4000)
        (println "I'll print after 4 seconds"))
(println "I'll print immediately")

;; ---
(let [result (future (Thread/sleep 1000)
                     (println "this prints once")
                     (+ 1 1))]
  (println "deref: " (deref result))
  (println "@: " @result))

;; ---
(let [jackson-5-delay (delay (let [message "Jackson-5: Just call my name and I'll be there"]
                               (println "First deref:" message)
                               message))
      jackson-6-delay (delay (let [message "Jackson-6: Just call my name and I'll be there"]
                               (println "First deref:" message)
                               message))]
  (println (force jackson-6-delay))
  (println @jackson-6-delay))

;; Simulate callbacks via futures blocked
(let [ferengi-wisdom-promise (promise)]
  (future (println "Here's some Ferengi wisdom:" @ferengi-wisdom-promise))
  (Thread/sleep 100)
  (deliver ferengi-wisdom-promise "Whisper your way to success."))

;; ---
(let [
      w (deref (future (Thread/sleep 3000) "just in time")
               2000
               "timed out")
      x (deref (future (Thread/sleep 1000) "just in time")
               2000
               "timed out")
      y (realized? (future (Thread/sleep 1000)))
      z (realized? (future (Thread/sleep 0)))

      ]
  (println)
  (println "w:" w)
  (println "x:" x)
  (println "y:" y)
  (println "z:" z)
  )
