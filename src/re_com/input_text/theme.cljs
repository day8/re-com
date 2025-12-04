(ns re-com.input-text.theme
  (:require
   [re-com.input-text :as-alias it]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [bootstrap base]]))

(defmethod base ::it/wrapper [props]
  (merge props {:align :start}))

(defmethod base ::it/inner [props]
  (tu/style props {:flex "auto"}))

(defmethod base ::it/field [props]
  (tu/style props {:flex "none" :padding-right "12px"}))

(defmethod base ::it/throbber [props]
  (tu/style props {:flex        "none"
                   :margin-left "4px"
                   :align-self  :center}))

(defmethod base ::it/status-icon [{{{:keys [showing?*]} :state} :re-com :as props}]
  (tu/style props {:flex         "none"
                   :font-size    "130%"
                   :margin-left  "4px"
                   :align-self   :center
                   :position     :static
                   :height       :auto
                   :opacity      (if (and showing?* @showing?*) "1" "0")}))

(defmethod bootstrap ::it/wrapper [props]
  (tu/class props "rc-input-text"))

(defmethod bootstrap ::it/inner [{{{:keys [status status-icon?]} :state} :re-com :as props}]
  (tu/class props "rc-input-text-inner"
            (case status
              :success "has-success"
              :warning "has-warning"
              :error   "has-error"
              "")
            (when (and status status-icon?) "has-feedback")))

(defmethod bootstrap ::it/field [props]
  (tu/class props "form-control" "rc-input-text-field"))

(defmethod bootstrap ::it/throbber [props]
  (tu/class props "smaller"))

(defmethod bootstrap ::it/status-icon [{{{:keys [status]} :state} :re-com :as props}]
  (tu/class props "zmdi" "zmdi-hc-fw" "form-control-feedback"
            (case status
              :success    "zmdi-check-circle"
              :warning    "zmdi-alert-triangle"
              :error      "zmdi-alert-circle"
              :validating "zmdi-hc-spin zmdi-rotate-right zmdi-spinner"
              "")))
