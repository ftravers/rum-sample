(ns rum-sample.core
  (:require
   [rum-sample.view :as v]
   [rum-sample.events :as e]))

(e/load-event-watchers)
(v/main-page)
