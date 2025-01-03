(ns re-com.nested-grid.parts
  (:require
   [reagent.core :as r]
   [re-com.nested-grid.util :as ngu]
   [re-com.nested-grid :as-alias ng]))

(defn cell-wrapper [{:keys [style class row-path column-path]}]
  [:div {:style (merge {:grid-row-start    (ngu/path->grid-line-name row-path)
                        :grid-column-start (ngu/path->grid-line-name column-path)}
                       style)
         :class class}
   (str (gensym))])

(def box-style
  {:top    {:top    -2 :left  0  :height 5 :width  "100%"}
   :bottom {:bottom -3 :left  0  :height 5 :width  "100%"}
   :left   {:top    0  :left  -2 :width  5 :height "100%"}
   :right  {:top    0  :right -3 :width  5 :height "100%"}})

(defn header-label [{:keys [path]}]
  (let [spec (peek path)]
    [:span {:title (pr-str (meta path))}
     (or (:label spec)
         (some-> spec :id str)
         (some-> spec pr-str))]))

(def row-header-label header-label)

(def column-header-label header-label)

(defn grid-line-button [_]
  (let [hover? (r/atom nil)]
    (fn [{:keys [on-mouse-down position style]}]
      [:div {:style         (merge {:position   :absolute
                                    :cursor     (case position
                                                  (:left :right) :col-resize
                                                  (:top :bottom) :row-resize)
                                    :background "rgba(0,0,0,0.1)"
                                    :box-shadow "0 0 4px rgba(0,0,0,0.1)"
                                    :opacity    (if @hover? 1 0)}
                                   (box-style position)
                                   style)
             :on-mouse-down on-mouse-down
             :on-mouse-over #(reset! hover? true)
             :on-mouse-out  #(reset! hover? false)}])))

(defn drag-overlay [{:keys [x-start y-start on-mouse-move on-mouse-up dimension]}]
  (fn [_]
    [:div {:on-mouse-up   on-mouse-up
           :on-mouse-move #(let [x (.-clientX %)
                                 y (.-clientY %)]
                             (.preventDefault %)
                             (on-mouse-move
                              {:x       x             :y       y
                               :x-start x-start       :y-start y-start
                               :dx      (- x x-start) :dy      (- y y-start)}))
           :style         {:position             "fixed"
                           :top                  0
                           :left                 0
                           :z-index              2147483647
                           :height               "100%"
                           :width                "100%"
                           :cursor               (case dimension
                                                   (:row-header-width :column-width) :col-resize
                                                   (:column-header-height :row-height) :row-resize :grab)
                           #_#_:background-color "rgba(255,0,0,0.4)"}}]))

(defn resizer [{:keys [path keypath size offset overlay on-resize index dimension style]}]
  (let [resize-dimension (case dimension
                           (:row-header-width :column-width)   :w
                           (:row-height :column-header-height) :h)]
    [:div {:class "rc-nested-v-grid-resizer"
           :style (merge
                   {:position :relative}
                   (case resize-dimension
                     :w {:grid-row-start 1
                         :grid-row-end   -1
                         :width          0
                         :margin-left    (+ size offset)}
                     :h {:grid-column-start 1
                         :grid-column-end   -1
                         :height            0
                         :margin-top        (+ size offset)})
                   (case dimension
                     :column-width         {:grid-column-start (ngu/path->grid-line-name path)}
                     :column-header-height {:grid-row-start (inc index)}
                     :row-height           {:grid-row-start (ngu/path->grid-line-name path)}
                     :row-header-width     {:grid-column-start (inc index)})
                   style)}
     [grid-line-button
      {:position      (case resize-dimension :w :right :h :bottom)
       :on-mouse-down (fn [e]
                        (reset! overlay [drag-overlay
                                         {:x-start       (.-clientX e)
                                          :y-start       (.-clientY e)
                                          :dimension     dimension
                                          :on-mouse-up   #(reset! overlay nil)
                                          :on-mouse-move (fn [{:keys [dx dy]}]
                                                           (on-resize {:dimension dimension
                                                                       :keypath   keypath
                                                                       :size      (-> (case resize-dimension :w dx :h dy)
                                                                                      (+ size)
                                                                                      (max 10))
                                                                       :key       :column-tree}))}]))}]]))
