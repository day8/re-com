(ns re-demo.welcome
  (:require [reagent.core :as reagent]
            [re-com.core  :refer [title hyperlink-href label]]
            [re-com.box   :refer [h-box v-box box gap line]]
            [re-demo.utils :refer [panel-title component-title]]))




(def datepicker-args-desc
  [{:name :model           :required true                   :description "an instance of goog.date.UtcDateTime or an atom containing one. Represents displayed month and actual selected day. Must be one of <code>:enabled-days</code>."}
   {:name :on-change       :required true                   :description "a callback which will be passed new selected goog.date.UtcDateTime."}
   {:name :disabled?       :required false :default false   :description "a boolean or a reagent/atom containing a boolean. If true, navigation is allowed but selection is disabled."}
   {:name :enabled-days    :required false                  :description "a subset of #{:Su :Mo :Tu :We :Th :Fr :Sa}. Dates falling on these days will be user-selectable, others not so. If nil or empty, all days are enabled."}
   {:name :show-weeks?     :required false :default false   :description "a boolean. If true, the first column shows week numbers."}
   {:name :show-today?     :required false :default false   :description "a boolean. When true, today's date is highlighted."}
   {:name :minimum         :required false                  :description "an instance of goog.date.UtcDateTime. Selection and navigation are blocked before this date."}
   {:name :maximum         :required false                  :description "an instance of goog.date.UtcDateTime. Selection and navigation are blocked after this date."}
   {:name :hide-border?    :required false :default false   :description "a boolean. When true the border is not displayed."}])


(defn arg-row
  [arg underline?]
  [h-box
   :style   {:font-size "small"}
   :gap      "20px"
   :children [[label :label (str (:name arg)) :style {:width "100px"}]
              [v-box
               :children [ [label :label  "type"]
                           [label :label  "required / ooptional / default"]
                           [:p  (:description arg)]
                           (when-not underline? [line])]]]])


(defn args-table
  [args]
  [v-box
   :width     "500px"
   :children (cons
               [label :label "Properties"]
               (map arg-row args))]
  )


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
