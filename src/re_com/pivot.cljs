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

(defn column-label [path]
  [h-box
   :style {:outline        "1px solid green"
           :outline-offset "-1px"}
   :children
   [[:div {:style {:height            (px (column-height path))
                   :width             (px (column-width path))
                   :text-align        "center"}}
     (let [column (last path)]
       (or (:label column) (name column)))]]])

(defn row-label [path]
  [v-box
   :style {:outline        "1px solid green"
           :outline-offset "-1px"}
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
                      [:div {:style {:height (px (row-height path))}}])]
    [v-box
     :children
     (into
      [parent-gap]
      (for [r this-level]
        [h-box
         :children
         [[row-label r]
          [v-box
           :style {:outline        "1px solid blue"
                   :outline-offset "-1px"}
           :children
           [[row-headers (merge props {:level (inc level)
                                       :path  (conj path (last r))})]]]]]))]))

(defn col-headers [& {:keys [columns level path]
                      :or   {path []
                             level 1}
                      :as   props}]
  (let [column-props (spec->headers columns)
        level->cols  (group-by count column-props)
        this-level   (->> (get level->cols level)
                          (filter (partial descendant? path)))
        parent-gap   (when (and (seq path)
                                (seq this-level))
                       [:div
                        {:style
                         {:width (px (column-width path))}}])]
    [h-box
     :children
     (into
      [parent-gap]
      (for [c this-level
            :let [c (cond-> c)]]
                   [v-box
                    :children
                    [[column-label c]
                     [h-box
                      :children
                      [[col-headers (merge props {:level (inc level)
                                       :path  (conj path (last c))})]]]]]))]))

(defn cell-renderer [{:keys [row-path column-path]}]
  (let [col (last column-path)]
    (or (:label col) (name col))))

(defn cell-wrapper [{:keys [row-path column-path cell] :as props}]
  [:div {:style {:outline        "1px solid red"
                 :outline-offset "-1px"
                 #_#_:border "1px solid red"
                 :width (px (column-width column-path))
                 :height (px (row-height row-path))}}
   [(or cell cell-renderer) props]])

(defn table [& {:keys [columns rows cell] :as props}]
  (let [column-props (spec->headers columns)
        row-props    (spec->headers rows)
        level->cols  (group-by count column-props)
        corner-gap [gap :size (px (->> column-props
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
       (for [c column-props]
         [v-box
          :children
            (doall (for [r row-props]
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
