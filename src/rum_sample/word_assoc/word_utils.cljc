(ns rum-sample.word-assoc.word-utils
  (:require [rum-sample.word-assoc.words :as words]
            [clojure.string :as str]
            [clojure.set :as set]))

(def game-state
  {:word-count 25
   :player-num-words [7 6] ;indexed by player ID: 0 or 1
   :state
   (atom
    {:game-words []
     :player-words [[] []] ;indexed by player ID: 0 or 1
     :player-guessed-words [[] []]
     :guesser-hint {}
     })})

(defn get-n-random-words [words count]
  ; Don't pick from (rand-nth (seq words)), because then you may have repetitions of the same word.
  (take count (shuffle words)))

(defn get-game-words []
  (-> words/listed 
      (get-n-random-words (-> game-state :word-count))
      (into #{})))

(defn remaining-words [])

(defn init-game []
  (let [state (:state game-state)
        game-words (get-game-words)
        p1-word-count (-> game-state :p1-num-words)
        p1-words (get-n-random-words game-words p1-word-count)
        remaining-game-words (set/difference game-words p1-words)
        p2-word-count (-> game-state :p2-num-words)
        p2-words (get-n-random-words remaining-game-words p2-word-count)]
    (reset!
     state
     (-> @state
         (assoc :game-words game-words)
         (assoc :p1-words p1-words)
         (assoc :p2-words p2-words)))))

(comment
  (init-game)

  (deref (:state game-state))  

  ) 
