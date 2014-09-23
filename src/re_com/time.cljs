(ns re-com.time
  (:require
    [reagent.core :as reagent]
    ;;[clairvoyant.core :as trace :include-macros true]  ;; TODO remove clairvoyant - development only
    [clojure.string :as cljstring]
    [re-com.box      :refer  [h-box gap]]
    [re-com.util :refer [pad-zero-number]]))


; --- Private functions ---

(defrecord TimeRecord [hour minute second])
(defn create-time
  "Return a TimeRecord. No validation is made for hours."
  [& {:keys [hour minute second]}]
  (TimeRecord. hour minute second))

(defn create-time-from-map
  "Return a TimeRecord."
  [tm-map]
  (TimeRecord. (:hour tm-map) (:minute tm-map) (:second tm-map)))

(defn create-time-from-vector
  "Return a TimeRecord.
  ASSUMPTION: the vector contains 3 values which are -
   hour, ':' or '' and minutes."
  [vals]
  (create-time :hour (first vals) :minute (last vals) :second nil))

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

#_(defn pad-zero [subject-str max-chars]
  "If subject-str zero pad subject-str from left up to max-chars."
  (if (< (count subject-str) max-chars)
  	(apply str (take-last max-chars (concat (repeat max-chars \0) subject-str)))
  	subject-str))

#_(defn pad-zero-number [subject-num max-chars]
  "If subject-num zero pad subject-str from left up to max-chars."
  (pad-zero (str subject-num) max-chars))

#_(defn time-record->string
  "Return a string to display the time."
  [time-record]
  (str (when (:hour time-record)
         (if (or (>= (:hour time-record) 10)(not (nil? (:minute time-record))))
           (str (pad-zero-number (:hour time-record) 2))
           (:hour time-record)))
       (when (:minute time-record)
         (if (or (>= (:minute time-record) 10)(not (nil? (:second time-record))))
           (str (pad-zero-number (:minute time-record) 2))
           (:minute time-record)))
       (when (:second time-record)(:second time-record))))

(defn time-record->string
  "Return a string to display the time."
  [time-record]
  (str (if (:hour time-record)
         (str (pad-zero-number (:hour time-record) 2))
         (:hour time-record))
       (if (:minute time-record)
         (str (pad-zero-number (:minute time-record) 2))
         (:minute time-record))
       (when (:second time-record)(str (pad-zero-number (:second time-record) 2)))))

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

(defn validate-seconds
  "Validate the third element of a time vector. Return true if it is valid."
  [time-record]
  (let [se (:hour time-record)]
    (if se
      (< se 60)
      true)))

(defn validate-time-range
  "Validate the time string in comparison to the min and max values. Return true if it is valid.
  ASSUMPTION: we have already determined that both the hours and minutes components can be converted to integers."
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
        (create-time :hour nil :minute nil second nil)
        (.info js/console (str "WARNING: Time " tm-string " is outside range " range-str)))
      (if-not (validate-minutes time-record)
        (do
          (create-time :hour (:hour time-record) :minute nil second nil)
          (.info js/console (str "WARNING: Minutes of " tm-string " are invalid.")))
        (if-not (validate-seconds time-record)
          (do
            (create-time :hour (:hour time-record) :minute (:minute time-record) second nil)
            (.info js/console (str "WARNING: Seconds of " tm-string " are invalid.")))
          (if-not (validate-time-range time-record min max)
            (do
              (.info js/console (str "WARNING: Time " tm-string " is outside range " range-str))
              (create-time :hour (:hour time-record) :minute (:minute time-record) second nil))
            time-record))))))

(defn is-valid
  "Return true if the passed time is valid."
  [time-record min max]
  (if-not (validate-hours time-record min max)
    false
    (if-not (validate-minutes time-record)
      false
      (if-not (validate-seconds time-record)
        false
        (validate-time-range time-record min max)))))

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
    (if (:minute time-record)
      (str ":" (pad-zero-number (:minute time-record) 2))
      ":00")
    (when (:second time-record)(str ":" (pad-zero-number (:second time-record) 2)))))

(defn got-focus
  [ev tmp-model]
  (let [target (.-target ev)]
    (set! (.-value target)(time-record->string @tmp-model))))

(defn time-changed
  [ev tmp-model min max]
  (let [target (.-target ev)
        input-val (.-value target)
        time-record (create-time-from-string input-val)]
    (reset! tmp-model (validated-time-record time-record min max))
    (set! (.-value target)(display-string @tmp-model))))

(defn time-updated
  "Check what has been entered is complete. If not, and if possible, complete it. Then update the model."
  [ev model tmp-model min max callback]
  (let [target (.-target ev)
        input-val (.-value target)
        time-record (create-time-from-string input-val)]
    (reset! tmp-model (validated-time-record time-record min max))
    (set! (.-value target)(display-string @tmp-model)))
    (reset! model {:hour (:hour @tmp-model) :minute (:minute @tmp-model)})
  (if callback (callback @model)))  ;; TODO validate

(defn clipboard-paste
  "Prevent pasting of invalid characters."
  [ev tmp-model min max]
  (let [data (.getData (.-clipboardData ev) "text/plain")
        chrs (seq data)]
    (if (every? #(character-valid? %) chrs)
      (do
        (let [time-record (create-time-from-string data)]
          (reset! tmp-model (validated-time-record time-record min max)))  ;; TODO this is duplicated code
        true)
      (do
        (.info js/console (str "INFO: rejected paste of '" data "'"))
        false))))


;; --- Components ---

(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Required parameters -
    model - an atom of a time map like {:hour 9 :minute 30}
  Optional parameters are -
    minimum-time - default is 00:00:00 - a time map like {:hour 9 :minute 30}
    maximum-time - default is 23:59:59 - a time map like {:hour 9 :minute 30}
    callback - function to call when model has changed - parameter will be the new value
    style - css"
  [& {:keys [model]}]
  (let [tmp-model (reagent/atom (if (satisfies? cljs.core/IDeref model) @model model))]
    (fn [& {:keys [model callback minimum-time maximum-time style]}]
      (let [min (if minimum-time (create-time-from-map minimum-time) (create-time :hour 0 :minute 0 :second 0))
            max (if maximum-time (create-time-from-map maximum-time) (create-time :hour 23 :minute 59 :second 59))]
        [:span.input-append.bootstrap-timepicker
          [:input
            {:type "text"
             :class "time-entry"
             :default-value (display-string @tmp-model)  ;; TODO validate model first
             ;;:value (time-record->string @tmp-model)  ;; TODO validate model first
             :style (merge {:font-size "11px"
                            :width "35px"} style)
             :on-focus #(got-focus % tmp-model)
             :on-key-press #(key-pressed %)
             :on-paste #(clipboard-paste % tmp-model min max)
             ;;:on-change #(time-changed % tmp-model min max)
             :on-blur #(time-updated % model tmp-model min max callback)}
            ;;[:span.add-on [:i.glyphicon.glyphicon-time]]
           ]]))))

(defn time-range-input
  "I return the markup for a pair input boxes which will accept and validate times.
  Required parameters -
    model - an atom of from and to times [[hr mi][hr mi]]
  Optional parameters are -
    minimum-time - default is {:hour 0 :minute 0} - a time map like {:hour 9 :minute 30}
    maximum-time - default is {:hour 23 :minute 59} - time map like {:hour 9 :minute 30}
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
