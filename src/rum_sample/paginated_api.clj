(ns rum-sample.paginated-api
  (:require
   [clojure.core.async :refer [<!! timeout]]
   [clojure.test :as tst :refer [is are]]))

(defn mock-api [page-size]
  (for [x (range page-size)]
    (println :a)
    (* x x)))

(defn call-paginated
  ;; {:test
  ;;  (fn []
  ;;    (is (= 3
  ;;           (call-paginated mock-api 100 250 "http://yahoo.com/get-page=" "&results_per_page=100"))))}
  [f max-page-size total-records query-pre query-post]
  (let [numcalls (int (Math/ceil (/ total-records max-page-size)))]
    (flatten (for [page (range 1 (+ 1 numcalls))]
       (f 5)))))

(defn fn-call-period
  {:test
   (fn []
     (is (=
         60000 
         (fn-call-period 60 (* 60 60 1000)))))}
  [num-calls-per-period period-ms]
  (int (Math/ceil (/ period-ms num-calls-per-period)))
  )
