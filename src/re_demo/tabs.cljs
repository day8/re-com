(ns re-demo.tabs
  (:require [re-com.core             :refer [h-box v-box box gap line scroller border horizontal-tabs horizontal-bar-tabs
                                             vertical-bar-tabs horizontal-pill-tabs vertical-pill-tabs label button
                                             single-dropdown p]]
            [re-com.tabs             :refer [tabs-args-desc]]
            [re-com.util             :refer [item-for-id]]
            [re-demo.utils           :refer [panel-title title2 args-table github-hyperlink status-text]]
            [alandipert.storage-atom :refer [local-storage]]
            [reagent.core            :as    reagent]))


(def demos [{:id 1 :label "Tab Styles"}
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
       :gap      "20px"
       :children [
                   [p "Each of the five tab components shown below have a distinct visual style."]
                   [p "In this demo, all five tab components share the same state, so they change in lockstep."]
                   [p "For effect, some fake  \"Tab Contents\" (a string of text) is shown in the dotted border below."]
                   [p "The implementation here is simple and your selection is forgotten when you change to another demo panel."]
                  [h-box
                   :align    :center
                   :children [[title2
                               "[horizontal-tabs ... ]"
                               {:width fn-name-width :font-size "20px"}]
                              [horizontal-tabs
                               :model     selected-tab-id
                               :tabs      tabs-definition
                               :on-change change-tab]]]
                  [h-box
                   :align    :center
                   :children [[title2
                               "[horizontal-bar-tabs ... ]"
                               {:width fn-name-width :font-size "20px"}]
                              [horizontal-bar-tabs
                               :model     selected-tab-id
                               :tabs      tabs-definition
                               :on-change change-tab]]]
                  [h-box
                   :align    :center
                   :children [[title2
                               "[vertical-bar-tabs ... ]"
                               {:width fn-name-width :font-size "20px"}]
                              [vertical-bar-tabs
                               :model     selected-tab-id
                               :tabs      tabs-definition
                               :on-change change-tab]]]
                  [h-box
                   :align    :center
                   :children [[title2
                               "[horizontal-pill-tabs ... ]"
                               {:width fn-name-width :font-size "20px"}]
                              [horizontal-pill-tabs
                               :model     selected-tab-id
                               :tabs      tabs-definition
                               :on-change change-tab]]]
                  [h-box
                   :align    :center
                   :children [[title2
                               "[vertical-pill-tabs ... ]"
                               {:width fn-name-width :font-size "20px"}]
                              [vertical-pill-tabs
                               :model     selected-tab-id
                               :tabs      tabs-definition
                               :on-change change-tab]]]
                  [h-box
                   :align    :center
                   :children [[gap :size fn-name-width]
                              [border         ;; Display the tab content which, in this case, is a string extracted from the tab definition.
                               :border  "1px dashed grey"
                               :radius  "10px"
                               :padding "20px"
                               :margin  "10px"
                               :child   [:p (:say-this (item-for-id @selected-tab-id tabs-definition))]]]]]])))


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
       :gap   "10px"
       :children [
                  [p "Any tab selection you make below will persist, because your choice will be
                  remembered using HTML5's local-storage."]
                  [p "If you refresh the entire browser page and return here, you'll see the same selection."]
                  [gap :size "20px"]
                  [horizontal-tabs
                   :model     selected-tab-id
                   :tabs      tab-defs
                   :on-change #(reset! selected-tab-id %)]]])))


(defn adding-tabs-demo
  []
  (let [tab-defs        (reagent/atom [{:id ::1 :label "1"}
                                       {:id ::2 :label "2"}
                                       {:id ::3 :label "3"}])
        selected-tab-id (reagent/atom (:id (first @tab-defs)))]
    (fn []
      [v-box
       :gap   "10px"
       :children [
                  [p "If the " [:code ":tabs"] " parameter is an atom and the value in that atom changes,
                   the display will change dynamically"]
                  [gap :size "20px"]
                  [horizontal-tabs
                   :model     selected-tab-id
                   :tabs      tab-defs
                   :on-change #(reset! selected-tab-id %)]
                  [gap :size "20px"]
                  [h-box
                   :align :center
                   :gap "25px"
                   :children [[label :label "Click to \"add\" more tabs:"]
                              [button
                               :label "Add"
                               :on-click (fn []
                                           (let [c       (str (inc (count @tab-defs)))
                                                 new-tab {:id (keyword c) :label c}]
                                             (swap! tab-defs conj new-tab)))]]]]])))

(defn panel2
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "Tab Components"
                                "src/re_com/tabs.cljs"
                                "src/re_demo/tabs.cljs"]


                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :width    "450px"
                               :gap      "10px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A variety of horizontal and vertical tab selection components, styled
                                            using Bootstrap."]
                                          [p "It is quite straight formward to roll your own tab components. The left
                                           side navigation in this demo is effectively a hand-rolled tab component."]
                                          [args-table tabs-args-desc]]]
                              [v-box
                               :width    "600px"
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [v-box
                                           :children [[h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[label :label "Select a demo"]
                                                                  [single-dropdown
                                                                   :choices   demos
                                                                   :model     selected-demo-id
                                                                   :width     "200px"
                                                                   :on-change #(reset! selected-demo-id %)]]]
                                                      [gap :size "30px"]
                                                      (case @selected-demo-id
                                                        1 [tab-styles-demo]
                                                        2 [remembers-demo]
                                                        3 [adding-tabs-demo])]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
