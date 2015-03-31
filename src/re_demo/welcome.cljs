(ns re-demo.welcome
  (:require [re-com.core   :refer [h-box v-box box gap line title label hyperlink-href]]
            [re-demo.utils :refer [panel-title component-title paragraphs]]))


(defn panel2
  []
  [v-box
   :size     "auto"
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
               [:p "It contains some of layout and widgetry needed to build a desktop-class app."]
               [gap :size "10px"]
               [title :level :level2 :label "This app"]
               [gap :size "5px"]
               [:p "The demo app serves as:"
                [:ul
                 [:li "a visual showcase of the components"]
                 [:li "coding demos for using the components"]
                 [:li "documentation for the components (parameters etc.)"]
                 [:li "a harness for testing"]]]
               [title :level :level2 :label "Named Parameters"]
               [:pre
                {:style {:width "40em"}}
"[checkbox
 :label     \"Show Status Icon?\"
 :model     status-icon?      ; a ratom
 :on-change (fn [new-val] (reset! status-icon? new-val))]"]]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
