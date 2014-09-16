(ns re-com.date
  (:require
    [re-com.util  :as util]
    [clojure.string :as string]
    [reagent.core :as reagent]))

;; Based on: https://github.com/dangrossman/bootstrap-daterangepicker
;; depends: css/daterangepicker-bs3.css

(defn single-date
  [& {:keys [model]}]
  "TODO: Render a bootstrap styled date calendar"
  [:div {:class "calendar-date daterangepicker opensleft show-calendar"
         :style {:font-size "13px"}
         }
   [:table {:class "table-condensed"}
    [:thead
     [:tr
      [:th {:class "prev available"}
       [:i {:class "fa fa-arrow-left icon-arrow-left glyphicon glyphicon-arrow-left"}]]
      [:th {:class "month" :col-span "5"} "Aug 2014"]
      [:th {:class "next available"}
       [:i {:class "fa fa-arrow-right icon-arrow-right glyphicon glyphicon-arrow-right"}]]]
     [:tr [:th "Su"][:th "Mo"][:th "Tu"][:th "We"][:th "Th"][:th "Fr"][:th "Sa"]]
    ]
   ]])