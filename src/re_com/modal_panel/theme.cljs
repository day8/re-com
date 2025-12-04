(ns re-com.modal-panel.theme
  (:require
   [re-com.modal-panel :as-alias mp]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod base ::mp/wrapper [props]
  (tu/style props {:position :fixed
                   :left     "0px"
                   :top      "0px"
                   :width    "100%"
                   :height   "100%"
                   :z-index  1020}))

(defmethod base ::mp/backdrop [props]
  (tu/style props {:position :fixed
                   :width    "100%"
                   :height   "100%"
                   :z-index  1}))

(defmethod base ::mp/child-container [{{{:keys [wrap]} :state} :re-com :as props}]
  (tu/style props (merge {:margin  :auto
                          :z-index 2}
                         (when (= wrap :nicely)
                           {:background-color :white
                            :padding          "16px"
                            :border-radius    "6px"}))))

(defmethod bootstrap ::mp/wrapper [props]
  (tu/class props "display-flex" "rc-modal-panel"))

(defmethod bootstrap ::mp/backdrop [props]
  (tu/class props "rc-modal-panel-backdrop"))

(defmethod bootstrap ::mp/child-container [props]
  (tu/class props "rc-modal-panel-child-container"))
