(ns rum-sample.word-assoc.helper-test
  (:require [rum-sample.word-assoc.helper :as sut]
            [cljs.test :as t :include-macros true]))

(enable-console-print!)

(deftest get-cell-origin-tests
  (testing "get the origin for the given cell"
    (let [cell-x-ratio 0/5
          cell-y-ratio 0/5
          board-height 250
          board-width 250]
      (is (= [0 0]
             (sut/get-cell-origin cell-x-ratio cell-y-ratio board-height board-width))))))

(cljs.test/run-tests)
