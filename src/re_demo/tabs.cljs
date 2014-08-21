(ns re-demo.tabs
   (:require [reagent.core :as reagent]
             [alandipert.storage-atom :refer [local-storage]]
             [re-com.box   :refer [h-box v-box box gap line]]
             [re-com.core  :refer [gap button]]
             [re-com.tabs  :as tabs]))


;; Define some tabs.
;; A tab definition need only consist of an :id and a :label. The rest is up to you
;; Below, you'll note that all ids are namespaced keywords, but they can be anything.
;;
(def tabs-definition
  [ {:id ::tab1  :label "Tab1"   :say-this  "I don't like my tab siblings, they smell."}
    {:id ::tab2  :label "Tab2"   :say-this  "Don't listen to Tab1, he's just jealous of my train set."}
    {:id ::tab3  :label "Tab3"   :say-this  "I'm telling Mum on you two !!"}])


(defn title
  [text]
  "An underlined, left justified, H3 Title"
  [box
   :size "auto"
   :child  [v-box
            :children [[h-box
                        :children [[:h3 text]]]
                       [line :size "1px"]]]])


(defn horizontal-tabs-demo
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))]     ;; holds the id of the selected tab
    (fn []
      [v-box
       :children [[title "Horizontal Tabs"]
                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :margin  "20px 0px 0px 0px"       ;; TODO:  i supplied "start" (string) instead of :start and got runtime errors ... better protection
                               :children [
                                           [:div.h4 "Notes:"]
                                           [:ul
                                            [:li "The code for this page can be found in /demo_src/tabs.cljs"]
                                            [:li "For another demonstration, also look in /demo_src/core.cljs. This entire app is just a set of tabs."]
                                            [:li "Tab-like controls can be styled in the ways shown to the right."]
                                            [:li "We've linked all the examples to the one peice of state, so they'll change in lockstep."]
                                            [:li "Also, notice that if you refresh the page, it remembers which tab you were in last time."]]]]

                              [v-box
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TOD0:  decide would we prefer to use :top-margin??
                               :children [
                                           ;; Three visual variations on tabs which share selection state via 'selected-tab-id'
                                           [tabs/horizontal-pills
                                            :model selected-tab-id
                                            :tabs  tabs-definition]

                                           [tabs/horizontal-bar-tabs
                                            :model selected-tab-id
                                            :tabs  tabs-definition]

                                           [tabs/horizontal-tabs
                                            :model selected-tab-id
                                            :tabs  tabs-definition]

                                           ;; display the tab content which, in this case, is extracted from the tab definition
                                           [:h4.well (:say-this (tabs/find-tab @selected-tab-id tabs-definition))]]]]]]])))


(defn remembers-demo
  []
  (let [tab-defs        [{:id ::1 :label "1"}
                         {:id ::2 :label "2"}
                         {:id ::3 :label "3" }]
        id-store        (local-storage (atom nil) ::id-store)
        selected-tab-id (reagent/atom (if  (nil? @id-store) (:id (first tab-defs)) @id-store))   ;; id of the selected tab
        _               (add-watch selected-tab-id nil #(reset! id-store %4))]                   ;; put into local-store for later
    (fn []
      [v-box
       :children [[title "A Remembered Tab"]
                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :margin  "20px 0px 0px 0px"       ;; TODO:  i supplied "start" (string) instead of :start and got runtime errors ... better protection
                               :children [
                                           [:div.h4 "Notes:"]
                                           [:ul
                                            [:li "This tab's selection is remembered in local-storage."]
                                            [:li "When you refresh the page, the selection persists."]
                                            [:li "Also, notice that if you refresh the page, it remembers which tab you were in last time."]]]]

                              [v-box
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TOD0:  decide would we prefer to use :top-margin??
                               :children [[tabs/horizontal-tabs
                                           :model  selected-tab-id
                                           :tabs  tab-defs]]]]]]])))


(defn adding-tabs-demo
  []
  (let [tab-defs        (reagent/atom [{:id ::1 :label "1"}
                                       {:id ::2 :label "2"}
                                       {:id ::3 :label "3"}])
        selected-tab-id (reagent/atom (:id (first @tab-defs)))]
    (fn []
      [v-box
       :children [[title "Dynamic Tabs"]
                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :margin  "20px 0px 0px 0px"       ;; TODO:  i supplied "start" (string) instead of :start and got runtime errors ... better protection
                               :children [[button
                                           :label "Add"
                                           :on-click (fn []
                                                       (let [c (str (inc (count @tab-defs)))]
                                                         (swap! tab-defs conj {:id (keyword c) :label c})))]
                                          [:div.h4 "Notes:"]
                                          [:ul
                                           [:li "Click  \"Add\" for more tabs."]]]]

                              [v-box
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TOD0:  decide would we prefer to use :top-margin??
                               :children [[tabs/horizontal-tabs
                                           :model  selected-tab-id
                                           :tabs  tab-defs]]]]]]])))

(defn vertical-tabs-demo
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))]     ;; this atom holds the id of the selected
    (fn []
      [:div
       [:h3.page-header "Vertical Tabs"]
       [:div.col-md-4
        [:div.h4 "Notes:"]
        [:ul
         [:li "XXX Not Done yet"]]]])))


(defn panel
  []
  [v-box
   :children [[horizontal-tabs-demo]
              [remembers-demo]
              [adding-tabs-demo]
              #_[vertical-tabs-demo]]])
