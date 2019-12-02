(ns advent.day2)

(defn get-pos [e pos]
  (nth (:program e) pos))

(defn update-execution [e pos n]
  (-> e
      (update :program #(assoc % pos n))))

(defn ^{:args 3}
  add
  [e *x *y pos]
  (let [x (get-pos e *x)
        y (get-pos e *y)]
    (update-execution e pos (+ x y))))

(defn ^{:args 3}
  multiply
  [e *x *y pos]
  (update-execution e pos (* (get-pos e *x)
                             (get-pos e *y))))

(defn ^{:args 0}
  halt
  [e]
  (assoc e :halted? true))

(def ops
  {1 #'add
   2 #'multiply
   99 #'halt})

(defn exec-op [e idx]
  (if (< (count (:program e)) (inc idx))
    (halt e)
    (let [code (nth (:program e) idx)
          op (get ops code)
          arg-count (-> op meta :args)
          args (subvec (:program e)
                       (inc idx)
                       (inc (+ idx arg-count)))]
      (-> op
          (apply e args)
          (assoc :idx (+ idx (inc arg-count)))))))

(defn exec* [{:as e :keys [idx]}]
  (exec-op e idx))

(defn exec [program]
  (let [execution {:program program
                   :halted? false
                   :idx 0}]
    (->> execution
         (iterate exec*)
         (drop-while (complement :halted?))
         first
         :program)))
