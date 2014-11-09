(ns re-demo.welcome
  (:require [reagent.core :as reagent]
            [re-com.core  :refer [title hyperlink]]
            [re-com.box   :refer [h-box v-box box gap]]
            [re-demo.utils :refer [panel-title component-title]]))

(def flex-style {:style {:display "flex" :flex "none"}})
(defn panel
  []
  [v-box
   :width "600px"
   :children [[panel-title "Welcome"]
              [gap :size "15px"]
              [:p flex-style
               "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink
                :label  "Reagent"
                :href   "https://github.com/holmsand/reagent"
                :target "_blank"]
               "."]

              [:p "It contains some of layout and widgetry needed to build a desktop-class app."]
              ]])
