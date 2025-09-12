(ns re-com.alert-list.theme
  (:require
   [re-com.alert-list :as-alias al]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod bootstrap ::al/wrapper
  [props]
  (tu/class props "rc-alert-list"))

(defmethod base ::al/scroller
  [props]
  (merge props {:v-scroll :auto}))

(defmethod base ::al/v-box
  [props]
  (merge props {:size :auto}))
