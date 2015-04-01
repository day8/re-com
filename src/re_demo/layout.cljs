(ns re-demo.layout
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href]]
            [re-demo.utils :refer [panel-title component-title paragraphs]]))


(defn left-column
  []
  [v-box
   :size     "none"
   :children [[title :level :level2 :label "Components"]
              [paragraphs
               [:p "Re-com comes with a flexible set of layout components. In general, these
                components are not themselves visible."]
               [:p "Instead, their purpose is to arrange other components."]
               [title :level :level2 :label "Two Boxes"]
               [:p "The two key components are h-box and v-box, which lay out their children horizontally and vertically respectively."]
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
   :size     "none"
   :children [[title :level :level2 :label "Flexbox"]
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
               [:p.bold "Inserting block-level elements (divs?) into this tree
               can break this interplay and can cause a variety of layout issues."]
               [:p "As a result, we have found Flexbox use to be quite viral. Once you start using it, you seem to
               have to use it everywhere. The use of non-dispay-flex elements causes problems.
               So, we recommend you go 100% all-in on the re-com h-box/v-box layout method.
               If you do, everything should \"just work\". But, if instead you mix block-level and flex, you
               may spend alot of time playing layout whack-a-mole."]
               [:p.bold "Just to be clear: when using re-com, don't create [:divs].
               Always use an h-box or a v-box, or one of the widget components, like label."]
               ;[gap :size "10px"]
               [title :level :level2 :label "Non-Flex Leaf"]
               [:p "When we first put bootstrap buttons into re-com, they were streched.
               It turned out their block display didn't play well with flex containers."]
               [:p "So we created the box component which allows you to wrap
               non-flex leaf widgets for use in flex containers."]
               ]]])

(defn flex-property
  []
  [:pre "Determines the value for the 'flex' attribute (which has grow, shrink and basis), based on the :size parameter.
   IMPORTANT: The term 'size' means width of the item in the case of flex-direction 'row' OR height of the item in the case of flex-direction 'column'.
   Flex property explanation:
    - grow    Integer ratio (used with other siblings) to determined how a flex item grows it's size if there is extra space to distribute. 0 for no growing.
    - shrink  Integer ratio (used with other siblings) to determined how a flex item shrinks it's size if space needs to be removed. 0 for no shrinking.
    - basis   Initial size (width, actually) of item before any growing or shrinking. Can be any size value, e.g. 60%, 100px, auto
              Note: auto will cause the initial size to be calculated to take up as much space as possible, in conjunction with it's siblings :flex settings.
   Supported values:
    - initial            '0 1 auto'  - Use item's width/height for dimensions (or content dimensions if w/h not specifed). Never grow. Shrink (to min-size) if necessary.
                                       Good for creating boxes with fixed maximum size, but that can shrink to a fixed smaller size (min-width/height) if space becomes tight.
                                       NOTE: When using initial, you should also set a width/height value (depending on flex-direction) to specify it's default size
                                             and an optional min-width/height value to specify the size it can shrink to.
    - auto               '1 1 auto'  - Use item's width/height for dimensions. Grow if necessary. Shrink (to min-size) if necessary.
                                       Good for creating really flexible boxes that will gobble as much available space as they are allowed or shrink as much as they are forced to.
    - none               '0 0 auto'  - Use item's width/height for dimensions (or content dimensions if not specifed). Never grow. Never shrink.
                                       Good for creating rigid boxes that stick to their width/height if specified, otherwise their content size.
    - 100px              '0 0 100px' - Non flexible 100px size (in the flex direction) box.
                                       Good for fixed headers/footers and side bars of an exact size.
    - 60%                '60 1 0px'  - Set the item's size (it's width/height depending on flex-direction) to be 60% of the parent container's width/height.
                                       NOTE: If you use this, then all siblings with percentage values must add up to 100%.
    - 60                 '60 1 0px'  - Same as percentage above.
    - grow shrink basis  'grow shrink basis' - If none of the above common valaues above meet your needs, this gives you precise control.
   If number of words is not 1 or 3, an exception is thrown.
   Reference: http://www.w3.org/TR/css3-flexbox/#flexibility
   Diagram:   http://www.w3.org/TR/css3-flexbox/#flex-container
   Regex101 testing: ^(initial|auto|none)|(\\d+)(px|%|em)|(\\d+)\\w(\\d+)\\w(.*) - remove double backslashes"]

  #_[v-box
   :children [[title :level :level2 :label "The :size Parameter"]
              [v-box
               :class    "rc-div-table"
               :style    {:font-size "24px"}
               :children [^{:key "0"}
              [h-box
               :class    "rc-div-table-header"
               :children [[label :label "Sort"    :width (:sort    col-widths)]
                          [label :label "Name"    :width (:name    col-widths)]
                          [label :label "From"    :width (:from    col-widths)]
                          [label :label "To"      :width (:to      col-widths)]
                          [label :label "Actions" :width (:actions col-widths)]]]
                          (for [[_ row first? last?] (enumerate (sort-by :sort (vals rows)))]
                            ^{:key (:id row)} [data-row row first? last? col-widths mouse-over click-msg])]]
              [paragraphs
               [:p "Determines the value for the 'flex' attribute (which has grow, shrink and basis), based on the :size parameter."]
               [:p.bold "IMPORTANT: The term 'size' means width of the item in the case of flex-direction 'row' OR height of the item in the case of flex-direction 'column'."]
               [:p "While Re-com's components work in their own right, to understand them fully and use them
               powerfully, you need to have a strong understanding of Flexbox. Do the tutorials soon."]
               [:p "Flexbox works via the interplay between container (parent) attributes and item (child)
               attributes. Intermediate DOM nodes almost always play the role of both parent and child."]
               [:p.bold "Inserting block-level elements (divs?) into this tree
               can break this interplay and can cause a variety of layout issues."]
               [:p "As a result, we have found Flexbox use to be quite viral. Once you start using it, you seem to
               have to use it everywhere. The use of non-dispay-flex elements causes problems.
               So, we recommend you go 100% all-in on the re-com h-box/v-box layout method.
               If you do, everything should \"just work\". But, if instead you mix block-level and flex, you
               may spend alot of time playing layout whack-a-mole."]
               [:p.bold "Just to be clear: when using re-com, don't create [:divs].
               Always use an h-box or a v-box, or one of the widget components, like label."]
               ;[gap :size "10px"]
               [title :level :level2 :label "Non-Flex Leaf"]
               [:p "When we first put bootstrap buttons into re-com, they were streched.
               It turned out their block display didn't play well with flex containers."]
               [:p "So we created the box component which allows you to wrap
               non-flex leaf widgets for use in flex containers."]
               ]
              ]])



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
              [flex-property]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
