(ns re-demo.input-time
  (:require [re-com.core       :refer [h-box v-box box gap input-time label title button checkbox p]]
            [re-com.input-time :refer [input-time-args-desc]]
            [re-demo.utils     :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core      :as    reagent]))


(defn- simulated-bools
  [disabled? hide-border? show-icon?]
  [v-box
   :gap "20px"
   :align :start
   :children [[h-box
               :gap "15px"
               :align :start
               :children [[checkbox
                           :label [box :align :start :child [:code ":disabled?"]]
                           :model @disabled?
                           :on-change #(reset! disabled? %)]
                          [checkbox
                           :label [box :align :start :child [:code ":hide-border?"]]
                           :model @hide-border?
                           :on-change #(reset! hide-border? %)]
                          [checkbox
                           :label [box :align :start :child [:code ":show-icon?"]]
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
       :gap "10px"
       :children [[title2 "Demo"]
                   [:p "There are two instances of this component below."]
                   [:p "The first one is the default size."]
                   [:p "The second one specifies " [:code ":style {:font-size \"11px\"}"] " to make a smaller version."]
                  [gap :size "20px"]
                  [h-box
                   :children [[v-box
                               :width    "140px"
                               :gap      "30px"
                               :children [[input-time
                                           :model        an-int-time
                                           :minimum      @minimum
                                           :maximum      @maximum
                                           :on-change    #(reset! an-int-time %)
                                           :disabled?    disabled?
                                           :hide-border? @hide-border?
                                           :show-icon?   @show-icon?]
                                          [input-time
                                           :model        an-int-time
                                           :minimum      @minimum
                                           :maximum      @maximum
                                           :on-change    #(reset! an-int-time %)
                                           :disabled?    disabled?
                                           :hide-border? @hide-border?
                                           :show-icon?   @show-icon?
                                           :style        {:font-size "11px"}]]]
                              [v-box
                               :gap      "10px"
                               :children [[title :level :level3 :label "Parameters"]
                                          [simulated-bools disabled? hide-border? show-icon?]
                                          [gap :size "20px"]
                                          [title :level :level3 :label "Model resets"]
                                          [h-box
                                           :gap "10px"
                                           :align :center
                                           :children [[button
                                                       :label    "11am"
                                                       :class    "btn btn-default"
                                                       :on-click #(reset! an-int-time 1100)]
                                                      [button
                                                       :label    "5pm"
                                                       :class    "btn btn-default"
                                                       :on-click #(reset! an-int-time 1700)]]]
                                          [gap :size "20px"]
                                          [title :level :level3 :label "Simulated minimum & maximum changes"]
                                          [h-box
                                           :gap      "10px"
                                           :align    :center
                                           :children [[label :label ":minimum"]
                                                      [label :label @minimum :style {:width "40px" :font-size "11px" :text-align "center"}]
                                                      [label :label ":maximum"]
                                                      [label :label @maximum :style {:width "40px" :font-size "11px" :text-align "center"}]]]
                                          [h-box
                                           :gap      "10px"
                                           :align    :center
                                           :children [[checkbox
                                                       :label     [box :align :start :child [:code ":minimum 10am"]]
                                                       :model     (not= @minimum init-minimum)
                                                       :on-change #(reset! minimum (if % 1000 init-minimum))]
                                                      [checkbox
                                                       :label     [box :align :start :child [:code ":maximum 2pm"]]
                                                       :model     (not= @maximum init-maximum)
                                                       :on-change #(reset! maximum (if % 1400 init-maximum))]]]]]]]]])))


(defn panel2
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[input-time ... ]"
                            "src/re_com/input_time.cljs"
                            "src/re_demo/input_time.cljs"]
              [h-box
               :gap "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                       [p "Allows the user to input time in 24hr format."]
                                       [p "Filters out all keystrokes other than numbers and ':'. Attempts to limit input to valid values.
                                            Provides interpretation of incomplete input, for example '123' is interpretted as '1:23'."]
                                       [p "If the user exits the input field with an invalid value, it will be replaced with the last known valid value."]
                                      [args-table input-time-args-desc]]]
                          [basics-demo]]]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
