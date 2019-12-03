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
