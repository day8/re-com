(ns re-demo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core         :as    reagent]
            [re-com.util          :as    util]
            [re-com.core          :as    core]
            [re-com.tabs ]

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
      [:div.col-md-12
       [core/gap :height 15]
       [re-com.tabs/horizontal-pills                                 ;; tabs across the top
          :model selected-tab-id
          :tabs  tabs-definition]
       [:div [(-> (@selected-tab-id tabs-definition) :panel)]]])))   ;; the tab panel to show, for the selected tab


(defn ^:export  mount-demo
  []
  (reagent/render-component [main] (util/get-element-by-id "app")))
