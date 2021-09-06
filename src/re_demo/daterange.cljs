(ns re-demo.daterange
  (:require-macros
   [reagent.ratom     :refer [reaction]]
   [re-com.core       :refer []])
  (:require
   [goog.date.Date]
   [reagent.core      :as    reagent]
   [cljs-time.core    :refer [today days minus plus day-of-week before?]]
   [cljs-time.coerce  :refer [to-local-date]]
   [cljs-time.format  :refer [formatter unparse]]
   [re-com.core       :refer [at h-box v-box box gap single-dropdown datepicker datepicker-dropdown checkbox label title p button md-icon-button checkbox]]
   [re-com.datepicker :refer [iso8601->date datepicker-parts-desc datepicker-dropdown-args-desc]]
   [re-com.daterange  :as daterange :refer [daterange daterange-args-desc daterange-parts-desc daterange-dropdown-args-desc daterange-dropdown]]
   [re-com.validate   :refer [date-like?]]
   [re-com.util       :refer [now->utc px]]
   [cljs-time.core :as cljs-time]
   [re-demo.utils     :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]])
  (:import
   [goog.i18n DateTimeSymbols_pl]))

(def week-start-choices
  [{:id 1 :label "Monday"}
   {:id 2 :label "Tuesday"}
   {:id 3 :label "Wednesday"}
   {:id 4 :label "Thursday"}
   {:id 5 :label "Friday"}
   {:id 6 :label "Saturday"}
   {:id 7 :label "Sunday"}])

(defn create-checkbox [atom day]
  [v-box
   :align :center 
   :children [[box :style {:font-size "smaller"} :child day]
              [checkbox
               :model ((keyword day) @atom)
               :on-change #(swap! atom update-in [(keyword day)] not)]]])

(defn holder []
  (let [dropdown-model (reagent/atom nil)
        model-atom (reagent/atom nil)
        today-model (reagent/atom false)
        disabled-model (reagent/atom false)
        weeks-model (reagent/atom false)
        interval-model (reagent/atom false)
        week-start-model (reagent/atom 2)
        selected-days (reagent/atom {:M true :Tu true :W true :Th true :Fr true :Sa true :Su true}) ;model for all checkboxes
        valid? (fn [day] (nth (mapv val @selected-days) (dec (cljs-time/day-of-week day))))] ;convert to vector, check if day should be disabled
    (fn []
      [v-box 
       :gap "10px"
       :children [[panel-title "[daterange ...]"
                   "src/re_com/datepicker.cljs"
                   "src/re_demo/datepicker.cljs"]
                  [h-box
                   :gap "100px"
                   :children [[v-box
                               :gap "10px"
                               :width "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Alpha, written by an intern"]
                                          [p "A date range picker component."]
                                          [args-table daterange-dropdown-args-desc]]]
                              [v-box
                               :gap "15px"
                               :children [[title2 "Demo"]
                                          [daterange
                                           :show-today? @today-model
                                           :disabled? @disabled-model
                                           :show-weeks? @weeks-model
                                           :check-interval? @interval-model
                                           :model model-atom
                                           :selectable-fn valid?
                                           :start-of-week @week-start-model
                                           :on-change #(reset! model-atom %)]
                                          [h-box
                                           :align :center
                                           :children [[:code ":model"]
                                                      [box :child (str " is "
                                                                       (if @model-atom (str
                                                                                        (unparse (formatter "dd MMM, yyyy") (:start @model-atom)) " ... "
                                                                                        (unparse (formatter "dd MMM, yyyy") (:end @model-atom))) "nil"))]]]
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
                                                      [checkbox :src (at)
                                                       :model disabled-model
                                                       :on-change #(swap! disabled-model not)
                                                       :label [box :child [:code ":disabled?"]]]
                                                      [checkbox :src (at)
                                                       :model today-model
                                                       :on-change #(swap! today-model not)
                                                       :label [box :child [:code ":show-today?"]]]
                                                      [checkbox :src (at)
                                                       :model weeks-model
                                                       :on-change #(swap! weeks-model not)
                                                       :label [box :child [:code ":show-weeks?"]]]
                                                      [h-box
                                                       :gap "5px"
                                                       :align :end
                                                       :children [[box :child [:code ":start-of-week"]]
                                                                  [single-dropdown
                                                                   :width "110px"
                                                                   :choices week-start-choices
                                                                   :model week-start-model
                                                                   :on-change #(reset! week-start-model %)]]]
                                                      [h-box
                                                       :gap "5px"
                                                       :align :end
                                                       :children [[box :child [:code ":selectable-fn"]]
                                                                  [create-checkbox selected-days "M"]
                                                                  [create-checkbox selected-days "Tu"]
                                                                  [create-checkbox selected-days "W"]
                                                                  [create-checkbox selected-days "Th"]
                                                                  [create-checkbox selected-days "Fr"]
                                                                  [create-checkbox selected-days "Sa"]
                                                                  [create-checkbox selected-days "Su"]]]
                                                      [gap :size "5px"]
                                                      [checkbox
                                                       :model interval-model
                                                       :on-change #(swap! interval-model not)
                                                       :label [box :child [:code "check-interval?"]]]]]
                                          [v-box
                                           :align :start
                                           :gap "10px"
                                           :children [[title
                                                       :src   (at)
                                                       :level :level3 :label "Dropdown"]
                                                      [box :src (at)
                                                       :child "Attached to the same model and interactive paramters."]
                                                      [daterange-dropdown
                                                       :show-today? @today-model
                                                       :disabled? @disabled-model
                                                       :show-weeks? @weeks-model
                                                       :check-interval? @interval-model
                                                       :model model-atom
                                                       :selectable-fn valid?
                                                       :start-of-week @week-start-model
                                                       :on-change #(reset! model-atom %)
                                                       :placeholder "Select a range of dates"]]]]]]]
                  [parts-table "daterange" daterange-parts-desc]]])))





(defn panel
  []
  [holder])