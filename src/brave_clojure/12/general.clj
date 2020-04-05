(ns brave-clojure.12.general
  (:import [java.util Date Stack]
           [java.net Proxy URI]))

(defn tests
  []
  ;; Equivalent to "By Bluebeard's bananas!".toUpperCase()
  (println (.toUpperCase "By Bluebeard's bananas!"))
  (println (macroexpand-1 '(.toUpperCase "By Bluebeard's bananas!")))

  (println (java.lang.Math/abs -3))
  (println java.lang.Math/PI)

  ;; Most people use the dot version, (ClassName.)
  (let [a (new String "apple")
        b (String. "banana")]
    (println a b))

  ;; Playing around with Java stacks
  (let [stack (java.util.Stack.)]
    (doto stack
      (.push "Latest episode of Game of Thrones, ho!")
      (.push "Whoops, I meant 'Land, ho!'"))
    (println (str stack)))

  ;; Test if importing works
  (println (str (Date.)))

  ;; Commonly used Java stuff
  (println (str (System/getenv)))
  (println (str (System/getProperty "user.dir")))
  (println (str (System/getProperty "java.version")))

  (let [file (java.io.File. "/")]
    (println (.exists file))
    (println (.canWrite file))
    (println (.getPath file)))
  )

;; (tests)
