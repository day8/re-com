(ns re-demo.layout
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href]]
            [re-demo.utils :refer [panel-title component-title paragraphs]]))


(defn left-column
  []
  [v-box
   :children [[title :level :level2 :label "Components"]
              [paragraphs
               [:p "Re-com comes with a flexible set of layout components. In general, these
                components are not themselves visible."]
               [:p "Instead, their purpose is to arrange other components."]
               [title :level :level2 :label "Two Boxes"]
               [:p "The two key components are " [:span.bold "h-box"] " and " [:span.bold "v-box"] ", which lay out
               their children horizontally and vertically respectively."]
               [:p "They are mutually composable, and can be arbitrarily nested to create complex arrangements."]
               [:p "Here's example code showing them nesting..."]]
               [:pre
                {:style {:width "460px"}}
"[v-box
 :children [[box :child \"Header\"]
            [h-box
             :height \"100px\"
             :children [[box :size \"70px\" :child \"Nav\"]
                        [box :size \"100%\" :child \"Content\"]]
            [box :child \"Footer\"]]]"]
               [gap :size "15px"]
               [:p "the result looks like this..."]
               ;[gap :size "10px"]

               [v-box
                :gap      "1px"
                :children [[box :style {:background-color "lightgrey"} :child "Header"]
                           [h-box
                            :gap "1px"
                            :height "100px"
                            :children [[box :size "70px" :style {:background-color "lightgrey"} :child "Nav"]
                                       [box :size "100%" :style {:background-color "lightgrey"} :child "Content"]]]
                           [box :style {:background-color "lightgrey"} :child "Footer"]]]]])

(defn right-column
  []
  [v-box
   :children [[title :level :level2 :label "Flexbox"]
              [paragraphs
               [:p
                "Re-com's layout model is a thin layer over " [:span.bold "CSS Flexbox"] "."]
               [:p "While Re-com's components work in their own right, to understand them fully and use them
               powerfully, you " [:span.bold "will"] " need to have a strong understanding of Flexbox. Do these tutorials very soon:"]
               [:ul
                [:li
                 [hyperlink-href
                  :label "CSS-Tricks guide to flexbox"
                  :href "https://css-tricks.com/snippets/css/a-guide-to-flexbox"
                  :target "_blank"]]
                [:li
                 [hyperlink-href
                  :label "The Ultimate Flexbox Cheat Sheet"
                  :href "http://www.sketchingwithcss.com/samplechapter/cheatsheet.html"
                  :target "_blank"]]]

               [:p "Flexbox works via the interplay of " [:span.bold "container"] " (parent) styles and " [:span.bold "item"] " (child)
               styles. In any interesting layout, intermediate DOM elements play the role of both container and item."]
               [:p "But, if block-level elements (divs?) are inserted, they
               can break the flex style interplay up and down the DOM hierarchy, and cause a variety of layout issues."]
               [:p [:span.bold "As a result, we have found Flexbox use to be quite viral."] " Once you start using it, you
               have to use it everywhere - right up and down the DOM tree."]
               [:p "So, we recommend you go 100% all-in on using h-box and v-box. If you do, everything should \"just work\"."]
               [:p [:span.bold "Never use a naked [:div]"] " or you may spend alot of time playing layout whack-a-mole (unless
                you adroitly apply the correct flexbox styles to that :div, which is ardulous and error prone.)"]

               ;[gap :size "10px"]
               [title :level :level2 :label "Non-Flex Leaf"]
               [:p "When we first put bootstrap buttons into re-com, they were streched.
               It turned out their block display didn't play well with flex containers like h-box."]
               [:p "So we created the " [:span.bold "[box ...]"] "  component which allows you to wrap
               non-flex leaf widgets for use in flex containers."]
               ]]])


(defn size-table
  []
  (let [col1 "50px"
        col2 "130px"
        col3 "500px"]
    [v-box
     :children [[title :level :level2 :label ":size"]
                [paragraphs
                 [:p "v-box and h-box have a parameter called :size. This mirrors the flex sytle. In the Flexbox world, there are two parties in any layout - " [:span.bold "containers"] " and their child " [:span.bold "items"] "."]
                 [:p "While the tutorials walk you through the menagerie of flexbox related styles, we've found that one style more than any other is
                 critical to getting what you want: the " [:span.bold "flex"] " style of the children."]
                 [:p "When we're having a bad day with layouts, 90% of the time it is because we didn't get the flex style of a child right."]]
                [v-box
                 :class "rc-div-table"
                 :align-self :start
                 :children [[h-box
                             :class "rc-div-table-header"
                             :children [[label :width col1 :label "Value"]
                                        [label :width col2 :label "Meaning"]
                                        [label :width col3 :label "Description"]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "initial"]
                                        [label :width col2 :label "0 1 auto"]
                                        [:span {:style {:width col3}} "Use item's width/height for dimensions (or content dimensions if w/h not specifed). Never grow. Shrink (to min-size) if necessary.
                                                                       Good for creating boxes with fixed maximum size, but that can shrink to a fixed smaller size (min-width/height) if space becomes tight.
                                                                       NOTE: When using initial, you should also set a width/height value (depending on flex-direction) to specify it's default size
                                                                             and an optional min-width/height value to specify the size it can shrink to."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "auto"]
                                        [label :width col2 :label "1 1 auto"]
                                        [:span {:style {:width col3}} "Use item's width/height for dimensions. Grow if necessary. Shrink (to min-size) if necessary.
                                                                       Good for creating really flexible boxes that will gobble as much available space as they are allowed or shrink as much as they are forced to."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "none"]
                                        [label :width col2 :label "0 0 auto"]
                                        [:span {:style {:width col3}} "Use item's width/height for dimensions (or content dimensions if not specifed). Never grow. Never shrink.
                                                                      Good for creating rigid boxes that stick to their width/height if specified, otherwise their content size."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "100px"]
                                        [label :width col2 :label "0 0 100px"]
                                        [:span {:style {:width col3}} "Non flexible 100px size (in the flex direction) box.
                                                                      Good for fixed headers/footers and side bars of an exact size."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "60%"]
                                        [label :width col2 :label "60 1 0px"]
                                        [:span {:style {:width col3}} "Set the item's size (it's width/height depending on flex-direction) to be 60% of the parent container's width/height.
                                                                      NOTE: If you use this, then all siblings with percentage values must add up to 100%."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "60"]
                                        [label :width col2 :label "60 1 0px"]
                                        [:span {:style {:width col3}} "Same as percentage above."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "g s b"]
                                        [label :width col2 :label "grow shrink basis"]
                                        [:span {:style {:width col3}} "If none of the above common valaues above meet your needs, this gives you precise control."]]]]]]]))


(defn the-key-style
  []
  [v-box
   :children [[title :level :level2 :label "The Key Style"]
              [paragraphs
               [:p "In the Flexbox world, there are two parties in any layout - " [:span.bold "containers"] " and their child " [:span.bold "items"] "."]
               [:p "While the tutorials walk you through the menagerie of flexbox related styles, we've found that one style more than any other is
                 critical to getting what you want: the " [:span.bold "flex"] " style of the children."]
               [:p "When we're having a bad day with layouts, 90% of the time it is because we didn't get the flex style of a child right."]]
              [title :level :level2 :label "flex=??"]
              [paragraphs
               [:p "The tutorials tell you that the " [:span.bold "flex"] " style can be single values like " [:span.bold "none"] "  and " [:span.bold "auto"] ".
                 But realise that every single time, flex resolves to a triple of sub-values:"]
               [:ul
                [:li [:span [:span.bold "grow"]   " - Integer which determines how an item grows in size if there is extra container space to distribute. 0 for no growing."]]
                [:li [:span [:span.bold "shrink"] " - Integer which determines how an item shrinks in size if container space is reduced. 0 for no shrinking."]]
                [:li [:span [:span.bold "basis"]  " - The default size of an item before any necessary growing or shrinking. Can be
                  values like 60% or 100px. It can also be " [:span.bold "auto"] " which means to use the natural size of the item (or its further children)"]]]

               [:p "So even if you provide a single value like " [:span.bold "flex=\"none\""]  ",
                 what you are really providing is " [:span.bold "flex=\"0 0 auto\""] ". It always resolves to a triple. If you get this point, you are half way there."]

               [:p "Confusion reigns a bit because you can do this " [:span.bold "flex=\"auto\""]  ",
                 which resolves to " [:span.bold "flex=\"1 1 auto\""] ".  Eh?  My \"auto\" begates another \"auto\". Breeding like rabits."]
               [:p "Determines the value for the 'flex' attribute (which has grow, shrink and basis), based on the :size parameter."]
               [:p.bold "IMPORTANT: The term 'size' means width of the item in the case of flex-direction 'row' OR height of the item in the case of flex-direction 'column'."]
               [:p "Flex property explanation:"]
               ]
              [:p "Supported values:"]

              [gap :size "20px"]
              [paragraphs
               [:p "If number of words is not 1 or 3, an exception is thrown."]
               [:p "Reference: http://www.w3.org/TR/css3-flexbox/#flexibility"]
               [:p "Diagram:   http://www.w3.org/TR/css3-flexbox/#flex-container"]
                 [:p "Regex101 testing: ^(initial|auto|none)|(\\d+)(px|%|em)|(\\d+)\\w(\\d+)\\w(.*) - remove double backslashes"]]]])



(defn panel2
  []
  [v-box
   :gap      "10px"
   :children [[panel-title "Layout"]
              [h-box
               :gap      "100px"
               :children [[left-column]
                          [right-column]]]
              [line]
              [h-box
               :gap      "100px"
               :children [[the-key-style]
                          [size-table]]]
              ]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
