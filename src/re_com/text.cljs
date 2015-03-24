(ns re-com.text
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.box      :refer [v-box box line]]
            [re-com.validate :refer [extract-arg-data title-levels-list title-level-type? validate-args css-style? html-attr? string-or-hiccup?] :refer-macros [validate-args-macro]]))


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

;(when ^boolean js/goog.DEBUG
;  (def label-args (extract-arg-data label-args-desc)))

;(def testDEBUG false)
;(def blah (with-meta testDEBUG {:tag boolean}))
;(println "testDEBUG: '" testDEBUG "'  blah: '" blah "'")
;(println "testDEBUG: '" (meta testDEBUG) "'  blah: '" (meta blah) "'")

(defn ^boolean debug?
  []
  js/goog.DEBUG)


(defn label
  "Returns markup for a basic label"
  [& {:keys [label on-click width class style attr]
      :as   args}]
  ;{:pre [(if-not ^boolean js/goog.DEBUG true (validate-args (extract-arg-data label-args-desc) args "label"))]}

  ;{:pre [(if-not (with-meta js/goog.DEBUG {:tag boolean}) true (validate-args (extract-arg-data label-args-desc) args "label"))]} ;; No protocol method IWithMeta.-with-meta defined for type boolean: false
  ;{:pre [(if-not (with-meta js/goog.DEBUG {:tag 'boolean}) true (validate-args (extract-arg-data label-args-desc) args "label"))]} ;; No protocol method IWithMeta.-with-meta defined for type boolean: false

  {:pre [(validate-args-macro label-args-desc args "label")]}
  ;{:pre [(if-not (with-meta 'js/goog.DEBUG {:boolean true}) true (validate-args (extract-arg-data label-args-desc) args "label"))]} ;; Works but string still there

  ;{:pre [(if-not (with-meta testDEBUG {:tag boolean}) true (validate-args (extract-arg-data label-args-desc) args "label"))]} ;; No protocol method IWithMeta.-with-meta defined for type boolean: false
  ;{:pre [(if-not (with-meta testDEBUG {:tag 'boolean}) true (validate-args (extract-arg-data label-args-desc) args "label"))]} ;; No protocol method IWithMeta.-with-meta defined for type boolean: false

  ;(println "META: '" (meta testDEBUG) "'")

  (println (str "goog.DEBUG: '"                   js/goog.DEBUG "'"))
  (println (str "^boolean goog.DEBUG: '"          ^boolean js/goog.DEBUG "'"))
  (println (str "with-meta 'goog.DEBUG: '"        (with-meta 'js/goog.DEBUG {:tag 'boolean}) "'")) ;; without quote on boolean: {:tag #<function Xd(a){return r(a)?!0:!1}>}
  (println (str "META goog.DEBUG: '"              (meta js/goog.DEBUG) "'"))
  (println (str "META ^boolean goog.DEBUG: '"     (meta ^boolean js/goog.DEBUG) "'"))
  (println (str "META with-meta 'goog.DEBUG: '"   (meta (with-meta 'js/goog.DEBUG {:tag 'boolean})) "'"))

  (println (str "'debug?: '"                      (debug?) "'"))
  (println (str "META 'debug?: '"                 (meta (debug?)) "'"))

  ;{:pre [(validate-args-macro label-args-desc args "label")]}
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

(when ^boolean js/goog.DEBUG
  (def title-args-desc
    [{:name :label      :required true                   :type "anything"                                       :description "title or hiccup or anything to display"}
     {:name :level      :required false                  :type "keyword"         :validate-fn title-level-type? :description [:span "one of " title-levels-list ". If not provided then style the title using " [:code ":class"] " or " [:code ":style"]] }
     {:name :underline? :required false  :default false  :type "boolean"                                        :description "if true, the title is underlined"}
     {:name :class      :required false                  :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
     {:name :style      :required false                  :type "css style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
     {:name :attr       :required false                  :type "html attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}]))

(when ^boolean js/goog.DEBUG
  (def title-args (extract-arg-data title-args-desc)))

(defn title
  "A title with four preset styles"
  [& {:keys [label level underline? class style attr] :as args}]
  ;{:pre [(if-not ^boolean js/goog.DEBUG true (validate-args title-args args "title"))]}
  (let [preset-class (if (nil? level) "" (name level))]
    [v-box
     :children [[:span (merge {:class (str "rc-title " preset-class " " class)
                               :style (merge {:display "flex" :flex "none"}
                                             style)}
                              attr)
                 label]
                (when underline? [line :size "1px"])]]))
