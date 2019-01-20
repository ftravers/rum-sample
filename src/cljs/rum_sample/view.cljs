(ns rum-sample.view
  (:require [rum-sample.business :as b]
            [rum-sample.db :as db]
            [rum.core :refer [defc] :as rum]))

(defn make-li [users]
  (map (fn [x] [:li {:key (:name x)}
                (str (:name x) " - " (:age x))])
       users))

(defc ui-update-users-age []
  [:input
   {:on-blur #(b/update-bobs-age
               (-> % .-target .-value))}]) 

(defc ui-of-age-users < rum/reactive
  [conn]
  (let [db (rum/react conn)]
    [:div
     "Of Age Users:"
     [:ul
      (make-li (b/get-of-age-users))]
     (update-users-age)]))

(defn main-page []
  (rum/mount
   (ui-of-age-users db/conn)
   js/document.body))
