(ns re-com.datetime
  (:require
    [reagent.core :as reagent]))

; --- Private functions ---

(def min-time (reagent/atom [0 0]))
(def max-time (reagent/atom [23 59]))
(def model (reagent/atom nil))

(defn first-char
  "Validate the first char of a time string. Return the corrected string."
  [input-val]
  (let [int-val (js/parseInt input-val)]
    (if (or (js/isNaN int-val)(< int-val (quot (first @min-time) 10)))
      ""
      (if (> int-val (quot (first @max-time) 10))
        (str "0" input-val ":")
        input-val))))

(defn second-char
  "Validate the first and second chars of a time string. Return the corrected string."
  [input-val]
  (if (re-matches #"[0-9]" (last input-val))
    (let [int-val (js/parseInt input-val)]
       (if (or (js/isNaN int-val)(< int-val (first @min-time))(> int-val (first @max-time)))
         (subs input-val 0 1)                ;; Not a number or not in min-max range - ignore second char
         input-val))
    (subs input-val 0 1)))

(defn fourth-char
  "Validate the fourth chars of a time string.
  If another colon is added, ignore it.
  Return the corrected string."
  [input-val]
  (let [first-3-chars (str (subs input-val 0 2) ":")]
    (if (re-matches #"[:-]" (nth input-val 3))
      first-3-chars
      (if (re-matches #"[0-9]" (last input-val))
        (let [int-val (js/parseInt (last input-val))]
          (if (or (< int-val (quot (last @min-time) 10))(> int-val (quot (last @max-time) 10)))
            first-3-chars
            input-val))
        first-3-chars)))) ;; Ignore non- alpha character

(defn third-char
  "Validate the third chars of a time string.
  If no colon is found, add that and validate the (now) fourth character.
  Return the corrected string."
  [input-val]
  (if (re-matches #"[:-]" (last input-val ))
    (str (subs input-val 0 2) ":")
    (fourth-char (str (subs input-val 0 2) ":" (last input-val)))))

(defn fifth-char
  "Validate the fifth chars of a time string.
  Return the corrected string."
  [input-val]
  (let [first-4-chars (subs input-val 0 4)]
    (if (re-matches #"[0-9]" (last input-val))
      (let [int-val (js/parseInt (subs input-val 3 5))]
        (if (or (< int-val (last @min-time))(> int-val (last @max-time)))
            first-4-chars
            input-val))
      first-4-chars)))

(defn validated-time-change [input-val]
  (let [length (.-length input-val)]
    (cond
      (= length 0) input-val
      (= length 1) (first-char input-val)
      (= length 2) (second-char input-val)
      (= length 3) (third-char input-val)
      (= length 4) (fourth-char input-val)
      (= length 5) (fifth-char input-val)
      (> length 5) (subs input-val 0 5)
      :else input-val)))

(defn time-changed [ev]
  (let [target (.-target ev)
        input-val (.-value target)]
    (set! (.-value target)
      (validated-time-change input-val))))


(defn time-updated
  "Check what has been entered is complete. If not, and if possbible, complete it. Then update the model."
  [ev]
  (let [target (.-target ev)
        input-val (.-value target)
        length (.-length input-val)]
    (cond
      (< length 3) (set! (.-value target) nil)  ;; Insufficient data to complete
      (= length 3) (set! (.-value target) (str input-val "00"))
      (= length 4) (set! (.-value target) (str input-val "0"))))
  (reset! model (.-value (.-target ev))))

;; --- Public function ---

(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Optional parameters are -
    model
    minimum-time - 2 element vec of min hour and min minute - will not allow input less than this time
    maximum-time - 2 element vec of max hour and max minute - will not allow input more than this time
    callback - function to call when model has changed - parameter will be the new value"
  [& {:keys [model callback minimum-time maximum-time style]}]
  (when minimum-time (reset! min-time minimum-time))
  (when maximum-time (reset! max-time maximum-time))
  (let [model      (if (satisfies? cljs.core/IDeref model)    @model    model)]
    [:input
      {:type "text"
       :class "time-entry"
       :style {:font-size "11px"
               :max-width "41px"
               :width "41px"
               :min-width "41px"}
      :on-change time-changed
      :on-blur time-updated}]))
