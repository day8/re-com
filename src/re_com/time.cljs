(ns re-com.time
  (:require
    [reagent.core :as reagent]
    ;;[clairvoyant.core :as trace :include-macros true]  ;; TODO remove clairvoyant - development only
    [clojure.string :as cljstring]
    [re-com.box      :refer  [h-box gap]]
    [re-com.util :refer [pad-zero-number]]))


; --- Private functions ---

(defrecord TimeRecord [hour minute])
(defn create-time
  "Return a TimeRecord. No validation is made for hours."
  [& {:keys [hour minute]}]
  (TimeRecord. hour minute))

(defn create-time-from-map
  "Return a TimeRecord."
  [tm-map]
  (TimeRecord. (:hour tm-map) (:minute tm-map)))

(defn time-from-int
  "Return a TimeRecord."
  [tm-int]
  (TimeRecord. (quot tm-int 100) (rem tm-int 100)))

(defn create-time-from-vector
  "Return a TimeRecord.
  ASSUMPTION: the vector contains 3 values which are -
   hour, ':' or '' and minutes."
  [vals]
  (create-time :hour (first vals) :minute (last vals)))

(defn int-from-string
  [s]
  (if (nil? s)
    nil
    (let [val (js/parseInt s)]
      (if (js/isNaN val)
        nil
        val))))

(defn create-time-from-string
  "Return a TimeRecord from the passed string."
  [s]
  (let [matches (re-matches #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" s)
       vals (filter #(not (nil? %))(rest matches))]
    (create-time-from-vector (map int-from-string vals))))

(defn time-record->string
  "Return a string to display the time."
  [time-record]
  (str (when (:hour time-record)
         (if (>= (:hour time-record) 10)
           (:hour time-record)
           (str (pad-zero-number (:hour time-record) 2))))
       (when (:minute time-record)
         (str (pad-zero-number (:minute time-record) 2)))))

(defn time-record->int
  "Return the time as a time integer."
  [time-record]
  (let [hr (if (nil? (:hour time-record)) 0 (:hour time-record))
        mi (if (nil? (:minute time-record)) 0 (:minute time-record))]
  (+ (* hr 100) mi)))

;; --- Validation ---

(defn validate-hours
  "Validate the first element of a time vector. Return true if it is valid."
  [time-record min max]
  (let [hr (:hour time-record)]
    (if hr
      (and (>= hr (:hour min))(<= hr (:hour max)))
      true)))

(defn validate-minutes
  "Validate the second element of a time vector. Return true if it is valid."
  [time-record]
  (let [mi (:minute time-record)]
    (if mi
      (< mi 60)
      true)))

(defn validate-time-range
  "Validate the time in comparison to the min and max values. Return true if it is valid."
  [time-record min max]
  (let [tm-int (+ (* (:hour time-record) 100) (:minute time-record))
        minimum (+ (* (:hour min) 100)(:minute min))
        maximum (+ (* (:hour max) 100)(:minute max))]
    (if (or (< tm-int minimum)
            (> tm-int maximum))
      false
      true)))

(defn validated-time-record
  "Validate the values in the vector.
  If any are invalid replace them and the following values with nil."
  [time-record min max]
  (let [tm-string   (str (:hour time-record) ":" (:minute time-record))
        range-start (str (first min) ":" (last min))
        range-end   (str (first max) ":" (last max))
        range-str   (str range-start "-" range-end)]
    (if-not (validate-hours time-record min max)
      (do
        (create-time :hour nil :minute nil)
        (.info js/console (str "WARNING: Time " tm-string " is outside range " range-str)))
      (if-not (validate-minutes time-record)
        (do
          (create-time :hour (:hour time-record) :minute nil)
          (.info js/console (str "WARNING: Minutes of " tm-string " are invalid.")))
          time-record))))

(defn valid-time?
  "Return true if the passed time is valid."
  [time-record min max]
  (if-not (validate-hours time-record min max)
    false
    (if-not (validate-minutes time-record)
      false
      (validate-time-range time-record min max))))

(defn character-valid?
  "Return true if the character is valid."
  [ch]
  (not (nil? (re-matches #"^[\d|:]$" (str ch)))))

(defn key-pressed
  "Prevent input of invalid characters."
  [ev]
  (if (character-valid? (char (.-charCode ev)))
    true
    (do
      (.info js/console (str "INFO: rejected keyboard input of char " (.-charCode ev)))
      false)))

(defn display-string
  "Return a string display of the time."
  [time-record]
  (str
    (if (:hour time-record)
      (pad-zero-number (:hour time-record) 2)
      "00")
    ":"
    (if (:minute time-record)
      (str (pad-zero-number (:minute time-record) 2))
      "00")))

(defn got-focus
  "When the time input gets focus, select everything."
  [ev]
  (-> ev .-target .select))  ;; TODO works, but then gets deselected

(defn validate-string
  "Return true if the passed string valdiates OK."
  [s min max]
  (let [matches (re-matches #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" s)
       vals (filter #(not (nil? %))(rest matches))]
    (= (count vals) 3)))  ;; Cannot do any further validation here - input must be finished first (why? because when entering 6:30, "63" is not valid)

(defn time-changed
  [ev tmp-model min max]
  (let [target (.-target ev)
        input-val (.-value target)
        valid? (validate-string input-val min max)]
    (when valid?
      (reset! tmp-model input-val))))

(defn time-updated
  "Check what has been entered is complete. If not, and if possible, complete it. Then update the model."
  [ev model tmp-model min max callback]
  (let [target (.-target ev)
        input-val (.-value target)
        time-record (create-time-from-string input-val)]
    (reset! tmp-model (time-record->int (validated-time-record time-record min max)))
    ;;(set! (.-value target)(display-string @tmp-model)))
  (if callback (callback (time-record->int @tmp-model)))))

(defn clipboard-paste
  "Prevent pasting of invalid characters."
  [ev tmp-model min max]
  (let [data (.getData (.-clipboardData ev) "text/plain")
        chrs (seq data)]
    (if (every? #(character-valid? %) chrs)
      (do
        (let [time-record (create-time-from-string data)]
          (reset! tmp-model (time-record->int (validated-time-record time-record min max))))  ;; TODO this is duplicated code
        true)
      (do
        (.info js/console (str "INFO: rejected paste of '" data "'"))
        false))))


;; --- Components ---

(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Required parameters -
    model - an atom of a time integer like 930
  Optional parameters are -
    minimum-time - default is 0 - a time integer
    maximum-time - default is 2359 - a time integer
    on-change - function to call when model has changed - parameter will be the new value
    style - css"
  [& {:keys [model]}]
  ;; TODO validate model, min & max  first
  (let [tmp-model (reagent/atom (if (satisfies? cljs.core/IDeref model) @model model))]
    (fn [& {:keys [model on-change minimum-time maximum-time style]}]
      (let [min (if minimum-time (time-from-int minimum-time) (create-time :hour 0 :minute 0))
            max (if maximum-time (time-from-int maximum-time) (create-time :hour 23 :minute 59))]
        [:span.input-append.bootstrap-timepicker
          [:input
            {:type "text"
             :class "time-entry"
             ;;:default-value (display-string @tmp-model)
             :value @tmp-model
             :style (merge {:font-size "11px"
                            :width "35px"} style)
             :on-focus #(got-focus %)
             ;;:on-key-press #(key-pressed %)
             ;;:on-paste #(clipboard-paste % tmp-model min max)
             :on-change #(time-changed % tmp-model min max)
             :on-blur #(time-updated % model tmp-model min max on-change)}
            ;;[:span.add-on [:i.glyphicon.glyphicon-time]]
           ]]))))

#_(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

#_(defn shared-state []
  (let [val (atom "foo")]
    (fn []
      [:div
       [:p "The value is now: " @val]
       [:p "Change it here: " [atom-input val]]])))

(defn time-range-input
  "I return the markup for a pair input boxes which will accept and validate times.
  Required parameters -
    model - an atom of from and to times [[hr mi][hr mi]]
  Optional parameters are -
    minimum-time - default is {:hour 0 :minute 0} - a time map
    maximum-time - default is {:hour 23 :minute 59} - time map
    callback - function to call when model has changed - parameter will be the new value
    gap - horizontal gap between time inputs - default '4px'
    style - css"
  [& {:keys [model]}]
  (fn [& {:keys [model callback minimum-time maximum-time from-label to-label gap style]}]
    (let [deref-model (if (satisfies? cljs.core/IDeref model) @model model)
          from-model  (reagent/atom (first deref-model))
          to-model    (reagent/atom (last  deref-model))]
      [h-box
        :gap (if gap gap "4px")
        :children [(when from-label [:label from-label])
                   [time-input
                     :model from-model
                     :callback callback
                     :minimum-time minimum-time
                     :maximum-time (last deref-model)
                     :style style]
                   (when to-label [:label to-label])
                   [time-input
                     :model to-model
                     :callback callback
                     :minimum-time (first deref-model)
                     :maximum-time maximum-time
                     :style style]]])))
