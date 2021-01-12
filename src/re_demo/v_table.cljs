(ns re-demo.v-table
  (:require [re-com.core    :refer [h-box gap v-box v-table hyperlink-href p]]
            [re-com.v-table :refer [table-args-desc]]
            [re-demo.utils  :refer [panel-title title2 args-table github-hyperlink status-text]]))


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
                                      [status-text "Stable"]
                                      [p "Renders a scrollable table with optional fixed column and row headers and footers, totalling nine addressable sections."]
                                      [p "By default, it only displays rows that are visible, so it is very efficient for large data structures."]
                                      [args-table table-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [p "Refer to the demo in the [basic-v-table] section."]]]]]]])
