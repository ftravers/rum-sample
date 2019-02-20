(ns rum-sample.word-assoc.game-state-test
  (:require [rum-sample.word-assoc.game-state :as sut]
            [tilakone.core :as tk]
            #?(:cljs [cljs.test :refer-macros [is are deftest testing]]
               :clj  [clojure.test :refer [is are deftest testing]])))

(deftest valid-hint-tests
  (testing "invalid hints"
    (is (not (sut/valid-hint? {::tk/signal [:hint ""]
                               ::tk/process {:game-words #{"abc" "def"}}})))
    (is (not (sut/valid-hint? {::tk/signal [:hint "abc"]
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

(deftest state-transition-tests
  (testing "beginning the game"
    (is (= (::tk/state (tk/apply-signal sut/codewords [:start]))
           :p1)))
  (testing "player one provides a valid hint"
    (is (= (::tk/state (-> sut/codewords
                           (tk/apply-signal [:start])
                           (tk/apply-signal [:hint "hint"])))
           :p1-guesser)))
  (testing "player one provides an invalid hint"
    (is (= (::tk/state (-> sut/codewords
                           (tk/apply-signal [:start])
                           (tk/apply-signal [:hint ""])))
           :p1)))) ;; TODO make assertions about more of the keys and values from the game state
