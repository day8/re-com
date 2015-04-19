(ns re-demo.popover-dialog-demo
  (:require [re-com.core  :refer [h-box v-box box gap line label checkbox radio-button button single-dropdown
                                  popover-content-wrapper popover-anchor-wrapper]]
            [re-com.util  :refer [deref-or-value]]
            [reagent.core :as    reagent]))


(defn popover-body
  [showing? position dialog-data on-change]
  (let [dialog-data   (reagent/atom (deref-or-value dialog-data))
        submit-dialog (fn [new-dialog-data]
                        (reset! showing? false)
                        (on-change new-dialog-data))
        cancel-dialog #(reset! showing? false)
        show-tooltip? (reagent/atom (= (:tooltip-state @dialog-data) "2"))]
    (fn []
      [popover-content-wrapper
       :showing?         showing?
       :on-cancel        cancel-dialog
       :position         position
       :width            "400px"
       :backdrop-opacity 0.3
       :title            "This is the title"
       :body             [(fn []  ;; NOTE: THIS IS NASTY BUT REQUIRED (OTHERWISE DIALOG WILL NOT BE UPDATED WHEN ATOMS CHANGES)
                            [v-box
                             :children [[label
                                         :label "The body of a popover can act like a dialog box containing standard input controls."]
                                        [gap :size "15px"]
                                        [h-box
                                         :children [[v-box
                                                     :size "auto"
                                                     :children [[radio-button
                                                                 :label     "Don't show extra popover"
                                                                 :value     "1"
                                                                 :model     (:tooltip-state @dialog-data)
                                                                 :on-change (fn []
                                                                              (swap! dialog-data assoc :tooltip-state "1")
                                                                              (reset! show-tooltip? false))]
                                                                [radio-button
                                                                 :label     "Show extra popover"
                                                                 :value     "2"
                                                                 :model     (:tooltip-state @dialog-data)
                                                                 :on-change (fn []
                                                                              (swap! dialog-data assoc :tooltip-state "2")
                                                                              (reset! show-tooltip? true))]]]]]
                                        [gap :size "20px"]
                                        [line]
                                        [gap :size "10px"]
                                        [h-box
                                         :gap      "10px"
                                         :children [[button
                                                     :label    [:span [:i {:class "md-check" }] " Apply"]
                                                     :on-click #(submit-dialog @dialog-data)
                                                     :class    "btn-primary"]
                                                    [popover-anchor-wrapper
                                                     :showing? show-tooltip?
                                                     :position :right-below
                                                     :anchor   [button
                                                                :label    [:span [:i {:class "md-close" }] " Cancel"]
                                                                :on-click cancel-dialog]
                                                     :popover  [popover-content-wrapper ;; NOTE: didn't specify on-cancel here (handled properly)
                                                                :showing?      show-tooltip?
                                                                :position      :right-below
                                                                :width         "250px"
                                                                :title         "This is the cancel button"
                                                                :close-button? false
                                                                :body          "You can even have a popover over a popover!"]]]]]])]])))


(defn popover-dialog-demo
  [position]
  (let [showing?    (reagent/atom false)
        dialog-data (reagent/atom {:tooltip-state "2"})
        on-change   (fn [new-dialog-data]
                      (reset! dialog-data new-dialog-data))
        ;; position    :left-above ;; TODO:
        ]
    (fn []
      [popover-anchor-wrapper
       :showing? showing?
       :position @position                                  ;; TODO:
       :anchor   [button
                  :label    "Dialog box"
                  :on-click #(reset! showing? true)
                  :class    "btn btn-danger"]
       :popover  [popover-body showing? @position dialog-data on-change]]))) ;; TODO:
