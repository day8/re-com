(ns re-demo.v-table
  (:require [re-com.core    :refer [h-box gap v-box box p]]
            [re-com.v-table :refer [v-table-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table status-text]]
            [re-com.util    :refer [px]]
            [re-demo.v-table-sections  :refer [sections-render]]
            [re-demo.v-table-demo      :refer [demo]]
            [re-demo.v-table-renderers :refer [table-showing-renderers]]))

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
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre  {:style {:width 450}} 
                     "[v-table\n"
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
                   [:td border-style-nw "rc-v-table-column-headers rc-v-table-viewport"]
                   [:td border-style-nw (code-text ":column-headers")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[:div]")]
                   [:td border-style-nw "rc-v-table-selection"]
                   [:td border-style-nw (code-text ":column-header-selection-rect")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[box]")]
                   [:td border-style-nw "rc-v-table-column-header-content rc-v-table-content"]
                   [:td border-style-nw (code-text ":column-header-content")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 (code-text ":column-header-renderer"))]
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
                   [:td border-style-nw "rc-v-table-column-footers rc-v-table-viewport"]
                   [:td border-style-nw (code-text ":column-footers")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[box]")]
                   [:td border-style-nw "rc-v-table-column-footer-content rc-v-table-content"]
                   [:td border-style-nw (code-text ":column-footer-content")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 (code-text ":column-footer-renderer"))]
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


(defn notes-column
  []
  [v-box
   :gap      "10px"
   :width    "450px"
   :children [[title2 "Notes"]
              [status-text "Alpha" {:color "red" :font-weight "bold"}]
              [p "This Component provides a framework for creating table-ish visual structures. It is low level and abstract and, "
               "while it is very flexible in some ways, it is rigid in others, which means it could be a perfect fit your usecase. Or it might be useless. "
               "We use it to create components best described as \"pivot tables except they are writable\", and \"Gannt-chart-looking planning tools with complex editting and totalling\"."]
              [p "Imagine an Excel workbook. It is a large \"canvas\" (of rows and columns), too big to be viewed all at once - you must use scrollbars to explore it. "
               "Now imagine that you \"lock/freeze\" a few rows at the top because they contain " [:b [:i "column headings"]]  " and also a few rows at the bottom "
               "which contain say, totals - call them " [:b [:i "column footers"]]  ". Likewise, in the horizontal, we want to lock/freeze a few left-most columns - let's call this area " [:b [:i "row headers"]]  ", "
               "and some of the right-most columns - call that " [:b [:i "row-footers"]]  ". As  you scroll around the large worksheet, these locked areas always remain in view - eg: "
               "you can always see the column headings.  As you scroll left and right, the column headings/footers scroll horizontally in-sync with the central body of cells/worksheet.  "
               "So too the row-headers and row-footers scroll vertically to match the main body of worksheet/cells you are viewing."]

              [p [:code "v-table"] " creates a virtual, scrolling canvas which has " [:b [:i "nine sections"]] ":"]
              [sections-render]
              [p "Move the scrollbars to see the entire canvas. Sections 4, 5 and 6 will scroll horizonotally in sync.  Likewise sections 2, 5 and 6 will scroll vertically in sync. The four corners are fixed."]

              [title3 "A Row Oriented Canvas"]
              [p [:code "v-table"] " delivers a row oriented canvas:"]
              [:ol
               [:li "it is sufficiently abstract that it doesn't even have a native concept of columns - "
                "which is odd for something calling itself \"a table\", right? But the visual contents of each row is up to you. Render columns into them, if you want."]
               [:li "you can have a million rows in your table because it will render only those few which are currently viewable, but it does not virtualise the horizontal extent of the row - each visible row will be fully rendered to DOM."]
               [:li "all data rows must have the same fixed height. (Column headers/footers can be an arbitrary height)"]]


              [title3 "Your Renderers"]

              [p [:code "v-table "] " is a framework. You supply various render functions, for the various " [:code "sections"] ", and it will call them as needed, and then orchestrate their (hiccup) output into a scrolling and virtualisation whole."]
              [:p "In the following table, which has 5 data rows, colours and labels are used to identify where renderers are used so you can see how they are combined into a whole."]

              [table-showing-renderers]
              [:ul
               [:li "Each of the 5 data rows are rendered across 3 sections (2, 5 and 8). One renderer does the row's header, one the footer, and another does everything in between."]
               [:li "v-table doesn't do any grid lines. If you want them, your renderers must draw them."]
               [:li "there is only one renderer for the entire column header and column footer. If they contain more than one row, you'll need to render them all at once."]
               [:li "there is only one renderer for the entire column headers. If it contains multiple sub-rows, then return hiccup for them all."]]]])


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[v-table ... ]" "src/re_com/v_table.cljs" "src/re_demo/v_table.cljs"]
              [h-box
               :gap      "100px"
               :children [[notes-column]
                          [args-table v-table-args-desc {:total-width "550px" :name-column-width "180px"}]
                          [demo]]]
              [v-table-component-hierarchy]]])
