(ns re-com.throbber.theme
  (:require
   [re-com.throbber :as-alias th]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::th/wrapper
  [props]
  (merge props {:align :start}))

(defmethod bootstrap ::th/throbber
  [{{{:keys [size]} :state} :re-com
    :as                     props}]
  (tu/class props "loader" "rc-throbber"
            (case size
              :smaller "smaller"
              :small   "small"
              :large   "large"
              nil)))

(defmethod base ::th/segment
  [{{{:keys [color]} :state} :re-com
    :as                      props}]
  (tu/style props (when color {:background-color color})))
