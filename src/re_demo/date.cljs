(ns re-demo.date
  (:require [re-demo.util    :refer  [title]]
            [cljs-time.core `:refer  [now]]
            [re-com.core     :refer  [label]]
            [re-com.date     :refer  [single-date]]
            [re-com.box      :refer  [h-box v-box box gap line border]]
            [reagent.core    :as     reagent]))

(defn inline-date
  []
  (let [model (reagent/atom (now))]
    [v-box
     :gap "20px"
     :align :start
     :children [[title "Date"]
                [single-date :model model]]]))

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