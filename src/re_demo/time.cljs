(ns re-demo.time
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer [label]]
            [re-com.util     :refer  [pad-zero-number]]
            [re-com.time     :refer  [time-input]]
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

(defn time-examples []
  (let [model1 (reagent/atom 900)
        model2 (reagent/atom 2030)]
    [v-box
     :gap "0px"
     :children [[title "Time"]
                [:p "Accepts input of a time. Model does not update until input loses focus. If the entered value is invalid it will be ignored."]
                [:p "Required parameters are -"]
                [:ul
                  [:li "model - an integer time e.g. 930"]]
                [:p "Optional parameters are -"]
                [:ul
                  [:li "minimum - min time as an integer e.g.  930 - will not allow input less than this time - default 0."]
                  [:li "maximum - max time as an integer e.g. 1400 - will not allow input more than this time - default 2359."]
                  [:li "on-change - function to call upon change."]
                  [:li "disabled - true if the component should be disabled - default false"]
                  [:li "show-time-icon - true if the clock icon should be displayed - default false"]
                  [:li "style - css style"]]
                [label :label "Examples" :style {:font-weight "bold"}]
                [label :label "Basic time input, empty model"]
                [time-input
                  :model (reagent/atom nil)
                  :style {}]
                [label :label "Basic time input, with model, no border"]
                [time-input
                  :model (reagent/atom 0)
                  :style {:border "none"}]
                [label :label "Disabled time input"]
                [time-input
                 :model (reagent/atom nil)
                  :disabled true]
                [label :label "Time with default range and icon:"]
                [time-input :model model1 :on-change #(reset! model1 %) :show-time-icon true]
                [label :label "Time with range 06:00-21:59"]
                [time-input
                  :model model2
                  :minimum 600
                  :maximum 2159
                  :on-change #(reset! model2 %)]]]))

(defn panel
  []
  [v-box
   :children [[time-examples]]])



