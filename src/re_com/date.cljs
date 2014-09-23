;; Based on: https://github.com/dangrossman/bootstrap-daterangepicker
;; depends: daterangepicker-bs3.css

(ns re-com.date
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [cljs-time.core       :refer [now minus plus months days year month day day-of-week first-day-of-the-month
                                  before? after?]]
    [cljs-time.predicates :refer [sunday?]]
    [cljs-time.format     :refer [parse unparse formatters formatter]]
    [re-com.box           :refer [box border]]
    [re-com.util          :as    util]
    [clojure.string       :as    string]
    [reagent.core         :as    reagent]))

;; --- private cljs-time facades ----------------------------------------------
;; TODO: from day8date should be a common lib

(def ^:private month-format (formatter "MMM yyyy"))

(def ^:private week-format (formatter "ww"))

(defn- iso8601->date [iso8601]
  (when (seq iso8601)
    (parse (formatters :basic-date) iso8601)))

(defn- month-label [date] (unparse month-format date))

(defn- dec-month [date] (minus date (months 1)))

(defn- inc-month [date] (plus date (months 1)))

(defn- inc-date [date n] (plus date (days n)))

(defn- ->previous-sunday
  "If passed date is not a sunday, return the nearest previous sunday date"
  [date]
  (if (sunday? date)
    date
    (recur (minus date (days 1)))))

(defn- =date [date1 date2]
  ;; TODO: investigate why cljs-time/= and goog.date .equals etc don't work
  (and
    (= (year date1)  (year date2))
    (= (month date1) (month date2))
    (= (day date1)   (day date2))))

(defn- <=date [date1 date2]
  (or (=date date1 date2) (before? date1 date2)))

(defn- >=date [date1 date2]
  (or (=date date1 date2) (after? date1 date2)))

;; ----------------------------------------------------------------------------


(defn- main-div-with
  [table-div]
  ;;TODO: At some point add arg to optionaly turn off border
  [box
   :child [border
           :radius "4px"
           :child [:div
                   {:class "calendar-date daterangepicker opensleft show-calendar"
                           ;; override inherrited body larger 14px font-size
                           ;; override position from css because we are inline
                           :style {:font-size "13px"
                                       :position "static"
                                       :-webkit-user-select "none" ;; only good on webkit/chrome what do we do for firefox etc
                             }} table-div]]])


(defn- table-thead
  "Answer 2 x rows showing month with nav buttons and days NOTE: not internationalized"
  [current {show-weeks :show-weeks enabled-days :enabled-days minimum :minimum maximum :maximum}]
  ;;TODO: We might choose later to style by removing arrows altogether instead of greying when disabled navigation
  (let [style        (fn [week-day] {:class (if (enabled-days week-day) "selectable" "disabled")})
        prev-date    (dec-month @current)
        prev-enabled (if minimum (after? prev-date minimum) true)
        next-date    (inc-month @current)
        next-enabled (if maximum (before? next-date maximum) true)
        template-row (if show-weeks [:tr [:th]] [:tr])]
    [:thead
     (conj template-row
           [:th {:class (str "prev " (if prev-enabled "available selectable" "disabled"))}
            [:i {:class "fa fa-arrow-left icon-arrow-left glyphicon glyphicon-arrow-left"
                        :on-click #(when prev-enabled (reset! current prev-date))}]]
           [:th {:class "month" :col-span "5"} (month-label @current)]
           [:th {:class (str "next " (if next-enabled "available selectable" "disabled"))}
            [:i {:class "fa fa-arrow-right icon-arrow-right glyphicon glyphicon-arrow-right"
                        :on-click #(when next-enabled (reset! current next-date))}]])
     ;; could be done via more clever mapping but avoiding abscurity here.
     ;; style each day label based on if it is in enabled-days
     (conj template-row
           [:th (style 7) "Su"]
           [:th (style 1) "Mo"]
           [:th (style 2) "Tu"]
           [:th (style 3) "We"]
           [:th (style 4) "Th"]
           [:th (style 5) "Fr"]
           [:th (style 6) "Sa"])]))


(defn- selection-changed
  [selection change-callback]
  (change-callback selection))


(defn- table-td
  [date focus-month selected today {minimum :minimum maximum :maximum :as attributes} disabled on-change]
  ;;following can be simplified and terse
  (let [enabled-min  (if minimum (>=date date minimum) true)
        enabled-max  (if maximum (<=date date maximum) true)
        enabled-day  (and enabled-min enabled-max)
        disabled-day (if enabled-day
                       (nil? ((:enabled-days attributes) (day-of-week date)))
                       true)
        styles       (cond disabled
                           "off"

                           disabled-day
                           "off"

                           (= focus-month (month date))
                           "available"

                           :else
                           "available off")
        styles       (cond (=date selected date)
                           (str styles " active start-date end-date")

                           (and today (=date date today))
                           (str styles " today")

                           :else styles)
        on-click     #(when-not (or disabled disabled-day) (selection-changed date on-change))]
    [:td {:class styles :on-click on-click} (day date)]))


(defn- week-td [date]
  [:td {:class "week"} (unparse week-format date)])


(defn- table-tr
  "Return 7 columns of date cells from date inclusive."
  [date focus-month selected attributes disabled on-change]
  {:pre [(sunday? date)]}
  (let [table-row (if (:show-weeks attributes) [:tr (week-td date)] [:tr])
        row-dates (map #(inc-date date %) (range 7))
        today     (if (:show-today attributes) (:today attributes) nil)]
    (into table-row (map #(table-td % focus-month selected today attributes disabled on-change) row-dates))))


(defn- table-tbody
  "Return matrix of 6 rows x 7 cols table cells representing 41 days from start-date inclusive"
  [current selected attributes disabled on-change]
  {:pre [(and (seq (:enabled-days attributes)) ((:enabled-days attributes) (day-of-week selected)))]}
  (let [current-start   (->previous-sunday current)
        focus-month     (month current)
        row-start-dates (map #(inc-date current-start (* 7 %)) (range 6))]
    (into [:tbody] (map #(table-tr % focus-month selected attributes disabled on-change) row-start-dates))))


(defn- configuration
  "Augment passed attributes with extra info/defaults."
  [attributes]
  (let [enabled-days (->> (if (seq (:enabled-days attributes))
                            (:enabled-days attributes)
                            #{:Su :Mo :Tu :We :Th :Fr :Sa})
                          (map #(% {:Su 7 :Sa 6 :Fr 5 :Th 4 :We 3 :Tu 2 :Mo 1}))
                          set)
        minimum      (->> (:minimum attributes) iso8601->date)
        maximum      (->> (:maximum attributes) iso8601->date)]
    (merge attributes {:enabled-days enabled-days
                       :today (now)
                       :minimum minimum
                       :maximum maximum})))


(defn inline-date-picker
  [& {:keys [model attributes disabled on-change]}]
  ;;TODO: add attribute property handling for :minimum :maximum
  (let [current       (reagent/atom (first-day-of-the-month @model))
        configuration (configuration @attributes)]
    (fn []
      (main-div-with
        [:table {:class "table-condensed"}
         [table-thead current configuration]
         [table-tbody @current @model configuration @disabled on-change]]))))