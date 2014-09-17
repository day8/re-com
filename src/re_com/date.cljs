;; Based on: https://github.com/dangrossman/bootstrap-daterangepicker
;; depends: daterangepicker-bs3.css

(ns re-com.date
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [cljs-time.core       :refer [minus plus months days year month first-day-of-the-month]]
    [cljs-time.predicates :refer [sunday?]]
    [cljs-time.format     :refer [parse unparse formatters formatter]]
    [re-com.box           :refer [box border]]
    [re-com.util          :as util]
    [clojure.string       :as string]
    [reagent.core         :as reagent]))

;;TODO: From core-utils should be a common lib
(defn ^:private pad-zero [subject-str max-chars]
  "If subject-str zero pad subject-str from left up to max-chars."
  (if (< (count subject-str) max-chars)
    (apply str (take-last max-chars (concat (repeat max-chars \0) subject-str)))
    subject-str))


;; --- private cljs-time facades ----------------------------------------------
;; TODO: from day8date should be a common lib
(def ^:private iso_8601_extended (formatters :basic-date))

(def ^:private month-format (formatter "MMM yyyy"))

(defn ^:private from-ISO8601-extended [extended] (parse iso_8601_extended extended))

(defn ^:private month-label [date] (unparse month-format date))

(defn ^:private dec-date [date] (minus date (months 1)))

(defn ^:private inc-date [date] (plus date (months 1)))

(defn ^:private ->previous-sunday
  "If passed date is not a sunday, return the nearest previous sunday date"
  [date]
  (if (sunday? date)
    date
    (recur (minus date (days 1)))))

;; ----------------------------------------------------------------------------


(defn ^:private main-div-with
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


(defn ^:private table-thead
  "Answer 2 x rows for month with nav buttons and days NOTE: non internationalized"
  [model]
  [:thead
   [:tr
    [:th {:class "prev available"}
     [:i {:class "fa fa-arrow-left icon-arrow-left glyphicon glyphicon-arrow-left"
          :on-click #(reset! model (dec-date @model))}]]
    [:th {:class "month" :col-span "5"} (month-label @model)]
    [:th {:class "next available"}
     [:i {:class "fa fa-arrow-right icon-arrow-right glyphicon glyphicon-arrow-right"
          :on-click #(reset! model (inc-date @model))}]]]
   [:tr [:th "Su"][:th "Mo"][:th "Tu"][:th "We"][:th "Th"][:th "Fr"][:th "Sa"]]])


(trace-forms {:tracer default-tracer}
(defn single-date
  [& {:keys [model]}]
  (let [month-start (first-day-of-the-month @model)
        cal-start   (->previous-sunday month-start) ;;WIP
        cal-end     (plus cal-start (days 41))]     ;;WIP
    (main-div-with [:table {:class "table-condensed"} [table-thead model]]))))