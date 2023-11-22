(ns re-demo.popover-dialog-demo
  (:require [re-com.core  :refer [at h-box v-box box gap line label checkbox radio-button button single-dropdown popover-content-wrapper popover-anchor-wrapper]]
            [re-com.util  :refer [deref-or-value]]
            [reagent.core :as    reagent]))

(defn popover-body
  [dialog-data on-change & {:keys [showing-injected? position-injected]}]  ;; v0.10.0 breaking change fix (was [showing? position dialog-data on-change])
  (let [dialog-data   (reagent/atom (deref-or-value dialog-data))
        submit-dialog (fn [new-dialog-data]
                        (reset! showing-injected? false)
                        (on-change new-dialog-data))
        cancel-dialog #(reset! showing-injected? false)
        show-tooltip? (reagent/atom (= (:tooltip-state @dialog-data) "2"))]
    (fn []
      [popover-content-wrapper :src (at)
       :showing-injected? showing-injected?
       :position-injected position-injected
       :on-cancel         cancel-dialog
       :width            "400px"
       :backdrop-opacity 0.3
       :title            "This is the title"
       :body             [v-box :src (at)
                          :children [[label :src (at)
                                      :label "The body of a popover can act like a dialog box containing standard input controls."]
                                     [gap :src (at) :size "15px"]
                                     [h-box :src (at)
                                      :children [[v-box :src (at)
                                                  :size "auto"
                                                  :children [[radio-button :src (at)
                                                              :label     "Don't show extra popover"
                                                              :value     "1"
                                                              :model     (:tooltip-state @dialog-data)
                                                              :on-change (fn [val]
                                                                           (swap! dialog-data assoc :tooltip-state val)
                                                                           (reset! show-tooltip? false))]
                                                             [radio-button :src (at)
                                                              :label     "Show extra popover"
                                                              :value     "2"
                                                              :model     (:tooltip-state @dialog-data)
                                                              :on-change (fn [val]
                                                                           (swap! dialog-data assoc :tooltip-state val)
                                                                           (reset! show-tooltip? true))]]]]]
                                     [gap :src (at) :size "20px"]
                                     [line :src (at)]
                                     [gap :src (at) :size "10px"]
                                     [h-box :src (at)
                                      :gap      "10px"
                                      :children [[button :src (at)
                                                  :label    [:span [:i {:class "zmdi zmdi-check"}] " Apply"]
                                                  :on-click #(submit-dialog @dialog-data)
                                                  :class    "btn-primary"]
                                                 [popover-anchor-wrapper :src (at)
                                                  :showing? show-tooltip?
                                                  :position :right-below
                                                  :anchor   [button :src (at)
                                                             :label    [:span [:i {:class "zmdi zmdi-close"}] " Cancel"]
                                                             :on-click cancel-dialog]
                                                  :popover  [popover-content-wrapper :src (at) ;; NOTE: didn't specify on-cancel here (handled properly)
                                                             :width         "250px"
                                                             :title         "This is the cancel button"
                                                             :close-button? false
                                                             :body          "You can even have a popover over a popover!"]]]]]]])))

(defn popover-dialog-demo
  [position]
  (let [showing?    (reagent/atom false)
        dialog-data (reagent/atom {:tooltip-state "2"})
        on-change   (fn [new-dialog-data]
                      (reset! dialog-data new-dialog-data))]
    (fn []
      [popover-anchor-wrapper :src (at)
       :showing? showing?
       :position @position
       :anchor   [button :src (at)
                  :label    "Dialog box"
                  :on-click #(reset! showing? true)
                  :class    "btn btn-danger"]
       :popover  [popover-body dialog-data on-change]])))  ;; v0.10.0 breaking change fix (was [popover-body showing? @position dialog-data on-change])



