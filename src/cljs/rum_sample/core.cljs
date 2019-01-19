(ns rum-sample.core
  (:require
   [clojure.core.async :as a]
   [rum.core :as rum]
   [datascript.core :as d]))

(def global-write-queue (a/chan))

(def my-state (atom "abc"))

(a/go (a/>! global-write-queue "def"))

(def sub-to-me-to-listen (a/pub global-write-queue :msg-type))

(def happy-msgs (a/chan))

(a/sub sub-to-me-to-listen :happy happy-msgs)

(comment
  (a/go (a/>! global-write-queue {:msg-type :happy :msg "hello"}))
  (a/go (a/>! global-write-queue {:msg-type :sad :msg "bob"}))
  )

(a/go-loop []
  (let [msg (a/<! happy-msgs)]
    (.log js/console (str "Got Msg: " msg))
    (reset! my-state (:msg msg)))
  (recur))

(def conn (d/create-conn))

(def datoms [{:db/id 1 :name "Bob" :age 30}
             {:db/id 3 :name "Fenton" :age 45}
             {:db/id 2 :name "Sally" :age 15}]) 

(def schema [{:name {:db/unique :db.unique/identity}}])

(d/transact! conn (concat schema datoms))

(d/transact! conn [{:name "Fenton"}])

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
  (a/go (>! global-write-queue
            {:msg-type :update-bob-age
             :new-age new-age})))

(let [bobs-age-changes-chan (a/chan)]
  (a/sub sub-to-me-to-listen
         :update-bob-age bobs-age-changes-chan)
  (a/go-loop []
    (let [msg (a/<! bobs-age-changes-chan)
          new-age (:new-age msg)]
      (.log js/console (str "Got Msg: " new-age))

      (d/transact!
       conn
       [{:db/id 1 :name "Bob"}
        {:db/id 1 :age new-age}])

      (reset! my-state (:msg msg)))
    (recur)))

(rum/defc update-users-age []
  [:input {:on-blur (fn [x] (update-bobs-age
                             (-> x .-target .-value)))}]) 

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
 (of-age-users conn)
 js/document.body)

(comment
  ;; evaluate the following and see the webpage automatically update
  ;; look at url below to figure out lookup refs...
  ;; https://github.com/kristianmandrup/datascript-tutorial/blob/master/create_schema.md
  (d/transact!
   conn
   [{:db/id 1 :name "Boob"}
    {:db/id 1 :age 32}])

  ;; try lookup-ref
  ;; :message "Lookup ref attribute should be marked
  ;; as :db/unique: [:db/id 1]", :data
  ;; {:error :lookup-ref/unique, :entity-id [:db/id 1]}}
  (let [conn (d/create-conn)]
    (d/transact! conn [{:name "Bob"}])
    (d/touch (d/entity (d/db conn) [:name "Bob"])))

  (let [conn (d/create-conn {:name {:db/unique :db.unique/identity}})]
    (d/transact! conn [{:name "Bob"}])
    (d/entity (d/db conn) [:name "Bob"]))

  (def conn (d/create-conn {:my/name {:db/unique :db.unique/identity}}))
  (def conn (d/create-conn))
  ;; (d/transact! conn {:my/name {:db/unique :db.unique/identity}})
  (d/transact! conn [{:my/name "Boob" :my/age 2}])
  (d/transact! conn [{:my/name "Bob" :my/age 3}])

  ;; 
  (d/touch (d/entity (d/db conn) 1))
  (d/touch (d/entity (d/db conn) [:my/name "Bob"]))
  (:datoms (d/db conn))
  (count (d/q '[:find ?e :where [?e :my/name]]
              (d/db conn)

              ))

;; => #datascript/DB {:schema #:my{:name
;; #:db{:unique :db.unique/identity}}, :datoms [[1 :my/age 3
;; 536870914] [1 :my/name "Bob" 536870913]]}

  )
