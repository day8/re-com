(ns re-demo.time
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer [label]]
            [re-com.time     :refer  [time-input time-range-input]]
            [re-com.box      :refer  [h-box v-box box gap line]]
            [reagent.core    :as     reagent]))

(defn pad-zero [subject-str max-chars]  ;; TODO this needs to be a utility method
  "If subject-str zero pad subject-str from left up to max-chars."
  (if (< (count subject-str) max-chars)
  	(apply str (take-last max-chars (concat (repeat max-chars \0) subject-str)))
  	subject-str))

(defn display-time
  "Return a string display of the time."
  [time-map]
  (str
    (if (:hour time-map)
      (pad-zero (str (:hour time-map)) 2)
      "00")
    (if (:minute time-map)
      (str ":" (pad-zero (str (:minute time-map)) 2))
      ":00")))

(defn single-time []
  (let [model1 (reagent/atom {:hour 9 :minute 0})
        model2 (reagent/atom {:hour 20 :minute 30})]
    [v-box
     :gap "0px"
     :children [[title "Time"]
                [:p "Accepts input of a time. Model does not update until a valid time has been entered. Required parameters are -"]
                [:ul
                  [:li "model"]]
                [:p "Optional parameters are -"]
                [:ul
                  [:li "minimum-time - 2 element vec of min hour and min minute - will not allow input less than this time - default [0 0]."]
                  [:li "maximum-time - 2 element vec of max hour and max minute - will not allow input more than this time - default [23 59]."]
                  [:li "callback - function to call upon model change."]]
                [time-input
                  :model (reagent/atom {:hour nil :minute nil})
                  :style {}]
                [h-box
                  :gap "4px"
                  :children [[label :label "Time with default range:"]
                             [time-input :model model1]
                             [label :label "entered time: "]
                             [label :label (display-time @model1)]]]
                [gap "14px"]
                [h-box
                  :gap "4px"
                  :children [[label :label "Time with range 06:00:00-21:59:59"]
                             [time-input :model model2
                                :minimum-time {:hour 6 :minute 0}
                                :maximum-time {:hour 21 :minute 59}]
                             [label :label "entered time: "]
                             [label :label (display-time @model2)]]]]]))

(defn time-range []
  (let [range-model (reagent/atom [{:hour 9  :minute 0 :second nil}
                                   {:hour 21 :minute 0 :second nil}])]
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



