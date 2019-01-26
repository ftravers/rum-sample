(ns rum-sample.word-assoc.helper)

(defn get-cell-origin
  [x-num y-num x-total y-total board-height board-width]
  [(* (/ (- x-num 1) x-total) (/ board-width x-total))
   (* (/ (- y-num 1) y-total) (/ board-height y-total))])
