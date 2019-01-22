(ns rum-sample.game
  (:require [play-cljs.core :as p]))

(defonce game (p/create-game 500 500))
(defonce state (atom {}))

;; define a screen, where all the action takes place
(def main-screen
  (reify p/Screen
  
    ;; runs when the screen is first shown
    (on-show [this]
      ;; start the state map with the x and y position of the text we want to display
      (reset! state {:text-x 20 :text-y 30 :dir :right}))

    ;; runs when the screen is hidden
    (on-hide [this])

    ;; runs every time a frame must be drawn (about 60 times per sec)
    (on-render [this]
      ;; we use `render` to display a light blue background and black text
      ;; as you can see, everything is specified as a hiccup-style data structure
      (p/render game
                ;; (let [new-state])
        [[:fill {:color "lightblue"}
          [:rect {:x 0 :y 0 :width 200 :height 200}]]
         [:fill {:color "black"}
          [:text {:value "Hello, world!"
                  :x (:text-x @state)
                  :y (:text-y @state)
                  :dir (:dir @state)
                  :size 16
                  :font "Georgia"
                  :style :italic}]]])
      ;; increment the x position of the text so it scrolls to the right
      (let [text-x (:text-x @state)
            dir (:dir @state)]
        (if (and (= :right (:dir @state))
                 (>= text-x 200))
          (swap! state update :dir :left))
        ;; (if (and ()))
        (case dir
          :left (swap! state update :text-x dec)
          :right (swap! state update :text-x inc)
          (swap! state update :text-x inc))))))

(defn start-game []
  (doto game
    (p/start)
    (p/set-screen main-screen)))

(comment
  (deref state))
