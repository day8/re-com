(ns re-com.demo.tabs
   (:require [reagent.core :as reagent]
             [re-com.tabs  :as tabs   ]))


;;
;;

(def tabs-definition
  { ::welcome  {:label "Tab1"   :say-this  "I don't like my siblings, they smell."}
    ::basics   {:label "Tab2"   :say-this  "Don't listen to Tab1, he's just jealous of my train set."}
    ::tabs     {:label "Tab3"   :say-this  "I'm telling Mum on you two."}})


;; this atom controls which tab is selected
;; In normal code, it wouldn't be global.
;; XXXX Notice how we can remember it for next time, you come in.
(def selected-tab (reagent/atom (ffirst tabs-definition)))

(defn panel
  []
  [:div
   [:h1.page-header "Horizontal Tabs"]
   [:p "The code for this page can be found in /demo_src/tabs.cljs"]
   [:p "Tab-like controls can be styled in a variety of ways."]
   [:p "Below, we've linked the three examples to the one peice of state, so they'll change in lockstep."]
   [:br] [:br]
   [tabs/horizontal-pills     selected-tab tabs-definition]
   [:br] [:br]
   [tabs/horizontal-bar-tabs  selected-tab tabs-definition]
   [:br] [:br]
   [tabs/horizontal-tabs      selected-tab tabs-definition]
   [:h3.well (str "Tab says: " (-> (@selected-tab tabs-definition) :say-this))]
   ])
