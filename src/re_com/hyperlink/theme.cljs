(ns re-com.hyperlink.theme
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require
   [re-com.hyperlink :as-alias hyperlink]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]
   [re-com.box :refer [flex-child-style]]))

(defmethod base ::hyperlink/wrapper
  [props]
  (merge props {:align :start}))

(defmethod bootstrap ::hyperlink/wrapper
  [props]
  (tu/class props "display-inline-flex" "rc-hyperlink-wrapper"))

(defmethod bootstrap ::hyperlink/popover-tooltip
  [props]
  (tu/class props "rc-hyperlink-tooltip"))

(defmethod base ::hyperlink/link
  [{{{:keys [disabled? on-click]} :state
     :keys                       [transition!]} :re-com
    :as                                         props}]
  (-> props
      (tu/style (merge (flex-child-style "none")
                       {:cursor (if disabled? "default" "pointer")
                        :pointer-events (when disabled? "none")
                        :color (when disabled? "grey")}))
      (tu/attr {:on-click (handler-fn
                           (when (and on-click (not disabled?))
                             (on-click event)))}
               (when transition!
                 {:on-mouse-over (handler-fn (transition! :show))
                  :on-mouse-out (handler-fn (transition! :hide))}))))

(defmethod bootstrap ::hyperlink/link
  [props]
  (tu/class props "noselect" "rc-hyperlink"))