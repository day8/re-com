(ns re-demo.v-table
  (:require [re-com.core    :refer [h-box gap v-box v-table hyperlink-href p]]
            [re-com.v-table :refer [table-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]))

(defn v-table-component-hierarchy
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
                [:pre "[v-table\n"
                      "   ...\n"
                      "   :parts {:v-scroll {:class \"blah\"\n"
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
                   [:td border-style-nw (indent-text 0 "[v-table]")]
                   [:td border-style-nw "rc-v-table"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[v-box]")]
                   [:td border-style-nw "rc-v-table-left-section"]
                   [:td border-style-nw (code-text ":left-section")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw "rc-v-table-top-left rc-v-table-content"]
                   [:td border-style-nw (code-text ":top-left")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 (code-text ":top-left-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[v-box]")]
                   [:td border-style-nw "rc-v-table-row-headers rc-v-table-viewport"]
                   [:td border-style-nw (code-text ":row-headers")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[:div]")]
                   [:td border-style-nw "rc-v-table-selection"]
                   [:td border-style-nw (code-text ":row-header-selection-rect")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[v-box]")]
                   [:td border-style-nw "rc-v-table-row-header-content rc-v-table-content"]
                   [:td border-style-nw (code-text ":row-header-content")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 (code-text ":row-header-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw "rc-v-table-bottom-left rc-v-table-content"]
                   [:td border-style-nw (code-text ":bottom-left")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 (code-text ":bottom-left-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[v-box]")]
                   [:td border-style-nw "rc-v-table-middle-section"]
                   [:td border-style-nw (code-text ":middle-section")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[v-box]")]
                   [:td border-style-nw "rc-v-table-col-headers rc-v-table-viewport"]
                   [:td border-style-nw (code-text ":col-headers")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[:div]")]
                   [:td border-style-nw "rc-v-table-selection"]
                   [:td border-style-nw (code-text ":col-header-selection-rect")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[box]")]
                   [:td border-style-nw "rc-v-table-col-header-content rc-v-table-content"]
                   [:td border-style-nw (code-text ":col-header-content")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 (code-text ":col-header-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[v-box]")]
                   [:td border-style-nw "rc-v-table-rows rc-v-table-viewport"]
                   [:td border-style-nw (code-text ":rows")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[:div]")]
                   [:td border-style-nw "rc-v-table-selection"]
                   [:td border-style-nw (code-text ":row-selection-rect")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[v-box]")]
                   [:td border-style-nw "rc-v-table-row-content rc-v-table-content"]
                   [:td border-style-nw (code-text ":row-content")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 (code-text ":row-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw "rc-v-table-col-footers rc-v-table-viewport"]
                   [:td border-style-nw (code-text ":col-footers")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[box]")]
                   [:td border-style-nw "rc-v-table-col-footer-content rc-v-table-content"]
                   [:td border-style-nw (code-text ":col-footer-content")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 (code-text ":col-footer-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[scrollbar]")]
                   [:td border-style-nw "rc-v-table-h-scroll"]
                   [:td border-style-nw (code-text ":h-scroll")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[v-box]")]
                   [:td border-style-nw "rc-v-table-right-section"]
                   [:td border-style-nw (code-text ":right-section")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw "rc-v-table-top-right rc-v-table-content"]
                   [:td border-style-nw (code-text ":top-right")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 (code-text ":top-right-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw "rc-v-table-row-footers rc-v-table-viewport"]
                   [:td border-style-nw (code-text ":row-footers")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[v-box]")]
                   [:td border-style-nw "rc-v-table-row-footer-content rc-v-table-content"]
                   [:td border-style-nw (code-text ":row-footer-content")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 (code-text ":row-footer-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw "rc-v-table-bottom-right rc-v-table-content"]
                   [:td border-style-nw (code-text ":bottom-right")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 (code-text ":bottom-right-renderer"))]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[v-box]")]
                   [:td border-style-nw "rc-v-table-v-scroll-section"]
                   [:td border-style-nw (code-text ":v-scroll-section")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[scrollbar]")]
                   [:td border-style-nw "rc-v-table-v-scroll"]
                   [:td border-style-nw (code-text ":v-scroll")]
                   [:td border-style ""]]]]]]))

(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[v-table ... ]"
                            "src/re_com/v_table.cljs"
                            "src/re_demo/v_table.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                      [p "Renders a scrollable table with optional fixed column and row headers and footers, totalling nine addressable sections."]
                                      [p "By default, it only displays rows that are visible, so it is very efficient for large data structures."]
                                      [args-table table-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [p "Refer to the demo in the [basic-v-table] section."]]]]]

              [v-table-component-hierarchy]]])
