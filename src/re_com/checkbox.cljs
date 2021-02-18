(ns re-com.checkbox
  (:require-macros
    [re-com.core     :refer [handler-fn]]
    [re-com.debug    :refer [src-coordinates]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [re-com.debug     :refer [src->attr]]
    [re-com.config    :refer [include-args-desc?]]
    [re-com.util      :refer [deref-or-value px]]
    [re-com.popover   :refer [popover-tooltip]]
    [re-com.box       :refer [h-box v-box box gap line flex-child-style align-style]]
    [re-com.validate  :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts?
                              number-or-string? string-or-atom? nillable-string-or-atom? throbber-size? throbber-sizes-list]]
    [reagent.core     :as    reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: checkbox
;; ------------------------------------------------------------------------------------

(def checkbox-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-checkbox-wrapper" :impl "[checkbox]" :notes "Outer wrapper of the checkbox, label, everything."}
     {:type :legacy  :level 1 :class "rc-checkbox"         :impl "[:input]"   :notes "The actual checkbox."}
     {:type :legacy  :level 1 :class "rc-checkbox-label"   :impl "[:span]"    :name-label [:span "Use " [:code ":label-class"] " or " [:code ":label-style"] " instead."]}]))

(def checkbox-parts
  (when include-args-desc?
    (-> (map :name checkbox-parts-desc) set)))

(def checkbox-args-desc
  (when include-args-desc?
    [{:name :model       :required true                 :type "boolean | r/atom"                                      :description "holds state of the checkbox when it is called"}
     {:name :on-change   :required true                 :type "boolean -> nil"   :validate-fn fn?                     :description "called when the checkbox is clicked. Passed the new value of the checkbox"}
     {:name :label       :required false                :type "string | hiccup"  :validate-fn string-or-hiccup?       :description "the label shown to the right"}
     {:name :disabled?   :required false :default false :type "boolean | r/atom"                                      :description "if true, user interaction is disabled"}
     {:name :label-class :required false                :type "string"           :validate-fn string?                 :description "CSS class names (applies to the label)"}
     {:name :label-style :required false                :type "CSS style map"    :validate-fn css-style?              :description "CSS style map (applies to the label)"}
     {:name :class       :required false                :type "string"           :validate-fn string?                 :description "CSS class names, space separated (applies to the checkbox, not the wrapping div)"}
     {:name :style       :required false                :type "CSS style map"    :validate-fn css-style?              :description "CSS style map (applies to the checkbox, not the wrapping div)"}
     {:name :attr        :required false                :type "HTML attr map"    :validate-fn html-attr?              :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the checkbox, not the wrapping div)"]}
     {:name :parts       :required false                :type "map"              :validate-fn (parts? checkbox-parts) :description "See Parts section below."}
     {:name :src         :required false                :type "map"              :validate-fn map?                    :description "Source code coordinates. See 'Debugging'."}]))

;; TODO: when disabled?, should the text appear "disabled".
(defn checkbox
  [& {:keys [model on-change label disabled? label-class label-style class style attr parts src]
      :as   args}]
  (or
    (validate-args-macro checkbox-args-desc args src)
    (let [cursor      "default"
          model       (deref-or-value model)
          disabled?   (deref-or-value disabled?)
          callback-fn #(when (and on-change (not disabled?))
                        (on-change (not model)))]  ;; call on-change with either true or false
      [h-box
       :src      src
       :class    (str "noselect rc-checkbox-wrapper " (get-in parts [:wrapper :class]))
       :style    (get-in parts [:wrapper :style])
       :attr     (get-in parts [:wrapper :attr])
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
                     label])]])))