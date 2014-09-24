(ns re-demo.time
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer [label]]
            [re-com.util     :refer  [pad-zero-number]]
            [re-com.time     :refer  [time-input time-range-input]]
            [re-com.box      :refer  [h-box v-box box gap line]]
            [reagent.core    :as     reagent]))

(defn display-time
  "Return a string display of the time."
  [time-int]
  (let [hour (quot time-int 100)
        min  (rem  time-int 100)]
    (str (pad-zero-number hour 2)
         ":"
         (pad-zero-number min 2))))

(defn single-time []
  (let [model1 (reagent/atom 900)
        model2 (reagent/atom 2030)]
    [v-box
     :gap "0px"
     :children [[title "Time"]
                [:p "Accepts input of a time. Model does not update until a valid time has been entered. Required parameters are -"]
                [:ul
                  [:li "model - an integer time e.g. 930"]]
                [:p "Optional parameters are -"]
                [:ul
                  [:li "minimum-time - min time as an integer e.g.  930 - will not allow input less than this time - default 0."]
                  [:li "maximum-time - max time as an integer e.g. 1400 - will not allow input more than this time - default 2359."]
                  [:li "on-change - function to call upon change."]]
                [time-input
                  :model (reagent/atom nil)
                  :style {}]
                [h-box
                  :gap "4px"
                  :children [[label :label "Time with default range:"]
                             [time-input :model model1 :on-change #(reset! model1 %)]
                             [label :label "entered time: "]
                             [label :label (display-time @model1)]]]
                [gap "14px"]
                [h-box
                  :gap "4px"
                  :children [[label :label "Time with range 06:00-21:59"]
                             [time-input
                                :model model2
                                :minimum-time 600
                                :maximum-time 2159
                                :on-change #(reset! model2 %)]
                             [label :label "entered time: "]
                             [label :label (display-time @model2)]]]]]))

(defn time-range []
  (let [range-model (reagent/atom [900 2100])]
    [v-box
      :gap "20px"
      :children [[title "Time Range"]
                 [time-range-input
                   :model range-model
                   ;;:from-label "From:"
                   :to-label "-"
                   :gap "4px"]]]))

(defn panel
  []
  [v-box
   :children [[single-time]
              [gap :height "30px"]
              [time-range]]])



