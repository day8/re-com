(ns re-com.alert-box.theme
  (:require
   [re-com.alert-box :as-alias ab]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod base ::ab/wrapper
  [props]
  (-> props
      (tu/style (flex-child-style "none"))
      (merge {:gap "10px"})))

(defmethod bootstrap ::ab/wrapper
  [props]
  (tu/class props "rc-alert" "alert" "fade" "in"))

(defmethod base ::ab/header
  [props]
  (merge props {:justify :between
                :align   :center}))

(defmethod bootstrap ::ab/header
  [props]
  (tu/class props "rc-alert-heading"))

(defmethod bootstrap ::ab/heading-wrapper
  [props]
  (tu/class props "rc-alert-h4"))

(defmethod base ::ab/body-wrapper
  [props]
  (merge props {:justify :between
                :align   :center}))

(defmethod bootstrap ::ab/body-wrapper
  [props]
  (tu/class props "rc-alert-body"))

(defmethod bootstrap ::ab/close-button
  [props]
  (tu/class props "rc-alert-close-button"))
         
