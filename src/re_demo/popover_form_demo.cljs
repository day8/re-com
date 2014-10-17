(ns re-demo.popover-form-demo
  (:require [reagent.core    :as    reagent]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.core    :refer  [button checkbox radio-button]]
            [re-com.dropdown :refer [single-dropdown]]
            [re-com.popover  :refer [popover]]))


(defn popover-body
  [form-data form-submit form-cancel show-tooltip?]
  [v-box
   :children [[:span "This popover contains an embedded form..."]
              [:h4 "Checkboxes and Radio buttons"]
              [h-box
               :children [[v-box
                           :size     "auto"
                           :children [[checkbox
                                       :label     "Red (toggle disabled)"
                                       :model     (:checkbox1 @form-data)
                                       :on-change #(swap! form-data assoc :checkbox1 %)]
                                      [checkbox
                                       :label     "Green (initially checked)"
                                       :model     (:checkbox2 @form-data)
                                       :on-change #(swap! form-data assoc :checkbox2 %)]
                                      [checkbox
                                       :label     (str "Blue" (when (:checkbox1 @form-data) " (disabled)"))
                                       :model     (:checkbox3 @form-data)
                                       :disabled  (:checkbox1 @form-data)
                                       :on-change #(swap! form-data assoc :checkbox3 %)]]]
                          [v-box
                           :size     "auto"
                           :children [[radio-button
                                       :label     "Hue"
                                       :value     "1"
                                       :model     (:radio-group @form-data)
                                       :on-change #(swap! form-data assoc :radio-group "1")]
                                      [radio-button
                                       :label     "Saturation (initially checked)"
                                       :value     "2"
                                       :model     (:radio-group @form-data)
                                       :on-change #(swap! form-data assoc :radio-group "2")]
                                      [radio-button
                                       :label     (str "Luminance" (when (:checkbox1 @form-data) " (disabled)"))
                                       :value     "3"
                                       :model     (:radio-group @form-data)
                                       :disabled  (:checkbox1 @form-data)
                                       :on-change #(swap! form-data assoc :radio-group "3")]]]]]
              [:em [:small "Note: Hover over the cancel button to see a popover over a popover"]]
              [:hr {:style {:margin "10px 0 10px"}}]
              [h-box
               :gap      "10px"
               :children [[button
                           :label [:span [:span.glyphicon.glyphicon-ok] " Apply"]
                           :on-click form-submit
                           :class "btn-primary"]
                          [popover
                           :position :right-below
                           :showing? show-tooltip?
                           :anchor   [:button         ;; Standard [button] can't handle mousr-over/out
                                      {:class         "btn"
                                       :style         {:flex "none"}
                                       :on-click      form-cancel
                                       :on-mouse-over #(reset! show-tooltip? true)
                                       :on-mouse-out  #(reset! show-tooltip? false)}
                                      [:span [:span.glyphicon.glyphicon-remove] " Cancel"]]
                           :popover {:title         "Tooltip"
                                     :close-button? false
                                     :body          "You can even have a popover over a popover!"}]]]]])


(defn popover-form-demo
  []
  (let [popover-form-showing? (reagent/atom false)
        show-tooltip?         (reagent/atom false)        ;; Ideally should be in [popover-body] but tooltip won't close when the parent closes, so have to close it in form-cancel
        initial-form-data     (reagent/atom {})
        form-data             (reagent/atom {:checkbox1   false
                                             :checkbox2   true
                                             :checkbox3   false
                                             :radio-group "2"})
        form-initialise       (fn []
                                (reset! initial-form-data @form-data)
                                (reset! popover-form-showing? true))
        form-submit           (fn []
                                (reset! popover-form-showing? false))
        form-cancel           (fn []
                                (reset! form-data @initial-form-data)
                                (reset! popover-form-showing? false)
                                (reset! show-tooltip? false))]
    [popover
     :position :below-center
     :showing? popover-form-showing?
     :anchor   [button
                :label             "Popover Form"
                :on-click          #(if-not @popover-form-showing? (form-initialise))
                :class             "btn btn-danger"]
     :popover  {:width             500
                :title             [:div "Arbitrary " [:strong "markup "] [:span {:style {:color "red"}} "title"]]
                :body              [popover-body form-data form-submit form-cancel show-tooltip?]}
     :options  {:arrow-length      15
                :arrow-width       10
                :backdrop-callback form-cancel
                :close-callback    form-cancel
                :backdrop-opacity  0.3}]))
