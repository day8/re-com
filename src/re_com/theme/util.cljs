(ns re-com.theme.util)

(defn merge-class [& classes]
  (filterv some? (flatten classes)))

(defn style [props & styles]
  (apply update props :style merge styles))

(defn attr [props & attrs]
  (apply update props :attr merge attrs))

(defn class [props & classes]
  (apply update props :class merge-class classes))

(defn ->v [x] (cond (vector? x)     x
                    (sequential? x) (vec x)
                    (nil? x)        nil
                    :else           [x]))

(defn merge-props-rf [acc {:keys [class attr style] :as m}]
  (merge acc (cond-> m
               (contains? m :class)
               (assoc :class (into (->v (:class acc)) (->v class)))
               (contains? m :attr)
               (assoc :attr (merge (:attr acc) attr))
               (contains? m :style)
               (assoc :style (merge (:style acc) style)))))

(defn merge-props [& ms] (reduce merge-props-rf {} ms))
