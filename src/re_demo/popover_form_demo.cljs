(ns re-demo.popover-form-demo
  (:require [reagent.core    :as    reagent]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.core    :refer  [button checkbox radio-button]]
            [re-com.dropdown :refer [single-dropdown]]
            [re-com.popover  :refer [popover make-button make-link]]))


;(def show-popover?     (reagent/atom false))
;(def show-tooltip?     (reagent/atom false))
;(def initial-form-data (reagent/atom {}))
;(def form-data         (reagent/atom {:checkbox1      false
;                                      :checkbox2      true
;                                      :checkbox3      false
;                                      :radio-group    "2"}))
;
;
;(defn form-initialise
;  []
;  (reset! initial-form-data @form-data)
;  (reset! show-popover? true))
;
;
;(defn form-submit
;  []
;  (reset! show-popover? false))
;
;
;(defn form-cancel
;  []
;  (reset! form-data @initial-form-data)
;  (reset! show-popover? false)
;  (reset! show-tooltip? false))


(defn popover-body
  [form-data form-submit form-cancel show-tooltip?]
  [v-box
   :children [[:h4 "Checkboxes and Radio buttons"]
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
                           :anchor   [:button
                                      {:class    "btn"
                                       :style    {:flex "none"}
                                       :on-click form-cancel
                                       :on-mouse-over #(reset! show-tooltip? true)
                                       :on-mouse-out  #(reset! show-tooltip? false)}
                                      [:span [:span.glyphicon.glyphicon-remove] " Cancel"]]
                           :popover {:title "Tooltip"
                                     :body  "You can even have a popover over a popover!"}]]]]])


(defn popover-title
  [form-cancel]
  [:div "Arbitrary " [:strong "markup "] [:span {:style {:color "red"}} "title"]
   [button
    :label "×"
    :on-click form-cancel
    :class "close"
    :style {:font-size "36px" :margin-top "-8px"}]])


(defn popover-form-demo
  []
  (let [popover-form-showing? (reagent/atom false)

        ;; ========================= NEW STUFF START

        show-tooltip?         (reagent/atom false)
        initial-form-data     (reagent/atom {})
        form-data             (reagent/atom {:checkbox1      false
                                             :checkbox2      true
                                             :checkbox3      false
                                             :radio-group    "2"})
        form-initialise       (fn []
                                (reset! initial-form-data @form-data)
                                (reset! popover-form-showing? true))

        form-submit           (fn []
                                (reset! popover-form-showing? false))
        form-cancel           (fn []
                                (reset! form-data @initial-form-data)
                                (reset! popover-form-showing? false)
                                (reset! show-tooltip? false))

        ;; ========================= NEW STUFF END

        popover-content       {:width             500
                               :title             [popover-title form-cancel]
                               :close-button?     false            ;; We have to add our own close button because it does more than simply close the popover
                               :body              [popover-body form-data form-submit form-cancel show-tooltip?]}
        popover-options       {:arrow-length      15
                               :arrow-width       10

                               :backdrop-callback form-cancel
                               ;; ADD...
                               ;:close-callback    form--cancel
                               ;:cancel-callback   form--cancel ;; This would be specific for forms in popovers
                               ;:submit-callback   form-submit ;; This would be specific for forms in popovers

                               ;; OR REPLACE ALL WITH...
                               ;:submit-callback   form-submit
                               ;:cancel-callback   form--cancel

                               :backdrop-opacity  0.3}]
    [popover
     :position :below-center
     :showing? popover-form-showing?
     :anchor   [button
                :label    "Popover Form"
                :on-click #(if-not @popover-form-showing?
                            (form-initialise))
                :class    "btn btn-danger"]
     :popover  popover-content
     :options  popover-options]))
