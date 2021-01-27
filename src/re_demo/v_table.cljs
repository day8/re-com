(ns re-demo.v-table
  (:require [re-com.core    :refer [h-box gap v-box box p]]
            [re-com.v-table :refer [v-table-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table status-text]]
            [re-com.util    :refer [px]]
            [re-demo.v-table-sections-demo :refer [sections-demo]]))

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
   :width    "400px"
   :children [[title2 "Notes"]
              [status-text "Alpha" {:color "red" :font-weight "bold"}]
              [p "This Component provides a framework for creating a table-like visual structures - ones that are organised around rows with a horizontal structure (columns?). It can be read-only or read-write."]
              [p "But this Component is low level and abstract.  While it is very flexible along some dimensions, it is rigid along others, so you'll have to figure out if it is appropriate for your usecase."]
              [p "It is a framework. You supply functions which do all the rendering and it coordinates their (hiccup) output. Essentially, it provides you with a scrolling and virtualisation infrastructure."]
              [p "Imagine an Excel workbook which is a  \"canvas\" of rows and columns, too big to be viewed all at once. You must use scrollbars to see it all. "
                 "Now imagine you want to \"lock/freeze\" a few rows at the top because they contain \"column headings\" and also a few rows at the bottom "
                 "which contain say, totals - call them \"column footers\". Likewise, in the horizontal, we want to lock/freeze a few left-most columns - let's call this area \"row headers\", " 
                 "and some of the right-most columns - call that \"row-footers\". Now, as  you scroll around the large worksheet, these locked areas always remain in view - eg: "
                 "you can always see the column headings.  As you scroll left and right, the column headings/footers scroll horizontally in-sync with the central body of cells/worksheet.  "
                 "So too the row-headers and row-footers scroll vertically to match the main body of worksheet/cells you are viewing."]
              [p "So, this Component will help you to create a virtual, scrolling table structure.  It models a table as having up to nine optional \"sections\":  "
                 "the four locked ones described above, plus the centre \"body\", and finally the four corners created by the intersection of the locked sections (top-left, bottom-right, etc)."]
              [v-box
               :style    {:border "1px solid #333"}
               :children [[h-box
                           :justify  :center
                           :align    :center
                           :height   "70px"
                           :style    {:border-bottom "1px solid #333"}
                           :children [[box
                                       :size    "1"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :style   {:border-right "1px solid #333"}
                                       :child   [:span "top-left (1)"]]
                                      [box
                                       :size    "2"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :style   {:border-right "1px solid #333"}
                                       :child [:span "column-headers (4)"]]
                                      [box
                                       :size    "1"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :child [:span "top-right (7)"]]]]
                          [h-box
                           :justify  :center
                           :align    :center
                           :height   "150px"
                           :style    {:border-bottom "1px solid #333"}
                           :children [[box
                                       :size    "1"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :style   {:border-right "1px solid #333"}
                                       :child   [:span "row-headers (2)"]]
                                      [box
                                       :size    "2"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :style   {:border-right "1px solid #333"}
                                       :child   [:span "rows (5)"]]
                                      [box
                                       :size    "1"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :child   [:span "row-footers (8)"]]]]
                          [h-box
                           :justify  :center
                           :align    :center
                           :height   "70px"
                           :children [[box
                                       :size    "1"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :style   {:border-right "1px solid #333"}
                                       :child   [:span "bottom-left (3)"]]
                                      [box
                                       :size    "2"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :style   {:border-right "1px solid #333"}
                                       :child   [:span "column-footers (6)"]]
                                      [box
                                       :size    "1"
                                       :height  "100%"
                                       :align   :center
                                       :justify :center
                                       :child   [:span "bottom-right (9)"]]]]]]
               [p "Except, this Component is sufficiently abstract that it doesn't even have a native concept of columns - which is kinda odd for something calling itself \"a table\", right? However, it does understand rows - indeed, the design is very row centric - and it does understand that rows have a horizontal extent. If your rows have columns, you'll have to render them yourself."]
               [p "This Component will allow you to have a million rows in your table because it will render only those few which are currently viewable, but it does not virtualise the horizontal extent of the row - each visible row will be fully rendered to DOM."]
               [p "So, it might be a good framework for representing complicated spreadsheet with many rows, but not too many columns. Or perhaps a Gannt Chart (although rendering lines up and down across rows involves swimming slightly against the tide abstractions-wise).  But, anyway, that sort of thing."]
               [p "BTW, all rows must have the same fixed height."]
               [p "While it certainly isn't required, it will be felicitous if the 9 (mostly optional) section renderers you supply return flexbox-friendly hiccup, including " [:code "v-box"] " and " [:code "h-box"] "."]
              ]])


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
                          [sections-demo]]]
              [v-table-component-hierarchy]]])
