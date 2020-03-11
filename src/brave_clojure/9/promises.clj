(ns brave-clojure.9.promises)

(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

(defmacro promisify
  [& body]
  `(let [p# (promise)]
     (do (future (deliver p# (do ~@body))))
     p#))

(defn promise-all
  "`promises` are executed in parallel. The call to `promise-all`
  is realized when all the promises are realized"
  [promises]
  (future (doall (map deref promises))))

(defn sanity-tests
  []
  (let [p1 (promisify (wait 1000 1))
        p2 (promisify (wait 4000 2))
        p3 (promisify (wait 2000 3))]
    @(promise-all [p1 p2 p3]))
  )
