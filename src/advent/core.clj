(ns advent.core
  (:require [advent.reader :as reader]
            [advent.day1 :as day1]
            [advent.day2 :as day2]
            [advent.day3 :as day3]))

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

(def day3-solution-part-1
  (day3/solve (reader/lines "day3/wires.txt")))

(def day3-solution-part-2
  (day3/solve-part-2 (reader/lines "day3/wires.txt")))
