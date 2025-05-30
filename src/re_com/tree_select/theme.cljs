(ns re-com.tree-select.theme
  (:require
   [re-com.theme.util :as tu :refer [merge-props]]
   [re-com.tree-select :as-alias ts]
   [re-com.theme.default :refer [base main]]))

(defmethod base ::ts/label
  [props]
  (update props :style merge
          {:white-space   :nowrap
           :overflow      :hidden
           :text-overflow :ellipsis}))

(defmethod main ::ts/dropdown-anchor
  [{:keys          [state]
    {$ :variables} :re-com
    :as            props}]
  (tu/style props {:padding  "0 0 0 0"
                   :overflow "hidden"
                   :color    (:foreground $)
                   :cursor   (if (-> state :enable (= :disabled))
                               "default" "pointer")}))

(defmethod main ::ts/dropdown-indicator
  [{{$ :variables} :re-com
    :as            props}]
  (merge-props props {:align :center
                      :style {:gap   "5px"
                              :color (:light-foreground $)}}))

(defmethod main
  ::ts/dropdown-indicator-triangle
  [{{$ :variables} :re-com
    :as props}]
  (merge-props props
               {:align :center
                :style {:gap   "5px"
                        :color (:foreground $)}}))

(defmethod main ::ts/dropdown-counter
  [props]
  (tu/style props
            {:style {#_#_:margin-left  "5px"
                     #_#_:margin-right "5px"
                     :opacity          "50%"}}))

(defmethod base ::ts/only-button
  [{{{:keys [background]} :variables} :re-com :as props}]
  (tu/style props {:position   :absolute
                   :right      5
                   :margin-bottom     2
                   :background background}))
