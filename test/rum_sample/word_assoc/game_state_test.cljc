(ns rum-sample.word-assoc.game-state-test
  (:require [rum-sample.word-assoc.game-state :as sut]
            [tilakone.core :as tk]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(deftest valid-hint-tests
  (testing "invalid hints"
    (are [hint] (not (sut/valid-hint? {::tk/signal [:hint hint]
                                       ::tk/process {:game-words #{"abc" "def"}}}))
      "" ;; hint must not be empty
      "abc" ;; cannot name a word
      "two words") ;; only a single word is allowed
    #_(is (not (sut/valid-hint? {::tk/signal [:hint ""]
                               ::tk/process {:game-words #{"abc" "def"}}})))
    #_(is (not (sut/valid-hint? {::tk/signal [:hint "abc"]
                               ::tk/process {:game-words #{"abc" "def"}}}))))
  (is (sut/valid-hint? {::tk/signal [:hint "ghi"]
                        ::tk/process {:game-words #{"abc" "def"}}})))

(deftest set-p1-hint-tests
  (testing "setting a hint and removing any errors about it."
    (let [set-hint (sut/set-p1-hint {::tk/signal [:set-p1-hint "Hint"]
                                    ::tk/process {:player-hints []}})]
     (is (= (get-in set-hint [::tk/process :player-hints 0])
            "Hint"))
     (is (not (get-in set-hint [::tk/process :hint-error]))))))

(deftest invalid-hint-tests
  (testing "an invalid hint should cause an error message to be made available."
    (are [hint] (get-in (sut/invalid-hint {::tk/signal [:hint hint]
                                           ::tk/process {:game-words #{"abc"}}})
                        [::tk/process :hint-error])
      "abc"
      "")))

(defn to-state [state signals]
  (reduce tk/apply-signal state signals))

(deftest starting-tests
  (let [started (to-state sut/codewords [[:start]])]
    (testing "beginning the game"
     (is (= (::tk/state started)
            :p1)))))

(deftest from-p1-tests
  (let [p1-turn (tk/apply-signal sut/codewords [:start])
        valid-hint (tk/apply-signal p1-turn [:hint "hint"])]
   (testing "player one provides a valid hint"
     (is (= (::tk/state valid-hint)
            :p1-guesser)))
   (testing "player one provides an invalid hint"
     (is (= (::tk/state (-> p1-turn
                            (tk/apply-signal [:hint ""])))
            :p1)))
   )) ;; TODO make assertions about more of the keys and values from the game state

(deftest from-p1-guesser-tests
  (let [p1-guesser (to-state sut/codewords [[:start] [:hint "hint"]])]
    (testing "the guesser for player one provides a valid guess"
      (is (= (::tk/state (-> p1-guesser
                             (update-in [::tk/process :p1-words] #(conj % "foobar"))
                             (tk/apply-signal [:guess "foobar"])))
             :p2)))))
