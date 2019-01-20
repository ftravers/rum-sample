(ns rum-sample.core
  (:require [websocket-server.core :refer [start-ws-server]]))

;; After we start the server a function is returned
;; that we use for stopping the server.
(defonce ws-server (atom nil))

(defn request-handler-upcase-string
  "The function that will take incoming data off the websocket,
  process it and return a reponse.  In our case we'll simply UPPERCASE
  whatever is received."
  [data] (clojure.string/upper-case (str data)))

(defn start
  "Demonstrate how to use the websocket server library."
  []
  (let [port 8899]
    (reset! ws-server (start-ws-server port request-handler-upcase-string))))

(defn stop "Stop websocket server" [] (@ws-server))

(comment
  (start)

  )
