(ns re-demo.popover-dialog-demo
  (:require [reagent.core    :as    reagent]
            [re-com.util     :refer [deref-or-value]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.core     :refer [button label checkbox radio-button]]
            [re-com.dropdown :refer [single-dropdown]]
            [re-com.popover  :refer [popover popover-content-wrapper popover-anchor-wrapper]]))


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
       :width            500
       :backdrop-opacity 0.3
       :title            "This is the title"
       :body             [(fn []                                   ;; NOTE: THIS IS NASTY BUT REQUIRED (OTHERWISE DIALOG WILL NOT BE UPDATED WHEN ATOMS CHANGES)
                            [v-box
                             :children [[label
                                         :label "The body of a popover can act like a dialog box containg standard input controls."]
                                        [gap :size "15px"]
                                        [h-box
                                         :children [[v-box
                                                     :size "auto"
                                                     :children [[radio-button
                                                                 :label "Don't show extra popover"
                                                                 :value "1"
                                                                 :model (:tooltip-state @dialog-data)
                                                                 :on-change (fn []
                                                                              (swap! dialog-data assoc :tooltip-state "1")
                                                                              (reset! show-tooltip? false))]
                                                                [radio-button
                                                                 :label "Show extra popover"
                                                                 :value "2"
                                                                 :model (:tooltip-state @dialog-data)
                                                                 :on-change (fn []
                                                                              (swap! dialog-data assoc :tooltip-state "2")
                                                                              (reset! show-tooltip? true))]]]]]
                                        [gap :size "15px"]
                                        [:hr {:style {:margin "10px 0 10px"}}] ;; TODO: Change to line
                                        [h-box
                                         :gap "10px"
                                         :children [[button
                                                     :label [:span [:span.glyphicon.glyphicon-ok] " Apply"]
                                                     :on-click #(submit-dialog @dialog-data)
                                                     :class "btn-primary"]

                                                    ;; Using the original popover function
                                                    #_[popover
                                                     :position :right-below
                                                     :showing? show-tooltip?
                                                     :anchor   [button
                                                                :label [:span [:span.glyphicon.glyphicon-remove] " Cancel"]
                                                                :on-click cancel-dialog]
                                                     :popover {:title         "This is the cancel button"
                                                               :close-button? false
                                                               :body          "You can even have a popover over a popover!"}]

                                                    ;; Using the new method
                                                    [popover-anchor-wrapper
                                                     :showing? show-tooltip?
                                                     :position :right-below
                                                     :anchor   [button
                                                                :label [:span [:span.glyphicon.glyphicon-remove] " Cancel"]
                                                                :on-click cancel-dialog]
                                                     :popover  [popover-content-wrapper ;; NOTE: didn't specify on-cancel here (handled properly)
                                                                :showing?         show-tooltip?
                                                                :position         :right-below
                                                                :title            "This is the cancel button"
                                                                :close-button?    false
                                                                :body             "You can even have a popover over a popover!"]]
                                                    ]]]])]])))


(defn popover-dialog-demo
  []
  (let [showing? (reagent/atom false)
        dialog-data             (reagent/atom {:tooltip-state "2"})
        on-change               (fn [new-dialog-data]
                                  (reset! dialog-data new-dialog-data))
        position                :right-center]
    (fn []
      [popover-anchor-wrapper
       :showing? showing?
       :position position
       :anchor   [button
                  :label "Dialog box"
                  :on-click #(reset! showing? true)
                  :class "btn btn-danger"]
       :popover  [popover-body showing? position dialog-data on-change]])))


#_(defn popover-body-old
  [dialog-data dialog-submit dialog-cancel]
  (let [dialog-data   (reagent/atom (deref-or-value dialog-data))
        show-tooltip? (reagent/atom false)]
    (fn []
      [v-box
       :children [[:span "This popover contains an embedded dialog..."]
                  [:h4 "Checkboxes and Radio buttons"]
                  [h-box
                   :children [[v-box
                               :size "auto"
                               :children [[checkbox
                                           :label "Red (toggle disabled)"
                                           :model (:red @dialog-data)
                                           :on-change #(swap! dialog-data assoc :red %)]
                                          [checkbox
                                           :label "Green (initially checked)"
                                           :model (:green @dialog-data)
                                           :on-change #(swap! dialog-data assoc :green %)]
                                          [checkbox
                                           :label (str "Blue" (when (:red @dialog-data) " (disabled)"))
                                           :model (:blue @dialog-data)
                                           :disabled (:red @dialog-data)
                                           :on-change #(swap! dialog-data assoc :blue %)]]]
                              [v-box
                               :size "auto"
                               :children [[radio-button
                                           :label "Hue"
                                           :value "1"
                                           :model (:colour-spec @dialog-data)
                                           :on-change #(swap! dialog-data assoc :colour-spec "1")]
                                          [radio-button
                                           :label "Saturation (initially checked)"
                                           :value "2"
                                           :model (:colour-spec @dialog-data)
                                           :on-change #(swap! dialog-data assoc :colour-spec "2")]
                                          [radio-button
                                           :label (str "Luminance" (when (:red @dialog-data) " (disabled)"))
                                           :value "3"
                                           :model (:colour-spec @dialog-data)
                                           :disabled (:red @dialog-data)
                                           :on-change #(swap! dialog-data assoc :colour-spec "3")]]]]]
                  [:em [:small "Note: Hover over the cancel button to see a popover over a popover"]]
                  [:hr {:style {:margin "10px 0 10px"}}]
                  [h-box
                   :gap "10px"
                   :children [[button
                               :label [:span [:span.glyphicon.glyphicon-ok] " Apply"]
                               :on-click #(dialog-submit @dialog-data)
                               :class "btn-primary"]
                              [popover
                               :position :right-below
                               :showing? show-tooltip?
                               :anchor [:button             ;; Standard [button] can't handle mousr-over/out
                                        {:class         "btn"
                                         :style         {:flex "none"}
                                         :on-click      dialog-cancel
                                         :on-mouse-over #(reset! show-tooltip? true)
                                         :on-mouse-out  #(reset! show-tooltip? false)}
                                        [:span [:span.glyphicon.glyphicon-remove] " Cancel"]]
                               :popover {:title         "Tooltip"
                                         :close-button? false
                                         :body          "You can even have a popover over a popover!"}]]]]])))


#_(defn popover-dialog-demo-old
  []
  (let [showing? (reagent/atom false)
        dialog-data             (reagent/atom {:red         false
                                             :green       true
                                             :blue        false
                                             :colour-spec "2"})
        submit-dialog           (fn [new-dialog-data]
                                (reset! dialog-data new-dialog-data)
                                (reset! showing? false))
        cancel-dialog           #(reset! showing? false)]
    [popover
     :position :below-center
     :showing? showing?
     :anchor   [button
                :label             "Original Popover dialog"
                :on-click          #(reset! showing? true)
                :class             "btn btn-danger"]
     :popover  {:width             500
                :title             [:div "Arbitrary " [:strong "markup "] [:span {:style {:color "red"}} "title"]]
                :body              [popover-body-old dialog-data submit-dialog cancel-dialog]}
     :options  {:arrow-length      15
                :arrow-width       10
                :backdrop-callback cancel-dialog
                :close-callback    cancel-dialog
                :backdrop-opacity  0.3}]))
