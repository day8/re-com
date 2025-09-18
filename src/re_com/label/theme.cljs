(ns re-com.label.theme
  (:require
   [re-com.text :as-alias text]
   [re-com.label :as-alias l]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::l/wrapper
  [props]
  (merge props {:align :start}))

(defmethod base ::l/label
  [props]
  (tu/style props (flex-child-style "none")))

(defmethod bootstrap ::l/wrapper
  [props]
  (tu/class props "display-inline-flex" "rc-label-wrapper"))

(defmethod bootstrap ::l/label
  [props]
  (tu/class props "rc-label"))
