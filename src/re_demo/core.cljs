(ns re-demo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core     :as    reagent]
            [re-com.util      :as    util]
            [re-com.core      :as    core]
            [re-com.tabs ]
            [re-com.box       :refer [h-box v-box box
                                      gap line]]

            [re-demo.welcome  :as welcome]
            [re-demo.basics   :as basics]
            [re-demo.alerts   :as alerts]
            [re-demo.tabs     :as tabs]
            [re-demo.popovers :as popovers]
            [re-demo.layouts  :as layouts]
            [re-demo.tour     :as tour]
            [re-demo.modals   :as modals]))

(enable-console-print!)

(def tabs-definition
  { ::welcome  {:label "Welcome"  :panel welcome/panel}
    ::basics   {:label "Basics"   :panel basics/panel}
    ::alerts   {:label "Alerts"   :panel alerts/panel}
    ::tabs     {:label "Tabs"     :panel tabs/panel}
    ::popovers {:label "Popovers" :panel popovers/panel}
    ::layouts  {:label "Layouts"  :panel layouts/panel}
    ::tour     {:label "Tour"     :panel tour/panel}
    ::modals   {:label "Modals"   :panel modals/panel}})


;; http://css-tricks.com/functional-css-tabs-revisited/   (see the demo)
;;
(defn main
  []
  (let [selected-tab-id (reagent/atom (ffirst tabs-definition))]
    (fn _main
      []
      [v-box
       :height   "100%"
       :children [[box
                   :f-child false
                   :padding "10px"
                   :child   [re-com.tabs/horizontal-pills ;; tabs across the top
                             :model selected-tab-id
                             :tabs  tabs-definition]]
                  [box
                   :f-contain true
                   :padding   "0px 10px"
                   :child     [(-> (@selected-tab-id tabs-definition) :panel)]]  ;; the tab panel to show, for the selected tab
                  ]
       ])))


(defn main2
  []
  (let [selected-tab-id (reagent/atom (ffirst tabs-definition))]
    (fn _main
      []
      [v-box
       :height   "100%"
       :children [[box
                   :f-child false
                   :padding "10px"
                   :child   [re-com.tabs/horizontal-pills ;; tabs across the top
                             :model selected-tab-id
                             :tabs  tabs-definition]]
                  [h-box
                   :children [[box
                               :f-child false
                               :padding "10px"
                               :b-color "coral"
                               :child [:div {:style {:width "50px"}} "LAYOUT SIDE BAR fixed to 50px"]]
                              [v-box
                               :children [[box
                                           :f-contain true
                                           :size      60
                                           :padding   "0px 10px"
                                           :child     [(-> (@selected-tab-id tabs-definition) :panel)]]  ;; the tab panel to show, for the selected tab
                                          [box
                                           :size      20
                                           :b-color   "teal"
                                           :padding   "0px 10px"
                                           :child     [:div "VERTICAL PANEL #2 (20% high). The one above is 60% high"]]
                                          [gap :size "10px"]
                                          [box
                                           :size      20
                                           ;; :b-color "plum"
                                           :padding   "0px 10px"
                                           :child   [h-box
                                                     :children [[box
                                                                 :b-color "gold"
                                                                 :padding "10px"
                                                                 :child [:div "VERTICAL PANEL #3 (20% high, 10px padding)"]]
                                                                [box
                                                                 :b-color "tan"
                                                                 :child [:div [:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"]]]
                                                                [gap :size "50px"]
                                                                [box
                                                                 :b-color "orange"
                                                                 :child [:div "horizontal panel #3 (50px gap between this and horizontal panel #2"]]
                                                                ]
                                                     ]
                                           ]]
                               ]]
                   ]]
       ])))


(defn ^:export  mount-demo
  []
  (reagent/render-component [main] (util/get-element-by-id "app")))
