(ns re-demo.v-box
  (:require [re-com.core   :refer [h-box gap v-box hyperlink-href p]]
            [re-com.box    :refer [v-box-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[v-box ... ]"
                            "src/re_com/box.cljs"
                            "src/re_demo/v_box.cljs"]

              [h-box
               :gap      "100px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "Same as [h-box] except in the vertical direction."]
                                      [p "See [h-box] for further details."]
                                      [args-table v-box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [p "Refer to the full interactive demo in the [h-box] section, then tilt your head 90 degrees."]]]]]]])
