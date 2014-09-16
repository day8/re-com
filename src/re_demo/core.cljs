(ns re-demo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core       :as    reagent]
            [re-com.util        :as    util]
            [re-com.core        :as    core]
            [re-com.tabs ]
            [re-com.box         :refer [h-box v-box box
                                        gap line scroller border]]

            [re-demo.welcome    :as welcome]
            [re-demo.basics     :as basics]
            [re-demo.dropdowns  :as dropdowns]
            [re-demo.alerts     :as alerts]
            [re-demo.tabs       :as tabs]
            [re-demo.popovers   :as popovers]
            [re-demo.time       :as time]
            [re-demo.date       :as date-chooser]
            [re-demo.layouts    :as layouts]
            [re-demo.tour       :as tour]
            [re-demo.modals     :as modals]
            [re-demo.boxes      :as boxes]))

(enable-console-print!)

(def tabs-definition
  [ {:id ::welcome   :label "Welcome"    :panel welcome/panel}
    {:id ::basics    :label "Basics"     :panel basics/panel}
    {:id ::dropdown  :label "Dropdowns"  :panel dropdowns/panel}
    {:id ::alerts    :label "Alerts"     :panel alerts/panel}
    {:id ::tabs      :label "Tabs"       :panel tabs/panel}
    {:id ::popovers  :label "Popovers"   :panel popovers/panel}
    {:id ::time      :label "Time"       :panel time/panel}
    {:id ::date      :label "Date"       :panel date-chooser/panel}
    {:id ::tour      :label "Tour"       :panel tour/panel}
    {:id ::modals    :label "Modals"     :panel modals/panel}
    {:id ::boxes1    :label "Boxes-1"    :panel boxes/panel1}
    {:id ::boxes2    :label "Boxes-2"    :panel boxes/panel2}
    {:id ::layouts   :label "Layouts"    :panel layouts/panel}])


;; http://css-tricks.com/functional-css-tabs-revisited/   (see the demo)
;;
(defn main
  []
  (let [selected-tab-id (reagent/atom (:id (first tabs-definition)))]
    (fn _main
      []
      [v-box
       ;; TODO: EXPLAIN both lines below with more clarity
       ;; Outer-most box height must be 100% to fill the entrie client area
       ;; (height is 100% of body, which must have already had it's height set to 100%)
       ;; width doesn't need to be initially set
       :height   "100%"
       :children [[box
                   :padding "10px"
                   :child   [re-com.tabs/horizontal-pills ;; tabs across the top
                             :model selected-tab-id
                             :tabs  tabs-definition]]
                  [scroller
                   :child [box
                           :size      "auto"
                           :padding   "0px 10px"
                           :child     [(:panel (re-com.tabs/find-tab @selected-tab-id tabs-definition))]]] ;; the tab panel to show, for the selected tab
                  ]])))


(defn ^:export  mount-demo
  []
  (reagent/render-component [main] (util/get-element-by-id "app")))
