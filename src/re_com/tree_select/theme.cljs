(ns re-com.tree-select.theme
  (:require
   [re-com.theme.util :refer [merge-props merge-style]]
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
  (merge-style props {:padding  "0 0 0 0"
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
  (merge-style props
               {:style {#_#_:margin-left  "5px"
                        #_#_:margin-right "5px"
                        :opacity          "50%"}}))
