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
                                       [paragraphs
                                        [:p "Draws a line. Typically placed between the children of a v-box or h-box."]
                                        [:p "The line is added in the expected direction (i.e. vertically for h-box or horizontally for v-box)."]]]
                                      [args-table line-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [paragraphs
                                       [:span "Here is some sample code..."]]
                                      [:pre
                                       {:style {:width "40em"}}
"[h-box
 :gap      \"10px\"
 :children [[grey-box-1]
            [line
             :size  \"3px\"
             :color \"red\"]
            [grey-box-2]]]"]
                                      [paragraphs
                                       [:p "Here is the result..."]]
                                      [h-box
                                       :gap      "10px"
                                       :children [[box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 1"]
                                                  [line
                                                   :size  "3px"
                                                   :color "red"]
                                                  [box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 2"]]]]]]]
              [gap :size "30px"]]])
