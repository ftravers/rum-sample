(ns rum-sample.queue
  (:require [clojure.core.async :as a]))

(def global-write-queue (a/chan))

(def sub-to-me-to-listen
  (a/pub global-write-queue :msg-type))

(defn pub-msg [msg]
  (a/go (>! global-write-queue
            msg)))

(comment

  (a/go (a/>! q/global-write-queue "def"))

  (def happy-msgs (a/chan))

  (a/sub q/sub-to-me-to-listen :happy happy-msgs)

  (comment
    (a/go (a/>! global-write-queue {:msg-type :happy :msg "hello"}))
    (a/go (a/>! global-write-queue {:msg-type :sad :msg "bob"})))

  (a/go-loop []
    (let [msg (a/<! happy-msgs)]
      (.log js/console (str "Got Msg: " msg))
      (reset! my-state (:msg msg)))
    (recur)))
