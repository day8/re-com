(ns re-com.nested-v-grid.theme
  (:require
   [re-com.theme.default :as default :refer [base main]]
   [re-com.nested-v-grid :as-alias nvg]
   [re-com.theme.util :refer [merge-props merge-class]]))

(def border-light "thin solid #ccc")
(def border-dark "thin solid #aaa")

(defn style [props & ms] (apply update props :style merge ms))

(defn class [props & ss] (apply update props :class merge-class ss))

(defmethod base ::nvg/wrapper [props _]
  (style props {:height   300
                :width    500
                :overflow :auto
                :flex     "0 0 auto"
                :display  :grid}))

(defmethod base ::nvg/cell-grid
  [props _]
  (class props "rc-nested-v-grid-cell-grid"))

(defmethod main ::nvg/cell-grid
  [props _]
  (style props {#_#_:border-top  light-border
                #_#_:border-left light-border}))

(defmethod base ::nvg/column-header-grid
  [props _]
  (-> props
      (class "rc-nested-v-grid-column-header-grid")
      (style {:display  :grid
              :position :sticky
              :top      0})))

(defmethod base ::nvg/row-header-grid
  [props _]
  (-> props
      (class "rc-nested-v-grid-row-header-grid")
      (style {:display  :grid
              :position :sticky
              :left      0})))

(defmethod base ::nvg/corner-header-grid
  [props _]
  (style props {:position          :sticky
                :display           :grid
                :grid-row-start    1
                :grid-column-start 1
                :left              0
                :top               0}))

(def header-main
  (let [{:keys [sm-3 sm-4]}               default/golden-section-50
        {:keys [light-background]} default/colors]
    {:padding-top      sm-3
     :padding-right    sm-4
     :padding-left     sm-4
     :background-color light-background
     :color            "#666"
     :font-size        "13px"}))

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
  [props {:keys [state]}]
  (let [{:keys [align-column align-column-header align]} (:header-spec state)]
    (style props header-main
           {:text-align (or align-column-header align-column align :center)})))

(def row-header-main
  (let [{:keys [sm-3 sm-6]}               default/golden-section-50
        {:keys [border light-background]} default/colors]
    {:padding-top      sm-3
     :padding-right    sm-3
     :padding-left     sm-6
     :background-color light-background
     :color            "#666"
     :text-align       "left"
     :font-size        "13px"
     :white-space      "nowrap"}))

(defmethod main ::nvg/row-header
  [props {{:keys [edge]} :state}]
  (style props row-header-main
         {:border-right border-dark}))

(def cell-wrapper-main
  (let [{:keys [sm-3]} default/golden-section-50]
    {:font-size        12
     :background-color "white"
     :color            "#777"
     :padding-top      sm-3
     :padding-right    sm-3
     :padding-left     sm-3
     :text-align :right
     :border-right border-light
     :border-bottom border-light}))

(defmethod main ::nvg/cell-wrapper
  [props {{:keys [edge value column-path]} :state}]
  (let [align (some :align column-path)]
    (update props :style merge
            cell-wrapper-main
            #_#_(cond align
                      {:text-align align}
                      (string? value)
                      {:text-align :left})
              (when (seq edge)
                {:border-right  (cond
                                  (contains? edge :column-section-right)
                                  "thin solid #aaa"
                                  (contains? edge :right)
                                  "thin solid #aaa"
                                  :else
                                  light-border)
                 :border-bottom (if (contains? edge :bottom)
                                  "thin solid #aaa"
                                  light-border)}))))
