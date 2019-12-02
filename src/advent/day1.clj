(ns advent.day1)

(defn fuel-required
  [m]
  (max 0 (int (- (Math/floor (/ m 3)) 2))))

(defn total-fuel-required
  [m]
  (->> m
       (iterate fuel-required)
       (next)
       (take-while pos?)
       (reduce +)))
