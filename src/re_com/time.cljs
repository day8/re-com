(ns re-com.time
  (:require
    [reagent.core :as reagent]
    [re-com.box      :refer  [h-box gap]]))

; --- Private functions ---

(defn fifth-char
  "Validate the fifth chars of a time string.
  Return the corrected string."
  [input-val min max]
  (let [first-4-chars (subs input-val 0 4)]
    (if (re-matches #"[0-9]" (last input-val))
      (let [int-val (js/parseInt (subs input-val 3 5))]
        (if (or (< int-val (last min))(> int-val (last max)))
            first-4-chars
            input-val))
      first-4-chars)))

(defn fourth-char
  "Validate the fourth chars of a time string.
  If another colon is added, ignore it.
  Return the corrected string."
  [input-val min max]
  (let [first-3-chars (str (subs input-val 0 2) ":")]
    (if (re-matches #"[:-]" (nth input-val 3))
      first-3-chars
      (if (re-matches #"[0-9]" (last input-val))
        (let [int-val (js/parseInt (last input-val))]
          (if (or (< int-val (quot (last min) 10))(> int-val (quot (last max) 10)))
            first-3-chars
            input-val))
        first-3-chars)))) ;; Ignore non- alpha character

(defn third-char
  "Validate the third chars of a time string.
  If no colon is found, add that and validate the (now) fourth character.
  Return the corrected string."
  [input-val min max]
  (if (re-matches #"[:-]" (last input-val ))
    (str (subs input-val 0 2) ":")
    (fourth-char (str (subs input-val 0 2) ":" (last input-val)) min max)))

(defn second-char
  "Validate the first and second chars of a time string. Return the corrected string."
  [input-val min max]
  (if (re-matches #"[0-9]" (last input-val))
    (let [int-val (js/parseInt input-val)]
       (if (or (js/isNaN int-val)(< int-val (first min))(> int-val (first max)))
         (subs input-val 0 1)                ;; Not a number or not in min-max range - ignore second char
         input-val))
    (subs input-val 0 1)))

(defn first-char
  "Validate the first char of a time string. Return the corrected string."
  [input-val min max]
  (let [int-val (js/parseInt input-val)]
    (if (or (js/isNaN int-val)(< int-val (quot (first min) 10)))
      ""
      (if (> int-val (quot (first max) 10))
        (let [tmp (second-char (str "0" input-val) min max)]  ;; Treat it as second char, but validate it too
          (if (= "0" tmp)
            ""
            tmp))
        input-val))))

(defn validate-hours
  "Validate the first and second characters of a time string. Return true if it is valid."
  [s min max]
  (if s
    (let [int-val (js/parseInt s)]
      (not (js/isNaN int-val)))
    false))

(defn validate-third-char
  "Validate the third character of a time string. Return true if it is valid."
  [ch min max]
  (if ch
    (= \: ch)
    false))

(defn validate-minutes
  "Validate the fourth and fifth characters of a time string. Return true if it is valid."
  [s min max]
  (if s
    (let [int-val (js/parseInt s)]
      (if (js/isNaN int-val)
        false
        (< int-val 60)))
    false))

(defn validate-time-range
  "Validate the time string in comparison to the min and max values. Return true if it is valid.
  ASSUMPTION: we have already determined that both the hours and minutes components can be converted to integers."
  [hour minute min max]
  (let [hour-int (js/parseInt hour)
        minute-int (js/parseInt minute)
        tm-int (+ (* hour-int 100) minute-int)
        minimum (+ (* (first min) 100)(last min))
        maximum (+ (* (first max) 100)(last max))]
    (if (or (< tm-int minimum)
            (> tm-int maximum))
      (do
        (let [tm-string   (str hour ":" minute)
              range-start (str (first min) ":" (last min))
              range-end   (str (first max) ":" (last max))
              range-str   (str range-start "-" range-end)]
          (.warn js/console (str "WARNING: Time " tm-string " is outside range " range-str)))
        false)
      true)))

(defn validate-groups
  [tmp-model min max]
  (if-not (validate-hours (subs @tmp-model 0 2) min max)
    (do (reset! tmp-model "") false)
    true)
  (if-not (validate-third-char (nth @tmp-model 2) min max)
    (do ((reset! tmp-model (subs @tmp-model 0 2))) false)
    true)
  (if-not (validate-minutes (subs @tmp-model 3 5) min max)
    (do (reset! tmp-model (subs @tmp-model 0 3))false)
    true)
  (if-not (validate-time-range (subs @tmp-model 0 2)(subs @tmp-model 3 5) min max)
    (do
      (reset! tmp-model (subs @tmp-model ""))
      false)
    true))

(defn validate-time-string
  "Validate each character in the string.
  Remove it and subsequent characters if the character is not valid."
  [tmp-model min max]
  (if @tmp-model
    (if-not (= 5 (count @tmp-model))
      false
      (validate-groups tmp-model min max))
    false))

(defn is-valid
  "Return true if the passed time string is valid.
  During validation of each character, if any invalid characters are found they, and all following characters, are deleted.
  At the end we can assume a time of the correct length is valid."
  [tmp-model min max]
  (if (not (validate-time-string tmp-model min max))
    (reset! tmp-model ""))
  (and (not (nil? @tmp-model))(= 5 (count @tmp-model))))

(defn validated-time-change
  "Starting at the first character, perform the validation for each character until we have
  reached the end (which might come sooner than originally expected because if an invalid
  value is encountered the model will be truncated)."
  [chars min max n]
  (let [funcs [first-char second-char third-char fourth-char fifth-char]]
    (if (< n (count chars))
       ((nth funcs n) chars min max))))

(defn validate-each-character [tmp-model min max]
  (loop [i 0]
    (let [chars (subs @tmp-model 0 (+ i 1))
          new-val (validated-time-change chars min max i)]
      (if (= new-val chars)
        (if (and (< i 4)(< (+ 1 i)(count @tmp-model)))
          (recur (inc i)))
        (reset! tmp-model new-val)))))

(defn time-changed [ev tmp-model min max]
  (let [target (.-target ev)
        input-val (.-value target)]
     (reset! tmp-model input-val)
     (validate-each-character tmp-model min max)))
    ;;(set! (.-value target) new-val)
    ;;(when (= 5 (count new-val)) ;; tiem is complete - lose focus?

(defn time-updated
  "Check what has been entered is complete. If not, and if possible, complete it. Then update the model."
  [ev model tmp-model min max callback]
  (let [length (count @tmp-model)]
    (cond
      (= length 0) (reset! tmp-model nil)  ;; Insufficient data to complete
      (= length 1) (reset! tmp-model (str "0" @tmp-model ":00"))
      (= length 2) (reset! tmp-model (str @tmp-model ":00"))
      (= length 3) (reset! tmp-model (str @tmp-model "00"))
      (= length 4) (reset! tmp-model (str @tmp-model "0"))))
  (validate-time-string tmp-model min max)
  (reset! model @tmp-model)
  (if callback (callback @model)))

(defn pad-zero [subject-str max-chars]
  "If subject-str zero pad subject-str from left up to max-chars."
  (if (< (count subject-str) max-chars)
  	(apply str (take-last max-chars (concat (repeat max-chars \0) subject-str)))
  	subject-str))

;; --- Public function ---

(defn model-str
  "Return a string representation of the model."
  [mdl]
  (let [hr-mi (if (satisfies? cljs.core/IDeref mdl) @mdl mdl)
        hr (first hr-mi)
        mi (last hr-mi)]
    #_(str hr ":" mi)
    (str (pad-zero (str hr) 2) ":" (pad-zero (str mi) 2))))

(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Required parameters -
    model - an atom of [hr mi]
  Optional parameters are -
    minimum-time - default is [0 0] - a 2 element vector of minimum hour and minute
    maximum-time - default is [23 59] - a 2 element vector of maximum hour and minute
    callback - function to call when model has changed - parameter will be the new value
    style - css"
  [& {:keys [model]}]
  (let [tmp-model (reagent/atom (model-str (if (satisfies? cljs.core/IDeref model) @model model)))]
    (fn [& {:keys [model callback minimum-time maximum-time style]}]
      (let [min (if minimum-time minimum-time [0 0])
            max (if maximum-time maximum-time [23 59])]
          [:input
            {:type "text"
             :class "time-entry"
             :value @tmp-model  ;; TODO validate this first
             :style {:font-size "11px"
                     :max-width "35px"
                     :width "35px"
                     :min-width "35px"}
            :on-change #(time-changed % tmp-model min max)
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
  (fn [& {:keys [model callback minimum-time maximum-time gap style]}]
    (let [deref-model (if (satisfies? cljs.core/IDeref model) @model model)]
      [h-box
        :gap (if gap gap "4px")
        :children [[time-input
                     :model (first deref-model)
                     :callback callback
                     :minimum-time minimum-time
                     :maximum-time (last  deref-model)
                     :style style]
                   [time-input
                     :model (last  deref-model)
                     :callback callback
                     :minimum-time (first deref-model)
                     :maximum-time maximum-time
                     :style style]]])))
