(ns re-demo.input-time
  (:require [re-com.core       :refer [at h-box v-box box gap input-time label title button checkbox p]]
            [re-com.input-time :refer [input-time-parts-desc input-time-args-desc]]
            [re-demo.utils     :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util       :refer [px]]
            [reagent.core      :as    reagent]))


(defn- simulated-bools
  [disabled? hide-border? show-icon?]
  [v-box :src (at)
   :gap "20px"
   :align :start
   :children [[h-box :src (at)
               :gap "15px"
               :align :start
               :children [[checkbox :src (at)
                           :label [box :src (at) :align :start :child [:code ":disabled?"]]
                           :model @disabled?
                           :on-change #(reset! disabled? %)]
                          [checkbox :src (at)
                           :label [box :src (at) :align :start :child [:code ":hide-border?"]]
                           :model @hide-border?
                           :on-change #(reset! hide-border? %)]
                          [checkbox :src (at)
                           :label [box :src (at) :align :start :child [:code ":show-icon?"]]
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
      [v-box :src (at)
       :gap "10px"
       :children [[title2 "Demo"]
                  [:p "There are two instances of this component below."]
                  [:p "The first one is the default size."]
                  [:p "The second one specifies " [:code ":style {:font-size \"11px\"}"] " to make a smaller version."]
                  [gap :src (at) :size "20px"]
                  [v-box :src (at)
                   :children [[v-box :src (at)
                               :width    "140px"
                               :gap      "30px"
                               :children [[input-time :src (at)
                                           :model        an-int-time
                                           :minimum      @minimum
                                           :maximum      @maximum
                                           :on-change    #(reset! an-int-time %)
                                           :disabled?    disabled?
                                           :hide-border? @hide-border?
                                           :show-icon?   @show-icon?]
                                          [input-time :src (at)
                                           :model        an-int-time
                                           :minimum      @minimum
                                           :maximum      @maximum
                                           :on-change    #(reset! an-int-time %)
                                           :disabled?    disabled?
                                           :hide-border? @hide-border?
                                           :show-icon?   @show-icon?
                                           :style        {:font-size "11px"}]]]
                              [gap :src (at) :size "30px"]
                              [v-box :src (at)
                               :gap      "10px"
                               :style {:min-width        "550px"
                                       :padding          "15px"
                                       :border-top       "1px solid #DDD"
                                       :background-color "#f7f7f7"}
                               :children [[title :src (at) :level :level3 :label "Interactive Parameters" :style {:margin-top "0"}]
                                          [simulated-bools disabled? hide-border? show-icon?]
                                          [gap :src (at) :size "20px"]
                                          [title :src (at) :level :level3 :label "Model resets"]
                                          [h-box :src (at)
                                           :gap "10px"
                                           :align :center
                                           :children [[button :src (at)
                                                       :label    "11am"
                                                       :class    "btn btn-default"
                                                       :on-click #(reset! an-int-time 1100)]
                                                      [button :src (at)
                                                       :label    "5pm"
                                                       :class    "btn btn-default"
                                                       :on-click #(reset! an-int-time 1700)]]]
                                          [gap :src (at) :size "20px"]
                                          [title :src (at) :level :level3 :label "Simulated minimum & maximum changes"]
                                          [h-box :src (at)
                                           :gap      "10px"
                                           :align    :center
                                           :children [[label :src (at) :label ":minimum"]
                                                      [label :src (at) :label @minimum :style {:width "40px" :font-size "11px" :text-align "center"}]
                                                      [label :src (at) :label ":maximum"]
                                                      [label :src (at) :label @maximum :style {:width "40px" :font-size "11px" :text-align "center"}]]]
                                          [h-box :src (at)
                                           :gap      "10px"
                                           :align    :center
                                           :children [[checkbox :src (at)
                                                       :label     [box :src (at) :align :start :child [:code ":minimum 10am"]]
                                                       :model     (not= @minimum init-minimum)
                                                       :on-change #(reset! minimum (if % 1000 init-minimum))]
                                                      [checkbox :src (at)
                                                       :label     [box :src (at) :align :start :child [:code ":maximum 2pm"]]
                                                       :model     (not= @maximum init-maximum)
                                                       :on-change #(reset! maximum (if % 1400 init-maximum))]]]]]]]]])))

(defn panel2
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[input-time ... ]"
                            "src/re_com/input_time.cljs"
                            "src/re_demo/input_time.cljs"]
              [h-box :src (at)
               :gap "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "Allows the user to input time in 24hr format."]
                                      [p "Filters out all keystrokes other than numbers and ':'. Attempts to limit input to valid values.
                                            Provides interpretation of incomplete input, for example '123' is interpreted as '1:23'."]
                                      [p "If the user exits the input field with an invalid value, it will be replaced with the last known valid value."]
                                      [args-table input-time-args-desc]]]
                          [basics-demo]]]
              [parts-table "input-time" input-time-parts-desc]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])

