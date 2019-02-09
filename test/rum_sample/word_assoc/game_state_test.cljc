(ns rum-sample.word-assoc.game-state-test
  (:require [rum-sample.word-assoc.game-state :as sut]
            [tilakone.core :as tk :refer [_]]
            #?(:cljs [cljs.test    :as t :refer-macros [is are deftest testing]]
               :clj  [clojure.test :as t :refer        [is are deftest testing]])))

(t/deftest state-transition-tests
  (t/testing "beginning the game"
    (t/is (= (::tk/state (tk/apply-signal sut/codewords :start))
           :p1)))) ;; TODO make assertions about more of the keys and values from the game state
