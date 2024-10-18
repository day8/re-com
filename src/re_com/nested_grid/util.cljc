(ns re-com.nested-grid.util
  #?(:cljs (:require [reagent.core :as r])))

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
  (loop [coll         coll
         sum          0
         num-below    0 total-below  0 items-below  []
         num-within   0 total-within 0 items-within []
         num-above    0 total-above  0 items-above  []]
    (if (empty? coll)
      [num-below total-below items-below
       num-within total-within items-within
       num-above total-above items-above]
      (let [[i & remainder] coll
            value           (value-fn i)
            new-sum         (+ sum value)]
        (cond
          (< new-sum low)
          (recur remainder new-sum
                 (inc num-below) (+ total-below value) (conj items-below i)
                 num-within total-within items-within
                 num-above total-aboveq items-above)
          (and (>= new-sum low) (<= new-sum high))
          (recur remainder new-sum
                 num-below total-below items-below
                 (inc num-within) (+ total-within value) (conj items-within i)
                 num-above total-above items-above)
          (> new-sum high)
          (recur remainder new-sum
                 num-below total-below items-below
                 num-within total-within items-within
                 (inc num-above) (+ total-above value) (conj items-above i)))))))

(def branch? (some-fn vector? list?))
(def leaf? (complement branch?))
(def leaf-size (fn [node] (cond (number? node) node
                                (map? node) (:size node)
                                (keyword? node) 0)))

(defn walk-size [{:keys [window-start window-end tree size-cache]}]
  (let [sum-size       (volatile! 0)
        windowed-nodes (volatile! #{})
        collect!       (fn [node] (vswap! windowed-nodes conj node))
        cache!         (fn [node size] (vswap! size-cache assoc node size))
        intersection?  (fn [x1 x2]
                         (and (<= x1 window-end)
                              (>= x2 window-start)))
        walk
        (fn walk [path node]
          (cond
            (leaf? node)   (let [leaf-size (leaf-size node)
                                 sum       @sum-size
                                 new-sum   (+ sum leaf-size)]
                             (when (intersection? sum new-sum)
                               (collect! path))
                             (vreset! sum-size new-sum)
                             leaf-size)
            (branch? node) (let [sum        @sum-size
                                 csum       (get @size-cache node)
                                 skippable? (and csum (not (intersection? sum (+ sum csum))))]
                             (if skippable?
                               (let [new-sum (+ sum csum)]
                                 (vreset! sum-size new-sum)
                                 new-sum)
                               (let [descend     (map (partial walk (conj path (first node))))
                                     branch-size (transduce descend + node)]
                                 (when-not csum (cache! node branch-size))
                                 (when (intersection? sum (+ sum branch-size))
                                   (collect! path))
                                 branch-size)))))]
    (walk [] tree)
    {:sum-size       @sum-size
     :windowed-nodes @windowed-nodes}))

#_(walk-size {:window-start 8
              :window-end   12
              :size-cache   (volatile! {})
              :tree         [:root
                             [:a
                              [2 3]
                              [2 3]]
                             [:b
                              [2 3]
                              [2 3]]]})

#(:cljs
  (defn test-component []
    (let [window-size  100
          window-start (r/atom 0)
          window-end   (r/atom 0)])))
