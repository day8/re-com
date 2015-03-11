(ns re-demo.v-box
  (:require [re-com.core     :refer [h-box gap v-box hyperlink-href]]
            [re-com.box      :refer [v-box-args-desc]]
            ;[re-com.box      :refer [h-box gap v-box v-box-args-desc]]
            ;[re-com.buttons  :refer [hyperlink-href]]
            [re-demo.utils   :refer [panel-title component-title args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[v-box ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/v_box.cljs"]
                            [status-text "Beta"]]]

              [h-box
              :gap      "50px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [:span "The v-box is used to..."]
                                      [args-table v-box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
