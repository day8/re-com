(ns re-demo.popovers
  (:require [re-com.core                 :refer [label button checkbox]]
            [re-demo.util                :refer [title]]
            [re-com.box                  :refer [h-box v-box box gap line scroller border]]
            [re-com.popover              :refer [popover-content-wrapper popover-anchor-wrapper make-link]]
            [re-demo.popover-dialog-demo :as    popover-dialog-demo]
            [re-com.dropdown             :refer [single-dropdown]]
            [reagent.core                :as    reagent]))


(defn simple-popover-demo
  []
  (let [showing?          (reagent/atom false)
        title?            (reagent/atom true)
        close-button?     (reagent/atom false)
        body?             (reagent/atom true)
        no-clip?          (reagent/atom false)
        add-scroller?     (reagent/atom false)
        on-cancel?        (reagent/atom false)
        backdrop-opacity? (reagent/atom false)
        curr-position     (reagent/atom :below-center)
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
                           {:id :right-below  :label ":right-below "}]]
    (fn []
      (let [cancel-popover #(reset! showing? false)]
        [v-box
         :children [[title "Button Popover"]
                    [h-box
                     :gap      "50px"
                     :children [[v-box
                                 :width    "500px"
                                 :margin   "20px 0px 0px 0px"
                                 :style    {:font-size "small"}
                                 :children [[:div.h4 "Notes:"]
                                            [:ul
                                             [:li "You can link (anchor) a popover to arbitrary markup."]
                                             [:li "The connection between the anchor and the popover is achieved exclusively using CSS.
                                                   No matter how dramitcally the window is re-sized and/or controls repositioned while popped-up, it stays glued."]
                                             [:li "To create a popover, wrap your desired anchor with the " [:code "popover-anchor-wrapper"] " function. The arguments are:"]
                                             [:ul
                                              [:li  [:code ":showing?"] " - The atom used to show/hide the popover."]
                                              [:li  [:code ":position"] " - A position keyword specifying where the popover is attached to the anchor. See the demo to the right for the values."]
                                              [:li  [:code ":anchor"] " - The markup of the anchor to wrap."]
                                              [:li  [:code ":popover"] " - The markup of the popover body."]]
                                             [:li "A helper function is available for the body, called " [:code "popover-body-wrapper"] ". The main arguments are:"]
                                             [:ul
                                              [:li  [:code ":title"] " - Title of the popover. Can be ommitted."]
                                              [:li  [:code ":close-button?"] " - Add close button functionality. Default is true."]
                                              [:li  [:code ":body"] " - Body markup of the popover."]
                                              [:li  [:code ":on-cancel"] " - The callback for when any cancel event is triggered."]
                                              [:li  [:code ":no-clip?"] " - Prevents clipping and offset issues when popover is in scrollers. Trade-off is that it is fixed while it's popped up."]]]]]
                                [v-box
                                 :gap      "30px"
                                 :margin   "20px 0px 0px 0px"
                                 :children [[h-box
                                             :gap      "30px"
                                             :children [[v-box
                                                         :width    "200px"
                                                         :height   "300px"
                                                         :align    :center
                                                         :style    {:border   "1px solid lightgrey"             ;; turn a v-box into a border-scroller - this is a special case
                                                                    :overflow (when @add-scroller? "overlay")}  ;; Use overlay instead of scroll, otherwise things jump around
                                                         :children [[:span {:style {:flex "inherit" :color "lightgrey"}} (clojure.string/join (repeat 42 "text "))]
                                                                    [popover-anchor-wrapper
                                                                     :showing? showing?
                                                                     :position @curr-position
                                                                     :anchor   [button
                                                                                :label (if @showing? "Pop-down" "Click me")
                                                                                :on-click #(reset! showing? (not @showing?))
                                                                                :class "btn-success"]
                                                                     :popover  [popover-content-wrapper
                                                                                :showing?         showing?
                                                                                :position         @curr-position
                                                                                :no-clip?         @no-clip?
                                                                                :backdrop-opacity (when @backdrop-opacity? 0.3)
                                                                                :on-cancel        (when @on-cancel? cancel-popover)
                                                                                :title            (when @title? (if @no-clip? "no-clip? popover" "Popover happening"))
                                                                                :close-button?    @close-button?
                                                                                :body             (when @body?
                                                                                                    (if @no-clip?
                                                                                                      [:span {:style {:color "brown"}} [:strong "NOTE: "]
                                                                                                       "In this mode, the popover will not be clipped within the scroller but it
                                                                                                        will also not move when scrolling occurs while it's popped up. However, the next time it is popped up,
                                                                                                        the correct position will be recalculated."]
                                                                                                      "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup.
                                                                                                       Click the button again to cause a pop-down."))]]
                                                                    [:span {:style {:flex "inherit" :color "lightgrey"}} (clojure.string/join (repeat (if @add-scroller? 98 49) "text "))]]]
                                                        [v-box
                                                         :gap      "15px"
                                                         :align    :start
                                                         :children [[label :style {:font-style "italic"} :label "parameters:"]
                                                                    [h-box
                                                                     :gap      "20px"
                                                                     :align    :start
                                                                     :children [[checkbox
                                                                                 :label     ":title"
                                                                                 :model     title?
                                                                                 :on-change (fn [val]
                                                                                              (reset! title? val)
                                                                                              (cancel-popover))]
                                                                                [checkbox
                                                                                 :label     ":close-button?"
                                                                                 :model     close-button?
                                                                                 :on-change (fn [val]
                                                                                              (reset! close-button? val)
                                                                                              (cancel-popover))]
                                                                                [checkbox
                                                                                 :label     ":body"
                                                                                 :model     body?
                                                                                 :on-change (fn [val]
                                                                                              (reset! body? val)
                                                                                              (cancel-popover))]]]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox
                                                                                 :label     "add backdrop (catches clicks away from popover)"
                                                                                 :model     on-cancel?
                                                                                 :on-change (fn [val]
                                                                                              (reset! on-cancel? val)
                                                                                              (cancel-popover))]
                                                                                (when @on-cancel?
                                                                                  [checkbox
                                                                                   :label     (str ":backdrop-opacity " (if @backdrop-opacity? "(0.3)" "(0.0)"))
                                                                                   :model     backdrop-opacity?
                                                                                   :on-change (fn [val]
                                                                                                (reset! backdrop-opacity? val)
                                                                                                (cancel-popover))])]]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox
                                                                                 :label     "add scroll bars to box"
                                                                                 :model     add-scroller?
                                                                                 :on-change (fn [val]
                                                                                              (reset! add-scroller? val)
                                                                                              (cancel-popover))]
                                                                                [checkbox
                                                                                 :label     ":no-clip?"
                                                                                 :model     no-clip?
                                                                                 :on-change (fn [val]
                                                                                              (reset! no-clip? val)
                                                                                              (cancel-popover))]]]
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
  (let [showing? (reagent/atom false)
        pos      :right-below]
    (fn []
      [v-box
       :children [[title "Hyperlink Popover"]
                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :width    "500px"
                               :margin   "20px 0px 0px 0px"
                               :style    {:font-size "small"}
                               :children [[:ul
                                           [:li "A " [:code "make-link"] " helper function is provided to make creating popover links easy. Use this for the anchor."]
                                           [:li "This one has the " [:code ":toggle-on"] " argument of " [:code "make-link"] " set to " [:code ":click"] "."]]]]
                              [v-box
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-anchor-wrapper
                                           :showing? showing?
                                           :position pos
                                           :anchor   [make-link
                                                      :showing?  showing?
                                                      :toggle-on :click
                                                      :label     "click link popover"]
                                           :popover [popover-content-wrapper
                                                     :showing? showing?
                                                     :position pos
                                                     :title    "Popover Title"
                                                     :body     "popover body"]]]]]]]])))


(defn proximity-popover-demo
  []
  (let [showing? (reagent/atom false)
        pos      :above-center]
    (fn []
      [v-box
       :children [[title "Proximity Popover (tooltip)"]
                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :width    "500px"
                               :margin   "20px 0px 0px 0px"
                               :style    {:font-size "small"}
                               :children [[:ul
                                           [:li "This one has the " [:code ":toggle-on"] " argument of the helper function set to " [:code ":mouse"] "."]
                                           [:li "Also note that the  " [:code ":title"] " argument of the  " [:code "popover-content-wrapper"]
                                            " function is omitted, so none is shown."]]]]
                              [v-box
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-anchor-wrapper
                                           :showing? showing?
                                           :position pos
                                           :anchor   [make-link
                                                      :showing?  showing?
                                                      :toggle-on :mouse
                                                      :label     "tooltip popover"]
                                           :popover [popover-content-wrapper
                                                     :showing?      showing?
                                                     :position      pos
                                                     :body          "popover body (without a title specified) makes a great tooltip component"]]]]]]]])))


(defn complex-popover-demo
  []
  [v-box
   :children [[title "Complex Popover (dialog box)"]
              [h-box
               :gap      "50px"
               :children [[v-box
                           :width    "500px"
                           :margin   "20px 0px 0px 0px"
                           :style    {:font-size "small"}
                           :children [[:ul
                                       [:li "Popover-based dialogs are also possible, allowing for any number and any type of input fields or cutom input components. Here is a simple example."]
                                       [:li "The " [:code "popover-content-wrapper"] " function is friendly to dialog coding patterns, so the cancel event code can be defined once and used
                                             for the Cancel button, the close button in the title and when the backdrop is clicked."]]]]
                          [v-box
                           :gap      "30px"
                           :margin   "20px 0px 0px 0px"
                           :children [[popover-dialog-demo/popover-dialog-demo]]]]]]])


(defn panel
  []
  [v-box
   :children [[simple-popover-demo]
              [hyperlink-popover-demo]
              [proximity-popover-demo]
              [complex-popover-demo]
              [gap :height "180px"]]])
