(ns rum-sample.queue
  (:require [clojure.core.async :as a]))

(def global-write-queue
  "underlying channel to facilitate pub/sub"
  (a/chan))

(def publication
  "var that represents the publication that can be subscribed to"
  (a/pub global-write-queue :msg-type))

(defn pub-msg
  "publish a message on the global queue"
  [msg]
  (a/go (>! global-write-queue msg)))

(defn subscribe-to-msg
  "subscribe to the publication for a given message type"
  [msg-type action-fn]
  (let [incoming-msg-chan (a/chan)]
    (a/sub publication msg-type incoming-msg-chan)
    (a/go-loop []
      (action-fn (a/<! incoming-msg-chan))
      (recur))))
