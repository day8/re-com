(ns re-demo.checkbox
  (:require [re-com.core     :refer [h-box v-box box gap line checkbox label p]]
            [re-com.checkbox :refer [checkbox-args-desc]]
            [re-demo.utils   :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util     :refer [px]]
            [reagent.core    :as    reagent]))


(defn right-arrow
  []
  [:svg
   {:height 20  :width 25}
   [:line {:x1 "0" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "20,6 20,14 25,10" :style {:stroke "#888" :fill "#888"}}]])


(defn left-arrow
  []
  [:svg
   {:height 20  :width 25}
   [:line {:x1 "5" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "5,6 5,14 0,10" :style {:stroke "#888" :fill "#888"}}]])

(defn checkbox-component-hierarchy
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
                [:pre "[checkbox\n"
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
                   [:td border-style-nw (indent-text 0 "[checkbox]")]
                   [:td border-style-nw "rc-checkbox-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the checkbox, label, everything."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:input]")]
                   [:td border-style-nw "rc-checkbox"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The actual checkbox."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:span]")]
                   [:td border-style-nw "rc-checkbox-label"]
                   [:td border-style-nw "Use " (code-text ":label-class") " or " (code-text ":label-style") " arguments instead."]
                   [:td border-style "The label container."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 ":label")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style "The label."]]]]]]))

(defn checkboxes-demo
  []
  (let [; always-false (reagent/atom false)
        disabled?    (reagent/atom false)
        ticked?      (reagent/atom false)
        something1?  (reagent/atom false)
        something2?  (reagent/atom true)
        all-for-one? (reagent/atom true)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[checkbox ... ]"
                                "src/re_com/checkbox.cljs"
                                "src/re_demo/checkbox.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A boostrap-styled checkbox, with optional label (always displayed to the right)."]
                                          [p "Clicking on the label is the same as clicking on the checkbox."]
                                          [args-table checkbox-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [v-box
                                           :gap "15px"
                                           :children [[h-box
                                                       :gap      "10px"
                                                       :height   "20px"
                                                       :children [[checkbox
                                                                   :label     "tick me  "
                                                                   :model     ticked?
                                                                   :on-change #(reset! ticked? %)]
                                                                  (when @ticked? [left-arrow])
                                                                  (when @ticked? [label :label " is ticked"])]]

                                                      [h-box
                                                       :gap      "1px"
                                                       :children [[checkbox  :model all-for-one? :on-change #(reset! all-for-one? %)]
                                                                  [checkbox  :model all-for-one? :on-change #(reset! all-for-one? %)]
                                                                  [checkbox  :model all-for-one? :on-change #(reset! all-for-one? %)  :label  "all for one, and one for all.  "]]]

                                                      [h-box
                                                       :gap      "15px"
                                                       :children [[checkbox
                                                                   :label     "tick this one, to \"disable\""
                                                                   :model     disabled?
                                                                   :on-change #(reset! disabled? %)]
                                                                  [right-arrow]
                                                                  [checkbox
                                                                   :label       (if @disabled? "now disabled" "enabled")
                                                                   :model       something1?
                                                                   :disabled?   disabled?
                                                                   :label-style (if @disabled?  {:color "#888"})
                                                                   :on-change   #(reset! something1? %)]]]

                                                      [h-box
                                                       :gap      "1px"
                                                       :children [[checkbox
                                                                   :model     something2?
                                                                   :on-change #(reset! something2? %)]
                                                                  [gap :size "50px"]
                                                                  [left-arrow]
                                                                  [gap :size "5px"]
                                                                  [label
                                                                   :label "no label on this one"]]]]]]]]]
                  [checkbox-component-hierarchy]]])))


;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [checkboxes-demo])
