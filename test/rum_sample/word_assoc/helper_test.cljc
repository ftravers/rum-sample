(ns rum-sample.word-assoc.helper-test
  (:require #?(:clj [clojure.test :as t]
               :cljs [cljs.test :as t :include-macros true])
            [rum-sample.word-assoc.helper :as sut]))

(deftest get-cell-origin-tests
  (testing "get the origin for the given cell"
    (let [x-num 0
          y-num 0
          x-total 5
          y-total 5
          board-height 250
          board-width 250]
      (is (= [0 0]
             (sut/get-cell-origin x-num y-num x-total y-total board-height board-width))))
    (let [x-num 1
          y-num 1
          x-total 5
          y-total 5
          board-height 250
          board-width 250]
      (is (= [50 50]
               (sut/get-cell-origin x-num y-num x-total y-total board-height board-width))))))

