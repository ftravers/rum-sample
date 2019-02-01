(ns rum-sample.word-assoc.words-test
  (:require [rum-sample.words :as sut]
            #?(:clj [clojure.test :as t]
               :cljs [cljs.test :as t :include-macros true])))

(t/deftest words-are-unique-test
  (t/testing "Words are unique"
    (t/is (= (count sut/listed)
           (count (set sut/listed))))))
