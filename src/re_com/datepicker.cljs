(ns re-com.datepicker
  (:require-macros
    [re-com.core          :refer [handler-fn at reflect-current-component]])
  (:require
    [reagent.core         :as reagent]
    [cljs-time.core       :as cljs-time]
    [re-com.config        :refer [include-args-desc?]]
    [re-com.validate      :refer [date-like? css-style? html-attr? parts?] :refer-macros [validate-args-macro]]
    [cljs-time.predicates :refer [sunday?]]
    [cljs-time.format     :refer [parse unparse formatters formatter]]
    [re-com.box           :refer [border gap box line h-box flex-child-style]]
    [re-com.util          :refer [deref-or-value now->utc]]
    [re-com.popover       :refer [popover-anchor-wrapper popover-content-wrapper]]
    [clojure.string       :as string])
  (:import
    [goog.i18n DateTimeFormat]))

;; Loosely based on ideas: https://github.com/dangrossman/bootstrap-daterangepicker

;; --- cljs-time facades ------------------------------------------------------

(def month-format (formatter "MMMM yyyy"))

(def week-format (formatter "ww"))

(def ^:const date-format-str "yyyy MMM dd")

(def ^:const date-format (formatter date-format-str))

(defn iso8601->date [iso8601]
  (when (seq iso8601)
    (parse (formatters :basic-date) iso8601)))

(defn- month-label [date-time {:keys [months] :as i18n}]
  (if months
    (str (nth months (dec (cljs-time/month date-time))) " " (unparse (formatter "yyyy") date-time))
    (unparse month-format date-time)))

(defn- dec-year [date-time] (cljs-time/minus date-time (cljs-time/years 1)))

(defn- dec-month [date-time] (cljs-time/minus date-time (cljs-time/months 1)))

(defn- inc-month [date-time] (cljs-time/plus date-time (cljs-time/months 1)))

(defn- inc-year [date-time] (cljs-time/plus date-time (cljs-time/years 1)))

(defn- inc-date [date-time n] (cljs-time/plus date-time (cljs-time/days n)))

(defn previous
  "If date fails pred, subtract period until true, otherwise answer date"
  ;; date   - a date object that satisfies cljs-time.core/DateTimeProtocol.
  ;;          If omitted, use now->utc, which returns a goog.date.UtcDateTime version of now with time removed.
  ;; pred   - can be one of cljs-time.predicate e.g. sunday? but any valid pred is supported.
  ;; period - a period which will be subtracted see cljs-time.core periods
  ;; Note:  If period and pred do not represent same granularity, some steps may be skipped
  ;         e.g Pass a Wed date, specify sunday? as pred and a period (days 2) will skip one Sunday.
  ([pred]
   (previous pred (now->utc)))
  ([pred date]
   (previous pred date (cljs-time/days 1)))
  ([pred date period]
   (if (pred date)
     date
     (recur pred (cljs-time/minus date period) period))))

(defn- =date [date1 date2]
  (and
    (= (cljs-time/year date1)  (cljs-time/year date2))
    (= (cljs-time/month date1) (cljs-time/month date2))
    (= (cljs-time/day date1)   (cljs-time/day date2))))

(defn- <=date [date1 date2]
  (or (=date date1 date2) (cljs-time/before? date1 date2)))

(defn- >=date [date1 date2]
  (or (=date date1 date2) (cljs-time/after? date1 date2)))

(def log-formatter (formatter "E dd MMM Y HH:mm:ss"))

(defn leap-year?
  [year]
  ;; A year is a leap year if it is...
  (not
    (or
      ;; Divisible by 4 (bitwise and 3), OR
      (pos? (bit-and year 3))
      ;; Divisible by 16 (bitwise and 15) AND
      ;; not divisible by 25.
      (and
        (pos? (bit-and year 15))
        (not (mod 25 year))))))

(defn first-weekday-of-year
  [week-day-at-start-of-week year]
  {:pre [(number? week-day-at-start-of-week)
         (number? year)]}
  (let [;; First, we find jan-1-date-time for the year.
        jan-1-date-time   (cljs-time/at-midnight (cljs-time/date-time year 1 1))
        ;; Second, we find the week-day number (0-6 re-com numbering, not 1-6 cljs-time numbering) of jan-1-date-time.
        jan-1-day-of-week (dec (cljs-time/day-of-week jan-1-date-time))
        ;; Third, we find the delta between the week-day-at-start-of-week that we are searching for, and the week-day of
        ;; jan-1-date-time.
        week-day-delta    (- jan-1-day-of-week week-day-at-start-of-week)
        ;; Forth, we use the delta to find the number of days-to-add to jan-1-date-time to reach the date-time of the
        ;; first week-day in that year matching week-day-at-start-of-week. The calculation is different depending on if
        ;; the delta is positive (week-day in that week is after Jan 1) or not (week day in that week is before, or on,
        ;; Jan 1).
        days-to-add (if (pos? week-day-delta)
                      (- 7 week-day-delta)
                      (js/Math.abs week-day-delta))]
      (cljs-time/plus jan-1-date-time (cljs-time/days days-to-add))))

(defn week-of-year
  [week-day-at-start-of-week start-of-week-date-time]
  (let [;; First, we must find the year at the end of the week. For example, the first row in January 2021 with Sunday
        ;; as the week-day-at-start-of-week is Sun 27 Dec 2020 to Sat 2 Jan 2021. The value for state-of-week-date-time
        ;; will be Sun 27 Dec 2020. Therefore, we would otherwise incorrectly calculate year as 2020 if we used the year
        ;; at the start of the week when we should be using 2021.
        end-of-week-date-time       (cljs-time/at-midnight (cljs-time/plus start-of-week-date-time (cljs-time/days 6)))
        year-at-end-of-week         (cljs-time/year end-of-week-date-time)
        ;; Second, we find the first weekday (e.g. Sun 3 January 2021) in the year-at-end-of-week that matches
        ;; week-day-at-start-of-week (e.g. Sun/6).
        -first-weekday-of-year      (first-weekday-of-year week-day-at-start-of-week year-at-end-of-week)
        ;; Third, if the start-of-week-date-time comes before the first-weekday-of-year, then we need to display the
        ;; week number as the final week of the previous year. It will be either 53 where the previous year is a leap
        ;; year (as 366 days make 52.29 weeks), or 52 where the previous year is a normal year. E.g. continuing the 2021
        ;; example it would be 53 as 2020 was a leap year.
        last-week-of-previous-year? (cljs-time/before? start-of-week-date-time -first-weekday-of-year)]
    (if last-week-of-previous-year?
      (let [previous-year               (dec year-at-end-of-week)
            previous-year-is-leap-year? (leap-year? previous-year)]
        (if previous-year-is-leap-year?
          53
          52))
      ;; Otherwise, calculate the difference in weeks between the ordinal day of the first-weekday-of-year and the
      ;; start-of-week-date-time, round it up to the nearest integer and add one.
      (let [ordinal-day               (.getDayOfYear ^js/goog.date.UtcDateTime start-of-week-date-time)
            first-weekday-ordinal-day (.getDayOfYear ^js/goog.date.UtcDateTime -first-weekday-of-year)
            difference-in-days        (- ordinal-day first-weekday-ordinal-day)
            difference-in-weeks       (js/Math.ceil (/ difference-in-days 7))]
        (inc difference-in-weeks)))))

(def ^:private days-vector
  [{:key :Mo :short-name "M" :name "MON"}
   {:key :Tu :short-name "T" :name "TUE"}
   {:key :We :short-name "W" :name "WED"}
   {:key :Th :short-name "T" :name "THU"}
   {:key :Fr :short-name "F" :name "FRI"}
   {:key :Sa :short-name "S" :name "SAT"}
   {:key :Su :short-name "S" :name "SUN"}])

(defn to-days-vector [xs] (mapv (fn [x m] (assoc m :name x)) xs days-vector))

(defn- rotate
  [n coll]
  (let [c (count coll)]
    (take c (drop (mod n c) (cycle coll)))))

(defn- is-day-pred [d]
  #(= (cljs-time/day-of-week %) (inc d)))

;; ----------------------------------------------------------------------------


(defn- main-div-with
  [table-div hide-border? class style attr parts src debug-as]
  ;;extra h-box is currently necessary so that calendar & border do not stretch to width of any containing v-box
  [h-box
   :src      src
   :debug-as debug-as
   :class    "rc-datepicker-wrapper"
   :children [[border
               :src    (at)
               :class  (str "rc-datepicker-border " (get-in parts [:border :class]))
               :style  (get-in parts [:border :style] {})
               :attr   (get-in parts [:border :attr] {})
               :radius "4px"
               :size   "none"
               :border (when hide-border? "none")
               :child  [:div
                        (merge
                          {:class (str "datepicker noselect rc-datepicker " class)
                           ;; override inherited body larger 14px font-size
                           ;; override position from css because we are inline
                           :style (merge {:font-size "13px"
                                          :position  "static"}
                                         style)}
                          attr)
                        table-div]]]])

(defn- prev-year-icon
  [& {:keys [parts]}]
  [:svg
   (merge {:class   (str "rc-datepicker-prev-year-icon " (get-in parts [:prev-year-icon :class]))
           :style   (get-in parts [:prev-year-icon :style])
           :height  "24"
           :viewBox "0 0 24 24"
           :width   "24"}
          (get-in parts [:prev-year-icon :attr]))
   [:g
    {:transform "translate(1.5)"}
    [:path {:d "m 16.793529,7.4382353 -1.41,-1.41 -5.9999996,5.9999997 5.9999996,6 1.41,-1.41 -4.58,-4.59 z"}]
    [:path {:d "m 10.862647,7.4429412 -1.4100003,-1.41 -6,5.9999998 6,6 1.4100003,-1.41 -4.5800003,-4.59 z"}]]])

(defn- prev-month-icon
  [& {:keys [parts]}]
  [:svg
   (merge {:class   (str "rc-datepicker-prev-month-icon " (get-in parts [:prev-month-icon :class]))
           :style   (get-in parts [:prev-month-icon :style])
           :height  "24"
           :viewBox "0 0 24 24"
           :width   "24"}
          (get-in parts [:prev-month-icon :attr]))
   [:path {:d    "M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12l4.58-4.59z"}]])

(defn- next-month-icon
  [& {:keys [parts]}]
  [:svg
   (merge {:class   (str "rc-datepicker-next-month-icon " (get-in parts [:next-month-icon :class]))
           :style   (get-in parts [:next-month-icon :style])
           :height  "24"
           :viewBox "0 0 24 24"
           :width   "24"}
          (get-in parts [:next-month-icon :attr]))
   [:path {:d    "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6-6-6z"}]])

(defn- next-year-icon
  [& {:keys [parts]}]
  [:svg
   (merge {:class   (str "rc-datepicker-next-year-icon " (get-in parts [:next-year-icon :class]))
           :style   (get-in parts [:next-year-icon :style])
           :height  "24"
           :viewBox "0 0 24 24"
           :width   "24"}
          (get-in parts [:next-year-icon :attr]))
   [:g
    {:transform "translate(-1.5)"}
    [:path {:d "m 8.5882353,6 -1.41,1.41 4.5799997,4.59 -4.5799997,4.59 1.41,1.41 5.9999997,-6 z"}]
    [:path {:d "m 14.547353,5.9623529 -1.41,1.41 4.58,4.5900001 -4.58,4.59 1.41,1.41 6,-6 z"}]]])

(defn- prev-year-nav
  [& {:keys [display-month minimum disabled? parts]}]
  (let [prev-year-date-time (dec-year @display-month)
        prev-year-enabled?  (if minimum (cljs-time/after? prev-year-date-time (dec-month minimum)) true)]
    (when (not disabled?)
      [:<>
       [box
        :src     (at)
        :class   (str (if prev-year-enabled? "rc-datepicker-selectable " "rc-datepicker-disabled ") "rc-datepicker-prev-year " (get-in parts [:prev-year :class]))
        :style   (get-in parts [:prev-year :style])
        :attr    (merge
                   {:on-click (handler-fn (when prev-year-enabled? (reset! display-month prev-year-date-time)))}
                   (get-in parts [:prev-year :attr]))
        :width   "20px"
        :align   :center
        :justify :center
        :child  [prev-year-icon
                 :parts parts]]
       [line
        :src (at)]])))

(defn- prev-month-nav
  [& {:keys [display-month minimum disabled? parts]}]
  (let [prev-month-date-time (dec-month @display-month)
        prev-month-enabled?  (if minimum (cljs-time/after? prev-month-date-time (dec-month minimum)) true)]
    (when (not disabled?)
      [:<>
       [box
        :src     (at)
        :class   (str (if prev-month-enabled? "rc-datepicker-selectable " "rc-datepicker-disabled ") "rc-datepicker-prev-month " (get-in parts [:prev-month :class]))
        :style   (get-in parts [:prev-month :style])
        :attr    (merge
                   {:on-click (handler-fn (when prev-month-enabled? (reset! display-month prev-month-date-time)))}
                   (get-in parts [:prev-month :attr]))
        :width   "20px"
        :align   :center
        :justify :center
        :child   [prev-month-icon
                  :parts parts]]
       [line
        :src (at)]])))

(defn- next-month-nav
  [& {:keys [display-month maximum disabled? parts]}]
  (let [next-month-date-time (inc-month @display-month)
        next-month-enabled?  (if maximum (cljs-time/before? next-month-date-time maximum) true)]
    (when (not disabled?)
      [:<>
       [line
        :src (at)]
       [box
        :src     (at)
        :class   (str (if next-month-enabled? "rc-datepicker-selectable " "rc-datepicker-disabled ") "rc-datepicker-next-month " (get-in parts [:next-month :class]))
        :style   (get-in parts [:next-month :style])
        :attr    (merge
                   {:on-click (handler-fn (when next-month-enabled? (reset! display-month next-month-date-time)))}
                   (get-in parts [:next-month :attr]))
        :align   :center
        :justify :center
        :width   "20px"
        :child   [next-month-icon
                  :parts parts]]])))

(defn- next-year-nav
  [& {:keys [display-month maximum disabled? parts]}]
  (let [next-year-date-time  (inc-year @display-month)
        next-year-enabled?   (if maximum (cljs-time/before? next-year-date-time maximum) true)]
    (when (not disabled?)
      [:<>
       [line
        :src (at)]
       [box
        :src     (at)
        :class   (str (if next-year-enabled? "rc-datepicker-selectable " "rc-datepicker-disabled ") "rc-datepicker-next-year " (get-in parts [:next-year :class]))
        :style   (get-in parts [:next-year :style])
        :attr    (merge
                   {:on-click (handler-fn (when next-year-enabled? (reset! display-month next-year-date-time)))}
                   (get-in parts [:next-year :attr]))
        :align   :center
        :justify :center
        :width   "20px"
        :child   [next-year-icon
                  :parts parts]]])))

(defn- nav
  [& {:keys [display-month minimum maximum disabled? i18n parts]}]
  (let [minimum (deref-or-value minimum)
        maximum (deref-or-value maximum)]
    [:th
     (merge
       {:col-span "7"
        :class    (str "rc-datepicker-nav " (get-in parts [:nav :class]))
        :style    (merge {:padding "0px"} (get-in parts [:nav :style]))}
       (get-in parts [:nav :attr]))
     [h-box
      :src      (at)
      :height   "100%"
      :children [[prev-year-nav
                  :display-month display-month
                  :minimum       minimum
                  :disabled?     disabled?
                  :parts         parts]
                 [prev-month-nav
                  :display-month display-month
                  :minimum       minimum
                  :disabled?     disabled?
                  :parts         parts]
                 [box
                  :src     (at)
                  :class   (str "rc-datepicker-month " (get-in parts [:month :class]))
                  :style   (get-in parts [:month :style])
                  :attr    (get-in parts [:month :attr])
                  :size    "1"
                  :align   :center
                  :justify :center
                  :child   (month-label @display-month i18n)]
                 [next-month-nav
                  :display-month display-month
                  :maximum       maximum
                  :disabled?     disabled?
                  :parts         parts]
                 [next-year-nav
                  :display-month display-month
                  :maximum       maximum
                  :disabled?     disabled?
                  :parts         parts]]]]))

(defn- week-days
  [& {:keys [start-of-week i18n parts]}]
  (let [{:keys [days]} i18n]
    (into
      [:<>]
      (for [day (rotate start-of-week (or (when days (to-days-vector days)) days-vector))]
        [:th
         (merge
           {:class (str "rc-datepicker-day rc-datepicker-day-" (string/lower-case (:name day)) " " (get-in parts [:day :class]))
            :style (get-in parts [:day :style] {})}
           (get-in parts [:day :attr]))
         (str (:name day))]))))

(defn- table-thead
  "Answer 2 x rows showing month with nav buttons and days"
  [display-month {:keys [show-weeks? minimum maximum start-of-week i18n]} disabled? parts]
  (let [template-row (if show-weeks? [:tr [:th]] [:tr])]
    [:thead
     (merge
       {:class (str "rc-datepicker-header " (get-in parts [:header :class]))
        :style (get-in parts [:header :style] {})}
       (get-in parts [:header :attr]))
     (conj template-row
           [nav
            :display-month display-month
            :minimum       minimum
            :maximum       maximum
            :disabled?     disabled?
            :i18n          i18n
            :parts         parts])
     (conj template-row
           [week-days
            :start-of-week start-of-week
            :i18n          i18n
            :parts         parts])]))

(defn- selection-changed
  [selection change-callback]
  (change-callback selection))


(defn- table-td
  [date focus-month selected today {minimum :minimum maximum :maximum selectable-fn :selectable-fn :as attributes} disabled? on-change parts]
  ;;following can be simplified and terse
  (let [minimum       (deref-or-value minimum)
        maximum       (deref-or-value maximum)
        enabled-min   (if minimum (>=date date minimum) true)
        enabled-max   (if maximum (<=date date maximum) true)
        enabled-day   (and enabled-min enabled-max)
        unselectable-day? (if enabled-day
                            (not (selectable-fn date))
                            true)
        classes       (cond disabled?                              "rc-datepicker-disabled"
                            unselectable-day?                      "rc-datepicker-unselectable"
                            (= focus-month (cljs-time/month date)) "rc-datepicker-selectable"
                            :else                                  "rc-datepicker-selectable rc-datepicker-out-of-focus")
        classes       (cond (and selected (=date selected date))           (str classes " rc-datepicker-selected start-date end-date ")
                            (and today (=date date today) (not disabled?)) (str classes " rc-datepicker-today ")
                            :else                                          (str classes " "))
        on-click      #(when-not (or disabled? unselectable-day?) (selection-changed date on-change))]
    [:td
     (merge
       {:class    (str classes "rc-datepicker-date " (get-in parts [:date :class]))
        :style    (get-in parts [:date :style])
        :on-click (handler-fn (on-click))}
       (get-in parts [:date :attr]))
     (cljs-time/day date)]))


(defn- week-td [start-of-week date]
  [:td {:class "week"} (week-of-year start-of-week date)])


(defn- table-tr
  "Return 7 columns of date cells from date inclusive"
  [date start-of-week focus-month selected attributes disabled? on-change parts]
;  {:pre [(sunday? date)]}
  (let [table-row (if (:show-weeks? attributes) [:tr (week-td start-of-week date)] [:tr])
        row-dates (map #(inc-date date %) (range 7))
        today     (when (:show-today? attributes) (now->utc))]
    (into table-row (map #(table-td % focus-month selected today attributes disabled? on-change parts) row-dates))))


(defn- table-tbody
  "Return matrix of 6 rows x 7 cols table cells representing 41 days from start-date inclusive"
  [display-month selected attributes disabled? on-change parts]
  (let [start-of-week   (:start-of-week attributes)
        current-start   (previous (is-day-pred start-of-week) display-month)
        focus-month     (cljs-time/month display-month)
        row-start-dates (map #(inc-date current-start (* 7 %)) (range 6))]
    (into [:tbody
           (merge
             {:class (str "rc-datepicker-dates " (get-in parts [:dates :class]))
              :style (get-in parts [:dates :style])}
             (get-in parts [:dates :attr]))]
          (map #(table-tr % start-of-week focus-month selected attributes disabled? on-change parts) row-start-dates))))


(defn- configure
  "Augment passed attributes with extra info/defaults"
  [attributes]
  (let [selectable-fn (if (-> attributes :selectable-fn fn?)
                        (:selectable-fn attributes)
                        (constantly true))]
    (merge attributes {:selectable-fn selectable-fn})))

(def datepicker-parts-desc
  (when include-args-desc?
    [{:name :wrapper         :level 0 :class "rc-datepicker-wrapper"         :impl "[datepicker]" :notes "Outer wrapper of the datepicker."}
     {:name :border          :level 1 :class "rc-datepicker-border"          :impl "[border]"     :notes "The datepicker border."}
     {:type :legacy          :level 2 :class "rc-datepicker"                 :impl "[:div]"       :notes "The datepicker container."}
     {:name :table           :level 3 :class "rc-datepicker-table"           :impl "[:table]"     :notes "The datepicker table."}
     {:name :header          :level 4 :class "rc-datepicker-header"          :impl "[:thead]"     :notes "The datepicker header."}
     {:type :legacy          :level 5                                        :impl "[:tr]"        :notes "The datepicker month row." :name-label "-"}
     {:name :nav             :level 6 :class "rc-datepicker-nav"             :impl "[:th]"        :notes "The datepicker navigation."}
     {:type :legacy          :level 7                                        :impl "[h-box]"      :name-label "-"}
     {:name :prev-year       :level 8 :class "rc-datepicker-prev-year"       :impl "[box]"        :notes "The datepicker previous year button."}
     {:name :prev-year-icon  :level 9 :class "rc-datepicker-prev-year-icon"  :impl "[:svg]"       :notes "The datepicker previous year button icon."}
     {:name :prev-month      :level 8 :class "rc-datepicker-prev-month"      :impl "[box]"        :notes "The datepicker previous month button."}
     {:name :prev-month-icon :level 9 :class "rc-datepicker-prev-month-icon" :impl "[:svg]"       :notes "The datepicker previous month button icon."}
     {:name :month           :level 8 :class "rc-datepicker-month"           :impl "[box]"        :notes "The datepicker month label."}
     {:name :next-month      :level 8 :class "rc-datepicker-next-month"      :impl "[box]"        :notes "The datepicker next month button."}
     {:name :next-month-icon :level 9 :class "rc-datepicker-next-month-icon" :impl "[:svg]"       :notes "The datepicker next month button icon."}
     {:name :next-year       :level 8 :class "rc-datepicker-next-year"       :impl "[box]"        :notes "The datepicker next year button."}
     {:name :next-year-icon  :level 9 :class "rc-datepicker-next-year-icon"  :impl "[:svg]"       :notes "The datepicker next year button icon."}
     {:type :legacy          :level 5                                        :impl "[:tr]"        :notes "The datepicker weekday row." :name-label "-"}
     {:name :day             :level 6 :class "rc-datepicker-day-mon"         :impl "[:th]"        :notes "Monday. WARNING: First weekday of week depends on arguments."}
     {:name :day             :level 6 :class "rc-datepicker-day-tue"         :impl "[:th]"        :notes "Tuesday."}
     {:name :day             :level 6 :class "rc-datepicker-day-wed"         :impl "[:th]"        :notes "Wednesday."}
     {:name :day             :level 6 :class "rc-datepicker-day-thu"         :impl "[:th]"        :notes "Thursday."}
     {:name :day             :level 6 :class "rc-datepicker-day-fri"         :impl "[:th]"        :notes "Friday."}
     {:name :day             :level 6 :class "rc-datepicker-day-sat"         :impl "[:th]"        :notes "Saturday."}
     {:name :day             :level 6 :class "rc-datepicker-day-sun"         :impl "[:th]"        :notes "Sunday."}
     {:name :dates           :level 4 :class "rc-datepicker-dates"           :impl "[:tbody]"     :notes "The table body containing the dates."}
     {:type :legacy          :level 5                                        :impl "[:tr]"        :notes "A date row. Repeats 6 times." :name-label "-"}
     {:name :date            :level 6 :class "rc-datepicker-date"            :impl "[:td]"        :notes "A date cell. Repeats 7 times per date row."}]))
   

(def datepicker-parts
  (when include-args-desc?
    (-> (map :name datepicker-parts-desc) set)))

(def datepicker-args-desc
  (when include-args-desc?
    [{:name :model          :required false                               :type "satisfies DateTimeProtocol | r/atom" :validate-fn date-like?                :description [:span "the selected date. If provided, should pass pred " [:code ":selectable-fn"] ". If not provided, (now->utc) will be used and the returned date will be a " [:code "goog.date.UtcDateTime"]]}
     {:name :on-change      :required true                                :type "satisfies DateTimeProtocol -> nil"   :validate-fn fn?                       :description [:span "called when a new selection is made. Returned type is the same as model (unless model is nil, in which case it will be " [:code "goog.date.UtcDateTime"] ")"]}
     {:name :disabled?      :required false  :default false               :type "boolean | atom"                                                             :description "when true, the user can't select dates but can navigate"}
     {:name :selectable-fn  :required false  :default "(fn [date] true)"  :type "function"                            :validate-fn fn?                       :description "This predicate function is called with one argument, the date. If it answers false, day will be shown disabled and can't be selected."}
     {:name :show-weeks?    :required false  :default false               :type "boolean"                                                                    :description "when true, week numbers are shown to the left"}
     {:name :show-today?    :required false  :default false               :type "boolean"                                                                    :description "when true, today's date is highlighted"}
     {:name :minimum        :required false                               :type "satisfies DateTimeProtocol | r/atom" :validate-fn date-like?                :description "no selection or navigation before this date"}
     {:name :maximum        :required false                               :type "satisfies DateTimeProtocol | r/atom" :validate-fn date-like?                :description "no selection or navigation after this date"}
     {:name :start-of-week  :required false  :default 6                   :type "int"                                                                        :description "first day of week (Monday = 0 ... Sunday = 6)"}
     {:name :hide-border?   :required false  :default false               :type "boolean"                                                                    :description "when true, the border is not displayed"}
     {:name :i18n           :required false                               :type "map"                                                                        :description [:span "internationalization map with optional keys " [:code ":days"] " and " [:code ":months"] " (both vectors of strings)"]}
     {:name :class          :required false                               :type "string"                              :validate-fn string?                   :description "CSS class names, space separated (applies to the outer border div, not the wrapping div)"}
     {:name :style          :required false                               :type "CSS style map"                       :validate-fn css-style?                :description "CSS styles to add or override (applies to the outer border div, not the wrapping div)"}
     {:name :attr           :required false                               :type "HTML attr map"                       :validate-fn html-attr?                :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] " allowed (applies to the outer border div, not the wrapping div)"]}
     {:name :parts          :required false                               :type "map"                                 :validate-fn (parts? datepicker-parts) :description "See Parts section below."}
     {:name :src            :required false                               :type "map"                                 :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as       :required false                               :type "map"                                 :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn datepicker
  [& {:keys [model] :as args}]
  (or
    (validate-args-macro datepicker-args-desc args)
    (let [external-model (reagent/atom (deref-or-value model))  ;; Set model type in stone on creation of this datepicker instance
          internal-model (reagent/atom @external-model)         ;; Holds the last known external value of model, to detect external model changes
          display-month  (reagent/atom (cljs-time/first-day-of-the-month (or @internal-model (now->utc))))]
      (fn datepicker-render
        [& {:keys [model on-change disabled? start-of-week hide-border? class style attr parts src debug-as]
            :or   {start-of-week 6} ;; Default to Sunday
            :as   args}]
        (or
          (validate-args-macro datepicker-args-desc args)
          (let [latest-ext-model    (deref-or-value model)
                disabled?           (deref-or-value disabled?)
                props-with-defaults (merge args {:start-of-week start-of-week})
                configuration       (configure props-with-defaults)]
            (when (not= @external-model latest-ext-model) ;; Has model changed externally?
              (reset! external-model latest-ext-model)
              (reset! internal-model latest-ext-model)
              (reset! display-month  (cljs-time/first-day-of-the-month (or @internal-model (now->utc)))))
            [main-div-with
             [:table
              (merge
                {:class (str "table-condensed rc-datepicker-table " (get-in parts [:table :class]))
                 :style (get-in parts [:table :style])}
                (get-in parts [:table :attr]))
              [table-thead display-month configuration disabled? parts]
              [table-tbody @display-month @internal-model configuration disabled? on-change parts]]
             hide-border?
             class
             style
             attr
             parts
             src
             (or debug-as (reflect-current-component))]))))))


(defn- anchor-button
  "Provide clickable field with current date label and dropdown button e.g. [ 2014 Sep 17 | # ]"
  [shown? model format goog? placeholder width disabled?]
  [:div {:class    "rc-datepicker-dropdown-anchor input-group display-flex noselect"
         :style    (flex-child-style "none")
         :on-click (handler-fn
                     (when (not (deref-or-value disabled?))
                       (swap! shown? not)))}
   [h-box
    :align     :center
    :class     "noselect"
    :min-width (when-not width "10em")
    :max-width (when-not width "10em")
    :width     width
    :children  [[:label {:disabled (deref-or-value disabled?)
                         :class    (str "form-control dropdown-button" (when (deref-or-value disabled?) " dropdown-button-disabled"))}
                 (cond (not (date-like? (deref-or-value model))) [:span {:style {:color "#bbb"}} placeholder]
                       goog?                                     (.format (DateTimeFormat. (if (seq format) format date-format-str)) (deref-or-value model))
                       :else                                     (unparse (if (seq format) (formatter format) date-format) (deref-or-value model)))]
                [:span
                 {:class (str "dropdown-button activator input-group-addon" (when (deref-or-value disabled?) " dropdown-button-disabled"))
                  :style {:padding "3px 0px 0px 0px"}}
                 [:i.zmdi.zmdi-apps {:style {:font-size "24px"}}]]]]])

(def datepicker-dropdown-args-desc
  (when include-args-desc?
    (conj datepicker-args-desc
          {:name :format          :required false  :default "yyyy MMM dd"  :type "string"   :description "[datepicker-dropdown only] a representation of a date format. See cljs_time.format"}
          {:name :goog?           :required false  :default false          :type "boolean"  :description [:span "[datepicker-dropdown only] use " [:code "goog.i18n.DateTimeFormat"] " instead of " [:code "cljs_time.format"] " for applying " [:code ":format"]]}
          {:name :no-clip?        :required false  :default true           :type "boolean"  :description "[datepicker-dropdown only] when an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped. When this parameter is true (which is the default), re-com will use a different CSS method to show the popover. This method is slightly inferior because the popover can't track the anchor if it is repositioned"}
          {:name :placeholder     :required false                          :type "string"   :description "[datepicker-dropdown only] placeholder text for when a date is not selected."}
          {:name :width           :required false  :validate-fn string?    :type "string"   :description "[datepicker-dropdown only] a CSS width style"}
          {:name :position-offset :required false  :validate-fn number?    :type "integer"  :description "[datepicker-dropdown only] px horizontal offset of the popup"})))

(defn datepicker-dropdown
  [& {:keys [src] :as args}]
  (or
    (validate-args-macro datepicker-dropdown-args-desc args)
    (let [shown?         (reagent/atom false)
          cancel-popover #(reset! shown? false)
          position       :below-left]
      (fn datepicker-dropdown-render
        [& {:keys [model show-weeks? on-change format goog? no-clip? placeholder width disabled? position-offset src debug-as]
            :or {no-clip? true, position-offset 0}
            :as passthrough-args}]
        (or
          (validate-args-macro datepicker-dropdown-args-desc passthrough-args)
          (let [collapse-on-select (fn [new-model]
                                     (reset! shown? false)
                                     (when on-change (on-change new-model)))                                                ;; wrap callback to collapse popover
                passthrough-args   (-> passthrough-args
                                       (dissoc :format :goog? :no-clip? :placeholder :width :position-offset)  ;; these keys only valid at this API level
                                       (assoc :on-change collapse-on-select)
                                       (assoc :src (at))
                                       (merge {:hide-border? true})                                                        ;; apply defaults
                                       vec
                                       flatten)]
            [popover-anchor-wrapper
             :src      src
             :debug-as (or debug-as (reflect-current-component))
             :class    "rc-datepicker-dropdown-wrapper"
             :showing? shown?
             :position position
             :anchor   [anchor-button shown? model format goog? placeholder width disabled?]
             :popover  [popover-content-wrapper
                        :src             (at)
                        :position-offset (+ (if show-weeks? 43 44) position-offset)
                        :no-clip?        no-clip?
                        :arrow-length    0
                        :arrow-width     0
                        :arrow-gap       3
                        :padding         "0px"
                        :on-cancel       cancel-popover
                        :body            (into [datepicker] passthrough-args)]]))))))
