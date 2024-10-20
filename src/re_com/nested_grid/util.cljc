(ns re-com.nested-grid.util
  (:require [clojure.string :as str]
            #?@(:cljs [[reagent.core :as r]
                       [re-com.util :as u]])))

(def spec? (some-fn vector? seq?))
(def item? (complement spec?))
(def ascend pop)
(def descend conj)

(defn header-spec->header-paths
  ([spec]
   (header-spec->header-paths [] [] spec))
  ([path acc [left & [right :as remainder]]]
   (let [item-left?  (item? left)
         spec-left?  (spec? left)
         descending? (and item-left? (spec? right))
         ascending?  (and spec-left? (item? right) (seq path))
         next-acc    (cond item-left? (conj acc (descend path left))
                           spec-left? (header-spec->header-paths path acc left))
         next-path   (cond descending? (descend path left)
                           ascending?  (ascend path)
                           :else       path)]
     (if (empty? remainder)
       next-acc
       (recur next-path next-acc remainder)))))

(defn cumulative-sum-window [low high value-fn coll]
  (loop [coll       coll
         sum        0
         num-below  0 total-below  0 items-below  []
         num-within 0 total-within 0 items-within []
         num-above  0 total-above  0 items-above  []]
    (if (empty? coll)
      [num-below  total-below  items-below
       num-within total-within items-within
       num-above  total-above  items-above]
      (let [[i & remainder] coll
            value           (value-fn i)
            new-sum         (+ sum value)]
        (cond
          (< new-sum low)
          (recur remainder       new-sum
                 (inc num-below) (+ total-below value) (conj items-below i)
                 num-within      total-within          items-within
                 num-above       total-above           items-above)
          (<= low new-sum high)
          (recur remainder        new-sum
                 num-below        total-below            items-below
                 (inc num-within) (+ total-within value) (conj items-within i)
                 num-above        total-above            items-above)
          (> new-sum high)
          (recur remainder       new-sum
                 num-below       total-below           items-below
                 num-within      total-within          items-within
                 (inc num-above) (+ total-above value) (conj items-above i)))))))

(def children (comp seq rest))
(def own-leaf first)
(def leaf? (some-fn keyword? map? number?))
(defn branch? [node]
  (and (vector? node)
       (leaf? (first node))))
(def invalid? (complement (some-fn leaf? branch?)))
(def leaf-size (fn [node] (cond (number? node)  node
                                (map? node)     (:size node)
                                (keyword? node) 20)))

(defn walk-size [{:keys [window-start window-end tree size-cache]}]
  (println)
  (let [sum-size       (volatile! 0)
        windowed-nodes (volatile! [])
        collect!       (fn [path] (vswap! windowed-nodes conj path))
        cache!         (fn [node size] (vswap! size-cache assoc node size))
        intersection?  (fn [[x1 x2]]
                         (and (<= x1 window-end)
                              (>= x2 window-start)))
        walk
        (fn walk [path node & {:keys [collect-me?] :or {collect-me? true}}]
          (cond
            (leaf? node)   (let [sum       @sum-size
                                 leaf-size (leaf-size node)
                                 leaf-path (conj path node)
                                 bounds    [sum (+ sum leaf-size)]]
                             (when (and (intersection? bounds) collect-me?)
                               (collect! leaf-path))
                             (vreset! sum-size (+ sum leaf-size))
                             leaf-size)
            (branch? node) (let [sum        @sum-size
                                 csize      (get @size-cache node)
                                 cbounds    (when csize [sum (+ sum csize)])
                                 skippable? (and csize (not (intersection? cbounds)))]
                             (if skippable?
                               (let [new-sum (+ sum csize)]
                                 (vreset! sum-size new-sum)
                                 csize)
                               (let [own-path                 (conj path (first node))
                                     own-size                 (walk path (own-leaf node) {:collect-me? false})
                                     _                        (when :always (collect! own-path))
                                     descend-tx               (map (partial walk own-path))
                                     total-size (+ own-size
                                                   (transduce descend-tx + (children node)))
                                     total-bounds [sum (+ sum total-size)]]
                                 (when-not (intersection? total-bounds)
                                   (vswap! windowed-nodes pop))
                                 (when-not csize (cache! node total-size))
                                 total-size)))))]
    (walk [] tree)
    {:sum-size       @sum-size
     :windowed-nodes @windowed-nodes}))

(def test-tree [:a
                [:g
                 [:x 20]
                 [:y 40]
                 [:z 20]]
                [:h
                 [:x 20]
                 [:y 40]
                 [:z 20]]
                [:i
                 [:x 20]
                 [:y 40]
                 [:z 20]]
                [:j
                 [:x 20]
                 [:y 40]
                 [:z 20]]])

#_(walk-size {:window-start 372
              :window-end   472
              :size-cache   (volatile! {})
              :tree         test-tree})
