(ns re-com.time
  (:require
    [reagent.core :as reagent]
    [clojure.string :as cljstring]
    [re-com.box      :refer  [h-box gap]]
    [re-com.util :refer [pad-zero-number]]))


; --- Private functions ---

(defn time-int->hour-minute
  "Convert the time integer (e.g. 930) to a vector of hour and minute."
  [time-int]
  (if (nil? time-int)
    [nil nil]
    [(quot time-int 100)
     (rem time-int 100)]))

(defn time-integer-from-vector
  "Return a TimeRecord.
  ASSUMPTION: the vector contains 3 values which are -
    hour, ':' or '' and minutes."
  [vals]
  (assert (= (count vals) 3) "Application error: re-com.time/time-integer-from-vector expected a vector of 3 values.")
  (let [hr (if (nil? (first vals)) 0 (first vals))
        mi (if (nil? (last vals)) 0 (last vals))]
  (+ (* hr 100) mi)))

(defn int-from-string
  [s]
  (if (nil? s)
    nil
    (let [val (js/parseInt s)]
      (if (js/isNaN val)
        nil
        val))))

(defn string->time-integer
  "Return a TimeRecord from the passed string."
  [s]
  (let [matches (re-matches #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" s)
       vals (filter #(not (nil? %))(rest matches))]
    (time-integer-from-vector (map int-from-string vals))))

(defn display-string
  "Return a string display of the time."
  [[hour minute]]
  (if (and (nil? hour)(nil? minute))
    ""
    (str
      (if hour
        (pad-zero-number hour 2)
        "00")
      ":"
      (if minute
        (str (pad-zero-number minute 2))
        "00"))))

(defn time-int->display-string
  "Return a string display of the time integer."
  [time-integer]
  (if (nil? time-integer)
    (display-string [nil nil])
    (display-string (time-int->hour-minute time-integer))))

;; --- Validation ---

(defn validate-hours
  "Validate the first element of a time vector. Return true if it is valid."
  [time-integer min max]
  (let [hr (quot time-integer 100)]
    (if hr
      (and (if (nil? min) true (>= hr (quot min 100)))(if (nil? max) true (<= hr (quot max 100))))
      true)))

(defn validate-minutes
  "Validate the second element of a time vector. Return true if it is valid."
  [time-integer]
  (let [mi (rem time-integer 100)]
    (if mi
      (< mi 60)
      true)))

(defn validate-time-range
  "Validate the time in comparison to the min and max values. Return true if it is valid."
  [time-integer min max]
  (and (if (nil? min) true (>= time-integer min))
       (if (nil? max) true (<= time-integer max))))

(defn validated-time-integer
  "Validate the values in the vector.
  If any are invalid replace them and the following values with nil."
  [time-integer min max]
  (let [tm-string   (time-int->display-string time-integer)
        range-str   (str (time-int->display-string min) "-" (time-int->display-string max))]
    (if-not (validate-hours time-integer min max)
      (do
        nil
        (.info js/console (str "WARNING: Time " tm-string " is outside range " range-str)))
      (if-not (validate-minutes time-integer)
        (do
          (time-integer-from-vector [(quot time-integer 100) 0])
          (.info js/console (str "WARNING: Minutes of " tm-string " are invalid.")))
          time-integer))))

(defn valid-time-integer?
  "Return true if the passed time integer is valid."
  [time-integer min max]
  (if-not (validate-hours time-integer min max)
    false
    (if-not (validate-minutes time-integer)
      false
      (validate-time-range time-integer min max))))

(defn got-focus
  "When the time input gets focus, select everything."
  [ev]
  (-> ev .-target .select))  ;; TODO works, but then gets deselected

(defn validate-string
  "Return true if the passed string valdiates OK."
  [s]
  (let [matches (re-matches #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" s)
       vals (filter #(not (nil? %))(rest matches))]
    (= (count vals) 3)))  ;; Cannot do any further validation here - input must be finished first (why? because when entering 6:30, "63" is not valid)

(defn validate-max-min
  [minimum maximum]
  (if-not (valid-time-integer? minimum nil nil)
    (throw (js/Error. (str "minimum " minimum " is not a valid time integer."))))
  (if-not (valid-time-integer? maximum nil nil)
    (throw (js/Error. (str "maximum " maximum " is not a valid time integer."))))
  (if (and minimum maximum)
    (if-not (< minimum maximum)
      (throw (js/Error. (str "maximum " maximum " is less than minimum " minimum "."))))))

(defn time-changed
  "Triggered whenever the input field changes via key press on cut & paste."
  [ev tmp-model]
  (let [input-val (-> ev .-target .-value)
        valid? (validate-string input-val)]
    (when valid?
      (reset! tmp-model input-val))))

(defn time-updated
  "Triggered whenever the input field loses focus.
  Re-validate been entered. Then update the model."
  [ev tmp-model min max callback]
  (let [input-val (-> ev .-target .-value)
        time-int (string->time-integer input-val)]
    (reset! tmp-model (display-string (time-int->hour-minute (validated-time-integer time-int @min @max))))
  (when callback (callback time-int))))

(defn dereffed-model
  [model]
  (if (satisfies? cljs.core/IDeref model)
    @model
    model))

(defn atom-on
  [model default]
  (reagent/atom (if model
                  (dereffed-model model)
                   default)))

;; --- Components ---
(defn private-time-input
  [model min max & {:keys [on-change disabled style]}]
        [:span.input-append.bootstrap-timepicker
          [:input
            {:type "text"
             :disabled (if (satisfies? cljs.core/IDeref disabled) @disabled disabled)
             :class "time-entry"
             :value @model
             :style (merge {:font-size "11px"
                            :width "35px"} style)
             :on-focus #(got-focus %)
             :on-change #(time-changed % model)
             :on-mouse-up #(.preventDefault %)    ;; Chrome browser deselects on mouse up - prevent this from happening
             :on-blur #(time-updated % model min max on-change)}
            ;;[:span.add-on [:i.glyphicon.glyphicon-time]]
           ]])

(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Required parameters -
    model - an atom of a time integer like 930
  Optional parameters are -
    minimum - default is 0 - an atom of a time integer
    maximum - default is 2359 - an atom of a time integer
    disabled - boolean - default false
    on-change - function to call when model has changed - parameter will be the new value
    style - css"
  [& {:keys [model minimum maximum]}]
  (let [deref-model (dereffed-model model)
        tmp-model (atom-on (display-string (time-int->hour-minute deref-model)) "")
        min (atom-on minimum 0)
        max (atom-on maximum 2359)]
    (validate-max-min @min @max)                  ;; This will throw an error if the parameters are invalid
    (if-not (valid-time-integer? deref-model @min @max)
      (throw (js/Error. (str "model " deref-model " is not a valid time integer."))))
    [private-time-input tmp-model min max]))

#_(defn time-range-input
  "This doesn't work because the model of time-input is a string, but the min and max are integers.

  I return the markup for a pair input boxes which will accept and validate times.
  Required parameters -
    model - an atom of from and to times [from-int to-int]
  Optional parameters are -
    minimum - default is 0} - a time integer
    maximum - default is 2359 - time integer
    callback - function to call when model has changed - parameter will be the new value
    gap - horizontal gap between time inputs - default '4px'
    style - css"
  [& {:keys [model minimum maximum]}]
  (let [deref-model (dereffed-model model)
        from-model  (atom-on (display-string (first deref-model)) nil)
        to-model    (atom-on (display-string (last  deref-model)) nil)
        min (atom-on minimum 0)
        max (atom-on maximum 2359)]
  (validate-max-min @min @max)                  ;; This will throw an error if the parameters are invalid
  (if-not (valid-time-integer? @from-model @min @max)
    (throw (js/Error. (str "model for FROM time: " @from-model " is not a valid time integer."))))
  (if-not (valid-time-integer? @to-model @min @max)
    (throw (js/Error. (str "model for TO time: " @to-model " is not a valid time integer."))))
  (if-not (< @from-model @to-model)
      (throw (js/Error. (str "TO " @to-model " is less than FROM " @from-model "."))))

  (fn [& {:keys [on-change from-label to-label gap style]}]
      [h-box
        :gap (if gap gap "4px")
        :children [(when from-label [:label from-label])
                   [private-time-input from-model min to-model]
                   (when to-label [:label to-label])
                   [private-time-input to-model from-model max]
                   ]])))
