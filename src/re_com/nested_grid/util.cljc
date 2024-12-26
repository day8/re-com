(ns re-com.nested-grid.util
  (:require [clojure.string :as str]
            #?@(:cljs [[reagent.core :as r]
                       [re-com.util :as u]
                       goog.string])))

(defn path->grid-line-name [path]
  (str "rc" (hash path)))

#?(:cljs
   (defn grid-template
     ([tokens & more-tokens]
      (grid-template (apply concat tokens more-tokens)))
     ([tokens]
      (let [rf (fn [s group]
                 (str s " "
                      (cond (number? (first group))
                            (str/join " " (map u/px group))
                            (string? (first group))
                            (str/join " " group)
                            :else
                            (str "[" (str/join " " (map path->grid-line-name group)) "]"))))]
        (str
         (->> tokens
              (partition-by (some-fn number? string?))
              (reduce rf ""))
         #_" [end]")))))

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

(def header-spec? sequential?)

(def branch? header-spec?)

(def children (comp seq rest))

(def own-leaf first)

(def leaf? (complement header-spec?))

(def invalid? (complement (some-fn leaf? branch?)))

(def leaf-size #(get % :size 20))

(defn remove-size [m]
  (cond-> m (map? m) (dissoc :size)))

(defn walk-size [{:keys [window-start window-end tree size-cache dimension tree-depth default-size]}]
  (let [sum-size               (volatile! 0)
        depth                  (volatile! 0)
        tree-hash              (hash tree)
        cached-depth           (and size-cache
                                    (get-in @size-cache [dimension tree-hash ::depth]))
        cached-sum-size        (and size-cache
                                    (get-in @size-cache [dimension tree-hash ::sum-size]))
        tail-cached?           (and cached-depth cached-sum-size)
        windowed-paths         (volatile! [])
        windowed-leaf-paths    (volatile! [])
        windowed-keypaths      (volatile! [])
        windowed-leaf-keypaths (volatile! [])
        windowed-sizes         (volatile! [])
        windowed-sums          (volatile! [])
        collect-depth!         (fn [n] (vswap! depth max n))
        collect-size!          (fn [size] (vswap! windowed-sizes conj size))
        forget-size!           #(vswap! windowed-sizes pop)
        collect-leaf-path!     (fn [path]  (vswap! windowed-leaf-paths conj path))
        collect-keypath!       (fn [keypath] (vswap! windowed-keypaths conj keypath))
        collect-leaf-keypath!  (fn [keypath] (vswap! windowed-leaf-keypaths conj keypath))
        forget-keypath!        #(vswap! windowed-keypaths pop)
        collect-sum!           (fn [sum]  (vswap! windowed-sums conj sum))
        forget-sum!            #(vswap! windowed-sums pop)
        collect-path!          (fn [size] (vswap! windowed-paths conj size))
        forget-path!           #(vswap! windowed-paths pop)
        cache!                 (if-not size-cache
                                 (constantly nil)
                                 (fn [node size] (vswap! size-cache assoc node size)))
        lookup                 (if-not size-cache
                                 (constantly nil)
                                 #(get @size-cache %))
        intersection?          (if-not (and window-start window-end)
                                 (constantly true)
                                 (fn [[x1 x2]]
                                   (and (<= x1 window-end)
                                        (>= x2 window-start))))
        walk
        (fn walk [path node & {:keys [collect-me? keypath]
                               :or   {collect-me? true}}]
          (let [sum          @sum-size
                passed-tail? (and tail-cached? (> sum window-end))]
            (cond
              passed-tail?   :exit
              (leaf? node)   (let [leaf-path (conj path node)
                                   show?     (get node :show?
                                                  (= tree-depth (count leaf-path))) ;;TODO counting is slow, even at O(1)! maybe accumulate the depth instead of counting it.
                                   leaf-size (when show?
                                               (or (leaf-size node)
                                                   default-size))
                                   bounds    [sum (+ sum leaf-size)]]
                               (when (intersection? bounds)
                                 (when show?
                                   (collect-leaf-path! leaf-path)
                                   (collect-leaf-keypath! keypath))
                                 (when collect-me?
                                   (collect-path! leaf-path)
                                   (collect-sum! sum)
                                   (collect-size! leaf-size)
                                   (collect-keypath! keypath)))
                               (vreset! sum-size (+ sum leaf-size))
                               leaf-size)
              (branch? node) (let [csize       (lookup node)
                                   new-keypath (conj keypath 0)
                                   cbounds     (when csize [sum (+ sum csize)])
                                   skippable?  (and csize (not (intersection? cbounds)))]
                               (if skippable?
                                 (let [new-sum (+ sum csize)]
                                   (vreset! sum-size new-sum)
                                   csize)
                                 (let [own-path     (conj path (own-leaf node))
                                       own-size     (walk path (own-leaf node) {:collect-me? false})
                                       _            (collect-path! own-path)
                                       _            (collect-size! own-size)
                                       _            (collect-sum!  sum)
                                       _            (collect-keypath! new-keypath)
                                       _            (when-not (or tree-depth
                                                                  cached-depth)
                                                      (collect-depth! (count own-path)))
                                       descend-tx   (map-indexed
                                                     (fn [i subtree]
                                                       (walk own-path
                                                             subtree
                                                             {:keypath
                                                              (conj keypath (inc i))})))
                                       total-size   (+ own-size
                                                       (transduce descend-tx + (children node)))
                                       total-bounds [sum (+ sum total-size)]]
                                   (when-not (intersection? total-bounds)
                                     (forget-path!)
                                     (forget-sum!)
                                     (forget-size!)
                                     (forget-keypath!))
                                   (when-not csize (cache! node total-size))
                                   total-size))))))]
    (walk [] tree {:keypath []})
    (when-not cached-depth
      (vswap! size-cache assoc-in [dimension tree-hash ::depth] @depth))
    (when-not cached-sum-size
      (vswap! size-cache assoc-in [dimension tree-hash ::sum-size] @sum-size))
    {:sum-size               (or cached-sum-size @sum-size)
     :depth                  (or tree-depth (inc cached-depth) (inc @depth))
     :windowed-sums          @windowed-sums
     :windowed-paths         (mapv #(mapv remove-size %) @windowed-paths)
     :windowed-keypaths      @windowed-keypaths
     :windowed-leaf-keypaths @windowed-leaf-keypaths
     :windowed-sizes         @windowed-sizes
     :windowed-leaf-paths    (mapv #(mapv remove-size %) @windowed-leaf-paths)
     :window-start           window-start
     :window-end             window-end}))

(walk-size {:window-start 0
            :window-end   999
            :size-cache   (volatile! {})
            :tree         [{:show? false}]})

(def test-tree [{:id :z :label "ZZ" :show? false}
                [:g
                 [{:id :x :label "HIHI" :size 99}
                  {:label "something" :size 20}]
                 [{:id :y :label "HIHI"}
                  [{:label "sometihng-else" :size 40}]]
                 [:z {:size 20}]]
                [:h
                 [:x {:id 20}]
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

(def big-test-tree
  (into test-tree
        (repeatedly 1000 #(do [(keyword (gensym))
                               [:x 20]
                               [:y 40]
                               [:z 20]]))))

(def huge-test-tree
  (into test-tree
        [(into [:hhh]
               (repeatedly 10000 #(do [(keyword (gensym))
                                       [:x 20]
                                       [:y 40]
                                       [:z 20]
                                       [:h 10]])))]))

#_(walk-size {:window-start 0
              :window-end   472
              :size-cache   (volatile! {})
              :tree         test-tree})

(def td {:sum-size       660,
         :window-end     342,
         :window-start   242,
         :windowed-paths [[:z] [:z :h] [:z :h :y] [:z :h :y 40] [:z :h :z] [:z :h :z 20]
                          [:z :i]],
         :windowed-sizes [20 20  20  40  20  20  20],
         :windowed-sums  [0  180 240 260 300 320 340]})

(defn lazy-grid-tokens
  [{:keys [windowed-paths windowed-sizes windowed-sums sum-size]}
   depth]
  (into ["[start]"]
        (loop [[path & rest-paths]              windowed-paths
               [size & rest-sizes]              windowed-sizes
               [sum & [next-sum :as rest-sums]] (conj windowed-sums sum-size)
               result                           []]
          (let [{:keys [show?] :or {show? (= depth (count path))}}
                (peek path)
                spacer?     (not= next-sum (+ sum size))
                next-result (cond-> result
                              :do     (conj path)
                              show?   (conj size)
                              spacer? (conj "[spacer]"
                                            (- next-sum size sum)))]
            (if (empty? rest-sizes)
              (conj next-result "[end]")
              (recur rest-paths rest-sizes rest-sums next-result))))))

(defn lazy-grid-template [grid-tokens]
  (str/replace
   (str/join " "
             (map #(cond (string? %) %
                         (vector? %) (str "[" (path->grid-line-name %) "]")
                         (number? %) (str % "px"))
                  grid-tokens))
   "] [" " "))

(defn ancestry [path]
  (take (count path) (iterate pop path)))

#?(:cljs
   (defn grid-spans [grid-tokens]
     (let [results (volatile! {})]
       (mapv (fn [path]
               (doseq [p (ancestry path)]
                 (vswap! results update p inc)))
             grid-tokens)
       @results)))

