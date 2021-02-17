(ns re-demo.datepicker
  (:require-macros
    [reagent.ratom     :refer [reaction]]
    [re-com.debug      :refer [src-coordinates]])
  (:require
    [goog.date.Date]
    [reagent.core      :as    reagent]
    [cljs-time.core    :refer [today days minus plus day-of-week before?]]
    [cljs-time.coerce  :refer [to-local-date]]
    [cljs-time.format  :refer [formatter unparse]]
    [re-com.core       :refer [h-box v-box box gap single-dropdown datepicker datepicker-dropdown checkbox label title p button md-icon-button]]
    [re-com.datepicker :refer [iso8601->date datepicker-parts-desc datepicker-dropdown-args-desc]]
    [re-com.validate   :refer [date-like?]]
    [re-com.util       :refer [now->utc px]]
    [re-demo.utils     :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]])
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
   :src      (src-coordinates)
   :align    :center
   :children [[label
               :src   (src-coordinates)
               :style {:font-size "smaller"}
               :label (day days-map)]
              [checkbox
               :src       (src-coordinates)
               :model     (@enabled-days day)
               :on-change #(toggle-inclusion! enabled-days day)]]])

(defn- parameters-with
  "Toggle controls for some parameters."
  [content enabled-days as-days disabled? show-today? show-weeks? start-of-week-choices start-of-week]
  [v-box
   :gap      "15px"
   :align    :start
   :children [content
              [v-box
               :src   (src-coordinates)
               :gap   "10px"
               :style {:min-width        "550px"
                       :padding          "15px"
                       :border-top       "1px solid #DDD"
                       :background-color "#f7f7f7"}
               :children [[title
                           :src   (src-coordinates)
                           :style {:margin-top "0"}
                           :level :level3 :label "Interactive Parameters"]
                          [checkbox
                           :src       (src-coordinates)
                           :label     [box
                                       :src   (src-coordinates)
                                       :align :start
                                       :child [:code ":disabled?"]]
                           :model     disabled?
                           :on-change #(reset! disabled? %)]
                          [checkbox
                           :src       (src-coordinates)
                           :label     [box
                                       :src   (src-coordinates)
                                       :align :start
                                       :child [:code ":show-today?"]]
                           :model     show-today?
                           :on-change #(reset! show-today? %)]
                          [checkbox
                           :src       (src-coordinates)
                           :label     [box
                                       :src   (src-coordinates)
                                       :align :start
                                       :child [:code ":show-weeks?"]]
                           :model     show-weeks?
                           :on-change #(reset! show-weeks? %)]
                          [h-box
                           :gap      "5px"
                           :align    :end
                           :children [[:code ":start-of-week"]
                                      [single-dropdown
                                       :src      (src-coordinates)
                                       :choices   start-of-week-choices
                                       :model     start-of-week
                                       :on-change #(reset! start-of-week %)
                                       :width     "110px"]]]
                          [h-box
                           :src      (src-coordinates)
                           :gap      "2px"
                           :align    :end
                           :children [[box
                                       :src   (src-coordinates)
                                       :align :end
                                       :child [:code ":selectable-fn"]]
                                      [gap
                                       :src  (src-coordinates)
                                       :size "5px"]
                                      [checkbox-for-day :Su enabled-days]
                                      [checkbox-for-day :Mo enabled-days]
                                      [checkbox-for-day :Tu enabled-days]
                                      [checkbox-for-day :We enabled-days]
                                      [checkbox-for-day :Th enabled-days]
                                      [checkbox-for-day :Fr enabled-days]
                                      [checkbox-for-day :Sa enabled-days]
                                      [gap
                                       :src  (src-coordinates)
                                       :size "5px"]
                                      [box
                                       :src   (src-coordinates)
                                       :align :end
                                       :child [:code (str "(fn [d]\n (" @as-days " (.getDay d)))")]]]]]]]])




(defn- date->string
  [date]
  (if (date-like? date)
    (unparse (formatter "dd MMM, yyyy") date)
    "no date"))




(defn- show-variant
  [variation]
  (let [model1                (reagent/atom #_nil  #_(today)                    (now->utc))                      ;; Test 3 valid data types
        model2                (reagent/atom #_nil  #_(plus (today) (days 120))  (plus (now->utc) (days 120)))    ;; (today) = goog.date.Date, (now->utc) = goog.date.UtcDateTime
        model3                (reagent/atom nil)
        model4                (reagent/atom (today))
        disabled?             (reagent/atom false)
        show-today?           (reagent/atom true)
        show-weeks?           (reagent/atom false)
        start-of-week         (reagent/atom 6)
        start-of-week-right   (reagent/atom 0)
        start-of-week-choices [{:id 0 :label "Monday"}
                               {:id 1 :label "Tuesday"}
                               {:id 2 :label "Wednesday"}
                               {:id 3 :label "Thursday"}
                               {:id 4 :label "Friday"}
                               {:id 5 :label "Saturday"}
                               {:id 6 :label "Sunday"}]
        enabled-days          (reagent/atom (-> days-map keys set))
        as-days               (reaction (->> (map #(% {:Su 7 :Sa 6 :Fr 5 :Th 4 :We 3 :Tu 2 :Mo 1}) @enabled-days) (map #(if (= 7 %) 0 %)) sort set))
        selectable-pred       (fn [^js/goog.date.UtcDateTime date] (@as-days (.getDay date)))] ; Simply allow selection based on day of week.
    (case variation
      :inline [(fn inline-fn
                 []
                 [parameters-with
                  [v-box
                   :gap      "15px"
                   :children [[datepicker
                               :model         model1
                               :disabled?     disabled?
                               :show-today?   @show-today?
                               :show-weeks?   @show-weeks?
                               :selectable-fn selectable-pred
                               :start-of-week @start-of-week
                               :on-change     #(do #_(js/console.log "model1:" %) (reset! model1 %))]
                              [label :label [:span [:code ":model"] " is " (date->string @model1)]]
                              #_[h-box
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
                  enabled-days
                  as-days
                  disabled?
                  show-today?
                  show-weeks?
                  start-of-week-choices
                  start-of-week])]

      :dropdown [(fn dropdown-fn
                   []
                   [parameters-with
                    [v-box
                     :src      (src-coordinates)
                     :gap      "15px"
                     :children [[datepicker-dropdown
                                 :src           (src-coordinates)
                                 :model         model3
                                 :show-today?   @show-today?
                                 :show-weeks?   @show-weeks?
                                 :selectable-fn selectable-pred
                                 :start-of-week @start-of-week
                                 :placeholder   "Select a date"
                                 :format        "dd MMM, yyyy"
                                 :disabled?     disabled?
                                 :on-change     #(reset! model3 %)]
                                [label
                                 :src   (src-coordinates)
                                 :label [:span [:code ":model"] " is " (date->string @model3)]]]]
                    enabled-days
                    as-days
                    disabled?
                    show-today?
                    show-weeks?
                    start-of-week-choices
                    start-of-week])]
      :i18n [(fn i18n-fn
               []
               (set! (.-DateTimeSymbols goog.i18n) DateTimeSymbols_pl)
               [parameters-with
                [v-box
                 :src      (src-coordinates)
                 :gap      "15px"
                 :children [[datepicker-dropdown
                             :src             (src-coordinates)
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
                             :on-change       #(reset! model4 %)]
                            [label
                             :src   (src-coordinates)
                             :label [:span [:code ":model"] " is " (date->string @model4)]]]]
                enabled-days
                as-days
                disabled?
                show-today?
                show-weeks?
                start-of-week-choices
                start-of-week])])))


(def variations ^:private
  [{:id :inline       :label "Inline"}
   {:id :dropdown     :label "Dropdown"}
   {:id :i18n         :label "I18n"}])

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
                   :src      (src-coordinates)
                   :gap      "100px"
                   :children [[v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "[datepicker ... ] & [datepicker-dropdown ... ]" {:font-size "24px"}]
                                          [status-text "Stable"]
                                          [p "An inline or popover date picker component."]
                                          [args-table datepicker-dropdown-args-desc]]]
                              [v-box
                               :src      (src-coordinates)
                               :gap       "20px"
                               :size      "auto"
                               :children  [[title2 "Demo"]
                                           [h-box
                                            :src      (src-coordinates)
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label
                                                        :src   (src-coordinates)
                                                        :label "Select a demo"]
                                                       [single-dropdown
                                                        :src      (src-coordinates)
                                                        :choices   variations
                                                        :model     selected-variation
                                                        :width     "200px"
                                                        :on-change #(reset! selected-variation %)]]]
                                           [show-variant @selected-variation]]]]]
                  [parts-table "datepicker" datepicker-parts-desc]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [datepicker-examples])
