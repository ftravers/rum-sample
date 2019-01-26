(ns rum-sample.events
  (:require [rum-sample.db :as d]
            [rum-sample.queue :as q]))

(defn load-event-watchers []
  (q/on-msg-do
   :update-bob-age
   #(let [new-age (:new-age %)]
      (d/save-data [{:db/id 1 :name "Bob"}
                    {:db/id 1 :age new-age}]))))
