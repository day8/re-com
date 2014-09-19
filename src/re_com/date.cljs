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
    [re-com.util          :as    util]
    [clojure.string       :as    string]
    [reagent.core         :as    reagent]))

;; --- private cljs-time facades ----------------------------------------------
;; TODO: from day8date should be a common lib

(def ^:private month-format (formatter "MMM yyyy"))

(def ^:private week-format (formatter "ww"))

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
                            :-webkit-user-select "none" ;; only good on webkit/chrome what do we do for firefox etc
                            }} table-div]]])


(defn- table-thead
  "Answer 2 x rows for month with nav buttons and days NOTE: not internationalized"
  [current {show-weeks :show-weeks}]
  (let [template-row (if show-weeks [:tr [:th]] [:tr])]
    [:thead
     (conj template-row
           [:th {:class "prev available"}
            [:i {:class "fa fa-arrow-left icon-arrow-left glyphicon glyphicon-arrow-left"
                 :on-click #(reset! current (dec-month @current))}]]
           [:th {:class "month" :col-span "5"} (month-label @current)]
           [:th {:class "next available"}
            [:i {:class "fa fa-arrow-right icon-arrow-right glyphicon glyphicon-arrow-right"
                 :on-click #(reset! current (inc-month @current))}]])
     (conj template-row [:th "Su"][:th "Mo"][:th "Tu"][:th "We"][:th "Th"][:th "Fr"][:th "Sa"])]))


(trace-forms {:tracer default-tracer}
(defn- table-td
  [date focus-month selected on-change]
  ;;Cells which represent days not in focus month are subdued
  ;;TODO: only allow Sundays to be selected, highlight ?
  (let [attributes (if (= focus-month (month date)) "available" "available off")
        attributes (if (= (cljs-time.core/milli selected) (cljs-time.core/milli date)) (str attributes " active start-date end-date") attributes)
        ]
    [:td {:class attributes} (day date)])))


(defn- week-td [date]
  [:td {:class "week"} (unparse week-format date)])


(defn- table-tr
  "Return 7 columns of date cells from date inclusive."
  [date focus-month selected {show-weeks :show-weeks} on-change]
  {:pre [(sunday? date)]}
  (let [table-row (if show-weeks [:tr (week-td date)] [:tr])]
    (into table-row (map #(table-td (inc-date date %) focus-month selected on-change) (range 7)))))


(defn- table-tbody
  "Return matrix of 6 rows x 7 cols table cells representing 41 days from start-date inclusive"
  [current selected attributes on-change]
  (let [current-start   (->previous-sunday current)
        focus-month     (month current)
        row-start-dates (map #(inc-date current-start (* 7 %)) (range 6))]
    (into [:tbody] (map #(table-tr % focus-month selected attributes on-change) row-start-dates))))


(defn inline-date-picker
  [& {:keys [model attributes on-change]}]
  ;;TODO: add attribute property handling for :minimum :maximum :disabled etc
  (let [current (reagent/atom (first-day-of-the-month @model))]
    (fn []
      (main-div-with
        [:table {:class "table-condensed"}
         [table-thead current @attributes]
         [table-tbody @current @model @attributes on-change]]))))