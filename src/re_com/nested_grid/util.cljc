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
                 num-above total-above items-above)
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

;; iff a child intersects the window
;; window-end must be > branch start

;; when done traversing the branch:
;; if the whole branch intersected
    ;; should have collected the own-leaf
;; else if nothing intersected
    ;; should not have collected the own-leaf

;; always collect the own-leaf
;; when done traversing the branch:
;; if the whole branch intersected (either own-leaf or a child)
    ;; cool
;; else if nothing intersected
    ;; pop

(walk-size {:window-start 372
            :window-end 472
            :tree test-tree
            :size-cache (volatile! {})})

(defn node->div [node {:keys [traversal path] :or {path []}}]
  (let [style {:border-top           "thin solid black"
               :border-left          "thin solid black"
               :margin-left          50
               :background-color :lightgreen}]
    (cond
      (leaf? node)   (let [leaf-path (conj path node)]
                       [:div {:style (merge style
                                            {:height     (leaf-size node)
                                             :background (if (contains? (set (some-> traversal deref :windowed-nodes)) leaf-path)
                                                           :lightgreen
                                                           :white)})}
                        (str leaf-path)])
      (branch? node) (let [[own-node & children] node
                           this-path (conj path (first node))]
                       (into [:div {:style (merge style
                                                  {:position :relative
                                                   :height       :fit-content
                                                   :background (if (contains? (set (some-> traversal deref :windowed-nodes)) this-path)
                                                                 :lightgreen
                                                                 :white)})}
                              [:div {:style {:height (leaf-size own-node)}}
                               (str this-path)]]
                             (map #(do [node->div % {:traversal traversal :path (conj path own-node)}])
                                  children))))))

[:div {:style {:border "thin solid black",
               :background-color :lightgreen,
               :padding-left 20,
               :height :fit-content}}
 ":a"
 [:div {:style {:border "thin solid black", :background-color :lightgreen, :padding-left 20, :height :fit-content}} ":b" [:div {:style {:border "thin solid black", :background-color :lightgreen, :height 100}} "100"] [:div {:style {:border "thin solid black", :background-color :lightgreen, :height 200}} "200"] [:div {:style {:border "thin solid black", :background-color :lightgreen, :height 300}} "300"]]
 [:div {:style {:border "thin solid black", :background-color :lightgreen, :padding-left 20, :height :fit-content}} ":c" [:div {:style {:border "thin solid black", :background-color :lightgreen, :height 200}} "200"]]
 [:div {:style {:border "thin solid black", :background-color :lightgreen, :height 20}} ":d"]
 [:div {:style {:border "thin solid black", :background-color :lightgreen, :height 20}} ":e"]]

#?(:cljs
   (defn test-component []
     (let [container-ref      (r/atom nil)
           set-container-ref! (partial reset! container-ref)
           scroll-top         (r/atom nil)
           scroll-left        (r/atom nil)
           on-scroll!         #(do (reset! scroll-top (.-scrollTop (.-target %)))
                                   (reset! scroll-left (.-scrollLeft (.-target %))))
           window-size        100
           window-ratio       0.5
           window-start       (r/reaction (* 2 @scroll-top))
           window-end         (r/reaction (+ window-size (* 2 @scroll-top)))
           height-cache       (volatile! {})
           path-seq           (def node-seq (:windowed-nodes (walk-size {:window-start 0
                                                                         :window-end   999999
                                                                         :tree         test-tree
                                                                         :size-cache   (volatile! {})})))
           {:keys [sum-size]} (walk-size {:window-start 0
                                          :window-end   100000
                                          :tree         test-tree
                                          :size-cache   (volatile! {})})
           traversal          (r/reaction (walk-size {:window-start @window-start
                                                      :window-end   @window-end
                                                      :tree         test-tree
                                                      :size-cache   height-cache}))]
       (r/create-class
        {:component-did-mount
         (fn [_] (.addEventListener @container-ref "scroll" on-scroll!))
         :reagent-render (fn []
                           [:<>
                            [:div {:ref   set-container-ref!
                                   :style {:width      400
                                           :overflow-y :auto
                                           :position   :relative
                                           :height     sum-size}}
                             [:div {:style {:position           :fixed
                                            :margin-left        20
                                            :display            :grid
                                            :grid-template-rows (str/join " " (->> path-seq
                                                                                   (map last)
                                                                                   (map leaf-size)
                                                                                   (map u/px)))}}
                              (node->div test-tree {:traversal traversal})]
                             [:div {:style {:position   :fixed
                                            :height     window-size
                                            :width      220
                                            :margin-left "70px"
                                            :border-top "thick solid red"
                                            :border-bottom "thick solid red"
                                            :margin-top (* 2 @scroll-top)
                                            :background "rgba(0,0,1,0.2)"}}
                              (str (* 2 @scroll-top))]
                             [:div {:style {:width      400
                                            :height     (* sum-size (+ 1 window-ratio))}}
                              (str sum-size)]]
                            [:br]
                            [:pre
                             (str @traversal)]])}))))
