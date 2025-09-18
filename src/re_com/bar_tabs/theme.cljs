(ns re-com.bar-tabs.theme
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [re-com.dropdown :as-alias dd]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.horizontal-tabs :as-alias ht]
   [re-com.bar-tabs :as-alias bt]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod bootstrap ::bt/wrapper [{{{:keys [vertical?]} :state} :re-com
                                    :as                          props}]
  (tu/class props "noselect"
            (if vertical? "btn-group-vertical" "btn-group")
            "rc-tabs"))

(defmethod base ::bt/wrapper [props]
  (-> props
      (assoc :type "button")
      (tu/style (flex-child-style "none"))))

(defmethod bootstrap ::bt/button [{{{:keys [enable selectable]} :state} :re-com
                                    :as                                  props}]
  (tu/class props
            "btn"
            "btn-default"
            "rc-tabs-btn"
            (when (= :disabled enable) "disabled")
            (when (= :selected selectable) ["active"])))

(defmethod base ::bt/button [{{{:keys [enable selectable]} :state} :re-com
                               :keys                                [id on-change]
                               :as                                  props}]
  (cond-> props
    :do                              (tu/style {:cursor "pointer"})
    (and on-change
         (= :enabled enable)
         (= :unselected selectable)) (tu/attr {:on-click (handler-fn (on-change id))})))
