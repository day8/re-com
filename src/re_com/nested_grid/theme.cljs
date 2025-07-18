(ns re-com.nested-grid.theme
  (:require
   [re-com.theme.default :as default :refer [base main]]
   [re-com.nested-grid :as-alias ng]))

;; NOTE: See re-com.css for styling of cells & headers

(def border-light "thin solid #ccc")
(def border-dark "thin solid #aaa")

(defn style [props & ms] (apply update props :style merge ms))

(defmethod base ::ng/wrapper [{:keys [sticky-child?] :as props}]
  (style props {:overflow (when-not sticky-child? :auto)
                :flex     "0 0 auto"
                :display  :grid}))

(defmethod base ::ng/cell-grid
  [props]
  (style props {:display           :grid
                :overflow          :hidden
                :grid-row-start    2
                :grid-column-start 2}))

(defmethod base ::ng/column-header-grid
  [{:keys [sticky-top] :or {sticky-top 0} :as props}]
  (style props {:display  :grid
                :overflow :hidden
                :position :sticky
                :top      sticky-top}))

(defmethod base ::ng/row-header-grid
  [{:keys [sticky-left] :or {sticky-left 0} :as props}]
  (style props {:display  :grid
                :position :sticky
                :left     sticky-left}))

(defmethod base ::ng/corner-header-grid
  [{:keys [sticky-left sticky-top]
    :or   {sticky-left 0
           sticky-top  0}
    :as   props}]
  (style props {:position          :sticky
                :display           :grid
                :grid-row-start    1
                :grid-column-start 1
                :left              sticky-left
                :top               sticky-top}))

(defmethod main ::ng/corner-header
  [{:keys [edge] :as props}]
  (style props {}
         (when (edge :top) {:border-top border-light})
         (when (edge :right) {:border-right border-light})
         (when (edge :bottom) {:border-bottom border-light})
         (when (edge :left) {:border-left border-light})))
