(ns re-demo.layout
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href p md-icon-button]]
            [re-demo.utils :refer [panel-title title2]]))



(defn components-section
  []
  [v-box
   :children [[title :level :level2 :label "The Components"]
              [p "Re-com has layout components which are not themselves visible -
                  they just arrange other components."]
              [p "The key components are " [:span.bold "h-box"] " and " [:span.bold "v-box"] " which arange
                  their children horizontally and vertically respectively. Because they are
                  mutually nestable, you can combine them to create arbitrarily complex layouts."]]])


(defn example-layout
  []
  [v-box
   :children [
              [h-box
               :children [
                          [v-box
                           :children [
                                      [p "And this example code, showing an " [:span.bold "h-box"] " as a child of a " [:span.bold "v-box"] " ..."]
                                      [:pre
                                       {:style {:width "460px"}}
"[v-box
  :children [[box :child \"Header\"]
             [h-box
              :height \"100px\"
              :children [[box :size \"70px\" :child \"Nav\"]
                         [box :size \"1\" :child \"Content\"]]
             [box :child \"Footer\"]]]"]]]
                          [box
                           :size "100px"
                           :align-self  :center
                           :justify :center
                           :child  [:div {:class "md-forward rc-icon-larger"
                                          :style {:color "lightgrey"}}]]
                          [v-box
                           :children [[p "... results in this kind of structure:"]
                                      [v-box
                                       :gap      "1px"
                                       :children [[box :style {:background-color "lightgrey"} :child "Header"]
                                                  [h-box
                                                   :gap "1px"
                                                   :height "100px"
                                                   :children [[box :size "70px" :style {:background-color "lightgrey"} :child "Nav"]
                                                              [box :size "1" :style {:background-color "lightgrey"} :child "Content"]]]
                                                  [box :style {:background-color "lightgrey"} :child "Footer"]]]
                                      [gap :size "15px"]]]]]]])

(defn flex-box-section
  []
  [v-box
   :gap "10px"
   :children [[title :level :level2 :label "Flexbox"]
              [h-box
               :gap "100px"
               :children [[v-box
                           :children [
                                      [p "Re-com's layout model is a thin layer over " [:span.bold "CSS Flexbox"] "."]
                                      [p "To fully understand Re-com's layout components and use them
                                          powerfully, you " [:span.bold "will"] " need to have a strong understanding of
                                          Flexbox. You should do these tutorials very soon:"
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
                                          :target "_blank"]]]]]]

                          [v-box
                           :style {:font-size "13px" :color "#aaa"}
                           :children [[p {:style {:width "250px"}} "While, flexbox may be waaaaaay better than the HTML5 alternatives ..."]
                                      [p {:style {:width "250px"}} "How crazy that Knuth pioneered this box/glue stuff "
                                       [hyperlink-href
                                        :label "35 years ago"
                                        :href "http://i.stanford.edu/pub/cstr/reports/csl/tr/88/358/CSL-TR-88-358.pdf"
                                        :target "_blank"]
                                       ", and GUI toolkits had it "
                                       [hyperlink-href
                                        :label "25 years ago"
                                        :href "http://i.stanford.edu/pub/cstr/reports/csl/tr/88/358/CSL-TR-88-358.pdf"
                                        :target "_blank"]
                                       ", and yet HTML5 only has a weak, half-arsed version?"]]]]]
              [v-box
               :children [[title :level :level2 :label "Warning: Be All In"]
                          [p "Flexbox works via the interplay of styles present on a " [:span.bold "container"] " (parent) and its " [:span.bold "items"]
                             " (children). Invariably, DOM nodes act as both a container for the level below, and an item for the level above."]
                          [p "If block-level elements (divs?) are present in this tree, they
                              can break the flex style interplay up and down the DOM hierarchy, and cause problems."]
                          [p [:span.bold "As a result, we have found Flexbox use to be viral."] " Once you start using it, you
                             end up using it everywhere - right up and down the DOM tree."]
                          [p "So, we recommend you go 100% all-in on using h-box and v-box. If you do, everything should \"just work\"."]
                          [p "Never mint your own container " [:span.bold "[:div]"] " or "  [:span.bold "[:span]"] " unless
                              you also  give them the correct flex styles, which is arduous and error prone."]]]]])


(defn key-style-section
  []
  [v-box
   :children [[title :level :level2 :label "The Key Style"]
              [p "Flexbox is about styles on " [:span.bold "containers"] " and their child " [:span.bold "items"] "."]
              [p "While tutorials will walk you through the menagerie of flexbox related styles,
                  we've found that one, more than any other, is critical to getting what you want: the " [:span.bold "flex"] " style of the items."]
              [p "Worth repeating: if you are having trouble with a layout, pay particular attention to the
               " [:span.bold "flex"] " styles on the items within that layout."]
              [title :level :level2 :label "flex=GSB"]
              [p "Tutorials will tell you that the " [:span.bold "flex"] " style can be single value like " [:span.bold "none"] "  or " [:span.bold "auto"]
                 ". But those are shortcuts. Every flex style resolves to a triple of sub-values:"
               [:ul
                [:li [:span [:span.bold "grow"]   " - Integer which determines how an item grows in size (in proportion to its siblings) if there is
                                                   extra container space to distribute. 0 for no growing."]]
                [:li [:span [:span.bold "shrink"] " - Integer which determines how an item shrinks in size (in proportion to its siblings) if container
                                                   space is reduced. 0 for no shrinking."]]
                [:li [:span [:span.bold "basis"]  " - The default size of an item before any necessary growing or shrinking. Can be:"
                      [:ul
                       [:li "absolute length values like \"100px\" or \"40em\""]
                       [:li [:span.bold "auto"] " which means use the natural size of the item"]
                       [:li "proporational values like \"60\""]]]]]]

              [p "Shortcut values are always transformed into a triple. For example:"
               [:ul
                [:li  [:span.bold "flex=\"none\""] " is eqivalent to  " [:span.bold "flex=\"0 0 auto\""]]
                [:li  [:span.bold "flex=\"auto\""] " is eqivalent to  " [:span.bold "flex=\"1 1 auto\""]]]]
              [p "Sure, use the shortcuts. But it is only by understanding triples that you become a power user of flexbox (or re-com layouts)."]
              [gap :size "10px"]]])


(defn table-row
  [size gsb description header?]
  (let [col1 "80px"
        col2 "130px"
        col3 "500px"
        col3-style {:style {:width col3}}]
    [h-box
     :class (if header? "rc-div-table-header" "rc-div-table-row")
     :children [[label :width col1 :label size]
                [label :width col2 :label gsb]
                (if header?
                  [label :width col3 :label description]
                  [:span col3-style description])]]))

(defn size-table
  []
  [v-box
   :children [[title :level :level2 :label ":size is flex"]
              [p "Various layout components, such as " [:span.bold "v-box"] " and " [:span.bold "h-box"] " and " [:span.bold "gap"] " take a " [:span.bold ":size"] " parameter which "
               "exactly matches the  " [:span.bold "flex"] " style talked about to the left."]
              [gap :size "10px"]
              [:pre
               {:style {:width "460px"}}
"[v-box
:size \"auto\"     ;; <-- equivalent of \"flex\" style
:children [...]]"]
              [p "Look again at the Simple Example up the top of this page, and you'll see  "
               [:span.bold ":size"] " being used. Values like \"70px\" and \"1\" are provided."]
              [gap :size "10px"]
              [p "This table of useful shortcuts might help."]
              [v-box
               :class "rc-div-table"
               :align-self :start
               :children [[table-row
                           ":size"
                           "G S B"
                           "Description"
                           true]
                          [table-row
                           "initial"
                           "0 1 auto"
                           "Use the item's length. Never grow. Shrink if necessary.
                           Good for creating items with a natural maximum size, which can
                           shrink to some smaller size, typically given by min-width/height, if space becomes tight. "]
                          [table-row
                           "auto"
                           "1 1 auto"
                           "Use the item's length. Grow if necessary. Shrink (to min-size) if necessary.
                           Good for creating items that happily take as much
                           space as they are allowed, or can shrink as much as they are forced to.
                           If necessary, use min-width/height to provide limits."]
                          [table-row
                           "none"
                           "0 0 auto"
                           "Use the item's length. Never grow. Never shrink.
                           Good for creating rigid items that stick to their width/height if specified, otherwise their content size."]
                          [table-row
                           "100px"
                           "0 0 100px"
                           "Item is given a fixed length of 100px (in the flex direction).
                                                         Good for headers/footers of fixed size, or LHS nav columns."]

                          [table-row
                           "60"
                           "60 1 0px"
                           [:span "Set the item's default length to be 60 sibling-proportional units.
                                    Allow it to stretch. And it can shrink to nothing."
                            [:br]
                            "Look back at the \"Sample Example\" up the
                             top.  Notice that the \"content\" part has a :size of \"1\". Because the other child had a fixed
                             size of 70px, the Content stretches to fill all available space. No other sibling is making claims for space, so \"1\" might as well be \"100%\"."]]
                          [table-row
                           "g s b"
                           "grow shrink basis"
                           "If none of the shortcut values above meet your needs,
                           you can always provide the triple yourself, to gain precise control. For example, the following item ..."]
                          [table-row
                           "1 0 auto"
                           "1 0 auto"
                           [:span "In this very app, the light grey part of the LHS nav has this " [:span.bold ":size"] ". The light
                                   grey background colour must always strech to the bottom of the page, hence
                                   the stretch.  The basis comes from its child nav items, hence the auto.
                                   But it can't go smaller than its children, hence shrink of 0."]]]]]])




(defn panel2
  []
  [v-box
   :gap "10px"
   :children [[panel-title "Layout"]
              [components-section]
              [example-layout]
              [gap :size "10px"] [line]
              [flex-box-section]
              [gap :size "10px"] [line]
              [h-box
               :gap      "100px"
               :children [[key-style-section]
                          [size-table]]]
              [gap :size "20px"]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
