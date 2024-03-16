(ns re-com.theme.util)

(defn merge-props [& ms]
  (let [class-vec #(if (vector? %) % [%])
        ms (remove nil? ms)
        ms (map #(cond-> % (and (map? %)
                                (:class %))
                         (update :class class-vec)) ms)]
    (cond
      (every? map? ms) (clojure.core/apply merge-with merge-props ms)
      (every? vector? ms) (reduce into ms)
      :else (last ms))))

(defn parts [part->props]
  (fn [props {:keys [part]}]
    (if-let [v (get part->props part (part->props (keyword (name part))))]
      (merge-props props v)
      props)))
