(ns re-com.alert-box.theme
  (:require
   [re-com.alert-box :as-alias ab]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod base ::ab/wrapper
  [{{{:keys [padding]} :state} :re-com
    :as                        props}]
  (-> props
      (tu/style (merge (flex-child-style "none")
                       (when padding {:padding padding})))
      (merge {:gap "10px"})))

(defmethod bootstrap ::ab/wrapper
  [{{{:keys [alert-type]} :state} :re-com
    :as                           props}]
  (let [alert-class (case alert-type
                      :none    ""
                      :info    "alert-success"
                      :warning "alert-warning"
                      :danger  "alert-danger"
                      "alert-success")]
    (tu/class props "rc-alert" "alert" "fade" "in" alert-class)))

(defmethod base ::ab/header
  [{{{:keys [body-provided?]} :state} :re-com
    :as                               props}]
  (-> props
      (merge {:justify :between
              :align   :center})
      (tu/style {:margin-bottom (if body-provided? "10px" "0px")})))

(defmethod bootstrap ::ab/header
  [props]
  (tu/class props "rc-alert-heading"))

(defmethod bootstrap ::ab/heading-wrapper
  [props]
  (tu/class props "rc-alert-h4"))

(defmethod base ::ab/body-wrapper
  [props]
  (merge props {:justify :between
                :align   :center}))

(defmethod bootstrap ::ab/body-wrapper
  [props]
  (tu/class props "rc-alert-body"))

(defmethod bootstrap ::ab/close-button
  [props]
  (tu/class props "rc-alert-close-button"))
         
