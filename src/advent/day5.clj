(ns advent.day5
  (:require [advent.reader :as r]
            [advent.intcode :as i]))

(defn solve []
  (i/exec (vec (r/program "day5/program.txt"))))
