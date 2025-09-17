(ns re-com.row-button.theme
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require
   [re-com.row-button :as-alias rb]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::rb/wrapper
  [props]
  (merge props {:align :start}))

(defmethod bootstrap ::rb/wrapper
  [props]
  (tu/class props "display-inline-flex"))

(defmethod bootstrap ::rb/popover-tooltip
  [props]
  (tu/class props "rc-row-button-tooltip"))

(defmethod bootstrap ::rb/button
  [{{{:keys [mouse-over-row? disabled?]} :state} :re-com
    :as                                          props}]
  (tu/class props "noselect" "rc-row-button"
            (when mouse-over-row? "rc-row-mouse-over-row")
            (when disabled? "rc-row-disabled")))

(defmethod base ::rb/button
  [{{{:keys [disabled? tooltip? on-click]} :state
     :keys                                 [transition!]} :re-com
    :as                                          props}]
  (-> props
      (tu/attr
       {:disabled disabled?
        :on-click (handler-fn
                   (when (and on-click (not disabled?))
                     (on-click event)))}
       (when tooltip?
         {:on-mouse-over (handler-fn (transition! :show))
          :on-mouse-out  (handler-fn (transition! :hide))}))))

(defmethod bootstrap ::rb/icon
  [{{{:keys [md-icon-name]} :state} :re-com
    :as                             props}]
  (tu/class props "zmdi" "zmdi-hc-fw-rc" md-icon-name))
