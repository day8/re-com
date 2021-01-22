(ns re-com.throbber
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
;;  Component: throbber
;; ------------------------------------------------------------------------------------

(def throbber-args-desc
  [{:name :size  :required false :default :regular :type "keyword"       :validate-fn throbber-size? :description [:span "one of " throbber-sizes-list]}
   {:name :color :required false :default "#999"   :type "string"        :validate-fn string?        :description "CSS color"}
   {:name :class :required false                   :type "string"        :validate-fn string?        :description "CSS class names, space separated (applies to the throbber, not the wrapping div)"}
   {:name :style :required false                   :type "CSS style map" :validate-fn css-style?     :description "CSS styles to add or override (applies to the throbber, not the wrapping div)"}
   {:name :attr  :required false                   :type "HTML attr map" :validate-fn html-attr?     :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the throbber, not the wrapping div)"]}
   {:name :parts :required false                   :type "map"           :validate-fn (parts? #{:wrapper :segment}) :description "See Parts section below."}])

(defn throbber
  "Render an animated throbber using CSS"
  [& {:keys [size color class style attr parts] :as args}]
  {:pre [(validate-args-macro throbber-args-desc args "throbber")]}
  (let [seg (fn []
              [:li
               (merge
                 {:class (str "rc-throbber-segment " (get-in parts [:segment :class]))
                  :style (merge
                           (when color {:background-color color})
                           (get-in parts [:segment :style]))}
                 (get-in parts [:segment :attr]))])]
    [box
     :class (str "rc-throbber-wrapper " (get-in parts [:wrapper :class]))
     :style (get-in parts [:wrapper :style] {})
     :attr  (get-in parts [:wrapper :attr] {})
     :align :start
     :child [:ul
             (merge {:class (str "loader rc-throbber "
                                 (case size :regular ""
                                            :smaller "smaller "
                                            :small "small "
                                            :large "large "
                                            "")
                                 class)
                     :style style}
                    attr)
             [seg] [seg] [seg] [seg]
             [seg] [seg] [seg] [seg]]])) ;; Each :li element in [seg] represents one of the eight circles in the throbber