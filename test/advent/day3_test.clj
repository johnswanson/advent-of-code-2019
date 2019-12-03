(ns advent.day3-test
  (:require [clojure.test :refer [deftest testing is]]
            [advent.day3 :as day3]))

(deftest move->relative-point-seq-works
  (testing "some simple examples"
    (is (= [[1 0] [2 0] [3 0]]
           (day3/move->relative-point-seq "R3")))
    (is (= [[-1 0] [-2 0] [-3 0]]
           (day3/move->relative-point-seq "L3")))
    (is (= [[0 1] [0 2] [0 3]]
           (day3/move->relative-point-seq "U3")))
    (is (= [[0 -1] [0 -2] [0 -3]]
           (day3/move->relative-point-seq "D3"))))
  (testing "move zero"
    (is (= []
           (day3/move->relative-point-seq "R0")))
    (is (= []
           (day3/move->relative-point-seq "L0")))
    (is (= []
           (day3/move->relative-point-seq "U0")))
    (is (= []
           (day3/move->relative-point-seq "D0")))))


(deftest moves->point-set-works
  (testing "trivial examples"
    (is (= #{[1 0] [2 0] [3 0]}
           (day3/moves->point-set ["R3"]))))
  (testing "i can move up and then go across"
    (is (= #{[0 1] [1 1] [2 1] [3 1]}
           (day3/moves->point-set ["U1" "R3"]))))
  (testing "i can do nothing"
    (is (= #{}
           (day3/moves->point-set []))))
  (testing "i can go back and forth"
    ;; note that the origin is included here because we went back
    (is (= #{[0 0] [1 0] [2 0] [3 0]}
           (day3/moves->point-set ["R3" "L3"])))))

(deftest examples-provides
  (testing "no1"
    (is (= 159
           (day3/solve
            ["R75,D30,R83,U83,L12,D49,R71,U7,L72"
             "U62,R66,U55,R34,D71,R55,D58,R83"]))))
  (testing "no2"
    (is (= 135
           (day3/solve
            ["R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
             "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"])))))

(deftest lowest-signal
  (testing "no1"
    (is (= 610
           (day3/solve-part-2
            ["R75,D30,R83,U83,L12,D49,R71,U7,L72"
             "U62,R66,U55,R34,D71,R55,D58,R83"]))))
  (testing "no2"
    (is (= 410
           (day3/solve-part-2
            ["R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
             "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"])))))
