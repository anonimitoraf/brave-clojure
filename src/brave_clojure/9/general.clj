(ns brave-clojure.9.general)

(defn run
  []
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
  (let [x (@(future (Thread/sleep 1000) 0) 2000 5)
        y (realized? (future (Thread/sleep 1000)))
        z (realized? (future (Thread/sleep 0)))

        ]
    (println "x:" x)
    (println "y:" y)
    (println "z:" z)

    )
  )

