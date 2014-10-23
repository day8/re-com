(ns re-demo.popovers
  (:require [re-com.core               :refer  [button]]
            [re-demo.util              :refer  [title]]
            [re-com.box                :refer  [h-box v-box box gap line]]
            [re-com.popover            :refer  [popover]]
            [re-demo.popover-form-demo :as     popover-form-demo]
            [reagent.core              :as     reagent]))


(defn simple-popover-demo
  []
  (let [popover-showing? (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Button Popover"]
                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :width   "500px"
                               :margin  "20px 0px 0px 0px"
                               :children [
                                           [:div.h4 "Notes:"]
                                           [:ul
                                            [:li "You can link (anchor) a popover to arbitrary markup."]
                                            [:li "On the right, is a popup linked to the button."]
                                            [:li "Initially, it is configured to show below-right of the anchor button."]
                                            [:li "Marvel at what happens if you resize the browser window. It stays on its anchor."]
                                            [:li "If window contents change while popped-up, it stays glued. "]]]]
                              [v-box
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"
                               :children [[h-box
                                           :gap      "10px"
                                           :children [[popover
                                                       :position :right-below
                                                       :showing? popover-showing?
                                                       :anchor   [button
                                                                  :label         (if @popover-showing? "Pop-down" "Click me")
                                                                  :on-click      #(reset! popover-showing? (not @popover-showing?))
                                                                  :class         "btn-success"]
                                                       :popover  {:title         "A Popover Is Happening"
                                                                  :close-button? false
                                                                  :body          "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup. Click the button again to cause a pop-down."}]
                                                      [popover
                                                       :position :right-below
                                                       :showing? popover-showing?
                                                       :anchor   [button
                                                                  :label         (if @popover-showing? "Pop-down" "Click me (new)")
                                                                  :on-click      #(reset! popover-showing? (not @popover-showing?))
                                                                  :class         "btn-success"]
                                                       :popover  {:title         "A Popover Is Happening"
                                                                  :close-button? false
                                                                  :body          "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup. Click the button again to cause a pop-down."}]]]]]]]]])))


(defn hyperlink-popover-demo
  []
  (let [popover-showing?  (reagent/atom false)]
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
                               :children [[:span "TODO"]]]]]]])))


(defn proximity-popover-demo
  []
  (let [popover-showing?  (reagent/atom false)]
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
                               :children [[:span "TODO"]]]]]]])))


(defn complex-popover-demo
  []
  (let [popover-showing?  (reagent/atom false)]
    (fn []
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
                               :children [[popover-form-demo/popover-form-demo]]]]]]])))


(defn panel
  []
  [v-box
   :children [[simple-popover-demo]
              [hyperlink-popover-demo]
              [proximity-popover-demo]
              [complex-popover-demo]]])