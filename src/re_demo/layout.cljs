(ns re-demo.layout
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href]]
            [re-demo.utils :refer [panel-title component-title paragraphs]]))


(defn top-left-column
  []
  [v-box
   :children [[title :level :level2 :label "Components"]
              [paragraphs
               [:p "Re-com comes with a flexible set of layout components. These
                components are not themselves visible - their job is to arrange other components."]
               ;[title :level :level2 :label "Two Boxes"]
               [:p "The key components are " [:span.bold "h-box"] " and " [:span.bold "v-box"] ", which arrange
               their children horizontally and vertically respectively."]
               [:p "They are mutually nestable, and can be used to create complex layouts."]
               [:p "Example code:"]]
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
               [:p "results in:"]

               [v-box
                :gap      "1px"
                :children [[box :style {:background-color "lightgrey"} :child "Header"]
                           [h-box
                            :gap "1px"
                            :height "100px"
                            :children [[box :size "70px" :style {:background-color "lightgrey"} :child "Nav"]
                                       [box :size "100%" :style {:background-color "lightgrey"} :child "Content"]]]
                           [box :style {:background-color "lightgrey"} :child "Footer"]]]
              [gap :size "15px"]
              [:p "Other pages will show you more of h-box and v-box, but first ..."]
              ]])

(defn top-right-column
  []
  [v-box
   :children [[title :level :level2 :label "Flexbox"]
              [paragraphs
               [:p
                "Re-com's layout model is a thin layer over " [:span.bold "CSS Flexbox"] "."]
               [:p "To fully understand Re-com's layout components and use them
               powerfully, you " [:span.bold "will"] " need to have a strong understanding of Flexbox. You should do these tutorials very soon:"]
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

               [title :level :level2 :label "Be All In"]
               [:p "Flexbox works via the interplay of styles present on a " [:span.bold "container"] " (parent) and its " [:span.bold "items"] " (children).
               In any interesting layout, many DOM elements sit in a tree, acting as both a container for the level below, and an item for the level above."]
               [:p "If block-level elements (divs?) are present in this tree, they
               can break the flex style interplay up and down the DOM hierarchy, and cause a variety of layout issues."]
               [:p [:span.bold "As a result, we have found Flexbox use to be quite viral."] " Once you start using it, you
               end up using it everywhere - right up and down the DOM tree."]
               [:p "So, we recommend you go 100% all-in on using h-box and v-box. If you do, everything should \"just work\"."]
               [:p "So, never mint your own container " [:span.bold "[:div]"] " or "  [:span.bold "[:span]"] " unless
               you also  give them the correct flex styles, which is arduous and error prone."]

               ]]])


(defn size-table
  []
  (let [col1 "75px"
        col2 "130px"
        col3 "500px"]
    [v-box
     :children [[title :level :level2 :label "The :size Parameter (aka flex)"]
                [paragraphs
                 [:p "Both " [:span.bold "v-box"] " and " [:span.bold "h-box"] " have a parameter called " [:span.bold ":size"] ".
                 This parameter is the equivalent of the style " [:span.bold "flex"] " talked about to the left."]]
                [gap :size "10px"]
                [:p "Possible values:"]
                [v-box
                 :class "rc-div-table"
                 :align-self :start
                 :children [[h-box
                             :class "rc-div-table-header"
                             :children [[label :width col1 :label ":size"]
                                        [label :width col2 :label "G S B"]
                                        [label :width col3 :label "Description"]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "initial"]
                                        [label :width col2 :label "0 1 auto"]
                                        [:span {:style {:width col3}} "Use the item's length. Never grow. Shrink if necessary.
                                                                       Good for creating items with a natural maximum size, which can
                                                                       shrink to some smaller size, typically given by min-width/height, if space becomes tight. "]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "auto"]
                                        [label :width col2 :label "1 1 auto"]
                                        [:span {:style {:width col3}} "Use the item's length. Grow if necessary. Shrink (to min-size) if necessary.
                                                                       Good for creating items that happily take as much
                                                                       space as they are allowed, or can shrink as much as they are forced to.
                                                                       If necessary, use min-width/height to provide limits."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "none"]
                                        [label :width col2 :label "0 0 auto"]
                                        [:span {:style {:width col3}} "Use the item's length. Never grow. Never shrink.
                                                                      Good for creating rigid items that stick to their width/height if specified, otherwise their content size."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "100px"]
                                        [label :width col2 :label "0 0 100px"]
                                        [:span {:style {:width col3}} "Item is given a fixed 100px (in the flex direction).
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
                                        [:span {:style {:width col3}} "If none of the common values above meet your needs, this gives you precise control."]]]]]]]))


(defn the-key-style
  []
  [v-box
   :children [[title :level :level2 :label "The Key Style"]
              [paragraphs
               [:p "Flexbox is about styles on " [:span.bold "containers"] " and their child " [:span.bold "items"] "."]
               [:p "While tutorials will walk you through the menagerie of flexbox related styles,
               we've found that one, more than any other, is
               critical to getting what you want: the " [:span.bold "flex"] " style of the items."]
               [:p "Worth repeating: if you are having trouble with a layout, pay particular attention to the
               " [:span.bold "flex"] " style you have given to the items of that layout."]]
              [title :level :level2 :label "flex=GSB"]
              [paragraphs
               [:p "Tutorials might tell you that the " [:span.bold "flex"] " style can be single value like " [:span.bold "none"] "  or " [:span.bold "auto"] ".
                 But those are shortcuts. Every flex style resolves to a triple of sub-values:"]
               [:ul
                [:li [:span [:span.bold "grow"]   " - Integer which determines how an item grows in size (proportionally to its siblngs) if there is
                extra container space to distribute. 0 for no growing."]]
                [:li [:span [:span.bold "shrink"] " - Integer which determines how an item shrinks in size (proportionally to its siblngs) if container
                space is reduced. 0 for no shrinking."]]
                [:li [:span [:span.bold "basis"]  " - The default size of an item before any necessary growing or
                shrinking. Can be
                  values like 60% or 100px. It can also be " [:span.bold "auto"] " which means to use the natural size
                  of the item (or its further children)"]]]

               [:p "When you provide a shortcut value like " [:span.bold "flex=\"none\""]  ",
                 what you are really providing is " [:span.bold "flex=\"0 0 auto\""] "."]

               [:p "And, " [:span.bold "flex=\"auto\""]  ",
                 resolves to " [:span.bold "flex=\"1 1 auto\""] ", which includes \"auto\" for the \"basis\"."]
               [:p "In re-com, v-box and h-box components will be items (children of a higher v-box or h-box container).
               When organising a layout, always pay attention to " [:span.bold "flex"] " style of these children."]]
              [gap :size "10px"]]])

(defn panel2
  []
  [v-box
   :style   {:font-size "15px"}
   :children [[panel-title "Layout"]
              [gap :size "10px"]
              [h-box
               :gap      "100px"
               :children [[top-left-column]
                          [top-right-column]]]
              [line]
              [gap :size "15px"]
              [h-box
               :gap      "100px"
               :children [[the-key-style]
                          [size-table]]]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
