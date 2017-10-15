(ns voting.reducer)

(defmulti reducer (fn [state action] (:type action)))
(defmethod reducer :default [state action] state)
