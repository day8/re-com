(ns re-com.hyperlink-href.theme
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require
   [re-com.hyperlink-href :as-alias hyperlink-href]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]
   [re-com.box :refer [flex-child-style]]
   [re-com.util :as u]))

(defmethod base ::hyperlink-href/wrapper
  [props]
  (merge props {:align :start}))

(defmethod bootstrap ::hyperlink-href/wrapper
  [props]
  (tu/class props "display-inline-flex" "rc-hyperlink-href-wrapper"))

(defmethod bootstrap ::hyperlink-href/popover-tooltip
  [props]
  (tu/class props "rc-hyperlink-href-tooltip"))

(defmethod base ::hyperlink-href/link
  [{{{:keys [disabled? href target]} :state
     :keys                          [transition!]} :re-com
    :as                                            props}]
  (-> props
      (tu/style (merge (flex-child-style "none")
                       {:cursor (if disabled? "default" "pointer")
                        :pointer-events (when disabled? "none")
                        :color (when disabled? "grey")}))
      (tu/attr {:target target}
               ;; HTML5 spec: href not required for disabled links (placeholder links)
               (when (not disabled?) {:href href})
               (when transition!
                 {:on-mouse-over (handler-fn (transition! :show))
                  :on-mouse-out (handler-fn (transition! :hide))}))))

(defmethod bootstrap ::hyperlink-href/link
  [props]
  (tu/class props "noselect" "rc-hyperlink-href"))