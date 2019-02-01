(ns rum-sample.word-assoc.word-assoc
  (:require [play-cljs.core :as p]
            [rum-sample.word-assoc.word-utils :as wu]
            [rum-sample.word-assoc.game-utils :as gl]))

(def const
  {:board-width 750
   :board-height 750
   :num-cells-x 5
   :num-cells-y 5})

(defonce game (p/create-game
               (:board-width const)
               (:board-height const)))

(defonce state (atom {}))

(defn render-state [state]
  (gl/render-game-cells state))

(defn update-state [state]
  state)

(def main-screen
  (reify p/Screen
    ;; runs when the screen is first shown
    (on-show [this]
      ;; start the state map with...
      (reset! state (wu/init-game)))

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
