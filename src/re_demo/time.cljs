(ns re-demo.time
  (:require [re-com.core     :refer  [label checkbox input-text title]]
            [re-com.buttons  :refer  [button]]
            [re-com.time     :refer  [input-time input-time-args-desc]]
            [re-com.box      :refer  [h-box v-box box gap]]
            [re-demo.utils   :refer  [panel-title component-title args-table]]
            [reagent.core    :as     reagent]))


(defn notes
  []
  [v-box
   :width    "450px"
   :children [ [:div.h4 "Notes"]
               [:div {:style {:font-size "small"}}
               [:p "Allows the user to input time in 24hr format."]
                [:p "Filters out all keystrokes other than numbers and ':'. Attempts to limit input to valid values.
                 Provides interpretation of incomplete input, for example '123' is interpretted as '1:23'."]
                [:p "If the user exists the input field with an invalid value, it will be replaced with the last known valid value."]]

               [args-table input-time-args-desc]]])


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
                           :label ":disabled?"
                           :label-style check-style
                           :model @disabled?
                           :on-change #(reset! disabled? %)]
                          [checkbox
                           :label ":hide-border?"
                           :label-style check-style
                           :model @hide-border?
                           :on-change #(reset! hide-border? %)]
                          [checkbox
                           :label ":show-icon?"
                           :label-style check-style
                           :model @show-icon?
                           :on-change #(reset! show-icon? %)]]]]])

(defn basics-demo
  []
  (let [disabled?    (reagent/atom false)
        hide-border? (reagent/atom false)
        show-icon?   (reagent/atom true)
        an-int-time  (reagent/atom 900)                      ;; start at 9am
        init-minimum 0
        minimum      (reagent/atom init-minimum)
        init-maximum 2359
        maximum      (reagent/atom init-maximum)]
    (fn []
      [v-box
       :gap "20px"
       :children [[:div.h4 "Demo"]
                  [h-box
                   :gap "40px"
                   :style {:font-size "small"}
                   :children [[input-time
                               :model an-int-time
                               :minimum @minimum
                               :maximum @maximum
                               :on-change #(reset! an-int-time %)
                               :disabled? disabled?
                               :hide-border? @hide-border?
                               :show-icon? @show-icon?
                               :style   {:width "50px"}]
                              [v-box
                               :gap "10px"
                               :children [[label :style {:font-style "italic"} :label "simulated boolean parameters:"]
                                          [simulated-bools disabled? hide-border? show-icon?]
                                          [gap :size "20px"]
                                          [label :style {:font-style "italic"} :label "simulated model resets:"]
                                          [h-box
                                           :gap "10px"
                                           :align :center
                                           :children [[button
                                                       :label "11am"
                                                       :class "btn btn-xs"
                                                       :on-click #(reset! an-int-time 1100)]
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
   :children [[panel-title "Time Components"]
              [h-box
               :gap "50px"
               :children [[component-title "[input-time ... ]"]
                          [notes]
                          [basics-demo]]]]])



