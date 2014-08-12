(ns re-com.demo.tabs
   (:require [reagent.core :as reagent]
             [alandipert.storage-atom :refer [local-storage]]
             [re-com.core  :refer [gap]]
             [re-com.tabs  :as tabs]))



;; A tab definition the :say-this part is completely optional
;;
(def tabs-definition
  { ::tab1  {:label "Tab1"   :say-this  "I don't like my siblings, they smell."}
    ::tab2  {:label "Tab2"   :say-this  "Don't listen to Tab1, he's just jealous of my train set."}
    ::tab3  {:label "Tab3"   :say-this  "I'm telling Mum on you two !!"}})



;; In normal code, it wouldn't be global.
;; XXXX Notice how we can remember it for next time, you come in.

(def storage (local-storage (atom nil) ::store))

;; (swap! storage assoc :tab-id selected-tab-id)

(defn panel
  []
  (let [selected-tab-id (reagent/atom (ffirst tabs-definition))]     ;; this atom controls which tab is selected
    (fn []
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

      ;; Three variations which share selection state via 'selected-tab-id'
      [tabs/horizontal-pills     selected-tab-id tabs-definition]   [gap :height 40]
      [tabs/horizontal-bar-tabs  selected-tab-id tabs-definition]   [gap :height 40]
      [tabs/horizontal-tabs      selected-tab-id tabs-definition]

      ;; display the tab content which, in this case, is extracted from the tab definition
      [:h3.well (-> (@selected-tab-id tabs-definition) :say-this)]]

     [:h2.page-header {:style {:margin-top "30px"}} "Vertical Tabs"]
     [:div.col-md-4
      [:div.h4 "Notes:"]
      [:ul
       [:li "XXX Not Done yet"]]]])))
