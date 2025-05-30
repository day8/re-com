(ns re-com.nested-grid.theme
  (:require
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base main golden-section-50 colors]]
   [re-com.nested-grid :as-alias ng]))

(def cell-wrapper-base {#_#_:pointer-events "none"
                        :user-select        "none"
                        :overflow           "hidden"
                        :position           "relative"})

(defmethod base ::ng/cell-wrapper [props]
  (update props :style merge cell-wrapper-base))

(def row-header-wrapper-base {:user-select        "none"
                              :height             "100%"})

(defmethod base ::ng/row-header-wrapper [props]
  (update props :style merge row-header-wrapper-base))

(defmethod base ::ng/column-header-wrapper [props]
  (update props :style merge
          {:user-select "none"
           :width       "100%"
           :height      "100%"}))

(defmethod main ::ng/column-header-wrapper
  [{:keys        [header-spec]
    {{:keys [sm-4 sm-3 light-background border-dark border]}
     :variables} :re-com
    :as          props}]
  (let [{:keys [align-column align-column-header align]} header-spec]
    (update props :style merge
            {:padding-top      sm-3
             :padding-right    sm-4
             :padding-left     sm-4
             :white-space      :nowrap
             :text-overflow    :ellipsis
             :overflow         :hidden
             :background-color light-background
             :color            "#666"
             :text-align       (or align-column-header align-column align :center)
             :font-size        "13px"
             :border-top       (when (get (:edge props) :top) (str "thin solid " border-dark))
             :border-bottom    (str "thin solid " border)
             :border-right     (cond
                                 (get (:edge props) :column-section-right)
                                 (str "thin" " solid " border-dark)
                                 (get (:edge props) :right)
                                 (str "thin" " solid " border-dark)
                                 :else
                                 (str "thin" " solid " border))})))

(defmethod base ::ng/row-header
  [props]
  (update props :style merge {:width         "100%"
                              :text-overflow :ellipsis
                              :overflow      :hidden
                              :white-space   :nowrap
                              :position      :sticky}))

(defmethod base ::ng/column-header
  [props]
  (update props :style merge {:height        "100%"
                              :text-overflow :ellipsis
                              :overflow      :hidden
                              :whitespace    :nowrap}))

(def row-header-wrapper-main
  (let [{:keys [sm-3 sm-6]}        golden-section-50
        {:keys [light-background]} colors]
    {:padding-top      sm-3
     :padding-right    sm-3
     :padding-left     sm-6
     :background-color light-background
     :color            "#666"
     :text-align       "left"
     :font-size        "13px"
     :white-space      "nowrap"
     :border-left      "thin solid #ccc"
     :border-bottom    "thin solid #ccc"}))

(defmethod base ::ng/corner-header-wrapper
  [props]
  (update props :style merge row-header-wrapper-base))

(defmethod main ::ng/corner-header-wrapper
  [{{{:keys [border-dark border light-background]} :variables} :re-com :as props}]
  (update props :style merge
          row-header-wrapper-main
          {:overflow      "hidden"
           :text-overflow "ellipsis"
           :white-space   "nowrap"}
          {:border-left      (when (contains? (:edge props) :left)
                               (str "thin" " solid " border-dark))
           :border-top       (when (get (:edge props) :top)
                               (str "thin solid " border-dark))
           :border-bottom    (when (get (:edge props) :bottom)
                               (str "thin solid " border))
           :border-right     (when (get (:edge props) :right)
                               (str "thin" " solid " border))
           :background-color light-background}))

(defmethod base ::ng/cell-grid-container
  [props]
  (tu/style props
            {:position "relative"
             :gap      "0px"}))

(defmethod main ::ng/cell-grid-container
  [props]
  (tu/style props
            {:padding          "0px"
             :background-color "transparent"}))

(def cell-wrapper-main
  (let [{:keys [sm-3]} golden-section-50]
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
  [{:keys [edge value column-path] :as props}]
  (let [align (some :align column-path)]
    (update props :style merge
            cell-wrapper-main
            (cond align
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

(defmethod main ::ng/row-header-wrapper
  [{:keys [edge] :as props}]
  (update props :style merge
          row-header-wrapper-main
          (when (contains? edge :right)
            {:border-right "thin solid #aaa"})
          (when (contains? edge :left)
            {:border-left "thin solid #aaa"})
          (when (contains? edge :bottom)
            {:border-bottom "thin solid #aaa"})))
