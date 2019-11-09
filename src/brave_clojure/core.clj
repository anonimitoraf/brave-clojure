(ns brave-clojure.core
  (:gen-class))

;; (defn -main
;;   "I don't do a whole lot ... yet."
;;   [& args]
;;   (println "Hello, World!"))

(def filename "suspects.csv")
(slurp filename)
