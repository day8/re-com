(ns re-com.misc
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [deref-or-value px]]
            [re-com.popover  :refer [popover-tooltip]]
            [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]]
            [re-com.validate :refer [input-status-type? input-status-types-list regex?
                                     string-or-hiccup? css-style? html-attr? number-or-string?
                                     string-or-atom? throbber-size? throbber-sizes-list] :refer-macros [validate-args-macro]]
            [reagent.core    :as    reagent]))


;; ------------------------------------------------------------------------------------
;;  Component: input-text
;; ------------------------------------------------------------------------------------

(def input-text-args-desc
  [{:name :model            :required true                   :type "string | atom"    :validate-fn string-or-atom?    :description "text of the input (can be atom or value)"}
   {:name :on-change        :required true                   :type "string -> nil"    :validate-fn fn?                :description [:span [:code ":change-on-blur?"] " controls when it is called. Passed the current input string"] }
   {:name :status           :required false                  :type "keyword"          :validate-fn input-status-type? :description [:span "validation status. " [:code "nil/omitted"] " for normal status or one of: " input-status-types-list]}
   {:name :status-icon?     :required false :default false   :type "boolean"                                          :description [:span "when true, display an icon to match " [:code ":status"] " (no icon for nil)"]}
   {:name :status-tooltip   :required false                  :type "string"           :validate-fn string?            :description "displayed in status icon's tooltip"}
   {:name :placeholder      :required false                  :type "string"           :validate-fn string?            :description "background text shown when empty"}
   {:name :width            :required false :default "250px" :type "string"           :validate-fn string?            :description "standard CSS width setting for this input"}
   {:name :height           :required false                  :type "string"           :validate-fn string?            :description "standard CSS height setting for this input"}
   {:name :rows             :required false :default 3       :type "integer | string" :validate-fn number-or-string?  :description "ONLY applies to 'input-textarea': the number of rows of text to show"}
   {:name :change-on-blur?  :required false :default true    :type "boolean | atom"                                   :description [:span "when true, invoke " [:code ":on-change"] " function on blur, otherwise on every change (character by character)"] }
   {:name :validation-regex :required false                  :type "regex"            :validate-fn regex?             :description "user input is only accepted if it would result in a string that matches this regular expression"}
   {:name :disabled?        :required false :default false   :type "boolean | atom"                                   :description "if true, the user can't interact (input anything)"}
   {:name :class            :required false                  :type "string"           :validate-fn string?            :description "CSS class names, space separated"}
   {:name :style            :required false                  :type "CSS style map"    :validate-fn css-style?         :description "CSS styles to add or override"}
   {:name :attr             :required false                  :type "HTML attr map"    :validate-fn html-attr?         :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}
   {:name :input-type       :required false                  :type "keyword"          :validate-fn keyword?           :description "ONLY applies to super function 'base-input-text': either :input or :textarea"}])

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
        internal-model (reagent/atom (if (nil? @external-model) "" @external-model))] ;; Create a new atom from the model to be used internally (avoid nil)
    (fn
      [& {:keys [model status status-icon? status-tooltip placeholder width height rows on-change change-on-blur? validation-regex disabled? class style attr]
          :or   {change-on-blur? true}
          :as   args}]
      {:pre [(validate-args-macro input-text-args-desc args "input-text")]}
      (let [latest-ext-model (deref-or-value model)
            disabled?        (deref-or-value disabled?)
            change-on-blur?  (deref-or-value change-on-blur?)
            showing?         (reagent/atom false)]
        (when (not= @external-model latest-ext-model) ;; Has model changed externally?
          (reset! external-model latest-ext-model)
          (reset! internal-model latest-ext-model))
        [h-box
         :align    :start
         :width    (if width width "250px")
         :class    "rc-input-text "
         :children [[:div
                     {:class (str "rc-input-text-inner "          ;; form-group
                                  (case status
                                    :warning "has-warning "
                                    :error "has-error "
                                    "")
                                  (when (and status status-icon?) "has-feedback"))
                      :style (flex-child-style "auto")}
                     [input-type
                      (merge
                        {:class       (str "form-control noselect " class)
                         :type        (when (= input-type :input) "text")
                         :rows        (when (= input-type :textarea) (if rows rows 3))
                         :style       (merge
                                        (flex-child-style "none")
                                        {:height        height
                                         :padding-right "12px"} ;; override for when icon exists
                                        style)
                         :placeholder placeholder
                         :value       @internal-model
                         :disabled    disabled?
                         :on-change   (handler-fn
                                        (let [new-val (-> event .-target .-value)]
                                          (when (and
                                                  on-change
                                                  (not disabled?)
                                                  (if validation-regex (re-find validation-regex new-val) true))
                                            (reset! internal-model new-val)
                                            (when-not change-on-blur?
                                              (on-change @internal-model)))))
                         :on-blur     (handler-fn
                                        (when (and
                                                on-change
                                                change-on-blur?
                                                (not= @internal-model @external-model))
                                          (on-change @internal-model)))
                         :on-key-up   (handler-fn
                                        (if disabled?
                                          (.preventDefault event)
                                          (case (.-which event)
                                            13 (when on-change (on-change @internal-model))
                                            27 (reset! internal-model @external-model)
                                            true)))

                         }
                        attr)]]
                    (when (and status-icon? status)
                      (if status-tooltip
                        [popover-tooltip
                         :label status-tooltip
                         :position :right-center
                         :status status
                         ;:width    "200px"
                         :showing? showing?
                         :anchor [:i {:class         (str (if (= status :warning) "md-warning" "md-error") " form-control-feedback")
                                      :style         {:position "static"
                                                      :width    "auto"
                                                      :height   "auto"
                                                      :opacity  (if (and status-icon? status) "1" "0")
                                                      }
                                      :on-mouse-over (handler-fn (when (and status-icon? status) (reset! showing? true)))
                                      :on-mouse-out  (handler-fn (reset! showing? false))}]
                         :style (merge (flex-child-style "none")
                                       (align-style :align-self :center)
                                       {:font-size   "130%"
                                        :margin-left "4px"})]
                        [:i {:class (str (if (= status :warning) "md-warning" "md-error") " form-control-feedback")
                             :style (merge (flex-child-style "none")
                                           (align-style :align-self :center)
                                           {:position    "static"
                                            :font-size   "130%"
                                            :margin-left "4px"
                                            :opacity     (if (and status-icon? status) "1" "0")
                                            :width       "auto"
                                            :height      "auto"})
                             :title status-tooltip}]))]]))))


(defn input-text
  [& args]
  (apply input-text-base :input-type :input args))


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
   {:name :style       :required false                :type "CSS style map"    :validate-fn css-style?        :description "the CSS style style map"}
   {:name :label-style :required false                :type "CSS style map"    :validate-fn css-style?        :description "the CSS class applied overall to the component"}
   {:name :label-class :required false                :type "string"           :validate-fn string?           :description "the CSS class applied to the label"}])

;; TODO: when disabled?, should the text appear "disabled".
(defn checkbox
  "I return the markup for a checkbox, with an optional RHS label"
  [& {:keys [model on-change label disabled? style label-class label-style]
      :as   args}]
  {:pre [(validate-args-macro checkbox-args-desc args "checkbox")]}
  (let [cursor      "default"
        model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn #(when (and on-change (not disabled?))
                      (on-change (not model)))]  ;; call on-change with either true or false
    [h-box
     :align    :start
     :class    "noselect"
     :children [[:input
                 {:class     "rc-checkbox"
                  :type      "checkbox"
                  :style     (merge (flex-child-style "none")
                                    {:cursor cursor}
                                    style)
                  :disabled  disabled?
                  :checked   model
                  :on-change (handler-fn (callback-fn))}]
                (when label
                  [:span
                   {:on-click (handler-fn (callback-fn))
                    :class    label-class
                    :style    (merge (flex-child-style "none")
                                     {:padding-left "8px"
                                      :cursor       cursor}
                                     label-style)}
                   label])]]))


;; ------------------------------------------------------------------------------------
;;  Component: radio-button
;; ------------------------------------------------------------------------------------

(def radio-button-args-desc
  [{:name :model       :required true                 :type "anything | atom"                                  :description [:span "selected value of the radio button group. See also " [:code ":value"]] }
   {:name :value       :required false                :type "anything"                                         :description [:span "if " [:code ":model"]  " equals " [:code ":value"] " then this radio button is selected"] }
   {:name :on-change   :required true                 :type "anything -> nil"   :validate-fn fn?               :description [:span "called when the radio button is clicked. Passed " [:code ":value"]]}
   {:name :label       :required false                :type "string | hiccup"   :validate-fn string-or-hiccup? :description "the label shown to the right"}
   {:name :disabled?   :required false :default false :type "boolean | atom"                                   :description "if true, the user can't click the radio button"}
   {:name :style       :required false                :type "CSS style map"     :validate-fn css-style?        :description "radio button style map"}
   {:name :label-style :required false                :type "CSS style map"     :validate-fn css-style?        :description "the CSS class applied overall to the component"}
   {:name :label-class :required false                :type "string"            :validate-fn string?           :description "the CSS class applied to the label"}])

(defn radio-button
  "I return the markup for a radio button, with an optional RHS label"
  [& {:keys [model on-change value label disabled? style label-class label-style]
      :as   args}]
  {:pre [(validate-args-macro radio-button-args-desc args "radio-button")]}
  (let [cursor      "default"
        model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn #(when (and on-change (not disabled?))
                      (on-change (not model)))]  ;; call on-change with either true or false
    [h-box
     :align    :start
     :class    "noselect"
     :children [[:input
                 {:class     "rc-radio-button"
                  :type      "radio"
                  :style     (merge
                               (flex-child-style "none")
                               {:cursor cursor}
                               style)
                  :disabled  disabled?
                  :checked   (= model value)
                  :on-change (handler-fn (callback-fn))}]
                (when label
                  [:span
                   {:on-click (handler-fn (callback-fn))
                    :class    label-class
                    :style    (merge (flex-child-style "none")
                                     {:padding-left "8px"
                                      :cursor       cursor}
                                     label-style)}
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
   {:name :class     :required false                  :type "string"                 :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style     :required false                  :type "CSS style map"          :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr      :required false                  :type "HTML attr map"          :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn slider
  "Returns markup for an HTML5 slider input"
  []
  (fn
    [& {:keys [model min max step width on-change disabled? class style attr]
        :or   {min 0 max 100}
        :as   args}]
    {:pre [(validate-args-macro slider-args-desc args "slider")]}
    (let [model     (deref-or-value model)
          min       (deref-or-value min)
          max       (deref-or-value max)
          step      (deref-or-value step)
          disabled? (deref-or-value disabled?)]
      [box
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
                                :width  (if width width "400px")
                                :cursor (if disabled? "not-allowed" "default")}
                               style)
                  :min       min
                  :max       max
                  :step      step
                  :value     model
                  :disabled  disabled?
                  :on-change (handler-fn (on-change (js/Number (-> event .-target .-value))))}
                 attr)]])))


;; ------------------------------------------------------------------------------------
;;  Component: progress-bar
;; ------------------------------------------------------------------------------------

(def progress-bar-args-desc
  [{:name :model    :required true  :type "double | string | atom"                 :validate-fn number-or-string? :description "current value of the slider. A number between 0 and 100"}
   {:name :width    :required false :type "string"                 :default "100%" :validate-fn string?           :description "a CSS width"}
   {:name :striped? :required false :type "boolean"                :default false                                 :description "when true, the progress section is a set of animated stripes"}
   {:name :class    :required false :type "string"                                 :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style    :required false :type "CSS style map"                          :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr     :required false :type "HTML attr map"                          :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [model width striped? class style attr]
      :or   {width "100%"}
      :as   args}]
  {:pre [(validate-args-macro progress-bar-args-desc args "progress-bar")]}
  (let [model (deref-or-value model)]
    [box
     :align :start
     :child [:div
             (merge
               {:class (str "rc-progress-bar progress " class)
                :style (merge (flex-child-style "none")
                              {:width width}
                              style)}
               attr)
             [:div
              {:class (str "progress-bar " (when striped? "progress-bar-striped active"))
               :role  "progressbar"
               :style {:width      (str model "%")
                       :transition "none"}}                 ;; Default BS transitions cause the progress bar to lag behind
              (str model "%")]]]))


;; ------------------------------------------------------------------------------------
;;  Component: throbber
;; ------------------------------------------------------------------------------------

(def throbber-args-desc
  [{:name :size     :required false :type "keyword"       :default :regular :validate-fn throbber-size? :description [:span "one of " throbber-sizes-list]}
   {:name :color    :required false :type "string"        :default "#999"   :validate-fn string?        :description "CSS color"}
   {:name :class    :required false :type "string"                          :validate-fn string?        :description "CSS class names, space separated"}
   {:name :style    :required false :type "CSS style map"                   :validate-fn css-style?     :description "CSS styles to add or override"}
   {:name :attr     :required false :type "HTML attr map"                   :validate-fn html-attr?     :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn throbber
  "Render an animated throbber using CSS"
  [& {:keys [size color class style attr] :as args}]
  {:pre [(validate-args-macro throbber-args-desc args "throbber")]}
  (let [seg (fn [] [:li (when color {:style {:background-color color}})])]
    [box
     :align :start
     :child [:ul
             (merge {:class (str "rc-throbber loader "
                                 (case size :regular ""
                                            :small "small "
                                            :large "large "
                                            "")
                                 class)
                     :style style}
                    attr)
             [seg] [seg] [seg] [seg]
             [seg] [seg] [seg] [seg]]])) ;; Each :li element in [seg] represents one of the eight circles in the throbber
