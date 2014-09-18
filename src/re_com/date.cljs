;; Based on: https://github.com/dangrossman/bootstrap-daterangepicker
;; depends: daterangepicker-bs3.css

(ns re-com.date
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [cljs-time.core       :refer [minus plus months days year month day first-day-of-the-month]]
    [cljs-time.predicates :refer [sunday?]]
    [cljs-time.format     :refer [parse unparse formatters formatter]]
    [re-com.box           :refer [box border]]
    [re-com.util          :as util]
    [clojure.string       :as string]
    [reagent.core         :as reagent]))

;; --- private cljs-time facades ----------------------------------------------
;; TODO: from day8date should be a common lib

(def ^:private iso_8601_extended (formatters :basic-date))

(def ^:private month-format (formatter "MMM yyyy"))

(defn- from-ISO8601-extended [extended] (parse iso_8601_extended extended))

(defn- month-label [date] (unparse month-format date))

(defn- dec-month [date] (minus date (months 1)))

(defn- inc-month [date] (plus date (months 1)))

(defn- inc-date [date n] (plus date (days n)))

(defn- ->previous-sunday
  "If passed date is not a sunday, return the nearest previous sunday date"
  [date]
  (if (sunday? date)
    date
    (recur (minus date (days 1)))))

;; ----------------------------------------------------------------------------


(defn- main-div-with
  [table-div]
  ;;TODO: At some point add arg to optionaly turn off border
  [box
   :child [border
           :radius "4px"
           :child [:div
                   {:class "calendar-date daterangepicker opensleft show-calendar"
                    ;; override inherrited body larger 14px font-size
                    ;; override position from css because we are inline
                    :style {:font-size "13px"
                            :position "static"
                            :-webkit-user-select "none"}} table-div]]])


(defn- table-thead
  "Answer 2 x rows for month with nav buttons and days NOTE: non internationalized"
  [current]
  [:thead
   [:tr
    [:th {:class "prev available"}
     [:i {:class "fa fa-arrow-left icon-arrow-left glyphicon glyphicon-arrow-left"
          :on-click #(reset! current (dec-month @current))}]]
    [:th {:class "month" :col-span "5"} (month-label @current)]
    [:th {:class "next available"}
     [:i {:class "fa fa-arrow-right icon-arrow-right glyphicon glyphicon-arrow-right"
          :on-click #(reset! current (inc-month @current))}]]]
   [:tr [:th "Su"][:th "Mo"][:th "Tu"][:th "We"][:th "Th"][:th "Fr"][:th "Sa"]]])


(defn- table-td
  [date column]
  [:td {:class "available"} (day date)])


(trace-forms {:tracer default-tracer}
(defn- table-tr
  [date week-row-ix]
  ;;TODO: Add first column to show week number.
  (let [week-start (inc-date date (* 7 week-row-ix))
        columns    (take-while (partial > 7) (iterate inc 0))]
    (into [:tr] (map #(table-td (inc-date week-start %) %) columns)))))


(defn- table-tbody
  "Answer matrix of 7 x 6 table cells representing days."
  [model]
  (let [rows (take-while (partial > 6) (iterate inc 0))]
    (into [:tbody] (map #(table-tr model %) rows))))


(trace-forms {:tracer default-tracer}
(defn single-date
  [& {:keys [model]}]
  (let [current (reagent/atom @model)
        current-start (-> (first-day-of-the-month @current) ->previous-sunday reagent/atom)]
    (fn []
      (main-div-with [:table {:class "table-condensed"}
                      [table-thead current]
                      [table-tbody @current-start]])))))