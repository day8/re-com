(ns re-com.input-text
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.util     :refer [deref-or-value px]]
    [re-com.popover  :refer [popover-tooltip]]
    [re-com.throbber :refer [throbber]]
    [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]]
    [re-com.validate :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts?
                             number-or-string? string-or-atom? nillable-string-or-atom? throbber-size? throbber-sizes-list]]
    [reagent.core    :as    reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: input-text
;; ------------------------------------------------------------------------------------

(def input-text-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-input-text"       :impl "[input-text]" :notes "Outer wrapper of the text input."}
     {:name :inner   :level 1 :class "rc-input-text-inner" :impl "[:div]"       :notes "The container for the text input."}
     {:type :legacy  :level 2 :class "rc-input-text-field" :impl "[:input]"     :notes "The actual input field."}]))

(def input-text-parts
  (when include-args-desc?
    (-> (map :name input-text-parts-desc) set)))

(def input-text-args-desc
  (when include-args-desc?
    [{:name :model            :required true                   :type "string/nil | r/atom"      :validate-fn nillable-string-or-atom?  :description "text of the input (can be atom or value/nil)"}
     {:name :on-change        :required true                   :type "string[, done-fn] -> nil" :validate-fn fn?                       :description [:span [:code ":change-on-blur?"] " controls when it is called. Passed the current input string, and optionally a function to call (with no args) to signal that " [:code ":model"] " has reached a steady state to avoid displaying a prior value while processing."]}
     {:name :status           :required false                  :type "keyword"                  :validate-fn input-status-type?        :description [:span "validation status. " [:code "nil/omitted"] " for normal status or one of: " input-status-types-list]}
     {:name :status-icon?     :required false :default false   :type "boolean"                                                         :description [:span "when true, display an icon to match " [:code ":status"] " (no icon for nil)"]}
     {:name :status-tooltip   :required false                  :type "string"                   :validate-fn string?                   :description "displayed in status icon's tooltip"}
     {:name :placeholder      :required false                  :type "string"                   :validate-fn string?                   :description "background text shown when empty"}
     {:name :width            :required false :default "250px" :type "string"                   :validate-fn string?                   :description "standard CSS width setting for this input"}
     {:name :height           :required false                  :type "string"                   :validate-fn string?                   :description "standard CSS height setting for this input"}
     {:name :rows             :required false :default 3       :type "integer | string"         :validate-fn number-or-string?         :description "ONLY applies to 'input-textarea': the number of rows of text to show"}
     {:name :change-on-blur?  :required false :default true    :type "boolean | r/atom"                                                :description [:span "when true, invoke " [:code ":on-change"] " function on blur, otherwise on every change (character by character)"]}
     {:name :on-alter         :required false                  :type "string -> string"         :validate-fn fn?                       :description "called with the new value to alter it immediately"}
     {:name :validation-regex :required false                  :type "regex"                    :validate-fn regex?                    :description "user input is only accepted if it would result in a string that matches this regular expression"}
     {:name :disabled?        :required false :default false   :type "boolean | r/atom"                                                :description "if true, the user can't interact (input anything)"}
     {:name :class            :required false                  :type "string"                   :validate-fn string?                   :description "CSS class names, space separated (applies to the textbox, not the wrapping div)"}
     {:name :style            :required false                  :type "CSS style map"            :validate-fn css-style?                :description "CSS styles to add or override (applies to the textbox, not the wrapping div)"}
     {:name :attr             :required false                  :type "HTML attr map"            :validate-fn html-attr?                :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the textbox, not the wrapping div)"]}
     {:name :parts            :required false                  :type "map"                      :validate-fn (parts? input-text-parts) :description "See Parts section below."}
     {:name :input-type       :required false                  :type "keyword"                  :validate-fn keyword?                  :description [:span "ONLY applies to super function 'base-input-text': either " [:code ":input"] ", " [:code ":password"] " or " [:code ":textarea"]]}
     {:name :src              :required false                  :type "map"                      :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                  :type "map"                      :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

;; Sample regex's:
;;  - #"^(-{0,1})(\d*)$"                   ;; Signed integer
;;  - #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$"  ;; Specific numeric value ##.#
;;  - #"^.{0,8}$"                          ;; 8 chars max
;;  - #"^[0-9a-fA-F]*$"                    ;; Hex number
;;  - #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" ;; Time input

(defn- input-text-base
  "Returns markup for a basic text input label"
  [& {:keys [model input-type src] :as args}]
  (or
    (validate-args-macro input-text-args-desc args)
    (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
          internal-model (reagent/atom (if (nil? @external-model) "" @external-model))] ;; Create a new atom from the model to be used internally (avoid nil)]
      (fn input-text-base-render
        [& {:keys [model on-change status status-icon? status-tooltip placeholder width height rows change-on-blur? on-alter validation-regex disabled? class style attr parts src debug-as]
            :or   {change-on-blur? true, on-alter identity}
            :as   args}]
        (or
          (validate-args-macro input-text-args-desc args)
          (let [latest-ext-model  (deref-or-value model)
                disabled?         (deref-or-value disabled?)
                change-on-blur?   (deref-or-value change-on-blur?)
                showing?          (reagent/atom false)
                ;; If the user types a value that is subsequently modified in :on-change to the prior value of :model, such
                ;; as validation or filtering, the :model is reset! to the same value then the value that the user typed
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
                                      (let [has-done-fn? (= 2 (.-length ^js/Function on-change))
                                            reset-fn     #(reset! external-model @internal-model)]
                                        (if has-done-fn?
                                          (on-change @internal-model reset-fn)
                                          (do
                                            (on-change @internal-model)
                                            (reset-fn))))))]
            (when (not= @external-model latest-ext-model) ;; Has model changed externally?
              (reset! external-model latest-ext-model)
              (reset! internal-model latest-ext-model))
            [h-box
             :src      src
             :debug-as (or debug-as (reflect-current-component))
             :align    :start
             :class    (str "rc-input-text " (get-in parts [:wrapper :class]))
             :style    (get-in parts [:wrapper :style])
             :attr     (get-in parts [:wrapper :attr])
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
                            {:class       (str "form-control rc-input-text-field " class)
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
                              :src      (at)
                              :label    status-tooltip
                              :position :right-center
                              :status   status
                              ;:width    "200px"
                              :showing? showing?
                              :anchor   (if (= :validating status)
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
                              :style    (merge (flex-child-style "none")
                                               (align-style :align-self :center)
                                               {:font-size   "130%"
                                                :margin-left "4px"})]
                             (if (= :validating status)
                               [throbber
                                :src   (at)
                                :size  :regular
                                :class "smaller"]
                               [:i {:class (str "zmdi zmdi-hc-fw " icon-class " form-control-feedback")
                                    :style (merge (flex-child-style "none")
                                                  (align-style :align-self :center)
                                                  {:position    "static"
                                                   :font-size   "130%"
                                                   :margin-left "4px"
                                                   :opacity     (if (and status-icon? status) "1" "0")
                                                   :height      "auto"})
                                    :title status-tooltip}]))))]]))))))


(defn input-text
  [& args]
  (apply input-text-base :input-type :input :debug-as (reflect-current-component) args))


(defn input-password
  [& args]
  (apply input-text-base :input-type :password :debug-as (reflect-current-component) args))


(defn input-textarea
  [& args]
  (apply input-text-base :input-type :textarea :debug-as (reflect-current-component) args))