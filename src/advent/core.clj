(ns advent.core
  (:require [advent.reader :as reader]
            [advent.day1 :as day1]
            [advent.day2 :as day2]))

(def day1-input
  (reader/as-ints "day1/input.txt"))

(def total-fuel-requirements
  (->> day1-input
       (map day1/fuel-required)
       (reduce +)))

(def real-total-fuel-requirements
  (->> day1-input
       (map day1/total-fuel-required)
       (reduce +)))

(def day2-program
  (-> (reader/program "day2/program.txt")
      vec
      (assoc 1 12)
      (assoc 2 2)
      (day2/exec)
      (first)))
