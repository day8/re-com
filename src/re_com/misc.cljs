(ns re-com.misc
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [deref-or-value px]]
            [re-com.popover  :refer [popover-tooltip]]
            [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]]
            [re-com.validate :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts?
                                     number-or-string? string-or-atom? nillable-string-or-atom? throbber-size? throbber-sizes-list] :refer-macros [validate-args-macro]]
            [reagent.core    :as    reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: throbber
;; ------------------------------------------------------------------------------------

(def throbber-args-desc
  [{:name :size  :required false :default :regular :type "keyword"       :validate-fn throbber-size? :description [:span "one of " throbber-sizes-list]}
   {:name :color :required false :default "#999"   :type "string"        :validate-fn string?        :description "CSS color"}
   {:name :class :required false                   :type "string"        :validate-fn string?        :description "CSS class names, space separated (applies to the throbber, not the wrapping div)"}
   {:name :style :required false                   :type "CSS style map" :validate-fn css-style?     :description "CSS styles to add or override (applies to the throbber, not the wrapping div)"}
   {:name :attr  :required false                   :type "HTML attr map" :validate-fn html-attr?     :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the throbber, not the wrapping div)"]}
   {:name :parts :required false                   :type "map"           :validate-fn (parts? #{:wrapper :segment}) :description "See Parts section below."}])

(defn throbber
  "Render an animated throbber using CSS"
  [& {:keys [size color class style attr parts] :as args}]
  {:pre [(validate-args-macro throbber-args-desc args "throbber")]}
  (let [seg (fn []
              [:li
               (merge
                 {:class (str "rc-throbber-segment " (get-in parts [:segment :class]))
                  :style (merge
                           (when color {:background-color color})
                           (get-in parts [:segment :style]))}
                 (get-in parts [:segment :attr]))])]
    [box
     :class (str "rc-throbber-wrapper " (get-in parts [:wrapper :class]))
     :style (get-in parts [:wrapper :style] {})
     :attr  (get-in parts [:wrapper :attr] {})
     :align :start
     :child [:ul
             (merge {:class (str "loader rc-throbber "
                                 (case size :regular ""
                                            :smaller "smaller "
                                            :small "small "
                                            :large "large "
                                            "")
                                 class)
                     :style style}
                    attr)
             [seg] [seg] [seg] [seg]
             [seg] [seg] [seg] [seg]]])) ;; Each :li element in [seg] represents one of the eight circles in the throbber


;; ------------------------------------------------------------------------------------
;;  Component: input-text
;; ------------------------------------------------------------------------------------

(def input-text-args-desc
  [{:name :model            :required true                   :type "string/nil | atom" :validate-fn nillable-string-or-atom? :description "text of the input (can be atom or value/nil)"}
   {:name :on-change        :required true                   :type "string[, done-fn] -> nil"     :validate-fn fn?                      :description [:span [:code ":change-on-blur?"] " controls when it is called. Passed the current input string, and optionally a function to call (with no args) to signal that " [:code ":model"] " has reached a steady state to avoid displaying a prior value while processing."]}
   {:name :status           :required false                  :type "keyword"           :validate-fn input-status-type?       :description [:span "validation status. " [:code "nil/omitted"] " for normal status or one of: " input-status-types-list]}
   {:name :status-icon?     :required false :default false   :type "boolean"                                                 :description [:span "when true, display an icon to match " [:code ":status"] " (no icon for nil)"]}
   {:name :status-tooltip   :required false                  :type "string"            :validate-fn string?                  :description "displayed in status icon's tooltip"}
   {:name :placeholder      :required false                  :type "string"            :validate-fn string?                  :description "background text shown when empty"}
   {:name :width            :required false :default "250px" :type "string"            :validate-fn string?                  :description "standard CSS width setting for this input"}
   {:name :height           :required false                  :type "string"            :validate-fn string?                  :description "standard CSS height setting for this input"}
   {:name :rows             :required false :default 3       :type "integer | string"  :validate-fn number-or-string?        :description "ONLY applies to 'input-textarea': the number of rows of text to show"}
   {:name :change-on-blur?  :required false :default true    :type "boolean | atom"                                          :description [:span "when true, invoke " [:code ":on-change"] " function on blur, otherwise on every change (character by character)"]}
   {:name :on-alter         :required false                  :type "string -> string"  :validate-fn fn?                      :description "called with the new value to alter it immediately"}
   {:name :validation-regex :required false                  :type "regex"             :validate-fn regex?                   :description "user input is only accepted if it would result in a string that matches this regular expression"}
   {:name :disabled?        :required false :default false   :type "boolean | atom"                                          :description "if true, the user can't interact (input anything)"}
   {:name :class            :required false                  :type "string"            :validate-fn string?                  :description "CSS class names, space separated (applies to the textbox, not the wrapping div)"}
   {:name :style            :required false                  :type "CSS style map"     :validate-fn css-style?               :description "CSS styles to add or override (applies to the textbox, not the wrapping div)"}
   {:name :attr             :required false                  :type "HTML attr map"     :validate-fn html-attr?               :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the textbox, not the wrapping div)"]}
   {:name :parts            :required false                  :type "map"               :validate-fn (parts? #{:wrapper :inner}) :description "See Parts section below."}
   {:name :input-type       :required false                  :type "keyword"           :validate-fn keyword?                 :description [:span "ONLY applies to super function 'base-input-text': either " [:code ":input"] ", " [:code ":password"] " or " [:code ":textarea"]]}])

;; Sample regex's:
;;  - #"^(-{0,1})(\d*)$"                   ;; Signed integer
;;  - #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$"  ;; Specific numeric value ##.#
;;  - #"^.{0,8}$"                          ;; 8 chars max
;;  - #"^[0-9a-fA-F]*$"                    ;; Hex number
;;  - #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" ;; Time input

(defn- input-text-base
  "Returns markup for a basic text input label"
  [& {:keys [model input-type] :as args}]
  {:pre [(validate-args-macro input-text-args-desc args "input-text")]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom (if (nil? @external-model) "" @external-model))] ;; Create a new atom from the model to be used internally (avoid nil)]
    (fn
      [& {:keys [model on-change status status-icon? status-tooltip placeholder width height rows change-on-blur? on-alter validation-regex disabled? class style attr parts]
          :or   {change-on-blur? true, on-alter identity}
          :as   args}]
      {:pre [(validate-args-macro input-text-args-desc args "input-text")]}
      (let [latest-ext-model  (deref-or-value model)
            disabled?         (deref-or-value disabled?)
            change-on-blur?   (deref-or-value change-on-blur?)
            showing?          (reagent/atom false)
            ;; If the user types a value that is subsequently modified in :on-change to the prior value of :model, such
            ;; as validation or filtering, then the :model is reset! to the same value the value that the user typed
            ;; (not the value of :model after the reset!) will remain displayed in the text input as no change is
            ;; detected. To fix this we force an update via (reset! external-model @internal-model) on any change.
            ;;
            ;; This causes another problem, where if there is a delay in processing on-change before reset! of :model
            ;; is called, such as if on-change is asynchronous, the displayed value can 'flicker' between the prior
            ;; value of :model and the eventual reset! of :model to a new value. To give developers an escape hatch to
            ;; fix this problem there is an optional 2-arity version of on-change that receives a function as the second
            ;; arg that when called signals that :model has reached a 'steady state' and the reset! of external-model
            ;; can be done thus avoiding the flicker.
            on-change-handler (fn []
                                (when (fn? on-change)
                                  (let [num-args (.-length on-change)
                                        reset-fn #(reset! external-model @internal-model)]
                                    (if (= num-args 2)
                                      (on-change @internal-model reset-fn)
                                      (do
                                        (on-change @internal-model)
                                        (reset-fn))))))]
        (when (not= @external-model latest-ext-model) ;; Has model changed externally?
          (reset! external-model latest-ext-model)
          (reset! internal-model latest-ext-model))
        [h-box
         :align    :start
         :class    (str "rc-input-text " (get-in parts [:wrapper :class]))
         :style    (get-in parts [:wrapper :style] {})
         :attr     (get-in parts [:wrapper :attr] {})
         :width    (if width width "250px")
         :children [[:div
                     (merge
                       {:class (str "rc-input-text-inner "          ;; form-group
                                    (case status
                                      :success "has-success "
                                      :warning "has-warning "
                                      :error "has-error "
                                      "")
                                    (when (and status status-icon?) "has-feedback ")
                                    (get-in parts [:inner :class]))
                        :style (merge (flex-child-style "auto")
                                      (get-in parts [:inner :style]))}
                       (get-in parts [:inner :attr]))
                     [(if (= input-type :password) :input input-type)
                      (merge
                        {:class       (str "form-control " class)
                         :type        (case input-type
                                        :input "text"
                                        :password "password"
                                        nil)
                         :rows        (when (= input-type :textarea) (or rows 3))
                         :style       (merge
                                        (flex-child-style "none")
                                        {:height        height
                                         :padding-right "12px"} ;; override for when icon exists
                                        style)
                         :placeholder placeholder
                         :value       @internal-model
                         :disabled    disabled?
                         :on-change   (handler-fn
                                        (let [new-val-orig (-> event .-target .-value)
                                              new-val (on-alter new-val-orig)]
                                          (when (not= new-val new-val-orig)
                                            (set! (-> event .-target .-value) new-val))
                                          (when (and
                                                  on-change
                                                  (not disabled?)
                                                  (if validation-regex (re-find validation-regex new-val) true))
                                            (reset! internal-model new-val)
                                            (when-not change-on-blur?
                                              (on-change-handler)))))
                         :on-blur     (handler-fn
                                        (when (and
                                                change-on-blur?
                                                (not= @internal-model @external-model))
                                          (on-change-handler)))
                         :on-key-up   (handler-fn
                                        (if disabled?
                                          (.preventDefault event)
                                          (case (.-which event)
                                            13 (on-change-handler)
                                            27 (reset! internal-model @external-model)
                                            true)))}
                        attr)]]
                    (when (and status-icon? status)
                      (let [icon-class (case status :success "zmdi-check-circle" :warning "zmdi-alert-triangle" :error "zmdi-alert-circle zmdi-spinner" :validating "zmdi-hc-spin zmdi-rotate-right zmdi-spinner")]
                        (if status-tooltip
                         [popover-tooltip
                          :label status-tooltip
                          :position :right-center
                          :status status
                          ;:width    "200px"
                          :showing? showing?
                          :anchor (if (= :validating status)
                                    [throbber
                                     :size  :regular
                                     :class "smaller"
                                     :attr  {:on-mouse-over (handler-fn (when (and status-icon? status) (reset! showing? true)))
                                             :on-mouse-out  (handler-fn (reset! showing? false))}]
                                    [:i {:class         (str "zmdi zmdi-hc-fw " icon-class " form-control-feedback")
                                         :style         {:position "static"
                                                         :height   "auto"
                                                         :opacity  (if (and status-icon? status) "1" "0")}
                                         :on-mouse-over (handler-fn (when (and status-icon? status) (reset! showing? true)))
                                         :on-mouse-out  (handler-fn (reset! showing? false))}])
                          :style (merge (flex-child-style "none")
                                        (align-style :align-self :center)
                                        {:font-size   "130%"
                                         :margin-left "4px"})]
                         (if (= :validating status)
                           [throbber :size :regular :class "smaller"]
                           [:i {:class (str "zmdi zmdi-hc-fw " icon-class " form-control-feedback")
                                :style (merge (flex-child-style "none")
                                              (align-style :align-self :center)
                                              {:position    "static"
                                               :font-size   "130%"
                                               :margin-left "4px"
                                               :opacity     (if (and status-icon? status) "1" "0")
                                               :height      "auto"})
                                :title status-tooltip}]))))]]))))


(defn input-text
  [& args]
  (apply input-text-base :input-type :input args))


(defn input-password
  [& args]
  (apply input-text-base :input-type :password args))


(defn input-textarea
  [& args]
  (apply input-text-base :input-type :textarea args))


;; ------------------------------------------------------------------------------------
;;  Component: checkbox
;; ------------------------------------------------------------------------------------

(def checkbox-args-desc
  [{:name :model       :required true                 :type "boolean | atom"                                  :description "holds state of the checkbox when it is called"}
   {:name :on-change   :required true                 :type "boolean -> nil"   :validate-fn fn?               :description "called when the checkbox is clicked. Passed the new value of the checkbox"}
   {:name :label       :required false                :type "string | hiccup"  :validate-fn string-or-hiccup? :description "the label shown to the right"}
   {:name :disabled?   :required false :default false :type "boolean | atom"                                  :description "if true, user interaction is disabled"}
   {:name :label-class :required false                :type "string"           :validate-fn string?           :description "CSS class names (applies to the label)"}
   {:name :label-style :required false                :type "CSS style map"    :validate-fn css-style?        :description "CSS style map (applies to the label)"}
   {:name :class       :required false                :type "string"           :validate-fn string?           :description "CSS class names, space separated (applies to the checkbox, not the wrapping div)"}
   {:name :style       :required false                :type "CSS style map"    :validate-fn css-style?        :description "CSS style map (applies to the checkbox, not the wrapping div)"}
   {:name :attr        :required false                :type "HTML attr map"    :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the checkbox, not the wrapping div)"]}
   {:name :parts       :required false                :type "map"              :validate-fn (parts? #{:wrapper}) :description "See Parts section below."}])

;; TODO: when disabled?, should the text appear "disabled".
(defn checkbox
  "I return the markup for a checkbox, with an optional RHS label"
  [& {:keys [model on-change label disabled? label-class label-style class style attr parts]
      :as   args}]
  {:pre [(validate-args-macro checkbox-args-desc args "checkbox")]}
  (let [cursor      "default"
        model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn #(when (and on-change (not disabled?))
                      (on-change (not model)))]  ;; call on-change with either true or false
    [h-box
     :class    (str "noselect rc-checkbox-wrapper " (get-in parts [:wrapper :class]))
     :style    (get-in parts [:wrapper :style] {})
     :attr     (get-in parts [:wrapper :attr] {})
     :align    :start
     :children [[:input
                 (merge
                   {:class     (str "rc-checkbox " class)
                    :type      "checkbox"
                    :style     (merge (flex-child-style "none")
                                      {:cursor cursor}
                                      style)
                    :disabled  disabled?
                    :checked   (boolean model)
                    :on-change (handler-fn (callback-fn))}
                   attr)]
                (when label
                  [:span
                   {:class    (str "rc-checkbox-label " label-class)
                    :style    (merge (flex-child-style "none")
                                     {:padding-left "8px"
                                      :cursor       cursor}
                                     label-style)
                    :on-click (handler-fn (callback-fn))}
                   label])]]))


;; ------------------------------------------------------------------------------------
;;  Component: radio-button
;; ------------------------------------------------------------------------------------

(def radio-button-args-desc
  [{:name :model       :required true                 :type "anything | atom"                                 :description [:span "selected value of the radio button group. See also " [:code ":value"]]}
   {:name :value       :required false                :type "anything"                                        :description [:span "if " [:code ":model"]  " equals " [:code ":value"] " then this radio button is selected"]}
   {:name :on-change   :required true                 :type "anything -> nil"  :validate-fn fn?               :description [:span "called when the radio button is clicked. Passed " [:code ":value"]]}
   {:name :label       :required false                :type "string | hiccup"  :validate-fn string-or-hiccup? :description "the label shown to the right"}
   {:name :disabled?   :required false :default false :type "boolean | atom"                                  :description "if true, the user can't click the radio button"}
   {:name :label-class :required false                :type "string"           :validate-fn string?           :description "CSS class names (applies to the label)"}
   {:name :label-style :required false                :type "CSS style map"    :validate-fn css-style?        :description "CSS style map (applies to the label)"}
   {:name :class       :required false                :type "string"           :validate-fn string?           :description "CSS class names, space separated (applies to the radio-button, not the wrapping div)"}
   {:name :style       :required false                :type "CSS style map"    :validate-fn css-style?        :description "CSS style map (applies to the radio-button, not the wrapping div)"}
   {:name :attr        :required false                :type "HTML attr map"    :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the radio-button, not the wrapping div)"]}
   {:name :parts       :required false                :type "map"              :validate-fn (parts? #{:wrapper}) :description "See Parts section below."}])

(defn radio-button
  "I return the markup for a radio button, with an optional RHS label"
  [& {:keys [model value on-change label disabled? label-class label-style class style attr parts]
      :as   args}]
  {:pre [(validate-args-macro radio-button-args-desc args "radio-button")]}
  (let [cursor      "default"
        model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn #(when (and on-change (not disabled?))
                      (on-change value))]  ;; call on-change with the :value arg
    [h-box
     :class    (str "noselect rc-radio-button-wrapper " (get-in parts [:wrapper :class]))
     :style    (get-in parts [:wrapper :style] {})
     :attr     (get-in parts [:wrapper :attr] {})
     :align    :start
     :children [[:input
                 (merge
                   {:class     (str "rc-radio-button " class)
                    :style     (merge
                                 (flex-child-style "none")
                                 {:cursor cursor}
                                 style)
                    :type      "radio"
                    :disabled  disabled?
                    :checked   (= model value)
                    :on-change (handler-fn (callback-fn))}
                   attr)]
                (when label
                  [:span
                   {:class    (str "rc-radio-button-label " label-class)
                    :style    (merge (flex-child-style "none")
                                     {:padding-left "8px"
                                      :cursor       cursor}
                                     label-style)
                    :on-click (handler-fn (callback-fn))}
                   label])]]))


;; ------------------------------------------------------------------------------------
;;  Component: slider
;; ------------------------------------------------------------------------------------

(def slider-args-desc
  [{:name :model     :required true                   :type "double | string | atom" :validate-fn number-or-string? :description "current value of the slider"}
   {:name :on-change :required true                   :type "double -> nil"          :validate-fn fn?               :description "called when the slider is moved. Passed the new value of the slider"}
   {:name :min       :required false :default 0       :type "double | string | atom" :validate-fn number-or-string? :description "the minimum value of the slider"}
   {:name :max       :required false :default 100     :type "double | string | atom" :validate-fn number-or-string? :description "the maximum value of the slider"}
   {:name :step      :required false :default 1       :type "double | string | atom" :validate-fn number-or-string? :description "step value between min and max"}
   {:name :width     :required false :default "400px" :type "string"                 :validate-fn string?           :description "standard CSS width setting for the slider"}
   {:name :disabled? :required false :default false   :type "boolean | atom"                                        :description "if true, the user can't change the slider"}
   {:name :class     :required false                  :type "string"                 :validate-fn string?           :description "CSS class names, space separated (applies to the slider, not the wrapping div)"}
   {:name :style     :required false                  :type "CSS style map"          :validate-fn css-style?        :description "CSS styles to add or override (applies to the slider, not the wrapping div)"}
   {:name :attr      :required false                  :type "HTML attr map"          :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the slider, not the wrapping div)"]}
   {:name :parts     :required false                  :type "map"                    :validate-fn (parts? #{:wrapper}) :description "See Parts section below."}])

(defn slider
  "Returns markup for an HTML5 slider input"
  [& {:keys [model min max step width on-change disabled? class style attr parts]
      :or   {min 0 max 100}
      :as   args}]
  {:pre [(validate-args-macro slider-args-desc args "slider")]}
  (let [model     (deref-or-value model)
        min       (deref-or-value min)
        max       (deref-or-value max)
        step      (deref-or-value step)
        disabled? (deref-or-value disabled?)]
    [box
     :class (str "rc-slider-wrapper " (get-in parts [:wrapper :class]))
     :style (get-in parts [:wrapper :style] {})
     :attr  (get-in parts [:wrapper :attr] {})
     :align :start
     :child [:input
             (merge
               {:class     (str "rc-slider " class)
                :type      "range"
                ;:orient    "vertical" ;; Make Firefox slider vertical (doesn't work because React ignores it, I think)
                :style     (merge
                             (flex-child-style "none")
                             {;:-webkit-appearance "slider-vertical"   ;; TODO: Make a :orientation (:horizontal/:vertical) option
                              ;:writing-mode       "bt-lr"             ;; Make IE slider vertical
                              :width  (or width "400px")
                              :cursor (if disabled? "default" "pointer")}
                             style)
                :min       min
                :max       max
                :step      step
                :value     model
                :disabled  disabled?
                :on-change (handler-fn (on-change (js/Number (-> event .-target .-value))))}
               attr)]]))


;; ------------------------------------------------------------------------------------
;;  Component: progress-bar
;; ------------------------------------------------------------------------------------

(def progress-bar-args-desc
  [{:name :model     :required true                  :type "double | string | atom" :validate-fn number-or-string? :description "current value of the slider. A number between 0 and 100"}
   {:name :width     :required false :default "100%" :type "string"                 :validate-fn string?           :description "a CSS width"}
   {:name :striped?  :required false :default false  :type "boolean"                                               :description "when true, the progress section is a set of animated stripes"}
   {:name :bar-class :required false                 :type "string"                 :validate-fn string?           :description "CSS class name(s) for the actual progress bar itself, space separated"}
   {:name :class     :required false                 :type "string"                 :validate-fn string?           :description "CSS class names, space separated (applies to the progress-bar, not the wrapping div)"}
   {:name :style     :required false                 :type "CSS style map"          :validate-fn css-style?        :description "CSS styles to add or override (applies to the progress-bar, not the wrapping div)"}
   {:name :attr      :required false                 :type "HTML attr map"          :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the progress-bar, not the wrapping div)"]}
   {:name :parts     :required false                 :type "map"                    :validate-fn (parts? #{:wrapper :segment}) :description "See Parts section below."}])

(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [model width striped? class bar-class style attr parts]
      :or   {width "100%"}
      :as   args}]
  {:pre [(validate-args-macro progress-bar-args-desc args "progress-bar")]}
  (let [model (deref-or-value model)]
    [box
     :class (str "rc-progress-bar-wrapper " (get-in parts [:wrapper :class]))
     :style (get-in parts [:wrapper :style] {})
     :attr  (get-in parts [:wrapper :attr] {})
     :align :start
     :child [:div
             (merge
               {:class (str "progress rc-progress-bar " class)
                :style (merge (flex-child-style "none")
                              {:width width}
                              style)}
               attr)
             [:div
              {:class (str "progress-bar " (when striped? "progress-bar-striped active rc-progress-bar-portion ") bar-class)
               :role  "progressbar"
               :style {:width      (str model "%")
                       :transition "none"}}                 ;; Default BS transitions cause the progress bar to lag behind
              (str model "%")]]]))
