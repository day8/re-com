(ns re-com.input-time
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [reagent.core    :as    reagent]
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :as debug :refer [->attr]]
    [re-com.validate :refer [css-style? html-attr? parts? number-or-string?]]
    [re-com.text     :refer [label]]
    [re-com.box      :refer [h-box gap]]
    [re-com.util     :refer [pad-zero-number deref-or-value]]))


(defn- time->mins
  [time]
  (rem time 100))


(defn- time->hrs
  [time]
  (quot time 100))

(defn- to-int
  "Parse the string 's' to a valid int. On parse failure, return 0"
  [s]
  (let [val (js/parseInt s)]
    (if (js/isNaN val) 0 val)))

(defn- triple->time
  "Return a time integer from a triple int vector of form  [H  _  M]"
  [[hr _ mi]]
  (+ (* hr 100) mi))                                        ;; a four digit integer:  HHMM


;; This regular expression matchs all valid forms of time entry, including partial
;; forms which happen during user entry.
;; It is composed of 3 'or' options, separated by '|',  and within each, is a sub-re which
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


(defn text->time
  "return as a time int, the contents of 'text'"
  [text]
  (->> text
       extract-triple-from-text
       (map to-int)                                         ;; make them ints (or 0)
       triple->time))                                       ;; turn the triple of values into a single int


(defn time->text
  "return a string of format HH:MM for 'time'"
  [time]
  (let [hrs  (time->hrs  time)
        mins (time->mins time)]
    (str (pad-zero-number hrs 2) ":" (pad-zero-number mins 2))))

(defn valid-text?
  "Return true if text passes basic time validation.
   Can't do to much validation because user input might not be finished.
   Why?  On the way to entering 6:30, you must pass through the invalid state of '63'.
   So we only really check against the triple-extracting regular expression"
  [text]
  (= 3 (count (extract-triple-from-text text))))

(defn valid-time?
  [time]
  (cond
    (nil? time) false                                       ;; can't be nil
    (> 0 time) false                                        ;; must be a poistive number
    (< 60 (time->mins time)) false                          ;; too many mins
    :else true))

(defn- validate-arg-times
  [model minimum maximum args]
  (when-let [message (cond
                       (not (and (number? model) (valid-time? model)))
                       (str "[input-time] given an invalid :model - " model)

                       (not (and (number? minimum) (valid-time? minimum)))
                       (str "[input-time] given an invalid :minimum - " minimum)

                       (not (and (number? maximum) (valid-time? maximum)))
                       (str "[input-time] given an invalid :maximum - " maximum)

                       (not (<= minimum maximum))
                       (str "[input-time] :minimum " minimum " > :maximum  " maximum)

                       :default
                       nil)]
    [debug/validate-args-error
      :component "input-time"
      :args      args
      :problems  [{:problem            :validate-fn-map
                   :validate-fn-result {:message message}}]]))


(defn- force-valid-time
  "Validate the time supplied.
   Return either the time or, if it is invalid, return something valid"
  [time min max previous]
  (cond
    (nil? time) previous
    (not (valid-time? time)) previous
    (< time min) min
    (< max time) max
    :else time))

(defn- on-new-keypress
  "Called each time the <input> field gets a keypress, or paste operation.
   Rests  the text-model only if the new text is valid"
  [event text-model]
  (let [current-text (-> event .-target .-value)]           ;; gets the current input field text
    (when (valid-text? current-text)
      (reset! text-model current-text))))

(defn- lose-focus-if-enter
  "When Enter is pressed, force the component to lose focus"
  [ev]
  (when (= (.-keyCode ev) 13)
    (-> ev .-target .blur)
    true))

(defn- on-defocus
  "Called when the field looses focus.
   Re-validate what has been entered, comparing to mins and maxs.
   Invoke the callback as necessary"
  [text-model min max callback previous-val]
  (let [time (text->time @text-model)
        time (force-valid-time time min max previous-val)]
    (reset! text-model (time->text time))
    (when (and callback (not= time previous-val))
      (callback time))))

(def input-time-parts-desc
  (when include-args-desc?
    [{:name :wrapper             :level 0 :class "rc-input-time "         :impl "[input-time]" :notes "Outer wrapper of the time input."}
     {:type :legacy              :level 1 :class "rc-time-entry"          :impl "[:input]"     :notes "The actual input field."}
     {:name :time-icon-container :level 1 :class "rc-time-icon-container" :impl "[:div]"       :notes "The time icon container."}
     {:name :time-icon           :level 2 :class "rc-time-icon"           :impl "[:i]"         :notes "The time icon."}]))

(def input-time-parts
  (when include-args-desc?
    (-> (map :name input-time-parts-desc) set)))

(def input-time-args-desc
  (when include-args-desc?
    [{:name :model        :required true                   :type "integer | string | r/atom" :validate-fn number-or-string?         :description "a time in integer form. e.g. '09:30am' is 930"}
     {:name :on-change    :required true                   :type "integer -> nil"            :validate-fn fn?                       :description "called when user entry completes and value is new. Passed new value as integer"}
     {:name :minimum      :required false :default 0       :type "integer | string"          :validate-fn number-or-string?         :description "user can't enter a time less than this value"}
     {:name :maximum      :required false :default 2359    :type "integer | string"          :validate-fn number-or-string?         :description "user can't enter a time more than this value"}
     {:name :disabled?    :required false :default false   :type "boolean | r/atom"                                                 :description "when true, user input is disabled"}
     {:name :show-icon?   :required false :default false   :type "boolean"                                                          :description "when true, a clock icon will be displayed to the right of input field"}
     {:name :hide-border? :required false :default false   :type "boolean"                                                          :description "when true, input filed is displayed without a border"}
     {:name :width        :required false                  :type "string"                    :validate-fn string?                   :description "standard CSS width setting for width of the input box (excluding the icon if present)"}
     {:name :height       :required false                  :type "string"                    :validate-fn string?                   :description "standard CSS height setting"}
     {:name :class        :required false                  :type "string"                    :validate-fn string?                   :description "CSS class names, space separated (applies to the textbox, not the wrapping div)"}
     {:name :style        :required false                  :type "CSS style map"             :validate-fn css-style?                :description "CSS style. e.g. {:color \"red\" :width \"50px\"} (applies to the textbox, not the wrapping div)"}
     {:name :attr         :required false                  :type "HTML attr map"             :validate-fn html-attr?                :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the textbox, not the wrapping div)"]}
     {:name :parts        :required false                  :type "map"                       :validate-fn (parts? input-time-parts) :description "See Parts section below."}
     {:name :src          :required false                  :type "map"                       :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as     :required false                  :type "map"                       :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn input-time
  "I return the markup for an input box which will accept and validate times.
   Parameters - refer input-time-args above"
  [& {:keys [model minimum maximum] :as args
      :or   {minimum 0 maximum 2359}}]
  (or
    (validate-args-macro input-time-args-desc args)
    (validate-arg-times (deref-or-value model) minimum maximum args)
    (let [deref-model    (deref-or-value model)
          text-model     (reagent/atom (time->text deref-model))
          previous-model (reagent/atom deref-model)]
      (fn input-time-render
        [& {:keys [model on-change minimum maximum disabled? show-icon? hide-border? width height class style attr parts src debug-as] :as args
            :or   {minimum 0 maximum 2359}}]
        (or
          (validate-args-macro input-time-args-desc args)
          (validate-arg-times (deref-or-value model) minimum maximum args)
          (let [style (merge (when hide-border? {:border "none"})
                             style)
                new-val (deref-or-value model)
                new-val (if (< new-val minimum) minimum new-val)
                new-val (if (> new-val maximum) maximum new-val)]
            ;; if the model is different to that currently shown in text, then reset the text to match
            ;; other than that we want to keep the current text, because the user is probably typing
            (when (not= @previous-model new-val)
              (reset! text-model (time->text new-val))
              (reset! previous-model new-val))

            [h-box
             :src      src
             :debug-as (or debug-as (reflect-current-component))
             :class    (str "rc-input-time " (get-in parts [:wrapper :class]))
             :style    (merge {:height height} (get-in parts [:wrapper :style]))
             :attr     (get-in parts [:wrapper :attr])
             :children [[:input
                         (merge
                           {:type      "text"
                            ;; Leaving time-entry class (below) for backwards compatibility only.
                            :class     (str "time-entry rc-time-entry " class)
                            :style     (merge {:width width}
                                              style)
                            :value     @text-model
                            :disabled  (deref-or-value disabled?)
                            :on-change (handler-fn (on-new-keypress event text-model))
                            :on-blur   (handler-fn (on-defocus text-model minimum maximum on-change @previous-model))
                            :on-key-up (handler-fn (lose-focus-if-enter event))}
                           attr)]
                        (when show-icon?
                          ;; Leaving time-icon class (below) for backwards compatibility only.
                          [:div
                           (merge
                             {:class (str "time-icon rc-time-icon-container " (get-in parts [:time-icon-container :class]))
                              :style (get-in parts [:time-icon-container :style] {})}
                             (get-in parts [:time-icon-container :attr]))
                           [:i
                            (merge
                              {:class (str "zmdi zmdi-hc-fw-rc zmdi-time rc-time-icon " (get-in parts [:time-icon :class]))
                               :style (merge {:position "static"
                                              :margin   "auto"}
                                             (get-in parts [:time-icon :style]))}
                              (get-in parts [:time-icon :attr]))]])]]))))))
