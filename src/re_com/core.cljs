(ns re-com.core
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [deref-or-value validate-arguments px]]
            [re-com.popover  :refer [popover-tooltip]]
            [re-com.box      :refer [h-box v-box box gap line]]
            [re-com.validate :refer [extract-arg-data validate-args justify-style? align-style? scroll-style? string-or-hiccup?]]
            [reagent.core    :as    reagent]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

(def label-args-desc
  [{:name :label    :required true  :type "anything"                      :description "text to display. Can be anything as it will be converted to a string"}
   {:name :width    :required false :type "string"   :validate-fn string? :description "a CSS width"}
   {:name :on-click :required false :type "function" :validate-fn fn?     :description "function to call when label is clicked"}
   {:name :class    :required false :type "string"   :validate-fn string? :description "CSS class names, space separated"}
   {:name :style    :required false :type "map"      :validate-fn map?    :description "additional CSS styles"}
   {:name :attr     :required false :type "map"      :validate-fn map?    :description [:span "html attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed."]}])

(def label-args (extract-arg-data label-args-desc))

(defn label
  "Returns markup for a basic label"
  [& {:keys [label on-click width class style attr]
      :as   args}]
  {:pre [(validate-args label-args args "label")]}
  [box
   :width width
   :align :start
   :child [:span
           (merge
             {:class (str "rc-label " class)
              :style (merge {:flex "none"} style)}
             (when on-click
               {:on-click (handler-fn (on-click))})
             attr)
           (str label)]])


;; ------------------------------------------------------------------------------------
;;  Component: input-text
;; ------------------------------------------------------------------------------------

(def input-text-args-desc
  [{:name :model            :required true                   :type "string"     :description "text of the input (can be atom or value)."}
   {:name :status           :required false                  :type "keyword"    :description "validation status - nil, :warning, :error"}
   {:name :status-icon?     :required false :default false   :type "boolean"    :description "when true, display an appropriate icon to match the status (no icon for nil)"}
   {:name :status-tooltip   :required false                  :type "string"     :description "string to display when hovering over the icon."}
   {:name :placeholder      :required false                  :type "string"     :description "text to show when there is no under text in the component."}
   {:name :width            :required false :default "250px" :type "string"     :description "standard CSS width setting for this input."}
   {:name :height           :required false                  :type "string"     :description "standard CSS width setting for this input."}
   {:name :rows             :required false :default "3"     :type "string"     :description "ONLY applies to 'input-textarea': the number of rows of text to show."}
   {:name :on-change        :required true                   :type "(new-text)" :description "a function which takes one parameter, which is the new text (see :change-on-blur?)."}
   {:name :change-on-blur?  :required false :default true    :type "boolean | atom"  :description "when true, invoke on-change function on blur, otherwise on every change (character by character)."}
   {:name :validation-regex :required false                  :type "regex"      :description "the regular expression which determines which characters are legal and which aren't."}
   {:name :disabled?        :required false :default false   :type "boolean | atom" :description "if true, the user can't interact (input anything)."}
   {:name :class            :required false                  :type "string"     :description "CSS class names, space separated."}
   {:name :style            :required false                  :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                  :type "map"        :description [:span "html attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed."]}
   {:name :input-type       :required true                   :type "keyword"    :description "ONLY applies to super function 'base-input-text': either :input or :textarea ("}
   ])

(def input-text-args
  (set (map :name input-text-args-desc)))

;; Sample regex's:
;;  - #"^(-{0,1})(\d*)$"                   ;; Signed integer
;;  - #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$"  ;; Specific numeric value ##.#
;;  - #"^.{0,8}$"                          ;; 8 chars max
;;  - #"^[0-9a-fA-F]*$"                    ;; Hex number
;;  - #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" ;; Time input

(defn- input-text-base-OLD                                  ;; TODO: Remove
  "Returns markup for a basic text input label"
  [& {:keys [model input-type] :as args}]
  {:pre [(validate-arguments input-text-args (keys args))]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom (if (nil? @external-model) "" @external-model))] ;; Create a new atom from the model to be used internally (avoid nil)
    (fn
      [& {:keys [model status status-icon? status-tooltip placeholder width height rows on-change change-on-blur? validation-regex disabled? class style attr]
          :or   {change-on-blur? true}
          :as   args}]
      {:pre [(validate-arguments input-text-args (keys args))]}
      (let [latest-ext-model (deref-or-value model)
            disabled?        (deref-or-value disabled?)
            change-on-blur?  (deref-or-value change-on-blur?)
            showing?         (reagent/atom false)]
        (when (not= @external-model latest-ext-model) ;; Has model changed externally?
          (reset! external-model latest-ext-model)
          (reset! internal-model latest-ext-model))
        [box
         :align :start
         :child [:div
                 {:class (str "rc-input-text form-group "
                              (case status
                                :warning "has-warning "
                                :error "has-error "
                                "")
                              (when (and status status-icon?) "has-feedback"))
                  :style {:flex          "none"
                          :margin-bottom "0px"}}
                 [input-type
                  (merge
                    {:class       (str "form-control " class)
                     :type        (when (= input-type :text) "text")
                     :rows        (when (= input-type :textarea) (if rows rows 3))
                     :style       (merge
                                    {:flex                "none"
                                     :width               (if width width "250px")
                                     :height              (when height height)
                                     :padding-right       (when status "2.4em")
                                     :-webkit-user-select "none"}
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
                    attr)]
                 (when (and status-icon? status)
                   (if status-tooltip
                     [popover-tooltip
                      :label    status-tooltip
                      :position :right-center
                      :status   status
                      ;:width    "200px"
                      :showing? showing?
                      :anchor   [:i {:class         (str (if (= status :warning) "md-warning" "md-error") " form-control-feedback")
                                     :style         {:position "static"
                                                     :width    "auto"
                                                     :height   "auto"}
                                     :on-mouse-over (handler-fn (reset! showing? true))
                                     :on-mouse-out  (handler-fn (reset! showing? false))}]
                      :style {:position     "absolute"
                              :font-size    "130%"
                              :top          "50%"
                              :right        "0px"
                              :margin-top   "-0.5em"
                              :margin-right "0.5em"}]
                     [:i {:class (str (if (= status :warning) "md-warning" "md-error") " form-control-feedback")
                          :style {:font-size    "130%"
                                  :top          "50%"
                                  :right        "0px"
                                  :z-index      "0"
                                  :margin-top   "-0.5em"
                                  :margin-right "0.5em"
                                  :width        "auto"
                                  :height       "auto"}
                          :title status-tooltip}]))]]))))


(defn- input-text-base
  "Returns markup for a basic text input label"
  [& {:keys [model input-type] :as args}]
  {:pre [(validate-arguments input-text-args (keys args))]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom (if (nil? @external-model) "" @external-model))] ;; Create a new atom from the model to be used internally (avoid nil)
    (fn
      [& {:keys [model status status-icon? status-tooltip placeholder width height rows on-change change-on-blur? validation-regex disabled? class style attr]
          :or   {change-on-blur? true}
          :as   args}]
      {:pre [(validate-arguments input-text-args (keys args))]}
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
                                  (when (and status status-icon?) "has-feedback")
                                  )
                      :style {:flex          "auto"
                              ;:margin-bottom "0px"
                              }}
                     [input-type
                      (merge
                        {:class       (str "form-control " class)
                         :type        (when (= input-type :text) "text")
                         :rows        (when (= input-type :textarea) (if rows rows 3))
                         :style       (merge
                                        {:flex                "none"
                                         ;:width               (if width width "250px")
                                         :height              (when height height)
                                         :padding-right       "12px" ;; override for when icon exists
                                         :-webkit-user-select "none"}
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
                         :style {:flex        "none"
                                 :align-self  :center
                                 :font-size   "130%"
                                 :margin-left "4px"
                                 ;:position     "absolute"
                                 ;:top          "50%"
                                 ;:right        "0px"
                                 ;:margin-top   "-0.5em"
                                 ;:margin-right "0.5em"
                                 }]
                        [:i {:class (str (if (= status :warning) "md-warning" "md-error") " form-control-feedback")
                             :style {:flex        "none"
                                     :align-self  :center
                                     :position    "static"
                                     :font-size   "130%"
                                     :margin-left "4px"
                                     :opacity     (if (and status-icon? status) "1" "0")
                                     ;:top          "50%"
                                     ;:right        "0px"
                                     ;:z-index      "0"
                                     ;:margin-top   "-0.5em"
                                     ;:margin-right "0.5em"
                                     :width       "auto"
                                     :height      "auto"
                                     }
                             :title status-tooltip}]))
                    ]]
        ))))


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
  [{:name :model         :required true                   :type "string | atom"    :description "Holds state of the checkbox when it is called"}
   {:name :on-change     :required false                  :type "(boolean) -> nil" :description "When model state is changed, call back with new state"}
   {:name :label         :required false                  :type "string"           :description "The label shown to the right"}
   {:name :disabled?     :required false  :default false  :type "boolean | atom"   :description "if true, user interaction is disabled"}
   {:name :style         :required false                  :type "map"              :description "the CSS style style map"}
   {:name :label-class   :required false                  :type "string"           :description "the CSS class applied to the label"}
   {:name :label-style   :required false                  :type "string"           :description "the CSS class applied overall to the component"}])

(def checkbox-args
  (set (map :name checkbox-args-desc)))


;; TODO: when disabled?, should the text appear "disabled".
(defn checkbox
  "I return the markup for a checkbox, with an optional RHS label."
  [& {:keys [model on-change label disabled? style label-class label-style]
      :as   args}]
  {:pre [(validate-arguments checkbox-args (keys args))]}
  (let [cursor      "default"
        model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn #(when (and on-change (not disabled?))
                      (on-change (not model)))]  ;; call on-change with either true or false
    [h-box
     :align    :start
     :style    {:-webkit-user-select "none"}
     :children [[:input
                 {:class     "rc-checkbox"
                  :type      "checkbox"
                  :style     (merge {:flex   "none"
                                     :cursor cursor}
                                    style)
                  :disabled  disabled?
                  :checked   model
                  :on-change (handler-fn (callback-fn))}]
                (when label
                  [:span
                   {:on-click (handler-fn (callback-fn))
                    :class    label-class
                    :style    (merge {:padding-left "8px"
                                      :flex         "none"
                                      :cursor       cursor}
                                     label-style)}
                   label])]]))


;; ------------------------------------------------------------------------------------
;;  Component: radio-button
;; ------------------------------------------------------------------------------------

(def radio-button-args-desc
  [{:name :model         :required true                   :type "string | atom"    :description "Holds state of the radio button when it is called"}
   {:name :on-change     :required false                  :type "(boolean) -> nil" :description "function to call when radio button is clicked."}
   {:name :value         :required false                  :type "string"         :description "Value of the radio button OR button group"}
   {:name :label         :required false                  :type "string"         :description "Radio button label"}
   {:name :disabled?     :required false  :default false  :type "boolean | atom" :description "if true, the user can't click the radio button"}
   {:name :style         :required false                  :type "string"         :description "Radio button style map"}
   {:name :label-class   :required false                  :type "string"         :description "the CSS class applied to the label"}
   {:name :label-style   :required false                  :type "string"         :description "the CSS class applied overall to the component"}])

(def radio-button-args
  (set (map :name radio-button-args-desc)))

(defn radio-button
  "I return the markup for a radio button, with an optional RHS label."
  [& {:keys [model on-change value label disabled? style label-class label-style]
      :as   args}]
  {:pre [(validate-arguments radio-button-args (keys args))]}
  (let [cursor      "default"
        model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn #(when (and on-change (not disabled?))
                      (on-change (not model)))]  ;; call on-change with either true or false
    [h-box
     :align    :start
     :style    {:-webkit-user-select "none"}
     :children [[:input
                 {:class     "rc-radio-button"
                  :type      "radio"
                  :style     (merge
                               {:flex   "none"
                                :cursor cursor}
                               style)
                  :disabled  disabled?
                  :checked   (= model value)
                  :on-change (handler-fn (callback-fn))}]
                (when label
                  [:span
                   {:on-click (handler-fn (callback-fn))
                    :class    label-class
                    :style    (merge {:padding-left "8px"
                                      :flex         "none"
                                      :cursor       cursor}
                                     label-style)}
                   label])]]))


;; ------------------------------------------------------------------------------------
;;  Component: slider
;; ------------------------------------------------------------------------------------

(def slider-args-desc
  [{:name :model         :required false                  :type "string | atom" :description "Numeric double. Current value of the slider. Can be value or atom."}
   {:name :min           :required false                  :type "string"        :description "Numeric double. The minimum value of the slider. Default is 0. Can be value or atom."}
   {:name :max           :required false                  :type "string"        :description "Numeric double. The maximum value of the slider. Default is 100. Can be value or atom."}
   {:name :step          :required false                  :type "string"        :description "Numeric double. Step value between min and max. Default is 1. Can be value or atom."}
   {:name :width         :required false                  :type "string"        :description "Standard CSS width setting for the slider. Default is 400px."}
   {:name :on-change     :required false                  :type "string"        :description "A function which takes one parameter, which is the new value of the slider."}
   {:name :disabled?     :required false  :default false  :type "boolean | atom" :description "if true, the user can't change the slider."}
   {:name :class         :required false                  :type "string"        :description "CSS class names, space separated."}
   {:name :style         :required false                  :type "map"           :description "CSS styles to add or override."}
   {:name :attr          :required false                  :type "map"           :description [:span "html attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed."]}])

(def slider-args
  (set (map :name slider-args-desc)))

(defn slider
  "Returns markup for an HTML5 slider input."
  []
  (fn
    [& {:keys [model min max step width on-change disabled? class style attr]
        :or   {min 0 max 100}
        :as   args}]
    {:pre [(validate-arguments slider-args (keys args))]}
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
                  :style     (merge
                               {:flex   "none"
                                :width  (if width width "400px")
                                :cursor (if disabled? "not-allowed" "default")}
                               style)
                  :min       min
                  :max       max
                  :step      step
                  :value     model
                  :disabled  disabled?
                  :on-change (handler-fn (on-change (double (-> event .-target .-value))))}
                 attr)]])))


;; ------------------------------------------------------------------------------------
;;  Component: progress-bar
;; ------------------------------------------------------------------------------------

(def progress-bar-args-desc
  [{:name :model    :required true  :type "double"                                       :description "Numeric double (can be a string). Current value of the slider. Value or atom."}
   {:name :width    :required false :type "string"  :default "100%" :validate-fn string? :description "A CSS width"}
   {:name :striped? :required false :type "boolean" :default false                       :description "When true, the progress section is a set of animated stripes"}
   {:name :class    :required false :type "string"                  :validate-fn string? :description "CSS class names, space separated."}
   {:name :style    :required false :type "map"                     :validate-fn map?    :description "CSS styles to add or override."}
   {:name :attr     :required false :type "map"                     :validate-fn map?    :description [:span "html attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed."]}])

(def progress-bar-args (extract-arg-data progress-bar-args-desc))

(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [model width striped? class style attr]
      :or   {width "100%"}
      :as   args}]
  {:pre [(validate-args progress-bar-args args "progress-bar")]}
  (let [model (deref-or-value model)]
    [box
     :align :start
     :child [:div
             (merge
               {:class (str "rc-progress-bar progress " class)
                :style (merge {:flex  "none"
                               :width width}
                              style)}
               attr)
             [:div
              {:class (str "progress-bar " (when striped? "progress-bar-striped active"))
               :role  "progressbar"
               :style {:width      (str model "%")
                       :transition "none"}}                 ;; Default BS transitions cause the progress bar to lag behind
              (str model "%")]]]))


;; ------------------------------------------------------------------------------------
;;  Component: spinner
;; ------------------------------------------------------------------------------------

(defn spinner
  "Render an animated gif spinner"
  []
  [box
   :align :start
   :child [:div {:style {:display "flex"
                         :flex    "none"
                         :margin "10px"}}
           [:img {:src "resources/img/spinner.gif"
                  :style {:margin "auto"}}]]])


;; ------------------------------------------------------------------------------------
;;  Component: title
;; ------------------------------------------------------------------------------------

;; TODO: REMOVE THIS (ONLY USED IN POPOVER)

(def title-args-desc
  [{:name :label      :required true                 :type "string"  :description "Text of the title."}
   {:name :style      :required false                :type "map"     :description "CSS styles to add or override."}
   {:name :h          :required false  :default :h3  :type "string"  :description "Something like :h3 or :h4."}
   {:name :underline? :required false  :default true :type "string"  :description "Boolean determines whether an underline is placed under the title."}])

(def title-args
  (set (map :name title-args-desc)))

(defn title
  "An underlined, left justified, Title. By default :h3"
  [& {:keys [label h underline? style]
      :or   {underline? true h :h3}
      :as   args}]
  {:pre [(validate-arguments title-args (keys args))]}
  [v-box
   :children [[h {:style (merge
                           {:display "flex" :flex "none"}
                           style)}
               label]
              (when underline? [line :size "1px"])]])
