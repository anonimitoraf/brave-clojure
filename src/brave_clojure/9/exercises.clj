(ns brave-clojure.9.exercises
  (:require [clojure.java.io :as io])
  (:require [brave-clojure.9.promises :refer :all]))

(def query-by-engine {:google "http://www.google.com/search?hl=en&q="
                      :bing "https://www.bing.com/search?q="
                      :ecosia "https://www.ecosia.org/search?q="})

;; Write a function that takes a string as an argument and searches for it on
;; Bing and Google using the slurp function. Your function should return the
;; HTML of the first page returned by the search.
(defn search
  [input-file-path engine]
  (->> input-file-path
       (io/resource)
       (slurp)
       (str (engine query-by-engine))
       (slurp)))
;; (println (search "9-input.txt" :ecosia))

;; Update your function so it takes a second argument consisting of the search
;; engines to use.
(defn search-multiple
  "Can search multiple engines and returns the first one that emits a result"
  [input-file-path engines]
  (let [result-promise (promise)]
    ;; For exercise's sake, this is done differently from `search-multiple-result-urls`
    ;; because each task here is done sequentially which is one (the worse) way to
    ;; go about it
    (doseq [engine engines]
      (future (deliver
               result-promise
               (search input-file-path engine))))
    (deref result-promise 20000 "Timed out")))
;; (println (search-multiple "9-input.txt" [:bing :ddg]))


(def link-re #"(?<=href=[\"']).*?(?=[\"'])")

;; Create a new function that takes a search term and search engines as arguments
;; and returns a vector of the URLs from the first page of search results from each
;; search engine.
(defn search-multiple-result-urls
  "Searches all provided engines then returns the URLs in the resulting pages"
  [input-file-path engines]
  (let [results-promise (promise-all (map
                                      (fn [engine] (promisify (search input-file-path engine)))
                                      engines))
        results (deref results-promise 20000 "Timed out")]
    (map (fn [r] (re-seq link-re r)) results)))
(println (search-multiple-result-urls "9-input.txt" [:bing :ecosia]))
