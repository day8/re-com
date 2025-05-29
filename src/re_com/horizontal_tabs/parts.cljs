(ns re-com.horizontal-tabs.parts
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [re-com.theme :as theme]))
 
(defn tab [{:keys [disabled? selected? id class style attr children]}]
  (into
   [:li
   (merge
    {:class (theme/merge-class class
                               (when disabled? "disabled")
                               (when selected? ["active" "rc-tab"]))
     :style style
     :key   (str id)}
    attr)]
   #p children))

(defn anchor [{:keys [on-change selected? disabled? id class style attr label]}]
  [:a
    (merge
     {:class    (str "rc-tab-anchor " #_(get-in parts [:anchor :class]))
      :style    (merge {:cursor "pointer"}
                       style)
      :on-click (when (and on-change (not selected?) (not disabled?)) (handler-fn (on-change id)))}
     #_(get-in parts [:anchor :attr]))
    label])
