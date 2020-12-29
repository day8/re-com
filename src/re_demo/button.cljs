(ns re-demo.button
  (:require [re-com.core    :refer [h-box v-box box gap line button label throbber hyperlink-href p p-span] :refer-macros [handler-fn]]
            [re-com.buttons :refer [button-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util    :refer [px]]
            [reagent.core   :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-throbber  false}))

(def click-outcomes
  [""   ;; start blank
   "Nuclear warhead launched."
   "Oops. Priceless Ming Vase smashed!!"
   "Diamonds accidentally flushed."
   "Toy disabled"])

(defn button-component-hierarchy
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
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[button\n"
                      "   ...\n"
                      "   :parts {:tooltip {:class \"blah\"\n"
                      "                     :style { ... }\n"
                      "                     :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[button]")]
                   [:td border-style-nw "rc-button-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the button, tooltip (if any), everything."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[popover-tooltip]")]
                   [:td border-style-nw "rc-button-tooltip"]
                   [:td border-style-nw (code-text ":tooltip")]
                   [:td border-style "Tooltip, if enabled."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:button]")]
                   [:td border-style-nw "rc-button"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The actual button."]]]]]]))

(defn button-demo
  []
  (let [hover? (reagent/atom false)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[button ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/button.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]

                                          [p "A button component with optional tooltip."]
                                          [p "Styling to be provided via the " [:code ":class"] " attribute. Typically you'll be using Bootstrap CSS styles such as \"btn-info\"."]
                                          [p-span "See "
                                            [hyperlink-href
                                             :label "Bootstrap Button Options"
                                             :href "http://getbootstrap.com/css/#buttons-options"
                                             :target "_blank"]
                                            " for information on available classes."]
                                          [args-table button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :children [[button
                                                       :label            "No Clicking!"
                                                       :tooltip          (when-not (= (:outcome-index @state) (dec (count click-outcomes))) "Seriously, NO CLICKING!")
                                                       :tooltip-position :below-center
                                                       :disabled?         (= (:outcome-index @state) (dec (count click-outcomes)))
                                                       :on-click          #(swap! state update-in [:outcome-index] inc)
                                                       :class             "btn-danger"]
                                                      [box
                                                       :align :center      ;; note: centered text wrt the button
                                                       :child  [label
                                                                :label (nth click-outcomes (:outcome-index @state))
                                                                :style {:margin-left "15px"}]]]]
                                          [gap :size "20px"]
                                          [h-box
                                           :height   "50px"
                                           :gap      "50px"
                                           :align    :center
                                           :children [[button
                                                       :label             (if (:see-throbber @state)  "Stop it!" "See Throbber")
                                                       :tooltip           "I'm a tooltip on the left"
                                                       :tooltip-position :left-center
                                                       :on-click          #(swap! state update-in [:see-throbber] not)]
                                                      (when (:see-throbber @state) [throbber])]]
                                          [gap :size "20px"]

                                          [p "The two buttons above are styled using Bootstrap. For the " [:code ":class"] " parameter, we passed in the name of a standard Bootstrap class, like \"btn-default\"."]
                                          [p "But the button below was created by supplying inline styles via the " [:code ":style"] " and " [:code ":attr"] " parameters. To see the code, click the \"Page Source\" hyperlink at the top."]
                                          [button
                                            :label    [:span "Microsoft Modern Button " [:i.zmdi.zmdi-hc-fw-rc.zmdi-download]]
                                            :on-click #()
                                            :style    {:color            "white"
                                                       :background-color (if @hover? "#0072bb" "#4d90fe")
                                                       :font-size        "22px"
                                                       :font-weight      "300"
                                                       :border           "none"
                                                       :border-radius    "0px"
                                                       :padding          "20px 26px"}
                                            :attr     {:on-mouse-over (handler-fn (reset! hover? true))
                                                       :on-mouse-out  (handler-fn (reset! hover? false))}]]]]]
                  [button-component-hierarchy]]])))



;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [button-demo])
