(ns rum-sample.game-utils-test
  (:require  [clojure.test :refer :all]
             [rum-sample.game-utils :as sut]))

(deftest add1_tests
  (testing "adding one"
    (is (= 2
           (sut/add1 1)))))
