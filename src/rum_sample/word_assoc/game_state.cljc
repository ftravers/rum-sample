(ns rum-sample.word-assoc.game-state
  (:require [rum-sample.word-assoc.word-utils :as wu]
            [clojure.set :as set]
            [tilakone.core :as tk :refer [_]]))

(def game-states
  ;; first we start the game, initializing the words
  [{::tk/name :before-start
    ::tk/transitions [{::tk/on :start
                       ::tk/actions [:add-game-words]
                       ::tk/to :p1}]}
   ;; then player one can enter a hint and the number of words to which it applies
   {::tk/name :p1
    ::tk/transitions [{::tk/on :hint ::tk/to :p1-guesser}]}
   ;; then the guesser makes their first guess
   {::tk/name :p1-guesser
    ::tk/transitions [{::tk/on :guess
                       ::tk/to :check-guess
                       ::tk/actions [:check-guess]}]}
   ;; we check the guess; if it's right the guesser goes again and we remove the guessed word
   {::tk/name :check-guess
    ::tk/transitions [{::tk/on :correct-guess
                       ::tk/to :p1-guesser
                       ::tk/actions [:remove-guessed-word]}
                      {::tk/on :incorrect-guess
                       ::tk/to :p2
                       ::tk/actions [:start-p2]}]}])

(defn add-game-words [process]
  "Populates part of the fsm with the initial game state."
  (let [game-words (wu/get-game-words)
        p1-word-count (-> wu/game-state :player-num-words first)
        p1-words (wu/get-n-random-words game-words p1-word-count)
        remaining-game-words (set/difference game-words p1-words)
        p2-word-count (-> wu/game-state :player-num-words second)
        p2-words (wu/get-n-random-words remaining-game-words p2-word-count)]
    (-> process
          (assoc :game-words game-words)
          (assoc :p1-words p1-words)
          (assoc :p2-words p2-words))))

(def codewords
  {::tk/states  game-states
   ::tk/action! (fn [{::tk/keys [action] :as fsm}]
                  (case action
                    :add-game-words (update fsm ::tk/process add-game-words)))
   ::tk/state   :before-start
   :game-words []
   :player-words [[] []]
   :player-guessed-words [#{} #{}]
   :current-turn :p1})

(comment
  (tk/apply-signal codewords :start))
