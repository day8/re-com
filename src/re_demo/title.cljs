(ns re-demo.title
  (:require [re-com.core      :refer [title title-args-desc]]
            [re-com.box       :refer [h-box v-box box gap line]]
            [re-demo.utils    :refer [panel-title component-title args-table github-hyperlink]]))


(defn title-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[title ... ]"
                            [github-hyperlink "Component Source" "src/re_com/core.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/title.cljs"]]]
              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[args-table title-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [v-box
                                       :children [[title :label "This is a title"]
                                                  [:br]
                                                  [:p "(currently there's a lack of interactive controls here. Will possibly add in the future)"]]]]]]]]])


(defn panel    ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [title-demo])
