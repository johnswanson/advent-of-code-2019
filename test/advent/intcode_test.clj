(ns advent.intcode-test
  (:require [clojure.test :refer [deftest testing is]]
            [advent.intcode :as intcode]))

(deftest examples-work
  (is (= [2 0 0 0 99]
         (intcode/exec [1 0 0 0 99])))
  (is (= [2 3 0 6 99]
         (intcode/exec [2 3 0 3 99])))
  (is (= [2 4 4 5 99 9801]
         (intcode/exec [2 4 4 5 99 0])))
  (is (= [30 1 1 4 2 5 6 0 99]
         (intcode/exec [1 1 1 4 99 5 6 0 99])))
  (is (= [3500 9 10 70 2 3 11 0 99 30 40 50]
         (intcode/exec
          [1 9 10 3 2 3 11 0 99 30 40 50]))))

(deftest interpreting-opcodes
  (is (= {:code 2
          :modes [false true]}
         (intcode/interpret-op 1002))))
