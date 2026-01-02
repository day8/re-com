(ns re-demo.datepicker
  (:require-macros
   [reagent.ratom     :refer [reaction]]
   [re-com.core       :refer [handler-fn]])
  (:require
   [goog.date.Date]
   [reagent.core      :as    reagent]
   [cljs-time.core    :refer [today days minus plus day-of-week before?]]
   [cljs-time.coerce  :refer [to-local-date]]
   [cljs-time.format  :refer [formatter unparse]]
   [re-com.core       :refer [at h-box v-box box gap single-dropdown datepicker datepicker-dropdown checkbox label title p button md-icon-button]]
   [re-com.datepicker :refer [iso8601->date datepicker-parts-desc datepicker-dropdown-args-desc]]
   [re-com.validate   :refer [date-like? css-class?]]
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
   :src      (at)
   :align    :center
   :children [[label
               :src   (at)
               :style {:font-size "smaller"}
               :label (day days-map)]
              [checkbox
               :src       (at)
               :model     (@enabled-days day)
               :on-change #(toggle-inclusion! enabled-days day)]]])

(defn- parameters-with
  "Toggle controls for some parameters."
  [content enabled-days as-days disabled? show-today? show-weeks? start-of-week-choices start-of-week custom-parts-model custom-theme-model]
  [v-box :src (at)
   :gap      "15px"
   :align    :start
   :children [content
              [v-box
               :src   (at)
               :gap   "10px"
               :style {:min-width        "550px"
                       :padding          "15px"
                       :border-top       "1px solid #DDD"
                       :background-color "#f7f7f7"}
               :children [[title
                           :src   (at)
                           :style {:margin-top "0"}
                           :level :level3 :label "Interactive Parameters"]
                          [checkbox
                           :src       (at)
                           :label     [box
                                       :src   (at)
                                       :align :start
                                       :child [:code ":disabled?"]]
                           :model     disabled?
                           :on-change #(reset! disabled? %)]
                          [checkbox
                           :src       (at)
                           :label     [box
                                       :src   (at)
                                       :align :start
                                       :child [:code ":show-today?"]]
                           :model     show-today?
                           :on-change #(reset! show-today? %)]
                          [checkbox
                           :src       (at)
                           :label     [box
                                       :src   (at)
                                       :align :start
                                       :child [:code ":show-weeks?"]]
                           :model     show-weeks?
                           :on-change #(reset! show-weeks? %)]
                          [checkbox
                           :src       (at)
                           :model     custom-parts-model
                           :on-change #(swap! custom-parts-model not)
                           :label     [box :child [:code ":parts (custom styling)"]]]
                          [checkbox
                           :src       (at)
                           :model     custom-theme-model
                           :on-change #(swap! custom-theme-model not)
                           :label     [box :child [:code ":theme (custom styling)"]]]
                          [h-box :src (at)
                           :gap      "5px"
                           :align    :end
                           :children [[:code ":start-of-week"]
                                      [single-dropdown
                                       :src      (at)
                                       :choices   start-of-week-choices
                                       :model     start-of-week
                                       :on-change #(reset! start-of-week %)
                                       :width     "110px"]]]
                          [h-box
                           :src      (at)
                           :gap      "2px"
                           :align    :end
                           :children [[box
                                       :src   (at)
                                       :align :end
                                       :child [:code ":selectable-fn"]]
                                      [gap
                                       :src  (at)
                                       :size "5px"]
                                      [checkbox-for-day :Su enabled-days]
                                      [checkbox-for-day :Mo enabled-days]
                                      [checkbox-for-day :Tu enabled-days]
                                      [checkbox-for-day :We enabled-days]
                                      [checkbox-for-day :Th enabled-days]
                                      [checkbox-for-day :Fr enabled-days]
                                      [checkbox-for-day :Sa enabled-days]
                                      [gap
                                       :src  (at)
                                       :size "5px"]
                                      [box
                                       :src   (at)
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
        selectable-pred       (fn [^js/goog.date.UtcDateTime date] (@as-days (.getDay date)))
        custom-parts-model    (reagent/atom false)
        custom-theme-model    (reagent/atom false)
        custom-theme          (fn [props]
                                (if-not @custom-theme-model
                                  props
                                  (let [styles (case (:part props)
                                                 :re-com.datepicker/border
                                                 {:border-color "#9C27B0"
                                                  :border-width "3px"
                                                  :box-shadow   "0 4px 12px rgba(156, 39, 176, 0.3)"}

                                                 :re-com.datepicker/container
                                                 {:background-color "#F3E5F5"}

                                                 :re-com.datepicker/nav
                                                 {:background-color "#E1BEE7"
                                                  :padding          "8px"}

                                                 (:re-com.datepicker/prev-year
                                                  :re-com.datepicker/prev-month
                                                  :re-com.datepicker/next-month
                                                  :re-com.datepicker/next-year)
                                                 {:background-color "#BA68C8"
                                                  :border-radius    "8px"
                                                  :padding          "4px"}

                                                 :re-com.datepicker/month
                                                 {:color       "#6A1B9A"
                                                  :font-weight "600"
                                                  :font-size   "14px"}

                                                 :re-com.datepicker/date
                                                 {:color       "#7B1FA2"
                                                  :font-weight "500"}

                                                 nil)]
                                    (update props :style merge styles))))]
    (case variation
      :inline [(fn inline-fn
                 []
                 [parameters-with
                  [v-box :src (at)
                   :gap      "15px"
                   :children [[datepicker
                               :src           (at)
                               :date-cell     (fn [{:keys [label date selectable? disabled? class style attr on-change]}]
                                                [:td
                                                 (-> {:class    class
                                                      :style    style
                                                      :on-click (when (and selectable? (not disabled?))
                                                                  (handler-fn (on-change date)))}
                                                     (merge attr))
                                                 label])
                               :model         model1
                               :disabled?     disabled?
                               :show-today?   @show-today?
                               :show-weeks?   @show-weeks?
                               :selectable-fn selectable-pred
                               :start-of-week @start-of-week
                               :on-change     #(do #_(js/console.log "model1:" %) (reset! model1 %))
                               :theme         custom-theme
                               :parts         (when @custom-parts-model
                                                {:border      {:style {:border-color "#4A90E2"
                                                                       :border-width "3px"
                                                                       :box-shadow   "0 2px 8px rgba(74, 144, 226, 0.2)"}}
                                                 :container   {:style {:background-color "#F8F9FA"}}
                                                 :nav         {:style {:background-color "#E3F2FD"
                                                                       :padding          "8px"}}
                                                 :prev-year   {:style {:background-color "#64B5F6"
                                                                       :border-radius    "4px"}}
                                                 :prev-month  {:style {:background-color "#64B5F6"
                                                                       :border-radius    "4px"}}
                                                 :month       {:style {:color       "#1976D2"
                                                                       :font-weight "600"}}
                                                 :next-month  {:style {:background-color "#64B5F6"
                                                                       :border-radius    "4px"}}
                                                 :next-year   {:style {:background-color "#64B5F6"
                                                                       :border-radius    "4px"}}
                                                 :date        {:style {:color       "#1976D2"
                                                                       :font-weight "500"}}})]
                              [label :src (at) :label [:span [:code ":model"] " is " (date->string @model1)]]
                              #_[h-box :src (at)
                                 :gap      "6px"
                                 :margin   "10px 0px 0px 0px"
                                 :align    :center
                                 :children [[label :src (at) :style label-style :label "Change model:"]
                                            [md-icon-button :src (at)
                                             :md-icon-name "zmdi-arrow-left"
                                             :size         :smaller
                                             :disabled?    (not (date-like? @model1))
                                             :on-click     #(when (date-like? @model1)
                                                              (reset! model1 (minus @model1 (days 1))))]
                                            [md-icon-button :src (at)
                                             :md-icon-name "zmdi-arrow-right"
                                             :size         :smaller
                                             :disabled?    (if (and (date-like? @model1) (date-like? @model2))
                                                             (not (before? (to-local-date @model1)
                                                                           (to-local-date @model2)))
                                                             true)
                                             :on-click     #(when (date-like? @model1)
                                                              (reset! model1 (plus @model1 (days 1))))]
                                            [button :src (at)
                                             :label    "Reset"
                                             :class    "btn btn-default"
                                             :style    {:padding "1px 4px"}
                                             :on-click #(reset! model1 nil)]]]]]
                  enabled-days
                  as-days
                  disabled?
                  show-today?
                  show-weeks?
                  start-of-week-choices
                  start-of-week
                  custom-parts-model
                  custom-theme-model])]

      :dropdown [(fn dropdown-fn
                   []
                   [parameters-with
                    [v-box
                     :src      (at)
                     :gap      "15px"
                     :children [[datepicker-dropdown
                                 :src           (at)
                                 :model         model3
                                 :show-today?   @show-today?
                                 :show-weeks?   @show-weeks?
                                 :selectable-fn selectable-pred
                                 :start-of-week @start-of-week
                                 :placeholder   "Select a date"
                                 :format        "dd MMM, yyyy"
                                 :disabled?     disabled?
                                 :on-change     #(reset! model3 %)
                                 :theme         custom-theme
                                 :parts         (when @custom-parts-model
                                                  {:border      {:style {:border-color "#4A90E2"
                                                                         :border-width "3px"
                                                                         :box-shadow   "0 2px 8px rgba(74, 144, 226, 0.2)"}}
                                                   :container   {:style {:background-color "#F8F9FA"}}
                                                   :nav         {:style {:background-color "#E3F2FD"
                                                                         :padding          "8px"}}
                                                   :prev-year   {:style {:background-color "#64B5F6"
                                                                         :border-radius    "4px"}}
                                                   :prev-month  {:style {:background-color "#64B5F6"
                                                                         :border-radius    "4px"}}
                                                   :month       {:style {:color       "#1976D2"
                                                                         :font-weight "600"}}
                                                   :next-month  {:style {:background-color "#64B5F6"
                                                                         :border-radius    "4px"}}
                                                   :next-year   {:style {:background-color "#64B5F6"
                                                                         :border-radius    "4px"}}
                                                   :date        {:style {:color       "#1976D2"
                                                                         :font-weight "500"}}})]
                                [label
                                 :src   (at)
                                 :label [:span [:code ":model"] " is " (date->string @model3)]]]]
                    enabled-days
                    as-days
                    disabled?
                    show-today?
                    show-weeks?
                    start-of-week-choices
                    start-of-week
                    custom-parts-model
                    custom-theme-model])]
      :i18n     [(fn i18n-fn
                   []
                   (set! (.-DateTimeSymbols goog.i18n) DateTimeSymbols_pl)
                   [parameters-with
                    [v-box
                     :src      (at)
                     :gap      "15px"
                     :children [[datepicker-dropdown
                                 :src             (at)
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
                                 :on-change       #(reset! model4 %)
                                 :theme           custom-theme
                                 :parts           (when @custom-parts-model
                                                    {:border      {:style {:border-color "#4A90E2"
                                                                           :border-width "3px"
                                                                           :box-shadow   "0 2px 8px rgba(74, 144, 226, 0.2)"}}
                                                     :container   {:style {:background-color "#F8F9FA"}}
                                                     :nav         {:style {:background-color "#E3F2FD"
                                                                           :padding          "8px"}}
                                                     :prev-year   {:style {:background-color "#64B5F6"
                                                                           :border-radius    "4px"}}
                                                     :prev-month  {:style {:background-color "#64B5F6"
                                                                           :border-radius    "4px"}}
                                                     :month       {:style {:color       "#1976D2"
                                                                           :font-weight "600"}}
                                                     :next-month  {:style {:background-color "#64B5F6"
                                                                           :border-radius    "4px"}}
                                                     :next-year   {:style {:background-color "#64B5F6"
                                                                           :border-radius    "4px"}}
                                                     :date        {:style {:color       "#1976D2"
                                                                           :font-weight "500"}}})]
                                [label
                                 :src   (at)
                                 :label [:span [:code ":model"] " is " (date->string @model4)]]]]
                    enabled-days
                    as-days
                    disabled?
                    show-today?
                    show-weeks?
                    start-of-week-choices
                    start-of-week
                    custom-parts-model
                    custom-theme-model])])))

(def variations ^:private
  [{:id :inline       :label "Inline"}
   {:id :dropdown     :label "Dropdown"}
   {:id :i18n         :label "I18n"}])

(defn datepicker-examples
  []
  (let [selected-variation (reagent/atom :inline)]
    (fn examples-fn []
      [v-box :src (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "Date Components"
                   "src/re_com/datepicker.cljs"
                   "demo/re_demo/datepicker.cljs"]
                  [h-box
                   :src      (at)
                   :gap      "100px"
                   :children [[v-box
                               :src      (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "[datepicker ... ] & [datepicker-dropdown ... ]" {:font-size "24px"}]
                                          [status-text "Stable"]
                                          [p "An inline or popover date picker component."]
                                          [args-table datepicker-dropdown-args-desc]]]
                              [v-box
                               :src      (at)
                               :gap       "20px"
                               :size      "auto"
                               :children  [[title2 "Demo"]
                                           [h-box
                                            :src      (at)
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label
                                                        :src   (at)
                                                        :label "Select a demo"]
                                                       [single-dropdown
                                                        :src      (at)
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
