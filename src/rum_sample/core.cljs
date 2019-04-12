(ns rum-sample.core
  (:require
   [rum-sample.view :as v]
   [rum-sample.events :as e]
   [rum-sample.word-assoc.word-assoc :as g]))

(e/load-event-watchers)
#_(v/main-page)
(g/start-game)
(g/show-game-controls)
