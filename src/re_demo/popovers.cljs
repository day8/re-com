(ns re-demo.popovers
  (:require [re-com.core                 :refer [label button checkbox]]
            [re-demo.util                :refer [title]]
            [re-com.box                  :refer [h-box v-box box gap line]]
            [re-com.popover              :refer [popover-content-wrapper popover-anchor-wrapper]]
            [re-demo.popover-dialog-demo :as    popover-dialog-demo]
            [re-com.dropdown             :refer [single-dropdown]]
            [reagent.core                :as    reagent]))


(defn simple-popover-demo
  []
  (let [showing?          (reagent/atom false)
        title?            (reagent/atom true)
        close-button?     (reagent/atom false)
        body?             (reagent/atom true)
        on-cancel?        (reagent/atom false)
        backdrop-opacity? (reagent/atom false)
        positions         [{:id :above-left   :label ":above-left  "}
                           {:id :above-center :label ":above-center"}
                           {:id :above-right  :label ":above-right "}
                           {:id :below-left   :label ":below-left  "}
                           {:id :below-center :label ":below-center"}
                           {:id :below-right  :label ":below-right "}
                           {:id :left-above   :label ":left-above  "}
                           {:id :left-center  :label ":left-center "}
                           {:id :left-below   :label ":left-below  "}
                           {:id :right-above  :label ":right-above "}
                           {:id :right-center :label ":right-center"}
                           {:id :right-below  :label ":right-below "}]
        curr-position     (reagent/atom :below-center)]
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
                                             :children [[box
                                                         :width "180px"
                                                         :align :center
                                                         :child [popover-anchor-wrapper
                                                                 :showing? showing?
                                                                 :position @curr-position
                                                                 :anchor   [button
                                                                            :label    (if @showing? "Pop-down" "Click me")
                                                                            :on-click #(reset! showing? (not @showing?))
                                                                            :class    "btn-success"]
                                                                 :popover [popover-content-wrapper
                                                                           :showing?         showing?
                                                                           :position         @curr-position
                                                                           :backdrop-opacity (when @backdrop-opacity? 0.3)
                                                                           :on-cancel        (when @on-cancel? cancel-popover)
                                                                           :title            (when @title? "Popover happening")
                                                                           :close-button?    @close-button?
                                                                           :body             (when @body?
                                                                                               "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup.
                                                                                                Click the button again to cause a pop-down.")]]]
                                                        [v-box
                                                         :gap      "15px"
                                                         :align    :start
                                                         :children [[label :style {:font-style "italic"} :label "parameters:"]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox
                                                                                 :label ":title"
                                                                                 :model title?
                                                                                 :on-change (fn [val]
                                                                                              (reset! title? val)
                                                                                              (cancel-popover))]
                                                                                [checkbox
                                                                                 :label ":close-button?"
                                                                                 :model close-button?
                                                                                 :on-change (fn [val]
                                                                                              (reset! close-button? val)
                                                                                              (cancel-popover))]
                                                                                [checkbox
                                                                                 :label ":body"
                                                                                 :model body?
                                                                                 :on-change (fn [val]
                                                                                              (reset! body? val)
                                                                                              (cancel-popover))]]]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox
                                                                                 :label "add backdrop (catches clicks away from popover)"
                                                                                 :model on-cancel?
                                                                                 :on-change (fn [val]
                                                                                              (reset! on-cancel? val)
                                                                                              (cancel-popover))]
                                                                                (when @on-cancel?
                                                                                  [checkbox
                                                                                   :label (str ":backdrop-opacity " (if @backdrop-opacity? "(0.3)" "(0.0)"))
                                                                                   :model backdrop-opacity?
                                                                                   :on-change (fn [val]
                                                                                                (reset! backdrop-opacity? val)
                                                                                                (cancel-popover))])]]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :center
                                                                     :children [[label :label ":position"]
                                                                                [single-dropdown
                                                                                 :choices    positions
                                                                                 :model      curr-position
                                                                                 :width      "140px"
                                                                                 :max-height "600px"
                                                                                 :on-change  (fn [val]
                                                                                               (reset! curr-position val)
                                                                                               (cancel-popover))]]]]]]]]]]]]]))))


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