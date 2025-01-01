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

(defn walk-size [{:keys [window-start window-end tree size-cache dimension default-size]}]
  (let [sum-size         (volatile! 0)
        tree-hash        (hash tree)
        cached-sum-size  (and size-cache
                              (get-in @size-cache [dimension tree-hash ::sum-size]))
        tail-cached?     cached-sum-size
        paths            (volatile! [])
        keypaths         (volatile! [])
        sizes            (volatile! [])
        sums             (volatile! [])
        collect-size!    (fn [size] (vswap! sizes conj size))
        forget-size!     #(vswap! sizes pop)
        collect-keypath! (fn [keypath] (vswap! keypaths conj keypath))
        forget-keypath!  #(vswap! keypaths pop)
        collect-sum!     (fn [sum]  (vswap! sums conj sum))
        forget-sum!      #(vswap! sums pop)
        collect-path!    (fn [size] (vswap! paths conj size))
        forget-path!     #(vswap! paths pop)
        cache!           (if-not size-cache
                           (constantly nil)
                           (fn [node size] (vswap! size-cache assoc node size)))
        lookup           (if-not size-cache
                           (constantly nil)
                           #(get @size-cache %))
        intersection?    (if-not (and window-start window-end)
                           (constantly true)
                           (fn [[x1 x2]]
                             (and (<= x1 window-end)
                                  (>= x2 window-start))))
        walk
        (fn walk [path node & {:keys [keypath collect-anyway? is-leaf? is-leaf-2? show-below? is-after? last-child?]
                               :or   {is-leaf? true
                                      keypath  []}}]
          (let [sum          @sum-size
                passed-tail? (and tail-cached? (> sum window-end))]
            (cond
              passed-tail?   :exit
              (leaf? node)   (let [leaf-path (conj path node)
                                   show?     (get node :show-above?)
                                   leaf-size (if-not (or is-leaf? show?)
                                               0
                                               (or (leaf-size node)
                                                   default-size))
                                   bounds    [sum (+ sum leaf-size)]]
                               (when (or (intersection? bounds) collect-anyway?)
                                 (collect-path! (cond-> (mapv remove-size leaf-path)
                                                  (or is-leaf? show?)
                                                  (vary-meta merge {}
                                                             (when is-leaf? {:leaf? true})
                                                             (when is-after? {:is-after? true})
                                                             (when show? {:show-above? true})
                                                             (when last-child? {:last-child? true}))))
                                 (collect-sum! sum)
                                 (collect-size! leaf-size)
                                 (collect-keypath! keypath))
                               (vswap! sum-size + leaf-size)
                               leaf-size)
              (branch? node) (let [csize        (lookup node)
                                   cbounds      (when csize [sum (+ sum csize)])
                                   skippable?   (and csize (not (intersection? cbounds)))
                                   children     (vec (children node))
                                   show-after?  (get (own-leaf node) :show-after?)
                                   add-after?   (seq children)
                                   after-child  [(first node)]
                                   all-children (cond-> children
                                                  (and show-after? add-after?) (conj after-child))]
                               (if skippable?
                                 (do (vswap! sum-size + csize) csize)
                                 (let [is-leaf?     (not (seq all-children))
                                       own-path     (conj path (own-leaf node))
                                       own-size     (walk path (own-leaf node) {:collect-anyway? true
                                                                                :is-leaf?        is-leaf?
                                                                                :keypath         (conj keypath 0)
                                                                                :is-after?       is-after?})
                                       descend-tx   (map-indexed
                                                     (fn [i subtree]
                                                       (walk own-path
                                                             subtree
                                                             (merge {:keypath   (cond-> keypath
                                                                                  (not= after-child subtree)
                                                                                  (conj (inc i)))
                                                                     :is-leaf?  true
                                                                     :is-after? (= after-child subtree)}
                                                                    (when (= i (- (count children) 1)) {:last-child? true})))))
                                       total-size   (+ own-size
                                                       (transduce descend-tx + all-children))
                                       total-bounds [sum (+ sum total-size)]]
                                   (when-not (intersection? total-bounds)
                                     (forget-path!)
                                     (forget-sum!)
                                     (forget-size!)
                                     (forget-keypath!))
                                   (when-not csize (cache! node total-size))
                                   total-size))))))]
    (walk [] tree)
    (when-not cached-sum-size
      (vswap! size-cache assoc-in [dimension tree-hash ::sum-size] @sum-size))
    {:sum-size     (or cached-sum-size @sum-size)
     :sums         @sums
     :paths        @paths
     :keypaths     @keypaths
     :sizes        @sizes
     :window-start window-start
     :window-end   window-end}))

(walk-size {:window-start 0
            :window-end   999
            :size-cache   (volatile! {})
            :tree         [{:show? false}]})

(def small-test-tree
  [{:id :a}
   [{:id :x :show-after? true}]
   [{:id :b #_#_:show-above? true :show-after? true} :c :d]
   [:e :f :g]])

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
         :paths [[:z] [:z :h] [:z :h :y] [:z :h :y 40] [:z :h :z] [:z :h :z 20]
                 [:z :i]],
         :sizes [20 20  20  40  20  20  20],
         :sums  [0  180 240 260 300 320 340]})

(defn lazy-grid-tokens
  [{:keys [paths sizes sums sum-size]}
   depth]
  (into ["[start]"]
        (loop [[path & rest-paths]              paths
               [size & rest-sizes]              sizes
               [sum & [next-sum :as rest-sums]] (conj sums sum-size)
               result                           []]
          (let [{:keys [show-above?] :or {show-above? (:leaf? (meta path))}}
                (peek path)
                spacer?     (not= next-sum (+ sum size))
                next-result (cond-> result
                              :do     (conj path)
                              show-above?   (conj (or size 0))
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

