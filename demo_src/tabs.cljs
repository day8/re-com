(ns re-com.demo.tabs
   (:require [reagent.core :as reagent]
             [re-com.tabs  :as tabs   ]))


;;
;;

(def tabs-definition
  { ::welcome  {:label "Tab1"   :say-this  "I don't like my siblings, they smell."}
    ::basics   {:label "Tab2"   :say-this  "Don't listen to Tab1, he's just jealous of my train set."}
    ::tabs     {:label "Tab3"   :say-this  "I'm tell Mum."}})


;; this atom controls which tabe is selected
(def selected-tab (reagent/atom (ffirst tabs-definition)))

(defn panel
  []
  [:div
   [:h3 "Tab Controls"]
   [:p "Tab-like controls can be styled in a variety of ways."]
   [:p "Below, we've linked the three examples below to the one peice of state, so they'll change in lockstep."]
   [tabs/horizontal-pills     selected-tab tabs-definition]
   [:p ]
   [tabs/horizontal-bar-tabs  selected-tab tabs-definition]
   [:p]
   [tabs/horizontal-tabs      selected-tab tabs-definition]
   [:p]
   ])
