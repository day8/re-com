(ns re-demo.core
  (:require-macros
   [re-com.core            :refer []]
   [cljs.core.async.macros :refer [go]]
   [secretary.core         :refer [defroute]])
  (:require [goog.events                   :as    events]
            [reagent.core                  :as    reagent]
            [reagent.dom                   :as    rdom]
            [alandipert.storage-atom       :refer [local-storage]]
            [secretary.core                :as    secretary]
            [re-com.core                   :as rc :refer [at h-box v-box box gap line scroller border label p title alert-box h-split] :refer-macros [handler-fn]]
            [re-com.config                 :refer [version]]
            [re-com.util                   :as u :refer [get-element-by-id item-for-id]]
            [re-demo.utils                 :refer [panel-title scroll-to-top]]
            [re-demo.debug                 :as    debug]
            [re-demo.config                :as    config]
            [re-demo.introduction          :as    introduction]
            [re-demo.radio-button          :as    radio-button]
            [re-demo.checkbox             :as    checkbox]
            [re-demo.input-text            :as    input-text]
            [re-demo.slider                :as    slider]
            [re-demo.label                 :as    label]
            [re-demo.p                     :as    p]
            [re-demo.title                 :as    title]
            [re-demo.progress-bar          :as    progress-bar]
            [re-demo.throbber              :as    throbber]
            [re-demo.button                :as    button]
            [re-demo.md-circle-icon-button :as    md-circle-icon-button]
            [re-demo.md-icon-button        :as    md-icon-button]
            [re-demo.info-button           :as    info-button]
            [re-demo.row-button            :as    row-button]
            [re-demo.hyperlink             :as    hyperlink]
            [re-demo.hyperlink-href        :as    hyperlink-href]
            [re-demo.dropdowns             :as    dropdowns]
            [re-demo.dropdown              :as    dropdown]
            [re-demo.alert-box             :as    alert-box]
            [re-demo.alert-list            :as    alert-list]
            [re-demo.tabs                  :as    tabs]
            [re-demo.tree-select           :as    tree-select]
            [re-demo.popovers              :as    popovers]
            [re-demo.datepicker            :as    datepicker]
            [re-demo.daterange             :as    daterange]
            [re-demo.selection-list        :as    selection-list]
            [re-demo.input-time            :as    input-time]
            [re-demo.layout                :as    layout]
            [re-demo.splits                :as    splits]
            [re-demo.tour                  :as    tour]
            [re-demo.modal-panel           :as    modal-panel]
            [re-demo.multi-select          :as    multi-select]
            [re-demo.h-box                 :as    h-box]
            [re-demo.v-box                 :as    v-box]
            [re-demo.box                   :as    box]
            [re-demo.gap                   :as    gap]
            [re-demo.line                  :as    line]
            [re-demo.scroller              :as    scroller]
            [re-demo.border                :as    border]
            [re-demo.tag-dropdown          :as    tag-dropdown]
            [re-demo.typeahead             :as    typeahead]
            [re-demo.v-table               :as    v-table]
            [re-demo.simple-v-table        :as    simple-v-table]
            [re-demo.nested-grid                 :as    nested-grid]
            [re-com.nested-grid            :as ng]
            [reagent.core :as r]
            [goog.history.EventType        :as    EventType])
  (:import [goog History]))

;; TODO - is this needed any more??
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
   {:id :daterange              :level :minor :label "Date Range Picker"  :panel daterange/panel}
   {:id :time                   :level :minor :label "Input Time"         :panel input-time/panel}

   {:id :selection              :level :major :label "Selection"}
   {:id :single-dropdown        :level :minor :label "Dropdown"           :panel dropdowns/panel}
   {:id :lists                  :level :minor :label "Selection List"     :panel selection-list/panel}
   {:id :multi-select           :level :minor :label "Multi-select List"  :panel multi-select/panel}
   {:id :tag-dropdown           :level :minor :label "Tag Dropdown"       :panel tag-dropdown/panel}
   {:id :tree-select            :level :minor :label "Tree-select"        :panel tree-select/panel}
   {:id :tabs                   :level :minor :label "Tabs"               :panel tabs/panel}
   {:id :typeahead              :level :minor :label "Typeahead"          :panel typeahead/panel}
   {:id :generic-dropdown        :level :minor :label "Generic Dropdown"   :panel dropdown/panel}

   {:id :tables                 :level :major :label "Tables"}
   {:id :simple-v-table         :level :minor :label "Simple V-table"     :panel simple-v-table/panel}
   {:id :v-table                :level :minor :label "V-table"            :panel v-table/panel}
   {:id :nested-grid                  :level :minor :label "Nested Grid"        :panel nested-grid/panel}

   {:id :layers                 :level :major :label "Layers"}
   {:id :modal-panel            :level :minor :label "Modal Panel"        :panel modal-panel/panel}
   {:id :popovers               :level :minor :label "Popover"            :panel popovers/panel}
   {:id :popover-reference      :level :minor :label "Popover Reference"  :panel popovers/arg-lists}
   {:id :tour                   :level :minor :label "Tour"               :panel tour/panel}

   {:id :typography             :level :major :label "Typography"}
   {:id :label                  :level :minor :label "Label"              :panel label/panel}
   {:id :p                      :level :minor :label "Paragraph (p)"      :panel p/panel}
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

   {:id :debug                  :level :major :label "Debugging"          :panel debug/panel}
   {:id :config                 :level :major :label "Config"             :panel config/panel}])

(defn nav-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [tab selected-tab-id on-select-tab]
      (let [selected?   (= @selected-tab-id (:id tab))
            is-major?  (= (:level tab) :major)
            has-panel? (some? (:panel tab))]
        [:div
         {:style         {;:width            "150px"
                          :white-space      "nowrap"
                          :line-height      "1.3em"
                          :padding-left     (if is-major? "24px" "32px")
                          :padding-top      (when is-major? "6px")
                          :font-size        (when is-major? "15px")
                          :font-weight      (when is-major? "bold")
                          :border-right     (when selected? "4px #d0d0d0 solid")
                          :cursor           (if has-panel? "pointer" "default")
                          :color            (if has-panel? (when selected? "#111") "#888")
                          :background-color (if (or
                                                 (= @selected-tab-id (:id tab))
                                                 @mouse-over?) "#eaeaea")}

          :on-mouse-over (handler-fn (when has-panel? (reset! mouse-over? true)))
          :on-mouse-out  (handler-fn (reset! mouse-over? false))
          :on-click      (handler-fn (when has-panel?
                                       (on-select-tab (:id tab))
                                       (scroll-to-top (get-element-by-id "right-panel"))))}
         [:span (:label tab)]]))))

(defn left-side-nav-bar
  [selected-tab-id on-select-tab]
  (let [background-col "#fcfcfc"]
    [v-box
     :src      (at)
     :size     "1"
     :style    {:background-color background-col}
     :children [[v-box
                 :src      (at)
                 :class    "noselect"
                 :style    {:background-color background-col}
                 :children (conj (into []
                                       (for [tab tabs-definition]
                                         [nav-item tab selected-tab-id on-select-tab])))]
                [gap
                 :src  (at)
                 :size "1"]
                [box
                 :src   (at)
                 :style {:padding "8px 24px"
                         :background-color background-col}
                 :child [label
                         :src   (at)
                         :style {:font-size "10px"}
                         :label version]]]]))

(defn re-com-title-box
  []
  [h-box
   :src     (at)
   :justify :center
   :align   :center
   :height  "62px"
   :style   {:background-color "#666"}
   :children [[title
               :src   (at)
               :label "re-com"
               :level :level1
               :style {:font-size   "32px"
                       :color       "#fefefe"}]]])

(defn browser-alert
  []
  [box
   :src     (at)
   :padding "10px 10px 0px 0px"
   :child   [alert-box
             :src        (at)
             :alert-type :danger
             :heading    "Only Tested On Chrome"
             :body       "re-com should work on all modern browsers, but there might be dragons!"]])

;; -- Routes, Local Storage and History ------------------------------------------------------

(defonce id-store        (local-storage (atom nil) ::id-store))
(defonce selected-tab-id (reagent/atom (if (or (nil? @id-store) (nil? (item-for-id @id-store tabs-definition)))
                                         (:id (first tabs-definition))
                                         @id-store)))  ;; id of the selected tab from local storage

(defroute demo-page "/:tab" [tab] (let [id (keyword tab)]
                                    (reset! selected-tab-id id)
                                    (reset! id-store id)))

(defonce history (History.))
(events/listen history EventType/NAVIGATE (fn [^js event] (secretary/dispatch! (.-token event))))
(.setEnabled history true)

(defn main
  []
  (let [on-select-tab #(.setToken history (demo-page {:tab (name %1)}))] ;; or can use (str "/" (name %1))
    (fn
      []
      ;(set! re-com.box/visualise-flow? true)
      [h-split
       ;; Outer-most box height must be 100% to fill the entire client height.
       ;; This assumes that height of <body> is itself also set to 100%.
       ;; width does not need to be set.
       :src           (at)
       :height        "100%"
       :split-is-px?  true
       :initial-split 180
       :margin        "0px"
       :panel-1       [scroller
                       :src      (at)
                       ;:size  "none"
                       :v-scroll :auto
                       :h-scroll :off
                       :child [v-box
                               :src      (at)
                               :size     "1"
                               :children [[re-com-title-box]
                                          [left-side-nav-bar selected-tab-id on-select-tab]]]]
       :panel-2       [scroller
                       :src   (at)
                       :attr  {:id "right-panel"}
                       :child [v-box
                               :src      (at)
                               :size     "1"
                               :children [(when-not (-> js/goog .-labs .-userAgent .-browser .isChrome) [browser-alert])
                                          [box
                                           :src     (at)
                                           :padding "0px 0px 0px 50px"
                                           :child   [(:panel (item-for-id @selected-tab-id tabs-definition))]]]]]])))    ;; the tab panel to show, for the selected tab

(defn cumulative-sum-window [low high value-fn coll]
  (loop [coll       coll
         sum        0
         num-below  0 total-below  0 items-below  []
         num-within 0 total-within 0 items-within []
         num-above  0 total-above  0 items-above  []]
    (if (empty? coll)
      [num-below  total-below  items-below
       num-within total-within items-within
       num-above  total-above  items-above]
      (let [[i & remainder] coll
            value           (value-fn i)
            new-sum         (+ sum value)]
        (cond
          (< new-sum low)
          (recur remainder       new-sum
                 (inc num-below) (+ total-below value) (conj items-below i)
                 num-within      total-within          items-within
                 num-above       total-above           items-above)
          (<= low new-sum high)
          (recur remainder        new-sum
                 num-below        total-below            items-below
                 (inc num-within) (+ total-within value) (conj items-within i)
                 num-above        total-above            items-above)
          (> new-sum high)
          (recur remainder       new-sum
                 num-below       total-below           items-below
                 num-within      total-within          items-within
                 (inc num-above) (+ total-above value) (conj items-above i)))))))

(defn new-grid [{:keys [cell row-height column-seq row-seq row-heights column-width column-widths] :as props}]
  (let [cell-container-ref  (r/atom nil)
        cell-container-ref! (partial reset! cell-container-ref)
        scroll-top          (r/atom 0)
        scroll-left         (r/atom 0)
        container-height    (r/atom nil)
        container-width     (r/atom nil)
        container-right     (r/reaction (+ @scroll-left @container-width))
        container-bottom    (r/reaction (+ @scroll-top @container-height))
        on-scroll!          #(do (reset! scroll-top (.-scrollTop (.-target %)))
                                 (reset! scroll-left (.-scrollLeft (.-target %))))
        on-resize!          #(do (reset! container-height (.-height (.-contentRect (aget % 0))))
                                 (reset! container-width (.-width (.-contentRect (aget % 0)))))
        path-fn             vector
        size-fn             :cell-size
        column-v-margin     100
        row-v-margin        100
        left-bound          (r/reaction (max 0 (- @scroll-left column-v-margin)))
        right-bound         (r/reaction (+ @container-right column-v-margin))
        top-bound           (r/reaction (max 0 (- @scroll-top row-v-margin)))
        bottom-bound        (r/reaction (+ @container-bottom row-v-margin))
        column-window       (r/reaction (cumulative-sum-window @left-bound @right-bound size-fn (u/deref-or-value column-seq)))
        row-window          (r/reaction (cumulative-sum-window @top-bound @bottom-bound size-fn (u/deref-or-value row-seq)))]
    (r/create-class
     {:component-did-mount
      (fn [_]
        (.addEventListener @cell-container-ref "scroll" on-scroll!)
        (.observe (js/ResizeObserver. on-resize!) @cell-container-ref))
      :reagent-render
      (fn [{:keys [row-seq column-seq row-tree column-tree row-height column-width max-height max-width extra-height]}]

        (let [[column-num-left column-space-left columns-left
               column-num-within column-space-within columns-within
               column-num-right column-space-right columns-right] @column-window
              [row-num-top row-space-top rows-top
               row-num-within row-space-within rows-within
               row-num-bottom row-space-bottom rows-bottom]       @row-window
              grid-container                                      [:div {:ref   cell-container-ref!
                                                                         :style {:max-height            max-height
                                                                                 :max-width             max-width
                                                                                 :min-width             100
                                                                                 :min-height            100
                                                                                 :overflow              :auto
                                                                                 :width                 :fit-content
                                                                                 :display               :grid
                                                                                 :grid-template-columns (ng/grid-template (concat [column-space-left]
                                                                                                                                  (interleave (map path-fn columns-within)
                                                                                                                                              (map size-fn columns-within))
                                                                                                                                  [column-space-right]))
                                                                                 :grid-template-rows    (ng/grid-template (concat [row-space-top]
                                                                                                                                  (interleave (map path-fn rows-within)
                                                                                                                                              (map size-fn rows-within))
                                                                                                                                  [(+ row-space-bottom @extra-height)]))}}]]
          (into grid-container
                (for [column-path (map path-fn columns-within)
                      row-path    (map path-fn rows-within)
                      :let        [props {:row-path    row-path
                                          :column-path column-path}]]
                  ^{:key [column-path row-path]}
                  [cell props]))))})))

(def extra-height (r/atom 0))

(defn data-chunk [& {:keys [dimension index-offset size with-loader?] :or {size 10 with-loader? true}}]
  (for [chunk-index (range size)]
    (cond->
     {:index       (+ chunk-index index-offset)
      :chunk-index chunk-index
      :size        size
      :id          (gensym)
      :cell-size   (+ 25 (rand-int 75))
      :dimension   dimension}
      (and with-loader? (= chunk-index 0)) (assoc :loader? true))))

(def row-seq (r/atom '()))

(def rows-loaded (r/atom 0))

(defn load-row-chunk! [& {:keys [size] :or {size 10}}]
  (swap! row-seq concat (data-chunk {:size         size
                                     :index-offset @rows-loaded
                                     :dimension    :row}))
  (swap! rows-loaded + size))

(def column-seq (r/atom (data-chunk {:dimension :column :size 100})))

(defn test-cell [{:keys [row-path column-path]}]
  (let [{:keys           [loader?]
         row-index       :index
         row-size        :size
         row-chunk-index :chunk-index}    (peek row-path)
        {column-index       :index
         column-chunk-index :chunk-index} (peek column-path)
        loader?                           (and loader? (= 0 column-chunk-index))
        loaded?                           (r/reaction (pos? (- @rows-loaded row-index row-size)))
        background-color                  (r/atom "#cceeff")
        init-background!                  #(reset! background-color "#fff")]
    (r/create-class
     {:component-did-mount
      #(do (when (and loader? (not @loaded?))
             (load-row-chunk!))
           (init-background!))
      :reagent-render
      (fn [{:keys [children column-path row-path]}]
        [:div {:style {:grid-column      (ng/path->grid-line-name column-path)
                       :grid-row         (ng/path->grid-line-name row-path)
                       :padding          5
                       :font-size        10
                       :transition       "background-color 0.5s ease-in"
                       :background-color @background-color
                       :border           "thin solid black"
                       :border-top       (if (= 0 row-chunk-index)
                                           "thick solid black"
                                           "thin solid black")}}
         (str row-index " // " column-index)])})))

(defn test-main []
  [:<> [new-grid {:row-height   25
                  :column-width 100
                  :max-height   "80vh"
                  :max-width    "80vw"
                  :extra-height extra-height
                  :row-seq      row-seq
                  :column-seq   column-seq
                  :cell         test-cell}]
   [:div "rows loaded:" @rows-loaded]])

(defn ^:dev/after-load mount-root
  []
  (load-row-chunk!)
  (rdom/render [test-main] (get-element-by-id "app")))

(defn ^:export mount-demo
  []
  (mount-root))
