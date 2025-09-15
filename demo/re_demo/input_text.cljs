(ns re-demo.input-text
  (:require [re-com.core       :refer [at h-box v-box box gap line input-text input-password input-textarea label checkbox radio-button slider title p]]
            [re-com.input-text :refer [input-text-parts-desc input-text-args-desc]]
            [re-demo.utils     :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util       :refer [px]]
            [clojure.string    :as    string]
            [reagent.core      :as    reagent]))

(defn input-text-demo
  []
  (let [text-val        (reagent/atom nil)
        regex           (reagent/atom nil)
        regex999        #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$"
        on-alter?       (reagent/atom false)
        status          (reagent/atom nil)
        status-icon?    (reagent/atom false)
        status-tooltip  (reagent/atom "")
        disabled?       (reagent/atom false)
        change-on-blur? (reagent/atom true)
        slider-val      (reagent/atom 4)]
    (fn
      []
      [v-box :src (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title  "Input Text Components"
                   "src/re_com/input_text.cljs"
                   "demo/re_demo/input_text.cljs"]

                  [h-box :src (at)
                   :gap      "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "Text entry components."]
                                          [p "You can control the input format via " [:code ":validation-regex"] "."]
                                          [p "The " [:code ":on-change"] " function will be called either after each character is entered or on blur."]
                                          [p "Input warnings and errors can be indicated visually by border colors and icons."]
                                          [args-table input-text-args-desc]]]
                              [v-box :src (at)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [label :src (at) :label "[input-text ... ]"]
                                          [gap :src (at) :size "5px"]
                                          [input-text :src (at)
                                           :model            text-val
                                           :status           @status
                                           :status-icon?     @status-icon?
                                           :status-tooltip   @status-tooltip
                                           :width            "300px"
                                           :placeholder      (if @regex "enter number (99.9)" "placeholder message")
                                           :on-change        #(reset! text-val %)
                                           :validation-regex @regex
                                           :on-alter         (if @on-alter? string/upper-case identity)
                                           :change-on-blur?  change-on-blur?
                                           :disabled?        disabled?]
                                          [gap :src (at) :size "20px"]
                                          [label :src (at) :label "[input-password ... ]"]
                                          [gap :src (at) :size "5px"]
                                          [input-password :src (at)
                                           :model            text-val
                                           :status           @status
                                           :status-icon?     @status-icon?
                                           :status-tooltip   @status-tooltip
                                           :width            "300px"
                                           :placeholder      (if @regex "enter number (99.9)" "placeholder message")
                                           :on-change        #(reset! text-val %)
                                           :validation-regex @regex
                                           :on-alter         (if @on-alter? string/upper-case identity)
                                           :change-on-blur?  change-on-blur?
                                           :disabled?        disabled?]
                                          [gap :src (at) :size "20px"]
                                          [label :src (at) :label "[input-textarea ... ]"]
                                          [gap :src (at) :size "5px"]
                                          [input-textarea :src (at)
                                           :model            text-val
                                           :status           @status
                                           :status-icon?     @status-icon?
                                           :status-tooltip   @status-tooltip
                                           :width            "300px"
                                           :rows             @slider-val
                                           :placeholder      (if @regex "enter number (99.9)" "placeholder message")
                                           :on-change        #(reset! text-val %)
                                           :validation-regex @regex
                                           :on-alter         (if @on-alter? string/upper-case identity)
                                           :change-on-blur?  change-on-blur?
                                           :disabled?        disabled?]
                                          [gap :src (at) :size "20px"]
                                          [label :src (at) :label "[input-text w/ alpha filter in on-change ... ]"]
                                          [gap :src (at) :size "5px"]
                                          [input-text :src (at)
                                           :model            text-val
                                           :status           @status
                                           :status-icon?     @status-icon?
                                           :status-tooltip   @status-tooltip
                                           :width            "300px"
                                           :placeholder      (if @regex "enter number (99.9)" "placeholder message")
                                           :on-change        (fn [v resolve-fn]
                                                               (let [v (string/replace v #"[^a-zA-Z]" "")]
                                                                 (reset! text-val v)
                                                                 (resolve-fn)))
                                           :validation-regex @regex
                                           :on-alter         (if @on-alter? string/upper-case identity)
                                           :change-on-blur?  change-on-blur?
                                           :disabled?        disabled?]
                                          [title :src (at) :level :level3 :label "Callbacks"]
                                          [h-box :src (at)
                                           :align    :center
                                           :gap      "5px"
                                           :children [[:code ":on-change"]
                                                      " last called with this value: "
                                                      [:span.bold (if @text-val @text-val "nil")]]]
                                          [v-box :src (at)
                                           :gap "15px"
                                           :style    {:min-width        "150px"
                                                      :padding          "15px"
                                                      :border-top       "1px solid #DDD"
                                                      :background-color "#f7f7f7"}
                                           :children [[title :src (at) :level :level3 :label "Interactive Parameters" :style {:margin-top "0"}]
                                                      [v-box :src (at)
                                                       :children [[box :src (at) :align :start :child [:code ":change-on-blur?"]]
                                                                  [radio-button :src (at)
                                                                   :label     "false - Call on-change on every keystroke"
                                                                   :value     false
                                                                   :model     @change-on-blur?
                                                                   :on-change #(reset! change-on-blur? %)
                                                                   :style     {:margin-left "20px"}]
                                                                  [radio-button :src (at)
                                                                   :label     "true - Call on-change only on blur or Enter key (Esc key resets text)"
                                                                   :value     true
                                                                   :model     @change-on-blur?
                                                                   :on-change #(reset! change-on-blur? %)
                                                                   :style     {:margin-left "20px"}]]]
                                                      [v-box :src (at)
                                                       :children [[box :src (at) :align :start :child [:code ":status"]]
                                                                  [radio-button :src (at)
                                                                   :label     "nil/omitted - normal input state"
                                                                   :value     nil
                                                                   :model     @status
                                                                   :on-change #(do
                                                                                 (reset! status %)
                                                                                 (reset! status-tooltip ""))
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button :src (at)
                                                                   :label ":validating - set while validating an input value"
                                                                   :value :validating
                                                                   :model @status
                                                                   :on-change #(do
                                                                                 (reset! status %)
                                                                                 (reset! status-tooltip "Validating tooltip - this (optionally) appears when an input-text is validating."))
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button :src (at)
                                                                   :label ":success - border color becomes green"
                                                                   :value :success
                                                                   :model @status
                                                                   :on-change #(do
                                                                                 (reset! status %)
                                                                                 (reset! status-tooltip "Success tooltip - this (optionally) appears when an input-text components has validated successfully."))
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button :src (at)
                                                                   :label     ":warning - border color becomes orange"
                                                                   :value     :warning
                                                                   :model     @status
                                                                   :on-change #(do
                                                                                 (reset! status %)
                                                                                 (reset! status-tooltip "Warning tooltip - this (optionally) appears when there are warnings on input-text components."))
                                                                   :style     {:margin-left "20px"}]
                                                                  [radio-button :src (at)
                                                                   :label     ":error - border color becomes red"
                                                                   :value     :error
                                                                   :model     @status
                                                                   :on-change #(do
                                                                                 (reset! status %)
                                                                                 (reset! status-tooltip "Error tooltip - this (optionally) appears when there are errors on input-text components."))
                                                                   :style     {:margin-left "20px"}]]]
                                                      [h-box :src (at)
                                                       :align :start
                                                       :gap      "5px"
                                                       :children [[checkbox :src (at)
                                                                   :label     [:code ":status-icon?"]
                                                                   :model     status-icon?
                                                                   :on-change (fn [val]
                                                                                (reset! status-icon? val))]
                                                                  [:span " (notice the tooltips on the icons)"]]]

                                                      [v-box :src (at)
                                                       :children [[box :src (at) :align :start :child [:code ":validation-regex"]]
                                                                  [radio-button :src (at)
                                                                   :label     "nil/omitted - no character validation"
                                                                   :value     nil
                                                                   :model     @regex
                                                                   :on-change #(do (reset! regex %)
                                                                                   (reset! text-val ""))
                                                                   :style     {:margin-left "20px"}]
                                                                  [radio-button :src (at)
                                                                   :label     "only accept input matching '99.9'"
                                                                   :value     regex999
                                                                   :model     @regex
                                                                   :on-change #(do (reset! regex %)
                                                                                   (reset! text-val ""))
                                                                   :style     {:margin-left "20px"}]]]
                                                      [h-box :src (at)
                                                       :align    :start
                                                       :gap      "5px"
                                                       :children [[checkbox :src (at)
                                                                   :label     [:code ":on-alter"]
                                                                   :model     on-alter?
                                                                   :on-change (fn [val]
                                                                                (reset! on-alter? val))]
                                                                  [:span " (set to " [:code "string/upper-case"] ")"]]]
                                                      [checkbox :src (at)
                                                       :label     [box :src (at) :align :start :child [:code ":disabled?"]]
                                                       :model     disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]
                                                      [h-box :src (at)
                                                       :gap "10px"
                                                       :children [[h-box :src (at)
                                                                   :align    :start
                                                                   :children [[:code ":rows"]
                                                                              "(textarea)"]]
                                                                  [slider :src (at)
                                                                   :model     slider-val
                                                                   :min       1
                                                                   :max       10
                                                                   :width     "200px"
                                                                   :on-change #(reset! slider-val %)]
                                                                  [label :src (at) :label @slider-val]]]]]]]]]
                  [parts-table "input-text" input-text-parts-desc]]])))

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [input-text-demo])
