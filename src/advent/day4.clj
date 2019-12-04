(ns advent.day4)

(defn at-least-two-adjacent-digits-same? [n]
  (->> n
       str
       (partition-by identity)
       (map count)
       (some #(< 1 %))))

(defn exactly-two-adjacent-digits-same? [n]
  (->> n
       str
       (partition-by identity)
       (map count)
       (some #(= 2 %))))

(defn digits-increasing? [n]
  (->> n
       str
       (map #(Integer/parseInt (str %)))
       (apply <=)))

(defn solve []
  (->> (range 272091 815432)
       (filter at-least-two-adjacent-digits-same?)
       (filter digits-increasing?)
       count))

(defn solve-part-2 []
  (->> (range 272091 815432)
       (filter exactly-two-adjacent-digits-same?)
       (filter digits-increasing?)
       count))
