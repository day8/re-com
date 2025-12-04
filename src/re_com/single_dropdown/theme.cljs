(ns re-com.single-dropdown.theme
  (:require-macros
   [re-com.core     :refer [handler-fn at]])
  (:require
   [re-com.single-dropdown :as-alias sd]
   [re-com.box      :refer [flex-child-style v-box h-box gap]]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod base ::sd/wrapper
  [{{:keys [transition!]
     {:keys [tooltip?]} :state} :re-com :as props}]
  (cond-> props
    tooltip? (tu/attr {:on-mouse-over (handler-fn (transition! :mouse-over))
                       :on-mouse-out  (handler-fn (transition! :mouse-out))})))

(defmethod bootstrap ::sd/wrapper
  [{{{:keys [free-text? drop-showing? focused?]} :state} :re-com :as props}]
  (tu/class props
            "rc-dropdown"
            "chosen-container"
            (if free-text?
              "chosen-container-multi"
              "chosen-container-single")
            "noselect"
            (when (or drop-showing? focused?) "chosen-container-active")
            (when drop-showing? "chosen-with-drop")))

(defmethod bootstrap ::sd/chosen-single [props]
  (tu/class props "rc-dropdown-chosen-single"))

(defmethod bootstrap ::sd/chosen-drop [props]
  (tu/class props "chosen-drop" "rc-dropdown-chosen-drop"))

(defmethod bootstrap ::sd/chosen-results [props]
  (tu/class props "chosen-results" "rc-dropdown-chosen-results"))

(defmethod bootstrap ::sd/choices-loading [props]
  (tu/class props "loading" "rc-dropdown-choices-loading"))

(defmethod bootstrap ::sd/choices-error [props]
  (tu/class props "error" "rc-dropdown-choices-error"))

(defmethod bootstrap ::sd/choices-no-results [props]
  (tu/class props "no-results" "rc-dropdown-choices-no-results"))
