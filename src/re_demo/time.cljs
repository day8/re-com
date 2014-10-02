(ns re-demo.time
  (:require [re-demo.util    :refer  [title]]
            [re-com.core     :refer  [label]]
            [re-com.util     :refer  [pad-zero-number]]
            [re-com.time     :refer  [time-input]]
            [re-com.box      :refer  [h-box v-box box gap line]]
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

(def demos [{:id "1" :label "Time input, empty model"}
            {:id "2" :label "Time input, no border"}
            {:id "3" :label "Disabled time input"}
            {:id "4" :label "Time with icon"}
            {:id "5" :label "Time with range 06:00-21:59"}])

(defn notes
  []
  [v-box
   :width    "500px"
   :children [[:div.h4 "General notes:"]
              [:div {:style {:font-size "small"}}
               [:p "Accepts input of a time. The callback is triggered when the input loses focus. If the entered value is invalid it will be ignored."]
               [:p "Times are passed to the component and returned in the callback as integers e.g. 1830 is '18:30'."]
               [:p "If invalid data is provided in the model or other parameters, an exception will be thrown."]
               [:div.h4 "Parameters"]
               [:label {:style {:font-variant "small-caps"}} "required"]
                [:ul
                 [:li.spacer [:strong ":model"] " - an integer time e.g. 930"]]
               [:label {:style {:font-variant "small-caps"}} "optional"]
                [:ul
                  [:li.spacer [:strong ":minimum"] " - min time as an integer e.g.  930 - will not allow input less than this time - default 0."]
                  [:li.spacer [:strong ":maximum"] " - max time as an integer e.g. 1400 - will not allow input more than this time - default 2359."]
                  [:li.spacer [:strong ":on-change"] " - function to call upon change."]
                  [:li.spacer [:strong ":disabled"] " - true if the component should be disabled - default false"]
                  [:li.spacer [:strong ":show-time-icon"] " - true if the clock icon should be displayed - default false"]
                  [:li.spacer [:strong ":style"] " - css style"]]]]])

;; TODO write a macro to convert the demo source to actual code - see time-input-demo and time-input-code in each demo

(defn demo1
  []
  (let [model (reagent/atom nil)
        time-input-demo  [time-input :model model]
        time-input-code "[time-input :model (reagent/atom nil)]"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Time input - empty model"]
                     [:label "Usage -"]
                     [:pre [:code time-input-code]]
                     [:ul
                       [:li [:strong ":model"] " - atom on nil"]
                       [:li [:strong ":minimum"] " - nil - (default 0)"]
                       [:li [:strong ":maximum"] " - nil - (default 2359)"]]
                     [:label "Demo -"]
                     time-input-demo]]])))

(defn demo2
  []
  (let [model  (reagent/atom 600)
        time-input-demo  [time-input :model model              :style {:border "none"}]
        time-input-code "[time-input :model (reagent/atom 600) :style {:border \"none\"}]"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :style {:font-size "small"}
          :children [[:div.h4 "Time input - model: 600"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li [:strong ":model"] " - atom on 600"]
                     [:li [:strong ":style"] " - map with: {:border \"none\"}"]
                     [:li [:strong ":minimum"] " - nil - (default 0)"]
                     [:li [:strong ":maximum"] " - nil - (default 2359)"]]
                   [:label "Demo -"]
                   time-input-demo]]])))

(defn demo3
  []
  (let [model  (reagent/atom 0)
        time-input-demo  [time-input :model model            :disabled true]
        time-input-code "[time-input :model (reagent/atom 0) :disabled true]"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Disabled time input - model: 0"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li [:strong ":model"] " - atom on nil"]
                     [:li [:strong ":disabled"] " - true"]
                     [:li [:strong ":minimum"] " - nil -(default 0)"]
                     [:li [:strong ":maximum"] " - nil - (default 2359)"]]
                   [:label "Demo -"]
                   time-input-demo]]])))

(defn demo4
  []
  (let [model  (reagent/atom 900)
        time-input-demo  [time-input :model model              :on-change #(reset! model %) :show-time-icon true]
        time-input-code "(let [model  (reagent/atom 900)]
  [time-input
    :model model
    :on-change #(reset! model %)
    :show-time-icon true])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Time input with icon - model: 900"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li [:strong ":model"] " - atom on 900"]
                     [:li [:strong ":on-change"] " - update the model with the new value"]
                     [:li [:strong ":minimum"] " - (default) 0"]
                     [:li [:strong ":maximum"] " - (default) 2359"]]
                   [:label "Demo -"]
                   time-input-demo]]])))

(defn demo5
  []
  (let [model  (reagent/atom 900)
        time-input-demo [time-input
                          :model model
                          :on-change #(reset! model %)
                          :minimum 600
                          :maximum 2159]
        time-input-code "(let [model  (reagent/atom 900)]
  [time-input
    :model model
    :on-change #(reset! model %)
    :minimum 600
    :maximum 2159])"]
    (fn []
      [:div {:style {:font-size "small"}}
        [v-box
          :children [[:div.h4 "Time input - model: 900 - range: 06:00-21:59"]
                   [:label "Usage -"]
                   [:pre [:code time-input-code]]
                   [:ul
                     [:li [:strong ":model"] " - atom on 900"]
                     [:li [:strong ":on-change"] " - update the model with the new value"]
                     [:li [:strong ":minimum"] " - 600"]
                     [:li [:strong ":maximum"] " - 2159"]]
                   [:label "Demo -"]
                   [:p "Try typing a value outside the range."]
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
                                                 "5" [demo5])]]]]]])))



