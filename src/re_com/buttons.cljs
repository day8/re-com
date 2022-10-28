(ns re-com.buttons
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]])
  (:require
    [re-com.util     :refer [deref-or-value px merge-css add-map-to-hiccup-call flatten-attr]]
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.validate :refer [position? position-options-list button-size? button-sizes-list
                             string-or-hiccup? css-style? html-attr? string-or-atom? parts?] :refer-macros [validate-args-macro]]
    [re-com.popover  :refer [popover-tooltip]]
    [re-com.box      :refer [h-box v-box box gap line flex-child-style]]
    [reagent.core    :as    reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: button
;; ------------------------------------------------------------------------------------

(def button-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-button-wrapper" :impl "[button]"          :notes "Outer wrapper of the button, tooltip (if any), everything."}
     {:name :tooltip :level 1 :class "rc-button-tooltip" :impl "[popover-tooltip]" :notes "Tooltip, if enabled."}
     {:type :legacy  :level 1 :class "rc-button"         :impl "[:button]"         :notes "The actual button."}]))

(def button-css-desc
  {:main {:class ["rc-button" "btn"]
          :style (flex-child-style "none") }
   :wrapper {:class ["rc-button-wrapper" "display-inline-flex"]}
   :tooltip {:class ["rc-button-tooltip"]}})

(def button-parts
  (when include-args-desc?
    (-> (map :name button-parts-desc) set)))

(def button-args-desc
  (when include-args-desc?
    [{:name :label            :required true                         :type "string | hiccup" :validate-fn string-or-hiccup?     :description "label for the button"}
     {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?                   :description "a function which takes no params and returns nothing. Called when the button is clicked"}
     {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup?     :description "what to show in the tooltip"}
     {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?             :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :disabled?        :required false :default false         :type "boolean | atom"                                     :description "if true, the user can't click the button"}
     {:name :class            :required false                        :type "string"          :validate-fn string?               :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
     {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?            :description "CSS styles (applies to the button, not the wrapping div)"}
     {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?            :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
     {:name :parts            :required false                        :type "map"             :validate-fn (parts? button-parts) :description "See Parts section below."}
     {:name :src              :required false                        :type "map"             :validate-fn map?                  :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                        :type "map"             :validate-fn map?                  :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn button
  "Returns the markup for a basic button"
  []
  (let [showing? (reagent/atom false)]
    (fn
      [& {:keys [label on-click tooltip tooltip-position disabled? class style attr parts src debug-as]
          :or   {class "btn-default"}
          :as   args}]
      (or
        (validate-args-macro button-args-desc args)
        (do
          (when-not tooltip (reset! showing? false)) ;; To prevent tooltip from still showing after button drag/drop
          (let [disabled? (deref-or-value disabled?)
                cmerger (merge-css button-css-desc args)
                the-button [:button
                            (merge
                             (flatten-attr (cmerger :main))
                             {:disabled disabled?
                              :on-click (handler-fn
                                         (when (and on-click (not disabled?))
                                           (on-click event)))}
                             (when tooltip
                               {:on-mouse-over (handler-fn (reset! showing? true))
                                :on-mouse-out  (handler-fn (reset! showing? false))})
                             attr)
                            label]]
            (when disabled?
              (reset! showing? false))
            (add-map-to-hiccup-call
             (cmerger :wrapper)
             [box
              :src      src
              :debug-as (or debug-as (reflect-current-component))
              :align    :start
              :child    (if tooltip
                          (add-map-to-hiccup-call
                           (cmerger :tooltip)
                           [popover-tooltip
                            :src      (at)
                            :label    tooltip
                            :position (or tooltip-position :below-center)
                            :showing? showing?
                            :anchor   the-button])
                          the-button)])))))))


;;--------------------------------------------------------------------------------------------------
;; Component: md-circle-icon-button
;;--------------------------------------------------------------------------------------------------

(def md-circle-icon-button-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-md-circle-icon-button-wrapper" :impl "[md-circle-icon-button]" :notes "Outer wrapper of the button, tooltip (if any), everything."}
     {:name :tooltip :level 1 :class "rc-md-circle-icon-button-tooltip" :impl "[popover-tooltip]"       :notes "Tooltip, if enabled."}
     {:type :legacy  :level 1 :class "rc-md-circle-icon-button"         :impl "[:div]"                  :notes "The actual button."}
     {:name :icon    :level 2 :class "rc-md-circle-icon-button-icon"    :impl "[:i]"                    :notes "The button icon."}]))

(def md-circle-icon-button-css-desc
  {:main {:class
          (fn [{:keys [size emphasise? disabled?]}]
            ["noselect" "rc-md-circle-icon-button"
             (case size
               :smaller "rc-circle-smaller"
               :larger "rc-circle-larger"
               nil)
             (when emphasise? "rc-circle-emphasis")
             (when disabled? "rc-circle-disabled")])
          :style
          (fn [{:keys [disabled?]}]
                (if disabled?
                  {}
                  {:cursor "pointer"}))}
   :wrapper {:class ["display-inline-flex" "rc-md-circle-icon-button-wrapper"]}
   :tooltip {:class ["rc-md-circle-icon-button-tooltip"]}
   :icon {:class
          (fn [{:keys [md-icon-name]}]
            ["zmdi" "zmdi-hc-fw-rc" md-icon-name "rc-md-circle-icon-button-icon"])}
   })

(def md-circle-icon-button-parts
  (when include-args-desc?
    (-> (map :name md-circle-icon-button-parts-desc) set)))

(def md-circle-icon-button-args-desc
  (when include-args-desc?
    [{:name :md-icon-name     :required true  :default "zmdi-plus"   :type "string"          :validate-fn string?                              :description [:span "the name of the icon." [:br] "For example, " [:code "\"zmdi-plus\""] " or " [:code "\"zmdi-undo\""]]}
     {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?                                  :description "a function which takes no params and returns nothing. Called when the button is clicked"}
     {:name :size             :required false :default :regular      :type "keyword"         :validate-fn button-size?                         :description [:span "one of " button-sizes-list]}
     {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup?                    :description "what to show in the tooltip"}
     {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?                            :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :emphasise?       :required false :default false         :type "boolean"                                                           :description "if true, use emphasised styling so the button really stands out"}
     {:name :disabled?        :required false :default false         :type "boolean"                                                           :description "if true, the user can't click the button"}
     {:name :class            :required false                        :type "string"          :validate-fn string?                              :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
     {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?                           :description "CSS styles to add or override (applies to the button, not the wrapping div)"}
     {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?                           :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
     {:name :parts            :required false                        :type "map"             :validate-fn (parts? md-circle-icon-button-parts) :description "See Parts section below."}
     {:name :src              :required false                        :type "map"             :validate-fn map?                                 :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                        :type "map"             :validate-fn map?                                 :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn md-circle-icon-button
  "a circular button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn md-circle-icon-button-render
      [& {:keys [md-icon-name on-click size tooltip tooltip-position emphasise? disabled? class style attr parts src debug-as]
          :or   {md-icon-name "zmdi-plus"}
          :as   args}]
      (or
        (validate-args-macro md-circle-icon-button-args-desc args)
        (do
          (when-not tooltip (reset! showing? false)) ;; To prevent tooltip from still showing after button drag/drop
          (let [cmerger (merge-css md-circle-icon-button-css-desc args)
                the-button [:div
                            (merge
                             (flatten-attr
                              (cmerger :main {:emphasise? emphasise? :disabled? disabled? :size size}))
                             {:on-click (handler-fn
                                         (when (and on-click (not disabled?))
                                           (on-click event)))}
                              (when tooltip
                                {:on-mouse-over (handler-fn (reset! showing? true))
                                 :on-mouse-out  (handler-fn (reset! showing? false))})
                              attr)
                            [:i (flatten-attr (cmerger :icon {:md-icon-name md-icon-name}))]]]
            (add-map-to-hiccup-call
             (cmerger :wrapper)
             [box
              :src      src
              :debug-as (or debug-as (reflect-current-component))
              :align    :start
              :child    (if tooltip
                          (add-map-to-hiccup-call
                           (cmerger :tooltip)
                           [popover-tooltip
                            :src      (at)
                            :label    tooltip
                            :position (or tooltip-position :below-center)
                            :showing? showing?
                            :anchor   the-button])
                          the-button)])))))))


;;--------------------------------------------------------------------------------------------------
;; Component: md-icon-button
;;--------------------------------------------------------------------------------------------------

(def md-icon-button-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-md-icon-button-wrapper" :impl "[md-icon-button]" :notes "Outer wrapper of the button, tooltip (if any), everything."}
     {:name :tooltip :level 1 :class "rc-md-icon-button-tooltip" :impl "[popover-tooltip]" :notes "Tooltip, if enabled."}
     {:type :legacy  :level 1 :class "rc-md-icon-button"         :impl "[:div]"                  :notes "The actual button."}
     {:name :icon    :level 2 :class "rc-md-icon-button-icon"    :impl "[:i]"                    :notes "The button icon."}]))

(def md-icon-button-css-desc
  {:main {:class
          (fn [{:keys [size emphasise? disabled?]}]
            ["noselect" "rc-md-icon-button"
             (case size
               :smaller "rc-icon-smaller"
               :larger "rc-icon-larger"
               "rc-icon-larger")
             (when emphasise? "rc-icon-emphasis")
             (when disabled? "rc-icon-disabled")])
          :style
          (fn [{:keys [disabled?]}]
                (if disabled?
                  {}
                  {:cursor "pointer"}))}
   :wrapper {:class ["display-inline-flex" "rc-md-icon-button-wrapper"]}
   :tooltip {:class ["rc-md-icon-button-tooltip"]}
   :icon {:class
          (fn [{:keys [md-icon-name]}]
            ["zmdi" "zmdi-hc-fw-rc" md-icon-name "rc-md-circle-icon-button-icon"])}
   })

(def md-icon-button-parts
  (when include-args-desc?
    (-> (map :name md-icon-button-parts-desc) set)))

(def md-icon-button-args-desc
  (when include-args-desc?
    [{:name :md-icon-name     :required true  :default "zmdi-plus"   :type "string"          :validate-fn string?                       :description [:span "the name of the icon." [:br] "For example, " [:code "\"zmdi-plus\""] " or " [:code "\"zmdi-undo\""]]}
     {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?                           :description "a function which takes no params and returns nothing. Called when the button is clicked"}
     {:name :size             :required false :default :regular      :type "keyword"         :validate-fn button-size?                  :description [:span "one of " button-sizes-list]}
     {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup?             :description "what to show in the tooltip"}
     {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?                     :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :emphasise?       :required false :default false         :type "boolean"                                                    :description "if true, use emphasised styling so the button really stands out"}
     {:name :disabled?        :required false :default false         :type "boolean"                                                    :description "if true, the user can't click the button"}
     {:name :class            :required false                        :type "string"          :validate-fn string?                       :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
     {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?                    :description "CSS styles to add or override (applies to the button, not the wrapping div)"}
     {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?                    :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
     {:name :parts            :required false                        :type "map"             :validate-fn (parts? md-icon-button-parts) :description "See Parts section below."}
     {:name :src              :required false                        :type "map"             :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                        :type "map"             :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn md-icon-button
  "a square button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn md-icon-button-render
      [& {:keys [md-icon-name on-click size tooltip tooltip-position emphasise? disabled? class style attr parts src debug-as]
          :or   {md-icon-name "zmdi-plus"}
          :as   args}]
      (or
        (validate-args-macro md-icon-button-args-desc args)
        (do
          (when-not tooltip (reset! showing? false)) ;; To prevent tooltip from still showing after button drag/drop
          (let [cmerger (merge-css md-circle-icon-button-css-desc args)
                the-button [:div
                            (merge
                             (flatten-attr
                              (cmerger :main {:size size :emphasise? emphasise? :disabled? disabled?}))
                             {:on-click (handler-fn
                                         (when (and on-click (not disabled?))
                                           (on-click event)))}
                             (when tooltip
                               {:on-mouse-over (handler-fn (reset! showing? true))
                                :on-mouse-out  (handler-fn (reset! showing? false))})
                             attr)
                            [:i (cmerger :icon {:md-icon-name md-icon-name})]]]
            (add-map-to-hiccup-call
             (cmerger :wrapper)
             [box
              :src      src
              :debug-as (or debug-as (reflect-current-component))
              :align    :start
              :child    (if tooltip
                          (add-map-to-hiccup-call
                           (cmerger :tooltip)
                           [popover-tooltip
                            :src      (at)
                            :label    tooltip
                            :position (or tooltip-position :below-center)
                            :showing? showing?
                            :anchor   the-button])
                          the-button)])))))))


;;--------------------------------------------------------------------------------------------------
;; Component: info-button
;;--------------------------------------------------------------------------------------------------

(def info-button-parts-desc
  (when include-args-desc?
    [{:name :tooltip :level 0 :class "rc-info-button-popover-anchor-wrapper" :impl "[popover-tooltip]" :notes "Outer wrapper of the button, tooltip (if any), everything."}
     {:type :legacy  :level 1 :class "rc-info-button"                        :impl "[:div]"                  :notes "The actual button."}
     {:name :icon    :level 2 :class "rc-info-button-icon"                   :impl "[:svg]"                    :notes "The button icon."}]))

(def info-button-css-desc
  {:main {:class (fn [{:keys [disabled?]}]
                   ["noselect" "rc-info-button" (when disabled? "rc-icon-disabled")])
          :style (fn [{:keys [disabled?]}]
                   (if disabled?
                     {:cursor "pointer"}))}
   :tooltip {:class ["rc-info-button-popover-anchor-wrapper"]}
   :icon {:class ["rc-info-button-icon"]}})

(def info-button-parts
  (when include-args-desc?
    (-> (map :name info-button-parts-desc) set)))

(def info-button-args-desc
  (when include-args-desc?
    [{:name :info      :required true                        :type "string | hiccup" :validate-fn string-or-hiccup?          :description "what's shown in the popover"}
     {:name :position  :required false :default :right-below :type "keyword"         :validate-fn position?                  :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :width     :required false :default "250px"      :type "string"          :validate-fn string?                    :description "width in px"}
     {:name :disabled? :required false :default false        :type "boolean"                                                 :description "if true, the user can't click the button"}
     {:name :class     :required false                       :type "string"          :validate-fn string?                    :description "CSS class names, space separated (applies to the button, not the popover wrapper)"}
     {:name :style     :required false                       :type "CSS style map"   :validate-fn css-style?                 :description "CSS styles to add or override (applies to the button, not the popover wrapper)"}
     {:name :attr      :required false                       :type "HTML attr map"   :validate-fn html-attr?                 :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the popover wrapper)"]}
     {:name :parts     :required false                       :type "map"             :validate-fn (parts? info-button-parts) :description "See Parts section below."}
     {:name :src       :required false                       :type "map"             :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as  :required false                       :type "map"             :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn info-button
  "A tiny light grey button, with an 'i' in it. Meant to be unobtrusive.
  When pressed, displays a popup assumedly containing helpful information.
  Primarily designed to be nestled against the label of an input field, explaining the purpose of that field.
  Create a very small \"i\" icon via SVG"
  []
  (let [showing? (reagent/atom false)]
    (fn info-button-render
      [& {:keys [info position width disabled? class style attr parts src debug-as] :as args}]
      (or
        (validate-args-macro info-button-args-desc args)
        (let [cmerger (merge-css info-button-css-desc args)]
          (add-map-to-hiccup-call
           (cmerger :tooltip)
           [popover-tooltip
            :src       src
            :debug-as  (or debug-as (reflect-current-component))
            :label     info
            :status    :info
            :position  (or position :right-below)
            :width     (or width "250px")
            :showing?  showing?
            :on-cancel #(swap! showing? not)
            :anchor    [:div
                        (merge
                         (flatten-attr
                          (cmerger :main {:disabled? disabled?}))
                         {:on-click (handler-fn
                                     (when (not disabled?)
                                       (swap! showing? not)))}
                         attr)
                        [:svg
                         (merge
                          (flatten-attr (cmerger :icon))
                          {:width  "11"
                           :height "11"})
                         [:circle {:cx "5.5" :cy "5.5" :r "5.5"}]
                         [:circle {:cx "5.5" :cy "2.5" :r "1.4" :fill "white"}]
                         [:line   {:x1 "5.5" :y1 "5.2" :x2 "5.5" :y2 "9.7" :stroke "white" :stroke-width "2.5"}]]]]))))))


;;--------------------------------------------------------------------------------------------------
;; Component: row-button
;;--------------------------------------------------------------------------------------------------

(def row-button-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-row-button-wrapper" :impl "[row-button]" :notes "Outer wrapper of the row button, tooltip (if any), everything."}
     {:name :tooltip :level 1 :class "rc-row-button-tooltip" :impl "[popover-tooltip]" :notes "Tooltip, if enabled."}
     {:type :legacy  :level 1 :class "rc-row-button"         :impl "[:div]"                  :notes "The actual button."}
     {:name :icon    :level 2 :class "rc-row-button-icon"    :impl "[:i]"                    :notes "The button icon."}]))

(def row-button-css-desc
  {:main {:class (fn [{:keys [disabled? mouse-over-row?]}]
                   ["noselect" "rc-row-button"
                    (when mouse-over-row? "rc-row-mouse-over-row")
                    (when disabled? "rc-row-disabled")])}
   :wrapper {:class ["display-inline-flex" "rc-row-button-wrapper"]}
   :tooltip {:class ["rc-row-button-tooltip"]}
   :icon {:class (fn [{:keys [md-icon-name]}]
                   ["zmdi" "zmdi-hc-fw-rc" md-icon-name "rc-row-button-icon"])}})

(def row-button-parts
  (when include-args-desc?
    (-> (map :name row-button-parts-desc) set)))

(def row-button-args-desc
  (when include-args-desc?
    [{:name :md-icon-name     :required true  :default "zmdi-plus"   :type "string"          :validate-fn string?                   :description [:span "the name of the icon." [:br] "For example, " [:code "\"zmdi-plus\""] " or " [:code "\"zmdi-undo\""]]}
     {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?                       :description "a function which takes no params and returns nothing. Called when the button is clicked"}
     {:name :mouse-over-row?  :required false :default false         :type "boolean"                                                :description "true if the mouse is hovering over the row"}
     {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup?         :description "what to show in the tooltip"}
     {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?                 :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :disabled?        :required false :default false         :type "boolean"                                                :description "if true, the user can't click the button"}
     {:name :class            :required false                        :type "string"          :validate-fn string?                   :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
     {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?                :description "CSS styles to add or override (applies to the button, not the wrapping div)"}
     {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?                :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
     {:name :parts            :required false                        :type "map"             :validate-fn (parts? row-button-parts) :description "See Parts section below."}
     {:name :src              :required false                        :type "map"             :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                        :type "map"             :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn row-button
  "a small button containing a material design icon"
  []
  (let [showing? (reagent/atom false)]
    (fn row-button-render
      [& {:keys [md-icon-name on-click mouse-over-row? tooltip tooltip-position disabled? class style attr parts src]
          :or   {md-icon-name "zmdi-plus"}
          :as   args}]
      (or
        (validate-args-macro row-button-args-desc args)
        (do
          (when-not tooltip (reset! showing? false)) ;; To prevent tooltip from still showing after button drag/drop
          (let [cmerger (merge-css row-button-css-desc args)
                the-button [:div
                            (merge
                             (flatten-attr
                              (cmerger :main {:mouse-over-row? mouse-over-row? :disabled? disabled?}))
                              {:on-click (handler-fn
                                           (when (and on-click (not disabled?))
                                             (on-click event)))}
                              (when tooltip
                                {:on-mouse-over (handler-fn (reset! showing? true))
                                 :on-mouse-out  (handler-fn (reset! showing? false))}) ;; Need to return true to ALLOW default events to be performed
                              attr)
                            [:i (flatten-attr (cmerger :icon {:md-icon-name md-icon-name}))]]]
            (add-map-to-hiccup-call
             (cmerger :wrapper)
             [box
              :src      src
              :debug-as (reflect-current-component)
              :align    :start
              :child    (if tooltip
                          (add-map-to-hiccup-call
                           (cmerger :tooltip)
                           [popover-tooltip
                            :src      (at)
                            :label    tooltip
                            :position (or tooltip-position :below-center)
                            :showing? showing?
                            :anchor   the-button])
                          the-button)])))))))


;;--------------------------------------------------------------------------------------------------
;; Component: hyperlink
;;--------------------------------------------------------------------------------------------------

(def hyperlink-parts-desc
  (when include-args-desc?
    [{:name :wrapper   :level 0 :class "rc-hyperlink-wrapper"   :impl "[hyperlink]"       :notes "Outer wrapper of the hyperlink, tooltip (if any), everything."}
     {:name :tooltip   :level 1 :class "rc-hyperlink-tooltip"   :impl "[popover-tooltip]" :notes "Tooltip, if enabled."}
     {:name :container :level 1 :class "rc-hyperlink-container" :impl "[box]"}
     {:type :legacy    :level 2 :class "rc-hyperlink"           :impl "[:a]"              :notes "The anchor."}]))

(def hyperlink-css-desc
  {:main {:class ["noselect" "rc-hyperlink"]
          :style (fn [{:keys [disabled?]}]
                   (merge
                    (flex-child-style "none")
                    (if disabled?
                      {:cursor "default"
                       :pointer-events "none"
                       :color "grey"}
                      {:cursor "pointer"})))}
   :wrapper {:class ["display-inline-flex" "rc-hyperlink-wrapper"]}
   :tooltip {:class ["rc-hyperlink-tooltip"]}
   :container {:class ["rc-hyperlink-container"]}})

(def hyperlink-parts
  (when include-args-desc?
    (-> (map :name hyperlink-parts-desc) set)))

(def hyperlink-args-desc
  (when include-args-desc?
    [{:name :label            :required true                         :type "string | hiccup | r/atom" :validate-fn string-or-hiccup?        :description "label/hiccup for the button"}
     {:name :on-click         :required false                        :type "-> nil"                   :validate-fn fn?                      :description "a function which takes no params and returns nothing. Called when the button is clicked"}
     {:name :tooltip          :required false                        :type "string | hiccup"          :validate-fn string-or-hiccup?        :description "what to show in the tooltip"}
     {:name :tooltip-position :required false :default :below-center :type "keyword"                  :validate-fn position?                :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :disabled?        :required false :default false         :type "boolean | r/atom"                                               :description "if true, the user can't click the button"}
     {:name :class            :required false                        :type "string"                   :validate-fn string?                  :description "CSS class names, space separated (applies to the hyperlink, not the wrapping div)"}
     {:name :style            :required false                        :type "CSS style map"            :validate-fn css-style?               :description "CSS styles to add or override (applies to the hyperlink, not the wrapping div)"}
     {:name :attr             :required false                        :type "HTML attr map"            :validate-fn html-attr?               :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the hyperlink, not the wrapping div)"]}
     {:name :parts            :required false                        :type "map"                      :validate-fn (parts? hyperlink-parts) :description "See Parts section below."}
     {:name :src              :required false                        :type "map"                      :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                        :type "map"                      :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn hyperlink
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel"
  []
  (let [showing? (reagent/atom false)]
    (fn hyperlink-render
      [& {:keys [label on-click tooltip tooltip-position disabled? class style attr parts src debug-as] :as args}]
      (or
        (validate-args-macro hyperlink-args-desc args)
        (do
          (when-not tooltip (reset! showing? false)) ;; To prevent tooltip from still showing after button drag/drop
          (let [label      (deref-or-value label)
                disabled?  (deref-or-value disabled?)
                cmerger (merge-css hyperlink-css-desc args)
                the-button (add-map-to-hiccup-call
                            (cmerger :container)
                            [box
                             :src   (at)
                             :align :start
                             :child [:a
                                     (merge
                                      (flatten-attr (cmerger :main {:disabled? disabled?}))
                                      {:on-click (handler-fn
                                                  (when (and on-click (not disabled?))
                                                    (on-click event)))}
                                      (when tooltip
                                        {:on-mouse-over (handler-fn (reset! showing? true))
                                         :on-mouse-out  (handler-fn (reset! showing? false))})
                                      attr)
                                     label]])]
            (add-map-to-hiccup-call
             (cmerger :wrapper)
             [box
              :src      src
              :debug-as (or debug-as (reflect-current-component))
              :align    :start
              :child    (if tooltip
                          (add-map-to-hiccup-call
                           (cmerger :tooltip)
                           [popover-tooltip
                            :src      (at)
                            :label    tooltip
                            :position (or tooltip-position :below-center)
                            :showing? showing?
                            :anchor   the-button])
                          the-button)])))))))


;;--------------------------------------------------------------------------------------------------
;; Component: hyperlink-href
;;--------------------------------------------------------------------------------------------------

(def hyperlink-href-parts-desc
  (when include-args-desc?
    [{:name :wrapper   :level 0 :class "rc-hyperlink-href-wrapper"   :impl "[hyperlink-href]"  :notes "Outer wrapper of the hyperlink-href, tooltip (if any), everything."}
     {:name :tooltip   :level 1 :class "rc-hyperlink-href-tooltip"   :impl "[popover-tooltip]" :notes "Tooltip, if enabled."}
     {:type :legacy    :level 2 :class "rc-hyperlink-href"           :impl "[:a]"              :notes "The anchor."}]))

(def hyperlink-href-css-desc
  {:main {:class ["rc-hyperlink-href" "noselect"]
          :style (fn [{:keys [disabled?]}]
                   (merge
                    (flex-child-style "none")
                    (if disabled?
                      {:cursor "default"
                       :pointer-events "none"
                       :color "grey"}
                      {:cursor "pointer"})))}
   :wrapper {:class ["rc-hyperlink-href-wrapper" "display-inline-flex"]}
   :tooltip {:class ["rc-hyperlink-href-tooltip"]}})

(def hyperlink-href-parts
  (when include-args-desc?
    (-> (map :name hyperlink-href-parts-desc) set)))

(def hyperlink-href-args-desc
  (when include-args-desc?
    [{:name :label            :required true                         :type "string | hiccup | r/atom" :validate-fn string-or-hiccup?             :description "label/hiccup for the button"}
     {:name :href             :required true                         :type "string | r/atom"          :validate-fn string-or-atom?               :description "if specified, the link target URL"}
     {:name :target           :required false :default "_self"       :type "string | r/atom"          :validate-fn string-or-atom?               :description "one of \"_self\" or \"_blank\""}
     {:name :tooltip          :required false                        :type "string | hiccup"          :validate-fn string-or-hiccup?             :description "what to show in the tooltip"}
     {:name :tooltip-position :required false :default :below-center :type "keyword"                  :validate-fn position?                     :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :disabled?        :required false :default false         :type "boolean | r/atom"                                                    :description "if true, the user can't click the button"}
     {:name :class            :required false                        :type "string"                   :validate-fn string?                       :description "CSS class names, space separated (applies to the hyperlink, not the wrapping div)"}
     {:name :style            :required false                        :type "CSS style map"            :validate-fn css-style?                    :description "CSS styles to add or override (applies to the hyperlink, not the wrapping div)"}
     {:name :attr             :required false                        :type "HTML attr map"            :validate-fn html-attr?                    :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the hyperlink, not the wrapping div)"]}
     {:name :parts            :required false                        :type "map"                      :validate-fn (parts? hyperlink-href-parts) :description "See Parts section below."}
     {:name :src              :required false                        :type "map"                      :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                        :type "map"                      :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn hyperlink-href
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel"
  []
  (let [showing? (reagent/atom false)]
    (fn hyperlink-href-render
      [& {:keys [label href target tooltip tooltip-position disabled? class style attr parts src debug-as] :as args}]
      (or
        (validate-args-macro hyperlink-href-args-desc args)
        (do
          (when-not tooltip (reset! showing? false)) ;; To prevent tooltip from still showing after button drag/drop
          (let [label      (deref-or-value label)
                href       (deref-or-value href)
                target     (deref-or-value target)
                disabled?  (deref-or-value disabled?)
                cmerger (merge-css hyperlink-href-css-desc args)
                the-button [:a
                            (merge (flatten-attr (cmerger :main {:disabled? disabled?}))
                                   {:target target}
                                   ;; As of HTML5 the href attribute on a elements is not required; when those elements do
                                   ;; not have href attributes they do not create hyperlinks. These are also known as a
                                   ;; 'placeholder link'. A placeholder link resembles a traditional hyperlink, but does not
                                   ;; lead anywhere; i.e. it is disabled.
                                   ;; Ref: https://www.w3.org/TR/html5/links.html#attr-hyperlink-href
                                   (when (not disabled?)
                                     {:href   href})
                                   (when tooltip
                                     {:on-mouse-over (handler-fn (reset! showing? true))
                                      :on-mouse-out  (handler-fn (reset! showing? false))})
                                   attr)
                            label]]

            (add-map-to-hiccup-call
             (cmerger :wrapper)
             [box
              :src      src
              :debug-as (or debug-as (reflect-current-component))
              :align    :start
              :child    (if tooltip
                          (add-map-to-hiccup-call
                           (cmerger :tooltip)
                           [popover-tooltip
                            :src      (at)
                            :label    tooltip
                            :position (or tooltip-position :below-center)
                            :showing? showing?
                            :anchor   the-button])
                          the-button)])))))))
