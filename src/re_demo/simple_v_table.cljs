(ns re-demo.simple-v-table
  (:require
    [reagent.core          :as reagent]
    [re-com.core           :refer [h-box gap v-box p line]]
    [re-com.simple-v-table :refer [simple-v-table-args-desc]]
    [re-demo.utils         :refer [panel-title title2 title3 args-table status-text]]
    [re-demo.simple-v-table-sales :as simple-v-table-sales]
    [re-demo.simple-v-table-periodic-table :as simple-v-table-periodic-table]
    [re-com.util           :refer [px]]))

(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[simple-v-table ... ]"
                            "src/re_com/simple_v_table.cljs"
                            "src/re_demo/simple_v_table.cljs"]
              [h-box
               :gap      "106px"
               :children [[v-box

                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [line]
                                      [gap :size (px 15)]
                                      [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                      [gap :size (px 15)]
                                      [p "This component provides a table which virtualises row rendering. You can have 1M rows but only those currently viewable will be in the DOM."]
                                      [p [:code "simple-v-table"] " is built on " [:code "v-table"] " and it exists because " [:code "v-table"] " is too low level and complicated for everyday use."]
                                      [title3 "Features"]
                                      [:ul
                                       [:li "Primary usecase involves showing a rectangular visual structure, with entities in rows and attributes of those entities in columns. Typically, read-only."]
                                       [:li "Unlimited columns with a fixed column header at the top"]
                                       [:li "Unlimited (virtualised) rows with an ()optional) fixed row header at the left by simply specifying the number of columns to fix"]
                                       [:li "Most aspects of the table are stylable using the " [:code ":parts"] " argument that can set " [:code ":class"] " or " [:code ":style"] " attributes"]
                                       [:li "Individual rows can be dynamically styled based on row data"]
                                       [:li "Individual cells can be dynamically styled based on row data"]]
                                      
                                      [title3 "Sizing"]
                                      [:ul
                                       [:li "A table's dimensions will grow and shrink, to fit the space provided by its parent. When the parent imposes dimensions that are insufficient to show all of the table, scrollbars will appear."]
                                       [:li "Other times, we want a table to impose certain dimensions. Eg, it should always show 10 rows, and have no horizontal scrollbar, and we want the parent dimensions to change to accomodate."]
                                       [:li "Width"
                                        [:ul
                                         [:li "The full horizontal extent of the table is determined by the accumulated width of the columns"]
                                         [:li "If the width provided by the table's parent container is less than this extent, then horizontal scrollbars will appear for the unfixed columns"]
                                         [:li "Where you wish to be explicit about the table's viewable width, use the " [:code ":max-width"] " arg"]]]
                                       [:li "Height"
                                        [:ul
                                         [:li "The full vertical extent of the table is determined by the accumulated height of all the rows"]
                                         [:li "If the height provided by the table's parent container is less than this extent, then vertical scrollbars will appear"]
                                         [:li "Where you wish to be explicit about the table's viewable height, use the " [:code ":max-rows"] " arg"]]]
                                       [:li "Even if you are explicit via " [:code ":max-width"]  " or " [:code ":max-rows"] ", the parent's dimensions will always dominate, if they are set"]]
                                       [p "The \"Sales Table Demo\" (to the right) allows you to experiment with these concepts."]
                                      [args-table simple-v-table-args-desc]]]
                          [v-box

                           :children [[title2 "Demos"]
                                      [line]
                                      [gap :size (px 15)]
                                      [simple-v-table-sales/demo]
                                      [gap :size "40px"]
                                      [line]
                                      [simple-v-table-periodic-table/demo]]]]]]])
