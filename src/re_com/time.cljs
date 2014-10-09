(ns re-com.time
  (:require
    [reagent.core :as reagent]
    [clojure.string :as cljstring]
    [clojure.set :refer [superset?]]
    [re-com.core :refer [label]]
    [re-com.box      :refer  [h-box gap]]
    [re-com.util :refer [pad-zero-number deref-or-value]]))


; --- Private functions ---

(defn- time-int->hour-minute
  "Convert the time integer (e.g. 930) to a vector of hour and minute."
  [time-int]
  (if (nil? time-int)
    [nil nil]
    [(quot time-int 100)
     (rem time-int 100)]))

(defn- time-integer-from-vector
  "Return a time integer.
  ASSUMPTION: the vector contains 3 values which are -
    hour, ':' or '' and minutes."
  [vals]
  (assert (= (count vals) 3) (str "Application error: re-com.time/time-integer-from-vector expected a vector of 3 values. Got " vals))
  (let [hr (first vals)
        mi (last vals)]
    (assert (or (number? hr) (nil? hr))
            (str "Application error: re-com.time/time-integer-from-vector expected first value of vector to be nil or a number. Got " hr))
    (assert (or (number? mi) (nil? mi))
            (str "Application error: re-com.time/time-integer-from-vector expected last value of vector to be nil or a number. Got " mi))
    (let [hr-int (if (nil? hr) 0 hr)
          mi-int (if (nil? mi) 0 mi)]
    (+ (* hr-int 100) mi-int))))

(defn- int-from-string
  [s]
  (if (nil? s)
    nil
    (let [val (js/parseInt s)]
      (if (js/isNaN val)
        nil
        val))))

(defn- string->time-integer
  "Return a time integer from the passed string."
  [s]
  (let [matches (re-matches #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" s)
    vals (filter (comp not nil?)(rest matches))]
    (time-integer-from-vector (map int-from-string vals))))

(defn display-string
  "Return a string display of the time.
  The format will be HH:MM."
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

(defn- time-int->display-string
  "Return a string display of the time integer."
  [time-integer]
  (if (nil? time-integer)
    (display-string [nil nil])
    (display-string (time-int->hour-minute time-integer))))

;; --- Validation ---

(defn- validate-hours
  "Validate the first element of a time vector. Return true if it is valid."
  [time-integer min max]
  (let [hr (quot time-integer 100)]
    (if hr
      (and (if (nil? min) true (>= hr (quot min 100)))(if (nil? max) true (<= hr (quot max 100))))
      true)))

(defn- validate-minutes
  "Validate the second element of a time vector. Return true if it is valid."
  [time-integer]
  (let [mi (rem time-integer 100)]
    (if mi
      (< mi 60)
      true)))

(defn- validate-time-range
  "Validate the time in comparison to the min and max values. Return true if it is valid."
  [time-integer min max]
  (and (if (nil? min) true (>= time-integer min))
       (if (nil? max) true (<= time-integer max))))

(defn- validated-time-integer
  "Validate the values in the vector.
  If any are invalid replace them with the previous valid value."
  [time-integer min max previous-val]
  (let [tm-string   (time-int->display-string time-integer)
        range-str   (str (time-int->display-string min) "-" (time-int->display-string max))]
    (if-not (validate-hours time-integer min max)
      (do
        (.info js/console (str "WARNING: Time " tm-string " is outside range " range-str))
        previous-val)
      (if (validate-minutes time-integer)
        (if (validate-time-range time-integer min max)
          time-integer
          (do
            (.info js/console (str "WARNING:  Time " tm-string " is outside range " range-str))
            previous-val))
        #_(time-integer-from-vector [(quot time-integer 100) "" 0])
        previous-val))))

(defn- valid-time-integer?
  "Return true if the passed time integer is valid."
  [time-integer min max]
  (if-not (validate-hours time-integer min max)
    false
    (if-not (validate-minutes time-integer)
      false
      (validate-time-range time-integer min max))))

(defn- validate-string
  "Return true if the passed string valdiates OK."
  [s]
  (let [matches (re-matches #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" s)
       vals (filter #(not (nil? %))(rest matches))]
    (= (count vals) 3)))  ;; Cannot do any further validation here - input must be finished first (why? because when entering 6:30, "63" is not valid)

(defn- validate-max-min
  [minimum maximum]
  (if-not (valid-time-integer? minimum nil nil)
    (throw (js/Error. (str "minimum " minimum " is not a valid time integer."))))
  (if-not (valid-time-integer? maximum nil nil)
    (throw (js/Error. (str "maximum " maximum " is not a valid time integer."))))
  (if (and minimum maximum)
    (if-not (< minimum maximum)
      (throw (js/Error. (str "maximum " maximum " is less than minimum " minimum "."))))))

(defn- time-changed
  "Triggered whenever the input field changes via key press or cut & paste."
  [ev input-model]
  (let [input-val (-> ev .-target .-value)
        valid? (validate-string input-val)]
    (when valid?
      (reset! input-model input-val))))

(defn- time-updated
  "Triggered whenever the input field loses focus.
  Re-validate what has been entered. Then update the model."
  [ev input-model min max callback previous-val]
  (let [input-val (-> ev .-target .-value)
        time-int (string->time-integer input-val)
        validated-int (validated-time-integer time-int (deref-or-value min) (deref-or-value max) previous-val)]
    (reset! input-model (display-string (time-int->hour-minute validated-int)))
    (when (and callback (not (= validated-int previous-val)))
      (callback validated-int))))

(defn- updated-range-time
  "One of the values of a range has changed. Update the min or max of the other input.
  Return true if the value has been accepted."
  [model max-or-min-model]
  (let [new-time-int (string->time-integer @model)]
    (reset! max-or-min-model new-time-int)))

(defn- updated-range-from-time
  "The From of a range has changed. Update the min of the other input.
  Send the new value to the caller using the callback."
  [model max-or-min-model previous-vals callback]
  (updated-range-time model max-or-min-model)
  (when callback
    (let [new-vals [@max-or-min-model (last previous-vals )]]
      (callback new-vals))))

(defn- updated-range-to-time
  "The To of a range has changed. Update the max of the other input.
  Send the new value to the caller using the callback."
  [model max-or-min-model previous-vals callback]
  (updated-range-time model max-or-min-model)
  (when callback
    (let [new-vals [(first previous-vals) @max-or-min-model]]
      (callback new-vals))))

(defn- atom-on
  [model default]
  (reagent/atom (if model
                  (deref-or-value model)
                   default)))

(def time-api
  #{;; REQUIRED
    :model          ;; Integer - a time integer e.g. 930 for '09:30'
    ;; OPTIONAL
    :minimum        ;; Integer - a time integer - times less than this will not be allowed - default is 0.
    :maximum        ;; Integer - a time integer - times more than this will not be allowed - default is 2359.
    :on-change      ;; function - callback will be passed new result - a time integer or nil
    :disabled       ;; boolean or reagent/atom on boolean - when true, navigation is allowed but selection is disabled.
    :show-time-icon ;; boolean - if true display a clock icon to the right of the
    :style          ;; map - optional css style information
    :hide-border    ;; boolean - hide border of the input box - default false.
    })

(defn- private-time-input
  "This is the markup for the time input."
  [model previous-val min max & {:keys [on-change disabled style hide-border show-time-icon :as args]}]
  {:pre [(superset? time-api (keys args))]}
  (let [def-style {:flex "none"
                   :margin-top "0px"
                   :padding-left "2px"
                   :padding-top "0px"
                   :font-size "11px"
                   :width "35px"}
        add-style (when hide-border {:border "none"})
        style (merge def-style add-style style)]
    [:span.input-append.bootstrap-timepicker
      [:input
        {:type "text"
         :disabled (deref-or-value disabled)
         :class "time-entry"
         :value @model
         :style style
         :on-change #(time-changed % model)
         :on-blur #(time-updated % model min max on-change previous-val)}
         (when show-time-icon
           [:span.time-icon
             [:span.glyphicon.glyphicon-time]])]]))

;; --- Components ---

(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Parameters - refer time-api above."
  [& {:keys [model minimum maximum on-change]}]
  (let [deref-model (deref-or-value model)
        input-model (atom-on (display-string (time-int->hour-minute deref-model)) "")
        min (atom-on minimum 0)
        max (atom-on maximum 2359)]
    (validate-max-min @min @max)                  ;; This will throw an error if the parameters are invalid
    (if-not (valid-time-integer? deref-model @min @max)
      (throw (js/Error. (str "model " deref-model " is not a valid time integer or is outside the min/max range."))))
     (fn [& {:keys [model disabled hide-border show-time-icon style]}]
       [private-time-input input-model (deref-or-value model) min max
         :on-change on-change
         :disabled disabled
         :hide-border hide-border
         :show-time-icon show-time-icon
         :style style])))

(defn time-range-input
  "I return the markup for a pair input boxes which will accept and validate times.
  Parameters - refer time-api above."
  [& {:keys [model minimum maximum on-change from-label to-label  hide-border show-time-icon gap style]}]
  (let [deref-model (deref-or-value model)
        input-from-model  (atom-on (display-string (time-int->hour-minute(first deref-model))) nil)
        input-to-model    (atom-on (display-string (time-int->hour-minute(last  deref-model))) nil)
        min (atom-on minimum 0)
        max (atom-on maximum 2359)]
  (validate-max-min @min @max)                  ;; This will throw an error if the parameters are invalid
  (if-not (valid-time-integer? (first deref-model) @min @max)
    (throw (js/Error. (str "model for FROM time: " @input-from-model " is not a valid time integer."))))
  (if-not (valid-time-integer? (last deref-model) @min @max)
    (throw (js/Error. (str "model for TO time: " @input-to-model " is not a valid time integer."))))
  (if-not (< (first deref-model) (last deref-model))
      (throw (js/Error. (str "TO " @input-to-model " is less than FROM " @input-from-model "."))))
  (let [from-max-model (atom-on (string->time-integer @input-to-model) nil)
        to-min-model   (atom-on (string->time-integer @input-from-model) nil)]

  (fn [& {:keys [model disabled]}]
      [h-box
        :gap (if gap gap "4px")
        :align :center
        :children [(when from-label [label :label from-label])
                   [private-time-input
                    input-from-model
                    (first (deref-or-value model))
                    min
                    from-max-model
                    :on-change #(updated-range-from-time input-from-model to-min-model (deref-or-value model) on-change)
                    :disabled disabled
                    :hide-border hide-border
                    :show-time-icon show-time-icon
                    :style style]
                   (when to-label [label :label to-label])
                   [private-time-input
                    input-to-model
                    (last (deref-or-value model))
                    to-min-model
                    max
                    :on-change #(updated-range-to-time input-to-model from-max-model (deref-or-value model) on-change)
                    :disabled disabled
                    :hide-border hide-border
                    :show-time-icon show-time-icon
                    :style style]]]))))
