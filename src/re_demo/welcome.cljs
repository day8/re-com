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
               [:p "It contains some of layout and widgetry needed to build a desktop-class app."]]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
