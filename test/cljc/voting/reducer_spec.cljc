(ns voting.reducer-spec
  (:require [voting.reducer :refer [reducer]]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer-macros [deftest is testing]])))

(deftest reducer-test
  (testing "handles SET_ENTRIES"
    (let [initial-state {}
          action {:type :set-entries :entries ["Trainspotting"]}
          next-state (reducer initial-state action)]
      (is (= {:entries ["Trainspotting"]}
             next-state))))

  (testing "handles NEXT"
    (let [initial-state {:entries ["Trainspotting" "28 Days Later"]}
          action {:type :next}
          next-state (reducer initial-state action)]
      (is (= {:vote {:pair ["Trainspotting" "28 Days Later"]}
              :entries []}
             next-state))))

  (testing "handles VOTE"
    (let [initial-state {:vote {:pair ["Trainspotting" "28 Days Later"]}
                         :entries []}
          action {:type :vote :entry "Trainspotting"}
          next-state (reducer initial-state action)]
      (is (= {:vote {:pair ["Trainspotting" "28 Days Later"]
                     :tally {"Trainspotting" 1}}
              :entries []}
             next-state))))

  (testing "has an initial state"
    (let [action {:type :set-entries :entries ["Trainspotting"]}
          next-state (reducer nil action)]
      (is (= {:entries ["Trainspotting"]}
             next-state))))

  (testing "can be used with reduce"
    (let [actions [{:type :set-entries
                    :entries ["Trainspotting" "28 Days Later"]}
                   {:type :next}
                   {:type :vote :entry "Trainspotting"}
                   {:type :vote :entry "28 Days Later"}
                   {:type :vote :entry "Trainspotting"}
                   {:type :next}]
          final-state (reduce reducer {} actions)])))
