(ns rum-sample.word-assoc.game-utils-test
  (:require  [clojure.test :refer :all]
             [rum-sample.word-assoc.game-utils :as sut]))

(deftest render-state-tests
  (testing "create the data structure for the game"
    (let [cell-numbers (range 2)
          game-state {:board-width 100
                      :board-height 50
                      :num-cells-x 2
                      :num-cells-y 1
                      :visuals [:blue :red]}]
      (is (= [[:fill "blue"
               [:rect {:x 0
                       :y 0
                       :width 50
                       :height 50}]]
              [:fill "red"
               [:rect {:x 50
                       :y 0
                       :width 50
                       :height 50}]]]
             (sut/render-game-cells game-state))))))

(deftest get-cell-origin-tests
  (testing "given a cell, get it's bottom left coordinate"
    (let [game-state
          {:board-width 250
           :board-height 250
           :num-cells-x 5
           :num-cells-y 5}]
      (is (= [0 0]
             (let [cell-num-x 0
                   cell-num-y 0]
               (sut/get-cell-origin
                cell-num-x
                cell-num-y
                game-state))))
      (is (= [50 50]
             (let [cell-num-x 1
                   cell-num-y 1]
               (sut/get-cell-origin
                cell-num-x
                cell-num-y
                game-state))))
      (is (= [200 0]
             (let [cell-num-x 4
                   cell-num-y 0]
               (sut/get-cell-origin
                cell-num-x
                cell-num-y
                game-state)))))))

(deftest convert-cell-num-to-cell-coord-tests
  (testing "given a number between 0 and the # of cells on the board
return the associated [x y] coord.
y
4 | 20 21 22 23 24
3 | 15 16 17 18 19
2 | 10 11 12 13 14
1 | 5  6  7  8  9
0 | 0  1  2  3  4
--+---------------
  | 0  1  2  3  4  <-- x
"
    (let [game-state {:board-width 250
                      :board-height 250
                      :num-cells-x 5
                      :num-cells-y 5}]
      (is (= [3 1] (sut/convert-cell-num-to-cell-coord 8 game-state)))
      (is (= [0 0] (sut/convert-cell-num-to-cell-coord 0 game-state)))
      (is (= [0 1] (sut/convert-cell-num-to-cell-coord 5 game-state)))
      (is (= [0 2] (sut/convert-cell-num-to-cell-coord 10 game-state)))
      (is (= [2 2] (sut/convert-cell-num-to-cell-coord 12 game-state)))
      (is (= [4 4] (sut/convert-cell-num-to-cell-coord 24 game-state))))))


