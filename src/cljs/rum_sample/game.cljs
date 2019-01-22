(ns rum-sample.game
  (:require [play-cljs.core :as p]))

(defn render-state [state]
  (let [{:keys [text world]} state
        [x y] (:position text)
        {:keys [width height]} world]
    [[:fill {:color "lightblue"}
      [:rect {:x 0 :y 0 :width width :height height}]]
     [:fill {:color "black"}
      [:text {:value "Text bounces back and forth! 8D"
              :x x
              :y y
              :dir :right
              :size 16
              :font "Georgia"
              :style :italic}]]]))

(defn update-state [state]
  (let [{:keys [world]} state
        {:keys [width height]} world]
    (update state :text (fn [{:keys [position velocity] :as text}]
                          (let [[x-vel y-vel] velocity
                                [x-pos y-pos] position
                                new-x-pos (+ x-pos x-vel)
                                constrained-x-pos (-> new-x-pos
                                                      (js/Math.max 0)
                                                      (js/Math.min width))
                                new-y-pos (+ y-pos y-vel)
                                constrained-y-pos (-> new-y-pos
                                                      (js/Math.max 0)
                                                      (js/Math.min height))
                                new-x-vel (if (or (< width new-x-pos)
                                                  (> 0 new-x-pos))
                                            (- x-vel)
                                            x-vel)
                                new-y-vel (if (or (< height new-y-pos)
                                                  (> 0 new-y-pos))
                                            (- y-vel)
                                            y-vel)]
                            (merge text
                                   {:velocity [new-x-vel new-y-vel]
                                    :position [constrained-x-pos constrained-y-pos]}))))))

(defonce game (p/create-game 500 500))
(defonce state (atom {}))

;; define a screen, where all the action takes place
(def main-screen
  (reify p/Screen

    ;; runs when the screen is first shown
    (on-show [this]
      ;; start the state map with the x and y position of the text we want to display
      (reset! state {
                     ;; :text-x 20 :text-y 30 :dir :right
                     :text {:velocity [1 1]
                            :position [20 30]}
                     :world {:width 200
                             :height 200}}))

    ;; runs when the screen is hidden
    (on-hide [this])

    ;; runs every time a frame must be drawn (about 60 times per sec)
    (on-render [this]
      ;; we use `render` to display a light blue background and black text
      ;; as you can see, everything is specified as a hiccup-style data structure
      (p/render game (render-state @state))
      (swap! state update-state))))

(defn start-game []
  (doto game
    (p/start)
    (p/set-screen main-screen)))

;; try this in the REPL! :D
(comment
  (deref state))
