(ns re-com.text
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.box      :refer [v-box box line flex-child-style]]
            [re-com.util     :refer [deep-merge]]
            [re-com.validate :refer [title-levels-list title-level-type? css-style? html-attr? string-or-hiccup?] :refer-macros [validate-args-macro]]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

  (def label-args-desc
    [{:name :label    :required true  :type "anything"                              :description "text or hiccup or whatever to display"}
     {:name :on-click :required false :type "-> nil"        :validate-fn fn?        :description "a function which takes no params and returns nothing. Called when the label is clicked"}
     {:name :width    :required false :type "string"        :validate-fn string?    :description "a CSS width"}
     {:name :class    :required false :type "string"        :validate-fn string?    :description "CSS class names, space separated"}
     {:name :style    :required false :type "CSS style map" :validate-fn css-style? :description "additional CSS styles"}
     {:name :attr     :required false :type "HTML attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn label
  "Returns markup for a basic label"
  [& {:keys [label on-click width class style attr]
      :as   args}]
  {:pre [(validate-args-macro label-args-desc args "label")]}
  [box
   :width width
   :align :start
   :class "display-inline-flex"
   :child [:span
           (merge
             {:class (str "rc-label " class)
              :style (merge (flex-child-style "none")
                            style)}
             (when on-click
               {:on-click (handler-fn (on-click))})
             attr)
           label]])


;; ------------------------------------------------------------------------------------
;;  Component: title
;; ------------------------------------------------------------------------------------

(def title-args-desc
  [{:name :label         :required true                    :type "anything"                                       :description "title or hiccup or anything to display"}
   {:name :level         :required false                   :type "keyword"         :validate-fn title-level-type? :description [:span "one of " title-levels-list ". If not provided then style the title using " [:code ":class"] " or " [:code ":style"]] }
   {:name :underline?    :required false  :default false   :type "boolean"                                        :description "if true, the title is underlined"}
   {:name :margin-top    :required false  :default "0.4em" :type "string"          :validate-fn string?           :description "CSS size for space above the title"}
   {:name :margin-bottom :required false  :default "0.1em" :type "string"          :validate-fn string?           :description "CSS size for space below the title"}
   {:name :class         :required false                   :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style         :required false                   :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr          :required false                   :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn title
  "A title with four preset levels"
  [& {:keys [label level underline? margin-top margin-bottom class style attr]
      :or   {margin-top "0.6em" margin-bottom "0.3em"}
      :as   args}]
  {:pre [(validate-args-macro title-args-desc args "title")]}
  (let [preset-class (if (nil? level) "" (name level))]
    [v-box
     :class    preset-class
     :children [[:span (merge {:class (str "rc-title display-flex " preset-class " " class)
                               :style (merge (flex-child-style "none")
                                             {:margin-top margin-top}
                                             {:line-height 1}             ;; so that the margins are correct
                                             (when-not underline? {:margin-bottom margin-bottom})
                                             style)}
                              attr)
                 label]
                (when underline? [line
                                  :size "1px"
                                  :style {:margin-bottom margin-bottom}])]]))


;; ------------------------------------------------------------------------------------
;;  Component: p
;; ------------------------------------------------------------------------------------

(defn p
  "acts like [:p ]

   Creates a paragraph of body text, expected to have a font-szie of 14px or 15px,
   which should have limited width.

   Why limited text width?  See http://baymard.com/blog/line-length-readability

   The actual font-size is inherited.

   At 14px, 450px will yield between 69 and 73 chars.
   At 15px, 450px will yield about 66 to 70 chars.
   So we're at the upper end of the prefered 50 to 75 char range.

   If the first child is a map, it is interpreted as a map of styles / attributes."
  [& children]
  (let [child1       (first children)    ;; it might be a map of attributes, including styles
        [m children] (if (map? child1)
                       [child1  (rest children)]
                       [{}      children])
        m             (deep-merge {:style {:flex      "none"
                                           :width     "450px"
                                           :min-width "450px"}}
                                  m)]
    [:span m (into [:p] children)]))    ;; the wrapping span allows children to contain [:ul] etc
