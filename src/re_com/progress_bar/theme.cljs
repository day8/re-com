(ns re-com.progress-bar.theme
  (:require
   [re-com.progress-bar :as-alias progress-bar]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [variables base main bootstrap]]))

(defmethod base ::progress-bar/wrapper
  [props]
  (merge props {:size "none"}))

(defmethod bootstrap ::progress-bar/container
  [props]
  (tu/class props "progress" "rc-progress-bar"))

(defmethod bootstrap ::progress-bar/portion
  [props]
  (tu/class props "progress-bar"))

(defmethod main ::progress-bar/portion
  [{{{:keys [striped?]} :state} :re-com
    :as                         props}]
  (cond-> props
    striped? (tu/class "progress-bar-striped" "active")))
