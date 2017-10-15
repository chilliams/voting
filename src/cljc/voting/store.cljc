(ns voting.store
  (:require [voting.reducer :refer [reducer]]))

(defprotocol IStore
  (subscribe [this key on-change])
  (unsubscribe [this key])
  (dispatch! [this action])
  (get-state [this]))

(deftype Store [reducer *actions]
  IStore
  (subscribe [this key on-change]
    (add-watch *actions
               key
               (fn [key atom old-state new-state]
                 (on-change)))
    this)
  (unsubscribe [this key]
    (remove-watch *actions key)
    this)
  (dispatch! [this action]
    (swap! *actions #(conj % action))
    this)
  (get-state [this] (reduce reducer {} @*actions)))

(defn make-store []
  (Store. reducer (atom [])))
