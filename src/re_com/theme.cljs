(ns re-com.theme
  (:refer-clojure :exclude [apply])
  (:require
   [reagent.core :as r]))

(def global (r/atom []))

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
    (merge-props props (get part->props part))))

(defn rf [[props ctx] theme]
  (let [result (theme props ctx)]
    (if (vector? result) result [result ctx])))

(defn apply
  ([props ctx & themes]
   (->> themes
        (into @global)
        flatten
        (remove nil?)
        (reduce rf [props ctx])
        first)))
