(ns re-com.md-circle-icon-button.theme
  (:require
   [re-com.button :as-alias btn]
   [re-com.md-circle-icon-button :as-alias ci-btn]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::btn/md-circle-wrapper
  [props]
  (merge props {:align :start}))

(defmethod bootstrap ::ci-btn/wrapper
  [props]
  (tu/class props "display-inline-flex" "rc-md-circle-icon-button-wrapper"))

(defmethod bootstrap ::ci-btn/tooltip-wrapper
  [props]
  (tu/class props "rc-md-circle-icon-button-tooltip"))

(defmethod base ::ci-btn/button [{:keys [disabled?]
                                  :as   props}]
  (tu/style props {:cursor (when-not disabled? "pointer")}))

(defmethod bootstrap ::ci-btn/button
  [{:keys [size emphasise? disabled?] :as props}]
  (tu/class props
            "noselect" "rc-md-circle-icon-button"
            (case size
              :smaller "rc-circle-smaller"
              :larger  "rc-circle-larger"
              "")
            (when emphasise? "rc-circle-emphasis")
            (when disabled? "rc-circle-disabled")))

(defmethod bootstrap ::ci-btn/icon
  [props]
  (tu/class props "zmdi" "zmdi-hc-fw-rc"))
