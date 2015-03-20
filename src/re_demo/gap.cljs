(ns re-demo.gap
  (:require [re-com.core   :refer [h-box v-box gap]]
            [re-com.box    :refer [gap-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[gap ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/gap.cljs"]]]

              [h-box
               :gap      "100px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Stable"]
                                      [paragraphs
                                       [:p "TBA..."]]
                                      [args-table gap-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span.all-small-caps "TBA..."]]]]]
              [gap :size "30px"]]])
