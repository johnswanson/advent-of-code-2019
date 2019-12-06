(ns advent.day6
  (:require [advent.reader :as r]
            [clojure.string :as str]))

(r/lines "day6/test.txt")

(defn parse-edge-str [s]
  (str/split s #"\)"))

(defn make-bidi-graph [edge-strs]
  (->> edge-strs
       (map parse-edge-str)
       (reduce (fn [g [a b]]
                 (-> g
                     (update a conj b)
                     (update b conj a)))
               {})))

(defn make-graph [edge-strs]
  (->> edge-strs
       (map parse-edge-str)
       (reduce (fn [g [a b]]
                 (update g a conj b))
               {})))

(defn count-orbits
  ([g node]
   (count-orbits g node 0))
  ([g node n]
   (if-let [children (g node)]
     (->> children
          (map #(count-orbits g % (inc n)))
          (reduce +)
          (+ n))
     n)))

(defn solve []
  (-> (r/lines "day6/input.txt")
      (make-graph)
      (count-orbits "COM")))

(defn search
  [g start finish visited?]
  (if (contains? (set (g start)) finish)
    []
    (when-let [found (some #(search g % finish (conj visited? start))
                           (remove visited? (g start)))]
      (conj found start))))

(defn solve-part-2 []
  (-> (r/lines "day6/input.txt")
      (make-bidi-graph)
      (search "YOU" "SAN" #{})))
