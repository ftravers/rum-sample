(ns rum-sample.word-assoc.game-state
  (:require [rum-sample.word-assoc.word-utils :as wu]
            [clojure.set :as set]
            [tilakone.core :as tk]))

;;; actions

(defn valid-hint? [{[_ hint] ::tk/signal
                    process ::tk/process}]
  "Evaluates to truthy if the value given by the signal key in the state machine is an allowed hint; falsy otherwise."
  (let [{:keys [game-words]} process]
    (not (or (game-words hint)
             (empty? hint)))))

(defn set-p1-hint [{::tk/keys [signal]
                    :as fsm}]
  "Sets the hint for player one to the value given by the signal key in the state machine. Removes error message about the hint, if any."
  (let [[_ hint] signal]
    (-> fsm
        (assoc-in [::tk/process :player-hints 0] hint)
        (update ::tk/process (fn [p] (dissoc p :hint-error))))))

(defn invalid-hint [{::tk/keys [signal process]
                     :as fsm}]
  "Determines what is invalid about the value given by the signal key in the state machine and assocs a human-readable error message about it."
  (let [[_ hint] signal
        {:keys [game-words]} process]
    (assoc-in fsm
              [::tk/process :hint-error]
              (cond
                (game-words hint) "The hint must not be one of your words."
                (empty? hint) "The hint must not be empty."))))

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

;;; state machine

;; this contains the names for all possible states in our game, the transitions between each state, and the transformations on the state machine during each transition.
(def game-states
  ;; first we start the game, initializing the words
  [{::tk/name :before-start
    ::tk/transitions [{::tk/on :start
                       ::tk/actions [:add-game-words]
                       ::tk/to :p1}]}
   ;; second, player one can enter a hint and the number of words to which it applies
   ;; if the hint does not "give away" one of the words and is not empty, it's valid. the guesser's turn comes next.
   ;; if the hint is invalid, then player one must correct it.
   {::tk/name :p1
    ::tk/transitions [{::tk/on :hint
                       ::tk/guards [valid-hint?]
                       ::tk/actions [:set-p1-hint]
                       ::tk/to :p1-guesser}
                      {::tk/on ::tk/_
                       ::tk/actions [:invalid-hint]}]}
   ;; then the guesser makes their first guess
   {::tk/name :p1-guesser}])

;; this is the top-level configuration and state for our state machine.
(def codewords
  {::tk/states  game-states
   ::tk/action! (fn [{::tk/keys [action] :as fsm}]
                  (case action
                    :add-game-words (update fsm ::tk/process add-game-words)
                    :invalid-hint (invalid-hint fsm)
                    :set-p1-hint (set-p1-hint fsm)))
   ::tk/state   :before-start
   :game-words #{}
   ::tk/guard? (fn [{::tk/keys [guard] :as fsm}] (guard fsm))
   ::tk/match? (fn [{::tk/keys [signal on]}] (-> signal first (= on)))
   :player-hints []
   :player-words [[] []]
   :player-guessed-words [#{} #{}]
   :current-turn :p1})

(comment
  (-> codewords
      (tk/apply-signal [:start])
      (tk/apply-signal [:hint ""]))) ;; giving an empty hint shouldn't transition to guesser's turn
