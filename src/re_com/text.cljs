(ns re-com.text
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.box      :refer [v-box box line]]
            [re-com.validate :refer [extract-arg-data validate-args css-style? html-attr? string-or-hiccup?]]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

(def label-args-desc
  [{:name :label    :required true  :type "anything"                              :description "text or hiccup or whatever to display"}
   {:name :on-click :required false :type "() -> nil"     :validate-fn fn?        :description "called when the label is clicked"}
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

(def title-args-desc
  [{:name :label      :required true                   :type "anything"                                       :description "title or hiccup or anything to display"}
   {:name :level      :required false                  :type "keyword"         :validate-fn keyword?          :description "one of :level1 to :level4. If not provided then style the title using :claas or :style"} ;; TODO: [codify]
   {:name :underline? :required false  :default false  :type "boolean"                                        :description "if true, the title is underlined"}
   {:name :class      :required false                  :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style      :required false                  :type "css style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr       :required false                  :type "html attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(def title-args (extract-arg-data title-args-desc))

(defn title
  "A title with four preset styles"
  [& {:keys [label level underline? class style attr] :as args}]
  {:pre [(validate-args title-args args "title")]}
  (let [preset-class (if (nil? level) "" (name level))]
    [v-box
     :children [[:span (merge {:class (str "rc-title " preset-class " " class)
                               :style (merge {:display "flex" :flex "none"}
                                             style)}
                              attr)
                 label]
                (when underline? [line :size "1px"])]]))
