(ns re-com.checkbox.theme
  (:require
   [re-com.checkbox :as-alias cb]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::cb/wrapper
  [props]
  (-> props
      (merge {:align :start :gap "8px"})
      (tu/style {:cursor "default"})))

(defmethod bootstrap ::cb/wrapper
  [props]
  (tu/class props "noselect" "rc-checkbox-wrapper"))

(defmethod bootstrap ::cb/input
  [props]
  (tu/class props "rc-checkbox"))

(defmethod bootstrap ::cb/label
  [props]
  (tu/class props "rc-checkbox-label"))

