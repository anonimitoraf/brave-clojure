(defn replace-left
  [my-string]
  (clojure.string/replace my-string #"^left-" "right-"))

(println (replace-left "left-something"))
(println (replace-left "not-left-something"))

;; For playing around with structured editing
(defn test
  [test-arg]
  (let [test-local test-arg]
    (println test-local)
    test-local))

 (test "BLAH")
