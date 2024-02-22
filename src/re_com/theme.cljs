(ns re-com.theme
  (:refer-clojure :exclude [apply])
  (:require
   [reagent.core :as r]))

(def global (r/atom []))

(defn merge-attr [& ms]
  (let [class-vec #(if (vector? %) % [%])
        ms (remove nil? ms)
        ms (map #(cond-> % (and (map? %)
                                (:class %))
                         (update :class class-vec)) ms)]
    (cond
      (every? map? ms) (clojure.core/apply merge-with merge-attr ms)
      (every? vector? ms) (reduce into ms)
      :else (last ms))))

(defn parts [parts attr _state part]
  (let [current-part (get parts part)]
    (cond-> attr current-part (merge-attr current-part))))

(defn apply-parts [attr parts-spec part]
  (parts parts-spec attr nil part))

(defn rf [[attr ctx] theme]
  (let [result (theme attr ctx)]
    (if (vector? result) result [result ctx])))

(defn apply
  ([attr ctx & themes]
   (->> themes
        (into @global)
        flatten
        (remove nil?)
        (reduce rf [attr ctx])
        first)))
