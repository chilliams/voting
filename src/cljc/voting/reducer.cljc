(ns voting.reducer
  (:require [voting.core :as voting]))

(defn reducer [state action]
  (let [state (if state state {})]
    (case (:type action)
      "SET_ENTRIES" (voting/set-entries state (:entries action))
      "NEXT" (voting/next-vote state)
      "VOTE" (update state :vote #(voting/vote % (:entry action)))
      state)))
