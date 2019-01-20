(ns rum-sample.queue
  (:require [clojure.core.async :as a]))

(def global-write-queue (a/chan))

(def sub-to-me-to-listen
  (a/pub global-write-queue :msg-type))

(defn pub-msg
  "to send a message on the global queue"
  [msg]
  (a/go (>! global-write-queue
            msg)))

(defn on-msg-do
  "attach funtions to messages"
  [msg-type action-fn]
  (let [msg-chan (a/chan)]
    (a/sub sub-to-me-to-listen msg-type msg-chan)
    (a/go-loop []
      (action-fn (a/<! msg-chan))
      (recur))))
