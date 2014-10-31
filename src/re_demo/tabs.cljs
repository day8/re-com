(ns re-demo.tabs
  (:require [reagent.core            :as    reagent]
            [alandipert.storage-atom :refer [local-storage]]
            [re-com.box              :refer [h-box v-box box gap line scroller border]]
            [re-com.dropdown         :refer [single-dropdown]]
            [re-com.core             :refer [button label title checkbox]]
            [re-com.tabs             :refer [horizontal-tabs horizontal-bar-tabs pill-tabs arrow-tabs find-tab]]))


(def demos [{:id 1 :label "Tab Styles"}
            {:id 2 :label "Persistent Tab Selection"}
            {:id 3 :label "Dynamic Tabs"}])


;; Define some tabs.
;; A tab definition need only consist of an :id and a :label. The rest is up to you
;; Below, you'll note that all ids are namespaced keywords, but they can be anything.
;;
(def tabs-definition
  [ {:id ::tab1  :label "Tab1"  :say-this "I don't like my tab siblings, they smell."}
    {:id ::tab2  :label "Tab2"  :say-this "Don't listen to Tab1, he's just jealous of my train set."}
    {:id ::tab3  :label "Tab3"  :say-this "I'm telling Mum on you two !!"}])


(defn horizontal-tabs-demo
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))     ;; holds the id of the selected tab
        vert?           (reagent/atom false)
        fn-name-width   "180px"]
    (fn []
      [v-box
       :children [[h-box
                   :gap "50px"
                   :children [[v-box
                               :size "100%"
                               :gap "30px"
                               :margin "20px 0px 0px 0px"
                               :children [[h-box
                                           :gap      "20px"
                                           :children [[label :style {:font-style "italic"} :label "parameters:"]
                                                      [checkbox
                                                       :label     ":vertical?"
                                                       :model     vert?
                                                       :on-change (fn [val] (reset! vert? val))]]]
                                          [h-box
                                           :align    :center
                                           :children [[box
                                                       :width fn-name-width
                                                       :child [:code "horizontal-tabs"]]
                                                      [horizontal-tabs
                                                       :model selected-tab-id
                                                       :tabs  tabs-definition]]]
                                          [h-box
                                           :align    :center
                                           :children [[box
                                                       :width fn-name-width
                                                       :child [:code "horizontal-bar-tabs"]]
                                                      [horizontal-bar-tabs
                                                       :model selected-tab-id
                                                       :tabs  tabs-definition]]]
                                          [h-box
                                           :align    :center
                                           :children [[box
                                                       :width fn-name-width
                                                       :child [:code "pill-tabs"]]
                                                      [pill-tabs
                                                       :model     selected-tab-id
                                                       :tabs      tabs-definition
                                                       :vertical? @vert?]]]
                                          [h-box
                                           :align    :center
                                           :children [[box
                                                       :width fn-name-width
                                                       :child [:span [:code "arrow-tabs"] " *TODO*"]]
                                                      [arrow-tabs
                                                       :model     selected-tab-id
                                                       :tabs      tabs-definition
                                                       :vertical? @vert?]]]
                                          [border         ;; Display the tab content which, in this case, is a string extracted from the tab definition.
                                           :border  "1px dashed grey"
                                           :radius  "10px"
                                           :padding "20px"
                                           :child   [:p (:say-this (find-tab @selected-tab-id tabs-definition))]]]]]]]])))


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
       :children [[h-box
                   :gap "50px"
                   :min-width "1000px"
                   :children [[v-box
                               :size "50%"
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TODO:  decide would we prefer to use :top-margin??
                               :children [[horizontal-tabs
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
       :children [[h-box
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
                                                         (swap! tab-defs conj new-tab)))]]]
                              [v-box
                               :size "50%"
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TODO:  decide would we prefer to use :top-margin??
                               :children [[horizontal-tabs
                                           :model  selected-tab-id
                                           :tabs  tab-defs]]]]]]])))

(defn notes
  [selected-demo-id]
  [v-box
   :width    "500px"
   :style    {:font-size "small"}
   :children [[:div.h4 "General notes"]
              [:ul
               [:li "To create a tab component, the following parameters are available:"
                [:ul
                 [:li.spacer [:code ":model"] " - sets/holds/returns the currently selected tab - can be literal/variable or atom."]
                 [:li.spacer [:code ":tabs"] " - the tabs object defined as a vector of maps - can be literal/variable or atom."]
                 [:li.spacer [:code ":vertical?"] " - stack vertically? - only applicable for " [:code "pill-tabs"] " and " [:code "arrow-tabs"] "."]]]]
              (case @selected-demo-id
                1 [:div
                   [:div.h4 "Horizontal tabs notes"]
                   [:ul
                    [:li "Tab-like controls can be styled in the 3 ways shown to the right."]
                    [:li "All 3 share the same state so they change in lockstep."]
                    [:li "Placeholder  \"Tab Contents\" (a string of text) is shown in the dotted border below. Just for effect."]
                    [:li "The implementation here is simple. As a result, your selection is forgotten when you change to another panel, like Welcome (look top left)."]
                    [:li "The code for this page can be found in /src/re_demo/tabs.cljs"]
                    [:li "For another demonstration, also look in /src/re_demo/core.cljs. After all, this entire demo app is just a series of tabs."]]]
                2 [:div
                   [:div.h4 "Peristent tabs notes"]
                   [:ul
                    [:li "Any tab selection you make on the right will persist."]
                    [:li "It is stored using HTML5's local-storage."]
                    [:li "Even if you refresh the entire browser page, you'll see the same selection."]]]
                3 [:div
                   [:div.h4 "Dynamic tabs notes"]
                   [:ul
                    [:li "Click  \"Add\" for more tabs."]]])]])


(defn panel
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :children [[title "Tabs"]
                  [h-box
                   :gap      "50px"
                   :children [[notes selected-demo-id]
                              [v-box
                               :gap       "15px"
                               :size      "auto"
                               :min-width "500px"
                               :margin    "20px 0px 0px 0px"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a demo"]
                                                       [single-dropdown
                                                        :choices   demos
                                                        :model     selected-demo-id
                                                        :width     "200px"
                                                        :on-change #(reset! selected-demo-id %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           (case @selected-demo-id
                                             1 [horizontal-tabs-demo]
                                             2 [remembers-demo]
                                             3 [adding-tabs-demo])]]]]]])))
