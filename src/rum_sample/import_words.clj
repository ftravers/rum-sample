(ns rum-sample.import-words
  (:require [clojure.string :as str]))

(defn -main "Run this in the main directory (above src). Invoke as: clj -A:be -m rum-sample.import-words" [& _]
  (let [_ (println "started")
        text (slurp "data/1k-words.txt")
        _ (println text)
        words (str/split-lines text)
        strings-seq (map #(str \" % \" \space) words)
        strings (apply str strings-seq)
        output (str "(ns rum-sample.words)\n(def listed '(" strings "))")
        _ (println output)
        _ (spit "src/cljc/rum_sample/words.cljc" output)]))

#_( (-main)
    (println "----") 
   )
