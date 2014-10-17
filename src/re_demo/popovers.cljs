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
                               :margin  "20px 0px 0px 0px"       ;; TODO:  i supplied "start" (string) instead of :start and got runtime errors ... better protection
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
                               :margin  "20px 0px 0px 0px"        ;; TODO:  decide would we prefer to use :top-margin??
                               :children [[popover
                                           :position :right-below
                                           :showing? popover-showing?
                                           :anchor   [button
                                                      :label         (if @popover-showing? "Pop-down" "Click me")
                                                      :on-click      #(reset! popover-showing? (not @popover-showing?))
                                                      :class         "btn-success"]
                                           :popover  {:title         "A Popover Is Happening"
                                                      :close-button? false
                                                      :body          "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup. Click the button again to cause a pop-down."}
                                           ;:options  {:arrow-length 30}
                                           ]
                                          [popover-form-demo/popover-form-demo]]]]]]])))



(defn hyperlink-popover-demo
  []
  (let [popover-showing?  (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Hyperlink Popover"]
                  [h-box
                   :gap "50px"
                   :children []]]])))


(defn proximity-popover-demo
  []
  (let [popover-showing?  (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Proximity Popover"]
                  [h-box
                   :gap "50px"
                   :children []]]])))


(defn modal-popover-demo
  []
  (let [popover-showing?  (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Modal Popover"]
                  [h-box
                   :gap "50px"
                   :children []]]])))

(defn panel
  []
  [v-box
   :children [[simple-popover-demo]
              [hyperlink-popover-demo]
              [proximity-popover-demo]
              [modal-popover-demo]
              #_[complicated-form-popover]]])