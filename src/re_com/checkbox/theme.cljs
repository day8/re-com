(ns re-com.checkbox.theme
  (:require
   [re-com.checkbox :as-alias cb]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::cb/wrapper
  [props]
  (-> props
      (merge {:align :start :gap "8px"})
      (tu/style {:cursor "default"})))

(defmethod bootstrap ::cb/wrapper
  [props]
  (tu/class props "noselect"))

(defmethod bootstrap ::cb/input
  [props]
  (tu/class props "rc-checkbox"))

