(ns re-demo.label
  (:require [re-com.core      :refer [label label-args-desc]]
            [re-com.box       :refer [h-box v-box box gap line]]
            [re-demo.utils    :refer [panel-title component-title args-table github-hyperlink]]))


(defn label-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[label ... ]"
                            [github-hyperlink "Component Source" "src/re_com/core.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/label.cljs"]]]
              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[args-table label-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [v-box
                                       :children [[label :label "This is a label"]
                                                  [:br]
                                                  [:p "(currently there's a lack of interactive controls here. Will possibly add in the future)"]]]]]]]]])


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [label-demo])
