(ns re-demo.welcome
  (:require [reagent.core :as reagent]
            [re-com.core  :refer [title hyperlink-href label]]
            [re-com.box   :refer [h-box v-box box gap line]]
            [re-demo.utils :refer [panel-title component-title]]))




(def datepicker-args-desc
  [{:name :model           :type  "goog.date.UtcDateTime"             :required true                   :description "an instance of goog.date.UtcDateTime or an atom containing one. Represents displayed month and actual selected day. Must be one of <code>:enabled-days</code>."}
   {:name :on-change       :type  "(fucntion goog.date.UtcDateTime)"  :required true                   :description "a callback which will be passed new selected goog.date.UtcDateTime."}
   {:name :disabled?       :type  "boolean"                           :required false :default false   :description "a boolean or a reagent/atom containing a boolean. If true, navigation is allowed but selection is disabled."}
   {:name :enabled-days    :type  "subset of #{:Su :Mo :Tu :We :Th :Fr :Sa}" :required false                  :description "only dates falling on these days will be user-selectable, others not so. If nil or empty, all days are enabled."}
   {:name :show-weeks?     :type  "boolean"                           :required false :default false   :description "a boolean. If true, the first column shows week numbers."}
   {:name :show-today?     :type  "boolean"                           :required false :default false   :description "a boolean. When true, today's date is highlighted."}
   {:name :minimum         :type  "goog.date.UtcDateTime"             :required false                  :description "an instance of goog.date.UtcDateTime. Selection and navigation are blocked before this date."}
   {:name :maximum         :type  "goog.date.UtcDateTime"  :required false                  :description "an instance of goog.date.UtcDateTime. Selection and navigation are blocked after this date."}
   {:name :hide-border?    :type  "boolean"                :required false :default false   :description "a boolean. When true the border is not displayed."}])


(defn arg-row
  [name-ems arg underline?]           ;; TODO: label-width overriddable
  (let [required  (:required arg)
        default   (:default arg)
        arg-type  (:type arg)
        needed-v (if (not required)
                   (if (nil? default)
                     [[label :label "optional" :class "small-caps"]]
                     [[label :label "default:" :class "small-caps"] [label :label (str default)]])
                   [[label :label "required" :class "small-caps"]])]
    [h-box
     :style   {:font-size "small"}
     :gap      "20px"
     :children [[label
                 :label (str (:name arg))
                 :style {:width name-ems}]
                [v-box
                 :width "300px"
                 :children [[h-box
                             :gap   "3px"
                             :children (concat [[label :label "type:" :class "small-caps"]
                                                [label :label arg-type]
                                                [gap :size "10px"]]
                                               needed-v)]
                             [:p  (:description arg)]
                            (when-not underline? [line])]]]]))


(defn args-table
  [args]
  (let [longest-name  (->> args
                           (map (comp count str :name))
                           (apply max))
        name-ems   (str (/ longest-name 2) "em")]
    (fn
      []
      [v-box
       ;; :width     "500px"
       :gap       "3px"
       :children (cons
                   [title :h :h4 :label "Properties"]
                   (map (partial arg-row name-ems)  args))])))


(defn panel
  []
  [v-box
   :width "600px"
   :children [[panel-title "Welcome"]
              [gap :size "15px"]
              [args-table datepicker-args-desc]
              [:p
               "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink-href
                :label  "Reagent"
                :href   "https://github.com/holmsand/reagent"
                :target "_blank"]
               "."]

              [:p "It contains some of layout and widgetry needed to build a desktop-class app."]]])
