(ns re-demo.welcome
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href input-text p]]
            [re-demo.utils :refer [panel-title title2]]))


(defn welecome
  []
  [v-box
   :size     "auto"
   ;:width    "600px"
   :children [
              [p
               "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink-href
                :label "Reagent"
                :href "https://github.com/reagent-project/reagent"
                :target "_blank"]
               "."]
              [p "It contains the sort of layout and controls you'd need to build a desktop-class app."]


              ]])

(defn this-app
  []
  [v-box
   :children [[title :level :level2 :label "This app"]
              ;[gap :size "5px"]
              [p "The demo app serves as:"
               [:ul
                [:li "a visual showcase of the components"]
                [:li "example code for using the components"]
                [:li "documentation for the components (parameters etc.)"]
                [:li "something of a test harness"]]]]])


(defn named-params
  []
  [v-box
   :children [
              [title :level :level2 :label "Named Parameters"]

              [p  "re-com components take " [:span.bold "named parameters"] ", rather than " [:span.bold "positional parameters"] "."]
              [p "So, use of a re-com component like " [:span.bold "checkbox"] ", looks like this:"]
              [:pre
               {:style {:width "450px"}}
               "[checkbox
  :label     \"Show Status Icon?\"
  :model     status-icon?      ; a ratom
  :on-change (fn [new-val] (reset! status-icon? new-val))]"]
              [p "Notice how each parameter has a short, leading keyword name."]
              [p "Using positional parameters (no naming), is more concise while using named parameters is more explicit.
              Both approaches have their merits - a situation which invariably leads to highly contested
              Religious Wars. "]
              [p "We've gone with named parameters in the API because:"
               [:ol
                [:li "the code seems easier to read (despite being longer)"]
                [:li "as a result the code seems more understandable - something we value above all other considerations."]
                [:li "optionality - not all parameters have to be supplied, defaults can be introduced"]
                [:li "API flexibility - new parameters are easily added"]]]
              [p "Read further analysis "
               [hyperlink-href
                :label "here"
                :href "https://clojurefun.wordpress.com/2012/08/13/keyword-arguments-in-clojure/"
                :target "_blank"] "."]
              ]])


(defn param-validation
  []
  [v-box
   :children [
              [title :level :level2 :label "Named Parameters"]
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

              [h-box
               :gap      "100px"
               :children [[named-params]]]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
