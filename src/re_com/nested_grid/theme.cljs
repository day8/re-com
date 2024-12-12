(ns re-com.nested-grid.theme
  (:require
   [re-com.theme.default :as default :refer [base main style]]
   [re-com.theme.util :refer [merge-props merge-class]]
   [re-com.nested-grid :as-alias ng]))

(defmethod base ::ng/wrapper [props _]
  (style props {:height   300
                :width    500
                :overflow :auto
                :flex     "0 0 auto"
                :display  :grid}))

(defmethod base ::ng/cell-grid
  [props _]
  (default/class props "rc-nested-v-grid-cell-grid"))

(defmethod main ::ng/cell-grid
  [props _]
  (style props {:border-top  "thin solid #ccc"
                :border-left "thin solid #ccc"}))

(defmethod base ::ng/column-header-grid
  [props _]
  (-> props
      (default/class "rc-nested-v-grid-row-header-grid")
      (style {:display  :grid
              :position :sticky
              :top      0})))

(defmethod base ::ng/row-header-grid
  [props _]
  (-> props
      (default/class "rc-nested-v-grid-row-header-grid")
      (style {:display  :grid
              :position :sticky
              :left      0})))

(defmethod base ::ng/column-header
  [props _]
  (update props :style merge
          {:user-select "none"
           :width       "100%"
           :height      "100%"}))

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

(defmethod main ::ng/row-header
  [props {{:keys [edge]} :state}]
  (style props row-header-main
         #_(when (contains? edge :right)
             {:border-right "thin solid #aaa"})
         #_(when (contains? edge :left)
             {:border-left "thin solid #aaa"})
         #_(when (contains? edge :bottom)
             {:border-bottom "thin solid #aaa"})))

(def column-header-main
  (let [{:keys [sm-3 sm-4]}               default/golden-section-50
        {:keys [border light-background]} default/colors]
    {:padding-top      sm-3
     :padding-right    sm-4
     :padding-left     sm-4
     :background-color light-background
     :color            "#666"
     :font-size        "13px"
     :border-left      "thin solid #ccc"
     :border-top       "thin solid #ccc"}))

(defmethod main ::ng/column-header
  [props {:keys [state]}]
  (let [{:keys [align-column align-column-header align]} (:header-spec state)]
    (style props column-header-main
           {:text-align (or align-column-header align-column align :center)})))

(def cell-wrapper-main
  (let [{:keys [sm-3]} default/golden-section-50]
    {:font-size        12
     :background-color "white"
     :color            "#777"
     :padding-top      sm-3
     :padding-right    sm-3
     :padding-left     sm-3
     :text-align :right
     :border-right "thin solid #ccc"
     :border-bottom "thin solid #ccc"}))

(defmethod main ::ng/cell-wrapper
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
                                  "thin solid #ccc")
                 :border-bottom (if (contains? edge :bottom)
                                  "thin solid #aaa"
                                  "thin solid #ccc")}))))
