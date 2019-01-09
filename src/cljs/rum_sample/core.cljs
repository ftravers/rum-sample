(ns rum-sample.core
  (:require [reagent.core :as r]
            [garden.core :refer [css]]))

(def css-data
  (css
   [[:.grid
     {:display :grid
      :grid-template-columns "1fr 1fr"
      :grid-gap "20px"}]
    
    [:.grid :div
     {:border "1px solid rgb(0,95,100)"}]]))

(defn my-comp []
  [:div {:class "grid"}
   [:style css-data]
   [:div "1"]
   [:div "2"]
   [:div "3"]
   [:div "4"]])

(r/render (my-comp) 
          (js/document.getElementById "app"))


