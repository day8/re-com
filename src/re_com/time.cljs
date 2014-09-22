(ns re-com.time
  (:require
    [reagent.core :as reagent]
    ;;[clairvoyant.core :as trace :include-macros true]  ;; TODO remove clairvoyant - development only
    [clojure.string :as cljstring]
    [re-com.box      :refer  [h-box gap]]))


; --- Private functions ---

(defrecord TimeRecord [hour minute second])
(defn create-time
  "Return a TimeRecord. No validation is made for hours."
  [& {:keys [hour minute second]}]
  ;;(assert (or (nil? minute)(< minute 60)) "Invalid value for minutes")
  ;;(assert (or (nil? second)(< second 60)) "Invalid value for seconds")
  (TimeRecord. hour minute second))

(defrecord DisplayedTimeRecord [displayed-string time-record])
(defn create-displayed-time
  "Return a DisplayedTimeRecord."
  [displayed-string time-record]
  (DisplayedTimeRecord. displayed-string time-record))

(defn create-time-from-vector
  "Return a TimeRecord.
  ASSUMPTION: the vector contains 3 values which are -
   hour, ':'|'', minutes."
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

#_(defn string-as-model-values
  "Convert string values to a 3 element vector with hour, minute and second (or part thereof)."
  [vals]
  (let [hr (if (> (count vals)0) (int-from-string (first vals)) nil)
        mi (if (> (count vals)2) (int-from-string (last vals)) nil)]
    [hr mi nil]))

(defn create-time-from-string
  "Return a TimeRecord from the passed string."
  [s]
  (let [matches (re-matches #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" s)
       vals (filter #(not (nil? %))(rest matches))]
    (create-time-from-vector (map int-from-string vals))))


(defn pad-zero [subject-str max-chars]
  "If subject-str zero pad subject-str from left up to max-chars."
  (if (< (count subject-str) max-chars)
  	(apply str (take-last max-chars (concat (repeat max-chars \0) subject-str)))
  	subject-str))

(defn pad-zero-number [subject-num max-chars]
  "If subject-num zero pad subject-str from left up to max-chars."
  (pad-zero (str subject-num) max-chars))

(defn time-record->string
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

(defn validated-time-record
  "Validate the values in the vector.
  If any are invalid replace them and the following values with nil."
  [time-record min max]
  (if-not (validate-hours time-record min max)
    (create-time :hour nil :minute nil second nil)
    (if-not (validate-minutes time-record)
      (create-time :hour (:hour time-record) :minute nil second nil)
      (if-not (validate-seconds time-record)
        (create-time :hour (:hour time-record) :minute (:minute time-record) second nil)
        time-record))))


(defn validate-time-range
  "Validate the time string in comparison to the min and max values. Return true if it is valid.
  ASSUMPTION: we have already determined that both the hours and minutes components can be converted to integers."
  [time-record min max]
  (let [tm-int (+ (* (:hour time-record) 100) (:minute time-record))
        minimum (+ (* (first min) 100)(last min))
        maximum (+ (* (first max) 100)(last max))]
    (if (or (< tm-int minimum)
            (> tm-int maximum))
      (do
        (let [tm-string   (str (:hour time-record) ":" (:minute time-record))
              range-start (str (first min) ":" (last min))
              range-end   (str (first max) ":" (last max))
              range-str   (str range-start "-" range-end)]
          (.warn js/console (str "WARNING: Time " tm-string " is outside range " range-str)))
        false)
      true)))

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


;;------------------------------------------------------------------

(defn key-pressed
  "Prevent input of invalid characters.
  Event properties are -
    boolean altKey
    Number charCode
    boolean ctrlKey
    function getModifierState(key)
    String key
    Number keyCode
    String locale
    Number location
    boolean metaKey
    boolean repeat
    boolean shiftKey
    Number which"
  [ev]
  (let [target (.-target ev)
        input-val (.-value target)]
    (let [match (re-matches #"^[\d|:]$" (char (.-charCode ev)))]
    (if (nil? match)
      (do
        (println (str "rejected char " (.-charCode ev)))
        false)
      (do
        #_(println (str "accepted char " (.-charCode ev)))
        true)))))

(defn display-string
  "Return a string display of the time."
  [time-record]
  (str (when (:hour time-record)  (pad-zero-number (:hour time-record) 2))
    (when (:minute time-record)(str ":" (pad-zero-number (:minute time-record) 2)))
    (when (:second time-record)(str ":" (pad-zero-number (:second time-record) 2)))))

(defn time-updated
  "Check what has been entered is complete. If not, and if possible, complete it. Then update the model."
  [ev model tmp-model min max callback]
  (let [target (.-target ev)
        input-val (.-value target)
        time-record (create-time-from-string input-val)]
    (reset! tmp-model (validated-time-record time-record min max))
    (set! (.-value target)(display-string @tmp-model)))  ;; Show formatted result
  (if callback (callback @model)))  ;; TODO validate, then update the model

;; --- Public function ---

(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Required parameters -
    model - an atom of a time vector
  Optional parameters are -
    minimum-time - default is 00:00:00 - a time vector of minimum hour, minute and second
    maximum-time - default is 23:59:59 - a element vector of maximum hour, minute and second
    callback - function to call when model has changed - parameter will be the new value
    style - css"
  [& {:keys [model]}]
  (let [tmp-model (reagent/atom (if (satisfies? cljs.core/IDeref model) @model model))]
    (fn [& {:keys [model callback minimum-time maximum-time style]}]
      (let [min (if minimum-time minimum-time (create-time :hour 0 :minute 0 :second 0))
            max (if maximum-time maximum-time (create-time :hour 23 :minute 59 :second 59))]
          [:input
            {:type "text"
             :class "time-entry"
             ;;:value (time-record->string @tmp-model)  ;; TODO validate model first
             :style (merge {:font-size "11px"
                            :width "35px"} style)
             :on-key-press #(key-pressed %)
             ;;:on-change #(time-changed % tmp-model min max)
             :on-blur #(time-updated % model tmp-model min max callback)}]))))

(defn time-range-input
  "I return the markup for a pair input boxes which will accept and validate times.
  Required parameters -
    model - an atom of from and to times [[hr mi][hr mi]]
  Optional parameters are -
    minimum-time - default is [0 0] - a 2 element vector of minimum hour and minute
    maximum-time - default is [23 59] - a 2 element vector of maximum hour and minute
    callback - function to call when model has changed - parameter will be the new value
    gap - horizontal gap between time inputs - default '4px'
    style - css"
  [& {:keys [model]}]
  (fn [& {:keys [model callback minimum-time maximum-time from-label to-label gap style]}]
    (let [deref-model (if (satisfies? cljs.core/IDeref model) @model model)]
      [h-box
        :gap (if gap gap "4px")
        :children [(when from-label [:label from-label])
                   [time-input
                     :model (first deref-model)
                     :callback callback
                     :minimum-time minimum-time
                     :maximum-time (last  deref-model)
                     :style style]
                   (when to-label [:label to-label])
                   [time-input
                     :model (last  deref-model)
                     :callback callback
                     :minimum-time (first deref-model)
                     :maximum-time maximum-time
                     :style style]]])))
