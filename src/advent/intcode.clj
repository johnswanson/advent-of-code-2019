(ns advent.intcode
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.async :as a]
            [advent.reader :as r]))

(defn read-input [_]
  (Integer/parseInt (read-line)))

(defn resolve [e {:keys [mode value]}]
  (case mode
    :immediate value
    :position (-> e :program (nth value))
    :relative (-> e :program (nth (+ value (:relative-base-offset e))))))

(defn update-execution [e {:keys [mode value]} n]
  (assert (not= :immediate mode))
  (let [position (case mode
                   :position value
                   :relative (+ value (:relative-base-offset e)))]
    (update e :program #(assoc % position n))))

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

(defn ^{:args 1}
  modify-relative-base-offset
  [e offset]
  (update e :relative-base-offset + (resolve e offset)))

(def ops
  {1 #'add
   2 #'multiply
   3 #'save-input
   4 #'output
   5 #'jump-if-true
   6 #'jump-if-false
   7 #'less-than
   8 #'equals
   9 #'modify-relative-base-offset
   99 #'halt})

(defn interpret-op [opcode]
  (let [code (mod opcode 100)
        modes (reverse (str (quot opcode 100)))]
    {:code code
     :mode (fn [index]
             (case (nth modes index ::nonexistent)
               \1 :immediate
               \2 :relative
               :position))}))

(defn make-args [args mode]
  (map-indexed
   (fn [i arg]
     {:mode (mode i)
      :value arg})
   args))

(defn exec* [{:as e :keys [instruction-pointer]}]
  (if (< (count (:program e)) (inc instruction-pointer))
    (halt e)
    (let [full-code (nth (:program e) instruction-pointer)
          {:keys [code mode]} (interpret-op full-code)
          op (get ops code)
          arg-count (-> op meta :args)
          args (make-args
                (subvec (:program e)
                        (inc instruction-pointer)
                        (inc (+ instruction-pointer arg-count)))
                mode)
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
  (let [execution {:program (vec (concat program
                                         (repeat 10000 0)))
                   :halted? false
                   :instruction-pointer 0
                   :relative-base-offset 0
                   :pending-output? false
                   :awaiting-input? false}]
    execution))

(defn continue-taking-input [ex]
  (continue (assoc ex :pending-output? nil)))

(defn exec-until-halted [program]
  (iterate continue))

(defn exec-with-inputs [program inputs]
  (->> (reductions (fn [paused-execution in]
                     (-> paused-execution
                         (input in)
                         (continue-taking-input)))
                   (exec program)
                   (concat inputs (repeat 0)))
       (take-while (complement :halted?))
       (next)
       (map :pending-output?)))

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
