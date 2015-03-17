(ns re-demo.line
  (:require [re-com.core   :refer [h-box v-box box gap line]]
            [re-com.box    :refer [line-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[line ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/line.cljs"]]]

              [h-box
               :gap      "50px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Alpha"]
                                      [:span "The line is used to..."]
                                      [args-table line-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
