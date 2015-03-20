(ns re-demo.line
  (:require [re-com.core   :refer [h-box v-box box gap line]]
            [re-com.box    :refer [line-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[line ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/line.cljs"]]]

              [h-box
               :gap      "100px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Stable"]
                                      [paragraphs
                                       [:p "TBA..."]]
                                      [args-table line-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span.all-small-caps "TBA..."]]]]]
              [gap :size "30px"]]])
