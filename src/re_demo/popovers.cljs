(ns re-demo.popovers
  (:require [re-com.core                 :refer [h-box v-box box gap line scroller border label title input-text checkbox radio-button button hyperlink
                                                 p single-dropdown popover-content-wrapper popover-anchor-wrapper popover-border popover-tooltip flex-child-style]
                                         :refer-macros [handler-fn]]
            [re-com.popover              :refer [popover-content-wrapper-args-desc popover-anchor-wrapper-args-desc popover-border-args-desc
                                                 popover-tooltip-args-desc]]
            [re-demo.popover-dialog-demo :as    popover-dialog-demo]
            [re-demo.utils               :refer [panel-title title2 args-table github-hyperlink status-text]]
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


(defn arg-lists
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "Popover Components"
                            "src/re_com/popover.cljs"
                            "src/re_demo/popovers.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[popover-anchor-wrapper ...]"]
                                      [status-text "Alpha" {:color "#EA6B00"}]
                                      [p "TBA..."]
                                      [args-table popover-anchor-wrapper-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [:span.all-small-caps "TBA..."]]]]]
              [line :style {:margin-top "20px"}]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[popover-content-wrapper ...]"]
                                      [p "TBA..."]
                                      [args-table popover-content-wrapper-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [:span.all-small-caps "TBA..."]]]]]
              [line :style {:margin-top "20px"}]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[popover-border ...]"]
                                      [p "TBA..."]
                                      [args-table popover-border-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [:span.all-small-caps "TBA..."]]]]]
              [line :style {:margin-top "20px"}]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[popover-tooltip ...]"]
                                      [p "TBA..."]
                                      [args-table popover-tooltip-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [:span.all-small-caps "TBA..."]]]]]
              [gap :size "30px"]]])

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
        extra-text        (clojure.string/join (repeat 4 "And here's a little more text just to pad everything out a bit. "))
        ]
    (fn []
      (let [cancel-popover  #(reset! showing? false)]
        [v-box
         :gap      "10px"
         :children [[title2 "[popover ... ] with [button ... ] anchor"]
                    [status-text "Alpha" {:color "#EA6B00"}]
                    [h-box
                     :gap      "100px"
                     :children [[v-box
                                 :gap      "10px"
                                 :width    "450px"
                                 :children [
                                            [p "Popovers appear above other components, and point to an anchor."]
                                            [p "In the simplest case, we're talking tooltips. In more complex cases, detailed dialog boxes."]
                                            [p "Even when the absolute position of the anchor changes, the popover stays pointing at it."]
                                            [p "To create a popover, wrap the anchor with " [:code "popover-anchor-wrapper"] ". The arguments are:"
                                             [:ul
                                              [:li [:code ":showing?"] " - An atom. When true, the popover shows."]
                                              [:li [:code ":position"] " - A keyword specifying the popover's position relative to the anchor. See the demo to the right for the values."]
                                              [:li [:code ":anchor"] " - The anchor component to wrap."]
                                              [:li [:code ":popover"] " - The popover body component (what gets shown in the popover)."]]]
                                            [p "You should use the " [:code "popover-content-wrapper"] " component to wrap the body content. The main arguments are:"
                                             [:ul
                                              [:li [:code ":title"] " - Title of the popover. Can be ommitted."]
                                              [:li [:code ":close-button?"] " - Add close button in the top right. Default is true."]
                                              [:li [:code ":body"] " - Body component of the popover."]
                                              [:li [:code ":on-cancel"] " - A function taking no parameters, invoked when the popover is cancelled (e.g. user clicks away)."]
                                              [:li [:code ":no-clip?"] " - When an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped.
                                                                         By passing true for this parameter, re-com will use a different CSS method to show the popover.
                                                                         This method is slightly inferior because the popover can't track the anchor if it is repositioned."]]]]]
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
                                                         :children [[:span
                                                                     {:style (merge (flex-child-style "inherit")
                                                                                    {:color "lightgrey"})}
                                                                     (clojure.string/join (repeat 42 "text "))]
                                                                    [popover-anchor-wrapper
                                                                     :showing? showing?
                                                                     :position @curr-position
                                                                     :anchor   [button
                                                                                :label (if @showing? "pop-down" "click me")
                                                                                :on-click #(swap! showing? not)
                                                                                :class "btn-success"]
                                                                     :popover  [popover-content-wrapper
                                                                                :showing?         showing?
                                                                                :position         @curr-position
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
                                                        [v-box
                                                         :gap      "15px"
                                                         :align    :start
                                                         :children [[title :level :level3 :label "Parameters"]
                                                                    [h-box
                                                                     :gap      "20px"
                                                                     :align    :start
                                                                     :children [[checkbox
                                                                                 :label     [box :align :start :child [:code ":title"]]
                                                                                 :model     title?
                                                                                 :on-change (fn [val] (reset! title? val))]
                                                                                [checkbox
                                                                                 :label     [box :align :start :child [:code ":close-button?"]]
                                                                                 :model     close-button?
                                                                                 :on-change (fn [val] (reset! close-button? val))]
                                                                                [checkbox
                                                                                 :label     [box :align :start :child [:code ":body"]]
                                                                                 :model     body?
                                                                                 :on-change (fn [val] (reset! body? val))]]]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox
                                                                                 :label     "add backdrop (catches clicks away from popover)"
                                                                                 :model     on-cancel?
                                                                                 :on-change (fn [val] (reset! on-cancel? val))]
                                                                                (when @on-cancel?
                                                                                  [checkbox
                                                                                   :label     [h-box
                                                                                               :align    :start
                                                                                               :children [[:code ":backdrop-opacity"]
                                                                                                          (if @backdrop-opacity? "(0.3)" "(0.0)")]]
                                                                                   :model     backdrop-opacity?
                                                                                   :on-change (fn [val] (reset! backdrop-opacity? val))])]]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :start
                                                                     :children [[checkbox
                                                                                 :label     "add scroll bars to box"
                                                                                 :model     add-scroller?
                                                                                 :on-change (fn [val] (reset! add-scroller? val))]
                                                                                [checkbox
                                                                                 :label     [h-box
                                                                                             :align    :start
                                                                                             :children [[:code ":no-clip?"]
                                                                                                        "*"]]
                                                                                 :model     no-clip?
                                                                                 :on-change (fn [val]
                                                                                              (reset! no-clip? val)
                                                                                              (cancel-popover))]]]
                                                                    [h-box
                                                                     :align :start
                                                                     :children [[label :label "body content size:"]
                                                                                [radio-button
                                                                                 :label     "small"
                                                                                 :value     false
                                                                                 :model     @long-paragraph?
                                                                                 :on-change #(reset! long-paragraph? false)
                                                                                 :style     {:margin-left "10px"}]
                                                                                [radio-button
                                                                                 :label     "large"
                                                                                 :value     true
                                                                                 :model     @long-paragraph?
                                                                                 :on-change #(reset! long-paragraph? true)
                                                                                 :style     {:margin-left "10px"}]]]
                                                                    [h-box
                                                                     :gap "20px"
                                                                     :align :center
                                                                     :children [[h-box
                                                                                 :align    :start
                                                                                 :children [[:code ":position"]
                                                                                            "*"]]
                                                                                [single-dropdown
                                                                                 :choices    positions
                                                                                 :model      curr-position
                                                                                 :width      "140px"
                                                                                 :max-height "600px"
                                                                                 :on-change  (fn [val]
                                                                                               (reset! curr-position val)
                                                                                               (cancel-popover))]
                                                                                [label :label "(applies to all popovers on this page)"]]]
                                                                    [label :label "* Changing starred items above closes the popover."]]]]]]]]]]]))))


(defn hyperlink-popover-demo
  []
  (let [showing?  (reagent/atom false)]
    (fn []
      [v-box
       :children [[title2 "[popover ... ] with [hyperlink ... ] anchor"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :margin   "20px 0px 0px 0px"
                               :children [
                                           [p "The " [:code "hyperlink"] " component is useful for creating link popovers. Use it as the anchor."]]]
                              [v-box
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-anchor-wrapper
                                           :showing? showing?
                                           :position @curr-position ;; TODO: pos
                                           :anchor   [hyperlink
                                                      :label     "click me for popover"
                                                      :on-click  #(swap! showing? not)]
                                           :popover  [popover-content-wrapper
                                                      :showing? showing?
                                                      :position @curr-position ;; TODO: pos
                                                      :width    "250px"
                                                      :title    "Popover Title"
                                                      :body     "popover body"]]]]]]]])))


(defn proximity-popover-demo
  []
  (let [showing? (reagent/atom false)
        pos      :above-center]
    (fn []
      [v-box
       :children [[title2 "Proximity Popover (tooltip)"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :margin   "20px 0px 0px 0px"
                               :children [ [p "Popovers can be used to create hover tooltips on any component. This example uses a " [:code "[:div]"] "."]
                                           [p "Simply set the " [:code "on-mouse-over"] " and " [:code "on-mouse-out"] " events to show/hide the popover."]]]
                              [v-box
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-anchor-wrapper
                                           :showing? showing?
                                           :position @curr-position ;; TODO: pos
                                           :anchor   [:div
                                                      {:style         {:background-color "lightblue"
                                                                       :border           "2px solid blue"
                                                                       :padding          "8px"
                                                                       :cursor           "default"}
                                                       :on-mouse-over (handler-fn (reset! showing? true))
                                                       :on-mouse-out  (handler-fn (reset! showing? false))}
                                                      "hover here for tooltip"]
                                           :popover [popover-content-wrapper
                                                     :showing? showing?
                                                     :position @curr-position ;; TODO: pos
                                                     :body     "popover body (without a title specified) makes a basic tooltip component"]]]]]]]])))


(defn popover-tooltip-demo
  []
  (let [showing? (reagent/atom false)
        status   (reagent/atom nil)
        text     (reagent/atom "This is a tooltip")
        width?   (reagent/atom false)
        tt-width "200px"]
    (fn
      []
      [v-box
       :children [[title2 "[popover-tooltip ... ]"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :margin   "20px 0px 0px 0px"
                               :children [
                                           [p "This is a seaprate component which makes it really easy to create tooltips."]
                                           [p "It also can be colored for warning or error status."]]]
                              [v-box
                               :gap      "30px"
                               :margin   "20px 0px 0px 0px"
                               :children [[popover-tooltip
                                           :label    @text
                                           :position @curr-position
                                           :showing? showing?
                                           :status   @status
                                           :width    (when @width? tt-width)
                                           :anchor   [button
                                                      :label    "click me"
                                                      :on-click #(swap! showing? not)
                                                      :class    "btn-success"]]]]
                              [v-box
                               :children [[gap :size "15px"]
                                          [title :level :level3 :label "Parameters"]
                                          [gap :size "15px"]
                                          [h-box
                                           :gap      "8px"
                                           :align    :center
                                           :children [[box :align :start :child [:code ":model"]]
                                                      [input-text
                                                       :model           text
                                                       :change-on-blur? false
                                                       :on-change       #(reset! text %)]]]
                                          [gap :size "15px"]
                                          [box :align :start :child [:code ":status"]]
                                          [radio-button
                                           :label     "nil/omitted - normal input state"
                                           :value     nil
                                           :model     @status
                                           :on-change #(do
                                                        (reset! status nil)
                                                        (reset! showing? false))
                                           :style {:margin-left "20px"}]
                                          [radio-button
                                           :label     ":warning - Warning status"
                                           :value     :warning
                                           :model     @status
                                           :on-change #(do
                                                        (reset! status :warning)
                                                        (reset! showing? false))
                                           :style     {:margin-left "20px"}]
                                          [radio-button
                                           :label     ":error - Error status"
                                           :value     :error
                                           :model     @status
                                           :on-change #(do
                                                        (reset! status :error)
                                                        (reset! showing? false))
                                           :style     {:margin-left "20px"}]
                                          [radio-button
                                           :label     ":info - Use for more detailed info tooltips, e.g. for info-button"
                                           :value     :info
                                           :model     @status
                                           :on-change #(do
                                                        (reset! status :info)
                                                        (reset! showing? false))
                                           :style     {:margin-left "20px"}]
                                          [gap :size "15px"]
                                          [h-box
                                           :align    :center
                                           :gap      "15px"
                                           :children [[checkbox
                                                       :label [box :align :start :child [:code ":width"]]
                                                       :model width?
                                                       :on-change #(reset! width? %)]
                                                      [:span (str (if @width?
                                                                    (str "\"" tt-width "\" - the tooltip is fixed to this width.")
                                                                    "not specified - the tooltip is as wide as it's contents."))]]]]]]]]])))


(defn complex-popover-demo
  []
  [v-box
   :children [[title2 "Complex Popover (dialog box)"]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :margin   "20px 0px 0px 0px"
                           :children [ [p "Popovers can be arbitrarilary complex."]
                                       [p [:code "popover-content-wrapper"] " is friendly to dialog coding patterns."]]]
                          [v-box
                           :gap      "30px"
                           :margin   "20px 0px 0px 0px"
                           :children [[popover-dialog-demo/popover-dialog-demo curr-position]]]]]]])


(defn panel2
  []
  [v-box
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
              [gap :size "280px"]]]) ;;TODO: 180px


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
