(ns rum-sample.db
  (:require [datascript.core :as d])
  )

(def schema {:name {:db/unique :db.unique/identity}})

(def conn (d/create-conn schema))

(let [datoms [{:db/id 1 :name "Bob" :age 30}
              {:db/id 3 :name "Fenton" :age 45}
              {:db/id 2 :name "Sally" :age 15}]]
  (d/transact! conn datoms))
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
