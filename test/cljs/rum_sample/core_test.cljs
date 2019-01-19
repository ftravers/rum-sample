(ns rum-sample.core-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [rum-sample.core :as sut]))

(enable-console-print!)
 
(deftest user-of-age
  (testing "assert only 2 users of age"
    (let [users [{:name "Bob", :age 30}
                 {:name "Fenton", :age 45}]
          list (sut/make-li users)]
      (is (= '([:li "Bob - 30"] [:li "Fenton - 45"])
             list)))))

(cljs.test/run-tests)
