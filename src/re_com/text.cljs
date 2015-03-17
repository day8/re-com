(ns re-com.text
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.box      :refer [v-box box line]]
            [re-com.validate :refer [extract-arg-data validate-args css-style? html-attr?]]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

(def label-args-desc
  [{:name :label    :required true  :type "anything"                              :description "text to display. Can be anything as it will be converted to a string"}
   {:name :on-click :required false :type "() -> nil"     :validate-fn fn?        :description "function to call when label is clicked"}
   {:name :width    :required false :type "string"        :validate-fn string?    :description "a CSS width"}
   {:name :class    :required false :type "string"        :validate-fn string?    :description "CSS class names, space separated"}
   {:name :style    :required false :type "css style map" :validate-fn css-style? :description "additional CSS styles"}
   {:name :attr     :required false :type "html attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(def label-args (extract-arg-data label-args-desc))

(defn label
  "Returns markup for a basic label"
  [& {:keys [label on-click width class style attr]
      :as   args}]
  {:pre [(validate-args label-args args "label")]}
  [box
   :width width
   :align :start
   :style {:display "inline-flex"}
   :child [:span
           (merge
             {:class (str "rc-label " class)
              :style (merge {:flex "none"} style)}
             (when on-click
               {:on-click (handler-fn (on-click))})
             attr)
           (str label)]])


;; ------------------------------------------------------------------------------------
;;  Component: title
;; ------------------------------------------------------------------------------------

;; TODO: Add :bs-type and :md-type keyword validation if we go with this

(def title-args-desc
  [{:name :label      :required true                   :type "anything"                              :description "text to display. Can be anything as it will be converted to a string"}
   {:name :bs-style   :required false  :default :span  :type "keyword"       :validate-fn keyword?   :description "Bootstrap styling. One of :h1 to :h6. If none specified, :span will be used"}
   {:name :md-style   :required false  :default :title :type "keyword"       :validate-fn keyword?   :description "Material Design styling. One of :display4, :display3, :display2, :display1, :headline, :title etc. If not specified, then fall back to :bs-type"}
   {:name :underline? :required false  :default false  :type "boolean"                               :description "determines whether an underline is placed under the title"}
   {:name :class      :required false                  :type "string"        :validate-fn string?    :description "CSS class names, space separated"}
   {:name :style      :required false                  :type "css style map" :validate-fn css-style? :description "CSS styles to add or override"}
   {:name :attr       :required false                  :type "html attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(def title-args (extract-arg-data title-args-desc))

(defn title
  "An underlined, left justified, Title. By default :h3"
  [& {:keys [label bs-style md-style underline? class style attr] :as args}]
  {:pre [(validate-args title-args args "title")]}
  (let [md-class (if (nil? md-style)
                   (if (nil? bs-style) "title" "")
                   (name md-style))
        html-tag (if (nil? bs-style) :span bs-style)]
    [v-box
     :children [[html-tag (merge {:class (str "rc-title " md-class " " class)
                                  :style (merge {:display "flex" :flex "none"}
                                                style)}
                                 attr)
                 label]
                (when underline? [line :size "1px"])]]))
