(ns re-com.daterange
  (:require-macros
   [re-com.core       :refer [handler-fn at reflect-current-component]]
   [re-com.validate   :refer [validate-args-macro]])
  (:require
   re-com.daterange.theme
   [reagent.core      :as r]
   [re-com.dropdown   :as dd]
   [re-com.args       :as args]
   [re-com.config     :refer [include-args-desc?]]
   [re-com.box        :refer [line border v-box h-box box]]
   [re-com.daterange  :as-alias dr]
   [re-com.debug      :as debug]
   [re-com.part       :as part]
   [re-com.theme      :as theme]
   [re-com.theme.util :as tu]
   [re-com.validate   :refer [date-like?]]
   [re-com.util       :refer [deref-or-value now->utc]]
   [cljs-time.format  :refer [unparse formatter]]
   [cljs-time.core    :as cljs-time])
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

(defn- prev-year-nav [current-month-atom part theme re-com-ctx]
  (let [prev-year (dec-year (deref-or-value current-month-atom))]
    (part ::dr/prev-year
      {:impl  box
       :theme theme
       :props {:re-com re-com-ctx
               :attr   {:on-click (handler-fn (reset! current-month-atom prev-year))}
               :child  (part ::dr/prev-year-icon
                         {:theme theme
                          :props {:re-com   re-com-ctx
                                  :tag      :svg
                                  :attr     {:viewBox "0 0 24 24"}
                                  :children [[:g
                                              {:transform "translate(1.5)"}
                                              [:path {:d "m 16.793529,7.4382353 -1.41,-1.41 -5.9999996,5.9999997 5.9999996,6 1.41,-1.41 -4.58,-4.59 z"}]
                                              [:path {:d "m 10.862647,7.4429412 -1.4100003,-1.41 -6,5.9999998 6,6 1.4100003,-1.41 -4.5800003,-4.59 z"}]]]}})}})))

(defn- prev-month-nav [current-month-atom part theme re-com-ctx]
  (let [prev-month (dec-month (deref-or-value current-month-atom))]
    (part ::dr/prev-month
      {:impl  box
       :theme theme
       :props {:re-com re-com-ctx
               :attr   {:on-click (handler-fn (reset! current-month-atom prev-month))}
               :child  (part ::dr/prev-month-icon
                         {:theme theme
                          :props {:re-com   re-com-ctx
                                  :tag      :svg
                                  :attr     {:viewBox "0 0 24 24"}
                                  :children [[:path {:d "M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12l4.58-4.59z"}]]}})}})))

(defn- next-year-nav [current-month-atom part theme re-com-ctx]
  (let [next-year (plus-year (deref-or-value current-month-atom))]
    (part ::dr/next-year
      {:impl  box
       :theme theme
       :props {:re-com re-com-ctx
               :attr   {:on-click (handler-fn (reset! current-month-atom next-year))}
               :child  (part ::dr/next-year-icon
                         {:theme theme
                          :props {:re-com   re-com-ctx
                                  :tag      :svg
                                  :attr     {:viewBox "0 0 24 24"}
                                  :children [[:g
                                              {:transform "translate(-1.5)"}
                                              [:path {:d "m 8.5882353,6 -1.41,1.41 4.5799997,4.59 -4.5799997,4.59 1.41,1.41 5.9999997,-6 z"}]
                                              [:path {:d "m 14.547353,5.9623529 -1.41,1.41 4.58,4.5900001 -4.58,4.59 1.41,1.41 6,-6 z"}]]]}})}})))

(defn- next-month-nav [current-month-atom part theme re-com-ctx]
  (let [next-month (plus-month (deref-or-value current-month-atom))]
    (part ::dr/next-month
      {:impl  box
       :theme theme
       :props {:re-com re-com-ctx
               :attr   {:on-click (handler-fn (reset! current-month-atom next-month))}
               :child  (part ::dr/next-month-icon
                         {:theme theme
                          :props {:re-com   re-com-ctx
                                  :tag      :svg
                                  :attr     {:viewBox "0 0 24 24"}
                                  :children [[:path {:d "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6-6-6z"}]]}})}})))

(defn- prev-nav [current-month-atom part theme re-com-ctx i18n]
  (part ::dr/prev-nav
    {:impl  h-box
     :theme theme
     :props {:re-com re-com-ctx
             :children
             [(prev-year-nav current-month-atom part theme re-com-ctx)
              [line]
              (prev-month-nav current-month-atom part theme re-com-ctx)
              [h-box
               :size "auto"
               :justify :center
               :children [(part ::dr/month-title
                            {:impl  box
                             :theme theme
                             :props {:re-com re-com-ctx
                                     :child  (month-label (deref-or-value current-month-atom) i18n)}})]]
              (part ::dr/year-title
                {:impl  box
                 :theme theme
                 :props {:re-com re-com-ctx
                         :child   (str (unparse (formatter "YYYY") (deref-or-value current-month-atom)))}})]}}))

(defn- next-nav [current-month-atom part theme re-com-ctx i18n]
  (part ::dr/next-nav
    {:impl  h-box
     :theme theme
     :props {:re-com re-com-ctx
             :children
             [(part ::dr/year-title
                {:impl  box
                 :theme theme
                 :props {:re-com re-com-ctx
                         :child   (str (unparse (formatter "YYYY") (plus-month (deref-or-value current-month-atom))))}})
              [h-box
               :size "auto"
               :justify :center
               :children [(part ::dr/month-title
                            {:impl  box
                             :theme theme
                             :props {:re-com re-com-ctx
                                     :child  (month-label (plus-month (deref-or-value current-month-atom)) i18n)}})]]
              (next-month-nav current-month-atom part theme re-com-ctx)
              [line]
              (next-year-nav current-month-atom part theme re-com-ctx)]}}))

(defn- date-disabled?
  "Checks various things to see if a date had been disabled."
  [date [minimum maximum disabled? selectable-fn]]
  (let [too-early?   (when minimum (cljs-time/before? date (deref-or-value minimum)))
        too-late?    (when maximum (cljs-time/after? date (deref-or-value maximum)))
        de-selected? (when selectable-fn (not (selectable-fn date)))]
    (or too-early? too-late? de-selected? disabled?)))

(defn- create-interval
  "inclusively creates a vector of date-formats from start to end."
  [start end]
  (let [first (deref-or-value start)
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
        (if check-interval?
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
    :else "daterange-default-td"))

(defn- create-day-td
  "Create table data elements with reactive classes and on click/hover handlers"
  [day [fsm start-date end-date temp-end] part theme re-com-ctx {:keys [on-change disabled? selectable-fn minimum maximum show-today? check-interval?]}]
  (let [disabled-data (vector minimum maximum disabled? selectable-fn)]
    (if (= day "")
      [:td ""]
      (let [correct-class (class-for-td fsm day start-date end-date temp-end disabled? selectable-fn minimum maximum show-today?)
            clickable?    (not (date-disabled? day disabled-data))]
        (part ::dr/date
          {:theme      theme
           :props      {:tag      :td
                        :re-com   re-com-ctx
                        :attr     {:on-click       (handler-fn (when clickable? (td-click-handler day [fsm start-date end-date] on-change check-interval? disabled-data)))
                                   :on-mouse-enter (handler-fn (reset! temp-end day))}
                        :children [(str (cljs-time/day day))]}
           :post-props {:class correct-class}})))))

(defn week-td [week-number]
  [:td {:class (theme/merge-class "daterange-td-basic"
                                  "daterange-week")}
   week-number])

(defn week-of-year-calc [days-list]
  (cljs-time/week-number-of-year (last days-list)))

(defn- create-week-tr
  "Given a list of days, create a table row with each :td referring to a different day"
  [days-list atoms part theme re-com-ctx {:keys [show-weeks?] :as args}]
  (let [week-of-year (week-of-year-calc days-list)]
    (into (if show-weeks? [:tr (week-td week-of-year)] [:tr])
          (for [day days-list]
            [create-day-td day atoms part theme re-com-ctx args]))))

(defn- empty-days-count
  "Returns the number of empty date tiles at the start of the month based on the first day of the month and the chosen week start day, monday = 1 sunday = 7"
  [chosen start]
  (let [chosen (or chosen 1)]
    (if (> chosen start)
      (- 7 (- chosen start))
      (- start chosen))))

(defn- days-for-month
  "Produces a partitioned list of date-formats with all the days in the given month, with leading empty strings to align with the days of the week"
  [date-from-month start-of-week]
  (let [month             (cljs-time/month date-from-month)
        year              (cljs-time/year date-from-month)
        last-day-of-month (cljs-time/day (cljs-time/last-day-of-the-month date-from-month))
        first-day-val     (cljs-time/day-of-week (cljs-time/first-day-of-the-month date-from-month)) ;; 1 = mon  7 = sun
        day-ints          (range 1 (inc last-day-of-month)) ;; e.g. (1 2 3 ... 31)
        days              (map #(cljs-time/date-time year month %) day-ints)
        with-lead-emptys  (-> start-of-week
                              (empty-days-count first-day-val)
                              (repeat "")
                              (cons days)
                              flatten)]

    (partition-all 7 with-lead-emptys))) ;; split into lists of 7 to be passed to create-week-tr

(def days ["M" "Tu" "W" "Th" "F" "Sa" "Su"])

(defn- create-table
  "Given the result from days-for-month for a given month, create the :tbody
  using the relevant :tr and :td functions above"
  [date atoms part theme re-com-ctx side {:keys [start-of-week i18n show-weeks?] :as args}]
  (let [table-ctx  (assoc-in re-com-ctx [:state :side] side)
        day-td     (fn [day-string]
                     (part ::dr/day-title
                       {:theme theme
                        :props {:tag      :td
                                :re-com   table-ctx
                                :children [day-string]}}))
        weekday-row (into (if show-weeks? [:tr [:td]] [:tr])
                          (map day-td (take 7 (drop (dec start-of-week)
                                                    (cycle (:days i18n days))))))
        date-rows   (for [days (days-for-month date start-of-week)]
                      [create-week-tr days atoms part theme table-ctx args])]
    (part ::dr/table
      {:theme theme
       :props {:tag      :table
               :re-com   table-ctx
               :children [(into [:tbody weekday-row] date-rows)]}})))

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

(def part-structure
  [::dr/wrapper {:impl 're-com.box/h-box}
   [::dr/border {:impl 're-com.box/border}
    [::dr/container {:impl 're-com.box/h-box}
     [::dr/left-panel {:impl 're-com.box/v-box}
      [::dr/prev-nav {:impl 're-com.box/h-box}
       [::dr/prev-year {:impl 're-com.box/box}
        [::dr/prev-year-icon {:tag :svg}]]
       [::dr/prev-month {:impl 're-com.box/box}
        [::dr/prev-month-icon {:tag :svg}]]
       [::dr/month-title {:impl 're-com.box/box}]
       [::dr/year-title {:impl 're-com.box/box}]]
      [::dr/table {:tag :table}
       [::dr/day-title {:tag :td}]
       [::dr/date {:tag :td}]]]
     [::dr/right-panel {:impl 're-com.box/v-box}
      [::dr/next-nav {:impl 're-com.box/h-box}
       [::dr/year-title {:impl 're-com.box/box}]
       [::dr/month-title {:impl 're-com.box/box}]
       [::dr/next-month {:impl 're-com.box/box}
        [::dr/next-month-icon {:tag :svg}]]
       [::dr/next-year {:impl 're-com.box/box}
        [::dr/next-year-icon {:tag :svg}]]]
      [::dr/table]]]]])

(def daterange-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def daterange-parts
  (when include-args-desc?
    (-> (map :name daterange-parts-desc) set)))

(def daterange-args-desc
  (when include-args-desc?
    (into
     [{:name :model
       :required false
       :type "map with keys :start, :end | r/atom"
       :validate-fn model?
       :description "the selected date range. Only updates after a selection has been completed. A closed (inclusive) interval. A map containing :start and :end whose values must both satisfy DateTimeProtocol. Nil is also acceptable if you want to start with nothing selected"}
      {:name :on-change
       :required true
       :type "satisfies DateTimeProtocol -> nil"
       :validate-fn fn?
       :description "called when a new complete selection has been made"}
      {:name :disabled?
       :required false
       :default false
       :type "boolean | atom"
       :description "when true, the user can't select dates but can navigate"}
      {:name :initial-display
       :required false
       :type "satisfies DateTimeProtocol | r/atom"
       :validate-fn date-like?
       :description "set the months shown when no model is selected, defaults to the current month"}
      {:name :selectable-fn
       :required false
       :type "function"
       :validate-fn fn?
       :description "called on each date, if it returns false, that date is not selectable"}
      {:name :show-today?
       :required false
       :default false
       :type "boolean"
       :description "when true, todays date is highlighted"}
      {:name :minimum
       :required false
       :type "satisfies DateTimeProtocol | r/atom"
       :validate-fn date-like?
       :description "no selection before this date"}
      {:name :maximum
       :required false
       :type "satisfies DateTimeProtocol | r/atom"
       :validate-fn date-like?
       :description "no selection after this date"}
      {:name :check-interval?
       :required false
       :default false
       :type "boolean"
       :description "if true, the user cannot select ranges which contain disabled days. If false, ranges spanning deselected or disabled dates are valid"}
      {:name :start-of-week
       :required false
       :default 1
       :type "int"
       :validate-fn int?
       :description "choose left most column of the table, 1 = monday ... 7 = sunday"}
      {:name :show-weeks?
       :required false
       :default false
       :type "boolean"
       :description "when true, week numbers are shown to the left"}
      {:name :hide-border?
       :required false
       :type "boolean"
       :description "when true, the border is not displayed"}
      {:name :i18n
       :required false
       :type "map"
       :validate-fn map?
       :description "internationalization map with optional keys :days and :months (both vectors of strings)"}
      args/class
      args/style
      args/attr
      (args/parts daterange-parts)
      args/src
      args/debug-as]
     (concat
      theme/args-desc
      (part/describe-args part-structure)))))

(defn daterange
  "Tracks the external model, but takes inputs into an internal model. The given on-change function is only called after a full selection has been made"
  [& {:keys [model initial-display pre-theme theme] :as args}]
  (or
   (validate-args-macro daterange-args-desc args)
   (let [composed-theme (theme/comp pre-theme theme)
         current-month  (r/atom (or (deref-or-value initial-display)
                                    (:start (deref-or-value model))
                                    (now->utc)))
         fsm            (r/atom "pick-start")
         start-date     (r/atom (:start (deref-or-value model)))
         end-date       (r/atom (:end (deref-or-value model)))
         temp-end       (r/atom (or @end-date (now->utc)))]
     (fn render-fn
       [& {:keys [model hide-border? i18n] :as args}]
       (or
        (validate-args-macro daterange-args-desc args)
        (let [latest-external-model   (deref-or-value model)
              internal-model-refernce {:start @start-date :end @end-date}
              _                       (when (and (model-changed? latest-external-model internal-model-refernce)
                                                 (= @fsm "pick-start"))
                                        (reset! start-date (:start latest-external-model))
                                        (reset! end-date (:end latest-external-model)))
              part                    (partial part/part part-structure args)
              re-com-ctx              {:state {:hide-border? (deref-or-value hide-border?)}}]
          (part ::dr/wrapper
            {:impl       h-box
             :theme      composed-theme
             :post-props (-> (select-keys args [:src :debug-as])
                             (update :debug-as #(or % (reflect-current-component)))
                             (debug/instrument args))
             :props
             {:re-com re-com-ctx
              :children
              [(part ::dr/border
                 {:impl  border
                  :theme composed-theme
                  :props
                  {:re-com re-com-ctx
                   :child
                   (part ::dr/container
                     {:impl       h-box
                      :theme      composed-theme
                      :post-props (-> (select-keys args [:class :style :attr])
                                      (tu/style {:font-size "13px"
                                                 :position  "static"}))
                      :props
                      {:re-com re-com-ctx
                       :children
                       [(part ::dr/left-panel
                          {:impl  v-box
                           :theme composed-theme
                           :props {:re-com re-com-ctx
                                   :children
                                   [(prev-nav current-month part composed-theme re-com-ctx i18n)
                                    [create-table @current-month
                                     [fsm start-date end-date temp-end]
                                     part composed-theme re-com-ctx :left args]]}})
                        (part ::dr/right-panel
                          {:impl  v-box
                           :theme composed-theme
                           :props {:re-com re-com-ctx
                                   :children
                                   [(next-nav current-month part composed-theme re-com-ctx i18n)
                                    [create-table (plus-month @current-month)
                                     [fsm start-date end-date temp-end]
                                     part composed-theme re-com-ctx :right args]]}})]}})}})]}})))))))

(defn- anchor-button
  "Provide clickable field with current date label and dropdown button e.g. [ 2014 Sep 17 | # ]"
  [model format goog? placeholder]
  (let [format-str (or format "dd MMM, yyyy")]
    (cond
      (not (date-like? (:start (deref-or-value model))))
      [box :style {:color "#bbb"} :child (or placeholder "")]
      goog? (str
             (.format (DateTimeFormat. (if (seq format) format format-str)) (:start (deref-or-value model)))
             " - "
             (.format (DateTimeFormat. (if (seq format) format format-str)) (:end (deref-or-value model))))

      :else (str
             (unparse (formatter format-str) (deref-or-value (:start (deref-or-value model))))
             " - "
             (unparse (formatter format-str) (deref-or-value (:end (deref-or-value model))))))))

(def daterange-dropdown-args-desc
  (when include-args-desc?
    (conj daterange-args-desc
          {:name :format          :required false  :default "dd MMM, yyyy" :type "string"   :description "[daterange-dropdown only] a representation of a date format. See cljs_time.format"}
          {:name :goog?           :required false  :default false          :type "boolean"  :description [:span "[daterange only] use " [:code "goog.i18n.DateTimeFormat"] " instead of " [:code "cljs_time.format"] " for applying " [:code ":format"]]}
          {:name :no-clip?        :required false  :default true           :type "boolean"  :description "[daterange-dropdown only] when an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped. When this parameter is true (which is the default), re-com will use a different CSS method to show the popover. This method is slightly inferior because the popover can't track the anchor if it is repositioned"}
          {:name :placeholder     :required false                          :type "string"   :description "[daterange-dropdown only] placeholder text for when a date is not selected."}
          {:name :width           :required false  :validate-fn string?    :type "string"   :description "[daterange-dropdown only] a CSS width style"}
          {:name :position-offset :required false  :validate-fn number?    :type "integer"  :description "[daterange-dropdown only] px horizontal offset of the popup"}
          {:name :body-footer     :required false   :type "part"  :description "[daterange-dropdown only] extra part added to the bottom of the dropdown"}
          {:name :position-offset :required false  :validate-fn number?    :type "integer"  :description "[daterange-dropdown only] px horizontal offset of the popup"})))

(defn daterange-dropdown
  [& {:keys [pre-theme theme] :as args}]
  (or
   (validate-args-macro daterange-dropdown-args-desc args)
   (let [shown?         (r/atom false)
         composed-theme (theme/comp pre-theme theme)]
     (fn render-fn
       [& {:keys [model on-change format goog?
                  placeholder width disabled?
                  anchor-width anchor-height body-header body-footer]

           :as passthrough-args}]
       (or
        (validate-args-macro daterange-dropdown-args-desc passthrough-args)
        (let [collapse-on-select (fn [new-model]
                                   (reset! shown? false)
                                   (when on-change (on-change new-model)))
              passthrough-args   (-> passthrough-args
                                     (dissoc :format :goog? :no-clip? :placeholder :width :position-offset :anchor-width :anchor-height :body-header :body-width :body-footer)
                                     (assoc :on-change collapse-on-select)
                                     (assoc :src (at))
                                     (merge {:hide-border? true}))]
          [dd/dropdown
           :class         "rc-daterange-dropdown-wrapper"
           :on-change     (partial reset! shown?)
           :model         shown?
           :label         [anchor-button model format goog? placeholder]
           :indicator     [:i.zmdi.zmdi-apps {:style {:font-size "18px"}}]
           :width         width
           :disabled?     disabled?
           :theme         composed-theme
           :anchor-width  anchor-width
           :anchor-height anchor-height
           :body-header   body-header
           :body-width    "520px"
           :body-footer   body-footer
           :body          [daterange passthrough-args]]))))))
