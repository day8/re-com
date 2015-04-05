(ns re-demo.layout
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href p md-icon-button]]
            [re-demo.utils :refer [panel-title title2]]))


(defn flex-box
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
                                       ", and yet HTML5 only has a weak, half-arsed version?"]
                                      [p {:style {:width "250px"}} "Talk about regression."]]]]]
              [v-box
               :children [[title :level :level2 :label "Warning: Be All In"]
                          [p "Flexbox works via the interplay of styles present on a " [:span.bold "container"] " (parent) and its " [:span.bold "items"] " (children).
               Invariably, DOM nodes act as both a container for the level below, and an item for the level above."]
                          [p "If block-level elements (divs?) are present in this tree, they
               can break the flex style interplay up and down the DOM hierarchy, and cause problems."]
                          [p [:span.bold "As a result, we have found Flexbox use to be viral."] " Once you start using it, you
               end up using it everywhere - right up and down the DOM tree."]
                          [p "So, we recommend you go 100% all-in on using h-box and v-box. If you do, everything should \"just work\"."]
                          [p "Never mint your own container " [:span.bold "[:div]"] " or "  [:span.bold "[:span]"] " unless
               you also  give them the correct flex styles, which is arduous and error prone."]
               ]]]])


(defn the-key-style
  []
  [v-box
   :children [[title :level :level2 :label "The Key Style"]
              [p "Flexbox is about styles on " [:span.bold "containers"] " and their child " [:span.bold "items"] "."]
              [p "While tutorials will walk you through the menagerie of flexbox related styles,
               we've found that one, more than any other, is
               critical to getting what you want: the " [:span.bold "flex"] " style of the items."]
              [p "Worth repeating: if you are having trouble with a layout, pay particular attention to the
               " [:span.bold "flex"] " styles on the items within that layout."]
              [title :level :level2 :label "flex=GSB"]
              [p "Tutorials will tell you that the " [:span.bold "flex"] " style can be single value like " [:span.bold "none"] "  or " [:span.bold "auto"] ".
                 But those are shortcuts. Every flex style resolves to a triple of sub-values:"
               [:ul
                [:li [:span [:span.bold "grow"]   " - Integer which determines how an item grows in size (proportionally to its siblngs) if there is
                extra container space to distribute. 0 for no growing."]]
                [:li [:span [:span.bold "shrink"] " - Integer which determines how an item shrinks in size (proportionally to its siblngs) if container
                space is reduced. 0 for no shrinking."]]
                [:li [:span [:span.bold "basis"]  " - The default size of an item before any necessary growing or
                shrinking. Can be:"
                      [:ul
                       [:li "absolute length values like \"100px\" or \"40em\""  ]
                       [:li [:span.bold "auto"] " which means use the natural size of the item"  ]
                       [:li "proporational values like \"60\""]]]]]]

              [p "Shortcut values are always transformed into a triple. For example:"
               [:ul
                [:li  [:span.bold "flex=\"none\""] " is eqivalent to  "  [:span.bold "flex=\"0 0 auto\""] ]
                [:li  [:span.bold "flex=\"auto\""] " is eqivalent to  "  [:span.bold "flex=\"1 1 auto\""] ]
                ]]
              [p "Sure, use the shortcuts. But it is only by understanding triples that you become a power user of flexbox (or re-com layouts)."]
              [gap :size "10px"]]])

(defn size-table
  []
  (let [col1 "80px"
        col2 "130px"
        col3 "500px"]
    [v-box
     :children [[title :level :level2 :label ":size is flex"]
                [p "Both " [:span.bold "v-box"] " and " [:span.bold "h-box"] " take a " [:span.bold ":size"] " parameter which "
                 "exactly matches the  " [:span.bold "flex"] " style talked about to the left."]
                [gap :size "10px"]
                [:pre
                 {:style {:width "460px"}}
                 "[v-box
  :size \"auto\"     ;; <-- equivalent of \"flex\" style
  :children [...]]"]
                [gap :size "10px"]
                [p "This table of useful shortcuts might help."]
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
                                        [:span {:style {:width col3}} "Item is given a fixed length of 100px (in the flex direction).
                                                                      Good for headers/footers of fixed size, or LHS nav columns."]]]

                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "60"]
                                        [label :width col2 :label "60 1 0px"]
                                        [:span {:style {:width col3}} "Set the item's default length to be 60 proportional units.
                                        Allow it to streach. And it can shrink to nothing."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "g s b"]
                                        [label :width col2 :label "grow shrink basis"]
                                        [:span {:style {:width col3}} "If none of the shortcut values above meet your needs,
                                        you can always provide the triple yourself, to gain precise control. For example, the following item ..."]]]
                            [h-box
                             :class "rc-div-table-row"
                             :children [[label :width col1 :label "1 0 auto"]
                                        [label :width col2 :label "1 0 auto"]
                                        [:span {:style {:width col3}} "In this very app, the light grey part of the LHS nav has this " [:span.bold ":size"] ". The light
                                        grey background colour must always strech to the bottom of the page, hence
                                        the streach.  The basis comes from its child nav items, hence the auto.
                                        But it can't go smaller than its children, hence shrink of 0."]]]
                            ]]]]))




(defn example
  []
  [v-box
   :children [[title :level :level2 :label "Example"]
              [h-box
               ;:gap "40px"
               :children [
                          [v-box
                           :children [
                                      [p "Some code:"]
                                      [:pre
                                       {:style {:width "460px"}}
                                       "[v-box
  :children [[box :child \"Header\"]
             [h-box
              :height \"100px\"
              :children [[box :size \"70px\" :child \"Nav\"]
                         [box :size \"100\" :child \"Content\"]]
             [box :child \"Footer\"]]]"]]]
                          [box
                           :size "100px"
                           :align-self  :center
                           :justify :center
                           :child  [:div {:class "md-forward rc-icon-larger"
                                          :style {:color "lightgrey"}}]]
                          [v-box
                           :children [[p "will result in:"]
                                      [v-box
                                       :gap      "1px"
                                       :children [[box :style {:background-color "lightgrey"} :child "Header"]
                                                  [h-box
                                                   :gap "1px"
                                                   :height "100px"
                                                   :children [[box :size "70px" :style {:background-color "lightgrey"} :child "Nav"]
                                                              [box :size "100" :style {:background-color "lightgrey"} :child "Content"]]]
                                                  [box :style {:background-color "lightgrey"} :child "Footer"]]]
                                      [gap :size "15px"]]]]]]])

(defn components
  []
  [v-box
   :children [[title :level :level2 :label "The Components"]
              [p "Re-com has layout components which are not themselves visible -
              they just arrange other components."]
              [p "The two key components are " [:span.bold "h-box"] " and " [:span.bold "v-box"] " which arange
               their children horizontally and vertically respectively. Because they are
               mutually nestable, they combine to create arbitrarily complex layouts."]]])
(defn panel2
  []
  [v-box
   :gap "10px"
   :children [[panel-title "Layout"]
              [components]
              [example]
              [line]
              [flex-box]
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
