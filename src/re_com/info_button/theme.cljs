(ns re-com.info-button.theme
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [re-com.button :as-alias btn]
   [re-com.info-button :as-alias info-btn]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod bootstrap ::info-btn/popover-tooltip [props]
  (tu/class props "rc-info-button-popover-anchor-wrapper"))

(defmethod bootstrap ::info-btn/button [props]
  (let [{:keys [disabled?]} (get-in props [:re-com :state])]
    (tu/class props "noselect" "rc-info-button"
              (when disabled? "rc-icon-disabled"))))

(defmethod base ::info-btn/button
  [{{{:keys [disabled?]} :state
     :keys               [transition!]} :re-com
    :as                                 props}]
  (-> props
      (tu/style {:cursor (when-not disabled? "pointer")})
      (tu/attr {:on-click (when-not disabled?
                            (handler-fn (transition! :toggle)))})))

(defmethod bootstrap ::info-btn/icon [props]
  (tu/class props "rc-info-button-icon"))
