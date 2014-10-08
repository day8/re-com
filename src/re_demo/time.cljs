(ns re-demo.time
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer  [label]]
            [re-com.util     :refer  [pad-zero-number]]
            [re-com.time     :refer  [time-input time-range-input]]
            [re-com.box      :refer  [h-box v-box box gap]]
            [re-com.dropdown :refer  [single-dropdown]]
            [reagent.core    :as     reagent]))

(defn display-time
  "Return a string display of the time."
  [time-int]
  (let [hour (quot time-int 100)
        min  (rem  time-int 100)]
    (str (pad-zero-number hour 2)
         ":"
         (pad-zero-number min 2))))

(def demos [{:id "1" :label "Nil model"}
            {:id "2" :label "No border"}
            {:id "3" :label "Disabled"}
            {:id "4" :label "With icon"}
            {:id "5" :label "Custom min & max"}
            {:id "6" :label "Range"}
            {:id "7" :label "Range with labels"}
            {:id "8" :label "Range with other options"}])

(defn notes
  []
  [v-box
   :width    "500px"
   :children [[:div.h4 "General notes:"]
              [:div {:style {:font-size "small"}}
               [:p "Accepts input of a time in 24hr format."]
               [:p " Only allows input of numbers and ':'. Limits input to valid values (e.g. does not allow input of '999').
               Also, attempts to interpret input e.g. '123' interpretted as '1:23'."]
               [:p "The "[:code ":model"] " is expected to be an integer in HHMM form. e.g. a time of '18:30' is the integer 1830.
                The same applies to "[:code ":minimum"] " and "[:code ":maximum"] "."]
               [:p "When the component loses focus, the " [:code ":on-change"] " callback is called with an integer of the same form."]
               [:p "If the entered value is invalid it will be replaced with the last valid value."]
               [:p "If "[:code ":model"] " is invalid an exception will be thrown."]
               [:div.h4 "Parameters"]
               [:label {:style {:font-variant "small-caps"}} "required"]
                [:ul
                 [:li [:code ":model"] " - an integer time e.g. 930"]]
               [:label {:style {:font-variant "small-caps"}} "optional"]
               [:ul
                  [:li [:code ":minimum"] " - min time as an integer e.g.  930 - will not allow input less than this time - default 0."]
                  [:li [:code ":maximum"] " - max time as an integer e.g. 1400 - will not allow input more than this time - default 2359."]
                  [:li [:code ":on-change"] " - function to call upon change."]
                  [:li [:code ":disabled"] " - true if the component should be disabled - default false"]
                  [:li [:code ":hide-border"] " - true if the time input should be displayed without a border - default false"]
                  [:li [:code ":show-time-icon"] " - true if the clock icon should be displayed - default false"]
                  [:li [:code ":style"] " - css style"]]
               [:label {:style {:font-variant "small-caps"}} "optional (range only)"]
               [:ul
                  [:li [:code ":from-label"] " - label to appear before the From input."]
                  [:li [:code ":to-label"] " - label to appear before the To input."]
                  [:li [:code ":gap"] " - gap between the input boxes (disregarding "[:code ":to-label"]"). Default is 4px."]]]]])

;; TODO write a macro to convert the demo source to actual code - see time-input-demo and time-input-code in each demo
;; TODO is it possible to use time-api to define demo parameter documentation?

(defn demo1
  []
  (let [model (reagent/atom nil)
        time-input-demo  [time-input :model model]
        time-input-code "(let [model  (reagent/atom nil)]
  [time-input :model model])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Nil model"]
                     [:label "Usage -"]
                     [:pre [:code time-input-code]]
                     [:ul
                       [:li "Because "[:code ":model"] " is nil the input will initially be blank."]]
                     [:label "Demo -"]
                     time-input-demo]]])))

(defn demo2
  []
  (let [model  (reagent/atom 600)
        time-input-demo  [time-input :model model              :hide-border true]
        time-input-code "(let [model  (reagent/atom 600)]
  [time-input :model (reagent/atom 600) :hide-border true}])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :style {:font-size "small"}
          :children [[:div.h4 "No border"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li [:code ":hide-border true"] " - hides the input border"]]
                   [:label "Demo -"]
                   time-input-demo]]])))

(defn demo3
  []
  (let [model  (reagent/atom 923)
        time-input-demo  [time-input :model model :disabled true]
        time-input-code "(let [model  (reagent/atom 923)]
  [time-input :model (reagent/atom 0) :disabled true])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Disabled"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li "Note use of "[:code ":disabled true"]]]
                   [:label "Demo -"]
                   time-input-demo]]])))

(defn demo4
  []
  (let [model  (reagent/atom 900)
        time-input-demo  [time-input
                            :model model
                            :on-change #(reset! model %)
                            :show-time-icon true]
        time-input-code "(let [model  (reagent/atom 900)]
  [time-input
    :model model
    :on-change #(reset! model %)
    :show-time-icon true])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "With icon"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li [:code ":show-time-icon true"] " - displays clock icon next to input"]]
                   [:label "Demo -"]
                   time-input-demo]]])))

(defn demo5
  []
  (let [model  (reagent/atom 900)
        time-input-demo [time-input
                          :model model
                          :on-change #(reset! model %)
                          :minimum 600
                          :maximum 2130]
        time-input-code "(let [model  (reagent/atom 900)]
  [time-input
    :model model
    :on-change #(reset! model %)
    :minimum 600
    :maximum 2130])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "With custom range: 06:00-21:30"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li [:code ":on-change"] " - update the model's value"]
                     [:li [:code ":minimum 600"] " - minimum time is '06:00'"]
                     [:li [:code ":maximum 2130"] " - maximum time is '21:30'"]]
                   [:label "Demo -"]
                   [:p "Try typing a value outside the range."]
                   time-input-demo]]])))

(defn demo6
  []
  (let [model  (reagent/atom [900 2300])
        time-input-demo [time-range-input
                          :model model
                          :on-change #(reset! model %)
                          :minimum 600
                          :maximum 2359
                          :to-label "-"]
        time-input-code "(let [model  (reagent/atom [900 2300])]
  [time-range-input
    :model model
    :on-change #(reset! model %)
    :minimum 600
    :maximum 2359]
    :to-label \"-\"])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Range"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li "Note that "[:code ":model"] " for a range must contain a vector of TWO integers"]
                     [:li [:code ":to-label \"-\""] " puts a dash between the From and To input boxes"]]
                   [:label "Demo -"]
                   [:p "The From time must be less than or equal to the To time and both must be within min and max."]
                   time-input-demo]]])))

(defn demo7
  []
  (let [model  (reagent/atom [1000 2159])
        time-input-demo [time-range-input
                          :model model
                          :on-change #(reset! model %)
                          :minimum 600
                          :maximum 2200
                          :show-time-icon true
                          :from-label "From:"
                          :to-label "To:"]
        time-input-code "(let [model  (reagent/atom [1000 2159])]
  [time-range-input
    :model model
    :on-change #(reset! model %)
    :minimum 600
    :maximum 2200]
    :show-time-icon true
    :from-label \"From:\")
    :to-label \"To:\"])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Range with labels"]
                     [:label "Usage -"]
                     [:pre [:code time-input-code]]
                     [:ul
                       [:li "Note that "[:code ":model"] " for a range must contain a vector of TWO integers"]
                       [:li [:code ":to-label \"From:\""] " puts a label before the From input box"]
                       [:li [:code ":to-label \"To:\""] " puts a label before the To input box"]]
                     [:label "Demo -"]
                     [:p "The From time must be less than or equal to the To time and both must be within min and max."]
                     time-input-demo]]])))

(defn demo8
  []
  (let [model  (reagent/atom [1000 2200])
        time-input-demo [time-range-input
                          :model model
                          :on-change #(reset! model %)
                          :minimum 1000
                          :maximum 2200
                          :hide-border true
                          :disabled true
                          :to-label "-"]
        time-input-code "(let [model  (reagent/atom [1000 2200])]
  [time-range-input
    :model model
    :on-change #(reset! model %)
    :minimum 1000
    :maximum 2200]
    :hide-border true
    :disabled true
    :to-label \"-\"])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Range with other options"]
                     [:label "Usage -"]
                     [:pre [:code time-input-code]]
                     [:ul
                       [:li "Note that "[:code ":model"] " for a range must contain a vector of TWO integers"]]
                     [:label "Demo -"]
                     [:p "This example shows how to set the other options."]
                     time-input-demo]]])))

(defn panel
  []
  (let [selected-demo-id (reagent/atom "1")]
    (fn [] [v-box
            :children [[:h3.page-header "Time Input"]
                       [h-box
                        :gap      "50px"
                        :children [[notes]
                                   [v-box
                                    :gap       "15px"
                                    :size      "auto"
                                    :min-width "500px"
                                    :children  [[h-box
                                                :gap      "10px"
                                                :align    :center
                                                :children [[label :label "Select a demo"]
                                                           [single-dropdown
                                                            :options   demos
                                                            :model     selected-demo-id
                                                            :width     "300px"
                                                            :on-select #(reset! selected-demo-id %)]]]
                                               [gap :size "0px"] ;; Force a bit more space here
                                               (case @selected-demo-id
                                                 "1" [demo1]
                                                 "2" [demo2]
                                                 "3" [demo3]
                                                 "4" [demo4]
                                                 "5" [demo5]
                                                 "6" [demo6]
                                                 "7" [demo7]
                                                 "8" [demo8])]]]]]])))



