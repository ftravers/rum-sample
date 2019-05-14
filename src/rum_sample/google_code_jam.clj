(ns rum-sample.google-code-jam
  (:require
   [clojure.string :as c-str]
   [clojure.edn :as edn]
   [clojure.test :as tst :refer [is are]]
   ))

(defn invert-letters
  {:test
   (fn []
     (are [input expected]
         (= expected (invert-letters input))
       "SE" "ES"
       "SEESS" "ESSEE"))}
  [first-path]
  (c-str/join (map #(case %
                      "E" "S"
                      "S" "E")
                   (c-str/split first-path #""))))

(def test-data
  (c-str/split-lines
   (slurp "src/rum_sample/google_test_data.txt")))
 
(def num-cases (edn/read-string
                (first test-data)))

(for [x (range num-cases)]
  (let [path (nth test-data (+ 2 (* 2 x)))]
    path
    (spit "google-solution.txt"
          (str "Case #" x ": " (invert-letters path) "\n")
          :append true)))
