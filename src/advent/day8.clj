(ns advent.day8
  (:require [advent.reader :as r]))

(def img-source (r/digits "day8/img.sif"))
(def small-img [0 2 2 2 1 1 2 2 2 2 1 2 0 0 0 0])

(defn all-layers [img-source [w h]]
  (partition (* w h) img-source))

(defn number-of-digit [n layer]
  (->> layer (filter #{n}) count))

(defn d [x] (prn x) x)

(defn solve-part-1 []
  (let [[w h] [25 6]

        layer (->> img-source
                   (partition (* w h))
                   (sort-by (partial number-of-digit 0))
                   first)]
    (* (number-of-digit 1 layer)
       (number-of-digit 2 layer))))

(defn solve-part-2 []
  (let [[w h] [25 6]]
    (->> img-source
         (partition (* w h))
         (map #(partition w %)))))
