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
            [re-demo.modals   :as modals]
            [re-demo.boxes    :as boxes]))

(enable-console-print!)

(def tabs-definition
  [ {:id ::welcome   :label "Welcome"  :panel welcome/panel}
    {:id ::basics    :label "Basics"   :panel basics/panel}
    {:id ::alerts    :label "Alerts"   :panel alerts/panel}
    {:id ::tabs      :label "Tabs"     :panel tabs/panel}
    {:id ::popovers  :label "Popovers" :panel popovers/panel}
    {:id ::layouts   :label "Layouts"  :panel layouts/panel}
    {:id ::tour      :label "Tour"     :panel tour/panel}
    {:id ::modals    :label "Modals"   :panel modals/panel}
    {:id ::boxes     :label "Boxes"    :panel boxes/panel}])


;; http://css-tricks.com/functional-css-tabs-revisited/   (see the demo)
;;
(defn main
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))
        simple-layout   (reagent/atom false)]
    (fn _main
      []
      [v-box
       ;; TODO: EXPLAIN both lines below with more clarity
       ;; Outer-most box height must be 100% to fill the entrie client area
       ;; (height is 100% of body, which must have already had it's height set to 100%)
       ;; width doesn't need to be initially set
       :height   "100%"
       :children [[box
                   :size    "auto"
                   :padding "10px"
                   :child   [re-com.tabs/horizontal-pills ;; tabs across the top
                             :model selected-tab-id
                             :tabs  tabs-definition]]
                  (if @simple-layout
                    [box
                     ;:f-container true
                     :padding   "0px 10px"
                     :child     [(:panel (re-com.tabs/find-tab @selected-tab-id tabs-definition))]]  ;; the tab panel to show, for the selected tab
                    #_[h-box ;; NOTE: MAKE SURE TO COMMENT THE ABOVE WHEN YOU UNCOMMENT THIS
                     :padding   "0px 10px"
                     :children  [[(:panel (re-com.tabs/find-tab @selected-tab-id tabs-definition))]]]  ;; alternate method to box above
                    [h-box
                     :children [[box
                                 :size "100px"
                                 :padding "10px"
                                 :b-color "coral"
                                 :child [:div
                                         {:style {:background-color "coral"}}
                                         "LAYOUT SIDE BAR fixed to 100px"]]
                                [v-box
                                 :children [[box
                                             ;:f-container true
                                             :size      "60%"
                                             :padding   "0px 10px"
                                             :child     [(:panel (re-com.tabs/find-tab @selected-tab-id tabs-definition))]]  ;; the tab panel to show, for the selected tab
                                            [box
                                             :size      "20%"
                                             :b-color   "teal"
                                             :padding   "0px 10px"
                                             :child     [:div
                                                         {:style {:background-color "teal"}}
                                                         "VERTICAL PANEL #2 (20% high). The one above is 60% high"]]
                                            [gap :size "10px"]
                                            [box
                                             ;:f-container true
                                             :size      "20%"
                                             ;; :b-color "plum"
                                             :padding   "0px 10px"
                                             :child   [h-box
                                                       :children [[box
                                                                   :b-color "khaki"
                                                                   :padding "10px"
                                                                   :child [:div
                                                                           {:style {:background-color "khaki"}}
                                                                           "VERTICAL PANEL #3 (20% high, 10px padding)"]]
                                                                  [box
                                                                   :b-color "tan"
                                                                   :child [:div
                                                                           {:style {:background-color "tan"}}
                                                                           [:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"]]]
                                                                  [gap :size "40px"]
                                                                  [box
                                                                   :b-color "orange"
                                                                   :child [:div
                                                                           {:style {:background-color "orange"}}
                                                                           "horizontal panel #3 (50px gap between this and horizontal panel #2"]]
                                                                  ]
                                                       ]
                                             ]]
                                 ]]
                     ]
                    )
                  ]])))


#_(defn main2
  []
  (let [selected-tab-id (reagent/atom (ffirst tabs-definition))]
    (fn _main
      []
      [v-box
       :height   "100%"
       :children [[box
                   :size "auto"
                   :padding "10px"
                   :child   [re-com.tabs/horizontal-pills ;; tabs across the top
                             :model selected-tab-id
                             :tabs  tabs-definition]]
                  [h-box
                   :children [[box
                               :size "100px"
                               :padding "10px"
                               :b-color "coral"
                               :child [:div "LAYOUT SIDE BAR fixed to 100px"]]
                              [v-box
                               :children [[box
                                           ;:f-container true
                                           :size      "60%"
                                           :padding   "0px 10px"
                                           :child     [(:panel (re-com.tabs/find-tab @selected-tab-id tabs-definition))]]  ;; the tab panel to show, for the selected tab
                                          [box
                                           :size      "20%"
                                           :b-color   "teal"
                                           :padding   "0px 10px"
                                           :child     [:div "VERTICAL PANEL #2 (20% high). The one above is 60% high"]]
                                          [gap :size "10px"]
                                          [box
                                           ;:f-container true
                                           :size      "20%"
                                           ;; :b-color "plum"
                                           :padding   "0px 10px"
                                           :child   [h-box
                                                     :children [[box
                                                                 :b-color "khaki"
                                                                 :padding "10px"
                                                                 :child [:div "VERTICAL PANEL #3 (20% high, 10px padding)"]]
                                                                [box
                                                                 :b-color "tan"
                                                                 :child [:div [:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"]]]
                                                                [gap :size "40px"]
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
