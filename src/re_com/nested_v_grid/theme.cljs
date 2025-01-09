(ns re-com.nested-v-grid.theme
  (:require
   [re-com.theme.default :as default :refer [base main]]
   [re-com.nested-v-grid :as-alias nvg]))

(def border-light "thin solid #ccc")
(def border-dark "thin solid #aaa")

(defn style [props & ms] (apply update props :style merge ms))

(defmethod base ::nvg/wrapper [props _]
  (style props {:height   300
                :width    500
                :overflow :auto
                :flex     "0 0 auto"
                :display  :grid}))

(defmethod base ::nvg/cell-grid
  [props _]
  (style props {:display           :grid
                :grid-row-start    2
                :grid-column-start 2}))

(defmethod base ::nvg/column-header-grid
  [props _]
  (style props {:display  :grid
                :position :sticky
                :top      0}))

(defmethod base ::nvg/row-header-grid
  [props _]
  (style props {:display  :grid
                :position :sticky
                :left      0}))

(defmethod base ::nvg/corner-header-grid
  [props _]
  (style props {:position          :sticky
                :display           :grid
                :grid-row-start    1
                :grid-column-start 1
                :left              0
                :top               0}))

(def header-main
  (let [{:keys [sm-3 sm-4]}        default/golden-section-50
        {:keys [light-background]} default/colors]
    {:padding-top      sm-3
     :padding-right    sm-4
     :padding-left     sm-4
     :background-color light-background
     :color            "#666"
     :font-size        "13px"
     :text-overflow    :ellipsis
     :overflow         :hidden
     :white-space      :nowrap}))

(defmethod main ::nvg/corner-header
  [{:keys [edge] :as props} _]
  (style props header-main
         (when (edge :top) {:border-top border-light})
         (when (edge :right) {:border-right border-light})
         (when (edge :bottom) {:border-bottom border-light})
         (when (edge :left) {:border-left border-light})))

(defmethod base ::nvg/column-header
  [props _]
  (update props :style merge
          {:user-select   "none"
           :width         "100%"
           :height        "100%"
           :border-bottom border-dark}))

(defmethod main ::nvg/column-header
  [{:keys [column-path] :as props} _]
  (let [{:keys [align-column align-column-header align]} (peek column-path)]
    (style props header-main
           {:text-align (or align-column-header align-column align :center)})))

(defmethod main ::nvg/row-header
  [props _]
  (style props header-main
         {:border-right border-dark
          :font-size    "13px"
          :text-align   "left"}))

(def cell-main
  (let [{:keys [sm-3]} default/golden-section-50]
    {:font-size        12
     :background-color "white"
     :color            "#777"
     :padding-top      sm-3
     :padding-right    sm-3
     :padding-left     sm-3
     :text-align       :right
     :border-right     border-light
     :border-bottom    border-light}))

(defmethod main ::nvg/cell
  [props {{:keys [edge value column-path]} :state}]
  (let [align (some :align (reverse column-path))]
    (update props :style merge cell-main)))
