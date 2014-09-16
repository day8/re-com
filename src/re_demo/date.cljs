(ns re-demo.date
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer  [label]]
            [re-com.date     :refer  [single-date]]
            [re-com.box      :refer  [h-box v-box box gap line]]
            [reagent.core    :as     reagent]))

(defn inline-date
  []
  [v-box
   :gap "20px"
   :children [[title "Date"]]])

(defn inline-date-range
  []
  [v-box
   :gap "20px"
   :children [[title "Date Range"]]])

(defn panel
  []
  [v-box
   :children [[inline-date]
              [gap :height "30px"]
              [inline-date-range]]])