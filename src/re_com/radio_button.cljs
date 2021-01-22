(ns re-com.radio-button
  (:require-macros
    [re-com.core :refer [handler-fn]]
    [re-com.validate :refer [validate-args-macro]])
  (:require [re-com.util     :refer [deref-or-value px]]
            [re-com.popover  :refer [popover-tooltip]]
            [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]]
            [re-com.validate :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts?
                                     number-or-string? string-or-atom? nillable-string-or-atom? throbber-size? throbber-sizes-list]]
            [reagent.core    :as    reagent]))

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