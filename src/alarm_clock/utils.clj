(ns alarm-clock.utils)

(defn parse-int [s]
  (if (nil? s) nil
    (Integer. (re-find #"[0-9]*" s))))

(defn uuid [] (str (java.util.UUID/randomUUID)))
