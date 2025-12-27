(ns re-com.buttons
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   [re-com.args :as args]
   re-com.button.theme
   re-com.md-circle-icon-button.theme
   re-com.md-icon-button.theme
   re-com.info-button.theme
   re-com.row-button.theme
   re-com.hyperlink.theme
   re-com.hyperlink-href.theme
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.button :as-alias btn]
   [re-com.md-circle-icon-button :as-alias ci-btn]
   [re-com.md-icon-button :as-alias md-btn]
   [re-com.info-button :as-alias info-btn]
   [re-com.row-button :as-alias rb]
   [re-com.hyperlink :as-alias hyperlink]
   [re-com.hyperlink-href :as-alias hyperlink-href]
   [re-com.util     :as u :refer [deref-or-value px]]
   [re-com.validate :refer [position? position-options-list button-size? button-sizes-list
                            string-or-hiccup? css-class? css-style? html-attr? string-or-atom? parts?]]
   [re-com.popover  :refer [popover-tooltip]]
   [re-com.box      :refer [box flex-child-style]]
   [reagent.core    :as reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: button
;; ------------------------------------------------------------------------------------

(def part-structure
  [::btn/wrapper {:impl 're-com.box/box}
   [::btn/popover-tooltip {:impl 're-com.popover/popover-tooltip}
    [::btn/tooltip {:top-level-arg? true}]]
   [::btn/button {:tag :button}
    [::btn/label {:top-level-arg? true :part-required? true}]]])

(def button-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def button-parts
  (when include-args-desc?
    (-> (map :name button-parts-desc) set)))

(def button-args-desc
  (when include-args-desc?
    (concat
     [{:description
       "a function which takes no params and returns nothing. Called when the button is clicked",
       :name        :on-click,
       :required    false,
       :type        "-> nil",       :validate-fn fn?}
      {:default     :below-center,
       :description [:span "relative to this anchor. One of " position-options-list],
       :name        :tooltip-position,
       :required    false,
       :type        "keyword",
       :validate-fn position?}
      {:default     false,
       :description "if true, the user can't click the button",
       :name        :disabled?,
       :required    false,
       :type        "boolean | atom"}]
     args/std
     (part/describe-args part-structure))))

(defn button
  "Returns the markup for a basic button"
  [& {:keys [pre-theme theme]}]
  (let [theme       (theme/comp pre-theme theme)
        showing?    (reagent/atom false)
        transition! #(case %
                       :show   (reset! showing? true)
                       :hide   (reset! showing? false)
                       :toggle (swap! showing? not))]
    (fn [& {:keys [tooltip-position disabled? on-click class] :as props}]
      (or
       (validate-args-macro button-args-desc props)
       (let [disabled?    (u/deref-or-value disabled?)
             part         (partial part/part part-structure props)
             tooltip?     (part/get-part part-structure props ::tooltip)
             re-com       {:transition! transition!
                           :state       {:class     class
                                         :disabled? disabled?
                                         :tooltip?  tooltip?}}
             label-part   (part ::btn/label
                            {:theme theme
                             :props {:re-com re-com}})
             button-part  (part ::btn/button
                            {:theme      theme
                             :post-props (select-keys props [:style :attr :class])
                             :props
                             {:tag      :button
                              :class    class
                              :re-com   re-com
                              :on-click on-click
                              :children [label-part]
                              :attr
                              (merge {:disabled disabled?
                                      :on-click (handler-fn
                                                  (when (and on-click (not disabled?))
                                                    (on-click event)))}
                                     (when tooltip?
                                       {:on-mouse-over (handler-fn (transition! :show))
                                        :on-mouse-out  (handler-fn (transition! :hide))}))}})
             tooltip-part (part ::btn/tooltip
                            {:theme theme
                             :props {:re-com re-com}})
             popover-part (part ::btn/popover-tooltip
                            {:impl  popover-tooltip
                             :theme theme
                             :props {:src      (at)
                                     :label    tooltip-part
                                     :position (or tooltip-position :below-center)
                                     :showing? showing?
                                     :anchor   button-part}})]
         (when (or disabled? (not tooltip?)) (transition! :hide))
         (part ::btn/wrapper
           {:impl       box
            :theme      theme
            :post-props {:attr (debug/->attr props)}
            :props      {:re-com re-com
                         :child  (if tooltip? popover-part button-part)}}))))))

;;--------------------------------------------------------------------------------------------------
;; Component: md-circle-icon-button
;;--------------------------------------------------------------------------------------------------

(def md-circle-icon-button-part-structure
  [::ci-btn/wrapper {:impl 're-com.box/box}
   [::ci-btn/popover-tooltip {:impl  're-com.popover/popover-tooltip
                              :notes "Tooltip, if enabled by passing a :tooltip part"}
    [::ci-btn/tooltip {:top-level-arg? true}]]
   [::ci-btn/button
    [::ci-btn/icon {:tag :i}]]])

(def md-circle-icon-button-parts-desc
  (when include-args-desc?
    (part/describe md-circle-icon-button-part-structure)))

(def md-circle-icon-button-parts
  (when include-args-desc?
    (-> (map :name md-circle-icon-button-parts-desc) set)))

(def md-circle-icon-button-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :md-icon-name :required true :default "zmdi-plus" :type "string" :validate-fn string? :description [:span "the name of the icon." [:br] "For example, " [:code "\"zmdi-plus\""] " or " [:code "\"zmdi-undo\""]]}
       {:name :on-click :required false :type "-> nil" :validate-fn fn? :description "a function which takes no params and returns nothing. Called when the button is clicked"}
       {:name :size :required false :default :regular :type "keyword" :validate-fn button-size? :description [:span "one of " button-sizes-list]}
       {:name :tooltip :required false :type "string | hiccup" :validate-fn string-or-hiccup? :description "what to show in the tooltip"}
       {:name :tooltip-position :required false :default :below-center :type "keyword" :validate-fn position? :description [:span "relative to this anchor. One of " position-options-list]}
       {:name :emphasise? :required false :default false :type "boolean" :description "if true, use emphasised styling so the button really stands out"}
       {:name :disabled? :required false :default false :type "boolean" :description "if true, the user can't click the button"}
       {:name :class :required false :type "string" :validate-fn css-class? :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
       {:name :style :required false :type "CSS style map" :validate-fn css-style? :description "CSS styles to add or override (applies to the button, not the wrapping div)"}
       {:name :attr :required false :type "HTML attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
       {:name :parts :required false :type "map" :validate-fn (parts? md-circle-icon-button-parts) :description "See Parts section below."}
       {:name :src :required false :type "map" :validate-fn map? :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
       {:name :debug-as :required false :type "map" :validate-fn map? :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks.  A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
      theme/args-desc
      (part/describe-args md-circle-icon-button-part-structure)))))

(defn md-circle-icon-button
  "a circular button containing a material design icon"
  [& {:keys [pre-theme theme debug-as]}]
  (let [theme       (theme/comp pre-theme theme)
        showing?    (reagent/atom false)
        transition! #(case %
                       :show   (reset! showing? true)
                       :hide   (reset! showing? false)
                       :toggle (swap! showing? not))]
    (fn md-circle-icon-button-render
      [& {:keys [md-icon-name tooltip-position size emphasise? disabled? on-click]
          :as   args
          :or   {md-icon-name "zmdi-plus"}}]
      (or
       (validate-args-macro md-circle-icon-button-args-desc args)
       (let [part         (partial part/part md-circle-icon-button-part-structure args)
             tooltip?     (part/get-part md-circle-icon-button-part-structure args ::ci-btn/tooltip)
             re-com       {:transition! transition!
                           :state       {:size         size
                                         :emphasise?   emphasise?
                                         :disabled?    disabled?
                                         :tooltip?     tooltip?
                                         :md-icon-name md-icon-name
                                         :on-click     on-click}}
             icon-part    (part ::ci-btn/icon
                            {:theme theme
                             :props {:re-com re-com
                                     :tag    :i}})
             btn-part     (part ::ci-btn/button
                            {:theme      theme
                             :post-props (select-keys args [:class :style :attr])
                             :props      {:re-com   re-com
                                          :children [icon-part]}})
             popover-part (part ::ci-btn/tooltip-wrapper
                            {:impl  popover-tooltip
                             :theme theme
                             :props {:src      (at)
                                     :label    (part ::ci-btn/tooltip {:theme theme :props {:re-com re-com}})
                                     :position (or tooltip-position :below-center)
                                     :showing? showing?
                                     :anchor   btn-part}})]
         ;; Prevent tooltip from still showing after button drag/drop
         (when-not tooltip? (transition! :hide))
         (part ::ci-btn/wrapper
           {:impl       box
            :theme      theme
            :post-props {:attr (debug/->attr args)}
            :props      {:re-com   re-com
                         :debug-as (or debug-as (reflect-current-component))
                         :child
                         (if tooltip? popover-part btn-part)}}))))))

;;--------------------------------------------------------------------------------------------------
;; Component: md-icon-button
;;--------------------------------------------------------------------------------------------------

(def md-icon-button-part-structure
  [::md-btn/wrapper {:impl 're-com.box/box}
   [::md-btn/popover-tooltip {:impl 're-com.popover/popover-tooltip}
    [::md-btn/tooltip {:top-level-arg? true}]]
   [::md-btn/button
    [::md-btn/icon {:tag :i}]]])

(def md-icon-button-parts-desc
  (when include-args-desc?
    (part/describe md-icon-button-part-structure)))

(def md-icon-button-parts
  (when include-args-desc?
    (-> (map :name md-icon-button-parts-desc) set)))

(def md-icon-button-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :md-icon-name     :required true  :default "zmdi-plus"   :type "string"          :validate-fn string?                       :description [:span "the name of the icon." [:br] "For example, " [:code "\"zmdi-plus\""] " or " [:code "\"zmdi-undo\""]]}
       {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?                           :description "a function which takes no params and returns nothing. Called when the button is clicked"}
       {:name :size             :required false :default :regular      :type "keyword"         :validate-fn button-size?                  :description [:span "one of " button-sizes-list]}
       {:name :emphasise?       :required false :default false         :type "boolean"                                                     :description "if true, use emphasised styling so the button really stands out"}
       {:name :disabled?        :required false :default false         :type "boolean"                                                     :description "if true, the user can't click the button"}
       {:name :pre-theme        :required false                        :type "map -> map"      :validate-fn fn?                           :description "Pre-theme function"}
       {:name :theme            :required false                        :type "map -> map"      :validate-fn fn?                           :description "Theme function"}
       {:name :class            :required false                        :type "string"          :validate-fn css-class?                    :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
       {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?                    :description "CSS styles to add or override (applies to the button, not the wrapping div)"}
       {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?                    :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
       {:name :parts            :required false                        :type "map"             :validate-fn (parts? md-icon-button-parts) :description "See Parts section below."}
       {:name :src              :required false                        :type "map"             :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
       {:name :debug-as         :required false                        :type "map"             :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
      theme/args-desc
      (part/describe-args md-icon-button-part-structure)))))

(defn md-icon-button
  "a square button containing a material design icon"
  [& {:keys [pre-theme theme debug-as]}]
  (let [showing?    (reagent/atom false)
        theme       (theme/comp pre-theme theme)
        transition! #(case %
                       :show   (reset! showing? true)
                       :hide   (reset! showing? false)
                       :toggle (swap! showing? not))]
    (fn md-icon-button-render
      [& {:keys [md-icon-name on-click size tooltip-position emphasise? disabled? src]
          :or   {md-icon-name "zmdi-plus"}
          :as   args}]
      (or
       (validate-args-macro md-icon-button-args-desc args)
       (let [part         (partial part/part md-icon-button-part-structure args)
             tooltip?     (part/get-part md-icon-button-part-structure args ::md-btn/tooltip)
             re-com       {:transition! transition!
                           :state       {:size         size
                                         :on-click     on-click
                                         :emphasise?   emphasise?
                                         :disabled?    disabled?
                                         :tooltip?     tooltip?
                                         :md-icon-name md-icon-name}}
             icon-part    (part ::md-btn/icon
                            {:theme theme
                             :props {:tag    :i
                                     :re-com re-com}})
             btn-part     (part ::md-btn/button
                            {:theme      theme
                             :post-props (select-keys args [:class :style :attr])
                             :props      {:re-com   re-com
                                          :children [icon-part]}})
             tooltip-part (part ::md-btn/tooltip {:theme theme
                                                  :props {:re-com re-com}})
             popover-part (part ::md-btn/popover-tooltip
                            {:impl  popover-tooltip
                             :theme theme
                             :props {:src      (at)
                                     :label    tooltip-part
                                     :position (or tooltip-position :below-center)
                                     :showing? showing?
                                     :anchor   btn-part}})]
         (when-not tooltip? (transition! :hide))
         (part ::md-btn/wrapper
           {:impl       box
            :theme      theme
            :post-props {:attr (debug/->attr args)}
            :props      {:src      src
                         :debug-as (or debug-as (reflect-current-component))
                         :re-com   re-com
                         :child    (if tooltip? popover-part btn-part)}}))))))

;;--------------------------------------------------------------------------------------------------
;; Component: info-button
;;--------------------------------------------------------------------------------------------------

(def info-button-part-structure
  [::info-btn/popover-tooltip {:impl 're-com.popover/popover-tooltip}
   [:info-bn/info {:top-level-arg? true}]
   [::info-btn/button
    [::info-btn/icon {:tag :svg}]]])

(def info-button-parts-desc
  (when include-args-desc?
    (part/describe info-button-part-structure)))

(def info-button-parts
  (when include-args-desc?
    (-> (map :name info-button-parts-desc) set)))

(def info-button-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :info      :required true                        :type "string | hiccup" :validate-fn string-or-hiccup?          :description "what's shown in the popover"}
       {:name :position  :required false :default :right-below :type "keyword"         :validate-fn position?                  :description [:span "relative to this anchor. One of " position-options-list]}
       {:name :width     :required false :default "250px"      :type "string"          :validate-fn string?                    :description "width in px"}
       {:name :disabled? :required false :default false        :type "boolean"                                                 :description "if true, the user can't click the button"}
       {:name :pre-theme :required false                       :type "map -> map"      :validate-fn fn?                        :description "Pre-theme function"}
       {:name :theme     :required false                       :type "map -> map"      :validate-fn fn?                        :description "Theme function"}
       {:name :class     :required false                       :type "string"          :validate-fn css-class?                 :description "CSS class names, space separated (applies to the button, not the popover wrapper)"}
       {:name :style     :required false                       :type "CSS style map"   :validate-fn css-style?                 :description "CSS styles to add or override (applies to the button, not the popover wrapper)"}
       {:name :attr      :required false                       :type "HTML attr map"   :validate-fn html-attr?                 :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the popover wrapper)"]}
       {:name :parts     :required false                       :type "map"             :validate-fn (parts? info-button-parts) :description "See Parts section below."}
       {:name :src       :required false                       :type "map"             :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
       {:name :debug-as  :required false                       :type "map"             :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
      theme/args-desc
      (part/describe-args info-button-part-structure)))))

(defn info-button
  "A tiny light grey button, with an 'i' in it. Meant to be unobtrusive.
  When pressed, displays a popup assumedly containing helpful information.
  Primarily designed to be nestled against the label of an input field, explaining the purpose of that field.
  Create a very small \"i\" icon via SVG"
  [& {:keys [pre-theme theme]}]
  (let [theme       (theme/comp pre-theme theme)
        showing?    (reagent/atom false)
        transition! #(case %
                       :show   (reset! showing? true)
                       :hide   (reset! showing? false)
                       :toggle (swap! showing? not))]
    (fn info-button-render
      [& {:keys [position width disabled? debug-as]
          :as   args}]
      (or
       (validate-args-macro info-button-args-desc args)
       (let [part        (partial part/part info-button-part-structure args)
             re-com      {:transition! transition!
                          :state       {:disabled? disabled?
                                        :showing?  @showing?}}
             info-part (part ::info-btn/info {:theme theme})
             icon-part   (part ::info-btn/icon
                           {:theme theme
                            :props {:tag      :svg
                                    :re-com   re-com
                                    :attr     {:width "11" :height "11"}
                                    :children [[:circle {:cx "5.5" :cy "5.5" :r "5.5"}]
                                               [:circle {:cx "5.5" :cy "2.5" :r "1.4" :fill "white"}]
                                               [:line {:x1     "5.5"   :y1           "5.2" :x2 "5.5" :y2 "9.7"
                                                       :stroke "white" :stroke-width "2.5"}]]}})
             button-part (part ::info-btn/button
                           {:theme      theme
                            :post-props (select-keys args [:class :style :attr])
                            :props      {:re-com   re-com
                                         :children [icon-part]}})]
         (part ::info-btn/popover-tooltip
           {:impl  popover-tooltip
            :theme theme
            :props {:src       (:src args)
                    :debug-as  (or debug-as (reflect-current-component))
                    :label     info-part
                    :status    :info
                    :position  (or position :right-below)
                    :width     (or width "250px")
                    :showing?  showing?
                    :on-cancel (handler-fn (transition! :toggle))
                    :anchor    button-part}}))))))

;;--------------------------------------------------------------------------------------------------
;; Component: row-button
;;--------------------------------------------------------------------------------------------------

(def row-button-part-structure
  [::rb/wrapper
   [::rb/popover-tooltip {:impl 're-com.popover/popover-tooltip}
    [::rb/tooltip {:top-level-arg? true}]
    [::rb/button
     [::rb/icon]]]])

(def row-button-parts-desc
  (when include-args-desc?
    (part/describe row-button-part-structure)))

(def row-button-parts
  (when include-args-desc?
    (-> (map :name row-button-parts-desc) set)))

(def row-button-args-desc
  (when include-args-desc?
    (vec
     (concat [{:name :md-icon-name     :required true  :default "zmdi-plus"   :type "string"          :validate-fn string?                   :description [:span "the name of the icon." [:br] "For example, " [:code "\"zmdi-plus\""] " or " [:code "\"zmdi-undo\""]]}
              {:name :on-click         :required false                        :type "-> nil"          :validate-fn fn?                       :description "a function which takes no params and returns nothing. Called when the button is clicked"}
              {:name :mouse-over-row?  :required false :default false         :type "boolean"                                                :description "true if the mouse is hovering over the row"}
              {:name :tooltip          :required false                        :type "string | hiccup" :validate-fn string-or-hiccup?         :description "what to show in the tooltip"}
              {:name :tooltip-position :required false :default :below-center :type "keyword"         :validate-fn position?                 :description [:span "relative to this anchor. One of " position-options-list]}
              {:name :disabled?        :required false :default false         :type "boolean"                                                :description "if true, the user can't click the button"}
              {:name :class            :required false                        :type "string"          :validate-fn css-class?                   :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
              {:name :style            :required false                        :type "CSS style map"   :validate-fn css-style?                :description "CSS styles to add or override (applies to the button, not the wrapping div)"}
              {:name :attr             :required false                        :type "HTML attr map"   :validate-fn html-attr?                :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
              {:name :parts            :required false                        :type "map"             :validate-fn (parts? row-button-parts) :description "See Parts section below."}
              {:name :src              :required false                        :type "map"             :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
              {:name :debug-as         :required false                        :type "map"             :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
             theme/args-desc
             (part/describe-args row-button-part-structure)))))

(defn row-button
  "a small button containing a material design icon"
  [& {:keys [pre-theme theme]}]
  (let [showing?    (reagent/atom false)
        transition! #(case %
                       :show   (reset! showing? true)
                       :hide   (reset! showing? false)
                       :toggle (swap! showing? not))
        theme       (theme/comp pre-theme theme)]
    (fn row-button-render
      [& {:keys [md-icon-name on-click mouse-over-row? tooltip tooltip-position disabled? parts src]
          :or   {md-icon-name "zmdi-plus"}
          :as   args}]
      (or
       (validate-args-macro row-button-args-desc args)
       (let [part         (partial part/part row-button-part-structure args)
             tooltip?     (part/get-part row-button-part-structure args ::rb/tooltip)
             re-com       {:transition! transition!
                           :state       {:mouse-over-row? (u/deref-or-value mouse-over-row?)
                                         :disabled?       (u/deref-or-value disabled?)
                                         :tooltip?        tooltip?
                                         :on-click        on-click
                                         :md-icon-name    md-icon-name}}
             icon-part    (part ::rb/icon
                            {:theme theme
                             :props {:tag    :i
                                     :src    (at)
                                     :re-com re-com}})
             button-part  (part ::rb/button
                            {:theme theme
                             :props {:src      (at)
                                     :re-com   re-com
                                     :children [icon-part]}})
             tooltip-part (part ::rb/tooltip
                            {:theme theme
                             :props {:src    (at)
                                     :re-com re-com}})
             popover-part (part ::rb/popover-tooltip
                            {:impl  popover-tooltip
                             :theme theme
                             :props {:src      (at)
                                     :re-com   re-com
                                     :label    tooltip-part
                                     :position (or tooltip-position :below-center)
                                     :showing? showing?
                                     :anchor   button-part}})]
         (when-not tooltip (transition! :hide))
         (part ::rb/wrapper
           {:impl  box
            :theme theme
            :props {:src      src
                    :re-com   re-com
                    :debug-as (reflect-current-component)
                    :child    (if tooltip? popover-part button-part)}}))))))

;;--------------------------------------------------------------------------------------------------
;; Component: hyperlink
;;--------------------------------------------------------------------------------------------------

(def hyperlink-part-structure
  [::hyperlink/wrapper {:impl 're-com.box/box}
   [::hyperlink/popover-tooltip {:impl 're-com.popover/popover-tooltip}
    [::hyperlink/tooltip {:top-level-arg? true}]]
   [::hyperlink/link {:tag :a}
    [::hyperlink/label {:top-level-arg? true}]]])

(def hyperlink-parts-desc
  (when include-args-desc?
    (part/describe hyperlink-part-structure)))

(def hyperlink-parts
  (when include-args-desc?
    (-> (map :name hyperlink-parts-desc) set)))

(def hyperlink-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :on-click         :required false                        :type "-> nil"                   :validate-fn fn?                      :description "a function which takes no params and returns nothing. Called when the button is clicked"}
       {:name :tooltip-position :required false :default :below-center :type "keyword"                  :validate-fn position?                :description [:span "relative to this anchor. One of " position-options-list]}
       {:name :disabled?        :required false :default false         :type "boolean | r/atom"                                               :description "if true, the user can't click the button"}
       {:name :class            :required false                        :type "string"                   :validate-fn css-class?                  :description "CSS class names, space separated (applies to the hyperlink, not the wrapping div)"}
       {:name :style            :required false                        :type "CSS style map"            :validate-fn css-style?               :description "CSS styles to add or override (applies to the hyperlink, not the wrapping div)"}
       {:name :attr             :required false                        :type "HTML attr map"            :validate-fn html-attr?               :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the hyperlink, not the wrapping div)"]}
       {:name :parts            :required false                        :type "map"                      :validate-fn (parts? hyperlink-parts) :description "See Parts section below."}
       {:name :src              :required false                        :type "map"                      :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
       {:name :debug-as         :required false                        :type "map"                      :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
      theme/args-desc
      (part/describe-args hyperlink-part-structure)))))

(defn hyperlink
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel"
  [& {:keys [pre-theme theme]}]
  (let [theme       (theme/comp pre-theme theme)
        showing?    (reagent/atom false)
        transition! #(case %
                       :show   (reset! showing? true)
                       :hide   (reset! showing? false)
                       :toggle (swap! showing? not))]
    (fn hyperlink-render
      [& {:keys [on-click tooltip-position disabled?] :as args}]
      (or
       (validate-args-macro hyperlink-args-desc args)
       (let [disabled?    (deref-or-value disabled?)
             part         (partial part/part hyperlink-part-structure args)
             tooltip?     (part/get-part hyperlink-part-structure args ::hyperlink/tooltip)
             re-com       {:transition! transition!
                           :state       {:disabled? disabled?
                                         :tooltip?  tooltip?
                                         :on-click  on-click}}
             label-part   (part ::hyperlink/label
                            {:theme theme
                             :props {:re-com re-com}})
             link-part    (part ::hyperlink/link
                            {:theme      theme
                             :post-props (select-keys args [:class :style :attr])
                             :props      {:tag      :a
                                          :re-com   re-com
                                          :children [label-part]}})
             tooltip-part (part ::hyperlink/tooltip
                            {:theme theme
                             :props {:re-com re-com}})
             popover-part (part ::hyperlink/popover-tooltip
                            {:impl  popover-tooltip
                             :theme theme
                             :props {:src      (at)
                                     :label    tooltip-part
                                     :position (or tooltip-position :below-center)
                                     :showing? showing?
                                     :anchor   link-part}})]
         (when-not tooltip? (transition! :hide))
         (part ::hyperlink/wrapper
           {:impl       box
            :theme      theme
            :post-props {:attr (debug/->attr args)}
            :props      {:src      (:src args)
                         :debug-as (or (:debug-as args) (reflect-current-component))
                         :re-com   re-com
                         :child    (if tooltip? popover-part link-part)}}))))))

;;--------------------------------------------------------------------------------------------------
;; Component: hyperlink-href
;;--------------------------------------------------------------------------------------------------

(def hyperlink-href-part-structure
  [::hyperlink-href/wrapper {:impl 're-com.box/box}
   [::hyperlink-href/popover-tooltip {:impl 're-com.popover/popover-tooltip}
    [::hyperlink-href/tooltip {:top-level-arg? true}]]
   [::hyperlink-href/link {:tag :a}
    [::hyperlink-href/label {:top-level-arg? true}]]])

(def hyperlink-href-parts-desc
  (when include-args-desc?
    (part/describe hyperlink-href-part-structure)))

(def hyperlink-href-parts
  (when include-args-desc?
    (-> (map :name hyperlink-href-parts-desc) set)))

(def hyperlink-href-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :href             :required true                         :type "string | r/atom"          :validate-fn string-or-atom?               :description "if specified, the link target URL"}
       {:name :target           :required false :default "_self"       :type "string | r/atom"          :validate-fn string-or-atom?               :description "one of \"_self\" or \"_blank\""}
       {:name :tooltip-position :required false :default :below-center :type "keyword"                  :validate-fn position?                     :description [:span "relative to this anchor. One of " position-options-list]}
       {:name :disabled?        :required false :default false         :type "boolean | r/atom"                                                    :description "if true, the user can't click the button"}
       {:name :class            :required false                        :type "string"                   :validate-fn css-class?                       :description "CSS class names, space separated (applies to the hyperlink, not the wrapping div)"}
       {:name :style            :required false                        :type "CSS style map"            :validate-fn css-style?                    :description "CSS styles to add or override (applies to the hyperlink, not the wrapping div)"}
       {:name :attr             :required false                        :type "HTML attr map"            :validate-fn html-attr?                    :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the hyperlink, not the wrapping div)"]}
       {:name :parts            :required false                        :type "map"                      :validate-fn (parts? hyperlink-href-parts) :description "See Parts section below."}
       {:name :src              :required false                        :type "map"                      :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
       {:name :debug-as         :required false                        :type "map"                      :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]
      theme/args-desc
      (part/describe-args hyperlink-href-part-structure)))))

(defn hyperlink-href
  "Renders an underlined text hyperlink component.
   This is very similar to the button component above but styled to looks like a hyperlink.
   Useful for providing button functionality for less important functions, e.g. Cancel"
  [& {:keys [pre-theme theme]}]
  (let [theme       (theme/comp pre-theme theme)
        showing?    (reagent/atom false)
        transition! #(case %
                       :show   (reset! showing? true)
                       :hide   (reset! showing? false)
                       :toggle (swap! showing? not))]
    (fn hyperlink-href-render
      [& {:keys [href target tooltip-position disabled?] :as args}]
      (or
       (validate-args-macro hyperlink-href-args-desc args)
       (let [href        (u/deref-or-value href)
             target     (u/deref-or-value target)
             disabled?  (u/deref-or-value disabled?)
             part       (partial part/part hyperlink-href-part-structure args)
             tooltip?   (part/get-part hyperlink-href-part-structure args ::hyperlink-href/tooltip)
             re-com     {:transition! transition!
                         :state       {:disabled? disabled?
                                       :tooltip?  tooltip?
                                       :href      href
                                       :target    target}}
             label-part (part ::hyperlink-href/label
                          {:theme theme
                           :props {:re-com re-com}})
             link-part  (part ::hyperlink-href/link
                          {:theme      theme
                           :post-props (select-keys args [:class :style :attr])
                           :props      {:tag      :a
                                        :re-com   re-com
                                        :children [label-part]}})
             tooltip-part (part ::hyperlink-href/tooltip
                            {:theme theme
                             :props {:re-com re-com}})
             popover-part (part ::hyperlink-href/popover-tooltip
                            {:impl  popover-tooltip
                             :theme theme
                             :props {:src      (at)
                                     :label    tooltip-part
                                     :position (or tooltip-position :below-center)
                                     :showing? showing?
                                     :anchor   link-part}})]
         (when-not tooltip? (transition! :hide))
         (part ::hyperlink-href/wrapper
           {:impl       box
            :theme      theme
            :post-props {:attr (debug/->attr args)}
            :props      {:src      (:src args)
                         :debug-as (or (:debug-as args) (reflect-current-component))
                         :re-com   re-com
                         :child    (if tooltip? popover-part link-part)}}))))))
