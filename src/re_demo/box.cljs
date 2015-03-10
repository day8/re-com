(ns re-demo.box
  (:require [re-com.box      :refer [h-box v-box box box-args-desc gap]]
            [re-com.buttons  :refer [hyperlink-href]]
            [re-demo.utils   :refer [panel-title component-title args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[box ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/box.cljs"]
                            [status-text "Beta"]]]

              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [:span "The box is used to..."]
                                      [args-table box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
