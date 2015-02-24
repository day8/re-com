(ns re-demo.tabs
  (:require [reagent.core            :as    reagent]
            [alandipert.storage-atom :refer [local-storage]]
            [re-com.box              :refer [h-box v-box box gap line scroller border]]
            [re-com.dropdown         :refer [single-dropdown]]
            [re-com.core             :refer [label]]
            [re-com.buttons          :refer [button]]
            [re-com.util             :refer [item-for-id]]
            [re-com.tabs             :refer [horizontal-tabs horizontal-bar-tabs vertical-bar-tabs horizontal-pill-tabs vertical-pill-tabs tabs-args-desc]]
            [re-demo.utils           :refer [panel-title component-title args-table]]))


(def demos [{:id 1 :label "The Tab Styles"}
            {:id 2 :label "Persistent Tab Selection"}
            {:id 3 :label "Dynamic Tabs"}])


;; Define some tabs.
;; A tab definition need only consist of an :id and a :label. The rest is up to you
;; Below, you'll note that all ids are namespaced keywords, but they can be anything.

(def tabs-definition
  [{:id ::tab1  :label "Tab1"  :say-this "I don't like my tab siblings, they smell."}
   {:id ::tab2  :label "Tab2"  :say-this "Don't listen to Tab1, he's just jealous of my train set."}
   {:id ::tab3  :label "Tab3"  :say-this "I'm telling Mum on you two !!"}])


(defn tab-styles-demo
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))     ;; holds the id of the selected tab
        change-tab      #(reset! selected-tab-id %)
        fn-name-width   "240px"]
    (fn []
      [v-box
       :children [[h-box
                   :gap "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [:ul
                                           [:li "Each of the 4 tab components shown to the right has a distinct visual style."]
                                           [:li "In this demo, all 4 tab compoents share the same state, so they change in lockstep."]
                                           [:li "For effect, some fake  \"Tab Contents\" (a string of text) is shown in the dotted border below."]
                                           [:li "The implementation here is simple and your selection is forgotten when you change to
                                              another panel, like Welcome (look top left)."]]
                                          [args-table tabs-args-desc]]]
                              [v-box
                               :size "100%"
                               :gap "50px"
                               :margin "0px 0px 0px 0px"
                               :children [[h-box
                                           :align    :center
                                           :children [[component-title
                                                       "[horizontal-tabs ... ]"
                                                       {:width fn-name-width}]
                                                      [horizontal-tabs
                                                       :model selected-tab-id
                                                       :tabs  tabs-definition
                                                       :on-change change-tab]]]
                                          [h-box
                                           :align    :center
                                           :children [[component-title
                                                       "[horizontal-bar-tabs ... ]"
                                                       {:width fn-name-width}]
                                                      [horizontal-bar-tabs
                                                       :model selected-tab-id
                                                       :tabs  tabs-definition
                                                       :on-change change-tab]]]
                                          [h-box
                                           :align    :center
                                           :children [[component-title
                                                       "[vertical-bar-tabs ... ]"
                                                       {:width fn-name-width}]
                                                      [vertical-bar-tabs
                                                       :model selected-tab-id
                                                       :tabs  tabs-definition
                                                       :on-change change-tab]]]
                                          [h-box
                                           :align    :center
                                           :children [[component-title
                                                       "[horizontal-pill-tabs ... ]"
                                                       {:width fn-name-width}]
                                                      [horizontal-pill-tabs
                                                       :model     selected-tab-id
                                                       :tabs      tabs-definition
                                                       :on-change change-tab]]]
                                          [h-box
                                           :align    :center
                                           :children [[component-title
                                                       "[vertical-pill-tabs ... ]"
                                                       {:width fn-name-width}]
                                                      [vertical-pill-tabs
                                                       :model     selected-tab-id
                                                       :tabs      tabs-definition
                                                       :on-change change-tab]]]
                                          [h-box
                                           :align    :center
                                           :children [[box
                                                       :width fn-name-width
                                                       :child [:span ""]]
                                                      [border         ;; Display the tab content which, in this case, is a string extracted from the tab definition.
                                                       :border  "1px dashed grey"
                                                       :radius  "10px"
                                                       :padding "20px"
                                                       :child   [:p (:say-this (item-for-id @selected-tab-id tabs-definition))]]]]]]]]]])))


(defn remembers-demo
  []
  (let [tab-defs        [{:id ::1 :label "1"}
                         {:id ::2 :label "2"}
                         {:id ::3 :label "3"}
                         {:id ::4 :label "4"}]
        id-store        (local-storage (atom nil) ::id-store)
        selected-tab-id (reagent/atom (if (nil? @id-store) (:id (first tab-defs)) @id-store))    ;; id of the selected tab
        _               (add-watch selected-tab-id nil #(reset! id-store %4))]                   ;; put into local-store for later
    (fn []
      [v-box
       :children [[h-box
                   :gap "50px"
                   :children [[box
                               :width "400px"
                               :child [:div
                                       [component-title "Notes"]
                                       [:ul
                                        [:li "Any tab selection you make on the right will persist."]
                                        [:li "Your choice will be remembered using HTML5's local-storage."]
                                        [:li "If you refresh the entire browser page and return here, you'll see the same selection."]]]]
                              [v-box
                               :width "400px"
                               :gap     "30px"
                               :margin  "20px 0px 0px 0px"       ;; TODO:  decide would we prefer to use :top-margin??
                               :children [[horizontal-tabs
                                           :model     selected-tab-id
                                           :tabs      tab-defs
                                           :on-change #(reset! selected-tab-id %)]]]]]]])))


(defn adding-tabs-demo
  []
  (let [tab-defs        (reagent/atom [{:id ::1 :label "1"}
                                       {:id ::2 :label "2"}
                                       {:id ::3 :label "3"}])
        selected-tab-id (reagent/atom (:id (first @tab-defs)))]
    (fn []
      [v-box
       :children [[gap :size "40px"]
                  [h-box
                   :align :center
                   :gap "25px"
                   :children [[label :label "Click to \"add\" more tabs:"]
                              [button
                               :label "Add"
                               :on-click (fn []
                                           (let [c       (str (inc (count @tab-defs)))
                                                 new-tab {:id (keyword c) :label c}]
                                             (swap! tab-defs conj new-tab)))]
                              [gap :size "10px"]
                              [v-box
                               :width "500px"
                               :gap     "30px"
                               ;;:margin  "20px 0px 0px 0px"       ;; TODO:  decide would we prefer to use :top-margin??
                               :children [[horizontal-tabs
                                           :model     selected-tab-id
                                           :tabs      tab-defs
                                           :on-change #(reset! selected-tab-id %)]]]]]]])))

(defn panel2
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :gap      "10px"
       :children [[panel-title "Tab Components"]
                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap       "30px"
                               :size      "auto"
                               ;:min-width "500px"
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
                                           (case @selected-demo-id
                                             1 [tab-styles-demo]
                                             2 [remembers-demo]
                                             3 [adding-tabs-demo])]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panel2])
