(ns re-com.slider
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.slider.theme
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.util     :refer [deref-or-value px]]
   [re-com.box      :refer [box]]
   [re-com.validate :refer [number-or-string? css-style? html-attr? parts? css-class?]]))

;; ------------------------------------------------------------------------------------
;;  Component: slider
;; ------------------------------------------------------------------------------------

(def part-structure
  [::wrapper {:impl 're-com.box/box}
   [::input {:tag :input}]])

(def slider-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def slider-parts
  (when include-args-desc?
    (-> (map :name slider-parts-desc) set)))

(def slider-args-desc
  (when include-args-desc?
    (into
     [{:name :model     :required true                   :type "double | string | r/atom" :validate-fn number-or-string? :description "current value of the slider"}
      {:name :on-change :required true                   :type "double -> nil"            :validate-fn fn?               :description "called when the slider is moved. Passed the new value of the slider"}
      {:name :min       :required false :default 0       :type "double | string | r/atom" :validate-fn number-or-string? :description "the minimum value of the slider"}
      {:name :max       :required false :default 100     :type "double | string | r/atom" :validate-fn number-or-string? :description "the maximum value of the slider"}
      {:name :step      :required false :default 1       :type "double | string | r/atom" :validate-fn number-or-string? :description "step value between min and max"}
      {:name :width     :required false :default "400px" :type "string"                   :validate-fn string?           :description "standard CSS width setting for the slider"}
      {:name :disabled? :required false :default false   :type "boolean | r/atom"                                        :description "if true, the user can't change the slider"}
      args/class
      args/style
      args/attr
      (args/parts slider-parts)
      args/src
      args/debug-as]
     (concat theme/args-desc
             (part/describe-args part-structure)))))

(defn slider
  "Returns markup for an HTML5 slider input"
  [& {:keys [pre-theme theme debug-as]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [model min max step width on-change disabled?]
            :or   {min 0 max 100}
            :as   args}]
      (or
       (validate-args-macro slider-args-desc args)
       (let [part      (partial part/part part-structure args)
             model     (deref-or-value model)
             min       (deref-or-value min)
             max       (deref-or-value max)
             step      (deref-or-value step)
             disabled? (deref-or-value disabled?)
             re-com    {:state {:disabled? disabled?
                                :width     width}}]
         (part ::wrapper
           {:impl       box
            :theme      theme
            :post-props {:attr (debug/->attr args)
                         :debug-as (or debug-as (reflect-current-component))}
            :props
            {:re-com re-com
             :child
             (part ::input
               {:theme theme
                :props {:re-com re-com
                        :tag    :input}
                :post-props
                (-> args
                    (select-keys [:class :style :attr])
                    (update :attr merge {:type     "range"
                                         :min      min
                                         :max      max
                                         :step     step
                                         :value    model
                                         :disabled disabled?
                                         :on-change
                                         (handler-fn (on-change
                                                      (js/Number
                                                       (-> event .-target .-value))))}))})}}))))))
