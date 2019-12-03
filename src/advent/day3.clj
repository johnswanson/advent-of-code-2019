(ns advent.day3
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn distance [move]
  (Integer/parseInt
   (apply str (next move))))

(defn direction [move]
  (case (first move)
    \R :right
    \L :left
    \U :up
    \D :down))

(defn move->relative-point-seq
  "Given a 'move' from the origin, such as R45, returns the complete sequence of
  points crossed during that move, excluding the first point (the origin).

  For example, 'R3' should return [(1,0), (2,0), (3,0)]"
  [move]
  (let [vs (next (range (inc (distance move))))]
    (case (direction move)
      :right (for [v vs]
               [v 0])
      :up (for [v vs]
            [0 v])
      :left (for [v vs]
              [(- v) 0])
      :down (for [v vs]
              [0 (- v)]))))

(defn add [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn moves->point-seq
  ([moves]
   (moves->point-seq [0 0] moves))
  ([origin [move & moves]]
   (if-not move
     []
     (let [point-seq (map (partial add origin) (move->relative-point-seq move))]
       (concat point-seq
               (when (seq moves)
                 (moves->point-seq (last point-seq) moves)))))))


(defn manhattan-distance-from-origin [[x y]]
  (+ (Math/abs x)
     (Math/abs y)))

(defn closest-intersection-distance [wires]
  (->> (map moves->point-seq wires)
       (map set)
       (apply set/intersection)
       (map manhattan-distance-from-origin)
       (apply min)))

(defn parse-wire [wire-str]
  (str/split wire-str #","))

(defn solve [wire-strs]
  (closest-intersection-distance
   (map parse-wire wire-strs)))

(defn point-seq->point->min-delay
  "Given a sequence of points, return a map with point keys and integer values,
  where the value for a given point is the minimum number of steps required to
  reach that point"
  [point-seq]
  (->> (for [[point delay] (map vector point-seq (next (range)))]
         [point delay])
       (reduce (fn [m [p d]]
                 (update m p (fnil min ##Inf) d))
               {})))

(defn lowest-signal-intersection [wires]
  (let [point->min-delays (->> (map moves->point-seq wires)
                               (map point-seq->point->min-delay))
        intersection-points (->> (map keys point->min-delays)
                                 (map set)
                                 (apply set/intersection))
        min-delay (->> intersection-points
                       (map (apply juxt point->min-delays))
                       (map #(apply + %))
                       (apply min))]
    min-delay))

(defn solve-part-2 [wire-strs]
  (lowest-signal-intersection
   (map parse-wire wire-strs)))
