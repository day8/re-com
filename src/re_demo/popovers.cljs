(ns re-demo.popovers
  (:require [re-com.core                 :refer [at h-box v-box box gap line scroller border label title input-text checkbox radio-button button hyperlink hyperlink-href p single-dropdown popover-content-wrapper popover-anchor-wrapper popover-border popover-tooltip flex-child-style] :refer-macros [handler-fn]]
            [re-com.popover              :refer [popover-content-wrapper-args-desc popover-content-wrapper-parts-desc popover-anchor-wrapper-parts-desc popover-anchor-wrapper-args-desc popover-anchor-wrapper-parts-desc popover-border-args-desc popover-border-parts-desc popover-tooltip-args-desc popover-tooltip-parts-desc]]
            [re-demo.popover-dialog-demo :as    popover-dialog-demo]
            [re-com.util                 :refer [get-element-by-id px]]
            [re-demo.utils               :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text scroll-to-top]]
            [reagent.core                :as    reagent]))


(def curr-position (reagent/atom :below-center))
(def positions     [{:id :above-left   :label ":above-left  "}
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
                    {:id :right-below  :label ":right-below "}])


(defn see-demo-page
  []
  [:span
   "See "
   [hyperlink :src (at)
    :label "Popover Demos"
    :on-click #(do
                 (.open js/window "#/popovers" "_self")
                 (scroll-to-top (get-element-by-id "right-panel")))]
   " page."])

(defn simple-code-and-demo []
  (let [showing?        (reagent/atom false)]
    [h-box :src (at)
     :gap      "40px"
     :children [[:pre "[popover-anchor-wrapper
  :showing? showing?
  :position :right-below
  :anchor   [button
             :label    \"Anchor\"
             :on-click #(swap! showing? not)]
  :popover  [popover-content-wrapper
             :title    \"Title\"
             :body     \"Popover body text\"]]]"]
                [popover-anchor-wrapper :src (at)
                 :showing? showing?
                 :position :right-below
                 :anchor   [button :src (at)
                            :label    "Anchor"
                            :on-click #(swap! showing? not)]
                 :popover  [popover-content-wrapper :src (at)
                            :title    "Title"
                            :body     "Popover body text"]]]]))


(defn popover-component-hierarchy
  []
  (let [indent          20
        table-style     {:style {:border "2px solid lightgrey" :margin-right "10px"}}
        border          {:border "1px solid lightgrey" :padding "6px 12px"}
        border-style    {:style border}
        border-style-nw {:style (merge border {:white-space "nowrap"})}
        valign          {:vertical-align "top"}
        valign-style    {:style valign}
        valign-style-hd {:style (merge valign {:background-color "#e8e8e8"})}
        indent-text     (fn [level text] [:span {:style {:padding-left (px (* level indent))}} text])
        highlight-text  (fn [text & [color]] [:span {:style {:font-weight "bold" :color (or color "dodgerblue")}} text])
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box :src (at)
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[popover-anchor\n"
                      "   ...\n"
                      "   :parts {:wrapper {:class \"blah\"\n"
                      "                     :style { ... }\n"
                      "                     :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 (highlight-text "[popover-anchor-wrapper]"))]
                   [:td border-style-nw "rc-popover-anchor-wrapper"]
                   [:td border-style (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the anchor, popover, backdrop, everything."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-point-wrapper"]
                   [:td border-style (code-text ":point-wrapper")]
                   [:td border-style "Wraps the anchor component and the popover-point (which the actual popover points to)."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 (highlight-text ":anchor"))]
                   [:td border-style-nw "n/a"]
                   [:td border-style "n/a"]
                   [:td border-style "The " (code-text ":anchor") " argument of " (code-text "[popover-anchor-wrapper]") " is placed here. Could be before or after popover-point based on " (code-text ":position") " arg."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:div]")]
                   [:td border-style-nw "rc-popover-point"]
                   [:td border-style (code-text ":popover-point")]
                   [:td border-style "The point (width/height 0) which is placed at the center of the relevant side of the anchor, based on " (code-text ":position") " arg."]]
                  [:tr
                   [:td border-style-nw (indent-text 3 (highlight-text ":popover"))]
                   [:td border-style-nw "popover-content-wrapper"]
                   [:td border-style (code-text ":content-wrapper")]
                   [:td border-style [:span "The " (code-text ":popover") " argument of " (code-text "[popover-anchor-wrapper]") " is placed here. It should be a " (code-text "[popover-content-wrapper]") " which wraps the actual content of the popover (specified as " (code-text ":body") " arg below) and the backdrop if required, but it could also be your own component which returns a " (code-text "[popover-content-wrapper]") "."]]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[backdrop]")]
                   [:td border-style-nw "rc-backdrop"]
                   [:td border-style (code-text ":backdrop")]
                   [:td border-style "The (semi-)transparent backdrop between the popover and the rest of the screen. Calls " (code-text ":on-cancel") " when backdrop is clicked. Optional based on " (code-text ":on-cancel") " arg being set."]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[popover-border]")]
                   [:td border-style-nw "popover"]
                   [:td border-style (code-text ":border")]
                   [:td border-style "Wraps the content of the popover (and title and arrow). Includes the rounded white border and background."]]
                  [:tr
                   [:td border-style-nw (indent-text 5 "[popover-arrow]")]
                   [:td border-style-nw "popover-arrow"]
                   [:td border-style (code-text ":arrow")]
                   [:td border-style "SVG component."]]
                  [:tr
                   [:td border-style-nw (indent-text 5 "[popover-title]")]
                   [:td border-style-nw "popover-title"]
                   [:td border-style (code-text ":title")]
                   [:td border-style "Optional based on " (code-text ":title") " arg. Adds a close button if " (code-text ":on-cancel") " arg is set."]]
                  [:tr
                   [:td border-style-nw (indent-text 5 "[:div]")]
                   [:td border-style-nw "popover-content"]
                   [:td border-style ""]
                   [:td border-style "Exists to override the default popover padding."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 (highlight-text ":body"))]
                   [:td border-style-nw "n/a"]
                   [:td border-style "n/a"]
                   [:td border-style "The " (code-text ":body") " argument of " (code-text "[popover-content-wrapper]") " is placed here."]]]]]]))


(defn arg-lists
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "Popover Reference"
                           "src/re_com/popover.cljs"
                           "src/re_demo/popovers.cljs"]

              [title2 "The Basics"]
              [p "Here is the actual code for a very simple popover along with the popover it produces:"]
              [simple-code-and-demo]
              [p "See the Popover page, which provides a general overview of how it works."]
              [p "Or refer to the sections below, which provide a reference for each of the popover components, along with an advanced component hierarchy table at the end."]
              [line :src (at) :style {:margin-top "20px"}]
              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "[popover-anchor-wrapper ...]"]
                                      [status-text "Stable"]
                                      [p "Description pending."]
                                      [box :src (at)
                                       :width "450px"
                                       :child [args-table popover-anchor-wrapper-args-desc]]
                                      [parts-table "popover-anchor" popover-anchor-wrapper-parts-desc]]]
                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [see-demo-page]]]]]
              [line :src (at) :style {:margin-top "20px"}]
              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "[popover-content-wrapper ...]"]
                                      [p "Description pending."]
                                      [box :src (at)
                                       :width "450px"
                                       :child [args-table popover-content-wrapper-args-desc]]
                                      [parts-table "popover-content-wrapper" popover-content-wrapper-parts-desc]]]
                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [see-demo-page]]]]]
              [line :src (at) :style {:margin-top "20px"}]
              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "[popover-border ...]"]
                                      [p "This component is not normally used as it is rendered by [popover-content-wrapper]."]
                                      [p "Only use this if you want to create a custom [popover-content-wrapper]."]
                                      [box :src (at)
                                       :width "450px"
                                       :child [args-table popover-border-args-desc]]
                                      [parts-table "popover-border" popover-border-parts-desc]]]
                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [p "There is no specific demo for this component."]]]]]
              [line :src (at) :style {:margin-top "20px"}]
              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "[popover-tooltip ...]"]
                                      [p "Description pending."]
                                      [args-table popover-tooltip-args-desc]
                                      [box :src (at)
                                       :width "450px"
                                       :child [parts-table "popover-tooltip" popover-tooltip-parts-desc]]]]
                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [see-demo-page]]]]]
              [line :src (at) :style {:margin-top "20px"}]
              [gap :src (at) :size "30px"]
              [popover-component-hierarchy]]])

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
        long-paragraph?   (reagent/atom false)
        standard-text     "This is the popover body. Can be a simple string or in-line hiccup or a function returning hiccup. Click the button again to cause a pop-down. "
        no-clip-text      "In this mode, the popover will not be clipped within the scroller but it will also not move when scrolling occurs while it's popped up. However, the next time it is popped up, the correct position will be recalculated. "
        extra-text        (clojure.string/join (repeat 4 "And here's a little more text just to pad everything out a bit. "))]
    (fn []
      (let [cancel-popover  #(reset! showing? false)]
        [v-box :src (at)
         :gap      "10px"
         :children [[title2 "[popover ... ] with [button ... ] anchor"]
                    [status-text "Stable"]
                    [h-box :src (at)
                     :gap      "100px"
                     :children [[v-box :src (at)
                                 :gap      "10px"
                                 :width    "450px"
                                 :children [[p "Popovers appear above other components, and point to an anchor."]
                                            [p "In the simplest case, we're talking tooltips. In more complex cases, detailed dialog boxes."]
                                            [p "Even when the absolute position of the anchor changes, the popover stays pointing at it."]
                                            [p "To create a popover, wrap the anchor with " [:code "popover-anchor-wrapper"] ". The arguments are:"]
                                            [:ul
                                             [:li [:code ":showing?"] " - An atom. When true, the popover shows."]
                                             [:li [:code ":position"] " - A keyword specifying the popover's position relative to the anchor. See the demo to the right for the values."]
                                             [:li [:code ":anchor"] " - The anchor component to wrap."]
                                             [:li [:code ":popover"] " - The popover body component (what gets shown in the popover)."]]
                                            [p "You should use the " [:code "popover-content-wrapper"] " component to wrap the body content. The main arguments are:"]
                                            [:ul
                                             [:li [:code ":title"] " - Title of the popover. Can be ommitted."]
                                             [:li [:code ":body"] " - Body component of the popover."]
                                             [:li [:code ":close-button?"] " - Add close button in the top right. Default is true."]
                                             [:li [:code ":on-cancel"] " - A function taking no parameters, invoked when the popover is cancelled (e.g. user clicks away)."]
                                             [:li [:code ":no-clip?"] " - When an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped.
                                                                         By passing true for this parameter, re-com will use a different CSS method to show the popover.
                                                                         This method is slightly inferior because the popover can't track the anchor if it is repositioned."]]]]
                                [v-box :src (at)
                                 :gap      "30px"
                                 :margin   "20px 0px 0px 0px"
                                 :children [[h-box :src (at)
                                             :gap      "30px"
                                             :children [[v-box :src (at)
                                                         :width    "200px"
                                                         :height   "300px"
                                                         :align    :center
                                                         :style    {:border   "1px solid lightgrey"             ;; turn a v-box into a border-scroller - this is a special case
                                                                    :overflow (when @add-scroller? "overlay")}  ;; Use overlay instead of scroll, otherwise things jump around
                                                         :children [[:span
                                                                     {:style (merge (flex-child-style "inherit")
                                                                                    {:color "lightgrey"})}
                                                                     (clojure.string/join (repeat 42 "text "))]
                                                                    [popover-anchor-wrapper :src (at)
                                                                     :showing? showing?
                                                                     :position @curr-position
                                                                     :anchor   [button :src (at)
                                                                                :label (if @showing? "pop-down" "click me")
                                                                                :on-click #(swap! showing? not)
                                                                                :class "btn-success"]
                                                                     :popover  [popover-content-wrapper :src (at)
                                                                                :width            "250px"
                                                                                :no-clip?         @no-clip?
                                                                                :backdrop-opacity (when @backdrop-opacity? 0.3)
                                                                                :on-cancel        (when @on-cancel? cancel-popover)
                                                                                :title            (when @title? (if @no-clip? "no-clip? popover" "Popover happening"))
                                                                                :close-button?    @close-button?
                                                                                :body             (when @body?
                                                                                                    (if @no-clip?
                                                                                                      [:span {:style {:color "brown"}} [:strong "NOTE: "] (str no-clip-text (when @long-paragraph? extra-text))]
                                                                                                      [:span (str standard-text (when @long-paragraph? extra-text))]))]]
                                                                    [:span
                                                                     {:style (merge (flex-child-style "inherit")
                                                                                    {:color "lightgrey"})}
                                                                     (clojure.string/join (repeat (if @add-scroller? 98 49) "text "))]]]
                                                        [v-box :src (at)
                                                         :gap      "15px"
                                                         :align    :start
                                                         :children [[title :src (at) :level :level3 :label "Parameters"]
                                                                    [h-box :src (at)
                                                                     :gap      "20px"
                                                                     :align    :start
                                                                     :children [[checkbox :src (at)
                                                                                 :label     [box :src (at) :align :start :child [:code ":title"]]
                                                                                 :model     title?
                                                                                 :on-change (fn [val] (reset! title? val))]
                                                                                [checkbox :src (at)
                                                                                 :label     [box :src (at) :align :start :child [:code ":close-button?"]]
                                                                                 :model     close-button?
                                                                                 :on-change (fn [val] (reset! close-button? val))]
                                                                                [checkbox :src (at)
                                                                                 :label     [box :src (at) :align :start :child [:code ":body"]]
                                                                                 :model     body?
                                                                                 :on-change (fn [val] (reset! body? val))]]]
                                                                    [h-box :src (at)
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox :src (at)
                                                                                 :label     "add backdrop (catches clicks away from popover)"
                                                                                 :model     on-cancel?
                                                                                 :on-change (fn [val] (reset! on-cancel? val))]
                                                                                (when @on-cancel?
                                                                                  [checkbox :src (at)
                                                                                   :label     [h-box :src (at)
                                                                                               :align    :start
                                                                                               :children [[:code ":backdrop-opacity"]
                                                                                                          (if @backdrop-opacity? "(0.3)" "(0.0)")]]
                                                                                   :model     backdrop-opacity?
                                                                                   :on-change (fn [val] (reset! backdrop-opacity? val))])]]
                                                                    [h-box :src (at)
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox :src (at)
                                                                                 :label     "add scroll bars to box"
                                                                                 :model     add-scroller?
                                                                                 :on-change (fn [val] (reset! add-scroller? val))]
                                                                                [checkbox :src (at)
                                                                                 :label     [h-box :src (at)
                                                                                             :align    :start
                                                                                             :children [[:code ":no-clip?"]
                                                                                                        "*"]]
                                                                                 :model     no-clip?
                                                                                 :on-change (fn [val]
                                                                                              (reset! no-clip? val)
                                                                                              (cancel-popover))]]]
                                                                    [h-box :src (at)
                                                                     :align :start
                                                                     :children [[label :src (at) :label "body content size:"]
                                                                                [radio-button :src (at)
                                                                                 :label     "small"
                                                                                 :value     false
                                                                                 :model     @long-paragraph?
                                                                                 :on-change #(reset! long-paragraph? %)
                                                                                 :style     {:margin-left "10px"}]
                                                                                [radio-button :src (at)
                                                                                 :label     "large"
                                                                                 :value     true
                                                                                 :model     @long-paragraph?
                                                                                 :on-change #(reset! long-paragraph? %)
                                                                                 :style     {:margin-left "10px"}]]]
                                                                    [h-box :src (at)
                                                                     :gap "20px"
                                                                     :align :center
                                                                     :children [[:code ":position"]
                                                                                [single-dropdown :src (at)
                                                                                 :choices    positions
                                                                                 :model      @curr-position
                                                                                 :width      "140px"
                                                                                 :max-height "600px"
                                                                                 :on-change  (fn [val] (reset! curr-position val))]
                                                                                [label :src (at) :label "(applies to all popovers on this page)"]]]
                                                                    [label :src (at) :label "* Changing starred items above closes the popover."]]]]]]]]]
                    [line :src (at) :style {:margin-top "20px"}]]]))))


(defn hyperlink-popover-demo
  []
  (let [showing?  (reagent/atom false)]
    (fn []
      [v-box :src (at)
       :children [[title2 "[popover ... ] with [hyperlink ... ] anchor"]
                  [h-box :src (at)
                   :gap      "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :margin   "20px 0px 0px 0px"
                               :children [
                                           [p "The " [:code "hyperlink"] " component is useful for creating link popovers. Use it as the anchor."]]]
                              [v-box :src (at)
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-anchor-wrapper :src (at)
                                           :showing? showing?
                                           :position @curr-position
                                           :anchor   [hyperlink :src (at)
                                                      :label     "click me for popover"
                                                      :on-click  #(swap! showing? not)]
                                           :popover  [popover-content-wrapper :src (at)
                                                      :width    "250px"
                                                      :title    "Popover Title"
                                                      :body     "popover body"]]]]]]
                  [line :src (at) :style {:margin-top "20px"}]]])))


(defn proximity-popover-demo
  []
  (let [showing? (reagent/atom false)]
    (fn []
      [v-box :src (at)
       :children [[title2 "Proximity Popover (tooltip)"]
                  [h-box :src (at)
                   :gap      "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :margin   "20px 0px 0px 0px"
                               :children [ [p "Popovers can be used to create hover tooltips on any component. This example uses a " [:code "[:div]"] "."]
                                           [p "Simply set the " [:code "on-mouse-over"] " and " [:code "on-mouse-out"] " events to show/hide the popover."]]]
                              [v-box :src (at)
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-anchor-wrapper :src (at)
                                           :showing? showing?
                                           :position @curr-position
                                           :anchor   [:div
                                                      {:style         {:background-color "lightblue"
                                                                       :border           "2px solid blue"
                                                                       :padding          "8px"
                                                                       :cursor           "default"}
                                                       :on-mouse-over (handler-fn (reset! showing? true))
                                                       :on-mouse-out  (handler-fn (reset! showing? false))}
                                                      "hover here for tooltip"]
                                           :popover [popover-content-wrapper :src (at)
                                                     :body     "popover body (without a title specified) makes a basic tooltip component"]]]]]]
                  [line :src (at) :style {:margin-top "20px"}]]])))


(defn popover-tooltip-demo
  []
  (let [showing?      (reagent/atom false)
        status        (reagent/atom nil)
        text          (reagent/atom "This is a tooltip")
        width?        (reagent/atom false)
        close-button? (reagent/atom false)
        tt-width      "200px"]
    (fn []
      [v-box :src (at)
       :children [[title2 "[popover-tooltip ... ]"]
                  [h-box :src (at)
                   :gap      "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :margin   "20px 0px 0px 0px"
                               :children [
                                           [p "This is a seaprate component which makes it really easy to create tooltips."]
                                           [p "It also can be colored for warning or error status."]]]
                              [v-box :src (at)
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-tooltip :src (at)
                                           :label         @text
                                           :position      @curr-position
                                           :showing?      showing?
                                           :status        @status
                                           :width         (when @width? tt-width)
                                           :close-button? @close-button?
                                           :anchor        [button :src (at)
                                                           :label    "click me"
                                                           :on-click #(swap! showing? not)
                                                           :class    "btn-success"]]]]
                              [v-box :src (at)
                               :children [[gap :src (at) :size "15px"]
                                          [title :src (at) :level :level3 :label "Parameters"]
                                          [gap :src (at) :size "15px"]
                                          [h-box :src (at)
                                           :gap      "8px"
                                           :align    :center
                                           :children [[box :src (at) :align :start :child [:code ":model"]]
                                                      [input-text :src (at)
                                                       :model           text
                                                       :change-on-blur? false
                                                       :on-change       #(reset! text %)]]]
                                          [gap :src (at) :size "15px"]
                                          [box :src (at) :align :start :child [:code ":status"]]
                                          [radio-button :src (at)
                                           :label     "nil/omitted - normal input state"
                                           :value     nil
                                           :model     @status
                                           :on-change #(reset! status %)
                                           :style {:margin-left "20px"}]
                                          [radio-button :src (at)
                                           :label     ":success - success status"
                                           :value     :success
                                           :model     @status
                                           :on-change #(reset! status %)
                                           :style     {:margin-left "20px"}]
                                          [radio-button :src (at)
                                           :label     ":warning - Warning status"
                                           :value     :warning
                                           :model     @status
                                           :on-change #(reset! status %)
                                           :style     {:margin-left "20px"}]
                                          [radio-button :src (at)
                                           :label     ":error - Error status"
                                           :value     :error
                                           :model     @status
                                           :on-change #(reset! status %)
                                           :style     {:margin-left "20px"}]
                                          [radio-button :src (at)
                                           :label     ":info - Use for more detailed info tooltips, e.g. for info-button"
                                           :value     :info
                                           :model     @status
                                           :on-change #(reset! status %)
                                           :style     {:margin-left "20px"}]
                                          [gap :src (at) :size "15px"]
                                          [h-box :src (at)
                                           :align    :center
                                           :gap      "15px"
                                           :children [[checkbox :src (at)
                                                       :label [box :src (at) :align :start :child [:code ":width"]]
                                                       :model width?
                                                       :on-change #(reset! width? %)]
                                                      [:span (str (if @width?
                                                                    (str "\"" tt-width "\" - the tooltip is fixed to this width.")
                                                                    "not specified - the tooltip is as wide as it's contents."))]]]
                                          [gap :src (at) :size "15px"]
                                          [h-box :src (at)
                                           :align    :center
                                           :gap      "15px"
                                           :children [[checkbox :src (at)
                                                       :label [box :src (at) :align :start :child [:code ":close-button?"]]
                                                       :model close-button?
                                                       :on-change #(reset! close-button? %)]
                                                      "Mostly used when :status is set to :info"]]]]]]
                  [line :src (at) :style {:margin-top "20px"}]]])))


(defn complex-popover-demo
  []
  [v-box :src (at)
   :children [[title2 "Complex Popover (dialog box)"]
              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :margin   "20px 0px 0px 0px"
                           :children [[p "Popovers can be arbitrarily complex."]
                                      [p [:code "popover-content-wrapper"] " is friendly to dialog coding patterns."]]]
                          [v-box :src (at)
                           :gap      "30px"
                           :margin   "20px 0px 0px 0px"
                           :children [[popover-dialog-demo/popover-dialog-demo curr-position]]]]]]])


(defn panel2
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "Popover Components"
                           "src/re_com/popover.cljs"
                           "src/re_demo/popovers.cljs"]
              [simple-popover-demo]
              [hyperlink-popover-demo]
              [proximity-popover-demo]
              [popover-tooltip-demo]
              [complex-popover-demo]
              [gap :src (at) :size "280px"]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
