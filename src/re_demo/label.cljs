(ns re-demo.label
  (:require [re-com.core   :refer [h-box v-box box gap line label p]]
            [re-com.text   :refer [label-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]))

(defn label-component-hierarchy
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
                [:pre "[label\n"
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
                   [:td border-style-nw (indent-text 0 "[label]")]
                   [:td border-style-nw "rc-label-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the label."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:span]")]
                   [:td border-style-nw "rc-label"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 ":label")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]]]]]))

(defn label-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[label ... ]"
                            "src/re_com/text.cljs"
                            "src/re_demo/label.cljs"]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [:p "A short single line of text."]
                                      [args-table label-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [v-box
                                       :children [[label :label "This is a label."]]]]]]]
              [label-component-hierarchy]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [label-demo])
