(ns re-com.throbber
  (:require-macros
   [re-com.core     :refer [at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.throbber.theme
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.box      :refer [box]]
   [re-com.validate :refer [throbber-size? throbber-sizes-list css-style? css-class?]]))

;; ------------------------------------------------------------------------------------
;;  Component: throbber
;; ------------------------------------------------------------------------------------

(def part-structure
  [::wrapper {:impl 're-com.box/box}
   [::throbber {:tag :ul}
    [::segment {:tag :li :multiple? true}]]])

(def throbber-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def throbber-parts
  (when include-args-desc?
    (-> (map :name throbber-parts-desc) set)))

(def throbber-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :size  :required false :default :regular :type "keyword" :validate-fn throbber-size? :description [:span "one of " throbber-sizes-list]}
       {:name :color :required false :default "#999"   :type "string"  :validate-fn string?        :description "CSS color"}
       args/class
       args/style
       args/attr
       (args/parts throbber-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args part-structure)))))

(defn throbber
  "Render an animated throbber using CSS"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [size color]
            :or   {size :regular color "#999"}
            :as   props}]
      (or
       (validate-args-macro throbber-args-desc props)
       (let [part   (partial part/part part-structure props)
             re-com {:state {:size  size
                             :color color}}]
         (part ::wrapper
           {:impl       box
            :theme      theme
            :post-props {:attr (debug/->attr props)}
            :props      {:re-com re-com
                         :child  (part ::throbber
                                   {:theme      theme
                                    :post-props (select-keys props [:class :style :attr])
                                    :props      {:re-com re-com
                                                 :tag    :ul
                                                 :children
                                                 (for [_ (range 8)]
                                                   (part ::segment
                                                     {:theme theme
                                                      :props {:re-com re-com
                                                              :tag    :li}}))}})}}))))))
