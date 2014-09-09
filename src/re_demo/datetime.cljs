(ns re-demo.datetime
  (:require [re-demo.util    :refer  [title]]
            [re-com.datetime :refer  [time-input]]
            [re-com.box      :refer  [h-box v-box box gap line]]
            [reagent.core    :as     reagent]))


(defn single-time []
  [v-box
   :gap "20px"
   :children [[title "Time"]
              [:p "Accepts input of a time. Optional parameters are -"]
              [:ul
               [:li "model"]
               [:li "minimum-time - 2 element vec of min hour and min minute - will not allow input less than this time - default [0 0]."]
               [:li "maximum-time - 2 element vec of max hour and max minute - will not allow input more than this time - default [23 59]."]
               [:li "callback - function to call upon model change."]]
              [time-input]
              [:label (str "entered time: " nil)]]])

(defn time-range []
  [v-box
   :gap "20px"
   :children [[title "Time Range"]]])

(defn single-date []
  [v-box
   :gap "20px"
   :children [[title "Date"]]])

(defn date-range []
  [v-box
   :gap "20px"
   :children [[title "Date Range"]]])

(defn panel
  []
  [v-box
   :children [[single-time]
              [gap :height "30px"]
              [time-range]
              [gap :height "30px"]
              [single-date]
              [gap :height "30px"]
              [date-range]]])



