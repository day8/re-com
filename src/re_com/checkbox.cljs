(ns re-com.checkbox
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.checkbox.theme
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.theme.util :as tu]
   [re-com.util     :refer [deref-or-value]]
   [re-com.box      :refer [h-box]]
   [re-com.validate :refer [string-or-hiccup? css-style? css-class? html-attr? parts?]]))

;; ------------------------------------------------------------------------------------
;;  Component: checkbox
;; ------------------------------------------------------------------------------------

(def part-structure
  [::wrapper {:impl 're-com.core/h-box}
   [::input {:tag :input}]
   [::label {:top-level-arg? true}]])

(def checkbox-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def checkbox-parts
  (when include-args-desc?
    (-> (map :name checkbox-parts-desc) set)))

(def checkbox-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :model       :required true                 :type "boolean | r/atom" :validate-fn #(or (nil? %) (some? %) (satisfies? IAtom %)) :description "holds state of the checkbox when it is called"}
       {:name :on-change   :required true                 :type "boolean -> nil"   :validate-fn fn?                     :description "called when the checkbox is clicked. Passed the new value of the checkbox"}
       {:name :disabled?   :required false :default false :type "boolean | r/atom" :validate-fn #(or (nil? %) (some? %) (satisfies? IAtom %)) :description "if true, user interaction is disabled"}
       {:name :label-class :required false                :type "string"           :validate-fn string?                 :description "CSS class names (applies to the label)"}
       {:name :label-style :required false                :type "CSS style map"    :validate-fn css-style?              :description "CSS style map (applies to the label)"}
       args/class
       args/style
       args/attr
       (args/parts checkbox-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args part-structure)))))

;; TODO: when disabled?, should the text appear "disabled".
(defn checkbox
  "Displays a single checkbox with optional label"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [model on-change disabled? label-class label-style class style attr]
            :as   props}]
      (or
       (validate-args-macro checkbox-args-desc props)
       (let [part            (partial part/part part-structure props)
             model           (deref-or-value model)
             disabled?       (deref-or-value disabled?)
             label-provided? (part/get-part part-structure props ::label)
             callback-fn     #(when (and on-change (not disabled?))
                                (on-change (not model)))
             re-com          {:state {:model           model
                                      :disabled?       disabled?
                                      :label-class     label-class
                                      :label-style     label-style
                                      :label-provided? label-provided?}}]
         (part ::wrapper
           {:impl       h-box
            :post-props (debug/instrument {} props)
            :theme      theme
            :props      {:re-com re-com
                         :children
                         [(part ::input
                            {:theme      theme
                             :props      {:re-com re-com
                                          :tag    :input
                                          :attr   {:type      :checkbox
                                                   :disabled  disabled?
                                                   :checked   (boolean model)
                                                   :on-change (handler-fn (callback-fn))}}
                             :post-props (cond-> {}
                                           class (assoc :class class)
                                           style (assoc :style style)
                                           attr  (assoc :attr attr))})

                          (when label-provided?
                            (part ::label
                              {:theme      theme
                               :props      {:re-com re-com
                                            :attr   {:on-click (handler-fn (callback-fn))}}
                               :post-props (cond-> {}
                                             label-class (tu/class label-class)
                                             label-style (tu/style label-style))}))]}}))))))
