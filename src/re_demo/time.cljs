(ns re-demo.time
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer  [label button checkbox input-text]]
            [re-com.time     :refer  [time-input]]
            [re-com.box      :refer  [h-box v-box box gap]]
            [reagent.core    :as     reagent]))


(defn notes
  []
  [v-box
   :width    "500px"
   :children [[:div.h4 "General notes:"]
              [:div {:style {:font-size "small"}}
               [:ul
                [:li "Accepts input of a time in 24hr format."
                 " Only allows input of numbers and ':'. Limits input to valid values (e.g. does not allow input of '999').
                 Also, attempts to interpret input e.g. '123' interpretted as '1:23'."]
                [:li "The "[:code ":model"] " is expected to be an integer in HHMM form. e.g. a time of '18:30' is the integer 1830.
                  The same applies to "[:code ":minimum"] " and "[:code ":maximum"] ". The model can also be an atom containing such an integer."]
                [:li "When the component loses focus, the " [:code ":on-change"] " callback is called with an integer of the same form."]
                [:li "If the entered value is invalid it will be replaced with the last valid value."]
                [:li "If "[:code ":model"] " is invalid an exception will be thrown."]]
               [:label {:style {:font-variant "small-caps"}} "required"]
               [:ul
                [:li [:code ":model"] " - an integer time e.g. 930"]]
               [:label {:style {:font-variant "small-caps"}} "optional"]
               [:ul
                [:li [:code ":minimum"] " - min time as an integer e.g.  930 - will not allow input less than this time - default 0."]
                [:li [:code ":maximum"] " - max time as an integer e.g. 1400 - will not allow input more than this time - default 2359."]
                [:li [:code ":on-change"] " - function to call upon change."]
                [:li [:code ":disabled"] " - true if the component should be disabled - default false. Can also be an atom containing a boolean."]
                [:li [:code ":hide-border"] " - true if the time input should be displayed without a border - default false"]
                [:li [:code ":show-icon"] " - true if the clock icon should be displayed - default false"]
                [:li [:code ":class"] " - class for styling"]
                [:li [:code ":style"] " - css style"]]]]])

(def check-style {:font-size "small" :margin-top "1px"})

(defn- simulated-bools
  [disabled? hide-border? show-icon?]
  [v-box
   :gap "20px"
   :align :start
   :children [[h-box
               :gap "15px"
               :align :start
               :children [[checkbox
                           :label ":disabled"
                           :label-style check-style
                           :model @disabled?
                           :on-change #(reset! disabled? %)]
                          [checkbox
                           :label ":hide-border"
                           :label-style check-style
                           :model @hide-border?
                           :on-change #(reset! hide-border? %)]
                          [checkbox
                           :label ":show-icon"
                           :label-style check-style
                           :model @show-icon?
                           :on-change #(reset! show-icon? %)]]]]])

(defn basics-demo
  []
  (let [disabled? (reagent/atom false)
        hide-border? (reagent/atom false)
        show-icon? (reagent/atom false)
        an-int-time (reagent/atom 900)                      ;; starts at 9am
        init-minimum 0
        minimum (reagent/atom init-minimum)
        init-maximum 2359
        maximum (reagent/atom init-maximum)
        ]
    (fn []
      [v-box
       :gap "20px"
       :children [[:div.h4 "Demo"]
                  [h-box
                   :gap "50px"
                   :style {:font-size "small"}
                   :children [[time-input
                               :model an-int-time
                               :minimum @minimum
                               :maximum @maximum
                               :on-change #(reset! an-int-time %)
                               :disabled disabled?
                               :hide-border @hide-border?
                               :show-icon @show-icon?]
                              [v-box
                               :gap "10px"
                               :children [[label :style {:font-style "italic"} :label "simulated boolean parameters:"]
                                          [simulated-bools disabled? hide-border? show-icon?]
                                          [gap :size "20px"]
                                          [label :style {:font-style "italic"} :label "simulated model changes:"]
                                          [h-box
                                           :gap "10px"
                                           :align :center
                                           :children [[button
                                                       :label "7am"
                                                       :class "btn btn-xs"
                                                       :on-click #(reset! an-int-time 700)]
                                                      [button
                                                       :label "5pm"
                                                       :class "btn btn-xs"
                                                       :on-click #(reset! an-int-time 1700)]]]
                                          [gap :size "20px"]
                                          [label :style {:font-style "italic"} :label "simulated minimum & maximum changes:"]
                                          [h-box
                                           :gap "10px"
                                           :align :center
                                           :children [[label :label ":minimum" :style {:font-size "small"}]
                                                      [label :label @minimum :style {:width "40px" :font-size "11px" :text-align "center"}]
                                                      [label :label ":maximum" :style {:font-size "small"}]
                                                      [label :label @maximum :style {:width "40px" :font-size "11px" :text-align "center"}]]]
                                          [h-box
                                           :gap "10px"
                                           :align :center
                                           :children [[checkbox
                                                       :label ":minimum 10am"
                                                       :label-style check-style
                                                       :model (not= @minimum init-minimum)
                                                       :on-change #(reset! minimum (if % 1000 init-minimum))]
                                                      [checkbox
                                                       :label ":maximum 2pm"
                                                       :label-style check-style
                                                       :model (not= @maximum init-maximum)
                                                       :on-change #(reset! maximum (if % 1400 init-maximum))]]]
                                          ]]]]]])))


(defn panel
  []
  [v-box
   :children [[:h3.page-header "Time Input"]
              [h-box
               :gap "50px"
               :children [[notes]
                          [basics-demo]]]]])



