(ns advent.day10
  (:require [advent.reader :as r]))

(defn slope [[x1 y1] [x2 y2]]
  (if (= x1 x2)
    (if (< y2 y1)
      ##Inf
      ##-Inf)
    (/ (- y2 y1)
       (- x2 x1))))

(defn distance [[x1 y1] [x2 y2]]
  (+ (Math/abs (- y2 y1)) (Math/abs (- x2 x1))))

(defn angle [[x1 y1] [x2 y2]]
  {:angle (Math/atan2 (- x2 x1) (- y2 y1))
   :distance (+ (Math/abs (- y2 y1)) (Math/abs (- x2 x1)))})

(defn lines-from [asteroid asteroids]
  (for [other asteroids]
    (let [slope (slope asteroid other)
          distance (distance asteroid other)]
      {asteroid [{:to other
                  :slope slope
                  :distance distance}]
       other [{:to asteroid
               :slope slope
               :distance (- distance)}]})))

(defn all-lines [lines [a & as]]
  (if a
    (recur (apply merge-with concat lines (lines-from a as)) as)
    lines))

(defn asteroids->lines
  [asteroids]
  (all-lines {} asteroids))

(defn count-reachable-asteroids [asteroids]
  (->> asteroids
       asteroids->lines
       (map (fn [[a lines]]
              [a
               (->> lines
                    (sort-by (juxt :slope :distance))
                    (group-by (juxt :slope (comp pos? :distance))))]))
       (into {})))

(def tmp (count-reachable-asteroids (r/asteroids "day10/part-2.test1.txt")))

#_(defn asteroids->lines
  "Given a collection of asteroids, returns a collection of lines connecting every
  pair of asteroids."
  [[asteroid & asteroids]]
  (when asteroid
    (concat (get-lines-from asteroid asteroids)
            (recur asteroids))))
