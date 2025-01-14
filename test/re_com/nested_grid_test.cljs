(ns re-com.nested-grid-test
  (:require
   [cljs.test :refer-macros [is are deftest]]
   [re-com.nested-v-grid.util :as ngu]
   [re-demo.nested-grid :as demo]))

(def main-keys [:header-paths :keypaths :sizes :sum-size :positions])

(deftest header-size
  (are [x y] (= (ngu/header-size x 20) y)
    :a                 20
    {:id :a}           20
    {:id :a :size 100} 100))

(deftest walk-size-basic
  (is (= (-> (ngu/window {:header-tree [:a [:b] [:c]]})
             (select-keys main-keys))
         {:header-paths [[:a] [:a :b] [:a :c]]
          :keypaths     [[0]  [1 0]   [2 0]]
          :sizes        [0    20      20]
          :positions    [0    0       20]
          :sum-size     40})
      "A sequential value is a branch.
       Its first item is its header-spec.
       The rest are children.
       Its keypath locates its header-spec in the tree.
       Its header-path lists the header-specs encountered along the keypath.
       It has an individual size and a position")
  (is (= (-> (ngu/window {:header-tree [:a :b :c]})
             (select-keys main-keys))
         {:header-paths [[:a] [:a :b] [:a :c]]
          :keypaths     [[0]  [1]     [2]]
          :sizes        [0    20      20]
          :positions    [0    0       20]
          :sum-size     40})
      "A non-sequential value is a header-spec.
       It has no children.
       Compared to a sequential value, its keypath is different,
       but its header-path is the same."))

(deftest walk-size-bounded
  (is (= (-> (ngu/window {:header-tree         [:a :b :c]
                          :window-start 0
                          :window-end   19})
             (select-keys main-keys))
         {:header-paths [[:a] [:a :b]]
          :keypaths     [[0]  [1]]
          :sizes        [0    20]
          :positions    [0    0]
          :sum-size     40})
      "When passed a pair of window-bounds,
       walk-size returns information about the visible headers.
       A header's bounds start at its position (i.e. the sum of previous sizes)
       and span its individual size.
       It is visible when its bounds intersect the window bounds.
       It is also visible when any of its descendents intersects the window.")
  (is (= (-> (ngu/window {:header-tree         [:a :b :c]
                          :window-start 21
                          :window-end   39})
             (select-keys main-keys))
         {:header-paths [[:a] [:a :c]]
          :keypaths     [[0]  [2]]
          :sizes        [0    20]
          :positions    [0    20]
          :sum-size     40})))

(deftest walk-size-cached
  (let [size-cache       (atom {})
        cache-miss-count (atom 0)
        lookup-count     (atom 0)
        cache-misses     (atom [])
        restart!         #(do (reset! cache-miss-count 0)
                              (reset! lookup-count 0)
                              (reset! cache-misses []))
        _                (add-watch size-cache :setup
                                    #(swap! cache-miss-count inc))
        branch-a         [:a :x]
        header-y         {:id :y :size 20}
        header-y-resized {:id :y :size 21}
        branch-b         [:b header-y]
        branch-b-resized [:b header-y-resized]
        tree             [:root branch-a branch-b]
        tree-resized     [:root branch-a branch-b-resized]
        tree-reordered   [:root branch-b branch-a]
        tree-deleted     [:root branch-a]
        cache-fn         (fn [node size]
                           (swap! cache-misses conj node)
                           (swap! size-cache assoc node size))
        lookup-fn        #(do
                            (swap! lookup-count inc)
                            (get @size-cache %))]
    (is true
        "A branch-node's total-size includes the sum of its descendents' sizes.
         Its total-bounds start at its position and span its total-size.
         Once calculated, walk-size keeps this total-size in a cache,
         keyed by the branch node's identity.

         When a branch-node is cached, the traversal does not descend into it.
         One exception is when the branch-node's total-bounds intersect the window-bounds.
         In that case, a descent is needed - not to calculate sizes,
         But to determine the visible header-paths & keypaths.
         walk-size minimizes work by collecting both sizes and paths from a single traversal.

         Assuming a balanced tree,
         The behavior of the cache, in response to various UX behaviors,
         is the main factor in walk-size's complexity:")
    (let [{:keys [nodes-traversed]} (ngu/window {:header-tree         tree
                                                 :size-cache   size-cache
                                                 :cache-fn     cache-fn
                                                 :lookup-fn    lookup-fn
                                                 :window-start 0
                                                 :window-end   19})]
      (are [node value] (= (get @size-cache node) value)
        tree     40
        branch-a 20
        branch-b 20)
      (is (= nodes-traversed [tree
                              :root
                              branch-a
                              :a
                              :x
                              branch-b
                              :b
                              header-y])
          "1) Setup: O(number of distinct nodes).
              walk-size- must traverse the entire tree once, to fill the cache."))
    (let [{:keys [nodes-traversed]} (ngu/window {:header-tree         tree
                                                 :size-cache   size-cache
                                                 :cache-fn     cache-fn
                                                 :lookup-fn    lookup-fn
                                                 :window-start 21
                                                 :window-end   39})]
      (is (= nodes-traversed [tree
                              :root
                              branch-a
                              branch-b
                              :b
                              header-y])
          "2) Scrolling: O(num. of visible leaf nodes & their ancestors).
              walk-size does not descend into branches which are both cached and invisible.
              In this case, it does not descend into branch-a.
              branch-a is cached, since it has not changed since the cache was set up."))
    (let [{:keys [nodes-traversed]} (ngu/window {:header-tree         tree-reordered
                                                 :size-cache   size-cache
                                                 :cache-fn     cache-fn
                                                 :lookup-fn    lookup-fn
                                                 :window-start 41
                                                 :window-end   ##Inf})]
      (is (= nodes-traversed [tree-reordered
                              :root
                              branch-b
                              branch-a])
          "3) Moving nodes around: O(num. of visible nodes & their ancestors).
              For instance, drag & drop movement.
              The traversal descends only as deeply as each moved-node,
              since both moved nodes (i.e. branch-a & branch-b) are still cached.
              Note: Window bounds are totally outside the tree for this assertion.
                    Otherwise, walk-size would still descend into any visible nodes
                    (regardless of the cache)."))
    (let [{:keys [nodes-traversed]} (ngu/window {:header-tree         tree-resized
                                                 :size-cache   size-cache
                                                 :cache-fn     cache-fn
                                                 :lookup-fn    lookup-fn
                                                 :window-start 41
                                                 :window-end   ##Inf})]
      (is (= nodes-traversed [tree-resized
                              :root
                              branch-a
                              branch-b-resized
                              :b
                              header-y-resized])
          "4) Resizing nodes: O(1) (worst-case O(n)), num resized-nodes & ancestors + num visible-nodes & ancestors.
              Since nodes contain their children, changing the value of a child also changes the identity
              of all its ancestors, causing them to miss the cache. This causes the traversal to descend
              as deeply as the resized header-spec (i.e. header-y-resized).
              This descent is necessary, since its size, and all its ancestors' total-sizes, have changed
              and must be recalculated."))))
