(ns re-com.buttons
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [deref-or-value px]]
            [re-com.validate :refer [position? position-options-list button-size? button-sizes-list
                                     string-or-hiccup? css-style? html-attr? string-or-atom?] :refer-macros [validate-args-macro]]
            [re-com.popover  :refer [popover-tooltip]]
            [re-com.box      :refer [h-box v-box box gap line flex-child-style]]
            [reagent.core    :as    reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: button
;; ------------------------------------------------------------------------------------

(def button-args-desc
  [{:name :label            :required true                         :type "string | hiccup" :validate-fn string-or-hiccup? :description "label for the button"}
   {:name :class            :required false                        :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the button is clicked"}
   {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "what to show in the tooltip"}
   {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :disabled?        :required false :default false         :type "boolean | atom"                                 :description "if true, the user can't click the button"}
   {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?        :description "CSS styles"}
   {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn button
  "Returns the markup for a basic button"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [label on-click tooltip tooltip-position disabled? class style attr]
          :or   {class "btn-default"}
          :as   args}]
      {:pre [(validate-args-macro button-args-desc args "button")]}
      (let [disabled? (deref-or-value disabled?)
            the-button [:button
                        (merge
                          {:class    (str "rc-button btn " class)
                           :style    (merge
                                       (flex-child-style "none")
                                       style)
                           :disabled disabled?
                           :on-click (handler-fn
                                       (when (and on-click (not disabled?))
                                         (on-click)))}
                          (when tooltip
                            {:on-mouse-over (handler-fn (reset! showing? true))
                             :on-mouse-out  (handler-fn (reset! showing? false))})
                          attr)
                        label]]
        [box
         :class "display-inline-flex"
         :align :start
         :child (if tooltip
                  [popover-tooltip
                   :label    tooltip
                   :position (if tooltip-position tooltip-position :below-center)
                   :showing? showing?
                   :anchor   the-button]
                  the-button)]))))


;;--------------------------------------------------------------------------------------------------
;; Component: md-circle-icon-button
;;--------------------------------------------------------------------------------------------------

(def md-circle-icon-button-args-desc
  [{:name :md-icon-name     :required true  :default "md-add"      :type "string"          :validate-fn string?           :description [:span "the name of the icon." [:br] "For example, " [:code "\"md-add\""] " or " [:code "\"md-undo\""]] }
   {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the button is clicked"}
   {:name :size             :required false :default :regular      :type "keyword"         :validate-fn button-size?      :description [:span "one of " button-sizes-list]}
   {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "what to show in the tooltip"}
   {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :emphasise?       :required false :default false         :type "boolean"                                        :description "if true, use emphasised styling so the button really stands out"}
   {:name :disabled?        :required false :default false         :type "boolean"                                        :description "if true, the user can't click the button"}
   {:name :class            :required false                        :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn md-circle-icon-button
  "a circular button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [md-icon-name on-click size tooltip tooltip-position emphasise? disabled? class style attr]
          :or   {md-icon-name "md-add"}
          :as   args}]
      {:pre [(validate-args-macro md-circle-icon-button-args-desc args "md-circle-icon-button")]}
      (let [the-button [:div
                        (merge
                          {:class    (str
                                       "rc-md-circle-icon-button noselect "
                                       (case size
                                         :smaller "rc-circle-smaller "
                                         :larger "rc-circle-larger "
                                         " ")
                                       (when emphasise? "rc-circle-emphasis ")
                                       (when disabled? "rc-circle-disabled ")
                                       class)
                           :style    (merge
                                       {:cursor (when-not disabled? "pointer")}
                                       style)
                           :on-click (handler-fn
                                       (when (and on-click (not disabled?))
                                         (on-click)))}
                          (when tooltip
                            {:on-mouse-over (handler-fn (reset! showing? true))
                             :on-mouse-out  (handler-fn (reset! showing? false))})
                          attr)
                        [:i {:class md-icon-name}]]]
        (if tooltip
          [popover-tooltip
           :label    tooltip
           :position (if tooltip-position tooltip-position :below-center)
           :showing? showing?
           :anchor   the-button]
          the-button)))))


;;--------------------------------------------------------------------------------------------------
;; Component: md-icon-button
;;--------------------------------------------------------------------------------------------------

(def md-icon-button-args-desc
  [{:name :md-icon-name     :required true  :default "md-add"      :type "string"          :validate-fn string?           :description [:span "the name of the icon." [:br] "For example, " [:code "\"md-add\""] " or " [:code "\"md-undo\""]]}
   {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the button is clicked"}
   {:name :size             :required false :default :regular      :type "keyword"         :validate-fn button-size?      :description [:span "one of " button-sizes-list]}
   {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "what to show in the tooltip"}
   {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :emphasise?       :required false :default false         :type "boolean"                                        :description "if true, use emphasised styling so the button really stands out"}
   {:name :disabled?        :required false :default false         :type "boolean"                                        :description "if true, the user can't click the button"}
   {:name :class            :required false                        :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn md-icon-button
  "a square button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [md-icon-name on-click size tooltip tooltip-position emphasise? disabled? class style attr]
          :or   {md-icon-name "md-add"}
          :as   args}]
      {:pre [(validate-args-macro md-icon-button-args-desc args "md-icon-button")]}
      (let [the-button [:div
                        (merge
                          {:class    (str
                                       "rc-md-icon-button noselect "
                                       (case size
                                         :smaller "rc-icon-smaller "
                                         :larger "rc-icon-larger "
                                         " ")
                                       (when emphasise? "rc-icon-emphasis ")
                                       (when disabled? "rc-icon-disabled ")
                                       class)
                           :style    (merge
                                       {:cursor (when-not disabled? "pointer")}
                                       style)
                           :on-click (handler-fn
                                       (when (and on-click (not disabled?))
                                         (on-click)))
                           }
                          (when tooltip
                            {:on-mouse-over (handler-fn (reset! showing? true))
                             :on-mouse-out  (handler-fn (reset! showing? false))})
                          attr)
                        [:i {:class md-icon-name}]]]
        (if tooltip
          [popover-tooltip
           :label    tooltip
           :position (if tooltip-position tooltip-position :below-center)
           :showing? showing?
           :anchor   the-button]
          the-button)))))


;;--------------------------------------------------------------------------------------------------
;; Component: info-button
;;--------------------------------------------------------------------------------------------------

(def info-button-args-desc
  [{:name :info     :required true                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "what's shown in the popover"}
   {:name :position :required false :default :right-below :type "keyword"         :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :width    :required false :default "250px"      :type "string"          :validate-fn string?           :description "width in px"}
   {:name :class    :required false                       :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style    :required false                       :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr     :required false                       :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn info-button
  "A tiny light grey button, with an 'i' in it. Meant to be unobrusive.
  When pressed, displays a popup assumidly containing helpful information.
  Primarily designed to be nestled against the label of an input field, explaining the purpose of that field.
  Create a very small \"i\" icon via SVG"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [info position width class style attr] :as args}]
      {:pre [(validate-args-macro info-button-args-desc args "info-button")]}
      [popover-tooltip
       :label     info
       :status    :info
       :position  (if position position :right-below)
       :width     (if width width "250px")
       :showing?  showing?
       :on-cancel #(swap! showing? not)
       :anchor    [:div
                   (merge
                     {:class    (str "rc-info-button noselect " class)
                      :style    (merge {:cursor "pointer"} style)
                      :on-click (handler-fn (swap! showing? not))}
                     attr)
                   [:svg {:width "11" :height "11"}
                    [:circle {:cx "5.5" :cy "5.5" :r "5.5"}]
                    [:circle {:cx "5.5" :cy "2.5" :r "1.4" :fill "white"}]
                    [:line   {:x1 "5.5" :y1 "5.2" :x2 "5.5" :y2 "9.7" :stroke "white" :stroke-width "2.5"}]]]])))


;;--------------------------------------------------------------------------------------------------
;; Component: row-button
;;--------------------------------------------------------------------------------------------------

(def row-button-args-desc
  [{:name :md-icon-name     :required true  :default "md-add"      :type "string"          :validate-fn string?           :description [:span "the name of the icon." [:br] "For example, " [:code "\"md-add\""] " or " [:code "\"md-undo\""]]}
   {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the button is clicked"}
   {:name :mouse-over-row?  :required false :default false         :type "boolean"                                        :description "true if the mouse is hovering over the row"}
   {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "what to show in the tooltip"}
   {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :disabled?        :required false :default false         :type "boolean"                                        :description "if true, the user can't click the button"}
   {:name :class            :required false                        :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn row-button
  "a circular button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [md-icon-name on-click mouse-over-row? tooltip tooltip-position disabled? class style attr]
          :or   {md-icon-name "md-add"}
          :as   args}]
      {:pre [(validate-args-macro row-button-args-desc args "row-button")]}
      (let [the-button [:div
                        (merge
                          {:class    (str
                                       "rc-row-button noselect "
                                       (when mouse-over-row? "rc-row-mouse-over-row ")
                                       (when disabled? "rc-row-disabled ")
                                       class)
                           :style    style
                           :on-click (handler-fn
                                       (when (and on-click (not disabled?))
                                         (on-click)))}
                          (when tooltip
                            {:on-mouse-over (handler-fn (reset! showing? true))
                             :on-mouse-out  (handler-fn (reset! showing? false))}) ;; Need to return true to ALLOW default events to be performed
                          attr)
                        [:i {:class md-icon-name}]]]
        (if tooltip
          [popover-tooltip
           :label    tooltip
           :position (if tooltip-position tooltip-position :below-center)
           :showing? showing?
           :anchor   the-button]
          the-button)))))


;;--------------------------------------------------------------------------------------------------
;; Component: hyperlink
;;--------------------------------------------------------------------------------------------------

(def hyperlink-args-desc
  [{:name :label            :required true                         :type "string | hiccup | atom" :validate-fn string-or-hiccup? :description "label/hiccup for the button"}
   {:name :on-click         :required false                        :type "-> nil"                 :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the button is clicked"}
   {:name :tooltip          :required false                        :type "string | hiccup"        :validate-fn string-or-hiccup? :description "what to show in the tooltip"}
   {:name :tooltip-position :required false :default :below-center :type "keyword"                :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :disabled?        :required false :default false         :type "boolean | atom"                                        :description "if true, the user can't click the button"}
   {:name :class            :required false                        :type "string"                 :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style            :required false                        :type "CSS style map"          :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr             :required false                        :type "HTML attr map"          :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn hyperlink
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [label on-click tooltip tooltip-position disabled? class style attr] :as args}]
      {:pre [(validate-args-macro hyperlink-args-desc args "hyperlink")]}
      (let [label      (deref-or-value label)
            disabled?  (deref-or-value disabled?)
            the-button [box
                        :align :start
                        :child [:a
                                (merge
                                  {:class    (str "rc-hyperlink noselect " class)
                                   :style    (merge
                                               (flex-child-style "none")
                                               {:cursor (if disabled? "not-allowed" "pointer")
                                                :color  (when disabled? "grey")}
                                               style)
                                   :on-click (handler-fn
                                               (when (and on-click (not disabled?))
                                                 (on-click)))}
                                  (when tooltip
                                    {:on-mouse-over (handler-fn (reset! showing? true))
                                     :on-mouse-out  (handler-fn (reset! showing? false))})
                                  attr)
                                label]]]
        (if tooltip
          [popover-tooltip
           :label tooltip
           :position (if tooltip-position tooltip-position :below-center)
           :showing? showing?
           :anchor the-button]
          the-button)))))


;;--------------------------------------------------------------------------------------------------
;; Component: hyperlink-href
;;--------------------------------------------------------------------------------------------------

(def hyperlink-href-args-desc
  [{:name :label            :required true                         :type "string | hiccup | atom" :validate-fn string-or-hiccup? :description "label/hiccup for the button"}
   {:name :href             :required true                         :type "string | atom"          :validate-fn string-or-atom?   :description "if specified, the link target URL"}
   {:name :target           :required false :default "_self"       :type "string | atom"          :validate-fn string-or-atom?   :description "one of \"_self\" or \"_blank\""}
   {:name :tooltip          :required false                        :type "string | hiccup"        :validate-fn string-or-hiccup? :description "what to show in the tooltip"}
   {:name :tooltip-position :required false :default :below-center :type "keyword"                :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :class            :required false                        :type "string"                 :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style            :required false                        :type "CSS style map"          :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr             :required false                        :type "HTML attr map"          :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn hyperlink-href
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [label href target tooltip tooltip-position class style attr] :as args}]
      {:pre [(validate-args-macro hyperlink-href-args-desc args "hyperlink-href")]}
      (let [label      (deref-or-value label)
            href       (deref-or-value href)
            target     (deref-or-value target)
            the-button [:a
                        (merge {:class  (str "rc-hyperlink-href noselect " class)
                                :style  (merge (flex-child-style "none")
                                               style)
                                :href   href
                                :target target}
                               (when tooltip
                                 {:on-mouse-over (handler-fn (reset! showing? true))
                                  :on-mouse-out  (handler-fn (reset! showing? false))})
                               attr)
                        label]]

        (if tooltip
          [popover-tooltip
           :label tooltip
           :position (if tooltip-position tooltip-position :below-center)
           :showing? showing?
           :anchor the-button]
          the-button)))))


;; TODO: Eventually remove
;;----------------------------------------------------------------------
;; Round button with no dependencies - for use in re-frame demo
;;----------------------------------------------------------------------

#_(defn round-button
  "a circular button containing a material design icon"
  []
  (let [mouse-over? (reagent/atom false)]
    (fn
      [& {:keys [md-icon-name on-click disabled? style attr]
          :or   {md-icon-name "md-add"}}]
      [:div
       (merge
         {:style         (merge
                           {:cursor              (when-not disabled? "pointer")
                            :font-size           "24px"
                            :width               "40px"
                            :height              "40px"
                            :line-height         "44px"
                            :text-align          "center"
                            :-webkit-user-select "none"}
                           (if disabled?
                             {:color             "lightgrey"}
                             {:border            (str "1px solid " (if @mouse-over? "#428bca" "lightgrey"))
                              :color             (when @mouse-over? "#428bca")
                              :border-radius     "50%"})
                           style)
          :on-mouse-over #(do (reset! mouse-over? true) nil)
          :on-mouse-out  #(do (reset! mouse-over? false) nil)
          :on-click      #(when (and on-click (not disabled?))
                           (on-click)
                           nil)}
         attr)
       [:i {:class md-icon-name}]])))
