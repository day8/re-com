(ns re-demo.basic-v-table
  (:require [re-com.core    :refer [h-box gap v-box v-table hyperlink-href p]]
            [re-com.basic-v-table :as basic-v-table :refer [table-args-desc]]
            [re-demo.utils  :refer [panel-title title2 args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[basic-v-table ... ]"
                            "src/re_com/basic_v_table.cljs"
                            "src/re_demo/basic_v_table.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                      [p "Renders a scrollable table with optional fixed column and row headers, totalling four addressable sections."]
                                      [p "By default, it only displays rows that are visible, so it is very efficient for large data structures."]
                                      [args-table table-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [basic-v-table/table
                                       :cols [{:id :country :header-label "Country" :row-label-fn :country :width 130 :align "left"}
                                              {:id :total-cases :header-label "Total Cases" :row-label-fn :total-cases :width 120 :align "right"}
                                              {:id :total-deaths :header-label "Total Deaths" :row-label-fn :total-deaths :width 120 :align "right"}]
                                       :model (atom [{:country "USA"    :total-cases 23143197 :total-deaths 385249}
                                                     {:country "Brazil" :total-cases 8133833  :total-deaths 203617}
                                                     {:country "India"  :total-cases 10479913 :total-deaths 151364}
                                                     {:country "Mexico" :total-cases 1541633  :total-deaths 134368}])]]]]]]])
