(ns rum-sample.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(r/render [:p "Hello World!"] (js/document.getElementById "app"))


