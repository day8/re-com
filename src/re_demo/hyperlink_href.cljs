(ns re-demo.hyperlink-href
  (:require [re-com.core    :refer [h-box v-box box gap line label title radio-button hyperlink-href p checkbox]]
            [re-com.buttons :refer [hyperlink-href-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util    :refer [px]]
            [reagent.core   :as    reagent]))

(defn hyperlink-href-component-hierarchy
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
                [:pre "[hyperlink-href\n"
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
                   [:td border-style-nw (indent-text 0 "[hyperlink-href]")]
                   [:td border-style-nw "rc-hyperlink-href-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the hyperlink, tooltip (if any), everything."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[popover-tooltip]")]
                   [:td border-style-nw "rc-hyperlink-href-tooltip"]
                   [:td border-style-nw (code-text ":tooltip")]
                   [:td border-style "Tooltip, if enabled."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:a]")]
                   [:td border-style-nw "rc-hyperlink-href"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The anchor."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 ":label")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style "The label."]]]]]]))

(defn hyperlink-href-demo
  []
  (let [disabled? (reagent/atom false)
        target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[hyperlink-href ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/hyperlink_href.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A blue, clickable hyperlink which launches external URLs."]
                                          [p "If you want a hyperlink with a click handler, use the [hyperlink] component."]
                                          [args-table hyperlink-href-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :gap "40px"
                                           :children [[box
                                                       :width "200px"
                                                       :align :start
                                                       :child [hyperlink-href
                                                               :label     "Launch Google"
                                                               :tooltip   "You're about to launch Google"
                                                               :href      (when href? "http://google.com")
                                                               :target    (when href? target)
                                                               :disabled?        disabled?]]
                                                      [v-box
                                                       :gap "15px"
                                                       :children [[title :level :level3 :label "Parameters"]
                                                                  (when @href?
                                                                    [v-box
                                                                     :children [[box :align :start :child [:code ":target"]]
                                                                                [radio-button
                                                                                 :label "_self - load link into same tab"
                                                                                 :value "_self"
                                                                                 :model @target
                                                                                 :on-change #(reset! target %)
                                                                                 :style {:margin-left "20px"}]
                                                                                [radio-button
                                                                                 :label "_blank - load link into new tab"
                                                                                 :value "_blank"
                                                                                 :model @target
                                                                                 :on-change #(reset! target %)
                                                                                 :style {:margin-left "20px"}]
                                                                                [checkbox
                                                                                 :label [:code ":disabled?"]
                                                                                 :model disabled?
                                                                                 :on-change (fn [val]
                                                                                              (reset! disabled? val))]]])]]]]]]]]
                  [hyperlink-href-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [hyperlink-href-demo])
