(ns re-demo.border
  (:require [re-com.core   :refer [h-box v-box gap border]]
            [re-com.box    :refer [border-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[border ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/border.cljs"]]]

              [h-box
               :gap      "50px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[status-text "Alpha"]
                                      [component-title "Notes"]
                                      [:span "The border is used to..."]
                                      [args-table border-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
