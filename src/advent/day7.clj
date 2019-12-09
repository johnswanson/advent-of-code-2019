(ns advent.day7
  (:require [advent.intcode :as i]))

(defn thruster-power [phases program]
    (reduce (fn [prev phase]
              (first (i/exec-with-inputs program [phase prev])))
            0
            phases))

(defn max-power [program]
  (first
   (sort-by
    :thruster-power
    >
    (for [a (range 5)
          b (remove #{a} (range 5))
          c (remove #{a b} (range 5))
          d (remove #{a b c} (range 5))
          e (remove #{a b c d} (range 5))]
      {:phases [a b c d e]
       :thruster-power (thruster-power [a b c d e] program)}))))

(def program (vec (r/program "day7/program.txt")))
