(ns re-com.nested-v-grid.parts
  (:require
   [reagent.core :as r]
   [re-com.nested-v-grid.util :as ngu]))

(defn box-style [position]
  (case position
    :top    {:top    -2 :left  0  :height 5 :width  "100%"}
    :bottom {:bottom -3 :left  0  :height 5 :width  "100%"}
    :left   {:top    0  :left  -2 :width  5 :height "100%"}
    :right  {:top    0  :right -3 :width  5 :height "100%"}))

(defn header-label [{:keys [path]}]
  (let [spec (peek path)]
    (or (:label spec)
        (some-> spec :id str)
        (some-> spec str))))

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

(defn drag-overlay [{:keys [x-start y-start on-mouse-move on-mouse-up size-dimension]}]
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
                           :cursor               (case size-dimension :width :col-resize :height :row-resize :grab)
                           #_#_:background-color "rgba(255,0,0,0.4)"}}]))

(defn resizer [{:keys [path keypath size offset overlay on-resize index header-dimension size-dimension style]}]
  [:div {:class "rc-nested-v-grid-resizer"
         :style (merge
                 {:position :relative}
                 (case size-dimension
                   :width {:grid-row-start 1
                           :grid-row-end   -1
                           :width          0
                           :margin-left    (+ size offset)}
                   :height {:grid-column-start 1
                            :grid-column-end   -1
                            :height            0
                            :margin-top        (+ size offset)})
                 (case [header-dimension size-dimension]
                   [:column :width]  {:grid-column-start (ngu/path->grid-line-name path)}
                   [:column :height] {:grid-row-start (inc index)}
                   [:row :height]    {:grid-row-start (ngu/path->grid-line-name path)}
                   [:row :width]     {:grid-column-start (inc index)})
                 style)}
   [grid-line-button
    {:position      (case size-dimension :width :right :height :bottom)
     :on-mouse-down (fn [e]
                      (reset! overlay [drag-overlay
                                       {:x-start       (.-clientX e)
                                        :y-start       (.-clientY e)
                                        :header-dimension header-dimension
                                        :size-dimension :size-dimension
                                        :on-mouse-up   #(reset! overlay nil)
                                        :on-mouse-move (fn [{:keys [dx dy]}]
                                                         (on-resize {:header-dimension header-dimension
                                                                     :size-dimension   size-dimension
                                                                     :keypath   keypath
                                                                     :size      (-> (case size-dimension :width dx :height dy)
                                                                                    (+ size)
                                                                                    (max 10))}))}]))}]])

