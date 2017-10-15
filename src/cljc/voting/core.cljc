(ns voting.core
  (:require [voting.reducer :refer [reducer]]))

(defn set-entries [state entries]
  (assoc state :entries entries))

(defmethod reducer :set-entries [state action]
  (set-entries state (:entries action)))

(defn get-winners [vote]
  (if-not vote
    []
    (let [[a b] (:pair vote)
          a-votes (get-in vote [:tally a] 0)
          b-votes (get-in vote [:tally b] 0)]
      (cond
        (> a-votes b-votes) [a]
        (< a-votes b-votes) [b]
        :else [a b]))))

(defn next-vote [state]
  (let [entries (-> state
                    (:entries)
                    (concat (get-winners (:vote state))))]
    (if (= 1 (count entries))
      {:winner (first entries)}
      (merge state {:vote {:pair (take 2 entries)}
                    :entries (drop 2 entries)}))))

(defmethod reducer :next [state action]
  (next-vote state))

(defn vote [state entry]
  (update-in state
             [:vote :tally entry]
             (fnil inc 0)))

(defmethod reducer :vote [state action]
  (vote state (:entry action)))
