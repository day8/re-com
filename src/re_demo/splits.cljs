(ns re-demo.splits
  (:require [re-com.core   :refer [h-box v-box box gap line scroller border h-split v-split title flex-child-style p]]
            [re-com.splits :refer [hv-split-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]))


(def rounded-panel (merge (flex-child-style "1")
                          {:background-color "#fff4f4"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :padding          "0px 20px 0px 20px"}))

(defn splitter-panel-title
  [text]
  [title
   :label text
   :level :level3
   :style {:margin-top "20px"}])

(defn left-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-1"]]]])


(defn right-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-2"]]]])


(defn top-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-1"]]]])


(defn bottom-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-2"]]]])

(defn splits-component-hierarchy
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
                [:pre "[h-split\n"
                      "   ...\n"
                      "   :parts {:splitter {:class \"blah\"\n"
                      "                      :style { ... }\n"
                      "                      :attr  { ... }}}]"]
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
                   [:td border-style-nw (indent-text 0 "[h-split] || [v-split]")]
                   [:td border-style-nw "rc-h-split || rc-v-split"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "Outer wrapper of the split."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-h-split-left || rc-v-split-top"]
                   [:td border-style-nw (code-text ":left") " || " (code-text ":top")]
                   [:td border-style "First panel of the split; i.e. left or top."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-h-split-splitter || rc-v-split-splitter"]
                   [:td border-style-nw (code-text ":splitter")]
                   [:td border-style "The splitter between the panels."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:div]")]
                   [:td border-style-nw "rc-h-split-handle || rc-v-split-handle"]
                   [:td border-style-nw (code-text ":handle")]
                   [:td border-style "The splitter handle."]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[:div]")]
                   [:td border-style-nw "rc-h-split-handle-bar-1 || rc-v-split-handle-bar-2"]
                   [:td border-style-nw (code-text ":handle-bar-1")]
                   [:td border-style "The splitter handle first bar."]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[:div]")]
                   [:td border-style-nw "rc-h-split-handle-bar-2 || rc-v-split-handle-bar-2"]
                   [:td border-style-nw (code-text ":handle-bar-2")]
                   [:td border-style "The splitter handle second bar."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-h-split-right || rc-v-split-bottom"]
                   [:td border-style-nw (code-text ":right") " || " (code-text ":bottom")]
                   [:td border-style "Second panel of the split; i.e. right or bottom."]]]]]]))

(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "Splitter Components"
                            "src/re_com/splits.cljs"
                            "src/re_demo/splits.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[h-split ... ] & [v-split ... ]"]
                                      [status-text "Stable"]
                                      [p "Arranges two components horizontally (or vertically) and provides a splitter bar between them."]
                                      [p "By dragging the splitter bar, a user can change the width (or height) allocated to each."]
                                      [p "Can contain further nested layout components."]
                                      [args-table hv-split-args-desc]]]
                          [v-box
                           :size     "auto"
                           :gap      "10px"
                           :height   "800px"
                           :children [[title2 "Demo"]
                                      [title :level :level3 :label "[h-split]"]
                                      [h-split
                                       :panel-1 [left-panel]
                                       :panel-2 [right-panel]
                                       :size    "300px"]
                                      [title :level :level3 :label "[v-split]"]
                                      [v-split
                                       :panel-1       [top-panel]
                                       :panel-2       [bottom-panel]
                                       :size          "300px"
                                       :initial-split "25%"]]]]]
              [splits-component-hierarchy]]])
