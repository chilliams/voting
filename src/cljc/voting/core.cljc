(ns voting.core)

(defn set-entries [state entries]
  (assoc state :entries entries))

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

(defn vote [state entry]
  (update-in state
             [:tally entry]
             (fnil inc 0)))
