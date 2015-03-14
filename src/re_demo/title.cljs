(ns re-demo.title
  (:require [re-com.core   :refer [h-box v-box box gap line title]]
            [re-com.text   :refer [title-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text]]))


(defn title-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[title ... ]"
                            [github-hyperlink "Component Source" "src/re_com/text.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/title.cljs"]]]
              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[status-text "Alpha"]
                                      [component-title "Notes"]
                                      [:span "The title is used to..."]
                                      [args-table title-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [v-box
                                       :children [[title :label "This is a title"]]]]]]]]])


(defn panel    ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [title-demo])
