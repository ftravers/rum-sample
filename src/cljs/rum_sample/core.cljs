(ns rum-sample.core
  (:require
   [rum-sample.view :as v]
   [rum-sample.events :as e]
   [rum-sample.word-assoc :as g])
  )

(e/load-event-watchers)
(v/main-page)
(g/start-game)
