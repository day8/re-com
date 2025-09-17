(ns re-com.button.theme
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require
   [re-com.button :as-alias btn]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::btn/wrapper
  [props]
  (merge props {:align :start
                :size "none"}))

(defmethod bootstrap ::btn/wrapper
  [props]
  (tu/class props "rc-button-wrapper" "display-inline-flex"))

(defmethod bootstrap ::btn/popover-tooltip
  [props]
  (tu/class props "rc-button-tooltip"))

(defmethod bootstrap ::btn/button
  [{:keys [class] :as props}]
  (tu/class props "rc-button" "btn" (when-not class "btn-default")))

(defmethod base ::btn/button
  [{{{:keys [disabled? tooltip? on-click]} :state
     :keys                        [transition!]} :re-com
    :as                                          props}]
  (-> props
      (tu/style (flex-child-style "none"))
      (tu/attr
       {:disabled disabled?
        :on-click (handler-fn
                   (when (and on-click (not disabled?))
                     (on-click event)))}
       (when tooltip?
         {:on-mouse-over (handler-fn (transition! :show))
          :on-mouse-out  (handler-fn (transition! :hide))}))))
