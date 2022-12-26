(ns re-demo.v-table
  (:require
    [re-com.text               :refer [p]]
    [re-com.box                :refer [h-box v-box box gap]]
    [re-com.v-table            :refer [v-table-parts-desc v-table-args-desc]]
    [re-com.tabs               :refer [horizontal-tabs]]
    [re-com.util               :refer [px]]
    [re-demo.v-table-sections  :refer [sections-render]]
    [re-demo.v-table-demo      :refer [demo]]
    [re-demo.v-table-renderers :refer [table-showing-renderers]]
    [re-demo.utils             :refer [source-reference panel-title title2 title3 parts-table args-table status-text new-in-version github-hyperlink]]
    [reagent.core              :as    reagent]))

(defn notes-column
  []
  [v-box
   :gap      "10px"
   :width    "550px"
   :padding  "0px 100px 0px 0px"
   :children [[title2 "Notes"]
              [status-text "Alpha" {:color "red" :font-weight "bold"}]
              [new-in-version "v2.13.0"]
              [p [:code "v-table"] " provides a framework for creating table-ish visual structures. It is low level and abstract and, "
               "while it is very flexible in some ways, it is rigid in others, which means it could either be a perfect fit your use case, or it might be useless. "]
              [p "We use it as a base to create a number of components: one best described as \"pivot tables except they are writable\", and another a \"Gantt-chart-looking planning tool with complex editing and totalling\"."]

              [p "Imagine an Excel workbook. It is a large \"canvas\" (of rows and columns), too big to be viewed all at once - you must use scrollbars to explore it. "
               "Now imagine that you \"lock/freeze\" a few rows at the top because they contain " [:b [:i "column headings"]]  " and also a few rows at the bottom "
               "which contain say, totals - call them " [:b [:i "column footers"]]  ". Likewise, in the horizontal, we want to lock/freeze a few left-most columns - let's call this area " [:b [:i "row headers"]]  ", "
               "and some of the right-most columns - call that " [:b [:i "row-footers"]]  ". As  you scroll around the large worksheet, these locked areas always remain in view - eg: "
               "you can always see the column headings.  As you scroll left and right, the column headings/footers scroll horizontally in-sync with the central body of cells/worksheet.  "
               "So too the row-headers and row-footers scroll vertically to match the main body of worksheet/cells you are viewing."]

              [p [:code "v-table"] " creates a virtual, scrolling canvas which has " [:b [:i "nine sections"]] ":"]
              [sections-render]

              [p "Use the scrollbars to view the entire canvas. Sections 4, 5 and 6 will scroll horizontally in sync.  Likewise sections 2, 5 and 6 will scroll vertically in sync. The four corners are fixed. All sections, other than 5, are optional."]

              [title3 "A Row Oriented Canvas"]
              [p [:code "v-table"] " delivers a row oriented canvas:"]
              [:ol
               [:li "it is sufficiently abstract that it doesn't even have a native concept of columns - "
                "which is odd for something calling itself \"a table\", right? But the visual contents of each row is up to you. Render columns into them, if you want."]
               [:li "you can have a million rows in your table because it will render only those few which are currently viewable, but it does not virtualise the horizontal extent of the row - each visible row will be fully rendered to DOM."]
               [:li "all data rows must have the same fixed height. (But the rows within the column headers/footers can be of any height)"]]


              [title3 "Your Renderers"]

              [p [:code "v-table"] " is a framework - you supply various render functions, for the various " [:code "sections"] ", and it will call them as needed, and then orchestrate their (hiccup) output into a scrolling and virtualisation whole."]
              [:p "In the following table, which has 5 data rows, colours and labels are used to identify where renderers are used so you can see how they are combined into a whole."]

              [table-showing-renderers]
              [p "Notes:"]
              [:ul
               [:li "each of the 5 data rows are rendered across three sections (2, 5 and 8). One renderer does the row's header, one the footer, and another does everything in between."]
               [:li "v-table doesn't do any grid lines. If you want them, your renderers must draw them."]
               [:li "the column header section and column footer section do not have any concept of rows. If they contain more than one row, you'll need to render them all at once."]]

              [title3 "Performance"]
              [p "For performance reasons, " [:code "v-table"] " virtualises rendering rows but even so, with wide tables, or tables showing a lot of rows at once, there can be a lot of DOM being thrown around, and so efficiency and performance can be a consideration. "]
              [p "You should pay attention to the three renderers for data rows (sections 2,5,8). " [:code "v-table"] " must call them a lot as the user scrolls vertically. The other 6 renderers are called very little (once?) and it is unlikely they could impact performance too much. "]
              [p "If you do run into performance issues, you may need to move away from using \"flex\" layouts in your data row renderers and instead switch to relative positioning or some other less computationally intensive scheme. "]

              [title3 "Section Dimensions"]
              [p "Widths:"]
              [:ul
               [:li "the width of the three left-most sections 1,2,3 is determined by the renderers of these sections. The \"widest\" hiccup returned will determine the width of all."]
               [:li "the overall width of the center sections 4,5,6 is determined by the argument " [:code ":row-content-width"] ". " 
                    "Because of space constraints, only part of this full width may be visible to the user, triggering scrollbars. "
                     "By default, " [:code "v-table"] " will expand to the greatest width possible but you can explicitly control the horizontal extent visible for these sections via " [:code ":row-viewport-width"] "."]
               [:li "the width of the three right-most sections 7,8,9 is determined by the renderers of these sections. The \"widest\" hiccup returned will determine the width of all."]
               [:li "finally, the overall (maximum) width of the table can be set with the " [:code ":max-width"] " argument. All these width settings will logically interact with each other, along with the width constraints of the parent to determine the final shape of the table."]]


              [p "Heights:"]
              [:ul
               [:li "the height of the three top-most sections 1,4,7 is determined by the argument " [:code ":column-header-height"] "."]
               [:li "the height of the center sections 2,5,8 is determined by the argument " [:code ":row-viewport-height"] ". "
                "By default, " [:code "v-table"] " will expand to the greatest height possible but you can explicitly control the vertical extent visible for these sections via " [:code ":max-row-viewport-height"] "."]
               [:li "the height of the three bottom-most sections 3,6,9 is determined by the argument " [:code ":column-footer-height"] "."]
               [:li "regarding the overall height of the table, there is no specific arg equivalent of " [:code ":max-width"] " but as mentioned above, use " [:code ":max-row-viewport-height"] " to specify the maximum height for row sections. All these height settings will logically interact with each other, along with the height constraints of the parent to determine the final shape of the table."]]]])


(defn dependencies
  []
  [v-box
   :children
   [[title2 "Required Dependencies"]
    [p "Add "
     [:a {:href "https://github.com/day8/re-com/blob/master/run/resources/public/assets/scripts/detect-element-resize.js"}
      [:code "detect-element-resize.js"]]
     " to your " [:code "index.html"] " or equivalent as per below:"]
    [:code    
     "<script src=\"path/to/detect-element-resize.js\" type=\"text/javascript\"></script>"]
    [:br]
    [p "If you do not include this script the " [:code "v-table"] " will not render and you will get an error in the "
     "console like " [:code "Your project is missing detect-element-resize.js or detect-element-resize-externs.js could not setup v-table."]]]])


(defn panel
  []
  (let [tab-defs        [{:id :note       :label "Notes"}
                         {:id :parameters :label "Parameters"}]
        selected-tab-id (reagent/atom (:id (first tab-defs)))]
    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[v-table ... ]" "src/re_com/v_table.cljs" "src/re_demo/v_table.cljs"]
                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :children [[horizontal-tabs
                                           :model     selected-tab-id
                                           :tabs      tab-defs
                                           :style     {:margin-top "12px"}
                                           :on-change #(reset! selected-tab-id %)]
                                          (case @selected-tab-id
                                            :note       [notes-column]
                                            :parameters [args-table v-table-args-desc {:total-width       "550px"
                                                                                       :name-column-width "180px"}])]]
                              [v-box
                               :children [[dependencies]
                                          [demo]]]]]
                  [parts-table "v-table" v-table-parts-desc]]])))
