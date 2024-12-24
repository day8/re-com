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

(defn grid-line-button [_]
  (let [hover? (r/atom nil)]
    (fn [{:keys [on-mouse-down position style]}]
      [:div {:style         (merge {:position   :absolute
                                    :cursor     :grab
                                    :background :orange #_"rgba(0,0,0,0.1)"
                                    :box-shadow "0 0 4px rgba(0,0,0,0.1)"
                                    :opacity    (if @hover? 1 0.5)}
                                   (box-style position)
                                   style)
             :on-mouse-down on-mouse-down
             :on-mouse-over #(reset! hover? true)
             :on-mouse-out  #(reset! hover? false)}])))

(defn drag-overlay [{:keys [x-start y-start on-mouse-move on-mouse-up]}]
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
                           :cursor               :grab
                           #_#_:background-color "rgba(255,0,0,0.4)"}}]))
