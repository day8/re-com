(ns re-com.multi-select.theme
  (:require
   [clojure.string :as str]
   [re-com.box :as box]
   [re-com.dropdown :as-alias dd]
   [re-com.table-filter :as-alias tf]
   [re-com.util :refer [px]]
   [re-com.theme.util :as tu :refer [merge-props]]
   [re-com.theme.default :refer [base main bootstrap]]
   [re-com.multi-select :as-alias ms]))

(defmethod base ::ms/filter-text-box [props]
  (-> props
      (merge {:width "100%"
              :align :center})
      (tu/style {:position :relative})))

(defmethod base ::ms/filter-input-text [props]
  (-> props
      (merge {:width "100%"
              :height "28px"})
      (tu/style {:padding "3px 4px"})))

(defmethod base ::ms/filter-reset-button [props]
  (merge props {:div-size    0
                :font-size   20
                :left-offset -13}))

(defmethod bootstrap ::ms/container [props]
  (tu/class props
            "rc-multi-select"
            "noselect"
            "chosen-container"
            "chosen-container-single"))

(defmethod base ::ms/container [props]
  (tu/style props
            {:overflow "hidden"}
            (box/align-style :align-self :start)))

(defmethod base ::ms/inner-container [props]
  (merge props {:gap "4px"}))

(defmethod base ::ms/left [props]
  (merge props {:size "50%"
                :gap  "4px"}))

(defmethod base ::ms/left-label-container [props]
  (merge props {:justify :between}))

(defmethod base ::ms/left-label [props]
  (tu/style props {:font-size   "small"
                   :font-weight "bold"}))

(defmethod base ::ms/left-label-item-count [props]
  (tu/style props {:font-size "smaller"}))

(defmethod base ::ms/left-filter-result-count [props]
  (tu/style props {:font-size "smaller"}))

(defmethod base ::ms/middle-container [props]
  (merge props {:justify :between}))

(defmethod base ::ms/middle-spacer [props]
  (merge props {:size  "0 1 22px" ;; 22 = (+ 18 4) - height of the top components
                :child ""}))

(defmethod base ::ms/middle [props]
  (merge props {:justify :center}))

(def button-style {:width        "86px"
                   :height       "24px"
                   :padding      "0px 8px 2px 8px"
                   :margin       "8px 6px"
                   :text-align   "left"
                   :font-variant "small-caps"
                   :font-size    11})

(defmethod base ::ms/include-all-button [props]
  (tu/style props button-style))

(defmethod base ::ms/include-selected-button [props]
  (tu/style props button-style))

(defmethod base ::ms/exclude-selected-button [props]
  (tu/style props button-style))

(defmethod base ::ms/exclude-all-button [props]
  (tu/style props button-style))

(defmethod base ::ms/right [props]
  (-> props
      (merge {:size  "50%"
              :gap   "4px"})
      (tu/style {:position "relative"})))

(defmethod base ::ms/right-label [props]
  (tu/style props   {:font-size   "small"
                     :font-weight "bold"}))

(defmethod base ::ms/right-label-item-count [props]
  (tu/style props   {:font-size   "smaller"}))

(defmethod base ::ms/right-filter-result-count [props]
  (tu/style props   {:font-size   "smaller"}))
