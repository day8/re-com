(ns re-com.horizontal-tabs.theme
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [re-com.dropdown :as-alias dd]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.horizontal-tabs :as-alias ht]
   [re-com.horizontal-bar-tabs :as-alias hbt]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod bootstrap ::ht/wrapper [props]
  (tu/class props "nav" "nav-tabs"  "noselect" "rc-tabs"))

(defmethod base ::ht/wrapper [props]
  (tu/style props (flex-child-style "none")))

(defmethod bootstrap ::ht/tab [{:keys                                [tab-type]
                                {{:keys [enable selectable]} :state} :re-com
                                :as                                  props}]
  (case tab-type
    :horizontal
    (tu/class props
              (when (= :disabled enable) "disabled")
              (when (= :selected selectable) ["active" "rc-tab"]))))

(defmethod base ::ht/anchor [{{{:keys [enable selectable]} :state} :re-com
                              :keys                                [id on-change]
                              :as                                  props}]
  (cond-> props
    :do                              (tu/style {:cursor "pointer"})
    (and (= :enabled enable)
         (= :unselected selectable)) (tu/attr {:on-click (handler-fn (on-change id))})))
