(ns rum-sample.word-assoc.helper-test
  (:require [rum-sample.word-assoc.helper :as sut]
            [cljs.test :as t :include-macros true]))

(enable-console-print!)

(t/deftest get-cell-origin-tests
  (t/testing "get the origin for the given cell"
    (let [x-num 0
      y-num 0
      x-total 5
      y-total 5
      board-height 250
      board-width 250]
  (t/is (= [0 0]
           (sut/get-cell-origin x-num y-num x-total y-total board-height board-width))))
    (let [x-num 1
          y-num 1
          x-total 5
          y-total 5
          board-height 250
          board-width 250]
      (t/is (= [50 50]
               (sut/get-cell-origin x-num y-num x-total y-total board-height board-width))))
    ))

(cljs.test/run-tests)
