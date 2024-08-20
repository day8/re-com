(ns re-com.theme.util
  (:require [re-com.util :as u]))

(defn merge-props [& ms]
  (let [ms (remove nil? ms)
        ms (map #(cond-> % (and (map? %)
                                (:class %))
                         (update :class u/->v)) ms)]
    (cond
      (every? map? ms) (clojure.core/apply merge-with merge-props ms)
      (every? vector? ms) (reduce into ms)
      :else (last ms))))
