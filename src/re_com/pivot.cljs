(ns re-com.pivot
  (:require
   [re-com.box :refer [h-box v-box box gap]]
   [re-com.v-table :as v-table :refer [v-table]]
   [re-com.util :refer [px]]
   [reagent.core :as r]))

(def scroll-pos (r/atom 0))

(def child? vector?)

(def parent? (some-fn keyword? map?))

(defn descendant? [v1 v2]
  (= v1 (vec (take (count v1) v2))))

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

(spec->headers [:x :z [:a :b]])

(->> [:a [:b :c]]
     spec->headers
     (group-by count))

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

(defn grid-cell [{:keys [on-resize
                         column-index
                         row-index
                         column-path
                         row-path]}]
  [:div {:style {:background-color "#fff"
                   :padding "10px"
                   :position "relative"}}
     (str (rand 10))
     [drag-button {:on-resize on-resize :column-index column-index}]])

(defn grid [& {:keys [columns rows]}]
  (let [column-count (r/atom (count (spec->headers columns)))
        widths      (r/atom (vec (repeat @column-count 30)))
        on-resize-cell (fn [{:keys [distance column-index]}]
                         (swap! widths update column-index + distance))]
    (fn [& {:keys [columns rows]}]
      (let [column-paths (spec->headers columns)
            row-paths    (spec->headers rows)]
        [:div {:style {:padding "2px"
                       :display "grid"
                       :grid-template-columns (->> @widths (map #(str % "px ")) (apply str))
                       :gap "2px"
                       :background-color "red"}}
         (for [row-index    (range (count row-paths))
               column-index (range (count column-paths))
               :let [c (nth column-paths column-index)
                     r (nth row-paths row-index)]]
           ^{:key [column-index row-index]}
           [grid-cell
            {:column-path c
             :column-index column-index
             :row-index row-index
             :row-path r
             :on-resize on-resize-cell}])]))))
