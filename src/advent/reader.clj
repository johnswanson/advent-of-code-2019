(ns advent.reader
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn- lines
  [file-name]
  (io/reader (io/resource file-name)))

(defn as-ints
  [file-name]
  (->> (io/resource file-name)
       (io/reader)
       (line-seq)
       (map #(Integer/parseInt %))))

(defn program [file-name]
  (map #(Integer/parseInt (string/trim %))
       (-> (io/resource file-name)
           (io/reader)
           (slurp)
           (string/split #","))))
