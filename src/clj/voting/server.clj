(ns voting.server
  (:require [org.httpkit.server :as server]
            [clojure.data.json :as json]
            [voting.store :as store]))

(def state (-> (store/make-store)
               (store/dispatch! {:type "SET_ENTRIES"
                                 :entries ["Shallow Grave"
                                           "Trainspotting"
                                           "A Life Less Ordinary"
                                           "The Beach"
                                           "28 Days Later"
                                           "Millions"
                                           "Sunshine"
                                           "Slumdog Millionaire"
                                           "127 Hours"
                                           "Trance"
                                           "Steve Jobs"]})
               (store/dispatch! {:type "NEXT"})))

(def channel-hub (atom {}))

(defn response [state]
  (json/write-str state))

(store/subscribe
 state
 :channels
 #(doseq [channel (keys @channel-hub)]
    (server/send! channel (response (store/get-state state)))))

(defn app [request]
  (server/with-channel request channel
    ;; Store the channel somewhere,
    ;; and use it to send response to client when interesting event happens
    (swap! channel-hub assoc channel request)
    (server/on-close channel (fn [status]
                               ;; remove from hub when channel get closed
                               (swap! channel-hub dissoc channel)))
    (server/on-receive channel
                       (fn [data]
                         (let [action (json/read-str data :key-fn keyword)]
                           (store/dispatch! state action))))
    (server/send! channel (response (store/get-state state)))))
