(ns re-com.checkbox
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.checkbox.theme
   [re-com.checkbox :as-alias cb]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.theme.util :as tu]
   [re-com.util     :refer [deref-or-value]]
   [re-com.box      :refer [h-box]]
   [re-com.validate :refer [string-or-hiccup? css-style? css-class? html-attr? parts?]]
   [reagent.core    :as reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: checkbox
;; ------------------------------------------------------------------------------------

(def part-structure
  [::cb/wrapper {:impl 're-com.core/h-box}
   [::cb/input {:impl "input"}]
   [::cb/label {:top-level-arg? true :impl "span"}]])

(def checkbox-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def checkbox-parts
  (when include-args-desc?
    (-> (map :name checkbox-parts-desc) set)))

(def checkbox-args-desc
  (when include-args-desc?
    (concat
     [{:name :model       :required true                 :type "boolean | r/atom"                                      :description "holds state of the checkbox when it is called"}
      {:name :on-change   :required true                 :type "boolean -> nil"   :validate-fn fn?                     :description "called when the checkbox is clicked. Passed the new value of the checkbox"}
      {:name :disabled?   :required false :default false :type "boolean | r/atom"                                      :description "if true, user interaction is disabled"}
      {:name :label-class :required false                :type "string"           :validate-fn string?                 :description "CSS class names (applies to the label)"}
      {:name :label-style :required false                :type "CSS style map"    :validate-fn css-style?              :description "CSS style map (applies to the label)"}
      {:name :pre-theme   :required false                :type "map -> map"       :validate-fn fn?                     :description "Pre-theme function"}
      {:name :theme       :required false                :type "map -> map"       :validate-fn fn?                     :description "Theme function"}
      {:name :class       :required false                :type "string"           :validate-fn css-class?              :description "CSS class names, space separated (applies to the wrapper)"}
      {:name :style       :required false                :type "CSS style map"    :validate-fn css-style?              :description "CSS style map (applies to the wrapper)"}
      {:name :attr        :required false                :type "HTML attr map"    :validate-fn html-attr?              :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the wrapper)"]}
      {:name :parts       :required false                :type "map"              :validate-fn (parts? checkbox-parts) :description "See Parts section below."}
      {:name :src         :required false                :type "map"              :validate-fn map?                    :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
      {:name :debug-as    :required false                :type "map"              :validate-fn map?                    :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
     (part/describe-args part-structure))))

;; TODO: when disabled?, should the text appear "disabled".
(defn checkbox
  "Displays a single checkbox with optional label"
  [& {:keys [model on-change disabled? label-class label-style pre-theme theme]
      :as   props}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [model on-change disabled? label-class label-style]
            :as   props}]
      (or
       (validate-args-macro checkbox-args-desc props)
       (let [part            (partial part/part part-structure props)
             model           (deref-or-value model)
             disabled?       (deref-or-value disabled?)
             label-provided? (part/get-part part-structure props ::cb/label)
             callback-fn     #(when (and on-change (not disabled?))
                                (on-change (not model)))]
         (part ::cb/wrapper
           {:impl       h-box
            :post-props (-> (select-keys props [:class :style :attr])
                            (debug/instrument props))
            :theme      theme
            :props
            {:children
             [(part ::cb/input
                {:theme      theme
                 :props      {:tag :input}
                 :post-props {:attr {:type      :checkbox
                                     :disabled  disabled?
                                     :checked   (boolean model)
                                     :on-change (handler-fn (callback-fn))}}})

              (when label-provided?
                (part ::cb/label
                  {:theme      theme
                   :post-props (cond-> {:on-click (handler-fn (callback-fn))}
                                 label-class (tu/class label-class)
                                 label-style (tu/style label-style))}))]}}))))))
