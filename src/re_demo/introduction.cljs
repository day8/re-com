(ns re-demo.introduction
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href input-text p p-span]]
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
  [v-box
   :children [[gap :size "10px"]
              [p-span
               "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink-href
                :label "Reagent"
                :href "https://github.com/reagent-project/reagent"
                :target "_blank"]
               "."]
              [h-box
               :gap center-gap-px
               :children [
                          [p "It provides " [:span.bold "layout"] " and " [:span.bold "widget"] " components
                          for building desktop-class apps. The set of widgets is incomplete but growing."]
                          [p-span RHS-column-style [:br] "The github repo "
                           [hyperlink-href
                            :label "is here"
                            :href "https://github.com/day8/re-com"
                            :target "_blank"] "."]]]]])


(defn this-app
  []
  [v-box
   :children [
              [title :level :level2 :label "This app"]

              [h-box
               :gap center-gap-px
               :children [[v-box
                           :children [[p "The demo app is an SPA, built using re-com. It serves as:"]
                                      [:ul
                                       [:li "a visual showcase of the components"]
                                       [:li "documentation for the components (parameters etc.)"]
                                       [:li "shows, via its own code, how to use the components"]
                                       [:li "a test harness"]]]]
                          [v-box
                           :children [[p RHS-column-style [:br] [:br] [:br]
                                       "Most pages of this app have hyperlinks which take you
                                        directly to the associated source code (in github)."]]]]]]])


(defn params-validation
  []
  [v-box
   :children [
              [title :level :level2 :label "Has Parameter Validation"]
              [gap :size "10px"]

              [p "Now, our sausage fingers sometimes type onmouseover instead of on-mouse-over,
                                     or centre rather than center, and sometimes we pass in a string where  there should be keyword."]
              [h-box
               :gap center-gap-px
               :children [[v-box
                           :children [[p "re-com catches these errors early by validating both parameter names and values."]
                                      [p "To avoid the overhead in this checking,
                                         include the following in your project.clj for your productuion builds:"]
                                      [:pre
                                       {:style {:width "450px"}}
                                       ":closure-defines {:goog.DEBUG false}"]]]
                          [v-box
                           :children [[p RHS-column-style "Parameter validation errors are written to the console."]]]]]]])

(defn named-params
  []
  [v-box
   :children [
              [title :level :level2 :label "Uses Named Parameters"]
              [gap :size "10px"]
              [p  "Generally, when you use a Reagent component it looks  like this:"]
              [:pre
               {:style {:width "450px"}}
               "[component-name style-map param1 param2  param3]"]
              [p  "The name of a component is optionally followed by a style map, and then some number of " [:span.bold "positional parameters"] "."]
              [gap :size "10px"]

              [h-box
               :gap center-gap-px
               :children [[v-box
                          :children [[p  "Re-com uses a different approach.  All re-com components take " [:span.bold "named parameters"] "."]
                                     [p "For example, using re-com's " [:span.bold "button"] " component, looks like:"]
                                     [:pre
                                      {:style {:width "450px"}}
                                      "[button
  :label     \"Click me!\"
  :on-click  #(swap! click-count inc)
  :style     {:background-color \"blue\"}]"]
                                     [p "Each parameter involves a leading keyword name, followed by a value.  Always pairs. "]]]
                          [v-box
                           :children [[p RHS-column-style "We use named parameters because:"]
                                      [:ol RHS-column-style
                                       [:li "the code seems more easily read and understood (although longer)"]
                                       [:li "optionality - not all parameters need be supplied and defaults can be introduced"]
                                       [:li "API flexibility - easy to add new parameters"]]
                                      [p-span RHS-column-style "Read further analysis "
                                        [hyperlink-href
                                         :label "here"
                                         :href "https://clojurefun.wordpress.com/2012/08/13/keyword-arguments-in-clojure/"
                                         :target "_blank"] "."]]]]]]])


(defn layouts-section
  []
  [v-box
   :children [[title :level :level2 :label "Layouts"]

              [gap :size "10px"]
              [p "Re-com has layout components which are not themselves visible -
              they just arrange other components."]
              [h-box
               :gap center-gap-px
               :children [[v-box
                           :children [
                                      [p "The key components are " [:span.bold "h-box"] " and " [:span.bold "v-box"] " which arange
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
                           [v-box
                            :children [[p-span RHS-column-style "The underlying technology is "
                                        [hyperlink-href
                                         :label "flexbox"
                                         :href "https://css-tricks.com/snippets/css/a-guide-to-flexbox"
                                         :target "_blank"]]]]]]]])


(defn example-layout
  []
  [v-box
   :children [[h-box
               :children [[v-box
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
                          [box
                           :size "100px"
                           :align-self  :center
                           :justify :center
                           :child  [:div {:class "zmdi zmdi-forward rc-icon-larger"
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

(defn panel2
  []
  [v-box
   :children [[panel-title "Introduction"]
              [gap :size "15px"]
              [welecome]
              [this-app]
              [gap :size "30px"]
              [line]
              [named-params]
              [gap :size "30px"]
              [line]
              [params-validation]
              [gap :size "30px"]
              [line]
              [layouts-section]
              [gap :size "30px"]
              [example-layout]
              [gap :size "40px"]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
