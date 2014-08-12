(ns re-com.demo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core             :as    reagent]
            [re-com.util              :as    util]
            [re-com.tabs              :as    tabs]

            [re-com.demo.welcome  :as welcome]
            [re-com.demo.basics   :as basics]
            [re-com.demo.popovers :as popovers]
            [re-com.demo.tour     :as tour]
            [re-com.demo.modals   :as modals]))



(def tabs-definition
  { ::welcome  {:label "Welcome"  :panel welcome/panel}
    ::basics   {:label "Basics"   :panel basics/panel}
    ::popovers {:label "Popovers" :panel popovers/panel}
    ::tour     {:label "Tour"     :panel tour/panel}
    ::modals   {:label "Modals"   :panel modals/panel}})


;; http://css-tricks.com/functional-css-tabs-revisited/   (see the demo)
;;
(defn main
  []
  (let [selected-tab (reagent/atom (ffirst tabs-definition))]
    (fn _main                                               ;;  TODO:  to assit with debugging, always include a name ?? So we avoid anonomus
      []
      [:div {:style {:margin "15px"}}
        [tabs/horizontal-pills selected-tab tabs-definition]      ;; the button bar
        [(-> (@selected-tab tabs-definition) :panel)]])))   ;; the panel for the select tab


(defn init
  []
  (reagent/render-component [main] (util/get-element-by-id "app")))
