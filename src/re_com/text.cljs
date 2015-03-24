(ns re-com.text
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.box      :refer [v-box box line]]
            [re-com.validate :as r :refer [extract-arg-data title-levels-list title-level-type? #_css-style?
                                     html-attr? string-or-hiccup?] :refer-macros [validate-args-macro]]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

  (def label-args-desc
    [{:name :label    :required true  :type "anything"                              :description "text or hiccup or whatever to display"}
     {:name :on-click :required false :type "() -> nil"     :validate-fn fn?        :description "called when the label is clicked"}
     {:name :width    :required false :type "string"        :validate-fn string?    :description "a CSS width"}
     {:name :class    :required false :type "string"        :validate-fn string?    :description "CSS class names, space separated"}
     {:name :style    :required false :type "css style map" :validate-fn r/css-style? :description "additional CSS styles"}
     {:name :attr     :required false :type "html attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

;(def label-args (extract-arg-data label-args-desc))

(defn label
  "Returns markup for a basic label"
  [& {:keys [label on-click width class style attr]
      :as   args}]
  {:pre [(validate-args-macro label-args-desc args "label")]}
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

(def title-args-desc
  [{:name :label      :required true                   :type "anything"                                       :description "title or hiccup or anything to display"}
   {:name :level      :required false                  :type "keyword"         :validate-fn title-level-type? :description [:span "one of " title-levels-list ". If not provided then style the title using " [:code ":class"] " or " [:code ":style"]] }
   {:name :underline? :required false  :default false  :type "boolean"                                        :description "if true, the title is underlined"}
   {:name :class      :required false                  :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style      :required false                  :type "css style map"   :validate-fn r/css-style?        :description "CSS styles to add or override"}
   {:name :attr       :required false                  :type "html attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

;(def title-args (extract-arg-data title-args-desc))

(defn title
  "A title with four preset styles"
  [& {:keys [label level underline? class style attr] :as args}]
  {:pre [(validate-args-macro title-args-desc args "title")]}
  (let [preset-class (if (nil? level) "" (name level))]
    [v-box
     :children [[:span (merge {:class (str "rc-title " preset-class " " class)
                               :style (merge {:display "flex" :flex "none"}
                                             style)}
                              attr)
                 label]
                (when underline? [line :size "1px"])]]))
