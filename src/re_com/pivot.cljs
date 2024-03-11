(ns re-com.pivot
  (:require
   [clojure.string :as str]
   [re-com.box :refer [h-box v-box box gap]]
   [re-com.v-table :as v-table :refer [v-table]]
   [re-com.util :as u :refer [px]]
   [reagent.core :as r]
   [re-com.theme :as theme]))

(def scroll-pos (r/atom 0))

(defn descendant? [path-a path-b]
  (and (not= path-a path-b)
       (= path-a (vec (take (count path-a) path-b)))))

(def header? (complement (some-fn vector? seq?)))

(defn spec->headers ;; TODO simplify
  ([data] (spec->headers [] data))
  ([path data]
   (let [pairs
         (->> data
              (into [::stub]) ;; make sure 1st partition is always headers
              (partition-by header?)
              (partition-all 2))]
     (vec
      (apply concat
             (for [[headers branches] pairs
                   :let              [headers (remove #{::stub} headers)
                                      branch-path (into path (take-last 1 headers))]]
               (concat
                (map (partial conj path) headers)
                (mapcat (partial spec->headers branch-path) branches))))))))

(assert (= (spec->headers [:a :b :c])
           [[:a] [:b] [:c]]))
(assert (= (spec->headers [[:a [:b] [:c]]])
           [[:a] [:a :b] [:a :c]]))
(assert (= (spec->headers [[:a :b [:c]]])
           [[:a] [:b] [:b :c]]))

(defn header-cross-span [group-path all-paths]
  (->> all-paths
       (filter (partial descendant? group-path))
       count
       inc))

(defn header-main-span [group-path all-paths]
  (->> all-paths (map count) (apply max) (+ (- (count group-path))) inc))

(def widths (r/atom {[:z] 100}))
(def heights (r/atom {[{:product-category :fruit :label "fruit"} :banana] 80}))

(def default-width 40)
(def default-height 30)

(defn column-width  [path] (get (last path) :width default-width))
(defn column-height [path] (get (last path) :height default-height))
(defn row-width     [path] (get (last path) :width default-width))
(defn row-height    [path] (get (last path) :height default-height))

(defn column-label [{:keys [path leaf?]}]
  [h-box
   :children
   [[:div {:style {:height            (px (column-height path))
                   :width             (when leaf? (px (column-width path)))
                   :text-align        "center"}}
     (let [column (last path)]
       (or (:label column) (name column)))]]])

(defn row-label [path]
  [v-box
   :children
   [[:div {:style {:height     (row-height path)
                   :width      (row-width path)
                   :text-align "center"}}
     (let [row (last path)]
       (or (:label row) (name row)))]]])

(defn row-headers [& {:keys [rows level path]
                      :or   {path [] level 1}
                      :as   props}]
  (let [row-props   (spec->headers rows)
        level->rows (group-by count row-props)
        this-level  (->> (get level->rows level)
                         (filter (partial descendant? path)))
        parent-gap  (when (and (seq path)
                               (seq this-level))
                      [:div {:style {:height (px (row-height path))}}])
        h-gap       [:div {:style {:width (px (row-width path))}}]]
    [v-box
     :style {:outline        "1px solid blue"
             :outline-offset "-1px"}
     :children
     (into
      []
      (for [r this-level]
        [v-box
         :children
         [(when (seq this-level)
            [row-label r])
        [h-box
         :children
           [h-gap
          [v-box

           :children
           [[row-headers (merge props {:level (inc level)
                                         :path  (conj path (last r))})]]]]]]]))]))

(defn col-headers [& {:keys [columns level path]
                      :or   {path []
                             level 1}
                      :as   props}]
  (let [column-props (spec->headers columns)
        level->cols  (group-by count column-props)
        this-level   (->> (get level->cols level)
                          (filter (partial descendant? path)))
        parent-gap   [:div
                        {:style
                       {:width (px (column-width path))}}]]
    [h-box
     :children
     (into
      []
      (for [c this-level
            :let [c (cond-> c)
                  descendants (filter (partial descendant? c) column-props)]]
        [:div
         {:style {:position       "relative"
                  :outline        "1px solid green"
                  :outline-offset "-1px"}}
         [:div.hihihi {:style {:max-width "100%"}}
          [column-label {:path c}]]
                     [h-box
                      :children
          [(when (seq this-level)
             parent-gap)
           [col-headers (merge props {:level (inc level)
                                      :path  (conj path (last c))})]]]]))]))

(defn cell-renderer [{:keys [row-path column-path]}]
  (let [col (last column-path)]
    (or (:label col) (name col))))

(defn cell-wrapper [{:keys [row-path column-path cell] :as props}]
  [:div {:style {:outline        "1px solid red"
                 :outline-offset "-1px"
                 :width (px (column-width column-path))
                 :height (px (row-height row-path))}}
   [(or cell cell-renderer) props]])

(defn table [& {:keys [columns rows cell] :as props}]
  (let [column-paths (spec->headers columns)
        row-paths    (spec->headers rows)
        corner-gap [gap :size (px (->> column-paths
                                       (sort-by count)
                                       (partition-by count)
                                       (map (fn [paths]
                                              (apply max
                                                     (map column-height paths))))
                                       (reduce +)))]]
    [h-box
     :children
     [[v-box
       :children
       [corner-gap
        [row-headers props]]]
    [v-box
     :children
     [[col-headers props]
      [h-box
       :children
         (for [c column-paths
               :let [c (cond-> c (map? c) (do))]]
         [v-box
          :children
            (doall (for [r row-paths]
                     [cell-wrapper {:row-path r
                                    :column-path c
                                    :cell cell}]))])]]]
      [v-table/scrollbar
       :type :horizontal
       :length 300
       :width 50
       :content-length 500
       :scroll-pos @scroll-pos
       :on-change #(reset! scroll-pos %)]]]))

(defn drag-button [& {:as args}]
  (let [dragging?    (r/atom false)
        mouse-down-x (r/atom 0)
        last-drag-x  (r/atom 0)
        drag-x       (r/atom 0)
        hovering?    (r/atom nil)]
    (fn [& {:keys [on-resize column-index path]}]
      [:<>
       [:div {:on-mouse-enter #(reset! hovering? true)
                   :on-mouse-leave #(reset! hovering? false)
                   :on-mouse-down #(do (reset! dragging?    true)
                                       (reset! mouse-down-x (.-clientX %))
                                       (reset! drag-x       (.-clientX %))
                                       (reset! last-drag-x       (.-clientX %)))
                   :style         {:position         "absolute"
                                   :opacity          (if (or @hovering? @dragging?) 1 0)
                                   :top              0
                                   :right            0
                                   :height           "100%"
                                   :width            "25px"
                                   :background-color "blue"}}]
       (when @dragging?
         [:div {:on-mouse-up   #(do (reset! dragging? false)
                                    (reset! hovering? true))
                :on-mouse-move #(do (.preventDefault %)
                                    (let [x (.-clientX %)]
                                      (reset! drag-x x)
                                      (when on-resize
                                        (on-resize {:distance (- x @last-drag-x)
                                                    :path     path}))
                                      (reset! last-drag-x x)))
                :style         {:position         "fixed"
                                :z-index          99999
                                :width            "100%"
                                :height           "100%"
                                :top              0
                                :left             0
                                :font-size        100}}])])))

(defn path->grid-line-name [path]
  (str "line__" (hash path) "-start"))

(defn grid-template [tokens]
  (let [rf (fn [s group]
             (str s " "
                  (if (number? (first group))
                    (str/join " " (map px group))
                    (str "[" (str/join " " (map path->grid-line-name group)) "]"))))]
    (->> tokens
         (partition-by number?)
         (reduce rf ""))))

(defn cell-part [{:keys [column-path row-path]}]
  nil)

(defn cell-wrapper-part [{:keys [column-path row-path cell]
                  :as args}]
  [:div {:style {:grid-column (path->grid-line-name column-path)
                 :grid-row (path->grid-line-name row-path)
                 :background-color "#fff"
                   :padding "10px"
                   :position "relative"}}
   [u/part cell args cell-part]])

(defn column-header-part [{:keys [path]}]
  (str (get (last path) :id (last path))))

(theme/apply {} {:part ::column-header-wrapper} [])

(defn column-header-wrapper-part [{:keys [column-header path column-paths on-resize show-branch-cells? leaf? theme] :as props}]
  (let [hide? (and (not leaf?) (not show-branch-cells?))]
    [:div
     (-> {:style {:grid-column-start (path->grid-line-name path)
                   :grid-column-end   (str "span " (cond-> path
                                                     :do   (header-cross-span column-paths)
                                                     hide? dec))
                  :grid-row-start    (count path)}}
         (theme/apply {:state {} :part ::column-header-wrapper} theme))
   [u/part column-header props column-header-part]
     [drag-button {:on-resize on-resize :path path}]]))

(defn row-header-part [{:keys [path row-paths row-header show-branch-cells? leaf?] :as props}]
  (let [hide? (and (not leaf?) (not show-branch-cells?))]
  [:div {:style {:grid-row-start    (path->grid-line-name path)
                 :grid-column-start (count path)
                   :grid-column-end   (str "span " (cond-> path
                                                     :do   (header-main-span row-paths)
                                                     hide? dec))
                   :padding           "10px"}}
     [u/part row-header props column-header-part]]))

(def level count)

(defn grid [& {:as _args}]
  (let [column-state       (r/atom {})
        row-state          (r/atom {})
        column-header-prop (fn [path k & [default]]
                             (or (some-> @column-state (get path) (get k))
                                 (get (meta (last path)) k)
                                 (get (last path) k)
                                 default))
        row-header-prop    (fn [path k & [default]]
                             (or (some-> @row-state (get path) (get k))
                                 (get (meta (last path)) k)
                                 (get (last path) k)
                                 default))
        max-props          (fn [k default paths]
                             (->> paths
                                  (group-by level)
                                  (sort-by key)
                                  (map val)
                                  (map (fn [paths] (apply max (map #(column-header-prop % k default) paths))))))]
    (fn [& {:keys [columns rows cell
                   cell-wrapper column-header-wrapper column-header row-header
                   show-branch-cells?
                   max-height column-width column-height row-width row-height]
            :or   {column-height      30
                   column-width       60
                   row-width          100
                   row-height         30
                   show-branch-cells? false}}]
      (let [on-resize-cell        (fn [{:keys [distance path]}]
                                    (swap! column-state update-in [path :width]
                                           #(+ distance (or % (column-header-prop path :width column-width)))))
            column-paths          (spec->headers columns)
            column-leaf-paths     (set (reduce (fn [paths p] (remove #(descendant? % p) paths)) column-paths column-paths))
            leaf-column?          column-leaf-paths
            column-widths         (map #(column-header-prop % :width column-width) column-paths)
            max-column-heights    (max-props :height column-height column-paths)
            row-paths             (spec->headers rows)
            row-leaf-paths        (set (reduce (fn [paths p] (remove #(descendant? % p) paths)) row-paths row-paths))
            leaf-row?             row-leaf-paths
            row-heights           (map #(column-header-prop % :height row-height) row-paths)
            max-row-widths        (max-props :width row-width row-paths)
            grid-template-columns (->> (mapcat
                                        (fn [path width]
                                          (cond-> [path]
                                            (or show-branch-cells?
                                                (leaf-column? path)) (conj width)))
                                        column-paths column-widths)
                                       (concat max-row-widths)
                                       grid-template)
            grid-template-rows    (->> (mapcat
                                        (fn [path height]
                                          (cond-> [path]
                                            (or show-branch-cells?
                                                (row-leaf-paths path)) (conj height)))
                                        row-paths row-heights)
                                       (concat max-column-heights)
                                       grid-template)]
        [:div {:style {:padding               "0px"
                       :max-height            max-height
                       :display "grid"
                       :overflow              "auto"
                       :grid-template-columns grid-template-columns
                       :grid-template-rows    grid-template-rows
                       :gap                   "0px"
                       :background-color      "transparent"}}
         (for [path column-paths
               :let [props {:path          path
                            :column-paths  column-paths
                            :on-resize     on-resize-cell
                            :column-header column-header
                            :show-branch-cells? show-branch-cells?
                            :leaf?              (column-leaf-paths path)}]]
           ^{:key [::column (or path (gensym))]}
           [u/part column-header-wrapper props column-header-wrapper-part])
         (for [path row-paths
               :let [props {:path               path
                            :row-paths          row-paths
                            :show-branch-cells? show-branch-cells?
                            :leaf?              (leaf-row? path)}]]
           ^{:key [::row (or path (gensym))]}
           [u/part row-header props row-header-part])
         (for [column-path column-paths
               row-path    row-paths
               :let        [props {:column-path column-path
             :row-path    row-path
                                   :cell        cell}]]
           ^{:key [::cell (or [column-path row-path] (gensym))]}
           [u/part cell-wrapper props cell-wrapper-part])]))))
