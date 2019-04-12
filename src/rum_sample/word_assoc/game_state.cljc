(ns rum-sample.word-assoc.game-state
  (:require [rum-sample.word-assoc.word-utils :as wu]
            [clojure.inspector :as ci]
            [clojure.set :as set]
            [tilakone.core :as tk]))

;;; actions

(defn invalid-hint [{::tk/keys [signal process]
                     :as fsm}]
  "Determines what is invalid about the value given by the signal key in the state machine and assocs a human-readable error message about it."
  (let [[_ hint] signal
        {:keys [game-words]} process]
    (assoc-in fsm
              [::tk/process :hint-error]
              (cond
                (re-find #" " hint) "The hint must be a single word."
                (game-words hint) "The hint must not be one of your words."
                (empty? hint) "The hint must not be empty."
                :else nil))))

(comment
  (-> codewords
      (tk/apply-signal [:start])
      (tk/apply-signal [:hint "two words"])
      ((juxt :hint-error)))
  #_["The hint must be a single word."]
  )

(defn valid-hint? [fsm]
  "Evaluates to truthy if the value given by the signal key in the state machine is an allowed hint; falsy otherwise."
  (not (get-in (invalid-hint fsm) [::tk/process :hint-error])))

(defn set-p1-hint [{::tk/keys [signal]
                    :as fsm}]
  "Sets the hint for player one to the value given by the signal key in the state machine. Removes error message about the hint, if any."
  (let [[_ hint] signal]
    (-> fsm
        (assoc-in [::tk/process :player-hints 0] hint)
        (update ::tk/process (fn [p] (dissoc p :hint-error))))))

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

(defn p1-guess [{::tk/keys [signal process]
                 :as fsm}]
  (println (str "fsm: " fsm))
  (let [{:keys [p1-words]} process
        [_ guess] signal]
    (if (p1-words guess)
      (-> fsm
          (update-in [::tk/process :player-guessed-words 0] #(conj % guess))
          (update-in [::tk/process :current-turn] :p2))
      fsm)))

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
   {::tk/name :p1-guesser
    ::tk/transitions [{::tk/on :guess
                       ::tk/action [:p1-guess]
                       ::tk/to :p2}]}
   {::tk/name :p2}])

;; this is the top-level configuration and state for our state machine.
(def codewords
  {::tk/states  game-states
   ::tk/action! (fn [{::tk/keys [action] :as fsm}]
                  (println {:action action})
                  (case action
                    :add-game-words (update fsm ::tk/process add-game-words)
                    :invalid-hint (invalid-hint fsm)
                    :set-p1-hint (set-p1-hint fsm)
                    :p1-guess (p1-guess fsm)))
   ::tk/state   :before-start
   :game-words #{}
   ::tk/guard? (fn [{::tk/keys [guard] :as fsm}] (guard fsm))
   ::tk/match? (fn [{::tk/keys [signal on]}] (do #_(println {:signal signal
                                                           :on on})
                                                 (-> signal first (= on))))
   :player-hints []
   :player-words [[] []]
   :player-guessed-words [#{} #{}]
   :current-turn :p1})

(comment
  (-> codewords
      ::tk/state) 

  (-> codewords
      (tk/apply-signal [:start])
      (select-keys [::tk/state :current-turn :game-words]))
{:tilakone.core/state :p1,
 :current-turn :p1,
 :game-words
 #{"mortgage" "february" "parts" "smith" "publications" "word" "cart"
   "files" "materials" "connection" "cause" "casino" "knowledge"
   "known" "until" "population" "publisher" "basis" "voice" "kingdom"
   "html" "evidence" "purchase" "conditions" "idea"}}
  (-> codewords
      (tk/apply-signal [:start])
      (tk/apply-signal [:hint ""])
      ((juxt :hint-error ::tk/state)))
  ;; ["The hint must not be empty." :p1]

  (-> codewords
      (tk/apply-signal [:start])
      (tk/apply-signal [:hint "hint"])
      ((juxt :hint-error ::tk/state :player-hints)))
  ;; [nil :p1-guesser ["hint"]]

  (let [hinted (reduce tk/apply-signal codewords [[:start] [:hint "something"]])
        updated (update-in hinted [::tk/process :p1-words] #(conj % "foobar"))]
    (-> (reduce tk/apply-signal updated [[:guess "foobar"]])
        ((juxt :player-guessed-words :current-turn)))))



