(ns re-demo.introduction
  (:require [re-com.core   :refer [at h-box v-box box gap line title label hyperlink-href input-text p p-span]]
            [re-demo.utils :refer [panel-title title2]]))

; narrow, light grey column of text, on the RHS
(def RHS-column-style
  {:style {:width "450px"
           :font-size "13px"
           :color "#aaa"}})

;; the gap betwen the the two columns
(def center-gap-px "100px")

(defn welecome
  []
  [v-box :src (at)
   :children [[gap :src (at) :size "10px"]
              [p-span
               "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink-href :src (at)
                :label "Reagent"
                :href "https://github.com/reagent-project/reagent"
                :target "_blank"]
               "."]
              [h-box :src (at)
               :gap center-gap-px
               :children [[p "It provides " [:span.bold "layout"] " and " [:span.bold "widget"] " components
                          for building desktop-class apps. The set of widgets is incomplete but growing."]
                          [p-span RHS-column-style [:br] "The github repo "
                           [hyperlink-href :src (at)
                            :label "is here"
                            :href "https://github.com/day8/re-com"
                            :target "_blank"] "."]]]]])

(defn this-app
  []
  [v-box :src (at)
   :children [; [title :level :level2 :label "This app"]

              [h-box :src (at)
               :gap center-gap-px
               :children [[v-box :src (at)
                           :children [[p "This demo app is an SPA (single page application), built using re-com. It serves as:"]
                                      [:ul
                                       [:li "a visual showcase of the components"]
                                       [:li "documentation for the components (parameters etc.)"]
                                       [:li "shows, via its own code, how to use the components"]
                                       [:li "something of a test harness"]]]]
                          [v-box :src (at)
                           :children [[p RHS-column-style [:br] [:br] [:br]
                                       "Most pages of this app have hyperlinks (to the right of the page title) which take you
                                        directly to the associated source code (within github)."]]]]]]])

(defn named-params
  []
  [v-box :src (at)
   :children [[title :src (at) :level :level2 :label "Uses Named Parameters"]
              [gap :src (at) :size "10px"]
              [p  "Generally, when you use a Reagent component it looks  like this:"]
              [:pre
               {:style {:width "450px"}}
               "[component-name style-map param1 param2  param3]"]
              [p  "The name of a component is optionally followed by a style map, and then some number of " [:span.bold "positional parameters"] "."]
              [gap :src (at) :size "10px"]

              [h-box :src (at)
               :gap center-gap-px
               :children [[v-box :src (at)
                           :children [[p  "Re-com uses a different approach.  All re-com components take " [:span.bold "named parameters"] "."]
                                      [p "For example, using re-com's " [:span.bold "button"] " component, looks like:"]
                                      [:pre
                                       {:style {:width "450px"}}
                                       "[button
  :label     \"Click me!\"
  :on-click  #(swap! click-count inc)
  :style     {:background-color \"blue\"}]"]
                                      [p "Each parameter involves a leading keyword name, followed by a value.  Always pairs. "]]]
                          [v-box :src (at)
                           :children [[p RHS-column-style "We use named parameters because:"]
                                      [:ol RHS-column-style
                                       [:li "the code seems more easily read and understood (although longer)"]
                                       [:li "optionality - not all parameters need be supplied and defaults can be introduced"]
                                       [:li "API flexibility - easy to add new parameters"]]
                                      [p-span RHS-column-style "Read further analysis "
                                       [hyperlink-href :src (at)
                                        :label "here"
                                        :href "https://clojurefun.wordpress.com/2012/08/13/keyword-arguments-in-clojure/"
                                        :target "_blank"] "."]]]]]]])

(defn layouts-section
  []
  [v-box :src (at)
   :children [[title :src (at) :level :level2 :label "Layouts"]

              [gap :src (at) :size "10px"]
              [p "Re-com has layout components which are not themselves visible -
              they just arrange other components."]
              [h-box :src (at)
               :gap center-gap-px
               :children [[v-box :src (at)
                           :children [[p "The key components are " [:span.bold "h-box"] " and " [:span.bold "v-box"] " which arange
               their children horizontally and vertically respectively. Because they are
               mutually nestable, you can combine them to create arbitrarily complex layouts."]
                                      [p "This very page involves a " [:span.bold "v-box"] " arranging other components:"]
                                      [:pre
                                       {:style {:width "450px"}}
                                       "[v-box
   :children [[title \"Introduction\"]
              [gap :size \"15px\"]
              [this-app]
              [gap :size \"30px\"]
              [line]
              [named-params]
              ... etc
              ]]"]]]
                          [v-box :src (at)
                           :children [[p-span RHS-column-style "The underlying technology is "
                                       [hyperlink-href :src (at)
                                        :label "flexbox"
                                        :href "https://css-tricks.com/snippets/css/a-guide-to-flexbox"
                                        :target "_blank"]]]]]]]])

(defn example-layout
  []
  [v-box :src (at)
   :children [[h-box :src (at)
               :children [[v-box :src (at)
                           :children [[p "And this example code, showing an " [:span.bold "h-box"] " as a child of a " [:span.bold "v-box"] " ..."]
                                      [:pre
                                       {:style {:width "460px"}}
                                       "[v-box
  :children [[box :child \"Header\"]
             [h-box
              :height \"100px\"
              :children [[box :size \"70px\" :child \"Nav\"]
                         [box :size \"1\" :child \"Content\"]]]
             [box :child \"Footer\"]]]"]]]
                          [box :src (at)
                           :size "100px"
                           :align-self  :center
                           :justify :center
                           :child  [:div {:class "zmdi zmdi-forward rc-icon-larger"
                                          :style {:color "lightgrey"}}]]
                          [v-box :src (at)
                           :children [[p "... results in this kind of structure:"]
                                      [v-box :src (at)
                                       :gap      "1px"
                                       :children [[box :src (at) :style {:background-color "lightgrey"} :child "Header"]
                                                  [h-box :src (at)
                                                   :gap "1px"
                                                   :height "100px"
                                                   :children [[box :src (at) :size "70px" :style {:background-color "lightgrey"} :child "Nav"]
                                                              [box :src (at) :size "1" :style {:background-color "lightgrey"} :child "Content"]]]
                                                  [box :src (at) :style {:background-color "lightgrey"} :child "Footer"]]]
                                      [gap :src (at) :size "15px"]]]]]]])

(defn panel2
  []
  [v-box :src (at)
   :children [[panel-title "Introduction" "src/re_com/core.cljs" "src/re_demo/introduction.cljs"]
              [gap :src (at) :size "15px"]
              [welecome]
              [this-app]
              [gap :src (at) :size "30px"]
              [line :src (at)]
              [named-params]
              [gap :src (at) :size "30px"]
              [line :src (at)]
              [layouts-section]
              [gap :src (at) :size "30px"]
              [example-layout]
              [gap :src (at) :size "40px"]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])

