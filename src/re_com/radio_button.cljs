(ns re-com.radio-button
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.radio-button.theme
   [re-com.radio-button :as-alias rb]
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.theme    :as theme]
   [re-com.part     :as part]
   [re-com.util     :refer [deref-or-value px]]
   [re-com.popover  :refer [popover-tooltip]]
   [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]]
   [re-com.validate :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts?
                            number-or-string? string-or-atom? nillable-string-or-atom? throbber-size? throbber-sizes-list css-class?]]))

;; ------------------------------------------------------------------------------------
;;  Component: radio-button
;; ------------------------------------------------------------------------------------

(def radio-button-part-structure
  [::rb/wrapper {:impl 're-com.box/h-box}
   [::rb/input {:tag :input}]
   [::rb/label {:top-level-arg? true :impl "empty"}]])

(def radio-button-parts-desc
  (when include-args-desc?
    (part/describe radio-button-part-structure)))

(def radio-button-parts
  (when include-args-desc?
    (-> (map :name radio-button-parts-desc) set)))

(def radio-button-args-desc
  (when include-args-desc?
    (into
     [{:name :model       :required true                 :type "anything | r/atom"                               :description [:span "selected value of the radio button group. See also " [:code ":value"]]}
      {:name :value       :required false                :type "anything"                                        :description [:span "if " [:code ":model"]  " equals " [:code ":value"] " then this radio button is selected"]}
      {:name :on-change   :required true                 :type "anything -> nil"  :validate-fn fn?               :description [:span "called when the radio button is clicked. Passed " [:code ":value"]]}
      {:name :label       :required false                :type "string | hiccup"  :validate-fn string-or-hiccup? :description "the label shown to the right"}
      {:name :disabled?   :required false :default false :type "boolean | r/atom"                                :description "if true, the user can't click the radio button"}
      {:name :label-class :required false                :type "string"           :validate-fn string?           :description "CSS class names (applies to the label)"}
      {:name :label-style :required false                :type "CSS style map"    :validate-fn css-style?        :description "CSS style map (applies to the label)"}
      args/class
      args/style
      args/attr
      (args/parts radio-button-parts)
      args/src
      args/debug-as]
     (concat theme/args-desc
             (part/describe-args radio-button-part-structure)))))

(defn radio-button
  "I return the markup for a radio button, with an optional RHS label"
  [& {:keys [pre-theme theme debug-as]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [model value on-change disabled? label-class label-style]
            :as   args}]
      (or
       (validate-args-macro radio-button-args-desc args)
       (let [model       (deref-or-value model)
             disabled?   (deref-or-value disabled?)
             checked?    (= model value)
             callback-fn #(when (and on-change (not disabled?) (not checked?))
                            (on-change value))
             part        (partial part/part radio-button-part-structure args)
             re-com-ctx  {:state {:disabled?   disabled?
                                  :checked?    checked?
                                  :label-class label-class
                                  :label-style label-style}}
             input-part  (part ::rb/input
                               {:theme      theme
                                :post-props (-> (select-keys args [:class :style :attr])
                                                (update :attr merge {:type      "radio"
                                                                     :disabled  disabled?
                                                                     :checked   checked?
                                                                     :on-change (handler-fn (callback-fn))}))
                                :props      {:re-com re-com-ctx
                                             :src    (at)
                                             :tag    :input}})
             label?     (part/get-part radio-button-part-structure args ::rb/label)
             label-part (when label?
                          (part ::rb/label
                                {:theme      theme
                                 :post-props {:on-click (handler-fn (callback-fn))}
                                 :props      {:re-com re-com-ctx
                                              :src    (at)}}))]
         (part ::rb/wrapper
               {:impl       h-box
                :theme      theme
                :post-props (-> {:debug-as (or debug-as (reflect-current-component))}
                                (debug/instrument args))
                :props      {:re-com   re-com-ctx
                             :src      (at)
                             :children [input-part label-part]}}))))))
