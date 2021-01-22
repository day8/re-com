(ns re-demo.simple-v-table
  (:require
    [reagent.core          :as reagent]
    [re-com.core           :refer [h-box gap v-box v-table hyperlink-href p]]
    [re-com.simple-v-table :refer [simple-v-table simple-v-table-args-desc]]
    [re-demo.utils         :refer [panel-title title2 args-table github-hyperlink status-text]]))


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
                                      [args-table simple-v-table-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [simple-v-table
                                       :fixed-column-count 1
                                       :fixed-column-border-color "red"
                                       :columns [{:id :country :header-label "Country" :row-label-fn :country :width 130 :align "left"}
                                                 {:id :total-cases :header-label "Total Cases" :row-label-fn :total-cases :width 120 :align "right"}
                                                 {:id :total-deaths :header-label "Total Deaths" :row-label-fn :total-deaths :width 120 :align "right"}]
                                       :model   (reagent/atom [{:country "USA"    :total-cases 23143197 :total-deaths 385249}
                                                               {:country "Brazil" :total-cases 8133833  :total-deaths 203617}
                                                               {:country "India"  :total-cases 10479913 :total-deaths 151364}
                                                               {:country "Mexico" :total-cases 1541633  :total-deaths 134368}])]]]]]]])
