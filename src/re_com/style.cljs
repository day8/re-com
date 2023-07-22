(ns re-com.style
  (:require [re-com.util :refer [deep-merge]]))

(def base {:margin 0
           :padding 0})

(defn class->vec [c]
  (cond-> c (string? c) vector))

(defn vec->class [c]
  (cond-> c
    (and (not (second c))
         (string? (first c))) first))

(defn merge-class [c1 c2]
  (vec->class ((fnil into [])
               (class->vec c1)
               (class->vec c2))))

(defn merge [c1 c2])

(defn with-part [parts k attrs]
  (if-let [parts (-> parts :attr (get k))]
    (-> attrs
        (update :class merge-class (:class parts))
        (deep-merge (select-keys parts [:style :attr])))
    attrs))
