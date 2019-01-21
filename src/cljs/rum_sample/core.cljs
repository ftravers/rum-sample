(ns rum-sample.core
  (:require
   [rum-sample.view :as v]
   [rum-sample.events :as e]
   [rum-sample.game :as g]))

(e/load-event-watchers)
(v/main-page)
(g/start-game)
