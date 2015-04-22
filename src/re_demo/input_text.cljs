(ns re-demo.input-text
  (:require [re-com.core   :refer [h-box v-box box gap line input-text input-textarea label checkbox radio-button slider title p]]
            [re-com.misc   :refer [input-text-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core  :as    reagent]))


(defn input-text-demo
  []
  (let [text-val        (reagent/atom "")
        regex           (reagent/atom nil)
        regex999        #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$"
        status          (reagent/atom nil)
        status-icon?    (reagent/atom false)
        status-tooltip  (reagent/atom "")
        disabled?       (reagent/atom false)
        change-on-blur? (reagent/atom true)
        slider-val      (reagent/atom 4)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title  "[input-text ... ] & [input-textarea ... ]"
                                "src/re_com/misc.cljs"
                                "src/re_demo/input_text.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "Text entry components."]
                                          [p "You can control the input format via " [:code ":validation-regex"] "."]
                                          [p "The " [:code ":on-change"] " function will be called either after each character is entered or on blur."]
                                          [p "Input warnings and errors can be indicated visually by border colors and icons."]
                                          [args-table input-text-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :gap "40px"
                                           :children [[v-box
                                                       :children [[label :label "[input-text ... ]"]
                                                                  [gap :size "5px"]
                                                                  [input-text
                                                                   :model            text-val
                                                                   :status           @status
                                                                   :status-icon?     @status-icon?
                                                                   :status-tooltip   @status-tooltip
                                                                   :width            "300px"
                                                                   :placeholder      (if @regex "enter number (99.9)" "placeholder message")
                                                                   :on-change        #(reset! text-val %)
                                                                   :validation-regex @regex
                                                                   :change-on-blur?  change-on-blur?
                                                                   :disabled?        disabled?]
                                                                  [gap :size "20px"]
                                                                  [label :label "[input-textarea ... ]"]
                                                                  [gap :size "5px"]
                                                                  [input-textarea
                                                                   :model            text-val
                                                                   :status           @status
                                                                   :status-icon?     @status-icon?
                                                                   :status-tooltip   @status-tooltip
                                                                   :width            "300px"
                                                                   :rows             @slider-val
                                                                   :placeholder      (if @regex "enter number (99.9)" "placeholder message")
                                                                   :on-change        #(reset! text-val %)
                                                                   :validation-regex @regex
                                                                   :change-on-blur?  change-on-blur?
                                                                   :disabled?        disabled?]]]
                                                      [v-box
                                                       :gap      "15px"
                                                       :children [[title :level :level3 :label "Callbacks"]
                                                                  [h-box
                                                                   :align    :center
                                                                   :gap      "5px"
                                                                   :children [[:code ":on-change"]
                                                                              " last called with this value: "
                                                                              [:span.bold (if @text-val @text-val "nil")]]]
                                                                  [title :level :level3 :label "Parameters"]
                                                                  [v-box
                                                                   :children [[box :align :start :child [:code ":change-on-blur?"]]
                                                                              [radio-button
                                                                               :label     "false - Call on-change on every keystroke"
                                                                               :value     false
                                                                               :model     @change-on-blur?
                                                                               :on-change #(reset! change-on-blur? false)
                                                                               :style     {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     "true - Call on-change only on blur or Enter key (Esc key resets text)"
                                                                               :value     true
                                                                               :model     @change-on-blur?
                                                                               :on-change #(reset! change-on-blur? true)
                                                                               :style     {:margin-left "20px"}]]]
                                                                  [v-box
                                                                   :children [[box :align :start :child [:code ":status"]]
                                                                              [radio-button
                                                                               :label     "nil/omitted - normal input state"
                                                                               :value     nil
                                                                               :model     @status
                                                                               :on-change #(do
                                                                                            (reset! status nil)
                                                                                            (reset! status-tooltip ""))
                                                                               :style {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     ":warning - border color becomes orange"
                                                                               :value     :warning
                                                                               :model     @status
                                                                               :on-change #(do
                                                                                            (reset! status :warning)
                                                                                            (reset! status-tooltip "Warning tooltip - this (optionally) appears when there are warnings on input-text components."))
                                                                               :style     {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     ":error - border color becomes red"
                                                                               :value     :error
                                                                               :model     @status
                                                                               :on-change #(do
                                                                                            (reset! status :error)
                                                                                            (reset! status-tooltip "Error tooltip - this (optionally) appears when there are errors on input-text components."))
                                                                               :style     {:margin-left "20px"}]]]
                                                                  [h-box
                                                                   :align :start
                                                                   :gap      "5px"
                                                                   :children [[checkbox
                                                                               :label     [:code ":status-icon?"]
                                                                               :model     status-icon?
                                                                               :on-change (fn [val]
                                                                                            (reset! status-icon? val))]
                                                                              [:span " (notice the tooltips on the icons)"]]]

                                                                  [v-box
                                                                   :children [[box :align :start :child [:code ":validation-regex"]]
                                                                              [radio-button
                                                                               :label     "nil/omitted - no character validation"
                                                                               :value     nil
                                                                               :model     @regex
                                                                               :on-change #(do (reset! regex nil)
                                                                                               (reset! text-val ""))
                                                                               :style     {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     "only accept input matching '99.9'"
                                                                               :value     regex999
                                                                               :model     @regex
                                                                               :on-change #(do (reset! regex regex999)
                                                                                               (reset! text-val ""))
                                                                               :style     {:margin-left "20px"}]]]
                                                                  [checkbox
                                                                   :label     [box :align :start :child [:code ":disabled?"]]
                                                                   :model     disabled?
                                                                   :on-change (fn [val]
                                                                                (reset! disabled? val))]
                                                                  [h-box
                                                                   :gap "10px"
                                                                   :children [[h-box
                                                                               :align    :start
                                                                               :children [[:code ":rows"]
                                                                                          "(textarea)"]]
                                                                              [slider
                                                                               :model     slider-val
                                                                               :min       1
                                                                               :max       10
                                                                               :width     "200px"
                                                                               :on-change #(reset! slider-val %)]
                                                                              [label :label @slider-val]]]]]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [input-text-demo])
