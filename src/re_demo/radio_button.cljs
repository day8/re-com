(ns re-demo.radio-button
  (:require [re-com.core   :refer [h-box v-box box gap checkbox title line radio-button p]]
            [re-com.misc   :refer [radio-button-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]
            [reagent.core  :as    reagent]))

(defn radio-button-component-hierarchy
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
                [:pre "[radio-button\n"
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
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[radio-button]")]
                   [:td border-style-nw "rc-radio-button-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the radio button."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:input]")]
                   [:td border-style-nw "rc-radio-button"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The actual input field."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:span]")]
                   [:td border-style-nw "rc-radio-button-label"]
                   [:td border-style-nw "Use " (code-text ":label-class") " or " (code-text ":label-style") " arguments instead."]
                   [:td border-style "The label of the radio button."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 ":label")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]]]]]))

(defn radios-demo
  []
  (let [disabled?   (reagent/atom false)
        color (reagent/atom "green")]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[radio-button ... ]"
                                "src/re_com/misc.cljs"
                                "src/re_demo/radio_button.cljs"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A boostrap-styled radio button, with optional label (always displayed to the right)."]
                                          [p "Clicking on the label is the same as clicking on the radio button."]
                                          [args-table radio-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :gap "30px"
                                           :children [[v-box
                                                       :children [(doall (for [c ["red" "green" "blue"]]    ;; Notice the ugly "doall"
                                                                           ^{:key c}                        ;; key should be unique among siblings
                                                                           [radio-button
                                                                            :disabled? disabled?
                                                                            :label       c
                                                                            :value       c
                                                                            :model       color
                                                                            :label-style (if (= c @color) {:color       c
                                                                                                            :font-weight "bold"})
                                                                            :on-change   #(reset! color %)]))]]
                                                      [v-box
                                                       :gap "15px"
                                                       :children [[title :level :level3 :label "Parameters"]
                                                                  [checkbox
                                                                   :label [:code ":disabled?"]
                                                                   :model disabled?
                                                                   :on-change (fn [val]
                                                                                (reset! disabled? val))]]]]]]]]]
                  [radio-button-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [radios-demo])
