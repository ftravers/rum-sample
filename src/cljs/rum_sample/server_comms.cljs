(ns rum-sample.server-comms
  (:require [cljs.core.async :refer [<! >!]]
            [websocket-client.core :refer [async-websocket]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn websocket-test []
  (let [url  "ws://localhost:8899"
        aws (async-websocket url)]

    ;; Write into the websocket
    (go (>! aws "Sending a test messsage."))

    ;; Read out of the websocket
    (go (.log js/console (<! aws)))))
