(ns advent.intcode
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.async :as a]))

(defn read-input [_]
  (Integer/parseInt (read-line)))

(defn resolve [e {:keys [pointer? value]}]
  (if pointer?
    (nth (:program e) value)
    value))

(defn update-execution [e {:keys [pointer? value]} n]
  (assert pointer?)
  (update e :program #(assoc % value n)))

(defn ^{:args 1}
  save-input
  [e pos]
  (if-let [input (:input e)]
    (-> e
        (dissoc :input)
        (update-execution pos input))
    (assoc e :awaiting-input? true)))

(defn ^{:args 1}
  output
  [e pos]
  (assoc e :pending-output? (resolve e pos)))

(defn ^{:args 3}
  less-than
  [e p1 p2 p3]
  (if (< (resolve e p1) (resolve e p2))
    (update-execution e p3 1)
    (update-execution e p3 0)))

(defn ^{:args 3}
  equals
  [e p1 p2 p3]
  (if (= (resolve e p1) (resolve e p2))
    (update-execution e p3 1)
    (update-execution e p3 0)))

(defn ^{:args 3}
  add
  [e *x *y pos]
  (let [x (resolve e *x)
        y (resolve e *y)]
    (update-execution e pos (+ x y))))

(defn ^{:args 3}
  multiply
  [e *x *y pos]
  (update-execution e pos (* (resolve e *x)
                             (resolve e *y))))

(defn ^{:args 0}
  halt
  [e]
  (assoc e :halted? true))

(defn ^{:args 2}
  jump-if-true
  [e p1 p2]
  (if-not (zero? (resolve e p1))
    (assoc e :instruction-pointer (resolve e p2))
    e))

(defn ^{:args 2}
  jump-if-false
  [e p1 p2]
  (if (zero? (resolve e p1))
    (assoc e :instruction-pointer (resolve e p2))
    e))

(def ops
  {1 #'add
   2 #'multiply
   3 #'save-input
   4 #'output
   5 #'jump-if-true
   6 #'jump-if-false
   7 #'less-than
   8 #'equals
   99 #'halt})

(defn interpret-op [opcode]
  (let [code (mod opcode 100)
        modes (mapv #(= \1 %) (reverse (str (quot opcode 100))))]
    {:code code
     :immediate-mode? modes}))

(defn make-args [args immediate-mode?]
  (map-indexed
   (fn [i arg]
     (if (get immediate-mode? i false)
       {:pointer? false
        :value arg}
       {:pointer? true
        :value arg}))
   args))

(defn exec* [{:as e :keys [instruction-pointer]}]
  (if (< (count (:program e)) (inc instruction-pointer))
    (halt e)
    (let [full-code (nth (:program e) instruction-pointer)
          {:keys [code immediate-mode?]} (interpret-op full-code)
          op (get ops code)
          arg-count (-> op meta :args)
          args (make-args
                (subvec (:program e)
                        (inc instruction-pointer)
                        (inc (+ instruction-pointer arg-count)))
                immediate-mode?)
          new-e (apply op e args)]
      (if (or (not= (:instruction-pointer new-e)
                    (:instruction-pointer e))
              (:awaiting-input? new-e))
        new-e
        (update new-e :instruction-pointer (fn [new-instruction-pointer]
                                             (+ instruction-pointer (inc arg-count))))))))

(defn input [env v]
  (assoc env :input v :awaiting-input? false))

(defn output? [execution]
  (:pending-output? execution))

(defn take-output [execution]
  (assoc execution :pending-output? nil))

(defn halted? [execution]
  (:halted? execution))

(defn awaiting-input? [execution]
  (:awaiting-input? execution))

(defn paused? [execution]
  (some #(% execution) [output? halted? awaiting-input?]))

(defn continue [env]
  (let [result
        (->> env
             (iterate exec*)
             (drop-while #(not (paused? %)))
             first)]
    result))

(defn exec [program]
  (let [execution {:program program
                   :halted? false
                   :instruction-pointer 0
                   :pending-output? false
                   :awaiting-input? false}]
    execution))

(defn exec-with-inputs [program inputs]
  (:pending-output?
   (reduce (fn [paused-execution in]
             (-> paused-execution
                 (input in)
                 (continue)))
           (exec program)
           inputs)))

#_(defn exec [program]
  (let [execution {:program program
                   :halted? false
                   :instruction-pointer 0}]
    (map #(Integer/parseInt %)
         (str/split-lines
          (with-out-str
            (->> execution
                 (iterate exec*)
                 (drop-while (complement :halted?))
                 first
                 :program))))))

#_(defn exec-with-inputs [program inputs]
  (binding [*in* (io/reader (char-array (str/join "\n" inputs)))]
    (exec program)))
