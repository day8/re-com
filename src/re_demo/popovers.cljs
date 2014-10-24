(ns re-demo.popovers
  (:require [re-com.core                 :refer  [label button checkbox]]
            [re-demo.util                :refer  [title]]
            [re-com.box                  :refer  [h-box v-box box gap line]]
            [re-com.popover              :refer  [popover popover-content-wrapper popover-anchor-wrapper]]
            [re-demo.popover-dialog-demo :as     popover-dialog-demo]
            [reagent.core                :as     reagent]))


(defn simple-popover-demo
  []
  (let [showing?          (reagent/atom false)
        title?            (reagent/atom true)
        close-button?     (reagent/atom false)
        body?             (reagent/atom true)
        on-cancel?        (reagent/atom false)
        backdrop-opacity? (reagent/atom false)]
    (fn []
      (let [cancel-popover #(reset! showing? false)]
        [v-box
         :children [[title "Button Popover"]
                    [h-box
                     :gap "50px"
                     :children [[v-box
                                 :width "500px"
                                 :margin "20px 0px 0px 0px"
                                 :children [[:div.h4 "Notes:"]
                                            [:ul
                                             [:li "You can link (anchor) a popover to arbitrary markup."]
                                             [:li "On the right, is a popup linked to the button."]
                                             [:li "Initially, it is configured to show below-right of the anchor button."]
                                             [:li "Marvel at what happens if you resize the browser window. It stays on its anchor."]
                                             [:li "If window contents change while popped-up, it stays glued. "]]]]
                                [v-box
                                 :gap "30px"
                                 :margin "20px 0px 0px 0px"
                                 :children [[h-box
                                             :gap "10px"
                                             :children [;; Using the original popover function
                                                        #_[popover
                                                         :position :below-center
                                                         :showing? showing?
                                                         :anchor   [button
                                                                    :label         (if @showing? "Pop-down" "Click me")
                                                                    :on-click      #(reset! showing? (not @showing?))
                                                                    :class         "btn-success"]
                                                         :popover  {:title         "A Popover Is Happening"
                                                                    :close-button? false
                                                                    :body          "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup. Click the button again to cause a pop-down."}]

                                                        ;; Using the new method
                                                         [box
                                                          :width "180px"
                                                          :child [popover-anchor-wrapper
                                                                  :showing? showing?
                                                                  :position :below-center
                                                                  :anchor [button
                                                                           :label (if @showing? "Pop-down" "Click me")
                                                                           :on-click #(reset! showing? (not @showing?))
                                                                           :class "btn-success"]
                                                                  :popover [popover-content-wrapper
                                                                            :showing? showing?
                                                                            :position :below-center
                                                                            :backdrop-opacity (if @backdrop-opacity? 0.3 nil)
                                                                            :on-cancel (if @on-cancel? cancel-popover nil)
                                                                            :title (if @title? "Popover happening" nil)
                                                                            :close-button? @close-button?
                                                                            :body (if @body?
                                                                                    "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup.
                                                                                     Click the button again to cause a pop-down."
                                                                                    nil)]]
                                                          ]
                                                         [v-box
                                                          ;:width    (str width "px")
                                                          :gap "15px"
                                                          :align :start
                                                          :children [[label :style {:font-style "italic"} :label "parameters:"]
                                                                     [h-box
                                                                      :gap "20px"
                                                                      :align :start
                                                                      :children [[checkbox
                                                                                  :label ":title"
                                                                                  :model title?
                                                                                  :on-change #(reset! title? %)]
                                                                                 [checkbox
                                                                                  :label ":close-button?"
                                                                                  :model close-button?
                                                                                  :on-change #(reset! close-button? %)]
                                                                                 [checkbox
                                                                                  :label ":body"
                                                                                  :model body?
                                                                                  :on-change #(reset! body? %)]
                                                                                 [checkbox
                                                                                  :label ":on-cancel (adds backdrop)"
                                                                                  :model on-cancel?
                                                                                  :on-change #(reset! on-cancel? %)]
                                                                                 [checkbox
                                                                                  :label ":backdrop-opacity"
                                                                                  :model backdrop-opacity?
                                                                                  :on-change #(reset! backdrop-opacity? %)]]]]]
                                                         ]]]]]]]]))))


(defn hyperlink-popover-demo
  []
  (let [showing? (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Hyperlink Popover"]
                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :width   "500px"
                               :margin  "20px 0px 0px 0px"
                               :children [[:ul
                                           [:li "Notes go here."]]]]
                              [v-box
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"
                               :children [[label :label "TODO"]]]]]]])))


(defn proximity-popover-demo
  []
  (let [showing? (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Proximity Popover (tooltip)"]
                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :width   "500px"
                               :margin  "20px 0px 0px 0px"
                               :children [[:ul
                                           [:li "Notes go here."]]]]
                              [v-box
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"
                               :children [[label :label "TODO"]]]]]]])))


(defn complex-popover-demo
  []
  [v-box
   :children [[title "Complex Popover (dialog box)"]
              [h-box
               :gap "50px"
               :children [[v-box
                           :width   "500px"
                           :margin  "20px 0px 0px 0px"
                           :children [[:ul
                                       [:li "Notes go here."]]]]
                          [v-box
                           :gap     "30px"
                           :margin  "20px 0px 0px 0px"
                           :children [[popover-dialog-demo/popover-dialog-demo]]]]]]])


(defn panel
  []
  [v-box
   :children [[simple-popover-demo]
              [hyperlink-popover-demo]
              [proximity-popover-demo]
              [complex-popover-demo]]])