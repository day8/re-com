(ns re-demo.welcome
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href input-text p]]
            [re-demo.utils :refer [panel-title title2]]))

(defn named-params
  []
  [v-box
   :children [
              [title :level :level2 :label "Named Parameters"]

               [p  "re-com components take " [:span.bold "named parameters"] ", rather than " [:span.bold "positional parameters "]]
               [p "When you use a re-com component like " [:code "checkbox"] ", you will not be asked to use positional parameters like this:"
               [:pre "[checkbox
               \"Show Status Icon?\"
               status-icon?
               (fn [new-val] (reset! status-icon? new-val))]"]
               ]
              [:pre
               {:style {:width "40em"}}
               "[checkbox
  :label     \"Show Status Icon?\" "
               [input-text  :model "hello" :on-change #()]

               "
  :model     status-icon?      ; a ratom
  :on-change (fn [new-val] (reset! status-icon? new-val))]"]]])


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


(defn first-column
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
              [this-app]
              [gap :size "30px"]
              [line]
              [named-params]]])


(defn panel2
  []
  [v-box
   :size     "auto"
   ;:width    "600px"
   :children [[panel-title "Welcome"]
              [gap :size "15px"]
              [h-box
               :gap      "100px"
               :children [[first-column]
                          [first-column]]]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
