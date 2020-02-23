(ns brave-clojure.8.exercises)

(def user-details-validations
  {:name
   ["Please enter a name" not-empty]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    #(or (empty? %) (re-seq #"@" %))]})

(defn error-messages-for
  "Return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
                     (partition 2 message-validator-pairs))))

;; (error-messages-for "" ["Please enter a name" not-empty])
;; => ("Please enter a name")

(defn validate
  "Returns a map with a vector of errors for each key"
  [to-validate validations]
  (let [key-error-tuples (map (fn [[k v]] [k (error-messages-for v (k validations))])
                              to-validate)]
    (into {} key-error-tuples))
  )

;; (validate {:name "Raf" :email "raf@raf.com"} user-details-validations)
;; (validate {:name "" :email "raf@raf.com"} user-details-validations)
;; (validate {:name "Raf" :email "raf"} user-details-validations)
;; (validate {:name "" :email "raf"} user-details-validations)

(defn if-valid-fn
  [record validations success-code failure-code]
  (let [errors (validate record validations)]
    (if (empty? errors)
      success-code
      failure-code)))

(defn if-valid-fn-2
  "Just like if-valid but asks for success
  and failure fns"
  [record validations success-fn failure-fn]
  (let [errors (validate record validations)]
    (if (empty? errors)
      (success-fn)
      (failure-fn errors))))

(defmacro if-valid-macro
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))

(defmacro when-valid-macro
  [to-validate validations errors-name & then]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (not-empty ~errors-name)
       (do ~@then))))

(defmacro custom-or
  ([] nil)
  ([expr] expr)
  ([expr & other-exprs]
   `(let [expr# ~expr]
      (if expr# expr# (custom-or ~@other-exprs)))))

;; Test stuff out
(defn run []
  (let [user-details-sample {:name "" :email "raf"}]

    ;; This would not work because both :success and :failure will be evaluated
    (println "Using is-valid-fn")
    (if-valid-fn user-details-sample user-details-validations
                 (println :success)
                 (println :failure))

    ;; This works but perhaps we can do better using a macro
    (println "Using is-valid-fn-2")
    (if-valid-fn-2 user-details-sample user-details-validations
                   #(println :success)
                   (fn [errors] (println :failure errors)))

    (println "Using is-valid-macro")
    (if-valid-macro user-details-sample user-details-validations errors-identifier
                    (println :success)
                    (println :failure errors-identifier))

    (println "Using when-valid-macro")
    (when-valid-macro user-details-sample user-details-validations errors-identifier
                      (println "It's a success!")
                      (println "Really!"))

    (println "Using custom-or")
    (println (custom-or []))
    (println (custom-or false))
    (println (custom-or false 1 2))
    ))
