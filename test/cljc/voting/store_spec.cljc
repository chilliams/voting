(ns voting.store-spec
  (:require [voting.store :as store]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer-macros [deftest is testing]])))

(deftest store-test
  (testing "is a Redux store configured with the correct reducer"
    (let [store (store/make-store)]
      (is (= (store/get-state store) {}))
      (store/dispatch! store {:type "SET_ENTRIES"
                              :entries ["Trainspotting" "28 Days Later"]})
      (is (= (store/get-state store)
             {:entries ["Trainspotting" "28 Days Later"]})))))
