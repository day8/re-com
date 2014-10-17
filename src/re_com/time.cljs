(ns re-com.time
  (:require
    [reagent.core :as reagent]
    [clojure.string :as cljstring]
    [clojure.set :refer [superset?]]
    [re-com.core :refer [label]]
    [re-com.box :refer [h-box gap]]
    [re-com.util :refer [pad-zero-number deref-or-value]]))


(defn- time->mins
  [time]
  (rem time 100))


(defn- time->hrs
  [time]
  (quot time 100))

(defn- to-int
  "Parse the string 's' to a valid int. On parse failure, return 0."
  [s]
  (let [val (js/parseInt s)]
    (if (js/isNaN val) 0 val)))

(defn- triple->time
  "Return a time integer from a triple int vector of form  [H  _  M]"
  [[hr _ mi]]
  (+ (* hr 100) mi))                                        ;; a four digit integer:  HHMM


;; This regular expression matchs all valid forms of time entry, including partial
;; forms which happen during user entry.
;; It is composed of 3 'or' options, seperated by '|',  and within each, is a sub-re which
;; attempts to match the HH ':' MM parts.
;; So any attempt to match against this re, using "re-matches" will return
;; a vector of 10 items:
;;   - the 1st item will be the entire string matched
;;   - followed by 3 groups of 3.
(def ^{:private true}
  triple-seeking-re #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$")

(defn- extract-triple-from-text
  [text]
  (->> text
       (re-matches triple-seeking-re)                       ;; looks for different ways of matching triples   H : M
       (rest)                                               ;; get rid of the first value. It is the entire matched string.
       (filter (comp not nil?))))                           ;; of the 9 items, there should be only 3 non-nil matches coresponding to  H : M


(defn- text->time
  "return as a time int, the contents of 'text'"
  [text]
  (->> text
       extract-triple-from-text
       (map to-int)                                         ;; make them ints (or 0)
       triple->time))                                       ;; turn the triple of values into a single int


(defn- time->text
  "Return a string of format HH:MM for time"
  [time]
  (let [hrs (time->hrs time)
        mins (time->mins time)]
    (str (pad-zero-number hrs 2) ":" (pad-zero-number mins 2))))

(defn- valid-text?
  "Return true if text passes basic time validation.
  Can't do to much validation because user input might not be finished.
  Why?  On the way to entering 6:30, you must pass through the invalid state of '63'.
  So we only really check against the triple-extracting regular expression."
  [text]
  (= 3 (count (extract-triple-from-text text))))

(defn- valid-time?
  [time]
  (cond
    (nil? time) false                                       ;; can't be nil
    (> 0 time) false                                        ;; must be a poistive number
    (< 60 (time->mins time)) false                          ;; too many mins
    :else true))

(defn- validate-arg-times
  [model minimum maximum]
  (assert (and (number? model) (valid-time? model)) (str "[time-input] given an invalid :model - " model))
  (assert (and (number? minimum) (valid-time? minimum)) (str "[time-input] given an invalid :minimum - " minimum))
  (assert (and (number? maximum) (valid-time? maximum)) (str "[time-input] given an invalid :maximum - " maximum))
  (assert (<= minimum maximum) (str "[time-input] :minimum " minimum " > :maximum  " maximum))
  true)

(defn- force-valid-time
  "Validate the time supplied.
  Return either the time or, if it is invalid, return something valid."
  [time min max previous]
  (cond
    (nil? time) previous
    (not (valid-time? time)) previous
    (< time min) min
    (< max time) max
    :else time))

(defn- on-new-keypress
  "Called each time the <input> field gets a keypress, or paste operation.
  Rests  the text-model only if the new text is valid."
  [event text-model]
  (let [current-text (-> event .-target .-value)]           ;; gets the current input field text
    (when (valid-text? current-text)
      (reset! text-model current-text))))

(defn- lose-focus-if-enter
  "When Enter is pressed, force the component to lose focus."
  [ev]
  (when (= (.-keyCode ev) 13)
    (-> ev .-target .blur)
    true))

(defn- on-defocus
  "Called when the field looses focus.
  Re-validate what has been entered, comparing to mins and maxs.
  Invoke the callback as necessary."
  [text-model min max callback previous-val]
  (let [time (text->time @text-model)
        time (force-valid-time time min max previous-val)]
    (reset! text-model (time->text time))
    (when (and callback (not= time previous-val))
      (callback time))))

;; TODO: should we add "class" as an arg

(def time-input-args
  #{;; REQUIRED
    :model                               ;; Integer - a time integer e.g. 930 for '09:30'
    ;; OPTIONAL
    :minimum                             ;; Integer - a time integer - times less than this will not be allowed - default is 0.
    :maximum                             ;; Integer - a time integer - times more than this will not be allowed - default is 2359.
    :on-change                           ;; function - callback will be passed new result - a time integer or nil
    :disabled                            ;; boolean or reagent/atom on boolean - when true, navigation is allowed but selection is disabled.
    :show-icon                           ;; boolean - if true display a clock icon to the right of the
    :style                               ;; map - optional css style information
    :hide-border                         ;; boolean - hide border of the input box - default false.
    :class                               ;; string - class for css styling
    })


(defn time-input
  "I return the markup for an input box which will accept and validate times.
  Parameters - refer time-input-args above."
  [& {:keys [model on-change class style] :as args
      :or   {minimum 0 maximum 2359}}]

  {:pre [(superset? time-input-args (keys args))]}

  (let [deref-model (deref-or-value model)
        text-model (reagent/atom (time->text deref-model))
        previous-model (reagent/atom deref-model)]

    (fn [& {:keys [model minimum maximum disabled hide-border show-icon] :as passthrough-args}]

      {:pre [(and (superset? time-input-args (keys passthrough-args))
                  (validate-arg-times deref-model minimum maximum))]}

      (let [style (merge (when hide-border {:border "none"})
                         style)
            new-val (deref-or-value model)
            new-val (if (< new-val minimum) minimum new-val)
            new-val (if (> new-val maximum) maximum new-val)]
        ;; if the model is different to that currently shown in text, then reset the text to match
        ;; other than that we want to keep the current text, because the user is probably typing
        (when (not= @previous-model new-val)
          (reset! text-model (time->text new-val))
          (reset! previous-model new-val))

        [:span.input-append {:style {:flex "none"}}
         [:input
          {:type      "text"
           :disabled  (deref-or-value disabled)
           :class     (if class (str "time-entry " class) "time-entry")
           :value     @text-model
           :style     style
           :on-change #(on-new-keypress % text-model)
           :on-blur   #(on-defocus text-model minimum maximum on-change @previous-model)
           :on-key-up #(lose-focus-if-enter %)}]
         (when show-icon
           [:span.time-icon [:span.glyphicon.glyphicon-time]])]))))

