(ns re-com.progress-bar
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.progress-bar.theme
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.theme.util :as tu]
   [re-com.util     :refer [deref-or-value px]]
   [re-com.box      :refer [box flex-child-style]]
   [re-com.validate :refer [number-or-string? css-style? html-attr? parts? css-class?]]))

;; ------------------------------------------------------------------------------------
;;  Component: progress-bar
;; ------------------------------------------------------------------------------------

(def part-structure
  [::wrapper {:impl 're-com.box/box}
   [::container
    [::portion]]])

(def progress-bar-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def progress-bar-parts
  (when include-args-desc?
    (-> (map :name progress-bar-parts-desc) set)))

(def progress-bar-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :model     :required true                  :type "double | string | r/atom" :validate-fn number-or-string?           :description "current value of the slider. A number between 0 and 100"}
       {:name :width     :required false :default "100%" :type "string"                   :validate-fn string?                     :description "a CSS width"}
       {:name :striped?  :required false :default false  :type "boolean"                  :validate-fn boolean?                    :description "when true, the progress section is a set of animated stripes"}
       {:name :bar-class :required false                 :type "string"                   :validate-fn string?                     :description "CSS class name(s) for the actual progress bar itself, space separated"}
       args/class
       args/style
       args/attr
       (args/parts progress-bar-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args part-structure)))))

(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [model width striped? bar-class]
            :or   {width "100%"}
            :as   props}]
      (or
       (validate-args-macro progress-bar-args-desc props)
       (let [model   (deref-or-value model)
             part    (partial part/part part-structure props)
             re-com  {:state {:model     model
                              :width     width
                              :striped?  striped?
                              :bar-class bar-class}}]
         (part ::wrapper
           {:impl       box
            :post-props (-> props
                            (select-keys [:class :style :attr])
                            (debug/instrument props))
            :theme      theme
            :props      {:re-com re-com
                         :align  :start
                         :child  (part ::container
                                   {:theme      theme
                                    :post-props {:style {:width width}}
                                    :props      {:re-com re-com
                                                 :tag    :div
                                                 :children
                                                 [(part ::portion
                                                    {:theme      theme
                                                     :post-props (cond-> {:attr {:role "progressbar"}}
                                                                   bar-class (tu/class bar-class))
                                                     :props      {:re-com   re-com
                                                                  :tag      :div
                                                                  :striped? striped?
                                                                  :style    {:width      (str model "%")
                                                                             :transition "none"}
                                                                  :children [(str model "%")]}})]}})}}))))))
