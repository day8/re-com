(ns re-demo.core
  (:require-macros [re-com.core            :refer [handler-fn]]
                   [cljs.core.async.macros :refer [go]])
  (:require [reagent.core                   :as    reagent]
            [alandipert.storage-atom        :refer [local-storage]]
            [re-demo.utils                  :refer [panel-title re-com-title]]
            [re-com.util                    :as    util]
            [re-com.box                     :refer [h-box v-box box gap line scroller border]]
            [re-demo.welcome                :as    welcome]
            [re-demo.radio-button           :as    radio-button]
            [re-demo.checkbox               :as    checkbox]
            [re-demo.input-text             :as    input-text]
            [re-demo.slider                 :as    slider]
            [re-demo.label                  :as    label]
            [re-demo.title                  :as    title]
            [re-demo.progress-bar           :as    progress-bar]
            [re-demo.button                 :as    button]
            [re-demo.md-circle-icon-button  :as    md-circle-icon-button]
            [re-demo.md-icon-button         :as    md-icon-button]
            [re-demo.info-button            :as    info-button]
            [re-demo.row-button             :as    row-button]
            [re-demo.hyperlink              :as    hyperlink]
            [re-demo.hyperlink-href         :as    hyperlink-href]
            [re-demo.dropdowns              :as    dropdowns]
            [re-demo.alert-box              :as    alert-box]
            [re-demo.alert-list             :as    alert-list]
            [re-demo.tabs                   :as    tabs]
            [re-demo.popovers               :as    popovers]
            [re-demo.date                   :as    date-picker]
            [re-demo.lists                  :as    lists]
            [re-demo.time                   :as    time]
            [re-demo.layouts                :as    layouts]
            [re-demo.tour                   :as    tour]
            [re-demo.modals                 :as    modals]

            [re-demo.h-box                  :as    h-box]
            [re-demo.v-box                  :as    v-box]
            [re-demo.box                    :as    box]
            [re-demo.gap                    :as    gap]
            [re-demo.line                   :as    line]
            [re-demo.scroller               :as    scroller]
            [re-demo.border                 :as    border]
            ))

(enable-console-print!)

(def tabs-definition
  [
   {:id ::welcome                :label "Welcome"            :panel welcome/panel}

   {:id ::h-box                  :label "H-box"              :panel h-box/panel}
   {:id ::v-box                  :label "V-box"              :panel v-box/panel}
   {:id ::box                    :label "Box"                :panel box/panel}
   {:id ::gap                    :label "Gap"                :panel gap/panel}
   {:id ::line                   :label "Line"               :panel line/panel}
   {:id ::scroller               :label "Scroller"           :panel scroller/panel}
   {:id ::border                 :label "Border"             :panel border/panel}

   {:id ::boxes1                 :label "Box Demo 1"         :panel h-box/panelA}
   {:id ::boxes2                 :label "Box Demo 2"         :panel h-box/panelB}

   {:id ::layouts                :label "Layouts"            :panel layouts/panel}

   {:id ::tabs                   :label "Tabs"               :panel tabs/panel}

   {:id ::button                 :label "Button"             :panel button/panel}
   {:id ::md-circle-icon-button  :label "Circle Icon Button" :panel md-circle-icon-button/panel}
   {:id ::md-icon-button         :label "Icon Button"        :panel md-icon-button/panel}
   {:id ::row-button             :label "Row Button"         :panel row-button/panel}
   {:id ::info-button            :label "Info Button"        :panel info-button/panel}
   {:id ::hyperlink              :label "Hyperlink"          :panel hyperlink/panel}
   {:id ::hyperlink-href         :label "Hyperlink (href)"   :panel hyperlink-href/panel}

   {:id ::label                  :label "Label"              :panel label/panel}
   {:id ::title                  :label "Title"              :panel title/panel}
   {:id ::checkbox               :label "Checkbox"           :panel checkbox/panel}
   {:id ::radio-button           :label "Radio Button"       :panel radio-button/panel}
   {:id ::input-text             :label "Input Text"         :panel input-text/panel}
   {:id ::slider                 :label "Slider"             :panel slider/panel}
   {:id ::progress-bar           :label "Progress Bar"       :panel progress-bar/panel}

   {:id ::dropdown               :label "Dropdowns"          :panel dropdowns/panel}

   {:id ::lists                  :label "Selection List"     :panel lists/panel}

   {:id ::popover-args           :label "Popover Args"       :panel popovers/arg-lists}
   {:id ::popovers               :label "Popover Demos"      :panel popovers/panel}

   {:id ::alert-box              :label "Alert Box"          :panel alert-box/panel}
   {:id ::alert-list             :label "Alert List"         :panel alert-list/panel}

   {:id ::date                   :label "Date Picker"        :panel date-picker/panel}

   {:id ::time                   :label "Input Time"         :panel time/panel}

   {:id ::tour                   :label "Tour"               :panel tour/panel}

   {:id ::modals                 :label "Modals"             :panel modals/panel}
   ])


(defn nav-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [tab selected-tab-id on-select-tab]
      (let [selected (= @selected-tab-id (:id tab))]
      [:div
       {:style {:color            (if selected "#111")
                :border-right     (if selected "4px #d0d0d0 solid")
                :background-color (if (or
                                        (= @selected-tab-id (:id tab))
                                        @mouse-over?) "#eaeaea")}

        :class "nav-item"
        :on-mouse-over (handler-fn (reset! mouse-over? true))
        :on-mouse-out  (handler-fn (reset! mouse-over? false))
        :on-click      (handler-fn (on-select-tab (:id tab)))
        }
       [:span
        {:style {:cursor "default"}}    ;; removes the I-beam over the label
        (:label tab)]]))))


(defn left-side-nav-bar
  [selected-tab-id on-select-tab]
    [v-box
     :children (for [tab tabs-definition]
                 [nav-item tab selected-tab-id on-select-tab])])


(defn re-com-title-box
  []
  [h-box
   :justify  :center
   :align    :center
   :height   "60px"
   :style  {:color "#FEFEFE"
            :background-color "#888"}
   :children [[re-com-title ]]])

(defn main
  []
  (let [id-store        (local-storage (atom nil) ::id-store)
        selected-tab-id (reagent/atom (if (or (nil? @id-store) (nil? (util/item-for-id @id-store tabs-definition)))
                                        (:id (first tabs-definition))
                                        @id-store))   ;; id of the selected tab
        on-select-tab   #(do (reset! selected-tab-id %1)
                             (reset! id-store %1))]
    (fn ;; _main
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
                           :children [[re-com-title-box]
                                      [left-side-nav-bar selected-tab-id on-select-tab]]]]
                  [scroller
                   :child [box
                           :size      "auto"
                           ;:padding   "15px 0px 5px 0px"         ;; top right bottom left
                           :child     [(:panel (util/item-for-id @selected-tab-id tabs-definition))]]]]])))    ;; the tab panel to show, for the selected tab


;; ---------------------------------------------------------------------------------------
;;  EXPERIMENT START - TODO: REMOVE
;; ---------------------------------------------------------------------------------------

(defn green-box
  [markup]
  [:div
   {:style {:width            "200px"
            :height           "40px"
            :margin           "10px 0px 10px"
            :padding          "5px"
            :text-align       "center"
            :background-color "lightgreen"}}
   markup])

(defn green-message-box-bad
  [msg]
  [:div
   [:h3 "Component 1"]
   [green-box [:p "Message: " [:span @msg]]]])

(defn green-message-box-good
  [msg]
  [:div
   [:h3 "Component 2"]
   [green-box [:p "Message: " [(fn [] [:span @msg])]]]])

(defn main1
  [msg show?]
  [:div
   {:style {:padding "20px"}}
   [green-message-box-bad  msg]
   [green-message-box-good msg]
   [:br]
   [:button {:on-click #(swap! show? not)} (if @show? "wax on" "wax off")]
   [:span " ==> "]
   [:button {:on-click #(reset! msg (if @show? "WAX ON!" "WAX OFF!"))} "update text"]])

(defn main2
  []
  (let [msg   (reagent/atom "initial text")
        show? (reagent/atom true)]
    (fn []
      [:div
       {:style {:padding "20px"}}
       [green-message-box-bad  msg]
       [green-message-box-good msg]
       [:br]
       [:button {:on-click #(swap! show? not)} (if @show? "wax on" "wax off")]
       [:span " ==> "]
       [:button {:on-click #(reset! msg (if @show? "WAX ON!" "WAX OFF!"))} "update text"]])))

(defn display-green-messages
  []
  (let [msg   (reagent/atom "initial text")
        show? (reagent/atom true)]
    (fn []
      #_[:div
       {:style {:padding "20px"}}
       [green-message-box-bad  msg]
       [green-message-box-good msg]
       [:br]
       [:button {:on-click #(swap! show? not)} (if @show? "wax on" "wax off")]
       [:span " ==> "]
       [:button {:on-click #(reset! msg (if @show? "WAX ON!" "WAX OFF!"))} "update text"]]

      #_[main1 msg show?]

      [main2]
      )))

;; ---------------------------------------------------------------------------------------
;;  EXPERIMENT END
;; ---------------------------------------------------------------------------------------


(defn ^:export mount-demo
  []
  (reagent/render [main] (util/get-element-by-id "app"))
  ;(reagent/render [display-green-messages] (util/get-element-by-id "app")) ;; TODO: EXPERIMENT - REMOVE
  )
