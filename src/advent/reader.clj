(ns advent.reader
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn lines
  [file-name]
  (->> (io/resource file-name)
       (io/reader)
       (line-seq)))

(defn as-ints
  [file-name]
  (map #(Integer/parseInt %) (lines file-name)))

(defn program [file-name]
  (map #(Integer/parseInt (string/trim %))
       (-> (io/resource file-name)
           (io/reader)
           (slurp)
           (string/split #","))))

(defn digits [file-name]
  (->> (slurp (io/resource file-name))
       (map str)
       (butlast)
       (map #(Integer/parseInt %))))

(defn- asteroids-on-line [map-line]
  (->> map-line
       (map-indexed vector)
       (filter #(= (second %) \#))
       (map first)))

(defn- asteroids [file-name]
  (for [[y map-line] (map-indexed vector (lines file-name))
        x (asteroids-on-line map-line)]
    [x y]))
