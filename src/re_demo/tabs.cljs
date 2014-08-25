(ns re-demo.tabs
   (:require [reagent.core :as reagent]
             [alandipert.storage-atom :refer [local-storage]]
             [re-demo.util :refer  [title]]
             [re-com.box   :refer [h-box v-box box gap line scroller border]]
             [re-com.core  :refer [gap-old button]]
             [re-com.tabs  :as tabs]))


;; Define some tabs.
;; A tab definition need only consist of an :id and a :label. The rest is up to you
;; Below, you'll note that all ids are namespaced keywords, but they can be anything.
;;
(def tabs-definition
  [ {:id ::tab1  :label "Tab1"   :say-this  "I don't like my tab siblings, they smell."}
    {:id ::tab2  :label "Tab2"   :say-this  "Don't listen to Tab1, he's just jealous of my train set."}
    {:id ::tab3  :label "Tab3"   :say-this  "I'm telling Mum on you two !!"}])


(defn horizontal-tabs-demo
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))]     ;; holds the id of the selected tab
    (fn []
      [v-box
       :children [[title "Horizontal Tabs"]
                  [h-box
                   :gap "50px"
                   :min-width "1200px"
                   :children [[v-box
                               :size "450px"
                               :margin  "20px 0px 0px 0px"        ;; TODO:  i supplied "start" (string) instead of :start and got runtime errors ... better protection
                               :children [[:div.h4 "Notes:"]
                                          [:ul
                                           [:li "Tab-like controls can be styled in the 3 ways shown to the right."]
                                           [:li "All 3 share the same state so they change in lockstep."]
                                           [:li "Placeholder  \"Tab Contents\" (a string of text) is shown in the dotted border below. Just for effect."]
                                           [:li "The implementation here is simple. As a result, your selection is forgotten when you change to another panel, like Welcome (look top left)."]
                                           [:li "The code for this page can be found in /src/re_demo/tabs.cljs"]
                                           [:li "For another demonstration, also look in /src/re_demo/core.cljs. After all, this entire demo app is just a series of tabs."]]]]

                              [v-box
                               :size "100%"
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"        ;; TODO:  decide would we prefer to use :top-margin??
                               :children [
                                           ;; Three visual variations on tabs follow
                                           [tabs/horizontal-pills
                                            :model selected-tab-id
                                            :tabs  tabs-definition]

                                           [tabs/horizontal-bar-tabs
                                            :model selected-tab-id
                                            :tabs  tabs-definition]

                                           [tabs/horizontal-tabs
                                            :model selected-tab-id
                                            :tabs  tabs-definition]

                                           ;; Display the tab content which, in this case, is a string
                                           ;; extracted from the tab definition.
                                           ;; We out a dotted border around it for dramatic effect.
                                           [border
                                            :border  "1px dashed grey"
                                            :radius  "10px"
                                            :padding "20px"
                                            ;;:margin  "4px"
                                            ;:size    "50%"
                                            :child [:p (:say-this (tabs/find-tab @selected-tab-id tabs-definition))]]]]]]]])))


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
       :children [[title "A Persistent Tab Selection"]
                  [h-box
                   :gap "50px"
                   :min-width "1000px"
                   :children [[v-box
                               :size "50%"
                               :margin  "20px 0px 0px 0px"       ;; TODO:  i supplied "start" (string) instead of :start and got runtime errors ... better protection
                               :children [
                                           [:div.h4 "Notes:"]
                                           [:ul
                                            [:li "Any tab selection you make on the right will persist."]
                                            [:li "It is stored using HTML5's local-storage."]
                                            [:li "Even if you refresh the entire browser page, you'll see the same selection."]]]]

                              [v-box
                               :size "50%"
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TODO:  decide would we prefer to use :top-margin??
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
                               :size    "auto"
                               :margin  "20px 0px 0px 0px"        ;; TODO:  i supplied "start" (string) instead of :start and got runtime errors ... better protection
                               :align   :start
                               :children [[button
                                           :label "Add"
                                           :on-click (fn []
                                                       (let [c       (str (inc (count @tab-defs)))
                                                             new-tab {:id (keyword c) :label c}]
                                                         (swap! tab-defs conj new-tab)))]
                                          [:div.h4 "Notes:"]
                                          [:ul
                                           [:li "Click  \"Add\" for more tabs."]]]]

                              [v-box
                               :size "50%"
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TODO:  decide would we prefer to use :top-margin??
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
