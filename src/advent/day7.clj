(ns advent.day7
  (:require [advent.intcode :as i]
            [advent.reader :as r]))

(defn thruster-power [phases program]

  (reduce (fn [prev phase]
            (i/exec-with-inputs program [phase prev]))
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

(def program-2 [3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0])

(def program-3 [3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5])
(def program-4 [3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,
                -5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,
                53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10])

(defn thruster-power* [[a & amps]]
  (loop [last-amp a
         amps (into clojure.lang.PersistentQueue/EMPTY amps)]
    (let [last-out (i/output? last-amp)
          next-amp (-> amps peek (i/input last-out) i/continue)
          next-amps (-> amps pop (conj (i/take-output last-amp)))]
      (if (i/halted? next-amp)
        last-out
        (recur next-amp next-amps)))))

(defn thruster-power-feedback [phases program]
  (let [[a & more] (map-indexed
                    (fn [i phase]
                      (-> (i/exec program)
                          (i/input phase)
                          i/continue
                          (assoc ::name (nth [:a :b :c :d :e] i))))
                    phases)
        a (-> a (i/input 0) i/continue)]
    (thruster-power* (conj more a))))

(defn max-power-2 [program]
  (first
   (sort-by
    :thruster-power
    >
    (for [a (range 5 10)
          b (remove #{a} (range 5 10))
          c (remove #{a b} (range 5 10))
          d (remove #{a b c} (range 5 10))
          e (remove #{a b c d} (range 5 10))]
      {:phases [a b c d e]
       :thruster-power (thruster-power-feedback [a b c d e] program)}))))
