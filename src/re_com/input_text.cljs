(ns re-com.input-text
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.input-text.theme
   [re-com.input-text :as-alias it]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.theme.util :as tu]
   [re-com.util     :refer [deref-or-value]]
   [re-com.popover  :refer [popover-tooltip]]
   [re-com.throbber :refer [throbber]]
   [re-com.box      :refer [h-box]]
   [re-com.validate :refer [input-status-type? input-status-types-list regex? css-style? css-class? html-attr? parts?
                            number-or-string? nillable-string-or-atom?]]
   [reagent.core    :as reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: input-text
;; ------------------------------------------------------------------------------------

(def part-structure
  [::it/wrapper {:impl 're-com.box/h-box}
   [::it/inner {:tag :div}
    [::it/field {:impl "empty" :type :legacy :notes "The text input field where users type their input."}]
    [::it/status-icon {:notes "Status icon shown when status-icon? is true and status is not :validating."}]
    [::it/throbber {:impl 're-com.throbber/throbber :notes "Throbber shown when status is :validating."}]]])

(def input-text-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def input-text-parts
  (when include-args-desc?
    (-> (map :name input-text-parts-desc) set)))

(def input-text-args-desc
  (when include-args-desc?
    (into [{:name :model            :required true                   :type "string/nil | r/atom"      :validate-fn nillable-string-or-atom?  :description "text of the input (can be atom or value/nil)"}
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
           {:name :pre-theme        :required false                  :type "map -> map"               :validate-fn fn?                       :description "Pre-theme function"}
           {:name :theme            :required false                  :type "map -> map"               :validate-fn fn?                       :description "Theme function"}
           {:name :class            :required false                  :type "string"                   :validate-fn css-class?                :description "CSS class names, space separated (applies to the textbox, not the wrapping div)"}
           {:name :style            :required false                  :type "CSS style map"            :validate-fn css-style?                :description "CSS styles to add or override (applies to the textbox, not the wrapping div)"}
           {:name :attr             :required false                  :type "HTML attr map"            :validate-fn html-attr?                :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the textbox, not the wrapping div)"]}
           {:name :parts            :required false                  :type "map"                      :validate-fn (parts? input-text-parts) :description "See Parts section below."}
           {:name :input-type       :required false                  :type "keyword"                  :validate-fn keyword?                  :description [:span "ONLY applies to super function 'base-input-text': either " [:code ":input"] ", " [:code ":password"] " or " [:code ":textarea"]]}
           {:name :src              :required false                  :type "map"                      :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
           {:name :debug-as         :required false                  :type "map"                      :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
          (part/describe-args part-structure))))

;; Sample regex's:
;;  - #"^(-{0,1})(\d*)$"                   ;; Signed integer
;;  - #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$"  ;; Specific numeric value ##.#
;;  - #"^.{0,8}$"                          ;; 8 chars max
;;  - #"^[0-9a-fA-F]*$"                    ;; Hex number
;;  - #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" ;; Time input

(defn- input-text-base
  "Returns markup for a basic text input label"
  [& {:keys [model input-type src pre-theme theme] :as args}]
  (or
   (validate-args-macro input-text-args-desc args)
   (let [external-model (reagent/atom (deref-or-value model))
         internal-model (reagent/atom (if (nil? @external-model) "" @external-model))
         theme          (theme/comp pre-theme theme)]
     (fn input-text-base-render
       [& {:keys [model on-change status status-icon? status-tooltip placeholder width height rows change-on-blur? on-alter validation-regex disabled? class style attr src debug-as]
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
          (let [part       (partial part/part part-structure args)
                re-com-ctx {:state {:status       status
                                    :status-icon? status-icon?
                                    :input-type   input-type
                                    :showing?*    showing?}}]
            (part ::it/wrapper
              {:impl       h-box
               :theme      theme
               :post-props (-> {}
                               (cond-> width (tu/style {:width width}))
                               (debug/instrument args))
               :props
               {:re-com   re-com-ctx
                :src      src
                :debug-as (or debug-as (reflect-current-component))
                :children
                [(part ::it/inner
                   {:theme theme
                    :props
                    {:re-com re-com-ctx
                     :children
                     [(part ::it/field
                        {:theme theme
                         :props {:re-com re-com-ctx
                                 :tag    (case input-type
                                           :input    :input
                                           :password :input
                                           :textarea :textarea
                                           :input)}
                         :post-props
                         (cond-> {:attr {:type        (case input-type
                                                        :input    :text
                                                        :password :password
                                                        nil)
                                         :rows        (when (= input-type :textarea) (or rows 3))
                                         :placeholder placeholder
                                         :value       @internal-model
                                         :disabled    disabled?
                                         :on-change   (handler-fn
                                                       (let [new-val-orig (-> event .-target .-value)
                                                             new-val      (on-alter new-val-orig)]
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
                                                         (case (.-key event)
                                                           "Enter"  (on-change-handler)
                                                           "Escape" (reset! internal-model @external-model)
                                                           true)))}}
                           height (tu/style {:height height})
                           class  (tu/class class)
                           style  (tu/style style)
                           attr   (update :attr merge attr))})
                      (when (and status-icon? status)
                        (let [throbber-part (part ::it/throbber
                                              {:impl       throbber
                                               :theme      theme
                                               :post-props {:size :regular
                                                            :src  (at)}
                                               :props
                                               {:re-com re-com-ctx
                                                :attr   (when status-tooltip
                                                          {:on-mouse-over (handler-fn (when (and status-icon? status)
                                                                                        (reset! showing? true)))
                                                           :on-mouse-out  (handler-fn (reset! showing? false))})}})
                              status-icon   (part ::it/status-icon
                                              {:theme      theme
                                               :post-props (cond-> {:attr (when status-tooltip
                                                                            {:on-mouse-over (handler-fn (when (and status-icon? status)
                                                                                                          (reset! showing? true)))
                                                                             :on-mouse-out  (handler-fn (reset! showing? false))})}
                                                             status-tooltip (assoc :title status-tooltip))
                                               :props      {:re-com re-com-ctx
                                                            :tag    :i}})
                              icon-part     (if (= :validating status) throbber-part status-icon)]
                          (if status-tooltip
                            [popover-tooltip
                             :src      (at)
                             :label    status-tooltip
                             :position :right-center
                             :status   status
                             :showing? showing?
                             :anchor   icon-part]
                            icon-part)))]}})]}}))))))))

(defn input-text
  [& {:as args}]
  (input-text-base (merge {:input-type :input :debug-as (reflect-current-component)}
                          args)))

(defn input-password
  [& {:as args}]
  (input-text-base (merge {:input-type :password :debug-as (reflect-current-component)}
                          args)))

(defn input-textarea
  [& {:as args}]
  (input-text-base (merge {:input-type :textarea :debug-as (reflect-current-component)}
                          args)))


