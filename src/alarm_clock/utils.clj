(ns alarm-clock.utils)

(defn parse-int [s]
    (Integer. (re-find #"[0-9]*" s)))