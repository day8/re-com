(ns re-demo.info-button
  (:require [re-com.core    :refer [h-box v-box box gap line info-button label input-text hyperlink-href p]]
            [re-com.buttons :refer [info-button-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util    :refer [px]]))


(defn info-button-component-hierarchy
  []
  (let [indent          20
        table-style     {:style {:border "2px solid lightgrey" :margin-right "10px"}}
        border          {:border "1px solid lightgrey" :padding "6px 12px"}
        border-style    {:style border}
        border-style-nw {:style (merge border {:white-space "nowrap"})}
        valign          {:vertical-align "top"}
        valign-style    {:style valign}
        valign-style-hd {:style (merge valign {:background-color "#e8e8e8"})}
        indent-text     (fn [level text] [:span {:style {:padding-left (px (* level indent))}} text])
        highlight-text  (fn [text & [color]] [:span {:style {:font-weight "bold" :color (or color "dodgerblue")}} text])
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[info-button\n"
                      "   ...\n"
                      "   :parts {:tooltip {:class \"blah\"\n"
                      "                     :style { ... }\n"
                      "                     :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[info-button]")]
                   [:td border-style-nw "rc-info-button-popover-anchor-wrapper"]
                   [:td border-style-nw (code-text ":tooltip")]
                   [:td border-style "Outer wrapper of the button, everything."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-info-button"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The actual button."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:svg]")]
                   [:td border-style-nw "rc-info-button-icon"]
                   [:td border-style-nw (code-text ":icon")]
                   [:td border-style "The button icon."]]]]]]))

(defn info-button-demo
  []
  (let [info [v-box
              :children [[:p.info-heading "Info Popup Heading"]
                         [:p "You can use the " [:span.info-bold "info-bold"] " class to make text bold."]
                         [:p "Use the " [:span.info-bold "code"] " element to display source code:"]
                         [:code
                          "(defn square [n] (* n n))" [:br]
                          "=> #'user/square" [:br]
                          "(square 45)" [:br]
                          "=> 2025" [:br]]
                         [:p.info-subheading "Sub heading"]
                         [:p "Note: Styles copied from"]
                         [hyperlink-href
                          :label  "ClojureScript Cheatsheet"
                          :href   "http://cljs.info/cheatsheet"
                          :target "_blank"]]]]
    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[info-button ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/info_button.cljs"]

                  [h-box
                   :gap "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A tiny information button, which is light grey and unobrusive. When clicked, displays a popup containing helpful information. "]
                                          [p "Designed to be used with input fields, to explain the purpose of the field."]
                                          [p "The popup has a dark theme, and uses white text. CSS classes for the text are available as follows:to format the text in the popover"]
                                          [:ul
                                           [:li [:span.semibold "info-heading"] " - heading/title style"]
                                           [:li [:span.semibold"info-subheading"] " - subheading style"]
                                           [:li [:span.semibold "info-bold"] " - bold style"]]
                                          [args-table info-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [p "Notice the small round information icon above each input box. On hover, the icon become blue. On click, a popoover appears with artbitrary explanation."]
                                          [gap :size "5px"]
                                          [v-box
                                           :children [[h-box
                                                       :gap      "4px"
                                                       :children [[:span.field-label "client"]
                                                                  [info-button
                                                                   :info info]]]
                                                      [input-text
                                                       :model       ""
                                                       :placeholder "Example input #1"
                                                       :on-change   #()]
                                                      [gap :size "30px"]
                                                      [h-box
                                                       :gap      "4px"
                                                       :children [[:span.field-label "product"]
                                                                  [info-button
                                                                   :position :right-center
                                                                   :width    "370px"
                                                                   :info     info]]]
                                                      [input-text
                                                       :model       ""
                                                       :placeholder "Example input #2"
                                                       :on-change   #()]
                                                      [gap :size "30px"]
                                                      [h-box
                                                       :gap      "4px"
                                                       :children [[:span.field-label "disabled"]
                                                                  [info-button
                                                                   :position :right-center
                                                                   :width    "370px"
                                                                   :disabled? true
                                                                   :info     info]]]
                                                      [input-text
                                                       :model       ""
                                                       :placeholder "Example input #3"
                                                       :disabled? true
                                                       :on-change   #()]]]]]]]
                  [info-button-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [info-button-demo])
