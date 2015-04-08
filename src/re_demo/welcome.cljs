(ns re-demo.welcome
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href input-text p]]
            [re-demo.utils :refer [panel-title title2]]))


(defn welecome
  []
  [v-box
   :children [[p
               "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink-href
                :label "Reagent"
                :href "https://github.com/reagent-project/reagent"
                :target "_blank"]
               "."]
              [p "It provides layouts and controls for building desktop-class apps (incomplete)."]]])

(defn this-app
  []
  [v-box
   :children [
              [title :level :level2 :label "This app"]

              [h-box
               :gap "100px"
               :children [[v-box
                           :children [[p "The demo app itself is built using re-com and serves as:"
                                       [:ul
                                        [:li "a visual showcase of the components"]
                                        [:li "documentation for the components (parameters etc.)"]
                                        [:li "shows, via its own code, how to use the components"]
                                        [:li "a test harness"]]]]]
                          [v-box
                           :style {:font-size "13px" :color "#aaa"}
                           :children [[p {:style {:width "250px"}} [:br] [:br] [:br] "Most pages in this app have
                           hyperlinks near the title which take you
                           directly to the associated source code in github."]]]]]]])


(defn named-params
  []
  [v-box
   :children [
              [title :level :level2 :label "Component Parameters"]
              [p  "re-com components take " [:span.bold "named parameters"] ", rather than " [:span.bold "positional parameters"] "."]
              [p  "Where a normal reagent component might be used like this:"]
              [:pre
               {:style {:width "450px"}}
               "[:input {:type \"button\" :value \"Click me!\"
         :on-click #(swap! click-count inc)
         :style {:background-color \"blue\"}]"]
              [h-box
               :gap "100px"
               :children [[v-box
                          :children [[p "When you use a re-com component, like " [:span.bold "button"] ", it looks like this:"]
                                     [:pre
                                      {:style {:width "450px"}}
                                      "[button
  :label     \"Click me!\"
  :on-click #(swap! click-count inc)
  :style    {:background-color \"blue\"}]"]
                                     [p "Each parameter has a leading keyword name."]
                                     [p "Now, our sausage fingers sometimes type onmouseover instead of on-mouse-over,
                                     or centre rather than center, and sometimes we pass in a string where  there should be keyword."]
                                     [p "re-com catches these errors early by validating parameter names and values."]
                                     [p "To avoid the overhead in this checking,
                                         include the following in your project.clj for your productuion builds:"]
                                     [:pre
                                      {:style {:width "450px"}}
                                      ":closure-defines {:goog.DEBUG false}"]]]
                          [v-box
                           :style {:font-size "13px" :color "#aaa"}
                           :children [[p {:style {:width "250px"}} "We use named parameters because:"
                                        [:ol
                                         [:li "the code seems more easily read and understood (although longer)"]
                                         [:li "optionality - not all parameters need be supplied and defaults can be introduced"]
                                         [:li "API flexibility - easy to add new parameters"]]]
                                       [p {:style {:width "250px"}} "Read further analysis "
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
