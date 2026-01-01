(ns re-com.table-filter.theme
  (:require
   [re-com.table-filter :as-alias tf]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod main ::tf/column-dropdown [props]
  (merge props {:width "140px"}))

(defmethod main ::tf/operator-text [props]
  (tu/style props
            {:align-items  "center"
             :color        "#6b7280"
             :display      "flex"
             :font-size    "14px"
             :font-weight  "500"
             :height       "34px"
             :margin-left  "0px"
             :margin-right "0px"
             :min-width    "50px"
             :padding      "6px 6px"
             :text-align   "left"}))
