(ns re-demo.tour
  (:require [re-com.util     :as    util]
            [re-com.core     :refer [button label input-text checkbox title]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown find-choice filter-choices-by-keyword]]
            [re-com.tour     :refer [make-tour start-tour make-tour-nav]]
            [re-com.popover  :refer [popover-content-wrapper popover-anchor-wrapper make-button]]
            [reagent.core    :as    reagent]))


(def demos [{:id 1 :label "Basic example"}
            {:id 2 :label "Other variations"}])


(defn demo1
  []
  (let [demo-tour (make-tour [:step1 :step2 :step3 :step4])]
    (fn []
      [:div
       [:p "The four buttons below are all part of this tour. Click on the first button to start the tour, then use the navigation buttons to move through the tour."]
       [:p "Any individual component can be the included in the tour, as long as you wrap it in a popover and conform the instrucitons on the left."]
       [h-box
        :children [[popover-anchor-wrapper
                    :showing? (:step1 demo-tour)
                    :position :above-center
                    :anchor   [button
                               :label "Start Tour!"
                               :on-click #(start-tour demo-tour)
                               :style {:font-weight "bold" :color "yellow"}
                               :class "btn-info"]
                    :popover [popover-content-wrapper
                              :showing?         (:step1 demo-tour)
                              :position         :above-center
                              :title            [:strong "Tour 1 of 4"]
                              :body             [:div "So this is the first tour popover"
                                                 [make-tour-nav demo-tour]]]]
                   [popover-anchor-wrapper
                    :showing? (:step2 demo-tour)
                    :position :above-center
                    :anchor   [make-button
                               :showing? (:step2 demo-tour)
                               :type     "info"
                               :label    "Tour 2"]
                    :popover [popover-content-wrapper
                              :showing?         (:step2 demo-tour)
                              :position         :above-center
                              :title            [:strong "Tour 2 of 4"]
                              :body             [:div "nd this is the second tour popover"
                                                 [make-tour-nav demo-tour]]]]
                   [popover-anchor-wrapper
                    :showing? (:step3 demo-tour)
                    :position :above-center
                    :anchor   [make-button
                               :showing? (:step3 demo-tour)
                               :type     "info"
                               :label    "Tour 3"]
                    :popover [popover-content-wrapper
                              :showing?         (:step3 demo-tour)
                              :position         :above-center
                              :title            [:strong "Tour 3 of 4"]
                              :body             [:div "Penultimate tour popover"
                                                 [make-tour-nav demo-tour]]]]
                   [popover-anchor-wrapper
                    :showing? (:step4 demo-tour)
                    :position :above-center
                    :anchor   [make-button
                               :showing? (:step4 demo-tour)
                               :type     "info"
                               :label    "Tour 4"]
                    :popover [popover-content-wrapper
                              :showing?         (:step4 demo-tour)
                              :position         :above-center
                              :title            [:strong "Tour 4 of 4"]
                              :body             [:div "Lucky last tour popover"
                                                 [make-tour-nav demo-tour]]]]

                   ]]])))


(defn demo2
  []
  (let [demo-tour (make-tour [:step1 :step2 :step3 :step4])]
    (fn []
      [:div
       [:p "Tour popovers can be attched to any."]
       [:p "Any individual component can be the included in the tour, as long as you wrap it in a popover and conform the instrucitons on the left."]
       [h-box
        :children [[:p "Tour popovers can be attched to any."]
                   ]]])))


(defn notes
  []
  [v-box
   :width    "500px"
   :style    {:font-size "small"}
   :children [[:div.h4 "General notes"]
              [:ul
               [:li "To create a tour component, do the following:"
                [:ul
                 [:li.spacer "Make a tour object, declaring all the steps in your tour."]
                 [:li.spacer "For example: " [:br] [:code "(let [demo-tour (make-tour [:step1 :step2 :step3])])"]]
                 [:li.spacer "Wrap all the components in your tour with a popover component."]
                 [:li.spacer "The popover " [:code ":showing?"] " parameter should look like this: " [:br] [:code ":showing? (:step1 demo-tour)"]]
                 [:li.spacer "To add navigation buttons to the popover, add the following component to the end of your popover's " [:code ":body"] " markup: " [:br] [:code "[make-tour-nav demo-tour]"]]
                 [:li.spacer "To start the tour, call: " [:code "(start-tour demo-tour)"]]
                 [:li.spacer "To finish the tour, call: " [:code "(finish-tour demo-tour)"]]]]]]])


(defn panel
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :children [[title "Tour"]
                  [h-box
                   :gap      "50px"
                   :children [[notes]
                              [v-box
                               :gap       "15px"
                               :size      "auto"
                               :min-width "500px"
                               :margin    "20px 0px 0px 0px"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a demo"]
                                                       [single-dropdown
                                                        :choices   demos
                                                        :model     selected-demo-id
                                                        :width     "300px"
                                                        :on-change #(reset! selected-demo-id %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           (case @selected-demo-id
                                             1 [demo1]
                                             2 [demo2])]]]]]])))
