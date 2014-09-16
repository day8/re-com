(ns re-com.date
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [cljs-time.core       :refer [now minus plus months]]
    [cljs-time.predicates :refer [sunday?]]
    [cljs-time.format     :refer [parse unparse formatters formatter]]
    [re-com.util          :as util]
    [clojure.string       :as string]
    [reagent.core         :as reagent]))

;; --- private cljs-time facades ----------------------------------------------

(def ^:private month-format (formatter "MMM yyyy"))

(defn ^:private month-label [date] (unparse month-format date))

(defn ^:private dec-date [date] (minus date (months 1)))

(defn ^:private inc-date [date] (plus date (months 1)))

;; ----------------------------------------------------------------------------

;; Based on: https://github.com/dangrossman/bootstrap-daterangepicker
;; depends: daterangepicker-bs3.css

(def ^:private calendar-div
  [:div
   {:class "calendar-date daterangepicker opensleft show-calendar"
    ;; override inherrited body larger 14px font-size
    ;; override positioning form css because we are inline
    ;; override specify width because currently it does not size on content GR investigatings
    :style {:font-size "13px" :position "static" :width "195px"}}])

(defn ^private table-thead
  "Answer 2 x rows for month with nav buttons and days"
  [current]
  [:thead
   [:tr
    [:th {:class "prev available"}
     [:i {:class "fa fa-arrow-left icon-arrow-left glyphicon glyphicon-arrow-left"
          :on-click #(reset! current (dec-date @current))}]]
    [:th {:class "month" :col-span "5"} (month-label @current)]
    [:th {:class "next available"}
     [:i {:class "fa fa-arrow-right icon-arrow-right glyphicon glyphicon-arrow-right"
          :on-click #(reset! current (inc-date @current))}]]]
   [:tr [:th "Su"][:th "Mo"][:th "Tu"][:th "We"][:th "Th"][:th "Fr"][:th "Sa"]]])


(defn single-date
  [& {:keys [model]}]
  (let [current (reagent/atom (now))
        table   [:table {:class "table-condensed"} [table-thead current]]]
    (conj calendar-div table)))