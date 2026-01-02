(ns re-com.datepicker.theme
  (:require
   [re-com.datepicker :as-alias dp]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod base ::dp/border [props]
  (merge props {:radius "4px"
                :size   "none"
                :border "1px solid lightgrey"}))

(defmethod base ::dp/month [props]
  (merge props {:size "1"}))

(defmethod base ::dp/prev-year [props]
  (merge props {:size "none"}))

(defmethod base ::dp/prev-month [props]
  (merge props {:size "none"}))

(defmethod base ::dp/next-month [props]
  (merge props {:size "none"}))

(defmethod base ::dp/next-year [props]
  (merge props {:size "none"}))

(defmethod bootstrap ::dp/wrapper [props]
  (tu/class props "rc-datepicker-wrapper"))

(defmethod bootstrap ::dp/border [props]
  (let [{:keys [hide-border?]} (get-in props [:re-com :state])]
    (cond-> (tu/class props "rc-datepicker-border")
      hide-border? (assoc :border "none"))))

(defmethod bootstrap ::dp/container [props]
  (-> props
      (tu/class "datepicker" "noselect" "rc-datepicker")
      (tu/style {:font-size "13px"
                 :position  "static"})))

(defmethod bootstrap ::dp/header [props]
  (tu/class props "rc-datepicker-header"))

(defmethod bootstrap ::dp/nav [props]
  (-> props
      (assoc-in [:attr :colspan] "7")
      (tu/class "rc-datepicker-nav")
      (tu/style {:padding "0px"})))

(defmethod bootstrap ::dp/month [props]
  (tu/class props "rc-datepicker-month"))

(defmethod bootstrap ::dp/day [props]
  (tu/class props "rc-datepicker-day"))

(defmethod bootstrap ::dp/dates [props]
  (tu/class props "rc-datepicker-dates"))

(defmethod bootstrap ::dp/date [props]
  (let [{:keys [selectable? disabled? selected? today?]} (get-in props [:re-com :state])]
    (tu/class props
              "rc-datepicker-date"
              (cond disabled?      "rc-datepicker-disabled"
                    (not selectable?) "rc-datepicker-unselectable"
                    :else             "rc-datepicker-selectable")
              (when selected? "rc-datepicker-selected start-date end-date")
              (when today? "rc-datepicker-today"))))

(defmethod bootstrap ::dp/prev-year [props]
  (let [{:keys [enabled?]} (get-in props [:re-com :state])]
    (tu/class props
              (if enabled? "rc-datepicker-selectable" "rc-datepicker-disabled")
              "rc-datepicker-prev-year")))

(defmethod bootstrap ::dp/prev-year-icon [props]
  (tu/class props "rc-datepicker-prev-year-icon"))

(defmethod bootstrap ::dp/prev-month [props]
  (let [{:keys [enabled?]} (get-in props [:re-com :state])]
    (tu/class props
              (if enabled? "rc-datepicker-selectable" "rc-datepicker-disabled")
              "rc-datepicker-prev-month")))

(defmethod bootstrap ::dp/prev-month-icon [props]
  (tu/class props "rc-datepicker-prev-month-icon"))

(defmethod bootstrap ::dp/next-month [props]
  (let [{:keys [enabled?]} (get-in props [:re-com :state])]
    (tu/class props
              (if enabled? "rc-datepicker-selectable" "rc-datepicker-disabled")
              "rc-datepicker-next-month")))

(defmethod bootstrap ::dp/next-month-icon [props]
  (tu/class props "rc-datepicker-next-month-icon"))

(defmethod bootstrap ::dp/next-year [props]
  (let [{:keys [enabled?]} (get-in props [:re-com :state])]
    (tu/class props
              (if enabled? "rc-datepicker-selectable" "rc-datepicker-disabled")
              "rc-datepicker-next-year")))

(defmethod bootstrap ::dp/next-year-icon [props]
  (tu/class props "rc-datepicker-next-year-icon"))
