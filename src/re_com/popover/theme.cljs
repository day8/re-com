(ns re-com.popover.theme
  (:require
   [re-com.box :refer [flex-child-style align-style]]
   [re-com.popover-title :as-alias pt]
   [re-com.popover-border :as-alias pb]
   [re-com.popover-content-wrapper :as-alias pcw]
   [re-com.popover-anchor-wrapper :as-alias paw]
   [re-com.popover-tooltip :as-alias ptip]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

;;-------------------------------------------------------------------
;; popover-title
;;-------------------------------------------------------------------

(defmethod base ::pt/wrapper [props]
  (tu/style props (merge (flex-child-style "inherit")
                         {:font-size "18px"})))

(defmethod bootstrap ::pt/wrapper [props]
  (tu/class props "popover-title" "rc-popover-title"))

(defmethod base ::pt/container [props]
  (merge props {:justify :between
                :align   :center}))

;;-------------------------------------------------------------------
;; popover-border
;;-------------------------------------------------------------------

(defmethod bootstrap ::pb/wrapper [props]
  (tu/class props "popover" "fade" "in" "rc-popover-border"))

(defmethod bootstrap ::pb/arrow [props]
  (tu/class props "popover-arrow" "rc-popover-arrow"))

(defmethod bootstrap ::pb/content [props]
  (tu/class props "popover-content" "rc-popover-content"))

;;-------------------------------------------------------------------
;; popover-content-wrapper
;;-------------------------------------------------------------------

(defmethod base ::pcw/wrapper [props]
  (tu/style props (flex-child-style "inherit")))

(defmethod bootstrap ::pcw/wrapper [props]
  (tu/class props "popover-content-wrapper" "rc-popover-content-wrapper"))

;;-------------------------------------------------------------------
;; popover-anchor-wrapper
;;-------------------------------------------------------------------

(defmethod base ::paw/wrapper [props]
  (tu/style props (flex-child-style "inherit")))

(defmethod bootstrap ::paw/wrapper [props]
  (tu/class props "rc-popover-anchor-wrapper" "display-inline-flex"))

(defmethod base ::paw/point-wrapper [props]
  (tu/style props (merge (flex-child-style "auto")
                         (align-style :align-items :center))))

(defmethod bootstrap ::paw/point-wrapper [props]
  (tu/class props "display-inline-flex" "rc-point-wrapper"))

(defmethod base ::paw/point [props]
  (tu/style props (merge (flex-child-style "auto")
                         {:position "relative"
                          :z-index  4})))

(defmethod bootstrap ::paw/point [props]
  (tu/class props "display-inline-flex" "rc-popover-point"))

;;-------------------------------------------------------------------
;; popover-tooltip
;;-------------------------------------------------------------------

(defmethod bootstrap ::ptip/wrapper [props]
  (tu/class props "rc-popover-tooltip"))

(defmethod bootstrap ::ptip/close-button-container [props]
  (tu/class props "rc-popover-tooltip-close-button-container"))

(defmethod bootstrap ::ptip/close-button [props]
  (tu/class props "rc-popover-tooltip-close-button"))
