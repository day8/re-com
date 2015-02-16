(ns re-demo.row-button
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.core      :refer [label checkbox]]
            [re-com.buttons   :refer [row-button row-button-args-desc
                                      hyperlink-href]]
            [re-com.box       :refer [h-box v-box box gap line]]
            [re-com.tabs      :refer [horizontal-bar-tabs vertical-bar-tabs]]
            [re-demo.utils    :refer [panel-title component-title args-table]]
            [re-com.util      :refer [enumerate]]
            [reagent.core     :as    reagent]))


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
      [v-box
       :gap "10px"
       :children [[panel-title "[row-button ... ]"]

                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :style    {:font-size "small"}
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [:span "Designed for tables which have per-row buttons. To avoid visual clutter, they only appear on row mouse over."]
                                          [:span "To experiement, mouse over the table in the demo.  Notice that buttons appear for each row, muted initially, but more strongly as the mouse is over them specifically."]
                                          [:p
                                           "Material design icons can be found "
                                           [hyperlink-href
                                            :label  "here"
                                            :href   "http://zavoloklom.github.io/material-design-iconic-font/icons.html"
                                            :target "_blank"]
                                           "."]
                                          [args-table row-button-args-desc]]]
                              [v-box
                               :gap      "10px"
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
                                                                              [label :label "]"]]]]] [data-table rows col-widths]]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [row-button-demo])
