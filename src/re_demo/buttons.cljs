(ns re-demo.buttons
  ;(:require-macros [clairvoyant.core :refer [trace-forms]]) ;;Usage: (trace-forms {:tracer default-tracer} (your-code))
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.core      :refer [label input-text spinner checkbox radio-button]]
            [re-com.buttons   :refer [button button-args-desc
                                      md-circle-icon-button md-circle-icon-button-args-desc
                                      md-icon-button md-icon-button-args-desc
                                      info-button info-button-args-desc
                                      row-button row-button-args-desc
                                      hyperlink hyperlink-args-desc
                                      hyperlink-href hyperlink-href-args-desc]]
            [re-com.box       :refer [h-box v-box box gap line]]
            [re-com.tabs      :refer [horizontal-bar-tabs vertical-bar-tabs]]
            [re-demo.utils    :refer [panel-title component-title args-table]]
            [re-com.util      :refer [enumerate]]
            [reagent.core     :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-spinner  false}))

(def click-outcomes
  [""   ;; start blank
   "Nuclear warhead launched."
   "Oops. Priceless Ming Vase smashed!!"
   "Diamonds accidentally flushed."
   "Toy disabled"])


(defn button-demo
  []
  [h-box
   :gap      "50px"
   :children [[v-box
               :gap      "10px"
               :style    {:font-size "small"}
               :children [[component-title "[button ... ]"]
                          [args-table button-args-desc]]]
              [v-box
               :children [[component-title "Demo"]
                          [v-box
                           :children [[h-box
                                       :children [[button
                                                   :label            "No Clicking!"
                                                   :tooltip          "Seriously, NO CLICKING!"
                                                   :tooltip-position :below-center
                                                   :disabled?         (= (:outcome-index @state) (dec (count click-outcomes)))
                                                   :on-click          #(swap! state update-in [:outcome-index] inc)
                                                   :class             "btn-danger"]
                                                  [box
                                                   :align :center      ;; note: centered text wrt the button
                                                   :child  [label
                                                            :label (nth click-outcomes (:outcome-index @state))
                                                            :style {:margin-left "15px"}]]]]

                                      [gap :size "40px"]
                                      [h-box
                                       :gap "50px"
                                       :children [[button
                                                   :label             (if (:see-spinner @state)  "Stop it!" "See Spinner")
                                                   :tooltip           "I'm a tooltip on the left"
                                                   :tooltip-position :left-center
                                                   :on-click          #(swap! state update-in [:see-spinner] not)]
                                                  (when (:see-spinner @state)  [spinner])]]]]]]]])


(def icons
  [{:id "md-add"    :label [:i {:class "md-add"}]}
   {:id "md-delete" :label [:i {:class "md-delete"}]}
   {:id "md-undo"   :label [:i {:class "md-undo"}]}
   {:id "md-home"   :label [:i {:class "md-home"}]}
   {:id "md-person" :label [:i {:class "md-person"}]}
   {:id "md-info"   :label [:i {:class "md-info"}]}])


(defn example-icons
  [selected-icon]
  [h-box
   :align :center
   :gap "8px"
   :children [[label :label "Example icons:"]
              [horizontal-bar-tabs
               :model     selected-icon
               :tabs      icons
               :on-change #(reset! selected-icon %)]
              [label :label @selected-icon]]])


(defn md-circle-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [h-box
       :gap "50px"
       :children [[v-box
                   :gap "10px"
                   :style {:font-size "small"}
                   :children [[component-title "[md-circle-icon-button ... ]"]
                              [label :class "small-caps" :label "notes:"]
                              [:p
                               "Material design icons can be found "
                               [hyperlink-href
                                :label  "here"
                                ;:tooltip "Click here to see all material design icons"
                                :href   "http://zavoloklom.github.io/material-design-iconic-font/icons.html"
                                :target "_blank"]
                               "."]
                              [args-table md-circle-icon-button-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :gap "15px"
                               :children [[example-icons selected-icon]
                                          [gap :size "10px"]
                                          [label :label "Hover over the buttons below to see a tooltip."]
                                          [h-box
                                           :gap      "20px"
                                           :align    :center
                                           :children [[label :label "States:"]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :emphasise?   true
                                                       :tooltip      "This button has :emphasise? set to true"
                                                       :on-click     #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is the default button"
                                                       :on-click     #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This button has :disabled? set to true"
                                                       :disabled?    true
                                                       :on-click     #()]]]
                                          [h-box
                                           :gap      "20px"
                                           :align    :center
                                           :children [[label :label "Sizes:"]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is a :smaller button"
                                                       :size         :smaller
                                                       :on-click #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This button does not specify a :size"
                                                       :on-click     #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is a :larger button"
                                                       :size         :larger
                                                       :on-click #()]]]]]]]]])))


(defn md-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [h-box
       :gap "50px"
       :children [[v-box
                   :gap "10px"
                   :style {:font-size "small"}
                   :children [[component-title "[md-icon-button ... ]"]
                              [label :class "small-caps" :label "notes:"]
                              [:p
                               "Material design icons can be found "
                               [hyperlink-href
                                :label  "here"
                                :href   "http://zavoloklom.github.io/material-design-iconic-font/icons.html"
                                :target "_blank"]
                               "."]
                              [args-table md-icon-button-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :gap "15px"
                               :children [[example-icons selected-icon]
                                          [gap :size "10px"]
                                          [label :label "Hover over the buttons below to see a tooltip."]
                                          [h-box
                                           :gap      "20px"
                                           :align    :center
                                           :children [[label :label "States:"]
                                                      [md-icon-button
                                                       :md-icon-name @selected-icon
                                                       :emphasise?   true
                                                       :tooltip      "This button has :emphasise? set to true"
                                                       :on-click     #()]
                                                      [md-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is the default button"
                                                       :on-click     #()]
                                                      [md-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This button has :disabled? set to true"
                                                       :disabled?    true
                                                       :on-click     #()]]]
                                          [h-box
                                           :gap      "20px"
                                           :align    :center
                                           :children [[label :label "Sizes:"]
                                                      [md-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is a :smaller button"
                                                       :size         :smaller
                                                       :on-click #()]
                                                      [md-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This button does not specify a :size"
                                                       :on-click     #()]
                                                      [md-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is a :larger button"
                                                       :size         :larger
                                                       :on-click #()]]]]]]]]])))


(defn info-button-demo
  []
  (let [info [v-box
              :gap      "7px"
              :children [[label :class "info-heading" :label "Info Popup Heading"]
                         [label :label "Click away to cause a pop-down."]
                         [label :label [:span "Here's an example of an embedded hyperlink: "
                                        [hyperlink-href
                                         :label "special forms"
                                         :href "http://clojure.org/special_forms"
                                         :target "_blank"] "."]]
                         [label :class "info-subheading" :label "Sub heading"]
                         [label :label [:span "Note: Styles copied from "
                                        [hyperlink-href
                                         :label "ClojureScript Cheatsheet"
                                         :href "http://cljs.info/cheatsheet"
                                         :target "_blank"] "."]]]]]
    (fn []
      [h-box
       :gap "50px"
       :children [[v-box
                   :gap "10px"
                   :style {:font-size "small"}
                   :width "450px"
                   :children [[component-title "[info-button ... ]"]
                              [label :class "small-caps" :label "notes:"]
                              [label :label "A tiny information button, which is light grey and unobrusive. When pressed, displays a popup contining helpful information. "]
                              [label :label "Designed to be used with input fields, to explain the purpose of the field."]
                              [args-table info-button-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :children [[label :label "Click on the buttons below to see a popup info panel."]
                                          [gap :size "15px"]
                                          [h-box
                                           :gap      "4px"
                                           :children [[label
                                                       :class "small-caps"
                                                       :label "client"]
                                                      [info-button
                                                       :info info]]]
                                          [input-text
                                           :placeholder "Example input #1"]
                                          [gap :size "15px"]
                                          [h-box
                                           :gap      "4px"
                                           :children [[label
                                                       :class "small-caps"
                                                       :label "product"]
                                                      [info-button
                                                       :position :right-center
                                                       :width    "420px"
                                                       :info     info]]]
                                          [input-text
                                           :placeholder "Example input #2"]]]]]]])))


(defn data-row
  [row first? last? col-widths mouse-over click-msg]
  (let [mouse-over-row? (identical? @mouse-over row)]
    [h-box
     :class    "rc-div-table-row"
     :attr     {:on-mouse-over (handler-fn (reset! mouse-over row))
                :on-mouse-out  (handler-fn (reset! mouse-over nil))}
     :children [[h-box
                 :width (:sort col-widths)
                 :gap "2px"
                 :align :center
                 :children [[row-button
                             :md-icon-name    "md-arrow-back md-rotate-90" ;; "md-arrow-back md-rotate-90", "md-play-arrow md-rotate-270", "md-expand-less"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Move this line up"
                             :disabled?       (and first? mouse-over-row?)
                             :on-click        #(reset! click-msg (str "move row " (:id row) " up"))]
                            [row-button
                             :md-icon-name    "md-arrow-forward md-rotate-90" ;; "md-arrow-forward md-rotate-90", "md-play-arrow md-rotate-90", "md-expand-more"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Move this line down"
                             :disabled?       (and last? mouse-over-row?)
                             :on-click        #(reset! click-msg (str "move row " (:id row) " down"))]]]
                [label :label (:name row) :width (:name col-widths)]
                [label :label (:from row) :width (:from col-widths)]
                [label :label (:to   row) :width (:to   col-widths)]
                [h-box
                 :gap      "2px"
                 :width    (:actions col-widths)
                 :align    :center
                 :children [[row-button
                             :md-icon-name    "md-content-copy"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Copy this line"
                             :on-click        #(reset! click-msg (str "copy row " (:id row)))]
                            [row-button
                             :md-icon-name    "md-mode-edit"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Edit this line"
                             :on-click        #(reset! click-msg (str "edit row " (:id row)))]
                            [row-button
                             :md-icon-name    "md-delete"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Delete this line"
                             :on-click        #(reset! click-msg (str "delete row " (:id row)))]]]]]))


(defn data-table
  [rows col-widths]
  (let [large-font (reagent/atom false)
        mouse-over (reagent/atom nil)
        click-msg  (reagent/atom "")]
    (fn []
      [v-box
       :align    :start
       :gap      "10px"
       :children [[checkbox
                   :label     "Large font-size (row-buttons inherit their font-size from their parent)"
                   :model     large-font
                   :on-change #(reset! large-font %)]
                  [v-box
                   :class    "rc-div-table"
                   :style    {:font-size (when @large-font "24px")}
                   :children [^{:key "0"}
                              [h-box
                               :class    "rc-div-table-header"
                               :children [[label :label "Sort"    :width (:sort    col-widths)]
                                          [label :label "Name"    :width (:name    col-widths)]
                                          [label :label "From"    :width (:from    col-widths)]
                                          [label :label "To"      :width (:to      col-widths)]
                                          [label :label "Actions" :width (:actions col-widths)]]]
                              (for [[_ row first? last?] (enumerate (sort-by :sort (vals rows)))]
                                ^{:key (:id row)} [data-row row first? last? col-widths mouse-over click-msg])]]
                  [label :label (str "Last row-button click: " @click-msg)]]])))


(defn row-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))
        col-widths {:sort "2.6em" :name "7.5em" :from "4em" :to "4em" :actions "4.5em"}
        rows       {"1" {:id "1" :sort 0 :name "Time range 1" :from "18:00" :to "22:30"}
                    "2" {:id "2" :sort 1 :name "Time range 2" :from "18:00" :to "22:30"}
                    ;"2" {:id "2" :sort 1 :name "Time range 2 with some extra text appended to the end." :from "18:00" :to "22:30"}
                    "3" {:id "3" :sort 2 :name "Time range 3" :from "06:00" :to "18:00"}}]
    (fn []
      [h-box
       :gap "50px"
       :children [[v-box
                   :gap "10px"
                   :style {:font-size "small"}
                   :children [[component-title "[row-button ... ]"]
                              [label :class "small-caps" :label "notes:"]
                              [label :label "Designed for tables which have per-row buttons. To avoid visual clutter, they only appear on row mouse over."]
                              [label :label "To experiement, mouse over the table in the demo.  Notice that buttons appear for each row, muted initially, but more strongly as the mouse is over them specifically."]

                              [:p
                               "Material design icons can be found "
                               [hyperlink-href
                                :label  "here"
                                :href   "http://zavoloklom.github.io/material-design-iconic-font/icons.html"
                                :target "_blank"]
                               "."]
                              [args-table row-button-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :gap "40px"
                               :children [[example-icons selected-icon]
                                          [v-box
                                           :gap      "8px"
                                           :children [[label :label "Hover over the buttons below to see a tooltip."]
                                                      [h-box
                                                       :gap      "2px"
                                                       :align    :center
                                                       :children [[label :label "States: ["]
                                                                  [row-button
                                                                   :md-icon-name    @selected-icon
                                                                   :mouse-over-row? false
                                                                   :tooltip         ":mouse-over-row? set to false (invisible)"
                                                                   :on-click        #()]
                                                                  [row-button
                                                                   :md-icon-name    @selected-icon
                                                                   :mouse-over-row? true
                                                                   :tooltip         ":mouse-over-row? set to true (semi-visible)"
                                                                   :on-click        #()]
                                                                  [row-button
                                                                   :md-icon-name    @selected-icon
                                                                   :tooltip         ":disabled? set to true"
                                                                   :disabled?       true
                                                                   :on-click        #()]
                                                                  [label :label "]"]]]]]
                                          [data-table rows col-widths]]]]]]])))


(defn hyperlink-demo
  []
  (let [disabled?   (reagent/atom false)
        click-count (reagent/atom 0)]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[hyperlink ... ]"]
                              [args-table hyperlink-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [h-box
                               :gap "30px"
                               :children [[box
                                           :width "200px"
                                           :align :start
                                           :child [hyperlink
                                                   :label     (if @disabled? "Now disabled" "Call back")
                                                   :tooltip   "Click here to increase the click count"
                                                   :on-click  #(swap! click-count inc)
                                                   :disabled? disabled?]]
                                          [v-box
                                           :gap "15px"
                                           :children [[label :label (str "click count = " @click-count)]
                                                      [label :label "parameters:"]
                                                      [checkbox
                                                       :label ":disabled?"
                                                       :model disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]]])))


(defn hyperlink-href-demo
  []
  (let [target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[hyperlink-href ... ]"]
                              [args-table hyperlink-href-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [h-box
                               :gap "40px"
                               :children [[box
                                           :width "200px"
                                           :align :start
                                           :child [hyperlink-href
                                                   :label     "Launch Google"
                                                   :tooltip   "You're about to launch Google"
                                                   :href      (when href? "http://google.com")
                                                   :target    (when href? target)]]
                                          [v-box
                                           :gap "15px"
                                           :children [[label :label "parameters:"]
                                                      (when @href?
                                                        [v-box
                                                         :children [[label :label ":target"]
                                                                    [radio-button
                                                                     :label "_self - load link into same tab"
                                                                     :value "_self"
                                                                     :model @target
                                                                     :on-change #(reset! target "_self")
                                                                     :style {:margin-left "20px"}]
                                                                    [radio-button
                                                                     :label "_blank - load link inot new tab"
                                                                     :value "_blank"
                                                                     :model @target
                                                                     :on-change #(reset! target "_blank")
                                                                     :style {:margin-left "20px"}]]])]]]]]]]])))



(def demos [{:id 0 :label "button"                :component button-demo}
            {:id 1 :label "md-circle-icon-button" :component md-circle-icon-button-demo}
            {:id 2 :label "md-icon-button"        :component md-icon-button-demo}
            {:id 3 :label "info-button"           :component info-button-demo}
            {:id 4 :label "row-button"            :component row-button-demo}
            {:id 5 :label "hyperlink"             :component hyperlink-demo}
            {:id 6 :label "hyperlink-href"        :component hyperlink-href-demo}
            ])


;; This is not the way you'd store state in a "real" app.  We're just taking shortcuts for demo purposes.
;; If we put this atom at global scope and use defonce, it works better with figwheel refreshes.
(defonce selected-demo-id (reagent/atom 0))


(defn panel2
  []
  [v-box
   :gap "10px"
   :children [[panel-title "Button Components" ]
              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :children [[component-title "Components"]
                                      [vertical-bar-tabs
                                       :model     selected-demo-id
                                       :tabs      demos
                                       :on-change #(reset! selected-demo-id %)]]]
                          [(get-in demos [@selected-demo-id :component])]]]]])


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panel2])
