(ns re-demo.gap
  (:require [re-com.core   :refer [h-box v-box gap p line box]]
            [re-com.box    :refer [gap-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(def rounded-panel
                          {:background-color "#fff4f4"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :margin           "4px"
                           :padding          "8px"})
(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[gap ... ]"
                            "src/re_com/box.cljs"
                            "src/re_demo/gap.cljs"]

              [h-box
               :gap      "100px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "Introduces whitespace into layouts. Typically placed between the children of a v-box or h-box."]
                                      [p "The whitespace added will be in the expected direction (i.e. horizontally for h-box or vertically for v-box)."]
                                      [args-table gap-args-desc]]]
                          [v-box
                           :children [[title2 "Demo #1"]
                                      [gap :size "10px"]
                                      [p "Example code..."]
                                      [:pre
                                       {:style {:width "40em"}}
"[h-box
 :gap      \"10px\"                ; <-- in between each children
 :children [[grey-box]
            [grey-box]
            [gap :size \"5px\"]    ; <-- ad hoc
            [grey-box]]]"]

                                      [p "Result:"]
                                      [h-box
                                       :gap      "10px"
                                       :style    {:border "dashed 1px red"}
                                       :children [[box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 1"]
                                                  [box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 2"]
                                                  [gap :size "5px"]
                                                  [box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 3"]]]
                                      [gap :size "10px"]
                                       [p "Notes:"
                                        [:ul
                                         [:li "h-box and v-box have a " [:code ":gap"] " parameter which inserts a
                                         given amount of white space between each child."]
                                         [:li "For more ad hoc gaps, use the " [:span.bold "[gap ...]"] " component itself."]
                                         [:li "In this example, the gap between components 2 and 3 will be 25px
                                         because the [gap] is a child of the h-box and will have 10px left and right of it."]]]

                                      [gap :size "10px"]
                                      [line]
                                      [title2 "Demo #2"]
                                      [gap :size "10px"]
                                      [p " "]
                                      [:pre
                                       {:style {:width "40em"}}
                                       "[h-box                 ; <-- no :gap, children seperated by 0px
 :children [
    [grey-box]
    [gap :size \"10px\"] ; <-- absolute gap
    [grey-box]         ; <-- pushed as far left as possible
    [gap :size \"1\"]    ; <-- grows as much as possible
    [grey-box]]]       ; <-- pushed as far right as possible"]

                                      [p "Result:"]
                                      [h-box
                                       :style    {:border "dashed 1px red"}
                                       :children [[box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 1"]
                                                  [gap :size "10px"]
                                                  [box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 2"]
                                                  [gap :size "1"]
                                                  [box
                                                   :style {:background-color "lightgrey"
                                                           :padding          "20px"}
                                                   :child "Box 3"]]]
                                      [gap :size "10px"]
                                      [p "Notes:"
                                       [:ul
                                        [:li "This example has a gap with a " [:span.bold "proportional size"] " of \"1\", not an absolute size. Synonymous to 100%."]
                                        [:li "Because it \"grows\" to fill all available space, it \"pushes\" box2 and box3 as far apart as possible."]
                                        [:li "Imagine the boxes as buttons, to see how this might be useful."]]]
                                      ]]]]
              [gap :size "30px"]]])
