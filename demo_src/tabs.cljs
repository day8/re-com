(ns re-com.demo.tabs
   (:require [reagent.core :as reagent]
             [re-com.tabs  :as tabs   ]))


;;
;;

(def tabs-definition
  { ::welcome  {:label "Tab1"   :say-this  "I don't like my siblings, they smell."}
    ::basics   {:label "Tab2"   :say-this  "Don't listen to Tab1, he's just jealous of my train set."}
    ::tabs     {:label "Tab3"   :say-this  "I'm telling Mum on you two !!"}})


;; this atom controls which tab is selected
;; In normal code, it wouldn't be global.
;; XXXX Notice how we can remember it for next time, you come in.
(def selected-tab (reagent/atom (ffirst tabs-definition)))

(defn panel
  []
  [:div
   [:h2.page-header "Horizontal Tabs"]
   [:div.col-md-4
    [:div.h4 "Notes:"]
    [:ul
     [:li "The code for this page can be found in /demo_src/tabs.cljs"]
     [:li "For another demonstration, also look in /demo_src/core.cljs. This entire app is just a set of tabs."]
     [:li "Tab-like controls can be styled in the ways demonstrated to the right."]
     [:li "We've linked the three examples to the one peice of state, so they'll change in lockstep."]
     [:li "Also, notice that if you refresh the page, it remembers which tab you were in last time."]]]

   [:div.col-md-7.col-md-offset-1

    ;; Show three variations. They all share the state in 'selected-tab'
    [tabs/horizontal-pills     selected-tab tabs-definition] [:br] [:br]
    [tabs/horizontal-bar-tabs  selected-tab tabs-definition] [:br] [:br]
    [tabs/horizontal-tabs      selected-tab tabs-definition]

    ;; display the tab content
    [:h3.well (-> (@selected-tab tabs-definition) :say-this)]]

   [:h2.page-header {:style {:margin-top "30px"}} "Vertical Tabs"]
   [:div.col-md-4
    [:div.h4 "Notes:"]
    [:ul
     [:li "XXX Not Done yet"]]]
   ])
