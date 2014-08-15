(ns re-demo.tabs
   (:require [reagent.core :as reagent]
             [alandipert.storage-atom :refer [local-storage]]
             [re-com.core  :refer [gap]]
             [re-com.tabs  :as tabs]))




;; Define a few tabs.
;; A tab definition need only consist of an 'id' and a :label. The rest is up to you
;;
(def tabs-definition
  { ::tab1  {:label "Tab1"   :say-this  "I don't like my tab siblings, they smell."}
    ::tab2  {:label "Tab2"   :say-this  "Don't listen to Tab1, he's just jealous of my train set."}
    ::tab3  {:label "Tab3"   :say-this  "I'm telling Mum on you two !!"}})



(defn horizontal-tabs-demo
  []
 (let [selected-tab-id (reagent/atom (ffirst tabs-definition))]     ;; this atom holds the id of the selected
    (fn []                                                          ;; returning a function which closes over selected-tab-id.
      [:div
       [:h3.page-header "Horizontal Tabs"]
       [:div.col-md-4
        [:div.h4 "Notes:"]
        [:ul
         [:li "The code for this page can be found in /demo_src/tabs.cljs"]
         [:li "For another demonstration, also look in /demo_src/core.cljs. This entire app is just a set of tabs."]
         [:li "Tab-like controls can be styled in the ways shown to the right."]
         [:li "We've linked all the examples to the one peice of state, so they'll change in lockstep."]
         [:li "Also, notice that if you refresh the page, it remembers which tab you were in last time."]]]

       [:div.col-md-7.col-md-offset-1

        ;; Three visual variations on tabs which share selection state via 'selected-tab-id'
        [tabs/horizontal-pills
          :model selected-tab-id
          :tabs  tabs-definition]
        [gap :height 40]

        [tabs/horizontal-bar-tabs
          :model selected-tab-id
          :tabs  tabs-definition]
        [gap :height 40]

        [tabs/horizontal-tabs
          :model selected-tab-id
          :tabs  tabs-definition]

        ;; display the tab content which, in this case, is extracted from the tab definition
        [:h4.well (-> (@selected-tab-id tabs-definition) :say-this)]]])))


(defn vertical-tabs-demo
  []
 (let [selected-tab-id (reagent/atom (ffirst tabs-definition))]     ;; this atom holds the id of the selected
    (fn []
      [:div
       [:h3.page-header "Vertical Tabs"]
       [:div.col-md-4
        [:div.h4 "Notes:"]
        [:ul
         [:li "XXX Not Done yet"]]]])))



;; In normal code, it wouldn't be global.
;; XXXX Notice how we can remember it for next time, you come in.

(def storage (local-storage (atom nil) ::store))

;; (swap! storage assoc :tab-id selected-tab-id)

(defn remembers-demo
  []
 (let [tab-defs        { ::1  {:label "1" } ::2  {:label "2" } ::3  {:label "3" }}
       storage         (local-storage (atom nil) ::tab-store)
       selected-tab-id (reagent/atom (ffirst tab-defs))]     ;; this atom holds the id of the selected
    (fn []                                                          ;; returning a function which closes over selected-tab-id.
      [:div
       [:h3.page-header "A Tab That Remembers"]
       [:div.col-md-4
        [:div.h4 "Notes:"]
        [:ul
         [:li "This tab remembers its selection when you refresh the page"]]]

       [:div.col-md-7.col-md-offset-1
        [tabs/horizontal-tabs
          :model  selected-tab-id
          :tabs  tab-defs]


        ]])))


(defn panel
  []
  [:div                             ;; must wrap
   [horizontal-tabs-demo]
   [gap :height 40]
   [remembers-demo]
   [gap :height 40]
   [vertical-tabs-demo]])
