(ns re-com.selection-list.theme
  (:require
   [re-com.selection-list :as-alias sl]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [bootstrap base main]]))

(defmethod main ::sl/wrapper [{{{:keys [border]} :state} :re-com :as props}]
  (merge props
         (when (= :hidden border) {:border "none"})
         {:radius "4px"}))

(defmethod bootstrap ::sl/wrapper [{{{:keys [interaction]} :state} :re-com :as props}]
  (tu/class props "rc-selection-list" (when (= :disabled interaction) "rc-disabled")))

(defmethod base ::sl/list-group [{{{:keys [border]} :state} :re-com :as props}]
  (tu/style props
            {:overflow-x "hidden"
             :overflow-y "auto"}
            (if (= :hidden border)
              {:padding-left   "0px"
               :padding-right  "5px"
               :padding-top    "0px"
               :padding-bottom "0px"
               :margin-top     "0px"
               :margin-bottom  "0px"}
              {:padding-top    "0px"
               :padding-bottom "0px"
               :padding-left   "5px"
               :padding-right  "5px"
               :margin-top     "5px"
               :margin-bottom  "5px"})))

(defmethod bootstrap ::sl/list-group [props]
  (tu/class props "list-group" "noselect" "rc-selection-list-group"))

(defmethod base ::sl/list-group-item [props]
  (merge props {:size    "auto"
                :align   :center
                :justify :between}))

(defmethod bootstrap ::sl/list-group-item [props]
  (tu/class props "list-group-item" "compact"))

(defmethod main ::sl/only-button [props]
  (tu/style props {:background  "none"
                   :border      "none"
                   :color       "#007bff"
                   :cursor      "pointer"
                   :font-size   "12px"
                   :padding     "2px 6px"
                   :line-height "1.2"}))
