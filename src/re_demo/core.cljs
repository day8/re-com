(ns re-demo.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core       :as    reagent]
            [alandipert.storage-atom :refer [local-storage]]
            [re-demo.utils      :refer  [panel-title]]
            [re-com.util        :as    util]
            [re-com.core        :as    core]
            [re-com.tabs]
            [re-com.box         :refer [h-box v-box box gap line scroller border]]
            [re-demo.welcome    :as welcome]
            [re-demo.basics     :as basics]
            [re-demo.dropdowns  :as dropdowns]
            [re-demo.alerts     :as alerts]
            [re-demo.tabs       :as tabs]
            [re-demo.popovers   :as popovers]
            [re-demo.date       :as date-picker]
            [re-demo.lists      :as lists]
            [re-demo.time       :as time]
            [re-demo.layouts    :as layouts]
            [re-demo.tour       :as tour]
            [re-demo.modals     :as modals]
            [re-demo.boxes      :as boxes]))

(enable-console-print!)

(def tabs-definition
  [ {:id ::welcome   :label "Welcome"     :panel welcome/panel}
    {:id ::basics    :label "Basics"      :panel basics/panel}
    {:id ::dropdown  :label "Dropdowns"   :panel dropdowns/panel}
    {:id ::alerts    :label "Alerts"      :panel alerts/panel}
    {:id ::tabs      :label "Tabs"        :panel tabs/panel}
    {:id ::popovers  :label "Popovers"    :panel popovers/panel}
    {:id ::date      :label "Dates"       :panel date-picker/panel}
    {:id ::time      :label "Time"        :panel time/panel}
    {:id ::lists     :label "List"        :panel lists/panel}
    {:id ::tour      :label "Tour"        :panel tour/panel}
    {:id ::modals    :label "Modals"      :panel modals/panel}
    {:id ::boxes1    :label "Boxes-1"     :panel boxes/panel1}
    {:id ::boxes2    :label "Boxes-2"     :panel boxes/panel2}
    {:id ::layouts   :label "Layouts"     :panel layouts/panel}
    ])


(defn nav-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [tab selected-tab-id]
      (let [selected (= @selected-tab-id (:id tab))]
      [:div
       {:style {:color            (if selected "#111")
                :border-right     (if selected "4px #e8e8e8 solid")
                :background-color (if (or
                                        (= @selected-tab-id (:id tab))
                                        @mouse-over?) "#f4f4f4")}

        :class "nav-item"
        :on-mouse-over  #(reset! mouse-over? true)
        :on-mouse-out   #(reset! mouse-over? false)
        :on-click       #(reset! selected-tab-id (:id tab))
       }
       [:span
        {:style {:cursor "default"}}    ;; removes the I-beam over the label
        (:label tab)]]))))


(defn left-side-nav-bar
  [selected-tab-id]
    [v-box
     :children (for [tab tabs-definition]
                 [nav-item tab selected-tab-id])])


(defn re-com-title
  []
  [h-box
   :justify  :center
   :align    :center
   :height   "57px"
   :style  {:color "#FEFEFE"
            :background-color "#888"}
   :children [[core/label
               :label     "Re-com"
               :style {:font-size "28px"
                       :font-family "Ubuntu"
                       :font-weight "300"
                      }]]])

(defn main
  []
  (let [
         id-store        (local-storage (atom nil) ::id-store)
         selected-tab-id (reagent/atom (if  (nil? @id-store) (:id (first tabs-definition)) @id-store))   ;; id of the selected tab
         _               (add-watch selected-tab-id nil #(reset! id-store %4))]
    (fn _main
      []
      [h-box
       ;; TODO: EXPLAIN both lines below with more clarity
       ;; Outer-most box height must be 100% to fill the entrie client area
       ;; (height is 100% of body, which must have already had it's height set to 100%)
       ;; width doesn't need to be initially set
       :height   "100%"
       :gap      "60px"
       ;:padding  "0px 10px 5px 0px"     ;; top right botton left TODO: [GR] Review whether we want this. I don't think so
       :children [[scroller
                   :size  "none"
                   :v-scroll :auto
                   :h-scroll :off
                   :child [v-box
                           :children [[re-com-title]
                                      [left-side-nav-bar selected-tab-id]
                                      #_[re-com.tabs/vertical-pill-tabs ;; tabs down the side
                                       :model selected-tab-id
                                       :tabs  tabs-definition]]]]
                  [scroller
                    :child [box
                            :size      "auto"
                            ;:padding   "15px 0px 5px 0px"         ;; top right bottom left
                            :child     [(:panel (re-com.tabs/find-tab @selected-tab-id tabs-definition))]]] ;; the tab panel to show, for the selected tab
                   ]])))


(defn ^:export  mount-demo
  []
  (reagent/render-component [main] (util/get-element-by-id "app")))
