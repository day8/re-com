(ns re-com.pill-tabs.theme
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [re-com.dropdown :as-alias dd]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.horizontal-tabs :as-alias ht]
   [re-com.pill-tabs :as-alias pt]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod bootstrap ::pt/wrapper [{{{:keys [vertical?]} :state} :re-com
                                    :as                              props}]
  (tu/class props
            "rc-tabs"
            "noselect"
            "nav"
            "nav-pills"
            (when vertical? "nav-stacked")))

(defmethod base ::pt/wrapper [props]
  (-> props
      (assoc :role "tabslist")
      (tu/style (flex-child-style "none"))))


(defmethod bootstrap ::pt/tab [{:keys                                [tab-type]
                                {{:keys [enable selectable]} :state} :re-com
                                :as                                  props}]
  (case tab-type
    :horizontal
    (tu/class props
              (when (= :disabled enable) "disabled")
              (when (= :selected selectable) ["active"]))))

(defmethod base ::pt/anchor [{{{:keys [enable selectable]} :state} :re-com
                              :keys                                [id on-change]
                              :as                                  props}]
  (cond-> props
    :do                              (tu/style {:cursor "pointer"})
    (and (= :enabled enable)
         (= :unselected selectable)) (tu/attr {:on-click (handler-fn (on-change id))})))

