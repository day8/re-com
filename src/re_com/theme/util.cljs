(ns re-com.theme.util
  (:require [re-com.util :as u]))

#_(defn merge-props [& ms]
    (let [ms (remove nil? ms)
          ms (map #(cond-> % (and (map? %)
                                  (:class %))
                           (update :class u/->v)) ms)]
      (cond
        (every? map? ms) (clojure.core/apply merge-with merge-props ms)
        (every? vector? ms) (reduce into ms)
        :else (last ms))))

(defn rf [acc {:keys [class attr style] :as m}]
  (merge acc (cond-> (if-not (string? m) m {:style [m]})
               class
               (assoc :class (into (u/->v (:class acc)) (u/->v class)))
               attr
               (assoc :attr (merge (:attr acc) attr))
               style
               (assoc :style (merge (:style acc) style)))))

(defn merge-props [& ms] (reduce rf {} ms))
