(ns re-demo.layout
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href]]
            [re-demo.utils :refer [panel-title component-title paragraphs]]))


(defn left-column
  []
  [v-box
   :size     "auto"
   :children [[title :level :level2 :label "Components"]

              [gap :size "5px"]
              [paragraphs
               [:p "Re-com comes with a flexible set of layout components. In general, these
                components are not themselves visible."]
               [:p "Instead, their purpose is to arrange other components."]
               [gap :size "10px"]
               [title :level :level2 :label "Two Boxes"]

               [gap :size "5px"]
               [:p "The two key components are h-box and v-box, which lay out their children horizontally and vertically respectively."]
               [:p "They are mutually composable, and can be arbitrarily nested to create complex arrangements."]
               [:p "Here's example code showing them nesting..."]
               [:pre
                {:style {:width "460px"}}
                "[v-box
     :children [[box :child \"Header\"]
                [h-box
                 :height \"75px\"
                 :children [[box :size \"70px\" :child \"Nav\"]
                            [box :size \"100%\" :child \"Content\"]]
                [box :child \"Footer\"]]]"]
               [gap :size "15px"]
               [:span "the result looks like this..."]
               [gap :size "10px"]
               [v-box
                :width    "460px"
                :gap      "1px"
                :children [[box :style {:background-color "lightgrey"} :child "Header"]
                           [h-box
                            :gap "1px"
                            :height "75px"
                            :children [[box :size "70px" :style {:background-color "lightgrey"} :child "Nav"]
                                       [box :size "100%" :style {:background-color "lightgrey"} :child "Content"]]]
                           [box :style {:background-color "lightgrey"} :child "Footer"]]]]]])

(defn right-column
  []
  [v-box
   :size     "auto"
   :children [[title :level :level2 :label "Flexbox"]
              [gap :size "5px"]

              [paragraphs
               [:p
                "Re-com's layout model is a thin layer over "
                [hyperlink-href
                 :label "CSS Flexbox"
                 :href "https://css-tricks.com/snippets/css/a-guide-to-flexbox"
                 :target "_blank"]
                "."]
               [:p "While Re-com's components work in their own right, to understand them fully and use them
               powerfully, you need to have a strong understanding of Flexbox. Do the tutorials soon."]
               [:p "Flexbox works via the interplay between container (parent) attributes and item (child)
               attributes. Intermediate DOM nodes almost always play the role of both parent and child."]
               [:p {:style {:font-weight "bold"}} "Inserting block-level elements (divs?) into this tree
               can break this interplay and can cause a variety of layout issues."]
               [:p "As a result, we have found Flexbox use to be quite viral. Once you start using it, you seem to
               have to use it everywhere. The use of non-dispay-flex elements causes problems.
               So, we recommend you go 100% all-in on the re-com h-box/v-box layout method.
               If you do, everything should \"just work\". But, if instead you mix block-level and flex, you
               may spend alot of time playing layout whack-a-mole."]
               [:p {:style {:font-weight "bold"}} "Just to be clear: when using re-com, don't create [:divs].
               Always use an h-box or a v-box, or one of the widget components, like label."]
               [gap :size "10px"]
               [title :level :level2 :label "Non-Flex Leaf"]

               [:p "When we first put bootstrap buttons into re-com, they were streched.
               It turned out their block display didn't play well with flex containers."]
               [:p "So we created the [bx] component which allows you to wrap
               non-flex leaf widgets for use in flex containers."]
               ]]])



(defn panel2
  []
  [v-box
   :children [[panel-title "Layout"]
              [gap :size "15px"]
              [h-box
               :children [[left-column]
                          [right-column]
                          #_[gap :size "1"]]]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
