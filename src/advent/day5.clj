(ns advent.day5
  (:require [advent.reader :as r]
            [advent.intcode :as i]
            [clojure.java.io :as io]))

(defn solve []
  (i/exec-with-inputs (vec (r/program "day5/program.txt"))
                      [5]))
