(ns re-com.demo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core             :as    reagent]
            [re-com.util              :as    util]
            [re-com.tabs ]

            [re-com.demo.welcome  :as welcome]
            [re-com.demo.basics   :as basics]
            [re-com.demo.tabs     :as tabs]
            [re-com.demo.popovers :as popovers]
            [re-com.demo.tour     :as tour]
            [re-com.demo.modals   :as modals]))





(def tabs-definition
  ;;  id
  { ::welcome  {:label "Welcome"  :panel welcome/panel}
    ::basics   {:label "Basics"   :panel basics/panel}
    ::alerts   {:label "Alerts"   :panel alerts/panel}
    ::tabs     {:label "Tabs"     :panel tabs/panel}
    ::popovers {:label "Popovers" :panel popovers/panel}
    ::tour     {:label "Tour"     :panel tour/panel}
    ::modals   {:label "Modals"   :panel modals/panel}})


;; http://css-tricks.com/functional-css-tabs-revisited/   (see the demo)
;;
(defn main
  []
  (let [selected-tab-id (reagent/atom (ffirst tabs-definition))]
    (fn _main                                               ;;  TODO:  to assit with debugging, always include a name ?? So we avoid anonomus
      []
      [:div.col-md-12 {:style {:role  "main" :margin-top "15px"}}
       [re-com.tabs/horizontal-pills selected-tab-id tabs-definition]      ;; the button bar
       [:div {:style {:margin "15px"}}
             [(-> (@selected-tab-id tabs-definition) :panel)]]])))         ;; the component to show, for the selected tab


(defn init
  []
  (reagent/render-component [main] (util/get-element-by-id "app")))
