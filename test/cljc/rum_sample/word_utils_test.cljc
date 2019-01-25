(ns rum-sample.word-utils-test
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [is are deftest testing]]
      :clj  [clojure.test :as t :refer        [is are deftest testing]])
   [rum-sample.word-utils :as sut]))

(t/deftest get-n-random-words-test
  (let [all-words '("a" "b" "c" "d" "e" "f")
        requested-count 4
        random (sut/get-n-random-words all-words requested-count)
        ]
    (testing "Length"
      (is (= requested-count (count random)))
      )
    (testing "Subset"
      (is (every? (set all-words) random)))))

(t/run-tests)
