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
                                       [:p "Introduces white space into layouts. Typically placed between the children of a v-box or h-box."]
                                       [:p "The white space added will be in the expected direction (i.e. horizontally for h-box or vertically for v-box)."]]
                                      [args-table gap-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [paragraphs
                                       [:span "Here is some sample code..."]]
                                      [:pre
                                       {:style {:width "40em"}}
"[h-box
 :gap      \"10px\"                ; <-- in between children
 :children [[component-1]
            [component-2]
            [gap :size \"5px\"]    ; <-- one off
            [component-3]]]"]
                                      [paragraphs
                                       [:span "Notes:"]
                                       [:ul
                                        [:li "h-box and v-box allow a " [:code ":gap"] " parameter which inserts a given amount of white space between children."]
                                        [:li "Where you need more adhoc control, use the gap component."]
                                        [:li "In the example above, the gap between the second and third components will be 25px because the gap component (5px) is surrounded above and below by 10px " [:code ":gap"] "s."]]]]]]]
              [gap :size "30px"]]])
