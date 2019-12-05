(ns advent.day2
  (:require [advent.reader :as r]
            [advent.intcode :as intcode]))

(defn find-value []
  (let [program (vec (r/program "day2/program.txt"))]
    (for [x (range 100)
          y (range 100)
          :when (-> program
                    (assoc 1 x)
                    (assoc 2 y)
                    intcode/exec
                    first
                    (= 19690720))]
      (+ (* 100 x)
         y))))
