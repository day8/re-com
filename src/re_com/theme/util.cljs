(ns re-com.theme.util)

;;FIXME: this is just here to avoid circular imports with re-com.util
;; really, re-com.util/part should be here, and ->v should be there.
(defn ->v [x] (cond (vector? x)     x
                    (sequential? x) (vec x)
                    (nil? x)        nil
                    :else           [x]))

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
               (assoc :class (into (->v (:class acc)) (->v class)))
               attr
               (assoc :attr (merge (:attr acc) attr))
               style
               (assoc :style (merge (:style acc) style)))))

(defn merge-class [x & classes]
  (if-not (seq classes)
    x
    (into (or (->v (flatten x)) [])
          (flatten classes))))

(defn merge-props [& ms] (reduce rf {} ms))
