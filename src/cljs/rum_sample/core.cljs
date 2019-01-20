(ns rum-sample.core
  (:require
  [clojure.core.async :as a] 
   [rum.core :as rum]
   [datascript.core :as d]
   [rum-sample.queue :as q]
   [rum-sample.db :as db]))

(def my-state (atom "abc"))

(defn get-of-age-users [conn]
  (d/q '[:find [(pull ?e [:name :age]) ...]
         :where
         [?e :age ?a]
         [(>= ?a 21)]]
       @conn))

(defn make-li [users]
  (map (fn [x] [:li (str (:name x) " - " (:age x))]) users))

(rum/defc blah < rum/reactive
  []
  [:p (rum/react my-state)])

(defn update-bobs-age [new-age]
  (a/go (>! q/global-write-queue
            {:msg-type :update-bob-age
             :new-age new-age})))

(let [bobs-age-changes-chan (a/chan)]
  (a/sub q/sub-to-me-to-listen
         :update-bob-age bobs-age-changes-chan)
  (a/go-loop []
    (let [msg (a/<! bobs-age-changes-chan)
          new-age (:new-age msg)]
      (.log js/console (str "Got Msg: " new-age))

      (d/transact!
       db/conn
       [{:db/id 1 :name "Bob"}
        {:db/id 1 :age new-age}])

      (reset! my-state (:msg msg)))
    (recur)))

(rum/defc update-users-age []
  [:input {:on-blur #(update-bobs-age
                     (-> % .-target .-value))}]) 

(rum/defc of-age-users < rum/reactive
  [conn]
  (let [db (rum/react conn)]
    [:div
     "Of Age Users:"
     [:ul
      (make-li (get-of-age-users conn))]
     (update-users-age)
     ]))

(rum/mount
 ;; (blah)
 (of-age-users db/conn)
 js/document.body)


