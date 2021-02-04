(ns re-demo.v-table-demo
  (:require [re-com.core   :refer [h-box gap v-box box v-table show-row-data-on-alt-click
                                   p label popover-anchor-wrapper popover-content-wrapper] :refer-macros [handler-fn]]
            [re-com.util   :refer [px]]
            [re-demo.utils :refer [title2 github-hyperlink source-reference]]
            ;[garden.color            :as color-util] ;TODO use for color-util/lighten, darken etc.
            [cljs-time.core          :as time.core]
            [cljs-time.format        :as time.format]
            [cljs-time.periodic      :as time.periodic]
            [reagent.core  :as reagent]
            [reagent.ratom :as ratom :refer-macros [reaction]]
            [goog.object   :as gob] ; lets consider https://github.com/binaryage/cljs-oops
            [goog.string   :as gstring]))

(def non-breaking-space (gstring/unescapeEntities "&nbsp;"))

(def table-border-style (str "1px solid " "rgb(204,204,204)"))
(def header-bg-color    "white")
(def footer-bg-color    "rgb(248,248,248)")

;; Selections
(def sel-color          "hsl(263, 86%, 50%)")
(def sel-bg-color       "hsl(263, 86%, 94%)")
(def sel-border         "1px dotted hsl(263, 86%, 70%)")
(def sel-on-mouse-up?   false)
(def sel-must-enclose?  false)

;; numbers representing px used for calculations
(def row-height  19) ; also known by CSS see app.css Planning v-table
(def demo-table-font-size "12px")
(def px-per-day-fixed {:days 20 :weeks 9 :months 3}) ; when not fitting to view

(def format-date-dd       (time.format/formatter "dd"))
(def format-date-dd-mmm   (time.format/formatter "dd-MMM"))
(def format-date-mmm-yyyy (time.format/formatter "MMM yyyy"))
(def dow-character {1 "M" 2 "T" 3 "W" 4 "T" 5 "F" 6 "S" 7 "S"})


(def activity-row-style
  {:position      "relative"
   :height        (str row-height "px")
   :font-size     demo-table-font-size
   :border-bottom "1px solid rgba(0, 0, 0, 0.05)"})

(def activity-row-v-grid-line-style
  {:position    "absolute"
   :width       "0px"
   :height      (str row-height "px")
   :margin      "0px 0px 0px -1px"
   :border-left "1px solid rgba(0, 0, 0, 0.05)"})

(defn from-ISO8601-extended
  [date-str]
  (time.format/parse date-str))

(def timeline-start-date (from-ISO8601-extended "20160731"))
(def timeline-end-date   (time.core/plus timeline-start-date (time.core/weeks 11)))
(def timeline-data
  [{}
   {:id           (random-uuid)
    :media-type  :tv
    :mkt-id      "0201"
    :duration-id "30"
    :label       "Sydney,30 sec"
    :activities
                 [{:id        1 :media-type :tv :label "500"
                   :from-date (from-ISO8601-extended "20160805")
                   :to-date   (from-ISO8601-extended "20160812")
                   :style     {:background-color "#fff8dc"}}
                  {:id        2 :media-type :tv :label "625"
                   :from-date (from-ISO8601-extended "20160812")
                   :to-date   (from-ISO8601-extended "20160819")
                   :style     {:background-color "#ffb3af"}}
                  {:id        3 :media-type :tv :label "Do not book!"
                   :from-date (from-ISO8601-extended "20160819")
                   :to-date   (from-ISO8601-extended "20160826")
                   :style     {:background-color "#fffff0" :color "#ff0000"}}
                  {:id        4 :media-type :tv :label "Sneaky hidden one"
                   :from-date (from-ISO8601-extended "20161009")
                   :to-date   (from-ISO8601-extended "20161016")
                   :style     {:background-color "#A00000" :color "#ffffff"}}]}
   {:id          (random-uuid)
    :media-type :tv :mkt-id "0201" :duration-id "15" :label "Sydney,15 sec"
    :activities
                [{:id        1 :media-type :tv :label "250"
                  :from-date (from-ISO8601-extended "20160801")
                  :to-date   (from-ISO8601-extended "20160817")
                  :style     {:background-color "#fff8dc"}}
                 {:id        2 :media-type :tv :label "100"
                  :from-date (from-ISO8601-extended "20160818")
                  :to-date   (from-ISO8601-extended "20160825")
                  :style     {:background-color "#c6f08a"}}]}
   {:id  (random-uuid) :media-type :tv :mkt-id "0301" :duration-id "30" :label "Melbourne,30 sec"
    :activities
        [{:id        1 :media-type :tv :label "999"
          :from-date (from-ISO8601-extended "20160806")
          :to-date   (from-ISO8601-extended "20160830")
          :style     {:background-color "#5ba9b8" :color "#ffffff"}}]}
   {:id  (random-uuid) :media-type :tv :mkt-id "0701" :duration-id "30" :label "Brisbane,30 sec"}
   {:id  (random-uuid) :media-type :tv :mkt-id "0801" :duration-id "60" :label "Adelaide,60 sec"
    :activities
        [{:id        1 :media-type :tv :label "Comment only"
          :from-date (from-ISO8601-extended "20160809")
          :to-date   (from-ISO8601-extended "20160817")
          :style     {:background-color "#000000" :color "#ffffff"}}]}
   {:id  (random-uuid) :media-type :tv :mkt-id "0801" :duration-id "60" :label "Adelaide,60 sec"}
   {:id  (random-uuid) :media-type :tv :mkt-id "0901" :duration-id "60" :label "Perth,60 sec"}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0201" :duration-id "30" :label "Albury,30 sec"
    :activities
        [{:id        1 :media-type :tv :label "123"
          :from-date (from-ISO8601-extended "20160805")
          :to-date   (from-ISO8601-extended "20160810")
          :style     {:background-color "#fff8dc"}}
         {:id        2 :media-type :tv :label "456"
          :from-date (from-ISO8601-extended "20160812")
          :to-date   (from-ISO8601-extended "20160815")
          :style     {:background-color "#ffb300"}}
         {:id        3 :media-type :tv :label "Definitely book!"
          :from-date (from-ISO8601-extended "20160819")
          :to-date   (from-ISO8601-extended "20160904")
          :style     {:background-color "#fffff0" :color "#ff0000"}}]}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0201" :duration-id "15" :label "Dubbo,15 sec"
    :activities
        [{:id        1 :media-type :tv :label "555"
          :from-date (from-ISO8601-extended "20160807")
          :to-date   (from-ISO8601-extended "20160814")
          :style     {:background-color "#fff8dc"}}
         {:id        2 :media-type :tv :label "666"
          :from-date (from-ISO8601-extended "20160821")
          :to-date   (from-ISO8601-extended "20160828")
          :style     {:background-color "#c6f08a"}}]}
   {:id  (random-uuid) :media-type :tv :mkt-id "0201" :duration-id "30" :label "Dubbo,30 sec"
    :activities
        [{:id        1 :media-type :tv :label "777"
          :from-date (from-ISO8601-extended "20160814")
          :to-date   (from-ISO8601-extended "20160821")
          :style     {:background-color "#fff8dc"}}
         {:id        2 :media-type :tv :label "888"
          :from-date (from-ISO8601-extended "20160828")
          :to-date   (from-ISO8601-extended "20160903")
          :style     {:background-color "#c6f08a"}}]}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0301" :duration-id "15" :label "Wodonga,15 sec"
    :activities
        [{:id        1 :media-type :tv :label "300"
          :from-date (from-ISO8601-extended "20160806")
          :to-date   (from-ISO8601-extended "20160830")
          :style     {:background-color "#5ba9b8" :color "#ffffff"}}]}
   {:id  (random-uuid) :media-type :tv :mkt-id "0301" :duration-id "30" :label "Wodonga,30 sec"
    :activities
        [{:id        1 :media-type :tv :label "305"
          :from-date (from-ISO8601-extended "20160810")
          :to-date   (from-ISO8601-extended "20160903")
          :style     {:background-color "#5ba9b8" :color "#ffffff"}}]}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0701" :duration-id "30" :label "Brisbane,30 sec"}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0801" :duration-id "60" :label "Adelaide,60 sec"
    :activities
        [{:id        1 :media-type :tv :label "Another comment"
          :from-date (from-ISO8601-extended "20160809")
          :to-date   (from-ISO8601-extended "20160817")
          :style     {:background-color "#000000" :color "#ffffff"}}]}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0301" :duration-id "15" :label "Newcastle,15 sec"
    :activities
        [{:id        1 :media-type :tv :label "310"
          :from-date (from-ISO8601-extended "20160806")
          :to-date   (from-ISO8601-extended "20160830")
          :style     {:background-color "#5ba9ff" :color "#ffffff"}}]}
   {:id  (random-uuid) :media-type :tv :mkt-id "0301" :duration-id "30" :label "Newcastle,30 sec"
    :activities
        [{:id        1 :media-type :tv :label "315"
          :from-date (from-ISO8601-extended "20160810")
          :to-date   (from-ISO8601-extended "20160903")
          :style     {:background-color "#5ba9ff" :color "#ffffff"}}]}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0201" :duration-id "15" :label "Wollongong,15 sec"
    :activities
        [{:id        1 :media-type :tv :label "999"
          :from-date (from-ISO8601-extended "20160807")
          :to-date   (from-ISO8601-extended "20160814")
          :style     {:background-color "#fff8ff"}}
         {:id        2 :media-type :tv :label "aaa"
          :from-date (from-ISO8601-extended "20160821")
          :to-date   (from-ISO8601-extended "20160828")
          :style     {:background-color "#c6f0ff"}}]}
   {:id  (random-uuid) :media-type :tv :mkt-id "0201" :duration-id "30" :label "Wollongong,30 sec"
    :activities
        [{:id        1 :media-type :tv :label "bbb"
          :from-date (from-ISO8601-extended "20160814")
          :to-date   (from-ISO8601-extended "20160821")
          :style     {:background-color "#fff8ff"}}
         {:id        2 :media-type :tv :label "ccc"
          :from-date (from-ISO8601-extended "20160828")
          :to-date   (from-ISO8601-extended "20160903")
          :style     {:background-color "#c6f0ff"}}]}
   {}
   {}
   {:id  (random-uuid) :media-type :tv :mkt-id "0801" :duration-id "60" :label "Final row"
    :activities
        [{:id        1 :media-type :tv :label "The End"
          :from-date (from-ISO8601-extended "20160809")
          :to-date   (from-ISO8601-extended "20160817")
          :style     {:background-color "#A00000" :color "#ffffff"}}
         {:id        2 :media-type :tv :label "The VERY End"
          :from-date (from-ISO8601-extended "20161009")
          :to-date   (from-ISO8601-extended "20161016")
          :style     {:background-color "#A00000" :color "#ffffff"}}]}])


(defn px-width
  [num-days px-per-day]
  (px (* num-days px-per-day)))

(defn px-width-activity
  [num-days px-per-day]
  ; we allow for borders and gap
  (px (- (* num-days px-per-day) 2)))

(defn translate-x
  [x-offset]
  (str "translateX(" x-offset "px)"))


(defn timeline-activities
  "Based on the resolution, return seq of activities with :start-date & :num-days"
  [date-start date-end resolution]
  (case resolution
    :days
    (map
      #(hash-map :start-date % :num-days 1)
      (time.periodic/periodic-seq date-start date-end (time.core/period resolution 1)))
    :weeks
    (let [extended-end (if-not (= :weeks resolution)
                         date-end ; no need to extend
                         (let [mod-week (-> (time.core/interval date-start date-end)
                                            (time.core/in-days)
                                            (mod 7))]
                           (if (zero? mod-week)
                             date-end ; already a multiple, no extension
                             ;(date/next date-end (- 7 mod-week))
                             (time.core/plus date-end (time.core/days (- 7 mod-week)))
                             )))]
      (map
        #(hash-map :start-date % :num-days 7)
        (time.periodic/periodic-seq date-start extended-end (time.core/period resolution 1))))
    :months
    (->> (time.periodic/periodic-seq date-start date-end (time.core/period :days 1))
         (partition-by time.core/month)
         (map (fn [all-days-in-month]
                (let [start-of-month (first all-days-in-month)
                      counted-days   (count all-days-in-month)]
                  {:start-date start-of-month
                   :num-days   counted-days}))))))


(defn create-row-header-line
  [description duration]
  (let [desc-width 90
        dur-width  55]
    [h-box
     :width    (px (+ desc-width dur-width))
     :height   (px row-height)
     :padding  "0 0 0 10px"
     :children [[box :width (px desc-width) :child description]
                [box :width (px dur-width) :child duration]]]))


(defn render-top-left-header
  []
  [h-box
   :size     "1"
   :align    :end ;; Send text to the bottom
   :children [[create-row-header-line "Market" "Dur"]]])


(defn render-dates-row
  [timeline-start px-per-day activities]
  ; activities - vector of maps each :start-date :num-days :label
  (into
    [:div {:class "table-date-header-label"
           :style {:height (px row-height) :position "relative" :font-size demo-table-font-size}}]
    (map-indexed
      (fn [index {:keys [start-date num-days label]}]
        (let [x-offset (-> (time.core/in-days (time.core/interval timeline-start start-date))
                           (* px-per-day))]
          ^{:key (str index)}
          [:span {:class "trans-date" ; TODO: def CSS to transition date x + width
                  :style {:position      "absolute"
                          :width         (px-width num-days px-per-day)
                          :height        (px row-height)
                          :border-right  "1px solid lightgrey"
                          :border-bottom "1px solid lightgrey"
                          :transform     (translate-x x-offset)
                          :text-align    "center"
                          :white-space   "nowrap"
                          :overflow      "hidden"
                          :text-overflow "ellipsis"
                          :font-size     demo-table-font-size}}
           (if label label non-breaking-space)]))
      activities)))


(defn render-dates-dow
  [timeline-start timeline-end px-per-day]
  ;TODO fix this formatter which pulls out first char of day name.
  (let [show-content? (>= px-per-day 14) ; any smaller and don't render date label
        activities    (map
                        #(assoc % :label (when show-content? (-> % :start-date time.core/day-of-week dow-character)))
                        (timeline-activities timeline-start timeline-end :days))]
    [render-dates-row timeline-start px-per-day activities]))


(defn render-dates-dd
  [timeline-start timeline-end px-per-day]
  (let [show-content? (>= px-per-day 14) ; any smaller and don't render date label
        activities    (map
                        #(assoc % :label (when show-content?
                                           ;(date/format-with (:start-date %) format-date-dd)
                                           (time.format/unparse format-date-dd (:start-date %))
                                           ))
                        (timeline-activities timeline-start timeline-end :days))]
    [render-dates-row timeline-start px-per-day activities]))


(defn render-dates-wc
  [timeline-start timeline-end px-per-day]
  (let [activities (map
                     #(assoc % :label
                               ;(date/format-with (:start-date %) format-date-dd-mmm)
                               (time.format/unparse format-date-dd-mmm (:start-date %))
                               )
                     (timeline-activities timeline-start timeline-end :weeks))]
    [render-dates-row timeline-start px-per-day activities]))


(defn render-dates-month
  [timeline-start timeline-end px-per-day]
  (let [activities (map
                     #(assoc % :label (when (>= (* (:num-days %) px-per-day) 40)
                                        ;(date/format-with (:start-date %) format-date-mmm-yyyy)
                                        (time.format/unparse format-date-mmm-yyyy (:start-date %))
                                        ))
                     (timeline-activities timeline-start timeline-end :months))]
    [render-dates-row timeline-start px-per-day activities]))


(defn render-table-dates ;; :column-header-renderer
  [{:keys [resolution px-per-day]}]
  (let [timeline (reagent/atom timeline-data)]
    (fn table-dates-renderer
      []
      ;TODO not sure if this h-box is considered too heavy or convolutes flex vs non GR?
      [v-box
       :children [[render-dates-month timeline-start-date timeline-end-date px-per-day]
                  [render-dates-wc    timeline-start-date timeline-end-date px-per-day]
                  (when (= :days resolution)
                    [render-dates-dow timeline-start-date timeline-end-date px-per-day])
                  (when (= :days resolution)
                    [render-dates-dd  timeline-start-date timeline-end-date px-per-day])]])))

;; ---- TABLE ROW PARTS --------------------------------------------------------

(defn render-activity-row-header
  [row-header-selections _row]
  (let []
    (fn activity-row-header-renderer ;; TODO: Remove the inner fn
      [row-index row]
      (let [selected? (and (>= row-index (:start-row @row-header-selections)) (<= row-index (:end-row @row-header-selections)))]
        [:div {:class "table-row-header"
               :style (merge activity-row-style
                             (when selected?
                               {:color            sel-color
                                :background-color sel-bg-color}))}
         (let [split-label (clojure.string/split (:label row) ",")
               market   (or (first split-label) "")
               duration (or (second split-label) "")]
           [create-row-header-line market duration])
         ]))))

(defn border-color
  [color]
  ;; Return a CSSColor suitable for border based on passed color.
  ;; Either darken | lighten original color based on HSL lightness.
  ;; - color can be existing instance of garden/CSSColor or hex color string.
  (let [color     (if (string? color) color #_(color-util/hex->hsl color) color) ;; TODO: Implement this?
        lightness (:lightness color)]
    (cond
      ;(<= lightness 20) (color-util/lighten  color 80)
      ;(<= lightness 60) (color-util/lighten  color 40)
      ;(> lightness 60)  (color-util/darken color 13)
      :else color)))


;; TODO: REMOVE THIS - REPLACE IT WITH STANDARD POPOVER-ANCHOR-WRAPPER AND USE PARTS TO STYLE IT!!
(defn popover-midpoint-wrapper
  "Renders a component along with a Bootstrap popover - the popover points to the mid point of the 'anchor'"
  [& {:keys [showing? position anchor popover anchor-width anchor-height style]}]
  (let [external-position (reagent/atom position)
        internal-position (reagent/atom @external-position)
        reset-on-hide     (reaction (when-not @showing? (reset! internal-position @external-position)))]
    (fn
      [& {:keys [showing? position anchor popover anchor-width anchor-height style]}]
      @reset-on-hide ;; TODO: Need to dereference this reaction, otherwise it will never update (probably a better way to do this)
      (when (not= @external-position position) ;; Has position changed externally?
        (reset! external-position position)
        (reset! internal-position @external-position))
      [:div
       {:class "popover-midpoint-wrapper"
        :style style}
       anchor
       (when @showing?
         [:div
          {:class "popover-midpoint"
           :style {:position   "relative"
                   :z-index    4
                   :top        (px (/ anchor-height 2))
                   :left       (px (/ anchor-width 2))
                   :width      "0px"
                   :height     "0px"}}
          (into popover [:showing-injected? showing? :position-injected internal-position])])]))) ;; NOTE: Inject showing? and position to the popover


(defn render-activity
  [{:keys [px-per-day timeline editor-on]} row activity sel-start-col sel-end-col]
  (let [show-editor? (reaction (= [(:id row) (:id activity)] @editor-on))]
    ;(debug "border" background-color :lightness (:lightness border-color) "->" border-color)
    (fn activity-renderer
      [{:keys [px-per-day timeline editor-on]} row activity sel-start-col sel-end-col]
      ;; To keep things light, only wrap the currently edited activity (if any) with the open popover.
      (let [num-days  (time.core/in-days (time.core/interval (:from-date activity) (:to-date activity)))
            x-offset  (-> (time.core/in-days (time.core/interval timeline-start-date (:from-date activity))) (* px-per-day))
            x-end     (+ x-offset (- (* num-days px-per-day) 2))
            selected? (when sel-start-col
                        (if sel-must-enclose?
                          (and (>= x-offset sel-start-col) (<= x-end sel-end-col))
                          (not (or (> x-offset sel-end-col) (< x-end sel-start-col)))))
            {:keys [background-color color]
             :or   {background-color "#fffff0" color "#000000"}}  (:style activity)
            ;; given the activity background color, use same color darken | lighten for border
            border-color (border-color background-color)
            anchor [:span
                    {:style    {:position         "absolute"
                                :width            (px-width-activity num-days px-per-day)
                                :height           (px (- row-height 1)) ;; If we decide to support wider activities, gridlines overlap on following rows
                                :border-radius    "4px"
                                :border           (if selected? sel-border (str "1px solid " border-color #_(color-util/as-hex border-color)))
                                :background-color (if selected? sel-bg-color background-color)
                                :color            (if selected? sel-color color)
                                :transform        (translate-x x-offset)
                                :text-align       "center"
                                :white-space      "nowrap"
                                :overflow         "hidden"
                                :text-overflow    "ellipsis"
                                :font-size        demo-table-font-size
                                :cursor           "pointer"}
                     :on-click (handler-fn (reset! editor-on [(:id row) (:id activity)]))}
                    (:label activity)]]

        ;;TODO; use pluggable editor based on row/activity ::media-type -> editors-activity
        (if @show-editor?
          [popover-midpoint-wrapper
           :showing?      show-editor?
           :position      :below-center
           :anchor-width  (* num-days px-per-day)
           :anchor-height row-height
           :anchor        anchor
           :popover       [popover-content-wrapper
                           ;:title      "Activity Editor"
                           :body       [label :label "Popup to edit this item"]
                           :no-clip?   true
                           :style      {:margin-left (px x-offset)}
                           :on-cancel  #(reset! editor-on nil)]]
          anchor)))))


(defn render-activity-row-body
  [{:keys [px-per-day total-resolution]
    :or   {total-resolution :week} :as render-options} row-selections _row]
  (let [timeline     (reagent/atom timeline-data)
        totals-dates (reaction (timeline-activities timeline-start-date timeline-end-date total-resolution))]
    (fn activity-row-body-renderer
      [row-index row]
      (let [selected?     (and (>= row-index (:start-row @row-selections)) (<= row-index (:end-row @row-selections)))
            sel-start-col (when selected? (:start-col @row-selections))
            sel-end-col   (when selected? (:end-col   @row-selections))]
        (-> (into
              ;; Row layer 1 - the outer div
              [:div {:class    "activity-row"
                     :style    (merge activity-row-style
                                      #_(when selected? {:background-color "rgba(253, 71, 1, 0.1)"}))
                     :on-click (handler-fn (show-row-data-on-alt-click row row-index event))}]

              ;; Row layer 2 - vertical grid lines based on totals-dates
              (map-indexed
                (fn
                  [index {:keys [start-date]}]
                  (when (pos? index)
                    ; Calc offset based on how many days this date is from timeline start.
                    (let [glx-offset (-> (time.core/interval timeline-start-date start-date)
                                         (time.core/in-days)
                                         (* px-per-day))]
                      ;; draw vertical grid lines using <hr> which is a no-content
                      ;; element so it is lighter weight then e.g. div/span as we do
                      ;; not need to include non-breaking-space and in turn width.
                      ^{:key (str "vr:" index)}
                      [:hr {:class "activity-row-v-grid-line"
                            :style (assoc activity-row-v-grid-line-style
                                     :transform (translate-x glx-offset))}])))
                @totals-dates))

            ;; Row layer 3 - activities
            (into
              (map
                #(identity ^{:key (:id %)} [render-activity render-options row % sel-start-col sel-end-col]) ;; Create a render-activity component to represent a single activity
                (:activities row)))
            (with-meta {:key (:id row)}))))))


(defn gantt-chart-demo
  []
  (let [margin                "0px"
        max-content-width     (- (gob/get js/window "innerWidth") 24 16 18) ; - left/right padding + v-scroll. TODO: consider win resize
        resolution            :days ;; TODO: Create a dropdown for this. Can be :days, :weeks, :months
        total-resolution      :days
        max-rows              100
        options               {:max-content-width max-content-width
                               :resolution        resolution
                               :total-resolution  total-resolution
                               :max-rows          max-rows}
        timeline              (reagent/atom timeline-data)
        days-in-timeline      (reaction (time.core/in-days (time.core/interval timeline-start-date timeline-end-date)))
        ;;
        row-selections        (reagent/atom nil)
        row-header-selections (reagent/atom nil)
        col-header-selections (reagent/atom nil)
        ctrlKey-down?         (reagent/atom false)
        shiftKey-down?        (reagent/atom false)
        editor-on             (reagent/atom nil)]
    (fn gantt-chart-demo-render
      []
      (let [content-width     (* @days-in-timeline (get px-per-day-fixed resolution))
            px-per-day        (get px-per-day-fixed resolution)
            rendering-options (assoc options
                                :editor-on        editor-on
                                :content-width    content-width
                                :timeline         @timeline
                                :days-in-timeline @days-in-timeline
                                :px-per-day       px-per-day)]
        [v-box
         :size     "1"
         :class    "v-table-wrapper noselect"
         :margin   margin
         :children [[v-table
                     :virtual?                   true
                     :model                      timeline

                     :row-renderer               (partial render-activity-row-body rendering-options row-selections)
                     :row-selection-fn           (when-not @editor-on
                                                   (fn [selection-event coords ctrlKey shiftKey _event]
                                                     (if sel-on-mouse-up?
                                                       (when (= selection-event :selection-end)
                                                         (reset! row-selections coords)
                                                         (reset! ctrlKey-down? ctrlKey)
                                                         (reset! shiftKey-down? shiftKey))
                                                       (do (reset! row-selections coords)
                                                           (reset! ctrlKey-down? ctrlKey)
                                                           (reset! shiftKey-down? shiftKey)))))
                     :row-height                 row-height
                     :max-row-viewport-height    (* 20 row-height) ;; Note: The v-table :wrapper must have :size "none" to use this
                     :row-content-width          content-width

                     :row-header-renderer        (partial render-activity-row-header #_rendering-options row-header-selections)
                     :row-header-selection-fn    (fn [_selection-event coords ctrlKey shiftKey _event]
                                                   (reset! row-header-selections coords) ;; [aaa]
                                                   (reset! ctrlKey-down? ctrlKey)
                                                   (reset! shiftKey-down? shiftKey))
                     :top-left-renderer          render-top-left-header

                     :column-header-height       (* row-height (case resolution :days 4 2)) ; date header rows
                     :column-header-renderer     (partial render-table-dates rendering-options)
                     :column-header-selection-fn (fn [_selection-event coords ctrlKey shiftKey _event]
                                                   (reset! col-header-selections coords)
                                                   (reset! ctrlKey-down? ctrlKey)
                                                   (reset! shiftKey-down? shiftKey))
                     :parts {:wrapper                      {:style {:margin-bottom "20px"
                                                                    :margin-right  "20px"}}
                             ; 1
                             :top-left                     {:style {:border-right  table-border-style
                                                                    :border-bottom table-border-style}}
                             ; 2
                             :row-headers                  {:style {:background-color header-bg-color
                                                                    :border-left      table-border-style
                                                                    :border-right     table-border-style}}
                             ; 3
                             :bottom-left                  {:style {:border-top       table-border-style}}
                             ; 4
                             :column-headers               {:style {:background-color "#999"
                                                                    :color            "white"
                                                                    :border-top       table-border-style
                                                                    :border-bottom    table-border-style}}
                             ; 6
                             :column-footers               {:style {:border-top       table-border-style}}
                             ; 7
                             :top-right                    {:style {:border-right     table-border-style}}
                             ; 8
                             :row-footers                  {:style {:border-right     table-border-style}}
                             ; Selection styles
                             :row-selection-rect           {:style {:z-index 0
                                                                    ;:background-color "rgba(0,152,12,0.1)"
                                                                    ;:border           "1px solid rgba(0,152,12,0.4)"
                                                                    }}
                             :column-header-selection-rect {:style {:z-index          0 ;; Behind rows
                                                                    :background-color "rgba(0,152,12,0.1)" ;; Green
                                                                    :border           "1px solid rgba(0,152,12,0.4)"
                                                                    }}
                             ;:row-header-selection-rect    {:style {:background-color "rgba(0,0,0,0.02)" ;; Very transparent black  ;; [aaa]
                             ;                                       :border           "1px solid transparent" ;; Disable border
                             ;                                       }}
                             }]]]))))

(defn demo
  []
  [v-box
   :size "1"
   :children [[title2 "Demo"]
              ;; TODO: This [:p] has links which have [:div]s and that causes red warnings in DvTools - fix
              [p [:b [:i "First,"]] " the " [:i "Notes"] " part of this page contains two diagrams describing " [:code "v-table"] " which are built using " [:code "v-table"] " component. Start by looking at the "  [github-hyperlink "source code" "src/re_demo/v_table_sections.cljs"]
                  " for "     [github-hyperlink "both of them" "src/re_demo/v_table_renderers.cljs"]
                 ". They provide a bare bones introduction."]
              [p [:b [:i "Next,"]] " look at " [:code "simple-v-table"]  " (see LHS navigation) to understand what is possible if you want rectangular data displays."]
              [p [:b [:i "Finally,"]] " below is a demo showing some of the capabilities of " [:code "v-table"]  ", including:"]
              [box
               :width "450px"
               :child [:ul
                       [:li "this table will take all available horizontal space to the right - to the edge of the browser window. If you make the browser window wider or narrower, it will adjust to the new width available. The same can be done in the horizontal direction (not demostrated here)."]
                       [:li "when you click on a horizontal bars, with a data row, you'll see a fake edit popup"]
                       [:li "the column header has four rows of independently sized content (a date range)"]
                       [:li "you can click and drag to select in both the row header and data rows. if necessary, content will scroll with drag,  The selection rectangle can be hidden or styled"]
                       [:li "row rendering is automatically virtualised"]
                       [:li "this particular table only uses row and column headers, but no footer sections. And only one corner section."]
                       [:li [source-reference "for this v-table" "src/re_demo/v_table_demo.cljs"]]]]
              [gantt-chart-demo]
              [gap :size "1"]]])
