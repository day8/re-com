(ns re-demo.welcome
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href input-text]]
            [re-demo.utils :refer [panel-title title2 paragraphs]]))

(defn named-params
  []
  [v-box
   :children [
              [title :level :level2 :label "Named Parameters"]

               [:p {:width "60em"} "re-com components take " [:code "named parameters"] ", rather than " [:code "positional parameters"] ]
               [:p {:width "60em"}"When you use a re-com component like " [:code "checkbox"] ", you will not be asked to use positional parameters like this:"
               [:pre {:width "60em"} "[checkbox
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
              [:p "The demo app serves as:"
               [:ul
                [:li "a visual showcase of the components"]
                [:li "coding demos for using the components"]
                [:li "documentation for the components (parameters etc.)"]
                [:li "a harness for testing"]]]]])


(defn panel2
  []
  [v-box
   :size     "auto"
   :width    "600px"
   :children [[panel-title "Welcome"]
              [gap :size "15px"]
              [paragraphs
               [:p
                "Re-com is a library of ClojureScript UI components, built on top of "
                [hyperlink-href
                 :label "Reagent"
                 :href "https://github.com/reagent-project/reagent"
                 :target "_blank"]
                "."]
               [:p "It contains the sort of layout and controls you'd need to build a desktop-class app."]]
               [this-app]
               [line]
               [named-params]
               ]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
