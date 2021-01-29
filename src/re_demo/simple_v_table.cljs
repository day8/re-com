(ns re-demo.simple-v-table
  (:require
    [reagent.core          :as reagent]
    [re-com.core           :refer [h-box gap v-box v-table hyperlink-href p]]
    [re-com.simple-v-table :refer [simple-v-table simple-v-table-args-desc]]
    [re-demo.utils         :refer [panel-title title2 args-table github-hyperlink status-text]]
    [re-demo.simple-v-table-sales :as simple-v-table-sales]
    [re-demo.simple-v-table-periodic-table :as simple-v-table-periodic-table]))

(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[simple-v-table ... ]"
                            "src/re_com/simple_v_table.cljs"
                            "src/re_demo/simple_v_table.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                      [p "This component provides a table which virtualises row rendering. You can have 1M rows but only those currently viewable will be in the DOM."]
                                      [p [:code "simple-v-table"] " is built on " [:code "v-table"] " and it exists because " [:code "v-table"] " is too low level and complicated for everyday use."]
                                      [p "The features of this component include:"]
                                      [:ul
                                       [:li "Unlimited columns with fixed column header at the top"]
                                       [:li "Unlimited (virtualised) rows with fixed row header at the left by simply specifying the number of columns to fix"]
                                       [:li "Every part of the table is stylable using the " [:code ":parts"] " argument that can set " [:code ":class"] " or " [:code ":style"] " attributes"]
                                       [:li "Individual rows can be dynamically styled based on row data"]
                                       [:li "Individual cells can be dynamically styled based on row data"]]
                                      [p "Understanding table sizing:"]
                                      [:ul
                                       [:li "Sometimes we want a table's dimensions to grow and shrink, subject to the available space provided by the parental context. When those dimensions are not enough to show all of the table, scrollbars will automatically appear"]
                                       [:li "Other times, we would like the table to impose its dimensions and for the context to adjust"]
                                       [:li "The full horizontal extent of the table is determined by the accumulated width of all the specified columns"]
                                       [:li "If the width provided by the table's parent container is less than this extent, then horizontal scrollbars will appear"]
                                       [:li "Where you wish to be explicit about the table's viewable width, use the " [:code ":max-width"] " arg"]
                                       [:li "The full vertical extent of the table is determined by the accumulated height of all the rows"]
                                       [:li "If the height provided by the table's parent container is less than this extent, then vertical scrollbars will appear"]
                                       [:li "Where you wish to be explicit about the table's viewable height, use the " [:code ":max-rows"] " arg"]]
                                      [args-table simple-v-table-args-desc]]]
                          [v-box
                           :gap      "30px"
                           :children [[simple-v-table-sales/demo]
                                      [simple-v-table-periodic-table/demo]]]]]]])
