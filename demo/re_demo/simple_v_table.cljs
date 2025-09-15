(ns re-demo.simple-v-table
  (:require-macros
   [re-com.core                           :refer []])
  (:require
   [re-com.core                           :refer [at h-box gap v-box p line horizontal-tabs]]
   [re-com.simple-v-table                 :refer [simple-v-table-parts-desc simple-v-table-args-desc]]
   [re-com.util                           :refer [px]]
   [re-demo.simple-v-table-sales          :as simple-v-table-sales]
   [re-demo.simple-v-table-periodic-table :as simple-v-table-periodic-table]
   [re-demo.utils                         :refer [source-reference panel-title title2 title3 parts-table args-table status-text new-in-version]]
   [reagent.core                          :as    reagent]))

(defn notes-column
  []
  [v-box
   :src      (at)
   :gap      "10px"
   :width    "550px"
   :padding  "0px 100px 0px 0px"
   :children [[title2 "Notes"]
              [status-text "Alpha" {:color "red" :font-weight "bold"}]
              [new-in-version "v2.13.0"]
              [p "This component provides a table which virtualises row rendering. You can have 1M rows but only those currently viewable will be in the DOM."]
              [p [:code "simple-v-table"] " is built on " [:code "v-table"] " and it exists because " [:code "v-table"] " is too low level and complicated for everyday use."]
              [title3 "Features"]
              [:ul
               [:li "Primary use case involves showing a rectangular visual structure, with entities in rows and attributes of those entities in columns. Typically, read-only."]
               [:li "Unlimited columns with a fixed column header at the top"]
               [:li "Unlimited (virtualised) rows with an (optional) fixed row header at the left by simply specifying the number of columns to fix"]
               [:li "Click on a column header to sort the rows in a column"]
               [:li "Shift-click on a column header to sort multiple columns hierarchically."]
               [:li "Most aspects of the table are stylable using the " [:code ":parts"] " argument that can set " [:code ":class"] " or " [:code ":style"] " attributes"]
               [:li "Individual rows can be dynamically styled based on row data"]
               [:li "Individual cells can be dynamically styled based on row data"]
               [:li "Alt+Click on a row in the table to see the data object for that row in DevTools (works best in dev mode with cljs-devtools)"]]
              [title3 "Not Implemented"]
              [p "The following features common to other table widgets are not implemented. After all, this is 'simple' v-table."]
              [:ul
               [:li "Resizable columns; e.g. drag to resize columns"]
               [:li "Re-orderable columns; e.g. drag to re-order columns"]
               [:li "Hide/show columns"]
               [:li "Selection of rows, columns and cells (as supported by v-table)"]
               [:li "Pagination (virtualised instead)"]
               [:li "Group by columns"]
               [:li "Filtering, instead just pre-filter " [:code ":model"] " externally using other widgets."]]
              [title3 "Sizing"]
              [:ul
               [:li "A table's dimensions will grow and shrink, to fit the space provided by its parent. When the parent imposes dimensions that are insufficient to show all of the table, scrollbars will appear."]
               [:li "Other times, we want a table to impose certain dimensions. Eg, it should always show 10 rows, and have no horizontal scrollbar, and we want the parent dimensions to change to accommodate."]
               [:li [:strong "Width"]
                [:ul
                 [:li "The full horizontal extent of the table is determined by the accumulated width of the columns"]
                 [:li "If the width provided by the table's parent container is less than this extent, then horizontal scrollbars will appear for the unfixed columns"]
                 [:li "Where you wish to be explicit about the table's viewable width, use the " [:code ":max-width"] " arg"]]]
               [:li [:strong "Height"]
                [:ul
                 [:li "The full vertical extent of the table is determined by the accumulated height of all the rows"]
                 [:li "If the height provided by the table's parent container is less than this extent, then vertical scrollbars will appear"]
                 [:li "Where you wish to be explicit about the table's viewable height, use the " [:code ":max-rows"] " arg"]]]
               [:li "Even if you are explicit via " [:code ":max-width"]  " or " [:code ":max-rows"] ", the parent's dimensions will always dominate, if they are set"]]
              [title3 "Sorting"]
              [:ul
               [:li [:strong "sort-by"]
                [:ul
                 [:li "Items in " [:code ":columns"] " have an optional " [:code ":sort-by"] " key."]
                 [:li "If the value is " [:code "true"] ", clicking the column header will sort all the rows, using the result of the column's " [:code ":row-label-fn"] " as a sort key."]
                 [:li [:code ":sort-by"] " can also be map, with optional keys " [:code ":comp"] " and " [:code ":keyfn"] ", corresponding to the parameters of " [:code "clojure.core/sort-by"] "."]]]
               [:li [:strong "hierarchical sort"]
                [:ul
                 [:li "Shift-clicking a column header will conjoin that column into a hierarchical sort. A number will appear next to the sort icon, indicating its sorting precedence."]
                 [:li "For instance, click " [:strong "name"] ", then shift-click " [:strong "units"] ". The columns will appear as " [:strong "name ↓1 ... units ↓2."]]
                 [:li "Rows will sort by the column marked " [:strong "1"] ", except when any two rows compare equally, they will sort by " [:strong "2"] "."]]]]
              [title3 "Export"]
              [:ul
               [:li "Pass " [:code ":show-export-button? true"] " to mount the export-button component into section 3 of the table (see " [:code ":top-right-renderer"] " in " [:code "v-table"] "."]
               [:li "Clicking this button invokes " [:code ":on-export"] " with keyword arguments " [:code ":rows"] " and " [:code ":columns"] ". " [:code ":rows"] " is sorted."]
               [:li "The default " [:code ":on-export"] " handler removes any columns declared with " [:code ":export? false"] ", serializes a TSV string and writes it to the clipboard."]
               [:li "You can also pass your own component function as " [:code ":export-button-renderer"] ". It can accept keyword arguments " [:code ":rows, :columns, :on-export"] "."]]
              [p "The \"Sales Table Demo\" (to the right) allows you to experiment with these concepts."]]])

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
    [p "If you do not include this script the " [:code "simple-v-table"] " will not render and you will get an error in the "
     "console like " [:code "Your project is missing detect-element-resize.js or detect-element-resize-externs.js could not setup v-table."]]]])

(defn panel
  []
  (let [tab-defs        [{:id :note :label "Notes"}
                         {:id :parameters :label "Parameters"}]
        selected-tab-id (reagent/atom (:id (first tab-defs)))]
    (fn []
      [v-box
       :src      (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[simple-v-table ... ]" "src/re_com/simple_v_table.cljs" "demo/re_demo/simple_v_table.cljs"]
                  [h-box
                   :src      (at)
                   :gap      "50px"
                   :children [[v-box
                               :src      (at)
                               :children [[horizontal-tabs
                                           {:src       (at)
                                            :model     selected-tab-id
                                            :tabs      tab-defs
                                            :parts     {:tab {:style {:margin-top "12px"}}}
                                            :on-change #(reset! selected-tab-id %)}]
                                          (case @selected-tab-id
                                            :note       [notes-column]
                                            :parameters [args-table simple-v-table-args-desc {:total-width       "550px"
                                                                                              :name-column-width "180px"}])]]
                              [v-box
                               :src      (at)
                               :children [[dependencies]
                                          [title2 "Demo #1"]
                                          [gap
                                           :src      (at)
                                           :size "15px"]
                                          [simple-v-table-sales/demo]
                                          [source-reference "for above simple-v-table" "demo/re_demo/simple_v_table_sales.cljs"]
                                          [gap
                                           :src      (at)
                                           :size "40px"]
                                          [line :src      (at)]
                                          [title2 "Demo #2"]
                                          [gap
                                           :src      (at)
                                           :size "15px"]
                                          [simple-v-table-periodic-table/demo]
                                          [source-reference "for above simple-v-table" "demo/re_demo/simple_v_table_periodic_table.cljs"]]]]]
                  [parts-table "simple-v-table" simple-v-table-parts-desc]]])))
