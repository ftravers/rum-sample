(ns rum-sample.core
  (:require [reagent.core :as r]
            [garden.core :refer [css]]))

(defn my-comp []
  [:div 
   [:style (css [:.mine {:font-size "10px"}])]
   [:div {:class "mine"} "Hello World!!!"]])

(r/render (my-comp) 
          (js/document.getElementById "app"))


