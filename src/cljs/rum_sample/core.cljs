(ns rum-sample.core
  (:require [rum.core :as rum]))

(rum/defc label [text]
  [:div {:class "label"} text])

(rum/mount (label "abc") js/document.body)




