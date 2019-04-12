(ns rum-sample.word-assoc.word-utils
  (:require [rum-sample.word-assoc.words :as words]
            [clojure.string :as str]
            [clojure.set :as set]))

(def game-state
  {:board-width 750
   :board-height 250
   :num-cells-x 5
   :num-cells-y 5
   :word-count 25
   :player-colors ["red" "blue" "green"]
   :player-num-words [7 6] ;indexed by player ID: 0 or 1
   :state
   (atom
    {:game-words []
     :player-words [[] []] ;indexed by player ID: 0 or 1
     :player-guessed-words [#{} #{}]
     :guesser-hint {}
     :current-turn :p1
     })})

(defn get-n-random-words [words count]
  ; Don't pick from (rand-nth (seq words)), because then you may have repetitions of the same word.
  (->> words
      shuffle
      (take count)
      set))

(defn get-game-words []
  (-> words/listed
      (get-n-random-words (-> game-state :word-count))
      (into #{})))

(defn remaining-words [])

(defn init-game []
  (let [state (:state game-state)
        game-words (get-game-words)
        p1-word-count (-> game-state :player-num-words first)
        p1-words (get-n-random-words game-words p1-word-count)
        remaining-game-words (set/difference game-words p1-words)
        p2-word-count (-> game-state :player-num-words second)
        p2-words (get-n-random-words remaining-game-words p2-word-count)]
    (reset!
     state
     (-> @state
         (assoc :game-words game-words)
         (assoc :p1-words p1-words)
         (assoc :p2-words p2-words))))
  game-state)

(comment
  (init-game)

  (deref (:state game-state))

  )

(defn next-turn! [game-state]
  (let [state-atom (:state game-state)
        state @state-atom
        current-turn (:current-turn state)
        next (case current-turn
               :p1 :p1-guesser
               :p1-guesser :p2
               :p2 :p2-guesser
               :p2-guesser :p1)
        new-state (assoc state :current-turn next)]
    (reset! state-atom new-state)
    game-state))
