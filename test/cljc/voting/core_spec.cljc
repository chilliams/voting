(ns voting.core-spec
  (:require [voting.core :as voting]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer-macros [deftest is testing]])))

(deftest application-logic
  (testing "set-entries"
    (testing "adds the entries to the state"
      (let [state {}
            entries ["Trainspotting" "28 Days Later"]
            next-state (voting/set-entries state entries)]
        (is (= {:entries ["Trainspotting" "28 Days Later"]}
               next-state)))))

  (testing "next-vote"
    (testing "takes the next two entries under vote"
      (let [state {:entries ["Trainspotting" "28 Days Later" "Sunshine"]}
            next-state (voting/next-vote state)]
        (is (= {:vote {:pair ["Trainspotting" "28 Days Later"]}
                :entries ["Sunshine"]}
               next-state))))
    (testing "puts winner of current vote back to entries"
      (let [state {:vote {:pair ["Trainspotting" "28 Days Later"]
                          :tally {"Trainspotting" 4
                                  "28 Days Later" 2}}
                   :entries ["Sunshine" "Millions" "127 Hours"]}
            next-state (voting/next-vote state)]
        (is (= {:vote {:pair ["Sunshine" "Millions"]}
                :entries ["127 Hours" "Trainspotting"]}
               next-state))))
    (testing "puts both from tied vote back to entries"
      (let [state {:vote {:pair ["Trainspotting" "28 Days Later"]
                          :tally {"Trainspotting" 3
                                  "28 Days Later" 3}}
                   :entries ["Sunshine" "Millions" "127 Hours"]}
            next-state (voting/next-vote state)]
        (is (= {:vote {:pair ["Sunshine" "Millions"]}
                :entries ["127 Hours" "Trainspotting" "28 Days Later"]}
               next-state))))
    (testing "marks winner when just one entry left"
      (let [state {:vote {:pair ["Trainspotting" "28 Days Later"]
                          :tally {"Trainspotting" 4
                                  "28 Days Later" 2}}
                   :entries []}
            next-state (voting/next-vote state)]
        (is (= {:winner "Trainspotting"}
               next-state)))))

  (testing "vote"
    (testing "creates a tally for the voted entry"
      (let [state {:vote {:pair ["Trainspotting" "28 Days Later"]}
                   :entries []}
            next-state (voting/vote state "Trainspotting")]
        (is (= {:vote {:pair ["Trainspotting" "28 Days Later"]
                       :tally {"Trainspotting" 1}}
                :entries []}
               next-state))))
    (testing "adds to existing tally for the voted entry"
      (let [state {:vote {:pair ["Trainspotting" "28 Days Later"]
                          :tally {"Trainspotting" 3
                                  "28 Days Later" 2}}
                   :entries []}
            next-state (voting/vote state "Trainspotting")]
        (is (=  {:vote {:pair ["Trainspotting" "28 Days Later"]
                        :tally {"Trainspotting" 4
                                "28 Days Later" 2}}
                 :entries []}
                next-state))))))
