(ns re-com.radio-button.theme
  (:require
   [re-com.radio-button :as-alias rb]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod base ::rb/wrapper [props]
  (merge props {:align :start}))

(defmethod base ::rb/input [props]
  (let [{:keys [disabled? checked?]} (get-in props [:re-com :state])]
    (tu/style props
              (merge (flex-child-style "none")
                     {:cursor (if disabled? "default" "default")}))))

(defmethod base ::rb/label [props]
  (let [{:keys [disabled? label-style]} (get-in props [:re-com :state])]
    (tu/style props
              (merge (flex-child-style "none")
                     {:padding-left "8px"
                      :cursor       (if disabled? "default" "default")}
                     label-style))))

(defmethod bootstrap ::rb/wrapper [props]
  (tu/class props "noselect"))

(defmethod bootstrap ::rb/input [props]
  (tu/class props "rc-radio-button"))

(defmethod bootstrap ::rb/label [props]
  (let [{:keys [label-class]} (get-in props [:re-com :state])]
    (tu/class props "rc-radio-button-label" label-class)))
