(ns re-demo.input-text
  (:require [re-com.core    :refer [h-box v-box box gap line input-text input-password input-textarea label checkbox radio-button slider title p]]
            [re-com.misc    :refer [input-text-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util    :refer [px]]
            [clojure.string :as    string]
            [reagent.core   :as    reagent]))

(defn input-text-component-hierarchy
  []
  (let [indent          20
        table-style     {:style {:border "2px solid lightgrey" :margin-right "10px"}}
        border          {:border "1px solid lightgrey" :padding "6px 12px"}
        border-style    {:style border}
        border-style-nw {:style (merge border {:white-space "nowrap"})}
        valign          {:vertical-align "top"}
        valign-style    {:style valign}
        valign-style-hd {:style (merge valign {:background-color "#e8e8e8"})}
        indent-text     (fn [level text] [:span {:style {:padding-left (px (* level indent))}} text])
        highlight-text  (fn [text & [color]] [:span {:style {:font-weight "bold" :color (or color "dodgerblue")}} text])
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[input-text\n"
                      "   ...\n"
                      "   :parts {:wrapper {:class \"blah\"\n"
                      "                     :style { ... }\n"
                      "                     :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[input-text]")]
                   [:td border-style-nw "rc-input-text"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the text input."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-input-text-inner"]
                   [:td border-style-nw (code-text ":inner")]
                   [:td border-style "The container for the text input."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:input]")]
                   [:td border-style-nw "form-control"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The actual input field."]]]]]]))

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
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title  "Input Text Components"
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
                                                                  #_[input-text
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
                                                                  [gap :size "20px"]
                                                                  [label :label "[input-password ... ]"]
                                                                  [gap :size "5px"]
                                                                  #_[input-password
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
                                                                  [gap :size "20px"]
                                                                  [label :label "[input-textarea ... ]"]
                                                                  [gap :size "5px"]
                                                                  #_[input-textarea
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
                                                                  [gap :size "20px"]
                                                                  [label :label "[input-text w/ alpha filter in on-change ... ]"]
                                                                  [gap :size "5px"]
                                                                  [input-text
                                                                   :model            text-val
                                                                   :status           @status
                                                                   :status-icon?     @status-icon?
                                                                   :status-tooltip   @status-tooltip
                                                                   :width            "300px"
                                                                   :placeholder      (if @regex "enter number (99.9)" "placeholder message")
                                                                   :on-change        (fn [v resolve-fn]
                                                                                       (let [v (string/replace v #"[^a-zA-Z]" "")]
                                                                                         #_(js/console.log "reset!" v)
                                                                                         #_(reset! text-val v)
                                                                                         (js/setTimeout
                                                                                           (fn []
                                                                                             (js/console.log "reset!" v)
                                                                                             (reset! text-val v)
                                                                                             (resolve-fn))
                                                                                           1000)))
                                                                   :validation-regex @regex
                                                                   :on-alter         (if @on-alter? string/upper-case identity)
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
                                                                               :on-change #(reset! change-on-blur? %)
                                                                               :style     {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     "true - Call on-change only on blur or Enter key (Esc key resets text)"
                                                                               :value     true
                                                                               :model     @change-on-blur?
                                                                               :on-change #(reset! change-on-blur? %)
                                                                               :style     {:margin-left "20px"}]]]
                                                                  [v-box
                                                                   :children [[box :align :start :child [:code ":status"]]
                                                                              [radio-button
                                                                               :label     "nil/omitted - normal input state"
                                                                               :value     nil
                                                                               :model     @status
                                                                               :on-change #(do
                                                                                            (reset! status %)
                                                                                            (reset! status-tooltip ""))
                                                                               :style {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label ":validating - set while validating an input value"
                                                                               :value :validating
                                                                               :model @status
                                                                               :on-change #(do
                                                                                            (reset! status %)
                                                                                            (reset! status-tooltip "Validating tooltip - this (optionally) appears when an input-text is validating."))
                                                                               :style {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label ":success - border color becomes green"
                                                                               :value :success
                                                                               :model @status
                                                                               :on-change #(do
                                                                                            (reset! status %)
                                                                                            (reset! status-tooltip "Success tooltip - this (optionally) appears when an input-text components has validated successfully."))
                                                                               :style {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     ":warning - border color becomes orange"
                                                                               :value     :warning
                                                                               :model     @status
                                                                               :on-change #(do
                                                                                            (reset! status %)
                                                                                            (reset! status-tooltip "Warning tooltip - this (optionally) appears when there are warnings on input-text components."))
                                                                               :style     {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     ":error - border color becomes red"
                                                                               :value     :error
                                                                               :model     @status
                                                                               :on-change #(do
                                                                                            (reset! status %)
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
                                                                               :on-change #(do (reset! regex %)
                                                                                               (reset! text-val ""))
                                                                               :style     {:margin-left "20px"}]
                                                                              [radio-button
                                                                               :label     "only accept input matching '99.9'"
                                                                               :value     regex999
                                                                               :model     @regex
                                                                               :on-change #(do (reset! regex %)
                                                                                               (reset! text-val ""))
                                                                               :style     {:margin-left "20px"}]]]
                                                                  [h-box
                                                                   :align    :start
                                                                   :gap      "5px"
                                                                   :children [[checkbox
                                                                               :label     [:code ":on-alter"]
                                                                               :model     on-alter?
                                                                               :on-change (fn [val]
                                                                                            (reset! on-alter? val))]
                                                                              [:span " (set to " [:code "string/upper-case"] ")"]]]
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
                                                                              [label :label @slider-val]]]]]]]]]]]
                  [input-text-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [input-text-demo])
