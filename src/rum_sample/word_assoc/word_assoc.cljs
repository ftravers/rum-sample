(ns rum-sample.word-assoc.word-assoc
  (:require [play-cljs.core :as p]
            [rum-sample.word-assoc.word-utils :as wu]
            [rum-sample.word-assoc.game-utils :as gl]
            [rum.core :refer [defc] :as rum]))

(def const
  {:board-width 750
   :board-height 750
   :num-cells-x 5
   :num-cells-y 5})

(defonce game (p/create-game
               (:board-width const)
               (:board-height const)
               {:parent (js/document.getElementById "game")}))

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

(defc game-controls < rum/reactive [game-atom]
  (rum/react game-atom)
  [:div
   [:p (str "Current turn: "
            (if-let [current-state (:state @game-atom)]
              (:current-turn @current-state)
              :p1))]
   [:input {:type :text}]
   [:button {:on-click #(swap! game-atom wu/next-turn!)} "Next"]])

(defn start-game []
  (doto game
    (p/start)
    (p/set-screen main-screen)))

(defn show-game-controls []
  (rum/mount
   (game-controls state)
   (js/document.getElementById "controls")))
