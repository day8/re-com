(ns re-com.nested-v-grid.util
  (:require [clojure.string :as str]
            #?@(:cljs [[reagent.core :as r]
                       [re-com.util :as u]
                       goog.string])))

(defn path->grid-line-name [path]
  (str "rc" (hash path)))

(def branch? sequential?)

(def children (comp seq rest))

(def own-leaf first)

(def leaf? (complement branch?))

(def leaf-size #(get % :size))

(defn remove-size [m]
  (cond-> m (map? m) (dissoc :size)))

(defn ancestry [path]
  (take (count path) (iterate pop path)))

(defn intersection? [x1 size window-start window-end]
  (and (<= x1 window-end)
       (>= (+ x1 size) window-start)))

(defn walk-size [{:keys [window-start
                         window-end
                         tree
                         size-cache
                         dimension
                         default-size
                         show-branch-cells?
                         hide-root?
                         skip-tail?]
                  :or   {skip-tail?   true
                         size-cache   (volatile! {})
                         window-end   js/Number.POSITIVE_INFINITY
                         default-size 20}}]
  (let [sum-size        (volatile! 0)
        paths           (volatile! [])
        keypaths        (volatile! [])
        sizes           (volatile! [])
        sums            (volatile! [])
        tree-hash       (hash tree)
        cached-sum-size (get-in @size-cache [dimension tree-hash ::sum-size])
        walk
        (fn walk [path node & {:keys [keypath collect-anyway? is-leaf? branch-end? last-child? hide?]
                              :or   {is-leaf? true
                                     keypath  []}}]
          (let [sum          @sum-size
                passed-tail? (and skip-tail? cached-sum-size (> sum window-end))]
            (cond
              passed-tail?   :exit
              (branch? node) (let [csize        (get @size-cache node)
                                   skippable?   (and csize (not (intersection? sum csize window-start window-end)))
                                   children     (vec (children node))
                                   show-after?  (or show-branch-cells? (get (own-leaf node) :show-after?))
                                   add-after?   (and (not hide?) (seq children))
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
                                                                                :branch-end?     branch-end?
                                                                                :last-child?     last-child?})
                                       descend-tx   (map-indexed
                                                     (fn [i subtree]
                                                       (walk own-path
                                                             subtree
                                                             (merge {:keypath     (cond-> keypath
                                                                                    (not= after-child subtree)
                                                                                    (conj (inc i)))
                                                                     :is-leaf?    true
                                                                     :branch-end? (= after-child subtree)}
                                                                    (when (= i (- (count children) (when add-after? 1)))
                                                                      {:last-child? true})))))
                                       total-size   (+ own-size
                                                       (transduce descend-tx + all-children))]
                                   (when-not (intersection? sum total-size window-start window-end)
                                     (vswap! paths pop)
                                     (vswap! sums pop)
                                     (vswap! sizes pop)
                                     (vswap! keypaths pop))
                                   (when-not csize (vswap! size-cache assoc node total-size))
                                   total-size)))
              :else          (let [leaf-path (conj path node)
                                   show?     (get node :show-above?)
                                   leaf-size (if-not (or is-leaf? show?)
                                               0
                                               (or (leaf-size node)
                                                   default-size))]
                               (when (or (intersection? sum leaf-size window-start window-end)
                                         collect-anyway?)
                                 (let [new-path (cond-> (mapv remove-size leaf-path)
                                                  (or is-leaf? show?)
                                                  (vary-meta merge {}
                                                             (when is-leaf? {:leaf? true})
                                                             (when branch-end? {:branch-end? true})
                                                             (when show? {:show-above? true})
                                                             (when last-child? {:last-child? true})))]
                                   (vswap! paths conj new-path)
                                   (vswap! sums conj sum)
                                   (vswap! sizes conj leaf-size)
                                   (vswap! keypaths conj keypath)))
                               (vswap! sum-size + leaf-size)
                               leaf-size))))]
    (walk [] tree {:hide? hide-root?})
    (when (and size-cache (not cached-sum-size))
      (vswap! size-cache assoc-in [dimension tree-hash ::sum-size] @sum-size))
    {:sum-size     (or cached-sum-size @sum-size)
     :sums         @sums
     :paths        @paths
     :keypaths     @keypaths
     :sizes        @sizes
     :window-start window-start
     :window-end   window-end}))

(defn grid-tokens
  [{:keys [paths sizes sums sum-size]}]
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

(defn grid-template [header-traversal]
  (str/replace
   (str/join " "
             (map #(cond (string? %) %
                         (vector? %) (str "[" (path->grid-line-name %) "]")
                         (number? %) (str % "px"))
                  (grid-tokens header-traversal)))
   "] [" " "))

(defn grid-cross-template
  ([tokens & more-tokens]
   (grid-cross-template (apply concat tokens more-tokens)))
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
      #_" [end]"))))

#?(:cljs
   (defn grid-spans [grid-tokens]
     (let [results (volatile! {})]
       (mapv (fn [path]
               (doseq [p (ancestry path)]
                 (vswap! results update p inc)))
             grid-tokens)
       @results)))


