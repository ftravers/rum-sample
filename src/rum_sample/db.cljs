(ns rum-sample.db
  (:require [datascript.core :as d]))

(def schema {:name {:db/unique :db.unique/identity}})

(def conn (d/create-conn schema))

(defn save-data [data]
  (d/transact! conn data))

;; some testdata
(save-data [{:db/id 1 :name "Bob" :age 30}
            {:db/id 3 :name "Fenton" :age 45}
            {:db/id 2 :name "Sally" :age 15}])


