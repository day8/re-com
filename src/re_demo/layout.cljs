(ns re-demo.layout
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href]]
            [re-demo.utils :refer [panel-title component-title paragraphs]]))


(defn panel2
  []
  [v-box
   :size     "auto"
   :children [[panel-title "Layout"]
              [gap :size "15px"]
              [paragraphs
               [:p
                "Re-com comes with a flexible and powerful set of layout components. In general, these components are not themselves visible. Their purpose is to arrange other components."]
               [gap :size "10px"]
               [title :level :level2 :label "The Boxes"]
               [:p "The two key components are h-box and v-box, which lay out their children horizontally and vertically respectively."]
               [:p "They are mutually composable, and can be arbitrarily nested to create complex arrangements."]
               [:p "Here's a simple example of nesting..."]
               [:pre
                {:style {:width "40em"}}
"[v-box
 :children [[box :child \"Header\"]
            [h-box
             :height \"50px\"
             :children [[box :size \"70px\" :child \"Nav\"]
                        [box :size \"100%\" :child \"Content\"]]
            [box :child \"Footer\"]]]"]
               [gap :size "10px"]
               [:p "And the output would look like this..."]
               [gap :size "10px"]
               [v-box
                :gap "1px"
                :children [[box :style {:background-color "lightgrey"} :child "Header"]
                           [h-box
                            :gap "1px"
                            :height "50px"
                            :children [[box :size "70px" :style {:background-color "lightgrey"} :child "Nav"]
                                       [box :size "100%" :style {:background-color "lightgrey"} :child "Content"]]]
                           [box :style {:background-color "lightgrey"} :child "Footer"]]]
               [gap :size "20px"]
               [title :level :level2 :label "Flexbox"]
               [gap :size "5px"]
               [:p
                "Re-com's layout model is a thin layer over "
                [hyperlink-href
                 :label "CSS Flexbox"
                 :href "https://css-tricks.com/snippets/css/a-guide-to-flexbox"
                 :target "_blank"]
                "."]
               [:p "While Re-com's components stand alone, to understand them fully and use them powerfully, it will certainly help to have a good understanding of Flexbox."]
               [:p "Flexbox works via the interplay between container (parent) and item (child) attributes. Intermediate DOM nodes almost always play the role of both parent and child."]
               [:p {:style {:font-weight "bold"}} "Inserting block-level elements (divs?) into this tree can break this interplay and can cause a variety of layout issues."]
               [:p "As a result, we have found Flexbox use to be quite viral. We recommend you go 100% all-in on the re-com h-box/v-box layout method. If you do, it should \"just work\". Otherwise, if you mix block-level and flexbox, you may spend time playing layout whack-a-mole."]
               [gap :size "10px"]
               [title :level :level2 :label "No \"display: block\""]
               [:p ""]
               [:p "The box component is useful for wrapping leaf nodes in the DOM tree."]
               [:p "Here is some sample code of two buttons in an h-box. The h-box height is set to 50px and notice how the buttons fill the given height...blah blah mention :align :stretch etc."]
               [gap :size "20px"]
               [:pre
                {:style {:width "40em"}}
"[h-box
 :gap      \"10px\"
 :height   \"50px\"
 :children [[:button \"Hello\"]
            [:button \"Good bye\"]]]"]
               [gap :size "10px"]
               [h-box
                :gap      "10px"
                :size     "50px"
                :style    {:border "1px dashed red"}
                :children [[:button "Hello"]
                           [:button "Good bye"]]]
               [gap :size "40px"]
               [:p "Now, if we wrap the buttons in box components, the problem is solved. This is what we do with our actual re-com buttons to avoid this problem."]

               [:pre
                {:style {:width "40em"}}
"[h-box
 :gap      \"10px\"
 :height   \"50px\"
 :children [[box
             :align :start
             :child [:button \"Hello\"]]
            [box
             :align :start
             :child [:button \"Good bye\"]]]]"]
               [gap :size "10px"]
               [h-box
                :gap      "10px"
                :size     "50px"
                :style    {:border "1px dashed red"}
                :children [[box
                            :align :start
                            :child [:button "Hello"]]
                           [box
                            :align :start
                            :child [:button "Good bye"]]]]
               [gap :size "60px"]
               ]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
