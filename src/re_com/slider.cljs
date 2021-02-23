(ns re-com.slider
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.util     :refer [deref-or-value px]]
    [re-com.popover  :refer [popover-tooltip]]
    [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]]
    [re-com.validate :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts?
                             number-or-string? string-or-atom? nillable-string-or-atom? throbber-size? throbber-sizes-list]]))

;; ------------------------------------------------------------------------------------
;;  Component: slider
;; ------------------------------------------------------------------------------------

(def slider-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-slider-wrapper" :impl "[slider]" :notes "Outer wrapper of the slider."}
     {:type :legacy  :level 1 :class "rc-slider"         :impl "[:input]" :notes "The actual input field."}]))

(def slider-parts
  (when include-args-desc?
    (-> (map :name slider-parts-desc) set)))

(def slider-args-desc
  (when include-args-desc?
    [{:name :model     :required true                   :type "double | string | r/atom" :validate-fn number-or-string?     :description "current value of the slider"}
     {:name :on-change :required true                   :type "double -> nil"            :validate-fn fn?                   :description "called when the slider is moved. Passed the new value of the slider"}
     {:name :min       :required false :default 0       :type "double | string | r/atom" :validate-fn number-or-string?     :description "the minimum value of the slider"}
     {:name :max       :required false :default 100     :type "double | string | r/atom" :validate-fn number-or-string?     :description "the maximum value of the slider"}
     {:name :step      :required false :default 1       :type "double | string | r/atom" :validate-fn number-or-string?     :description "step value between min and max"}
     {:name :width     :required false :default "400px" :type "string"                   :validate-fn string?               :description "standard CSS width setting for the slider"}
     {:name :disabled? :required false :default false   :type "boolean | r/atom"                                            :description "if true, the user can't change the slider"}
     {:name :class     :required false                  :type "string"                   :validate-fn string?               :description "CSS class names, space separated (applies to the slider, not the wrapping div)"}
     {:name :style     :required false                  :type "CSS style map"            :validate-fn css-style?            :description "CSS styles to add or override (applies to the slider, not the wrapping div)"}
     {:name :attr      :required false                  :type "HTML attr map"            :validate-fn html-attr?            :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the slider, not the wrapping div)"]}
     {:name :parts     :required false                  :type "map"                      :validate-fn (parts? slider-parts) :description "See Parts section below."}
     {:name :src       :required false                  :type "map"                      :validate-fn map?                  :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as  :required false                  :type "map"                      :validate-fn map?                  :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn slider
  "Returns markup for an HTML5 slider input"
  [& {:keys [model min max step width on-change disabled? class style attr parts src debug-as]
      :or   {min 0 max 100}
      :as   args}]
  (or
    (validate-args-macro slider-args-desc args)
    (let [model     (deref-or-value model)
          min       (deref-or-value min)
          max       (deref-or-value max)
          step      (deref-or-value step)
          disabled? (deref-or-value disabled?)]
      [box
       :src      src
       :debug-as (or debug-as (reflect-current-component))
       :class    (str "rc-slider-wrapper " (get-in parts [:wrapper :class]))
       :style    (get-in parts [:wrapper :style] {})
       :attr     (get-in parts [:wrapper :attr] {})
       :align    :start
       :child    [:input
                  (merge
                    {:class     (str "rc-slider " class)
                     :type      "range"
                     ;:orient    "vertical" ;; Make Firefox slider vertical (doesn't work because React ignores it, I think)
                     :style     (merge
                                  (flex-child-style "none")
                                  {;:-webkit-appearance "slider-vertical"   ;; TODO: Make a :orientation (:horizontal/:vertical) option
                                   ;:writing-mode       "bt-lr"             ;; Make IE slider vertical
                                   :width  (or width "400px")
                                   :cursor (if disabled? "default" "pointer")}
                                  style)
                     :min       min
                     :max       max
                     :step      step
                     :value     model
                     :disabled  disabled?
                     :on-change (handler-fn (on-change (js/Number (-> event .-target .-value))))}
                    attr)]])))