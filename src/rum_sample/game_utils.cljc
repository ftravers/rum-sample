(ns rum-sample.game-utils)

(defn get-cell-origin [x-num y-num
                       {:keys [board-width
                               board-height
                               num-cells-x
                               num-cells-y]
                        :as game-state}]
  "cells count from bottom up (y-coord), first cell is index
  0. x-coord is from left to right."
  (let [x-seg-len (/ board-width num-cells-x)
        y-seg-len (/ board-height num-cells-y)
        x-origin (* x-num x-seg-len)
        y-origin (* y-num y-seg-len)]
    [x-origin y-origin]))

(defn convert-cell-num-to-cell-coord
  [cell-num
   {:keys [board-width
           board-height
           num-cells-x
           num-cells-y]
    :as game-state}]
  "given a number between 0 and then number of cells in the game
  return an [x y] coord"
  (let [x (rem cell-num num-cells-x)
        y (quot cell-num num-cells-x)]
    [x y]))

