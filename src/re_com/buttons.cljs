(ns re-com.buttons
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util    :refer [deref-or-value validate-arguments px]]
            [re-com.popover :refer [popover-tooltip]]
            [re-com.box     :refer [h-box v-box box gap line]]
            [reagent.core   :as    reagent]))


;; ------------------------------------------------------------------------------------
;;  Component: button
;; ------------------------------------------------------------------------------------

(def button-args-desc
  [{:name :label            :required true                           :type "string | hiccup" :description "Label for the button."}
   {:name :on-click         :required false                          :type "() -> nil"       :description "a callback function to be invoked when button is clicked."}
   {:name :tooltip          :required false                          :type "string"          :description "show a popover-tooltip using this text."}
   {:name :tooltip-position :required false :default :below-center   :type "keyword"         :description "position of the popover-tooltip. e.g. :right-below."}
   {:name :disabled?        :required false :default false           :type "boolean"         :description "Set to true to disable the button."}
   {:name :class            :required false                          :type "string"          :description "Class string. e.g. \"btn-info\" (see: http://getbootstrap.com/css/#buttons)."}
   {:name :style            :required false                          :type "map"             :description "CSS styles to add or override."}
   {:name :attr             :required false                          :type "map"             :description "html attributes to add or override (:class/:style not allowed)."}])

(def button-args
  (set (map :name button-args-desc)))

(defn button
  "Returns the markup for a basic button."
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [label on-click tooltip tooltip-position disabled? class style attr]
          :or   {class "btn-default"}
          :as   args}]
      {:pre [(validate-arguments button-args (keys args))]}
      (let [disabled? (deref-or-value disabled?)
            the-button [:button
                        (merge
                          {:class    (str "rc-button btn " class)
                           :style    (merge
                                       {:flex "none"}
                                       style)
                           :disabled disabled?
                           :on-click (handler-fn
                                       (when (and on-click (not disabled?))
                                         (on-click)))
                           }
                          (when tooltip
                            {:on-mouse-over (handler-fn (reset! showing? true))
                             :on-mouse-out  (handler-fn (reset! showing? false))})
                          attr)
                        label]]
        [box
         :style {:display "inline-flex"}
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
  [{:name :md-icon-name     :required true   :default "md-add"       :type "string"     :description "the name of the icon. See http://zavoloklom.github.io/material-design-iconic-font/icons.html"}
   {:name :on-click         :required false                          :type "() -> nil"  :description "a callback function to be invoked when button is clicked."}
   {:name :size             :required false  :default "nil"          :type "keyword"    :description "set size of button (nil = regular, or :smaller or :larger."}
   {:name :tooltip          :required false                          :type "string"     :description "show a popover-tooltip using this text."}
   {:name :tooltip-position :required false :default ":below-center" :type "keyword"    :description "position of the popover-tooltip. e.g. :right-below."}
   {:name :emphasise?       :required false :default false           :type "boolean"    :description "if true, use emphasised styling so the button really stands out."}
   {:name :disabled?        :required false :default false           :type "boolean"    :description "if true, the user can't click the button."}
   {:name :class            :required false                          :type "string"     :description "additional CSS classes required."}
   {:name :style            :required false                          :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                          :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}])

(def md-circle-icon-button-args
  (set (map :name md-circle-icon-button-args-desc)))

(defn md-circle-icon-button
  "a circular button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [md-icon-name on-click size tooltip tooltip-position emphasise? disabled? class style attr]
          :or   {md-icon-name "md-add"}
          :as   args}]
      {:pre [(validate-arguments md-circle-icon-button-args (keys args))]}
      (let [the-button [:div
                        (merge
                          {:class    (str
                                       "rc-md-circle-icon-button "
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
  [{:name :md-icon-name     :required true   :default "md-add"       :type "string"     :description "the name of the icon. See http://zavoloklom.github.io/material-design-iconic-font/icons.html"}
   {:name :on-click         :required false                          :type "() -> nil"  :description "a callback function to be invoked when button is clicked."}
   {:name :size             :required false  :default "nil"          :type "keyword"    :description "set size of button (nil = regular, or :smaller or :larger."}
   {:name :tooltip          :required false                          :type "string"     :description "show a popover-tooltip using this text."}
   {:name :tooltip-position :required false :default ":below-center" :type "keyword"    :description "position of the popover-tooltip. e.g. :right-below."}
   {:name :emphasise?       :required false :default false           :type "boolean"    :description "if true, use emphasised styling so the button really stands out."}
   {:name :disabled?        :required false :default false           :type "boolean"    :description "if true, the user can't click the button."}
   {:name :class            :required false                          :type "string"     :description "additional CSS classes required."}
   {:name :style            :required false                          :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                          :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}])

(def md-icon-button-args
  (set (map :name md-icon-button-args-desc)))

(defn md-icon-button
  "a square button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [md-icon-name on-click size tooltip tooltip-position emphasise? disabled? class style attr]
          :or   {md-icon-name "md-add"}
          :as   args}]
      {:pre [(validate-arguments md-icon-button-args (keys args))]}
      (let [the-button [:div
                        (merge
                          {:class    (str
                                       "rc-md-icon-button "
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
  [{:name :info             :required false                          :type "hiccup"     :description "show a popover-tooltip using this markup."}
   {:name :position         :required false :default ":right-below"  :type "keyword"    :description "position of the popover-tooltip. e.g. :right-below."}
   {:name :width            :required false :default "250px"         :type "string"     :description "width in px"}
   {:name :class            :required false                          :type "string"     :description "additional CSS classes required."}
   {:name :style            :required false                          :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                          :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}])

(def info-button-args
  (set (map :name info-button-args-desc)))

(defn info-button
  "A tiny light grey button, with an 'i' in it. Meant to be unobrusive.
  When pressed, displays a popup assumidly contining helpful information.
   Primarily designed to be nestled against the label of an input field, explaining the purpose of the field."
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [info position width class style attr] :as args}]
      {:pre [(validate-arguments info-button-args (keys args))]}
      [popover-tooltip
       :label     info
       :status    :info
       :position  (if position position :right-below)
       :width     (if width width "250px")
       :showing?  showing?
       :on-cancel #(swap! showing? not)
       :anchor    [:div
                   (merge
                     {:class    (str "rc-info-button " class)
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
  [{:name :md-icon-name     :required true  :default "md-add"        :type "string"     :description "the name of the icon. See http://zavoloklom.github.io/material-design-iconic-font/icons.html"}
   {:name :on-click         :required false                          :type "() -> nil"  :description "the fucntion to call when the button is clicked."}
   {:name :mouse-over-row?  :required false :default false           :type "boolean"    :description "true if the mouse is hovering over the row this button is in."}
   {:name :tooltip          :required false                          :type "string"     :description "show a popover-tooltip using this text."}
   {:name :tooltip-position :required false :default ":below-center" :type "keyword"    :description "position of the popover-tooltip. e.g. :right-below."}
   {:name :disabled?        :required false :default false           :type "boolean"    :description "if true, the user can't click the button."}
   {:name :class            :required false                          :type "string"     :description "additional CSS classes required."}
   {:name :style            :required false                          :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                          :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}])

(def row-button-args
  (set (map :name row-button-args-desc)))

(defn row-button
  "a circular button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [md-icon-name on-click mouse-over-row? tooltip tooltip-position disabled? class style attr]
          :or   {md-icon-name "md-add"}
          :as   args}]
      {:pre [(validate-arguments row-button-args (keys args))]}
      (let [the-button [:div
                        (merge
                          {:class    (str
                                       "rc-row-button "
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
  [{:name :label            :required false                          :type "string"     :description "Label for the button (can be artitrary markup)."}
   {:name :on-click         :required false                          :type "string"     :description "Callback when the hyperlink is clicked."}
   {:name :tooltip          :required false                          :type "string"     :description "show a popover-tooltip using this text."}
   {:name :tooltip-position :required false :default ":below-center" :type "keyword"    :description "position of the popover-tooltip. e.g. :right-below."}
   {:name :disabled?        :required false :default false           :type "string"     :description "Set to true to disable the hyperlink."}
   {:name :class            :required false                          :type "string"     :description "additional CSS classes required."}
   {:name :style            :required false                          :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                          :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}])

(def hyperlink-args
  (set (map :name hyperlink-args-desc)))

(defn hyperlink
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel."
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [label on-click tooltip tooltip-position disabled? class style attr] :as args}]
      {:pre [(validate-arguments hyperlink-args (keys args))]}
      (let [label      (deref-or-value label)
            disabled?  (deref-or-value disabled?)
            the-button [box
                        :align :start
                        :child [:a
                                (merge
                                  {:class    (str "rc-hyperlink " class)
                                   :style    (merge
                                               {:flex                "none"
                                                :cursor              (if disabled? "not-allowed" "pointer")
                                                :-webkit-user-select "none"}
                                               style)
                                   :on-click (handler-fn
                                               (when (and on-click (not disabled?))
                                                 (on-click)))
                                   }
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
  [{:name :label            :required false                          :type "string"     :description "Label for the button (can be artitrary markup)."}
   {:name :href             :required false                          :type "string"     :description "If specified, which URL to jump to when clicked."}
   {:name :target           :required false                          :type "string"     :description "A string representing where to load href: _self - open in same window/tab (the default), _blank - open in new window/tab, _parent - open in parent window."}
   {:name :tooltip          :required false                          :type "string"     :description "show a popover-tooltip using this text."}
   {:name :tooltip-position :required false :default ":below-center" :type "keyword"    :description "position of the popover-tooltip. e.g. :right-below."}
   {:name :class            :required false                          :type "string"     :description "additional CSS classes required."}
   {:name :style            :required false                          :type "map"        :description "CSS styles to add or override."}
   {:name :attr             :required false                          :type "map"        :description "html attributes to add or override (:class/:style not allowed)."}])

(def hyperlink-href-args
  (set (map :name hyperlink-href-args-desc)))

(defn hyperlink-href
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel."
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [label href target tooltip tooltip-position class style attr] :as args}]
      {:pre [(validate-arguments hyperlink-href-args (keys args))]}
      (let [label      (deref-or-value label)
            href       (deref-or-value href)
            target     (deref-or-value target)
            the-button [:a
                        (merge
                          {:class  (str "rc-hyperlink-href " class)
                           :style  (merge
                                     {:flex                "none"
                                      :-webkit-user-select "none"}
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
