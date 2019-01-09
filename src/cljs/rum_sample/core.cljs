(ns rum-sample.core
  (:require [rum.core :as rum]))

(def text (atom "abc"))

(rum/defc label < rum/reactive
  [text]
  [:div {:class "label"} (rum/react text)])

(rum/mount (label text) js/document.body)




