(ns re-demo.datepicker
  (:require
    [goog.date.Date]
    [reagent.core      :as    reagent]
    [reagent.ratom     :refer-macros [reaction]]
    [cljs-time.core    :refer [today days minus plus day-of-week before?]]
    [cljs-time.coerce  :refer [to-local-date]]
    [cljs-time.format  :refer [formatter unparse]]
    [re-com.core       :refer [h-box v-box box gap single-dropdown datepicker datepicker-dropdown checkbox label title p button md-icon-button]]
    [re-com.datepicker :refer [iso8601->date datepicker-dropdown-args-desc]]
    [re-com.validate   :refer [date-like?]]
    [re-com.util       :refer [now->utc px]]
    [re-demo.utils     :refer [panel-title title2 title3 args-table github-hyperlink status-text]])
  (:import
    [goog.i18n DateTimeSymbols_pl]))


(def ^:private days-map
     {:Su "S" :Mo "M" :Tu "T" :We "W" :Th "T" :Fr "F" :Sa "S"})


(defn- toggle-inclusion!
  "convenience function to include/exclude member from"
  [set-atom member]
  (reset! set-atom
          (if (contains? @set-atom member)
            (disj @set-atom member)
            (conj @set-atom member))))

(defn- checkbox-for-day
  [day enabled-days]
  [v-box
   :align    :center
   :children [[label
               :style {:font-size "smaller"}
               :label (day days-map)]
              [checkbox
               :model     (@enabled-days day)
               :on-change #(toggle-inclusion! enabled-days day)]]])

(defn- parameters-with
  "Toggle controls for some parameters."
  [content enabled-days disabled? show-today? show-weeks? start-of-week start-of-week-choices]
  [v-box
   :gap      "15px"
   :align    :start
   :children [[gap :size "20px"]
              [title :level :level3 :label "Parameters"]
              [h-box
               :gap      "20px"
               :align    :start
               :children [[checkbox
                           :label     [box :align :start :child [:code ":disabled?"]]
                           :model     disabled?
                           :on-change #(reset! disabled? %)]
                          [checkbox
                           :label     [box :align :start :child [:code ":show-today?"]]
                           :model     show-today?
                           :on-change #(reset! show-today? %)]
                          [checkbox
                           :label     [box :align :start :child [:code ":show-weeks?"]]
                           :model     show-weeks?
                           :on-change #(reset! show-weeks? %)]]]
              [h-box
               :gap      "2px"
               :align    :center
               :children [[checkbox-for-day :Su enabled-days]
                          [checkbox-for-day :Mo enabled-days]
                          [checkbox-for-day :Tu enabled-days]
                          [checkbox-for-day :We enabled-days]
                          [checkbox-for-day :Th enabled-days]
                          [checkbox-for-day :Fr enabled-days]
                          [checkbox-for-day :Sa enabled-days]
                          [gap :size "5px"]
                          [box :align :start :child [:code ":selectable-fn"]]]]
              [:span [:code "e.g. (fn [date] (#{1 2 3 4 5 6 7} (day-of-week date)))"]]
              [h-box
               :gap      "5px"
               :align    :center
               :children [[single-dropdown
                           :choices   start-of-week-choices
                           :model     start-of-week
                           :on-change #(reset! start-of-week %)
                           :width     "110px"]
                          [:span " as " [:code ":start-of-week"]]]]
              content]])


(defn- date->string
  [date]
  (if (date-like? date)
    (unparse (formatter "dd MMM, yyyy") date)
    "no date"))


(defn- show-variant
  [variation]
  (let [model1          (reagent/atom #_nil  #_(today)                    (now->utc))                      ;; Test 3 valid data types
        model2          (reagent/atom #_nil  #_(plus (today) (days 120))  (plus (now->utc) (days 120)))    ;; (today) = goog.date.Date, (now->utc) = goog.date.UtcDateTime
        model3          (reagent/atom nil)
        model4          (reagent/atom (today))
        disabled?       (reagent/atom false)
        show-today?     (reagent/atom true)
        show-weeks?     (reagent/atom false)
        start-of-week   (reagent/atom 6)
        start-of-week-choices [{:id 0 :label "Monday"}
                               {:id 1 :label "Tuesday"}
                               {:id 2 :label "Wednesday"}
                               {:id 3 :label "Thursday"}
                               {:id 4 :label "Friday"}
                               {:id 5 :label "Saturday"}
                               {:id 6 :label "Sunday"}]
        enabled-days    (reagent/atom (-> days-map keys set))
        as-days         (reaction (-> (map #(% {:Su 7 :Sa 6 :Fr 5 :Th 4 :We 3 :Tu 2 :Mo 1}) @enabled-days) set))
        selectable-pred (fn [date] (@as-days (day-of-week date))) ; Simply allow selection based on day of week.
        label-style     {:font-style "italic" :font-size "smaller" :color "#777"}]
    (case variation
      :inline [(fn inline-fn
                 []
                 [parameters-with
                  [h-box
                   :gap      "20px"
                   :align    :start
                   :children [[v-box
                               :gap      "5px"
                               :children [[label
                                           :style label-style
                                           :label [:span " :maximum - " (date->string @model2) [:br] ":start-of-week - " (reduce
                                                                                                                           (fn [_ {:keys [id label]}]
                                                                                                                             (if (= id @start-of-week)
                                                                                                                               (reduced label)
                                                                                                                               nil))
                                                                                                                           nil
                                                                                                                           start-of-week-choices)]]
                                          [datepicker
                                           :model         model1
                                           :maximum       model2
                                           :disabled?     disabled?
                                           :show-today?   @show-today?
                                           :show-weeks?   @show-weeks?
                                           :selectable-fn selectable-pred
                                           :start-of-week @start-of-week
                                           :on-change     #(do #_(js/console.log "model1:" %) (reset! model1 %))]
                                          [label :style label-style :label (str "selected: " (date->string @model1))]
                                          [h-box
                                           :gap      "6px"
                                           :margin   "10px 0px 0px 0px"
                                           :align    :center
                                           :children [[label :style label-style :label "Change model:"]
                                                      [md-icon-button
                                                       :md-icon-name "zmdi-arrow-left"
                                                       :size         :smaller
                                                       :disabled?    (not (date-like? @model1))
                                                       :on-click     #(when (date-like? @model1)
                                                                        (reset! model1 (minus @model1 (days 1))))]
                                                      [md-icon-button
                                                       :md-icon-name "zmdi-arrow-right"
                                                       :size         :smaller
                                                       :disabled?    (if (and (date-like? @model1) (date-like? @model2))
                                                                       (not (before? (to-local-date @model1)
                                                                                     (to-local-date @model2)))
                                                                       true)
                                                       :on-click     #(when (date-like? @model1)
                                                                        (reset! model1 (plus @model1 (days 1))))]
                                                      [button
                                                       :label    "Reset"
                                                       :class    "btn btn-default"
                                                       :style    {:padding  "1px 4px"}
                                                       :on-click #(reset! model1 nil)]]]]]

                              [v-box
                               :gap      "5px"
                               :children [[label
                                           :style label-style
                                           :label [:span ":minimum - " (date->string @model1) [:br] ":start-of-week - Monday"]]
                                          [datepicker
                                           :start-of-week 0
                                           :model         model2
                                           :minimum       model1
                                           :show-today?   @show-today?
                                           :show-weeks?   @show-weeks?
                                           :selectable-fn selectable-pred
                                           :disabled?     disabled?
                                           :on-change     #(do #_(js/console.log "model2" %) (reset! model2 %))]
                                          [label :style label-style :label (str "selected: " (date->string @model2))]]]]]
                  enabled-days
                  disabled?
                  show-today?
                  show-weeks?
                  start-of-week
                  start-of-week-choices])]
      :dropdown [(fn dropdown-fn
                   []
                   [parameters-with
                    [h-box
                     :size     "auto"
                     :align    :start
                     :children [[gap :size "120px"]
                                [datepicker-dropdown
                                 :model         model3
                                 :show-today?   @show-today?
                                 :show-weeks?   @show-weeks?
                                 :selectable-fn selectable-pred
                                 :start-of-week @start-of-week
                                 :placeholder   "Select a date"
                                 :format        "dd MMM, yyyy"
                                 :disabled?     disabled?
                                 :on-change     #(reset! model3 %)]]]
                    enabled-days
                    disabled?
                    show-today?
                    show-weeks?
                    start-of-week
                    start-of-week-choices])]
      :i18n [(fn i18n-fn
               []
               (set! (.-DateTimeSymbols goog.i18n) DateTimeSymbols_pl)
               [parameters-with
                [box
                 :margin     "30px 0 0 0"
                 :align-self :start
                 :child      [datepicker-dropdown
                              :model           model4
                              :show-today?     @show-today?
                              :show-weeks?     @show-weeks?
                              :selectable-fn   selectable-pred
                              :start-of-week   @start-of-week
                              :placeholder     "Wybierz datę"
                              :format          "d MMMM yyyy"
                              :disabled?       disabled?
                              :goog?           true
                              :i18n            {:days   ["PON" "WT" "ŚR" "CZW" "PT" "SOB" "ND"]
                                                :months ["Styczeń" "Luty" "Marzec" "Kwiecień" "Maj" "Czerwiec" "Lipiec" "Sierpień" "Wrzesień" "Październik" "Listopad" "Grudzień"]}
                              :width           "190px"
                              :position-offset 25
                              :on-change       #(reset! model4 %)]]
                enabled-days
                disabled?
                show-today?
                show-weeks?
                start-of-week
                start-of-week-choices])])))


(def variations ^:private
  [{:id :inline   :label "Inline"}
   {:id :dropdown :label "Dropdown"}
   {:id :i18n     :label "I18n"}])


(defn datepicker-component-hierarchy
  []
  (let [indent          20
        table-style     {:style {:border "2px solid lightgrey" :margin-right "10px"}}
        border          {:border "1px solid lightgrey" :padding "6px 12px"}
        border-style    {:style border}
        border-style-nw {:style (merge border {:white-space "nowrap"})}
        valign          {:vertical-align "top"}
        valign-style    {:style valign}
        valign-style-hd {:style (merge valign {:background-color "#e8e8e8"})}
        indent-text     (fn [level text] [:span {:style {:padding-left (px (* level indent))}} text])
        highlight-text  (fn [text & [color]] [:span {:style {:font-weight "bold" :color (or color "dodgerblue")}} text])
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[datepicker\n"
                      "   ...\n"
                      "   :parts {:prev {:class \"blah\"\n"
                      "                  :style { ... }\n"
                      "                  :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[datepicker]")]
                   [:td border-style-nw "rc-datepicker-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the datepicker."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:border]")]
                   [:td border-style-nw "rc-datepicker-border"]
                   [:td border-style-nw (code-text ":border")]
                   [:td border-style "The datepicker border."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:div]")]
                   [:td border-style-nw "rc-datepicker"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The datepicker container."]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[:table]")]
                   [:td border-style-nw "rc-datepicker-table"]
                   [:td border-style-nw (code-text ":table")]
                   [:td border-style "The table."]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[:thead]")]
                   [:td border-style-nw "rc-datepicker-header"]
                   [:td border-style-nw (code-text ":header")]
                   [:td border-style "The table header."]]
                  [:tr
                   [:td border-style-nw (indent-text 5 "[:tr]")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style "The month row."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-prev"]
                   [:td border-style-nw (code-text ":prev")]
                   [:td border-style "The previous month button."]]
                  [:tr
                   [:td border-style-nw (indent-text 7 "[:i]")]
                   [:td border-style-nw "rc-datepicker-prev-icon"]
                   [:td border-style-nw (code-text ":prev-icon")]
                   [:td border-style "The previous month button icon."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-month"]
                   [:td border-style-nw (code-text ":month")]
                   [:td border-style "The month."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-next"]
                   [:td border-style-nw (code-text ":next")]
                   [:td border-style "The next month button."]]
                  [:tr
                   [:td border-style-nw (indent-text 7 "[:i]")]
                   [:td border-style-nw "rc-datepicker-next-icon"]
                   [:td border-style-nw (code-text ":next-icon")]
                   [:td border-style "The next month button icon."]]
                  [:tr
                   [:td border-style-nw (indent-text 5 "[:tr]")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style "The day row."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-day rc-datepicker-day-sun"]
                   [:td border-style-nw (code-text ":day")]
                   [:td border-style "Sun. WARNING: First day of week and naming of days depends on parameters incl i18n."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-day rc-datepicker-day-mon"]
                   [:td border-style-nw (code-text ":day")]
                   [:td border-style "Mon."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-day rc-datepicker-day-tue"]
                   [:td border-style-nw (code-text ":day")]
                   [:td border-style "Tue."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-day rc-datepicker-day-wed"]
                   [:td border-style-nw (code-text ":day")]
                   [:td border-style "Wed."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-day rc-datepicker-day-thu"]
                   [:td border-style-nw (code-text ":day")]
                   [:td border-style "Thu."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-day rc-datepicker-day-fri"]
                   [:td border-style-nw (code-text ":day")]
                   [:td border-style "Fri."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:th]")]
                   [:td border-style-nw "rc-datepicker-day rc-datepicker-day-sat"]
                   [:td border-style-nw (code-text ":day")]
                   [:td border-style "Sat."]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[:tbody]")]
                   [:td border-style-nw "rc-datepicker-dates"]
                   [:td border-style-nw (code-text ":dates")]
                   [:td border-style "The table body containing the dates."]]
                  [:tr
                   [:td border-style-nw (indent-text 5 "[:tr]")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style "A date row. Repeats 6 times."]]
                  [:tr
                   [:td border-style-nw (indent-text 6 "[:td]")]
                   [:td border-style-nw "rc-datepicker-date"]
                   [:td border-style-nw (code-text ":date")]
                   [:td border-style "A date cell. Repeats 7 times per date row."]]]]]]))

(defn datepicker-examples
  []
  (let [selected-variation (reagent/atom :inline)]
    (fn examples-fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "Date Components"
                               "src/re_com/datepicker.cljs"
                               "src/re_demo/datepicker.cljs"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "[datepicker ... ] & [datepicker-dropdown ... ]" {:font-size "24px"}]
                                          [status-text "Stable"]
                                          [p "An inline or popover date picker component."]
                                          [args-table datepicker-dropdown-args-desc]]]
                              [v-box
                               :gap       "10px"
                               :size      "auto"
                               :children  [[title2 "Demo"]
                                           [h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a demo"]
                                                       [single-dropdown
                                                        :choices   variations
                                                        :model     selected-variation
                                                        :width     "200px"
                                                        :on-change #(reset! selected-variation %)]]]
                                           [show-variant @selected-variation]]]]]
                  [datepicker-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [datepicker-examples])
