(ns re-com.pivot
  (:require
   [clojure.string :as str]
   [re-com.box :refer [h-box v-box box gap]]
   [re-com.v-table :as v-table :refer [v-table]]
   [re-com.util :as u :refer [px]]
   [reagent.core :as r]))

(def scroll-pos (r/atom 0))

(def child? (some-fn vector? seq?))

(def parent? (complement child?))

(defn descendant? [v1 v2]
  (and (not= v1 v2)
       (= v1 (vec (take (count v1) v2)))))

(defn spec->headers
  ([item]
   (spec->headers [] [] item))
  ([path acc item]
   (cond
     (parent? item)
     (conj acc (conj path item))
     (child? item)
     (let [parent (last (last acc))
           new-path (cond-> path parent (conj parent))]
       (into acc (reduce (partial spec->headers new-path) [] item))))))

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
    (fn [& {:keys [on-resize column-index]}]
      [:<>
       [:div.butt {:on-mouse-enter #(reset! hovering? true)
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
                                                    :column-index column-index}))
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

(def path? vector?)

(defn grid-template [tokens]
  (let [serialize (fn [token] (cond (number? token) (str token "px")
                                    (path? token) (str "[" (path->grid-line-name token) "]")))]
    (->> tokens
         (map serialize)
         (str/join " "))))

(defn default-cell-fn [{:keys [column-path row-path]}]
  (str column-path))

(defn grid-cell [{:keys [on-resize
                         column-index
                         row-index
                         column-path
                         row-path
                         cell-fn]
                  :as args}]
  [:div {:style {:grid-column (path->grid-line-name column-path)
                 :grid-row (path->grid-line-name row-path)
                 :background-color "#fff"
                   :padding "10px"
                   :position "relative"}}
   [u/part cell-fn args default-cell-fn]
     [drag-button {:on-resize on-resize :column-index column-index}]])

(defn column-header [{:keys [path column-paths]}]
  [:div {:style {:grid-column-start (path->grid-line-name path)
                 :grid-column-end   (str "span " (header-cross-span path column-paths))
                 :grid-row-start    (count path)
                 :padding           "10px"
                 :background-color  "black"}}
   (str path)])

(defn row-header [{:keys [path row-paths]}]
  [:div {:style {:grid-row-start    (path->grid-line-name path)
                 :grid-column-start (count path)
                 :grid-column-end   (str "span " (header-main-span path row-paths))
                 :padding           "10px"
                 :background-color  "black"}}
   (str path)])

(defn grid [& {:keys [columns rows hide-columns]}]
  (let [ainit-cols      (spec->headers columns)
        init-rows (spec->headers rows)
        hide-columns   (r/atom #{["Total TV"]})
        col-widths         (r/atom (vec (repeat (count init-cols) 100)))
        col-heights    (r/atom (vec (repeat (->> init-cols (map count) (apply max)) 50)))
        row-widths     (r/atom (vec (repeat (->> init-rows (map count) (apply max)) 60)))
        row-heights    (r/atom (vec (repeat (count init-rows) 50)))
        on-resize-cell (fn [{:keys [distance column-index]}]
                         (swap! col-widths update column-index + distance))]
    (fn [& {:keys [columns rows cell-fn max-height]}]
      (let [column-paths (spec->headers columns)
            row-paths             (spec->headers rows)
            grid-template-columns (->> @col-widths
                                       (interleave column-paths)
                                       (into @row-widths)
                                       grid-template)
            grid-template-rows (->> @row-heights
                                       (interleave row-paths)
                                       (into @col-heights)
                                       grid-template)]
        [:div {:style {:padding "2px"
                       :width                 "fit-content"
                       :max-height            max-height
                       :display "grid"
                       :overflow              "auto"
                       :grid-template-columns grid-template-columns
                       :grid-template-rows    grid-template-rows
                       :gap "2px"
                       :background-color      "lightgrey"}}
         (for [path column-paths]
           ^{:key (or path (gensym))}
           [column-header {:path path :column-paths column-paths}])
         (for [path row-paths]
           ^{:key (or path (gensym))}
           [row-header {:path path :row-paths row-paths}])
         (for [row-index    (range (count row-paths))
               column-index (range (count column-paths))
               :let         [c-path (nth column-paths column-index)
                             r-path (nth row-paths row-index)]]
           ^{:key (or [column-index row-index] (gensym))}
           [grid-cell
            {:column-path  c-path
             :column-index column-index
             :row-index row-index
             :row-path     r-path
             :on-resize    on-resize-cell
             :cell-fn      cell-fn}])]))))
