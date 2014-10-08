;; Loosly based on ideas: https://github.com/dangrossman/bootstrap-daterangepicker
;; depends: datepicker-bs3.css

(ns re-com.date
  (:require
    [clojure.set          :refer [superset?]]
    [cljs-time.core       :refer [now minus plus months days year month day day-of-week first-day-of-the-month
                                  before? after?]]
    [cljs-time.predicates :refer [sunday?]]
    [cljs-time.format     :refer [parse unparse formatters formatter]]
    [re-com.box           :refer [box border h-box]]
    [re-com.util          :refer [deref-or-value]]
    [re-com.popover       :refer [popover make-button make-link]]
    [reagent.core         :as    reagent]))

;; --- cljs-time facades ------------------------------------------------------
;; TODO: from day8date should be a common lib

(def ^:private month-format (formatter "MMMM yyyy"))

(def ^:private week-format (formatter "ww"))

(def ^:private date-format (formatter "yyyy MMM dd"))

(defn iso8601->date [iso8601]
  (when (seq iso8601)
    (parse (formatters :basic-date) iso8601)))

(defn- month-label [date] (unparse month-format date))

(defn- dec-month [date] (minus date (months 1)))

(defn- inc-month [date] (plus date (months 1)))

(defn- inc-date [date n] (plus date (days n)))

(defn previous
  "If date fails pred, subtract period until true, otherwise answer date"
  ;; date   - a goog.date.UtcDateTime now if ommited
  ;; pred   - can be one of cljs-time.predicate e.g. sunday? but anything that can deal with goog.date.UtcDateTime
  ;; period - a period which will be subtracted see cljs-time.core periods
  ;; Note:  If period and pred do not represent same granularity, some steps may be skipped
  ;         e.g Pass a Wed date, specify sunday? as pred and a period (days 2) will skip one Sunday.
  ([pred]
   (previous pred (now)))
  ([pred date]
   (previous pred date (days 1)))
  ([pred date period]
   (if (pred date)
    date
   (recur pred (minus date period) period))))

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
  [table-div hide-border]
  ;;extra h-box is currently necessary so that calendar & border do not strecth to width of any containing v-box
  [h-box
   :children [[border
               :radius "4px"
               :size   "none"
               :border (when hide-border "none")
               :child [:div
                       {:class "calendar-date datepicker"
                        ;; override inherrited body larger 14px font-size
                        ;; override position from css because we are inline
                        :style {:font-size "13px"
                                :position "static"
                                :-webkit-user-select "none" ;; only good on webkit/chrome what do we do for firefox etc
                                }}
                       table-div]]]])


(defn- table-thead
  "Answer 2 x rows showing month with nav buttons and days NOTE: not internationalized"
  [current {show-weeks :show-weeks enabled-days :enabled-days minimum :minimum maximum :maximum}]
  ;;TODO: We might choose later to style by removing arrows altogether instead of greying when disabled navigation
  (let [style        (fn [week-day] {:class (if (enabled-days week-day) "day-enabled" "day-disabled")})
        prev-date    (dec-month @current)
        prev-enabled (if minimum (after? prev-date minimum) true)
        next-date    (inc-month @current)
        next-enabled (if maximum (before? next-date maximum) true)
        template-row (if show-weeks [:tr [:th]] [:tr])]
    [:thead
     (conj template-row
           [:th {:class (str "prev " (if prev-enabled "available selectable" "disabled"))}
            [:i {:class "fa fa-arrow-left icon-arrow-left glyphicon glyphicon-chevron-left"
                        :on-click #(when prev-enabled (reset! current prev-date))}]]
           [:th {:class "month" :col-span "5"} (month-label @current)]
           [:th {:class (str "next " (if next-enabled "available selectable" "disabled"))}
            [:i {:class "fa fa-arrow-right icon-arrow-right glyphicon glyphicon-chevron-right"
                        :on-click #(when next-enabled (reset! current next-date))}]])
     ;; could be done via more clever mapping but avoiding abscurity here.
     ;; style each day label based on if it is in enabled-days
     (conj template-row
           [:th (style 7) "SUN"]
           [:th (style 1) "MON"]
           [:th (style 2) "TUE"]
           [:th (style 3) "WED"]
           [:th (style 4) "THU"]
           [:th (style 5) "FRI"]
           [:th (style 6) "SAT"])]))


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
  (let [current-start   (previous sunday? current)
        focus-month     (month current)
        row-start-dates (map #(inc-date current-start (* 7 %)) (range 6))]
    (into [:tbody] (map #(table-tr % focus-month selected attributes disabled on-change) row-start-dates))))


(defn- configure
  "Augment passed attributes with extra info/defaults."
  [attributes]
  (let [enabled-days (->> (if (seq (:enabled-days attributes))
                            (:enabled-days attributes)
                            #{:Su :Mo :Tu :We :Th :Fr :Sa})
                          (map #(% {:Su 7 :Sa 6 :Fr 5 :Th 4 :We 3 :Tu 2 :Mo 1}))
                          set)]
    (merge attributes {:enabled-days enabled-days
                       :today (now)})))

(def core-api
  #{:model        ; goog.date.UtcDateTime can be reagent/atom.
                  ; The calendar will be focused on corresponding date and the date represents selection.
    :on-change    ; function callback will be passed new selected goog.date.UtcDateTime
    :disabled     ; optional boolean can be reagent/atom. When true, navigation is allowed but selection is disabled.
    :enabled-days ; set of any #{:Su :Mo :Tu :We :Th :Fr :Sa} if nil or an empty set, all days are enabled.
    :show-weeks   ; boolean. When true, first column shows week numbers.
    :show-today   ; boolean. When true, today's date is highlighted. Selected day highlight takes precedence.
    :minimum      ; optional goog.date.UtcDateTime inclusive beyond which navigation & selection is blocked.
    :maximum      ; optional goog.date.UtcDateTime inclusive beyond which navigation & selection is blocked.
    :hide-border  ; boolean. Default false.
   })

(defn inline-picker
  [& {:keys [model] :as args}]
  {:pre [(superset? core-api (keys args))]}
  (let [current (-> (deref-or-value model) first-day-of-the-month reagent/atom)]
    (fn
      [& {:keys [model disabled hide-border on-change] :as properties}]
      (let [configuration (configure properties)]
        (main-div-with
          [:table {:class "table-condensed"}
           [table-thead current configuration]
           [table-tbody
            @current
            (deref-or-value model)
            configuration
            (if (nil? disabled) false (deref-or-value disabled))
            on-change]]
          hide-border)))))


(defn- anchor-button
  [shown? model format]
  "Provide clickable field with current date label and dropdown button e.g. [ 2014 Sep 17 | # ]"
  [:div {:class    "input-group"
         :style    {:display             "flex"
                    :flex                "none"
                    :-webkit-user-select "none"}
         :on-click #(reset! shown? (not @shown?))}
   [h-box
    :align :center
    :children [[:label {:class "form-control dropdown-button"}
                (unparse (if (seq format) (formatter format) date-format) @model)]
               [:span  {:class "dropdown-button activator input-group-addon"}
                [:i {:class "glyphicon glyphicon-th"}]]]]])

(def dropdown-api
  (conj core-api
        :format ; optional date format see cljs_time.format Default "yyyy MMM dd"
  ))

(defn dropdown-picker
  [& {:as args}]
  {:pre [(superset? dropdown-api (keys args))]}
  (let [shown? (reagent/atom false)]
    (fn
      [& {:keys [model show-weeks on-change format] :as passthrough-args}]
      (let [collapse-on-select (fn [new-model]
                                 (reset! shown? false)
                                 (when on-change (on-change new-model))) ;; wrap callback to collapse popover
            passthrough-args   (dissoc passthrough-args :format)         ;; :format is only valid at this API level
            passthrough-args   (->> (assoc passthrough-args :on-change collapse-on-select)
                                    (merge {:hide-border true})          ;; apply defaults
                                    (dissoc)
                                    vec
                                    flatten)]
        [popover
         :position :below-center
         :showing? shown?
         :options {:arrow-length      0
                   :arrow-width       0
                   :margin-left       (if show-weeks "-24px" "-11px")
                   :margin-top        "3px"
                   :padding           "0px"
                   :backdrop-callback #(reset! shown? false)
                   :backdrop-opacity  0}
         :anchor  [anchor-button shown? model format]
         :popover {:body  (into [inline-picker] passthrough-args)
                   :width "auto"}]))))