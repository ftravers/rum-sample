(ns rum-sample.business
  (:require [datascript.core :as d]
            [rum-sample.db :as db]
            [rum-sample.queue :as q]))

(defn get-of-age-users
  "get a list of users over 21"
  []
  (d/q '[:find [(pull ?e [:name :age]) ...]
         :where
         [?e :age ?a]
         [(>= ?a 21)]]
       @db/conn))

(defn update-bobs-age [new-age]
  (q/pub-msg
   {:msg-type :update-bob-age
    :new-age new-age}))


