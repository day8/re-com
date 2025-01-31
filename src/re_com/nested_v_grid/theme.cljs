(ns re-com.nested-v-grid.theme
  (:require
   [re-com.theme.default :as default :refer [base main]]
   [re-com.nested-v-grid :as-alias nvg]))

;; NOTE: See re-com.css for styling of cells & headers

(def border-light "thin solid #ccc")
(def border-dark "thin solid #aaa")

(defn style [props & ms] (apply update props :style merge ms))

(defmethod base ::nvg/wrapper [props]
  (style props {:overflow :auto
                :flex     "1 1 auto"
                :display  :grid}))

(defmethod base ::nvg/cell-grid
  [props]
  (style props {:display           :grid
                :overflow :hidden
                :grid-row-start    2
                :grid-column-start 2}))

(defmethod base ::nvg/column-header-grid
  [props]
  (style props {:display  :grid
                :overflow :hidden
                :position :sticky
                :top      0}))

(defmethod base ::nvg/row-header-grid
  [props]
  (style props {:display  :grid
                :position :sticky
                :left      0}))

(defmethod base ::nvg/corner-header-grid
  [props]
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
     :font-size        13
     :text-overflow    :ellipsis
     :overflow         :hidden
     :white-space      :nowrap}))

(defmethod main ::nvg/corner-header
  [{:keys [edge] :as props}]
  (style props {}
         (when (edge :top) {:border-top border-light})
         (when (edge :right) {:border-right border-light})
         (when (edge :bottom) {:border-bottom border-light})
         (when (edge :left) {:border-left border-light})))
