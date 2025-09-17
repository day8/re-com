(ns re-com.md-circle-icon-button.theme
  (:require-macros [re-com.core :refer [handler-fn]])
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

(defmethod bootstrap ::ci-btn/popover-tooltip
  [props]
  (tu/class props "rc-md-circle-icon-button-tooltip"))

(defmethod base ::ci-btn/button
  [{{{:keys [disabled? on-click tooltip?]} :state
     :keys                                 [transition!]} :re-com
    :as                                                   props}]
  (-> props
      (tu/style {:cursor (when-not disabled? "pointer")})
      (tu/attr {:on-click
                (handler-fn
                 (when (and on-click (not disabled?))
                   (on-click event)))}
               (when tooltip?
                 {:on-mouse-over (handler-fn (transition! :show))
                  :on-mouse-out  (handler-fn (transition! :hide))}))))

(defmethod bootstrap ::ci-btn/button
  [props]
  (let [{:keys [size emphasise? disabled?]} (get-in props [:re-com :state])]
    (tu/class props
              "noselect" "rc-md-circle-icon-button"
              (case size
                :smaller "rc-circle-smaller"
                :larger  "rc-circle-larger"
                "")
              (when emphasise? "rc-circle-emphasis")
              (when disabled? "rc-circle-disabled"))))

(defmethod bootstrap ::ci-btn/icon
  [{{{:keys [md-icon-name]} :state} :re-com
    :as                             props}]
  (tu/class props "zmdi" "zmdi-hc-fw-rc" md-icon-name))
