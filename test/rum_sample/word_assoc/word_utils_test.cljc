(ns rum-sample.word-assoc.word-utils-test
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [is are deftest testing]]
      :clj  [clojure.test :as t :refer        [is are deftest testing]])
   [rum-sample.word-assoc.word-utils :as sut]))

(t/deftest get-n-random-words-test
  (let [all-words '("a" "b" "c" "d" "e" "f")
        requested-count 4
        random (sut/get-n-random-words all-words requested-count)
        ]
    (testing "Length"
      (is (= requested-count (count random)))
      )
    (testing "Subset"
      (is (every? (set all-words) random)))
    (testing "Is a set"
      (is (set? random)))))

(t/deftest next-turn-tests
  (let [game-state {:state (atom {:current-turn :p1})}]
    (testing "player 1 to guesser"
      (is (= (-> game-state
                 sut/next-turn!
                 :state
                 deref
                 :current-turn)
             :p1-guesser)))))

(t/run-tests)
