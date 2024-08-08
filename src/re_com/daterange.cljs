(ns re-com.daterange
  (:require-macros
   [re-com.core       :refer [handler-fn at reflect-current-component]]) ;; TODO [GR-REMOVE] I like to have requires lined up (not everyone cares)
  (:require
   [reagent.core      :as r]
   [re-com.config     :refer [include-args-desc?]]
   [re-com.box        :refer [line border flex-child-style]]
   [re-com.core       :refer [at v-box h-box box popover-anchor-wrapper popover-content-wrapper]] ;; TODO [GR-REMOVE] Removed unused `:as re-com` (also we normally use `:as rc`)
   [re-com.validate   :refer [date-like? css-style? html-attr? parts?] :refer-macros [validate-args-macro]]
   [re-com.util       :refer [deref-or-value now->utc]]
   [cljs-time.format  :refer [parse unparse formatter]]
   [cljs-time.core    :as cljs-time]
   [goog.string       :refer [format]]);; TODO [GR-REMOVE] Removed unused `:as gstring`
  (:import
   [goog.i18n DateTimeFormat]))

(defn- dec-month  [date-time] (cljs-time/minus date-time (cljs-time/months 1)))
(defn- plus-month [date-time] (cljs-time/plus date-time (cljs-time/months 1)))
(defn- plus-day   [date-time] (cljs-time/plus date-time (cljs-time/days 1)))
(defn- dec-year   [date-time] (cljs-time/minus date-time (cljs-time/years 1)))
(defn- plus-year  [date-time] (cljs-time/plus date-time (cljs-time/years 1)))

;for internationalisation
(defn- month-label
  "Returns the appropriate month from the list on ordered months (likely not in english)"
  [date {:keys [months]}]
  (if months
    (->> date
         cljs-time/month
         dec
         (nth months)
         str)
    (unparse (formatter "MMMM") date)))

;;button icon svg's
(defn- prev-month-icon
  [parts]
  [:svg
   (merge {:class   (str "rc-daterange-nav-icon" (get-in parts [:prev-month-icon :class]))
           :style   (get-in parts [:prev-month-icon :style])
           :viewBox "0 0 24 24"}
          (get-in parts [:prev-month-icon :attr]))
   [:path {:d    "M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12l4.58-4.59z"}]])

(defn- prev-year-icon
  [parts]
  [:svg
   (merge {:class   (str "rc-daterange-nav-icon" (get-in parts [:prev-month-icon :class]))
           :style   (get-in parts [:prev-month-icon :style])
           :viewBox "0 0 24 24"}
          (get-in parts [:prev-year-icon :attr]))
   [:g
    {:transform "translate(1.5)"}
    [:path {:d "m 16.793529,7.4382353 -1.41,-1.41 -5.9999996,5.9999997 5.9999996,6 1.41,-1.41 -4.58,-4.59 z"}]
    [:path {:d "m 10.862647,7.4429412 -1.4100003,-1.41 -6,5.9999998 6,6 1.4100003,-1.41 -4.5800003,-4.59 z"}]]])

(defn- next-month-icon
  [parts]
  [:svg
   (merge {:class   (str "rc-daterange-nav-icon" (get-in parts [:prev-month-icon :class]))
           :style   (get-in parts [:prev-month-icon :style])
           :viewBox "0 0 24 24"}
          (get-in parts [:next-month-icon :attr]))
   [:path {:d    "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6-6-6z"}]])

(defn- next-year-icon
  [parts]
  [:svg
   (merge {:class   (str "rc-daterange-nav-icon" (get-in parts [:prev-month-icon :class]))
           :style   (get-in parts [:prev-month-icon :style])
           :viewBox "0 0 24 24"}
          (get-in parts [:next-year-icon :attr]))
   [:g
    {:transform "translate(-1.5)"}
    [:path {:d "m 8.5882353,6 -1.41,1.41 4.5799997,4.59 -4.5799997,4.59 1.41,1.41 5.9999997,-6 z"}]
    [:path {:d "m 14.547353,5.9623529 -1.41,1.41 4.58,4.5900001 -4.58,4.59 1.41,1.41 6,-6 z"}]]])

;;boxes containing icons, attr's should be added at this level
(defn- prev-year-nav [current-month-atom parts]
  (let [prev-year (dec-year (deref-or-value current-month-atom))]
    [box :src (at)
     :class (str "rc-daterange-nav-button " (get-in parts [:prev-year :class]))
     :style (get-in parts [:prev-year :style])
     :attr (merge
            {:on-click #(reset! current-month-atom prev-year)}
            (get-in parts [:prev-year :attr]))
     :child [prev-year-icon parts]]))

(defn- prev-month-nav [current-month-atom parts]
  (let [prev-month (dec-month (deref-or-value current-month-atom))]
    [box :src (at)
     :class (str "rc-daterange-nav-button " (get-in parts [:prev-month :class]))
     :style (get-in parts [:prev-month :style])
     :attr (merge
            {:on-click #(reset! current-month-atom prev-month)}
            (get-in parts [:prev-month :attr]))
     :child [prev-month-icon parts]]))

(defn- next-year-nav [current-month-atom parts]
  (let [next-year (plus-year (deref-or-value current-month-atom))]
    [box :src (at)
     :class (str "rc-daterange-nav-button " (get-in parts [:next-year :class]))
     :style (get-in parts [:next-year :style])
     :attr (merge
            {:on-click #(reset! current-month-atom next-year)}
            (get-in parts [:next-year :attr]))
     :child [next-year-icon parts]]))

(defn- next-month-nav [current-month-atom parts]
  (let [next-month (plus-month (deref-or-value current-month-atom))]
    [box :src (at)
     :class (str "rc-daterange-nav-button " (get-in parts [:next-month :class]))
     :style (get-in parts [:next-month :stlye])
     :attr (merge
            {:on-click #(reset! current-month-atom next-month)}
            (get-in parts [:next-month :attr]))
     :child [next-month-icon parts]]))

(defn- prev-nav [current-month-atom parts i18n]
  [h-box :src (at)
   :align-self :stretch
   :class (str "rc-daterange-prev-nav" (get-in parts [:prev-nav :class]))
   :style (get-in parts [:prev-nav :style])
   :attr (get-in parts [:prev-nav :attr])
   :children [[prev-year-nav current-month-atom parts]
              [line]
              [prev-month-nav current-month-atom parts]
              [h-box
               :size "auto"
               :justify :center
               :children [[box
                           :src (at)
                           :class (str "rc-daterange-month-title" (get-in parts [:month-title :class]))
                           :style (get-in parts [:month-title :style])
                           :attr (get-in parts [:month-title :attr])
                           :child (month-label (deref-or-value current-month-atom) i18n)]]]
              [box
               :align-self :end
               :justify :end
               :width "49px"
               :class (str "rc-daterange-year-title " (get-in parts [:year-title :class]))
               :style (get-in parts [:year-title :style])
               :attr (get-in parts [:year-title :attr])
               :child (str (unparse (formatter "YYYY") (deref-or-value current-month-atom)))]]])

(defn- next-nav [current-month-atom parts i18n]
  [h-box :src (at)
   :align-self :stretch
   :class (str "rc-daterange-next-nav" (get-in parts [:next-nav :class]))
   :style (get-in parts [:next-nav :style])
   :attr (get-in parts [:next-nav :attr])
   :children [[box
               :align-self :end
               :justify :start
               :width "49px"
               :class (str "rc-daterange-year-title " (get-in parts [:year-title :class]))
               :style (get-in parts [:year-title :style])
               :attr (get-in parts [:year-title :attr])
               :child (str (unparse (formatter "YYYY") (plus-month (deref-or-value current-month-atom))))]
              [h-box
               :size "auto"
               :justify :center
               :children [[box
                           :src (at)
                           :class (str "rc-daterange-month-title " (get-in parts [:month-title :class]))
                           :style (get-in parts [:month-title :style])
                           :attr (get-in parts [:month-title :attr])
                           :child (month-label (plus-month (deref-or-value current-month-atom)) i18n)]]]
              [next-month-nav current-month-atom parts]
              [line]
              [next-year-nav current-month-atom parts]]])

(defn- main-div-with ;; TODO [GR-REMOVE] Haven't updated it here but consider changing to kwargs so that calls are more self documenting
  "Main container to pass: class, style and attributes"
  [body hide-border? class style attr parts src debug-as]
  [h-box
   :src src
   :debug-as debug-as
   :size "none"
   :height "250px"
   :class "rc-daterange-wrapper"
   :children [[border
               :src (at)
               :class (str "rc-daterange-border noselect" (get-in parts [:border :class]))
               :style (get-in parts [:border :style])
               :attr (get-in parts [:border :attr])
               :radius "5px"
               :size "none"
               :border (when hide-border? "none")
               :child [:div
                       (merge {:class class
                               :style (merge {:font-size "13px"
                                              :position "static"}
                                             style)}
                              attr)
                       body]]]])

(defn- date-disabled?
  "Checks various things to see if a date had been disabled."
  [date [minimum maximum disabled? selectable-fn]]
  (let [too-early?   (when minimum (cljs-time/before? date (deref-or-value minimum))) ;; TODO: [GR-REMOVE] It's nice to have formatted let block (IntelliJ has a keyboard shortcut to do this - Highlight the block and press Ctrl+Alt+L)
        too-late?    (when maximum (cljs-time/after? date (deref-or-value maximum)))
        de-selected? (when selectable-fn (not (selectable-fn date)))]
    (or too-early? too-late? de-selected? disabled?)))

(defn- create-interval
  "inclusively creates a vector of date-formats from start to end."
  [start end]
  (let [first (deref-or-value start) ;; TODO: [GR-REMOVE] Format let block
        last  (deref-or-value end)]
    (loop [cur first result []]
      (if (cljs-time/after? cur last)
        result
        (recur (plus-day cur) (conj result cur))))))

(defn- interval-valid?
  "Returns true if all days are NOT disabled in some way."
  [start end disabled-data]
  (let [interval (create-interval start end)]
    (->> interval
         (map #(date-disabled? % disabled-data))
         (some identity)
         not)))

(defn- td-click-handler
  "Depending on the stage of the selection and if the new selected date is before the old start date, do different things"
  [day [fsm start-date end-date] on-change check-interval? disabled-data]
  (if
   (and (= @fsm "pick-end")                                                                   ;if we're picking and end date
        (or (cljs-time/before? @start-date day)
            (cljs-time/equal?  @start-date day))
        (if check-interval? ;; TODO [GR-REMOVE] Multi-line formatting more readable, especially when else clause is insignificant compared to if clause
          (interval-valid? start-date day disabled-data)
          true))
    (do
      (reset! fsm "pick-start")
      (reset! end-date day)                          ;update the internal end-date value
      (on-change {:start @start-date :end day}))     ;run the on-change function

    (do                               ;if we're picking a start date
      (reset! start-date day)
      (reset! end-date day)           ;set the end-date to the same date for view reasons
      (reset! fsm "pick-end"))))      ;we are next picking an end date

(defn- class-for-td
  "Given a date, and the values in the internal model, determine which css class the :td should have"
  [fsm day start-date end-date temp-end disabled? selectable-fn minimum maximum show-today?]
  (cond
    (and @start-date
         (not= @fsm "pick-end")
         (cljs-time/equal? day @start-date)
         (cljs-time/equal? day @end-date)) "daterange-start-end-td"
    (and @start-date (cljs-time/equal? day @start-date)) "daterange-start-td"
    (and @start-date (cljs-time/equal? day @end-date)) "daterange-end-td"
    (and @start-date (not= day "") (cljs-time/before? day @end-date) (cljs-time/after? day @start-date)) "daterange-interval-td"
    (when minimum (cljs-time/before? day (deref-or-value minimum))) "daterange-disabled-td"
    (when maximum (cljs-time/after? day (deref-or-value maximum))) "daterange-disabled-td"
    disabled? "daterange-disabled-td"
    (when selectable-fn (not (selectable-fn day))) "daterange-disabled-td"
    (and @start-date (not= day "") (cljs-time/equal? @end-date @start-date) (cljs-time/before? day (plus-day @temp-end)) (cljs-time/after? day @start-date)) "daterange-temp-td" ;changed to fix flashing
    (and show-today? (cljs-time/equal? day (now->utc))) "daterange-today"
    :default "daterange-default-td"))

(defn- create-day-td
  "Create table data elements with reactive classes and on click/hover handlers"
  [day [fsm start-date end-date temp-end] {:keys [on-change disabled? selectable-fn minimum maximum show-today? check-interval? parts] :as args}]
  (let [disabled-data (vector minimum maximum disabled? selectable-fn)]
    (if (= day "") ;; TODO [GR-REMOVE] Formatting
      [:td ""]
      (let [correct-class (class-for-td fsm day start-date end-date temp-end disabled? selectable-fn minimum maximum show-today?)
            clickable?    (not (date-disabled? day disabled-data))]
        (into [:td]
              (vector (merge {:class          (str "rc-daterange-td-basic " correct-class (get-in parts [:date :class]))
                              :style          (get-in parts [:date :style])
                              :on-click       #(when clickable? (td-click-handler day [fsm start-date end-date] on-change check-interval? disabled-data))
                              :on-mouse-enter #(reset! temp-end day)}
                             (get-in parts [:date :attr]))
                      (str (cljs-time/day day))))))))

(defn week-td [week-number]
  [:td {:class (str "daterange-td-basic " "daterange-week")} week-number])

(defn week-of-year-calc [days-list]
  (cljs-time/week-number-of-year (last days-list)))

(defn- create-week-tr
  "Given a list of days, create a table row with each :td referring to a different day"
  [days-list atoms {:keys [show-weeks?] :as args}]
  (let [week-of-year (week-of-year-calc days-list)]
    (into (if show-weeks? [:tr (week-td week-of-year)] [:tr])
          (for [day days-list]
            [create-day-td day atoms args]))))

;(defn- parse-date-from-ints  ;; TODO: [GR-REMOVE] This already exists as cljs-time/date-time - REMOVE THIS
;  "Given 3 ints, parse them as a useable date-format e.g. 11 2 2021"
;  [d m y]
;  (parse (formatter "ddMMYYYY") (str (format "%02d" d) (format "%02d" m) (str y))))

(defn- empty-days-count
  "Returns the number of empty date tiles at the start of the month based on the first day of the month and the chosen week start day, monday = 1 sunday = 7"
  [chosen start]
  (let [chosen (or chosen 1)] ;default week start of monday ;; TODO: Change from (if chosen chosen 1)
    (if (> chosen start)
      (- 7 (- chosen start))
      (- start chosen))))

(defn- days-for-month
  "Produces a partitioned list of date-formats with all the days in the given month, with leading empty strings to align with the days of the week"
  [date-from-month start-of-week]
  (let [month             (cljs-time/month date-from-month) ;; TODO: [GR-REMOVE] Format let block
        year              (cljs-time/year date-from-month)
        last-day-of-month (cljs-time/day (cljs-time/last-day-of-the-month date-from-month))
        first-day-val     (cljs-time/day-of-week (cljs-time/first-day-of-the-month date-from-month)) ;; 1 = mon  7 = sun
        day-ints          (range 1 (inc last-day-of-month)) ;; e.g. (1 2 3 ... 31)

        ;days              (map #(parse-date-from-ints % month year) day-ints) ;; turn into real date-times
        ;; TODO [GR-REMOVE] parse-date-from-ints is already implemented in cljs-time lib
        days              (map #(cljs-time/date-time year month %) day-ints) ;; turn into real date-times

        ;with-lead-emptys  (flatten (cons (repeat (empty-days-count start-of-week first-day-val) "") days)) ;; for padding the table
        ;; TODO [GR-REMOVE] I have replaced the above line with a generally more readable (and more easily edited) version (will let you look for other examples to update
        with-lead-emptys  (-> start-of-week ;; for padding the table
                              (empty-days-count first-day-val)
                              (repeat "")
                              (cons days)
                              flatten)]

    (partition-all 7 with-lead-emptys))) ;; split into lists of 7 to be passed to create-week-tr

(def days-vec [[:td "M"] [:td "Tu"] [:td "W"] [:td "Th"] [:td "F"] [:td "Sa"] [:td "Su"]]) ;for cycling and display depending on start-of-week

(defn- create-table
  "Given the result from days-for-month for a given month, create the :tbody using the relevant :tr and :td functions above"
  [date atoms {:keys [start-of-week i18n parts show-weeks?] :as args}]
  (let [into-tr            (if show-weeks? [:tr [:td]] [:tr]) ;; TODO: [GR-REMOVE] Format let block
        days-of-week       (if (:days i18n)
                             (map (fn [new-day [td _]] [td new-day]) (:days i18n) days-vec) ;update days vec with the changed days
                             days-vec)
        add-parts          (fn [[td day-string]]
                             (vector td
                                     (merge {:class (str "daterange-day-title" (get-in parts [:day-title :class]))
                                             :style (get-in parts [:day-title :style])}
                                            (get-in parts [:day-title :attr]))
                                     day-string))
        with-parts         (map #(add-parts %) days-of-week)
        table-row-weekdays (into into-tr (take 7 (drop (dec start-of-week) (cycle with-parts))))

        partitioned-days   (days-for-month date start-of-week)
        date-rows          (for [x partitioned-days]
                             [create-week-tr x atoms args])

        with-weekdays-row  (into [:tbody table-row-weekdays])
        with-dates         (into with-weekdays-row date-rows)]
    [:table
     (merge {:class (str "rc-daterange-table" (get-in parts [:table :class]))
             :style (get-in parts [:table :style])}
            (get-in parts [:table :attr]))
     with-dates]))

(defn- model-changed?
  "takes two date ranges and checks if they are different"
  [old latest]
  (not (and
        (nil? latest)
        (cljs-time/equal? (:start old) (:start latest))
        (cljs-time/equal? (:end old) (:end latest)))))

(defn model?
  "useless"
  [{:keys [start end]}]
  (and (date-like? start) (date-like? end))
  true)

;for validation and demo
(def daterange-parts-desc
  (when include-args-desc?
    [{:class "rc-daterange-wrapper",
      :impl "[date-range]",
      :level 0,
      :name :wrapper,
      :notes "Outer wrapper of the date-range picker."} ;seems this isn't a used
                                        ;accessor, even in
                                        ;datepicker?
     {:class "rc-daterange-border",
      :impl "[border]",
      :level 1,
      :name :border,
      :notes "The border."}
     {:class "rc-daterange",
      :impl "[:div]",
      :level 2,
      :name-label "-",
      :notes "The daterange container.",
      :type :legacy}
     {:impl "[h-box]",
      :level 3,
      :name-label "-",
      :notes "To display hozitonally.",
      :type :legacy}
     {:impl "[v-box]",
      :level 4,
      :name-label "-",
      :notes "To contain the left side of the display.",
      :type :legacy}
     {:class "rc-daterange-prev-nav",
      :impl "[h-box]",
      :level 5,
      :name :prev-nav,
      :notes "Contains navigation buttons and month/year."}
     {:class "rc-daterange-nav-button",
      :impl "[box]",
      :level 6,
      :name :prev-year,
      :notes "Previous year button."}
     {:class "rc-daterange-nav-icon",
      :impl "[:svg]",
      :level 7,
      :name :prev-year-icon,
      :notes "Previous year icon."}
     {:class "rc-daterange-nav-button",
      :impl "[box]",
      :level 6,
      :name :prev-month,
      :notes "Previous month button."}
     {:class "rc-daterange-nav-icon",
      :impl "[:svg]",
      :level 7,
      :name :prev-month-icon,
      :notes "Previous month icon."}
     {:class "rc-daterange-month-title",
      :impl "[box]",
      :level 6,
      :name :month-title,
      :notes "Month title for both sides."}
     {:class "rc-daterange-year-title",
      :impl "[box]",
      :level 6,
      :name :year-title,
      :notes "Year title for both sides."}
     {:class "rc-daterange-table",
      :impl "[:table]",
      :level 5,
      :name :table,
      :notes "Table."}
     {:impl "[:tr]",
      :level 6,
      :name-label "-",
      :notes "Row containing day titles.",
      :type :legacy}
     {:class "rc-daterange-day-title",
      :impl "[:td]",
      :level 7,
      :name :day-title,
      :notes "Titles for columns, days of the week"}
     {:class "rc-daterange-td-basic",
      :impl "[:td]",
      :level 7,
      :name :date,
      :notes "The date tiles populating the table."}
     {:impl "[v-box]",
      :level 4,
      :name-label "-",
      :notes "To contain the right side of the display.",
      :type :legacy}
     {:class "rc-daterange-next-nav",
      :impl "[h-box]",
      :level 5,
      :name :next-nav,
      :notes "Contains navigation buttons and month/year."}
     {:class "rc-daterange-nav-button",
      :impl "[box]",
      :level 6,
      :name :next-month,
      :notes "Next month button."}
     {:class "rc-daterange-nav-icon",
      :impl "[:svg]",
      :level 7,
      :name :next-month-icon,
      :notes "Next month icon."}
     {:class "rc-daterange-nav-button",
      :impl "[:box]",
      :level 6,
      :name :next-year,
      :notes "Next year button."}
     {:class "rc-daterange-nav-icon",
      :impl "[:svg]",
      :level 7,
      :name :next-year-icon,
      :notes "Next year icon."}]))

(def daterange-parts
  (when include-args-desc?
    (set (map :name daterange-parts-desc))))

(def daterange-args-desc
  "used to validate the arguments supplied by the user"
  (when include-args-desc?
    [{:name :model,
      :required false,
      :type "map with keys :start, :end | r/atom",
      :validate-fn model?,
      :description
      "the selected date range. Only updates after a selection has been completed. A closed (inclusive) interval. A map containing :start and :end whose values must both satisfy DateTimeProtocol. Nil is also acceptable if you want to start with nothing selected"}
     {:name :on-change,
      :required true,
      :type "satisfies DateTimeProtocol -> nil",
      :validate-fn fn?,
      :description "called when a new complete selection has been made"}
     {:name :disabled?,
      :required false,
      :default false,
      :type "boolean | atom",
      :description "when true, the user can't select dates but can navigate"}
     {:name :initial-display,
      :required false,
      :type "satisfies DateTimeProtocol | r/atom",
      :validate-fn date-like?,
      :description
      "set the months shown when no model is selected, defaults to the current month"}
     {:name :selectable-fn,
      :required false,
      :type "function",
      :validate-fn fn?,
      :description
      "called on each date, if it returns false, that date is not selectable"}
     {:name :show-today?,
      :required false,
      :default false,
      :type "boolean",
      :description "when true, todays date is highlighted"}
     {:name :minimum,
      :required false,
      :type "satisfies DateTimeProtocol | r/atom",
      :validate-fn date-like?,
      :description "no selection before this date"}
     {:name :maximum,
      :required false,
      :type "satisfies DateTimeProtocol | r/atom",
      :validate-fn date-like?,
      :description "no selection after this date"}
     {:name :check-interval?,
      :required false,
      :default false,
      :type "boolean",
      :description
      "if true, the user cannot select ranges which contain disabled days. If false, ranges spanning deselected or disabled dates are valid"}
     {:name :start-of-week,
      :required false,
      :default 1,
      :type "int",
      :validate-fn int?,
      :description
      "choose left most column of the table, 1 = monday ... 7 = sunday"}
     {:name :show-weeks?,
      :required false,
      :default false,
      :type "boolean",
      :description "when true, week numbers are shown to the left"}
     {:name :hide-border?,
      :required false,
      :type "boolean",
      :description "when true, the border is not displayed"}
     {:name :i18n,
      :required false,
      :type "map",
      :validate-fn map?,
      :description
      "internationalization map with optional keys :days and :months (both vectors of strings)"}
     {:name :class,
      :required false,
      :type "string",
      :validate-fn string?,
      :description
      "CSS class names, space separated (applies to the outer border div, not the wrapping div)"}
     {:name :style,
      :required false,
      :type "CSS style map",
      :validate-fn css-style?,
      :description
      "CSS styles to add or override (applies to the outer border div, not the wrapping div)"}
     {:name :attr,
      :required false,
      :type "HTML attribute map",
      :validate-fn html-attr?,
      :description
      "HTML attributes, like :on-mouse-move, No :class or :style allowed (applies to the outer border div, not the wrapping div)"}
     {:name :parts,
      :required false,
      :type "map",
      :validate-fn (parts? daterange-parts),
      :description "See Parts section below."}
     {:name :src,
      :required false,
      :type "map",
      :validate-fn map?,
      :description
      [:span
       "Used in dev builds to assist with debugging. Source code coordinates map containing keys"
       [:code ":file"] "and" [:code ":line"] ". See 'Debugging'."]}
     {:name :debug-as,
      :required false,
      :type "map",
      :validate-fn map?,
      :description
      [:span
       "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys"
       [:code ":component"] "and" [:code ":args"] "."]}]))

(defn daterange
  "Tracks the external model, but takes inputs into an internal model. The given on-change function is only called after a full selection has been made"
  [& {:keys [model initial-display] :as args}]
  (or
   (validate-args-macro daterange-args-desc args)
   (let [current-month (r/atom (or (deref-or-value initial-display) (:start (deref-or-value model)) (now->utc))) ;; TODO: [GR-REMOVE] Format let block
         fsm           (r/atom "pick-start")
         start-date    (r/atom (:start (deref-or-value model)))
         end-date      (r/atom (:end (deref-or-value model)))
         temp-end      (r/atom (or @end-date (now->utc)))]                 ;for :on-hover css functionality
     (fn render-fn
       [& {:keys [model hide-border? i18n class style attr parts src debug-as] :as args}]
       (or
        (validate-args-macro daterange-args-desc args) ;re validate args each time they change
        (let [latest-external-model   (deref-or-value model) ;; TODO: [GR-REMOVE] Format let block
              internal-model-refernce {:start @start-date :end @end-date}]
          (when (and (model-changed? latest-external-model internal-model-refernce) (= @fsm "pick-start"))
            (reset! start-date (:start latest-external-model))
            (reset! end-date (:end latest-external-model)))
          [main-div-with ;; TODO [GR-REMOVE] Haven't updated it here but consider changing to kwargs so that calls are more self documenting
           [h-box :src (at)
            :gap "60px"
            :padding "15px"
            :children [[v-box :src (at)
                        :gap "10px"
                        :children [[prev-nav current-month parts i18n]
                                   [create-table @current-month [fsm start-date end-date temp-end] args]]]
                       [v-box :src (at)
                        :gap "10px"
                        :children [[next-nav current-month parts i18n]
                                   [create-table (plus-month @current-month) [fsm start-date end-date temp-end] args]]]]]
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
  (let [format-str (or format "dd MMM, yyyy")]       ;; TODO [GR-REMOVE] Changed from (if format format "dd MMM, yyyy")
    [:div {:class    "rc-daterange-dropdown-anchor input-group display-flex noselect"
           :style    (flex-child-style "none")
           :on-click (handler-fn
                      (when (not (deref-or-value disabled?))
                        (swap! shown? not)))}
     [h-box
      :width     (or width "228px") ;; TODO [GR-REMOVE] Changed from (if width width "228px")
      :children  [[box
                   :size "auto"
                   :class (str "form-control dropdown-button" (when (deref-or-value disabled?) " dropdown-button-disabled"))
                   :style {#_#_:font-weight 600 :border-radius "5px 0px 0px 5px" :padding "3px 8px 0 8px"}
                   :child (cond
                            (not (date-like? (:start (deref-or-value model)))) (do
                                                                                 (prn (:start (deref-or-value model)))
                                                                                 [:span {:style {:color "#bbb"}} placeholder])
                            goog? (str
                                   (.format (DateTimeFormat. (if (seq format) format format-str)) (:start (deref-or-value model)))
                                   " - "
                                   (.format (DateTimeFormat. (if (seq format) format format-str)) (:end (deref-or-value model))))

                            :else (str
                                   (unparse (formatter format-str) (deref-or-value (:start (deref-or-value model))))
                                   " - "
                                   (unparse (formatter format-str) (deref-or-value (:end (deref-or-value model))))))]
                  [h-box
                   :justify :around
                   :class (str "dropdown-button activator input-group-addon" (when (deref-or-value disabled?) " dropdown-button-disabled"))
                   :style {:padding "3px 0px 0px 0px"
                           :width "30px"}
                   :children [[:i.zmdi.zmdi-apps {:style {:font-size "24px"}}]]]]]]))

(def daterange-dropdown-args-desc
  (when include-args-desc?
    (conj daterange-args-desc
          {:name :format          :required false  :default "dd MMM, yyyy" :type "string"   :description "[daterange-dropdown only] a representation of a date format. See cljs_time.format"}
          {:name :goog?           :required false  :default false          :type "boolean"  :description [:span "[daterange only] use " [:code "goog.i18n.DateTimeFormat"] " instead of " [:code "cljs_time.format"] " for applying " [:code ":format"]]}
          {:name :no-clip?        :required false  :default true           :type "boolean"  :description "[daterange-dropdown only] when an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped. When this parameter is true (which is the default), re-com will use a different CSS method to show the popover. This method is slightly inferior because the popover can't track the anchor if it is repositioned"}
          {:name :placeholder     :required false                          :type "string"   :description "[daterange-dropdown only] placeholder text for when a date is not selected."}
          {:name :width           :required false  :validate-fn string?    :type "string"   :description "[daterange-dropdown only] a CSS width style"}
          {:name :position-offset :required false  :validate-fn number?    :type "integer"  :description "[daterange-dropdown only] px horizontal offset of the popup"})))

(defn daterange-dropdown
  [& {:keys [src] :as args}]
  (or
   (validate-args-macro daterange-dropdown-args-desc args)
   (let [shown?         (r/atom false)
         cancel-popover #(reset! shown? false)
         position       :below-left]
     (fn render-fn
       [& {:keys [model show-weeks? on-change format goog? no-clip? placeholder width disabled? position-offset src debug-as initial-display]
           :or {no-clip? true, position-offset 0}
           :as passthrough-args}]
       (or
        (validate-args-macro daterange-dropdown-args-desc passthrough-args)
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
           :class    "rc-daterange-dropdown-wrapper"
           :showing? shown?
           :position position
           :anchor   [anchor-button shown? model format goog? placeholder width disabled?]
           :popover  [popover-content-wrapper
                      :src             (at)
                      :position-offset (+ (if show-weeks? 87 88) position-offset)
                      :no-clip?        no-clip?
                      :arrow-length    0
                      :arrow-width     0
                      :arrow-gap       3
                      :padding         "0px"
                      :on-cancel       cancel-popover
                      :body            (into [daterange] passthrough-args)]]))))))
