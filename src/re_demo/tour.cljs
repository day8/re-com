(ns re-demo.tour
  (:require [re-com.core   :refer [h-box v-box box gap align-style make-tour start-tour make-tour-nav button popover-content-wrapper popover-anchor-wrapper p]]
            [re-demo.utils :refer [panel-title title2 github-hyperlink status-text]]))


(defn demo
  []
  (let [demo-tour (make-tour [:step1 :step2 :step3 :step4])]
    (fn []
      [v-box
       :width    "600px"
       :gap      "10px"
       :children [[title2 "Demo"]
                  [p "The four buttons below are all part of this tour. Click on the first button to
                   start the tour, then use the navigation buttons to move through the tour."]
                  [p "Any individual component can be the included in the tour, as long as you wrap
                   it in a popover and conform the instrucitons on the left."]
                  [h-box
                   :height   "150px"
                   :gap      "30px"
                   :align    :start
                   :margin   "50px 0px 0px 0px"
                   :children [[popover-anchor-wrapper
                               :showing? (:step1 demo-tour)
                               :position :above-center
                               :anchor   [button
                                          :label    "Start Tour!"
                                          :on-click #(start-tour demo-tour)
                                          :style    {:font-weight "bold" :color "yellow"}
                                          :class    "btn-info"]
                               :popover [popover-content-wrapper
                                         :width    "250px"
                                         :title    [:strong "Tour 1 of 4"]
                                         :body     [:div "So, you clicked the button below and the tour started. Click the 'Next' button to proceed to the next step."
                                                   [make-tour-nav demo-tour]]]
                               :style   (align-style :align-self :center)]
                              [popover-anchor-wrapper
                               :showing? (:step2 demo-tour)
                               :position :below-center
                               :anchor   [button
                                          :label "another element in the tour"
                                          :class "btn-info"]
                               :popover [popover-content-wrapper
                                         :width    "250px"
                                         :title    [:strong "Tour 2 of 4"]
                                         :body     [:div "Here's the second tour popover. Now you can advance to the next one, or go back to the first, or finish the tour by clicking the close 'X' button above."
                                                   [make-tour-nav demo-tour]]]
                               :style   (align-style :align-self :end)]
                              [popover-anchor-wrapper
                               :showing? (:step3 demo-tour)
                               :position :right-below
                               :anchor   [button
                                          :label "and another"
                                          :class "btn-info"
                                          :style (when (:step3 demo-tour) {:position "relative" :z-index 10})] ;; Make the anchor appear above the backdrop
                               :popover [popover-content-wrapper
                                         :width            "250px"
                                         :on-cancel        #(reset! (:step3 demo-tour) false)
                                         :backdrop-opacity 0.5
                                         :title            [:strong "Tour 3 of 4"]
                                         :body             [:div "This is the penultimate tour popover. Using the backdrop feature, you can focus attention on the item you are explaining."
                                                            [make-tour-nav demo-tour]]]
                               :style   (align-style :align-self :center)]
                              [popover-anchor-wrapper
                               :showing? (:step4 demo-tour)
                               :position :above-center
                               :anchor   [button
                                          :label "last one"
                                          :class "btn-info"]
                               :popover [popover-content-wrapper
                                         :width    "420px"
                                         :title    [:strong "Tour 4 of 4"]
                                         :body     [:div "Lucky last tour popover. The tour component renders a 'Finish' button instead of a 'Next button for the last popover."
                                                    [make-tour-nav demo-tour]]]]]]]])))


(defn panel2
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title  "Tour Components"
                            "src/re_com/tour.cljs"
                            "src/re_demo/tour.cljs"]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :width    "450px"
                           :gap      "10px"
                           :children [[title2 "Notes"]
                                      [status-text "Alpha" {:color "#EA6B00"}]

                                      [p "To create a tour:"]
                                      [:ul
                                       [:li.spacer "Make a tour object, something like: " [:br]
                                        [:code "(let [demo-tour (make-tour [:step1 :step2 :step3])])"] "."]
                                       [:li.spacer "Then, wrap each anchor components in your tour with a popover component."]
                                       [:li.spacer "Each each such popover the " [:code ":showing?"] " parameter should look like this: "
                                        [:br] [:code ":showing? (:step1 demo-tour)"] "."]]
                                      [p "To add navigation buttons to a popover, add the following component to
                                       the end of your popover's "
                                       [:code ":body"] " markup: " [:br] [:code "[make-tour-nav demo-tour]"] "."]
                                      [p "To start the tour, call: " [:code "(start-tour demo-tour)"] "."]
                                      [p "To finish the tour, call: " [:code "(finish-tour demo-tour)"] "."]]]
                          [v-box
                           :gap       "150px"
                           :size      "auto"
                           :min-width "400px"
                           :children  [[demo]]]]]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
