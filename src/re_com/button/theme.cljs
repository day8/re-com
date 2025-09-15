(ns re-com.button.theme
  (:require
   [re-com.button :as-alias btn]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [variables base main bootstrap]]))

(defmethod base ::btn/wrapper [props]
  (merge props {:align :start
                :size "none"}))

(defmethod bootstrap ::btn/wrapper [props]
  (tu/class props "rc-button-wrapper" "display-inline-flex"))

(defmethod bootstrap ::btn/tooltip-wrapper [props]
  (tu/class props "rc-button-tooltip"))

(defmethod bootstrap ::btn/button [props]
  (tu/class props "rc-button" "btn"))

(defmethod base ::btn/button [props]
  (tu/style props (flex-child-style "none")))
