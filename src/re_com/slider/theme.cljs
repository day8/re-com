(ns re-com.slider.theme
  (:require
   [re-com.slider :as-alias slider]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::slider/wrapper
  [props]
  (merge props {:align :start}))

(defmethod base ::slider/input
  [{{{:keys [disabled? width]} :state} :re-com
    :as                                props}]
  (tu/style props
            (merge (flex-child-style "none")
                   {:width  (or width "400px")
                    :cursor (if disabled? "default" "pointer")})))

(defmethod bootstrap ::slider/wrapper
  [props]
  (tu/class props "rc-slider-wrapper"))

(defmethod bootstrap ::slider/input
  [props]
  (tu/class props "rc-slider"))