(ns rum-sample.word-assoc
  (:require [play-cljs.core :as p]))

(def const
  {:board-width 250
   :board-height 250
   :num-cells-x 5
   :num-cells-y 5})

(defonce game (p/create-game
               (:board-width const)
               (:board-height const)))

(defonce state (atom {}))

(defn render-state [state]
  [[:fill {:color "blue"}
    [:rect {:x 0
            :y 0
            :width (:board-width const)
            :height (:board-height const)}]]])

(defn update-state [state]
  state)

(def main-screen
  (reify p/Screen
    ;; runs when the screen is first shown
    (on-show [this]
      ;; start the state map with...
      (reset! state {}))

    ;; runs when the screen is hidden
    (on-hide [this])

    ;; runs every time a frame must be drawn (about 60 times per sec)
    (on-render [this]
      ;; we use `render` to display...
      (p/render game (render-state @state))
      (swap! state update-state))))

(defn start-game []
  (doto game
    (p/start)
    (p/set-screen main-screen)))
