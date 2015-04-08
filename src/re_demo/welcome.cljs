(ns re-demo.welcome
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href input-text p]]
            [re-demo.utils :refer [panel-title title2]]))


; narrow, light grey column of text, on the RHS
(def RHS-column-style
  {:style {:width "250px"
           :font-size "13px"
           :color "#aaa"}})

;; the gap betwen the the two columns
(def center-gap-px "100px")

(defn welecome
  []
  [v-box
   :children [[gap :size "10px"]
              [p
               "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink-href
                :label "Reagent"
                :href "https://github.com/reagent-project/reagent"
                :target "_blank"]
               "."]
              [h-box
               :gap center-gap-px
               :children [
                          [p "It attempts to provide layouts and widgets for building desktop-class apps. Some widgets are still missing."]
                          [p RHS-column-style [:br] "The "
                           [hyperlink-href
                            :label "github repo"
                            :href "https://github.com/Day8/re-com"
                            :target "_blank"]]]]]])


(defn this-app
  []
  [v-box
   :children [
              [title :level :level2 :label "This app"]

              [h-box
               :gap center-gap-px
               :children [[v-box
                           :children [[p "The demo app is an SPA, built using re-com. It serves as:"
                                       [:ul
                                        [:li "a visual showcase of the components"]
                                        [:li "documentation for the components (parameters etc.)"]
                                        [:li "shows, via its own code, how to use the components"]
                                        [:li "a test harness"]]]]]
                          [v-box
                           :children [[p RHS-column-style [:br] [:br] [:br] "Most pages of this app have
                           hyperlinks which take you
                           directly to the associated source code (github)."]]]]]]])


(defn named-params
  []
  [v-box
   :children [
              [title :level :level2 :label "Component Parameters"]
              [gap :size "10px"]
              [p  "Generally, use of a Reagent component looks  like this:"]
              [:pre
               {:style {:width "450px"}}
               "[component-name style-map param1 param2  param3]"]
              [p  "The name of a component is optionally followed by a style map, and then some number of " [:span.bold "positional parameters"] "."]
              [gap :size "10px"]
              [p  "Re-com uses a slightly different approach.  All re-com components take " [:span.bold "named parameters"] "."]
              [h-box
               :gap center-gap-px
               :children [[v-box
                          :children [[p "For example, using the re-com " [:span.bold "button"] " component, looks like:"]
                                     [:pre
                                      {:style {:width "450px"}}
                                      "[button
  :label     \"Click me!\"
  :on-click  #(swap! click-count inc)
  :style     {:background-color \"blue\"}]"]
                                     [p "Each parameter involves a leading keyword name, followed by a value.  Always pairs. "]
                                     [p "Now, our sausage fingers sometimes type onmouseover instead of on-mouse-over,
                                     or centre rather than center, and sometimes we pass in a string where  there should be keyword."]
                                     [p "re-com catches these errors early by validating parameter names and values. Problems are written to the console."]
                                     [p "To avoid the overhead in this checking,
                                         include the following in your project.clj for your productuion builds:"]
                                     [:pre
                                      {:style {:width "450px"}}
                                      ":closure-defines {:goog.DEBUG false}"]]]
                          [v-box
                           :children [[p RHS-column-style "We use named parameters because:"
                                        [:ol
                                         [:li "the code seems more easily read and understood (although longer)"]
                                         [:li "optionality - not all parameters need be supplied and defaults can be introduced"]
                                         [:li "API flexibility - easy to add new parameters"]]]
                                       [p RHS-column-style "Read further analysis "
                                        [hyperlink-href
                                         :label "here"
                                         :href "https://clojurefun.wordpress.com/2012/08/13/keyword-arguments-in-clojure/"
                                         :target "_blank"] "."]]]
                          ]]
              ]])



(defn panel2
  []
  [v-box
   :size     "auto"
   ;:width    "600px"
   :children [[panel-title "Welcome"]
              [gap :size "15px"]
              [welecome]
              [this-app]
              [gap :size "30px"]
              [line]
              [named-params]
              [gap :size "40px"]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
