(ns re-demo.line
  (:require [re-com.core   :refer [at h-box v-box box gap line p]]
            [re-com.box    :refer [line-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[line ... ]"
                            "src/re_com/box.cljs"
                            "src/re_demo/line.cljs"]

              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "Draws a line. Typically placed between the children of a v-box or h-box."]
                                      [p "The line is added in the expected direction (i.e. vertically for h-box or horizontally for v-box)."]
                                      [args-table line-args-desc]]]
                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [p "For an example, look at the top of this page. There's a title " [:span.bold "[line ...]"]
                                       ", and notice the line under it."]
                                      [p "Here is some sample code..."]
                                      [:pre
                                       {:style {:width "40em"}}
"[h-box
 :gap      \"10px\"
 :children [[grey-box]
            [line
             :size  \"3px\"
             :color \"red\"]
            [grey-box]]]"]
                                      [p "Here is the result..."]
                                      [h-box :src (at)
                                       :gap      "10px"
                                       :children [[box :src (at)
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 1"]
                                                  [line :src (at)
                                                   :size  "3px"
                                                   :color "red"]
                                                  [box :src (at)
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 2"]]]]]]]
              [gap :src (at) :size "30px"]]])
