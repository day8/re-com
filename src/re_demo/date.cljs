(ns re-demo.date
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer  [label]]
            [re-com.date     :refer  [single-date]]
            [re-com.box      :refer  [h-box v-box box gap line border]]
            [reagent.core    :as     reagent]))

(defn inline-date
  []
  ;;TODO: Greg investigating why border doesn't size based on content
  ;; so box needs 2px bigger size.
  [v-box
   :gap "20px"
   :children [[title "Date"]
              [box
               :width "197px"
               :child [border
                       :radius "4px"
                       :child [single-date]]]]])

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