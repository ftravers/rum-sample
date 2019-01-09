(ns rum-sample.core
  (:require
   ;; [rum.core :as rum]
   [datascript.core :as d]))

(def conn (d/create-conn))

(def datoms [{:db/id -1 :name "Bob" :age 30}
             {:db/id -1 :name "Fenton" :age 45}
             {:db/id -2 :name "Sally" :age 15}])

(d/transact! conn datoms)

(def data (d/q '[:find (pull ?e [:name :age])
                 :where [?e :name "Fenton"]]
               @conn))

(js/console.log (str data))

;; (rum/defc users < rum/reactive
;;   [text]
;;   [:div {:class "label"} text])

;; (rum/mount (users "abc") js/document.body)




