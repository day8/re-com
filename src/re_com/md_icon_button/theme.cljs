(ns re-com.md-icon-button.theme
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [re-com.md-icon-button :as-alias md-btn]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::md-btn/wrapper [props]
  (merge props {:align :start}))

(defmethod bootstrap ::md-btn/wrapper [props]
  (tu/class props "rc-md-icon-button-wrapper" "display-inline-flex"))

(defmethod bootstrap ::md-btn/popover-tooltip [props]
  (tu/class props "rc-md-icon-button-tooltip"))

(defmethod bootstrap ::md-btn/button [props]
  (let [{:keys [size emphasise? disabled?]} (get-in props [:re-com :state])]
    (tu/class props "noselect" "rc-md-icon-button"
              (case size
                :smaller "rc-icon-smaller"
                :larger "rc-icon-larger"
                "")
              (when emphasise? "rc-icon-emphasis")
              (when disabled? "rc-icon-disabled"))))

(defmethod base ::md-btn/button
  [{{{:keys [disabled? tooltip? on-click]} :state
     :keys                                 [transition!]} :re-com
    :as                                                   props}]
  (-> props
      (tu/style (flex-child-style "none")
                {:cursor (if disabled? "default" "pointer")})
      (tu/attr {:on-click (handler-fn
                           (when (and on-click (not disabled?))
                             (on-click event)))}
               (when tooltip?
                 {:on-mouse-over (handler-fn (transition! :show))
                  :on-mouse-out  (handler-fn (transition! :hide))}))))

(defmethod base ::md-btn/icon [props]
  (let [{:keys [md-icon-name]} (get-in props [:re-com :state])]
    (tu/class props "zmdi" "zmdi-hc-fw-rc" md-icon-name)))
