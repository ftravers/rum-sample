(ns rum-sample.view
  (:require [rum-sample.business :as b]
            [rum-sample.db :as db]
            [rum.core :refer [defc] :as rum]))

;; example of calling a business function based on a UI event
;(defc ui-update-users-age []
;  [:input
;   {:on-blur #(b/update-bobs-age
;               (-> % .-target .-value))}]) 

;; example of a reactive component
;(defc ui-of-age-users < rum/reactive
;  [conn]
;  (let [db (rum/react conn)]
;    [:div
;     "Of Age Users:"
;     [:ul
;      (make-li (b/get-of-age-users))]
;     (ui-update-users-age)]))

(defc hello-world-component
  []
  [:h1 "Word Association Game"])

(defn main-page []
  (rum/mount
   (hello-world-component db/conn)
   js/document.body))
