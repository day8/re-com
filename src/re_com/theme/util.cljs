(ns re-com.theme.util)

(defn merge-class [& classes]
  (vec (filter some? (flatten classes))))

(defn merge-style [props style]
  (update props :style merge style))

(defn ->v [x] (cond (vector? x)     x
                    (sequential? x) (vec x)
                    (nil? x)        nil
                    :else           [x]))

(defn merge-props-rf [acc {:keys [class attr style] :as m}]
  (merge acc (cond-> (if-not (string? m) m {:style [m]})
               (contains? m :class)
               (assoc :class (into (->v (:class acc)) (->v class)))
               (contains? m :attr)
               (assoc :attr (merge (:attr acc) attr))
               (contains? m :style)
               (assoc :style (merge (:style acc) style)))))

(defn merge-props [& ms] (reduce merge-props-rf {} ms))

