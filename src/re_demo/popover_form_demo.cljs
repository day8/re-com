(ns re-demo.popover-form-demo
  (:require [reagent.core    :as    reagent]
            [re-com.util     :refer [deref-or-value]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.core     :refer  [button checkbox radio-button]]
            [re-com.dropdown :refer [single-dropdown]]
            [re-com.popover  :refer [popover backdrop popover-border popover-title popover-content popover-anchor-wrapper]]))


(defn popover-body
  [popover-form-showing? position form-data on-change]
  (println "   First-Render: " @form-data)
  (let [form-data             (reagent/atom (deref-or-value form-data))
        submit-popover-form   (fn [new-form-data]
                                (reset! popover-form-showing? false)
                                (on-change new-form-data))
        cancel-popover-form   #(reset! popover-form-showing? false)
        show-tooltip?         (reagent/atom (= (:show-tooltip? @form-data) "2"))]
    (fn []
      (println "         Render: " @form-data)
      [popover-content
       :showing?         popover-form-showing?
       :on-cancel        cancel-popover-form
       :position         position
       :width            500
       :backdrop-opacity 0.3
       :title            "This is the title"
       :body             [(fn []                                   ;; NOTE: THIS IS NASTY BUT REQUIRED (OTHERWISE FORM WILL NOT BE UPDATED WHEN ATOMS CHANGES)
                            [v-box
                             :children [[:span "The body of a popover can act like a dialog box containg standard input controls."]
                                        #_[label ;; TODO: asdlaldsjalksd
                                         :label "This popover contains an embedded form..."]
                                        [gap :size "15px"]
                                        [h-box
                                         :children [[v-box
                                                     :size "auto"
                                                     :children [[radio-button
                                                                 :label "Don't show extra popover"
                                                                 :value "1"
                                                                 :model (:show-tooltip? @form-data)
                                                                 :on-change (fn []
                                                                              (swap! form-data assoc :show-tooltip? "1")
                                                                              (reset! show-tooltip? false))]
                                                                [radio-button
                                                                 :label "Show extra popover"
                                                                 :value "2"
                                                                 :model (:show-tooltip? @form-data)
                                                                 :on-change (fn []
                                                                              (swap! form-data assoc :show-tooltip? "2")
                                                                              (reset! show-tooltip? true))]]]]]
                                        [gap :size "15px"]
                                        [:hr {:style {:margin "10px 0 10px"}}] ;; TODO: Change to line
                                        [h-box
                                         :gap "10px"
                                         :children [[button
                                                     :label [:span [:span.glyphicon.glyphicon-ok] " Apply"]
                                                     :on-click #(submit-popover-form @form-data)
                                                     :class "btn-primary"]
                                                    [popover
                                                     :position :right-below
                                                     :showing? show-tooltip?
                                                     :anchor   [button
                                                                :label [:span [:span.glyphicon.glyphicon-remove] " Cancel"]
                                                                :on-click cancel-popover-form]
                                                     :popover {:title         "This is the cancel button"
                                                               :close-button? false
                                                               :body          "You can even have a popover over a popover!"}]]]]])]])))


(defn popover-form-demo
  []
  (let [popover-form-showing? (reagent/atom false)
        form-data             (reagent/atom {:show-tooltip? "2"})
        on-change             (fn [new-form-data]
                                (reset! form-data new-form-data)
                                (println "Data changed to: " @form-data))
        position              :right-center]
    (fn []
      [popover-anchor-wrapper
       :position position
       :showing? popover-form-showing?
       :anchor [button
                :label "Dialog box"
                :on-click #(reset! popover-form-showing? true)
                :class "btn btn-danger"]
       :popover [popover-body popover-form-showing? position form-data on-change]
       ])))


#_(defn popover-body-old
  [form-data form-submit form-cancel]
  (println "INITIAL popover-body" @form-data)
  (let [form-data     (reagent/atom (deref-or-value form-data))
        show-tooltip? (reagent/atom false)]
    (fn []
      (println "RENDER popover-body" @form-data)
      [v-box
       :children [[:span "This popover contains an embedded form..."]
                  [:h4 "Checkboxes and Radio buttons"]
                  [h-box
                   :children [[v-box
                               :size "auto"
                               :children [[checkbox
                                           :label "Red (toggle disabled)"
                                           :model (:red @form-data)
                                           :on-change #(swap! form-data assoc :red %)]
                                          [checkbox
                                           :label "Green (initially checked)"
                                           :model (:green @form-data)
                                           :on-change #(swap! form-data assoc :green %)]
                                          [checkbox
                                           :label (str "Blue" (when (:red @form-data) " (disabled)"))
                                           :model (:blue @form-data)
                                           :disabled (:red @form-data)
                                           :on-change #(swap! form-data assoc :blue %)]]]
                              [v-box
                               :size "auto"
                               :children [[radio-button
                                           :label "Hue"
                                           :value "1"
                                           :model (:colour-spec @form-data)
                                           :on-change #(swap! form-data assoc :colour-spec "1")]
                                          [radio-button
                                           :label "Saturation (initially checked)"
                                           :value "2"
                                           :model (:colour-spec @form-data)
                                           :on-change #(swap! form-data assoc :colour-spec "2")]
                                          [radio-button
                                           :label (str "Luminance" (when (:red @form-data) " (disabled)"))
                                           :value "3"
                                           :model (:colour-spec @form-data)
                                           :disabled (:red @form-data)
                                           :on-change #(swap! form-data assoc :colour-spec "3")]]]]]
                  [:em [:small "Note: Hover over the cancel button to see a popover over a popover"]]
                  [:hr {:style {:margin "10px 0 10px"}}]
                  [h-box
                   :gap "10px"
                   :children [[button
                               :label [:span [:span.glyphicon.glyphicon-ok] " Apply"]
                               :on-click #(form-submit @form-data)
                               :class "btn-primary"]
                              [popover
                               :position :right-below
                               :showing? show-tooltip?
                               :anchor [:button             ;; Standard [button] can't handle mousr-over/out
                                        {:class         "btn"
                                         :style         {:flex "none"}
                                         :on-click      form-cancel
                                         :on-mouse-over #(reset! show-tooltip? true)
                                         :on-mouse-out  #(reset! show-tooltip? false)}
                                        [:span [:span.glyphicon.glyphicon-remove] " Cancel"]]
                               :popover {:title         "Tooltip"
                                         :close-button? false
                                         :body          "You can even have a popover over a popover!"}]]]]])))


#_(defn popover-form-demo-old
  []
  (let [popover-form-showing? (reagent/atom false)
        form-data             (reagent/atom {:red         false
                                             :green       true
                                             :blue        false
                                             :colour-spec "2"})
        form-submit           (fn [new-form-data]
                                (reset! form-data new-form-data)
                                (reset! popover-form-showing? false))
        form-cancel           #(reset! popover-form-showing? false)]
    [popover
     :position :below-center
     :showing? popover-form-showing?
     :anchor   [button
                :label             "Original Popover Form"
                :on-click          #(reset! popover-form-showing? true)
                :class             "btn btn-danger"]
     :popover  {:width             500
                :title             [:div "Arbitrary " [:strong "markup "] [:span {:style {:color "red"}} "title"]]
                :body              [popover-body-old form-data form-submit form-cancel]}
     :options  {:arrow-length      15
                :arrow-width       10
                :backdrop-callback form-cancel
                :close-callback    form-cancel
                :backdrop-opacity  0.3}]))
