(ns re-com.input-time.theme
  (:require
   [re-com.input-time :as-alias it]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod base ::it/time-entry [{{{:keys [border]} :state} :re-com :as props}]
  (tu/style props (when (= border :hidden)
                    {:border :none})))

(defmethod base ::it/icon [props]
  (tu/style props {:position :static
                   :margin   :auto}))

(defmethod bootstrap ::it/wrapper [props]
  (tu/class props "rc-input-time"))

(defmethod bootstrap ::it/time-entry [props]
  (tu/class props "time-entry" "rc-time-entry"))

(defmethod bootstrap ::it/icon-container [props]
  (tu/class props "time-icon" "rc-time-icon-container"))

(defmethod bootstrap ::it/icon [props]
  (tu/class props "zmdi" "zmdi-hc-fw-rc" "zmdi-time" "rc-time-icon"))
