(ns re-com.alert-box.theme
  (:require
   [re-com.alert-box :as-alias ab]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod base ::ab/wrapper
  [props]
  (-> props
      (tu/style (merge (flex-child-style "none")
                       {:gap "10px"}))
      (merge {:size "auto"})))

(defmethod bootstrap ::ab/wrapper
  [{{{:keys [alert-type]} :state} :re-com
    :as props}]
  (let [alert-class (case alert-type
                      :none    ""
                      :info    "alert-success"
                      :warning "alert-warning"
                      :danger  "alert-danger"
                      "alert-success")]
    (tu/class props "rc-alert" "alert" "fade" "in" alert-class)))

(defmethod base ::ab/header
  [props]
  (merge props {:justify :between
                :align   :center}))

(defmethod bootstrap ::ab/header
  [props]
  (tu/class props "rc-alert-heading"))

(defmethod bootstrap ::ab/heading-wrapper
  [{{:keys [from]} :re-com :as props}]
  (cond-> props
    :do (tu/class "rc-alert-h4")
    (and (contains? (set from) :re-com/alert-list)
         (contains? (set from) :re-demo/alert-list))
    (tu/attr {:title (str "This tooltip only appears on an alert-box "
                          "which descends from an alert-list!")})))

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

