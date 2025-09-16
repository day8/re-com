(ns re-com.info-button.theme
  (:require
   [re-com.button :as-alias btn]
   [re-com.info-button :as-alias info-btn]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod bootstrap ::info-btn/tooltip-wrapper [props]
  (tu/class props "rc-info-button-popover-anchor-wrapper"))

(defmethod bootstrap ::info-btn/button [props]
  (let [{:keys [disabled?]} (get-in props [:re-com])]
    (tu/class props "noselect" "rc-info-button"
              (when disabled? "rc-icon-disabled"))))

(defmethod base ::info-btn/button [props]
  (let [{:keys [disabled?]} (get-in props [:re-com])]
    (tu/style props {:cursor (when-not disabled? "pointer")})))

(defmethod bootstrap ::info-btn/icon [props]
  (tu/class props "rc-info-button-icon"))
