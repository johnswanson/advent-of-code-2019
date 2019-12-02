(ns advent.day1-test
  (:require [clojure.test :refer [deftest testing is]]
            [advent.day1 :as day1]))

(deftest fuel-required-for-module-works
  (testing "The examples work"
    (is (= 2 (day1/fuel-required 12)))
    (is (= 2 (day1/fuel-required 14)))
    (is (= 654 (day1/fuel-required 1969)))
    (is (= 33583 (day1/fuel-required 100756))))
  (testing "no negative fuel"
    (is (= 0 (day1/fuel-required 2))))
  (testing "fuel-counter-adder-upper-works"
    (is (= 34241
           (->> [12 14 1969 100756]
                (map day1/fuel-required)
                (reduce + ))))))

(deftest total-fuel-required-works
  (testing "the examples work"
    (is (= 2 (day1/total-fuel-required 12)))
    (is (= 966 (day1/total-fuel-required 1969)))
    (is (= 50346 (day1/total-fuel-required 100756)))))
