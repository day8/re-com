(ns re-com.core
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util    :refer [deref-or-value validate-arguments px]]
            [re-com.popover :refer [popover-tooltip]]
            [re-com.box     :refer [h-box v-box box gap line]]
            [reagent.core   :as    reagent]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

(def label-args-desc
  [{:name :label     :required true  :type "string"     :description "text to display"}
   {:name :on-click  :required false :type "function"   :description "callback function to call when label is clicked"}
   {:name :width     :required false :type "string"     :description "a CSS width"}
   {:name :class     :required false :type "string"     :description "a CSS class name"}
   {:name :style     :required false :type "string"     :description "additional CSS styles"}
   ])

(def label-args
  (set (map :name label-args-desc)))

(defn label
  "Returns markup for a basic label"
  [& {:keys [label on-click width class style]
      :as   args}]
  {:pre [(validate-arguments label-args (keys args))]}
  [box
   :width width
   :align :start
   :child [:span
           (merge
             {:class (str "rc-label " class)
              :style (merge {:flex "none"} style)}
             (when on-click
               ;{:on-click #(do (on-click) false)}
               {:on-click (handler-fn (on-click))}
               ))
           label]])


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
   {:name :change-on-blur?  :required false :default false   :type "boolean"    :description "when true, invoke on-change function on blur, otherwise on every change (character by character)."}
   {:name :validation-regex :required false                  :type "regex"      :description "the regular expression which determines which characters are legal and which aren't."}
   {:name :disabled?        :required false :default false   :type "boolean"    :description "set to true to disable the input box (can be atom or value)."}
   {:name :class            :required false                  :type "string"     :description "additional CSS classes required."}
   {:name :style            :required false                  :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                  :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}
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

                     ;:on-change   (fn [event]
                     ;               (let [new-val (-> event .-target .-value)]
                     ;                 (when (and
                     ;                         on-change
                     ;                         (not disabled?)
                     ;                         (if validation-regex (re-find validation-regex new-val) true))
                     ;                   (reset! internal-model new-val)
                     ;                   (when-not change-on-blur?
                     ;                     (on-change @internal-model)))
                     ;                 false))
                     :on-change   (handler-fn
                                    (let [new-val (-> event .-target .-value)]
                                      (when (and
                                              on-change
                                              (not disabled?)
                                              (if validation-regex (re-find validation-regex new-val) true))
                                        (reset! internal-model new-val)
                                        (when-not change-on-blur?
                                          (on-change @internal-model)))))

                     ;:on-blur     #(do (when (and
                     ;                          on-change
                     ;                          change-on-blur?
                     ;                          (not= @internal-model @external-model))
                     ;                    (on-change @internal-model))
                     ;                  false)
                     :on-blur     (handler-fn
                                    (when (and
                                            on-change
                                            change-on-blur?
                                            (not= @internal-model @external-model))
                                      (on-change @internal-model))
                                    #_(.preventDefault event))

                     ;:on-key-up   #(if disabled?
                     ;               false
                     ;               (do (case (.-which %)
                     ;                     13 (when on-change (on-change @internal-model))
                     ;                     27 (reset! internal-model @external-model)
                     ;                     true)
                     ;                   true))
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
                      :label status-tooltip
                      :position :right-center
                      :status status
                      ;:width "200px"
                      :showing? showing?
                      :anchor [:i {:class         (str (if (= status :warning) "md-warning" "md-error") " form-control-feedback")
                                   :style         {:position "static"
                                                   :width    "auto"
                                                   :height   "auto"}
                                   ;:on-mouse-over #(do (reset! showing? true) true) ;; true CANCELs mouse-over (false cancels all others)
                                   :on-mouse-over (handler-fn (reset! showing? true))
                                   ;:on-mouse-out  #(do (reset! showing? false) false)
                                   :on-mouse-out  (handler-fn (reset! showing? false))
                                   }]
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
  [{:name :model         :required false                  :type "string"     :description "Holds state of the checkbox when it is called"}
   {:name :on-change     :required false                  :type "string"     :description "When model state is changed, call back with new state"}
   {:name :label         :required false                  :type "string"     :description "Checkbox label"}
   {:name :disabled?     :required false  :default false  :type "boolean"    :description "Set to true to disable the checkbox"}
   {:name :style         :required false                  :type "string"     :description "Checkbox style map"}
   {:name :label-class   :required false                  :type "string"     :description "Label class string"}
   {:name :label-style   :required false                  :type "string"     :description "Label style map"}])

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
  [{:name :model         :required false                  :type "string"     :description "Holds state of the radio button when it is called"}
   {:name :on-change     :required false                  :type "string"     :description "When model state is changed, call back with new state"}
   {:name :value         :required false                  :type "string"     :description "Value of the radio button OR button group"}
   {:name :label         :required false                  :type "string"     :description "Radio button label"}
   {:name :disabled?     :required false  :default false  :type "string"     :description "Set to true to disable the radio button"}
   {:name :style         :required false                  :type "string"     :description "Radio button style map"}
   {:name :label-class   :required false                  :type "string"     :description "Label class string"}
   {:name :label-style   :required false                  :type "string"     :description "Label style map"}])

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
  [{:name :model         :required false                  :type "string"     :description "Numeric double. Current value of the slider. Can be value or atom."}
   {:name :min           :required false                  :type "string"     :description "Numeric double. The minimum value of the slider. Default is 0. Can be value or atom."}
   {:name :max           :required false                  :type "string"     :description "Numeric double. The maximum value of the slider. Default is 100. Can be value or atom."}
   {:name :step          :required false                  :type "string"     :description "Numeric double. Step value between min and max. Default is 1. Can be value or atom."}
   {:name :width         :required false                  :type "string"     :description "Standard CSS width setting for the slider. Default is 400px."}
   {:name :on-change     :required false                  :type "string"     :description "A function which takes one parameter, which is the new value of the slider."}
   {:name :disabled?     :required false  :default false  :type "string"     :description "Set to true to disable the slider. Can be value or atom."}
   {:name :class         :required false                  :type "string"     :description "additional CSS classes required."}
   {:name :style         :required false                  :type "map"        :description "CSS styles to add or override."}
   {:name :attr          :required false                  :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}])

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
                  ;:on-change #(on-change (double (-> % .-target .-value)))
                  :on-change (handler-fn (on-change (double (-> event .-target .-value))))
                  }
                 attr)]])))


;; ------------------------------------------------------------------------------------
;;  Component: inline-tooltip
;; ------------------------------------------------------------------------------------

#_(def inline-tooltip-args-desc
  [{:name :label         :required true                     :type "string"     :description "the text in the tooltip."}
   {:name :position      :required false  :default ":below" :type "keyword"    :description "where the tooltip will appear, relative to what it points at (:left, :right, :above, :below)."}
   {:name :status        :required false  :default "nil"    :type "keyword"    :description "controls background colour of the tooltip. Values: nil= black, :warning = orange, :error = red)."}
   {:name :max-width     :required false  :default "200px"  :type "string"     :description "set max width of the tool tip."}
   {:name :class         :required false                    :type "string"     :description "CSS classes appended to base component list."}
   {:name :style         :required false                    :type "map"        :description "CSS styles. Will override (or add to) the base component base styles."}
   {:name :attr          :required false                    :type "map"        :description "HTML Element attributes. Will override (or add to) those in the base component. Expected to be things like on-mouse-over, etc. (:class/:style not allowed)."}])

#_(def inline-tooltip-args
  (set (map :name inline-tooltip-args-desc)))

#_(defn inline-tooltip
  "Returns markup for an inline-tooltip."
  []
  (fn
    [& {:keys [label position status max-width class style attr]
        :or   {position :above}
        :as   args}]
    {:pre [(validate-arguments inline-tooltip-args (keys args))]}
    (assert (not-any? #(contains? #{:style :class} (first %)) attr) ":attr cannot contain :class or :style members")
    (let [bg-col       (case status
                         :warning "#f57c00"
                         :error   "#d50000"
                         nil)
          which-border (case position
                         :left  :border-left-color
                         :right :border-right-color
                         :above :border-top-color
                         :below :border-bottom-color
                         :border-bottom-color)]
      [box
       :align :start
       :child [:div
               (merge
                 {:class (str "rc-inline-tooltip tooltip "
                              (case position
                                :left "left"
                                :right "right"
                                :above "top"
                                :below "bottom"
                                "bottom")
                              " "
                              class)
                  :style (merge {:flex     "none"
                                 :position "relative"
                                 :opacity  1}
                                style)}
                 attr)
               [:div.tooltip-arrow
                {:style {which-border bg-col}}]
               [:div.tooltip-inner
                {:style {:background-color bg-col
                         :max-width        (when max-width max-width)
                         :font-weight      "bold"}}
                label]]])))


;; ------------------------------------------------------------------------------------
;;  Component: progress-bar
;; ------------------------------------------------------------------------------------

(def progress-bar-args-desc
  [{:name :model  :required false  :type "string"  :description "Numeric double. Current value of the slider. Can be value or atom."}])

(def progress-bar-args
  (set (map :name progress-bar-args-desc)))

(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [model] :as args}]
  {:pre [(validate-arguments progress-bar-args (keys args))]}
  [box
   :align :start
   :child [:div
           {:class "rc-progress-bar progress"
            :style {:flex "none"}}
           [:div.progress-bar ;;.progress-bar-striped.active
            {:role "progressbar"
             :style {:width (str @model "%")
                     :transition "none"}} ;; Default BS transitions cause the progress bar to lag behind
            (str @model "%")]]])


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
