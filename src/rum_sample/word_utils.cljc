(ns rum-sample.word-utils
  (:require [rum-sample.words :as words]
            [clojure.string :as str]
            [clojure.set :as set]))

(def game-state
  {:word-count 25
   :p1-num-words 7
   :p2-num-words 6
   :state
   (atom
    {:game-words []
     :p1-words []
     :p2-words []
     :p1-guessed-words []
     :p2-guessed-words []
     :guesser-hint {}
     })})

(defn get-n-random-words [words count]
  (reduce
   (fn [acc itm]
     (conj acc (rand-nth (seq words))))
   #{} 
   (range count)))

(defn get-game-words []
  (-> words/listed 
      (get-n-random-words (-> game-state :word-count))
      (into #{})))

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
