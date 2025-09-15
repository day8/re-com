(ns re-com.progress-bar
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.progress-bar.theme
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
    (concat
     [{:name :model     :required true                  :type "double | string | r/atom" :validate-fn number-or-string?           :description "current value of the slider. A number between 0 and 100"}
      {:name :width     :required false :default "100%" :type "string"                   :validate-fn string?                     :description "a CSS width"}
      {:name :striped?  :required false :default false  :type "boolean"                                                           :description "when true, the progress section is a set of animated stripes"}
      {:name :bar-class :required false                 :type "string"                   :validate-fn string?                     :description "CSS class name(s) for the actual progress bar itself, space separated"}
      {:name :pre-theme :required false                 :type "map -> map"               :validate-fn fn?                         :description "Pre-theme function"}
      {:name :theme     :required false                 :type "map -> map"               :validate-fn fn?                         :description "Theme function"}
      {:name :class     :required false                 :type "string"                   :validate-fn css-class?                  :description "CSS class names, space separated (applies to wrapper)"}
      {:name :style     :required false                 :type "CSS style map"            :validate-fn css-style?                  :description "CSS styles to add or override (applies to wrapper)"}
      {:name :attr      :required false                 :type "HTML attr map"            :validate-fn html-attr?                  :description "HTML attributes (applies to wrapper)"}
      {:name :parts     :required false                 :type "map"                      :validate-fn (parts? progress-bar-parts) :description "Map of part names to styling"}
      {:name :src       :required false                 :type "map"                      :validate-fn map?                        :description "Source code coordinates for debugging"}
      {:name :debug-as  :required false                 :type "map"                      :validate-fn map?                        :description "Debug output masquerading"}]
     (part/describe-args part-structure))))

(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [model width striped? bar-class]
            :or   {width "100%"}
            :as   props}]
      (or
       (validate-args-macro progress-bar-args-desc props)
       (let [model (deref-or-value model)
             part  (partial part/part part-structure props)]
         (part ::wrapper
           {:impl       box
            :post-props (-> props
                            (select-keys [:class :style :attr])
                            (debug/instrument props))
            :theme      theme
            :props
            {:align :start
             :child (part ::container
                      {:theme      theme
                       :post-props {:style {:width width}}
                       :props
                       {:tag :div
                        :children
                        [(part ::portion
                           {:theme      theme
                            :post-props (cond-> {:attr {:role "progressbar"}}
                                          bar-class (tu/class bar-class))
                            :props
                            {:tag      :div
                             :striped? striped?
                             :style    {:width      (str model "%")
                                        :transition "none"}
                             :children [(str model "%")]}})]}})}}))))))
