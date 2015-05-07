(ns re-com.datepicker
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require
    [reagent.core         :as    reagent]
    [cljs-time.core       :refer [now minus plus months days year month day day-of-week first-day-of-the-month before? after?]]
    [re-com.validate      :refer [goog-date? css-style? html-attr?] :refer-macros [validate-args-macro]]
    [cljs-time.predicates :refer [sunday?]]
    [cljs-time.format     :refer [parse unparse formatters formatter]]
    [re-com.box           :refer [border h-box flex-child-style]]
    [re-com.util          :refer [deref-or-value]]
    [re-com.popover       :refer [popover-content-wrapper popover-anchor-wrapper backdrop popover-border]]))

;; Loosely based on ideas: https://github.com/dangrossman/bootstrap-daterangepicker

;; --- cljs-time facades ------------------------------------------------------
;; TODO: from day8date should be a common lib

(def ^:const month-format (formatter "MMMM yyyy"))

(def ^:const week-format (formatter "ww"))

(def ^:const date-format (formatter "yyyy MMM dd"))

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
  [table-div hide-border? class style attr]
  ;;extra h-box is currently necessary so that calendar & border do not strecth to width of any containing v-box
  [h-box
   :children [[border
               :radius "4px"
               :size   "none"
               :border (when hide-border? "none")
               :child  [:div
                        (merge
                          {:class (str "rc-datepicker datepicker noselect " class)
                           ;; override inherrited body larger 14px font-size
                           ;; override position from css because we are inline
                           :style (merge {:font-size "13px"
                                          :position  "static"}
                                          style)}
                          attr)
                        table-div]]]])


(defn- table-thead
  "Answer 2 x rows showing month with nav buttons and days NOTE: not internationalized"
  [current {show-weeks? :show-weeks? enabled-days :enabled-days minimum :minimum maximum :maximum}]
  ;;TODO: We might choose later to style by removing arrows altogether instead of greying when disabled navigation
  (let [style         (fn [week-day] {:class (if (enabled-days week-day) "day-enabled" "day-disabled")})
        prev-date     (dec-month @current)
        prev-enabled? (if minimum (after? prev-date minimum) true)
        next-date     (inc-month @current)
        next-enabled? (if maximum (before? next-date maximum) true)
        template-row  (if show-weeks? [:tr [:th]] [:tr])]
    [:thead
     (conj template-row
           #_[:th {:class    (str "prev " (if prev-enabled? "available selectable" "disabled"))} ;; TODO: Remove
            [:i {:class    "glyphicon glyphicon-chevron-left"
                 :on-click (handler-fn (when prev-enabled? (reset! current prev-date)))}]]
           [:th {:class (str "prev " (if prev-enabled? "available selectable" "disabled"))
                 :style {:padding "0px"}}
            [:i.md-chevron-left
             {:style    {:font-size "24px"}
              :on-click (handler-fn (when prev-enabled? (reset! current prev-date)))}]]

           [:th {:class "month" :col-span "5"} (month-label @current)]

           #_[:th {:class (str "next " (if next-enabled? "available selectable" "disabled"))} ;; TODO: Remove
            [:i {:class    "glyphicon glyphicon-chevron-right"
                 :on-click (handler-fn (when next-enabled? (reset! current next-date)))}]]
           [:th {:class (str "next " (if next-enabled? "available selectable" "disabled"))
                 :style {:padding "0px"}}
            [:i.md-chevron-right
             {:style    {:font-size "24px"}
              :on-click (handler-fn (when next-enabled? (reset! current next-date)))}]])
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
  [date focus-month selected today {minimum :minimum maximum :maximum :as attributes} disabled? on-change]
  ;;following can be simplified and terse
  (let [enabled-min   (if minimum (>=date date minimum) true)
        enabled-max   (if maximum (<=date date maximum) true)
        enabled-day   (and enabled-min enabled-max)
        disabled-day? (if enabled-day
                        (nil? ((:enabled-days attributes) (day-of-week date)))
                        true)
        styles       (cond disabled?
                           "off"

                           disabled-day?
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
        on-click     #(when-not (or disabled? disabled-day?) (selection-changed date on-change))]
    [:td {:class styles
          :on-click (handler-fn (on-click))} (day date)]))


(defn- week-td [date]
  [:td {:class "week"} (unparse week-format date)])


(defn- table-tr
  "Return 7 columns of date cells from date inclusive"
  [date focus-month selected attributes disabled? on-change]
  {:pre [(sunday? date)]}
  (let [table-row (if (:show-weeks? attributes) [:tr (week-td date)] [:tr])
        row-dates (map #(inc-date date %) (range 7))
        today     (if (:show-today? attributes) (:today attributes) nil)]
    (into table-row (map #(table-td % focus-month selected today attributes disabled? on-change) row-dates))))


(defn- table-tbody
  "Return matrix of 6 rows x 7 cols table cells representing 41 days from start-date inclusive"
  [current selected attributes disabled? on-change]
  {:pre [(and (seq (:enabled-days attributes)) ((:enabled-days attributes) (day-of-week selected)))]}
  (let [current-start   (previous sunday? current)
        focus-month     (month current)
        row-start-dates (map #(inc-date current-start (* 7 %)) (range 6))]
    (into [:tbody] (map #(table-tr % focus-month selected attributes disabled? on-change) row-start-dates))))


(defn- configure
  "Augment passed attributes with extra info/defaults"
  [attributes]
  (let [enabled-days (->> (if (seq (:enabled-days attributes))
                            (:enabled-days attributes)
                            #{:Su :Mo :Tu :We :Th :Fr :Sa})
                          (map #(% {:Su 7 :Sa 6 :Fr 5 :Th 4 :We 3 :Tu 2 :Mo 1}))
                          set)]
    (merge attributes {:enabled-days enabled-days
                       :today (now)})))

(def datepicker-args-desc
  [{:name :model        :required true                        :type "goog.date.UtcDateTime | atom"   :validate-fn goog-date? :description "the selected date. Should match :enabled-days"}
   {:name :on-change    :required true                        :type "goog.date.UtcDateTime -> nil"   :validate-fn fn?        :description "called when a new selection is made"}
   {:name :disabled?    :required false :default false        :type "boolean | atom"                                         :description "when true, the can't select dates but can navigate"}
   {:name :enabled-days :required false                       :type "set"                            :validate-fn set?       :description "a subset of #{:Su :Mo :Tu :We :Th :Fr :Sa}. Only dates falling on these days will be user-selectable. Default is all 7 days"}
   {:name :show-weeks?  :required false :default false        :type "boolean"                                                :description "when true, week numbers are shown to the left"}
   {:name :show-today?  :required false :default false        :type "boolean"                                                :description "when true, today's date is highlighted"}
   {:name :minimum      :required false                       :type "goog.date.UtcDateTime"          :validate-fn goog-date? :description "no selection or navigation before this date"}
   {:name :maximum      :required false                       :type "goog.date.UtcDateTime"          :validate-fn goog-date? :description "no selection or navigation after this date"}
   {:name :hide-border? :required false :default false        :type "boolean"                                                :description "when true, the border is not displayed"}
   {:name :class        :required false                       :type "string"                         :validate-fn string?    :description "CSS class names, space separated"}
   {:name :style        :required false                       :type "CSS style map"                  :validate-fn css-style? :description "CSS styles to add or override"}
   {:name :attr         :required false                       :type "HTML attr map"                  :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn datepicker
  [& {:keys [model] :as args}]
  {:pre [(validate-args-macro datepicker-args-desc args "datepicker")]}
  (let [current (-> (deref-or-value model) first-day-of-the-month reagent/atom)]
    (fn
      [& {:keys [model disabled? hide-border? on-change class style attr] :as properties}]
      {:pre [(validate-args-macro datepicker-args-desc properties "datepicker")]}
      (let [configuration (configure properties)]
        [main-div-with
         [:table {:class "table-condensed"}
          [table-thead current configuration]
          [table-tbody
           @current
           (deref-or-value model)
           configuration
           (if (nil? disabled?) false (deref-or-value disabled?))
           on-change]]
         hide-border?
         class
         style
         attr]))))


(defn- anchor-button
  "Provide clickable field with current date label and dropdown button e.g. [ 2014 Sep 17 | # ]"
  [shown? model format]
  [:div {:class    "input-group display-flex noselect"
         :style    (flex-child-style "none")
         :on-click (handler-fn (swap! shown? not))}
   [h-box
    :align :center
    :class "noselect"
    :children [[:label {:class "form-control dropdown-button"}
                (unparse (if (seq format) (formatter format) date-format) @model)]
               #_[:span  {:class "dropdown-button activator input-group-addon"} ;; TODO: Remove
                [:i {:class "glyphicon glyphicon-th"}]]
               [:span.dropdown-button.activator.input-group-addon
                {:style {:padding "3px 0 0 0"}}
                [:i.md-apps {:style {:font-size "24px"}}]]]]])

(def datepicker-dropdown-args-desc
  (conj datepicker-args-desc
    {:name :format  :required false  :default "yyyy MMM dd"  :type "string"   :description "a represenatation of a date format. See cljs_time.format"}))

(defn datepicker-dropdown
  [& {:as args}]
  {:pre [(validate-args-macro datepicker-dropdown-args-desc args "datepicker-dropdown")]}
  (let [shown?         (reagent/atom false)
        cancel-popover #(reset! shown? false)
        position       :below-center]
    (fn
      [& {:keys [model show-weeks? on-change format] :as passthrough-args}]
      (let [collapse-on-select (fn [new-model]
                                 (reset! shown? false)
                                 (when on-change (on-change new-model))) ;; wrap callback to collapse popover
            passthrough-args   (dissoc passthrough-args :format)         ;; :format is only valid at this API level
            passthrough-args   (->> (assoc passthrough-args :on-change collapse-on-select)
                                    (merge {:hide-border? true})         ;; apply defaults
                                    vec
                                    flatten)]
        [popover-anchor-wrapper
         :showing? shown?
         :position position
         :anchor   [anchor-button shown? model format]
         :popover  [:div {:style (flex-child-style "inherit")}
                    (when shown? [backdrop :on-click cancel-popover])
                    [popover-border
                     :position     position
                     :width        "auto"                       ;; TODO: Sort this mess out!
                     :arrow-length 0
                     :arrow-width  0
                     :margin-left  (if show-weeks? "-26px" "-13px")
                     :margin-top   "3px"
                     :padding      "0px"
                     :children     [(into [datepicker] passthrough-args)]]]]))))
