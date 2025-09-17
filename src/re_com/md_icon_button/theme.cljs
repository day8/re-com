(ns re-com.md-icon-button.theme
  (:require
   [re-com.md-icon-button :as-alias md-btn]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::md-btn/wrapper [props]
  (merge props {:align :start}))

(defmethod bootstrap ::md-btn/wrapper [props]
  (tu/class props "rc-md-icon-button-wrapper" "display-inline-flex"))

(defmethod bootstrap ::md-btn/tooltip-wrapper [props]
  (tu/class props "rc-md-icon-button-tooltip"))

(defmethod bootstrap ::md-btn/button [props]
  (let [{:keys [size emphasise? disabled?]} (get-in props [:re-com])]
    (tu/class props "noselect" "rc-md-icon-button"
              (case size
                :smaller "rc-icon-smaller"
                :larger "rc-icon-larger"
                "")
              (when emphasise? "rc-icon-emphasis")
              (when disabled? "rc-icon-disabled"))))

(defmethod base ::md-btn/button [props]
  (let [{:keys [disabled?]} (get-in props [:re-com])]
    (-> props
        (tu/style (merge (flex-child-style "none")
                         {:cursor (if disabled? "default" "pointer")}))
        (update :re-com dissoc :disabled?))))

(defmethod bootstrap ::md-btn/icon [props]
  (let [{:keys [md-icon-name]} (get-in props [:re-com])]
    (tu/class props "zmdi" "zmdi-hc-fw-rc" md-icon-name "rc-md-icon-button-icon")))