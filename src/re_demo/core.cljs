(ns re-demo.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core         :refer [defroute]])
  (:require [goog.events                   :as    events]
            [reagent.core                  :as    reagent]
            [alandipert.storage-atom       :refer [local-storage]]
            [secretary.core                :as    secretary]
            [re-com.core                   :refer [h-box v-box box gap line scroller border label title alert-box] :refer-macros [handler-fn]]
            [re-com.util                   :refer [get-element-by-id item-for-id]]
            [re-demo.utils                 :refer [panel-title]]
            [re-demo.introduction          :as    introduction]
            [re-demo.radio-button          :as    radio-button]
            [re-demo.checkbox              :as    checkbox]
            [re-demo.input-text            :as    input-text]
            [re-demo.slider                :as    slider]
            [re-demo.label                 :as    label]
            [re-demo.title                 :as    title]
            [re-demo.progress-bar          :as    progress-bar]
            [re-demo.throbber               :as   throbber]
            [re-demo.button                :as    button]
            [re-demo.md-circle-icon-button :as    md-circle-icon-button]
            [re-demo.md-icon-button        :as    md-icon-button]
            [re-demo.info-button           :as    info-button]
            [re-demo.row-button            :as    row-button]
            [re-demo.hyperlink             :as    hyperlink]
            [re-demo.hyperlink-href        :as    hyperlink-href]
            [re-demo.dropdowns             :as    dropdowns]
            [re-demo.alert-box             :as    alert-box]
            [re-demo.alert-list            :as    alert-list]
            [re-demo.tabs                  :as    tabs]
            [re-demo.popovers              :as    popovers]
            [re-demo.datepicker            :as    datepicker]
            [re-demo.selection-list        :as    selection-list]
            [re-demo.input-time            :as    input-time]
            [re-demo.layout                :as    layout]
            [re-demo.splits               :as     splits]
            [re-demo.tour                  :as    tour]
            [re-demo.modal-panel           :as    modal-panel]
            [re-demo.h-box                 :as    h-box]
            [re-demo.v-box                 :as    v-box]
            [re-demo.box                   :as    box]
            [re-demo.gap                   :as    gap]
            [re-demo.line                  :as    line]
            [re-demo.scroller              :as    scroller]
            [re-demo.border                :as    border]
            [goog.history.EventType        :as    EventType])
  (:import [goog History]))

(enable-console-print!)

(def tabs-definition
  [{:id :introduction           :level :major :label "Introduction"       :panel introduction/panel}


   {:id :buttons                :level :major :label "Buttons"}
   {:id :button                 :level :minor :label "Basic Button"       :panel button/panel}
   {:id :row-button             :level :minor :label "Row Button"         :panel row-button/panel}
   {:id :md-circle-icon-button  :level :minor :label "Circle Icon Button" :panel md-circle-icon-button/panel}
   {:id :md-icon-button         :level :minor :label "Icon Button"        :panel md-icon-button/panel}
   {:id :info-button            :level :minor :label "Info Button"        :panel info-button/panel}
   {:id :hyperlink              :level :minor :label "Hyperlink"          :panel hyperlink/panel}
   {:id :hyperlink-href         :level :minor :label "Hyperlink (href)"   :panel hyperlink-href/panel}

   {:id :basics                 :level :major :label "Basics"}
   {:id :checkbox               :level :minor :label "Checkbox"           :panel checkbox/panel}
   {:id :radio-button           :level :minor :label "Radio Button"       :panel radio-button/panel}
   {:id :input-text             :level :minor :label "Input Text"         :panel input-text/panel}
   {:id :slider                 :level :minor :label "Slider"             :panel slider/panel}
   {:id :progress-bar           :level :minor :label "Progress Bar"       :panel progress-bar/panel}
   {:id :throbber               :level :minor :label "Throbber"           :panel throbber/panel}
   {:id :date                   :level :minor :label "Date Picker"        :panel datepicker/panel}
   {:id :time                   :level :minor :label "Input Time"         :panel input-time/panel}

   {:id :selection              :level :major :label "Selection"}
   {:id :dropdown               :level :minor :label "Dropdowns"          :panel dropdowns/panel}
   {:id :lists                  :level :minor :label "Selection List"     :panel selection-list/panel}
   {:id :tabs                   :level :minor :label "Tabs"               :panel tabs/panel}

   {:id :layers                 :level :major :label "Layers"}
   {:id :modal-panel            :level :minor :label "Modal Panel"        :panel modal-panel/panel}
   {:id :popover-args           :level :minor :label "Popover Args"       :panel popovers/arg-lists}
   {:id :popovers               :level :minor :label "Popover Demos"      :panel popovers/panel}
   {:id :tour                   :level :minor :label "Tour"               :panel tour/panel}

   {:id :typography             :level :major :label "Typography"}
   {:id :label                  :level :minor :label "Label"              :panel label/panel}
   {:id :title                  :level :minor :label "Title"              :panel title/panel} ;; TODO: field-label?
   {:id :alert-box              :level :minor :label "Alert Box"          :panel alert-box/panel}
   {:id :alert-list             :level :minor :label "Alert List"         :panel alert-list/panel}

   {:id :layout                 :level :major :label "Layout"             :panel layout/panel}
   {:id :h-box                  :level :minor :label "H-box"              :panel h-box/panel}
   {:id :v-box                  :level :minor :label "V-box"              :panel v-box/panel}
   {:id :box                    :level :minor :label "Box"                :panel box/panel}
   {:id :gap                    :level :minor :label "Gap"                :panel gap/panel}
   {:id :line                   :level :minor :label "Line"               :panel line/panel}
   {:id :scroller               :level :minor :label "Scroller"           :panel scroller/panel}
   {:id :border                 :level :minor :label "Border"             :panel border/panel}
   {:id :splits                 :level :minor :label "Splits"             :panel splits/panel}
   ])


(defn nav-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [tab selected-tab-id on-select-tab]
      (let [selected?   (= @selected-tab-id (:id tab))
            is-major?  (= (:level tab) :major)
            has-panel? (some? (:panel tab))]
      [:div
       {:style {:width            "150px"
                :line-height      "1.3em"
                :padding-left     (if is-major? "24px" "32px")
                :padding-top      (when is-major? "6px")
                :font-size        (when is-major? "15px")
                :font-weight      (when is-major? "bold")
                :color            (when selected? "#111")
                :border-right     (when selected? "4px #d0d0d0 solid")
                :background-color (if (or
                                        (= @selected-tab-id (:id tab))
                                        @mouse-over?) "#eaeaea")}

        :on-mouse-over (handler-fn (when has-panel? (reset! mouse-over? true)))
        :on-mouse-out  (handler-fn (reset! mouse-over? false))
        :on-click      (handler-fn (when has-panel? (on-select-tab (:id tab))))}
       [:span
        {:style {:cursor "default"}}    ;; removes the I-beam over the label
        (:label tab)]]))))


(defn left-side-nav-bar
  [selected-tab-id on-select-tab]
    [v-box
     :class    "noselect"
     :style    {:background-color "#fcfcfc"}
     :size    "1 0 auto"
     :children (for [tab tabs-definition]
                 [nav-item tab selected-tab-id on-select-tab])])


(defn re-com-title-box
  []
  [h-box
   :justify :center
   :align   :center
   :height  "62px"
   :style   {:background-color "#666"}
   :children [[title
               :label "Re-com"
               :level :level1
               :style {:font-size   "32px"
                       :color       "#fefefe"}
               ]]])

(defn browser-alert
  []
  [box
   :padding "10px 10px 0px 0px"
   :child   [alert-box
             :alert-type :danger
             :heading    "Only Tested On Chrome"
             :body       "re-com should work on all modern browsers, but there might be dragons!"]])

;; -- Routes, Local Storage and History ------------------------------------------------------

(def id-store        (local-storage (atom nil) ::id-store))
(def selected-tab-id (reagent/atom (if (or (nil? @id-store) (nil? (item-for-id @id-store tabs-definition)))
                                     (:id (first tabs-definition))
                                     @id-store)))  ;; id of the selected tab from local storage

(defroute demo-page "/:tab" [tab] (let [id (keyword tab)]
                                    (reset! selected-tab-id id)
                                    (reset! id-store id)))

(def history (History.))
(events/listen history EventType/NAVIGATE (fn [event] (secretary/dispatch! (.-token event))))
(.setEnabled history true)

(defn main
  []
  (let [on-select-tab #(.setToken history (demo-page {:tab (name %1)}))] ;; or can use (str "/" (name %1))
    (fn
      []
      [h-box
       ;; Outer-most box height must be 100% to fill the entrie client height.
       ;; This assumes that height of <body> is itself also set to 100%.
       ;; width does not need to be set.
       :height   "100%"
       :gap      "60px"
       :children [[scroller
                   :size  "none"
                   :v-scroll :auto
                   :h-scroll :off
                   :child [v-box
                           :children [[re-com-title-box]
                                      [left-side-nav-bar selected-tab-id on-select-tab]]]]
                  [scroller
                   :child [v-box
                           :size  "auto"
                           :children [(when-not (-> js/goog .-labs .-userAgent .-browser .isChrome) [browser-alert])
                                      [(:panel (item-for-id @selected-tab-id tabs-definition))]]]]]])))    ;; the tab panel to show, for the selected tab

(defn ^:export mount-demo
  []
  (reagent/render [main] (get-element-by-id "app")))
