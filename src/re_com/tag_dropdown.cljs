(ns re-com.tag-dropdown
  (:require-macros
    [reagent.ratom   :refer [reaction]]
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [goog.string           :as gstring]
    [reagent.core          :as reagent]
    [re-com.config         :refer [include-args-desc?]]
    [re-com.debug          :refer [->attr]]
    [re-com.util           :refer [deref-or-value px-n assoc-in-if-empty merge-css add-map-to-hiccup-call flatten-attr]]
    [re-com.validate       :as validate :refer [parts?]]
    [re-com.box            :refer [box h-box v-box gap]]
    [re-com.checkbox       :refer [checkbox]]
    [re-com.selection-list :as    selection-list]
    [re-com.close-button   :refer [close-button]]
    [re-com.popover        :refer [popover-content-wrapper popover-anchor-wrapper]]))

;; TODO: [GR] Ripped off from re-com.selection-list
(defn label-style
  ([selected? as-exclusions?]
   (label-style selected? as-exclusions? nil))

  ([selected? as-exclusions? selected-color]
    ;;TODO: margin-top required because currently checkbox & radio-button don't center label
   (let [base-style {:margin-top "1px"
                     :color "#333"}
         base-style (if (and selected? as-exclusions?)
                      (merge base-style {:text-decoration "line-through"})
                      base-style)
         base-style (if (and selected? selected-color)
                      (merge base-style {:color selected-color})
                      base-style)]
     base-style)))

(declare tag-dropdown-css-spec)

(defn text-tag
  []
  (let [over? (reagent/atom false)]
    (fn text-tag-render
      [& {:keys [tag-data on-click on-unselect tooltip id-fn label-fn description-fn hover-style disabled? class style attr parts]
          :or   {id-fn          :id
                 label-fn       :label}
          :as args}]
      (let [tag-id        (id-fn tag-data)
            tag-id-kw     (keyword tag-id)
            clickable?    (and (some? on-click) (not disabled?))
            unselectable? (and (some? on-unselect) (not disabled?))
            placeholder?  (= (:id tag-data) :$placeholder$)
            tag-label     (label-fn tag-data)
            tag-description (when description-fn (description-fn tag-data))
            cmerger (merge-css tag-dropdown-css-spec args)]
        (add-map-to-hiccup-call
         (cmerger :tag {:class (get-in parts [tag-id-kw :class])
                        :style (get-in parts [tag-id-kw :style])
                        :attr (get-in parts [tag-id-kw :attr])})
         [v-box
          :src      (at)
          :align :start
          :children [(add-map-to-hiccup-call
                      (cmerger :tag-box
                               {:background-color (:background-color tag-data)
                                :disabled? disabled?
                                :placeholder? placeholder?
                                :style (when (and @over? (not disabled?)) hover-style)
                                :attr {:title          tooltip
                                       :on-click       (handler-fn (when (and placeholder? (not disabled?)) (on-click (:id tag-data))))
                                       :on-mouse-enter (handler-fn (reset! over? true))
                                       :on-mouse-leave (handler-fn (reset! over? false))}})
                      [h-box
                       :src        (at)
                       :align-self :start
                       :justify    (if placeholder? :end :center)

                                        ;:width    (if placeholder? (:width tag-data) width)

                       :min-width (when placeholder? (:width tag-data))

                       :padding "0px 4px"
                       :margin  (px-n 2 (if placeholder? 0 6) 2 0)
                       :children [(if placeholder?
                                    (add-map-to-hiccup-call
                                     (cmerger :tag-label {:placeholder? placeholder?})
                                     [box
                                      :src   (at)
                                      :child (gstring/unescapeEntities "&#9660;")])
                                    (add-map-to-hiccup-call
                                     (cmerger :tag-label {:placeholder? placeholder?
                                                          :clickable? clickable?})
                                     [box
                                      :src   (at)
                                      :attr  {:on-click (handler-fn
                                                         (when clickable?
                                                           (on-click (:id tag-data)))
                                                         #_(.stopPropagation event))}
                                      :child (or tag-label "???")]))
                                  (when (and unselectable? (not placeholder?))
                                    [h-box
                                     :src      (at)
                                     :align    :center
                                     :children [(add-map-to-hiccup-call
                                                 (cmerger :tag-close-spacer)
                                                 [box
                                                  :src   (at)
                                                  :child "|"])
                                                [close-button
                                                 :src         (at)
                                                 :color       "white"
                                                 :hover-color "#ccc"
                                                 :div-size    13
                                                 :font-size   13
                                                 :top-offset  1
                                                 :on-click    #(when unselectable?
                                                                 (on-unselect (:id tag-data)))]]])]])
                     (when tag-description
                       [:span
                        (flatten-attr
                         (cmerger :tag-description))
                        tag-description])]])))))

(def tag-dropdown-exclusive-parts-desc
  (when include-args-desc?
    [{:name :popover-anchor-wrapper :level 0 :class "rc-tag-dropdown-popover-anchor-wrapper" :impl "[popover-anchor-wrapper]"}
     {:name :main                   :level 1 :class "rc-tag-dropdown"                        :impl "[h-box]"}
     {:name :tags                   :level 2 :class "rc-tag-dropdown-tags"                   :impl "[h-box]"}
     {:name :tag                    :level 3 :class "rc-tag-dropdown-tag"                    :impl "[h-box]" :notes [:span "Each individual tag can be independently targeted with the keyword of its " [:code ":id"]]}
     {:name :selection-list         :level 2 :class "rc-tag-dropdown-selection-list"         :impl "[selection-list]"}]))

(def tag-dropdown-parts-desc
  (when include-args-desc?
    (into
      tag-dropdown-exclusive-parts-desc
      (->>
        selection-list/selection-list-parts-desc
        (remove #(= :legacy (:type %)))
        (map #(update % :level (comp inc inc)))))))

(def tag-dropdown-css-spec
  {:main {:class ["rc-tag-dropdown"]
          :style (fn [{:keys [disabled?]}]
                   {:background-color (if disabled? "#EEE" "white")
                    :color            "#BBB"
                    :border           "1px solid lightgrey"
                    :border-radius    "2px"
                    :overflow         "hidden"
                    :cursor           (if disabled? "default" "pointer")})}
   :tags {:class ["rc-tag-dropdown-tags"]
          :style {:overflow "hidden"}}

   :tag {:class ["rc-tag-dropdown-tag"]}
   :tag-box {:class ["noselect" "rc-text-tag"]
             :style (fn [{:keys [background-color disabled? placeholder?]}]
                      {:color "white"
                       :background-color background-color
                       :cursor (if disabled? "default" "pointer")
                       :font-size "12px"
                       :border (when placeholder? "1px dashed #828282")
                       :border-radius "3px"})}
   :tag-label {:style (fn [{:keys [placeholder? clickable?]}]
                        (if placeholder?
                          {:color "hsl(194, 61%, 85%)"}
                          (when clickable? {:cursor "pointer"})))}
   :tag-close-spacer {:style {:margin-left "4px"
                              :margin-right "3px"}}
   :tag-description {:style {:color "#586069"}}

   :close-button-wrapper {:style {:margin-left "5px"}}
   :selection-list {:class ["rc-tag-dropdown-selection-list"]}
   :popover-anchor-wrapper {:class ["rc-tag-dropdown-popover-anchor-wrapper"]}})

(def tag-dropdown-parts
  (when include-args-desc?
    (-> (map :name tag-dropdown-parts-desc) set)))

(def tag-dropdown-args-desc
  (when include-args-desc?
    [{:name :choices            :required true                          :type "vector of maps | r/atom" :validate-fn validate/vector-of-maps?    :description [:span "Each map represents a choice. Values corresponding to id, label, short label and tag background color are extracted by the functions " [:code ":id"] ", " [:code ":label-fn"] " & " [:code ":short-label-fn"]  " & " [:code ":background-color"] ". See below."]}
     {:name :model              :required true                          :type "a set of ids | r/atom"                                            :description [:span "The set of the ids for currently selected choices. If nil or empty, see " [:code ":placeholder"] "."]}
     {:name :placeholder        :required false                         :type "string"                  :validate-fn string?                     :description "Background text shown when there's no selection."}
     {:name :on-change          :required true                          :type "id -> nil"               :validate-fn fn?                         :description [:span "This function is called whenever the selection changes. Called with one argument, the set of selected ids. See " [:code ":model"] "."]}
     {:name :disabled?          :required false :default false          :type "boolean"                                                          :description "if true, no user selection is allowed"}
     {:name :required?          :required false :default false          :type "boolean | r/atom"                                                 :description "when true, at least one item must be selected."}
     {:name :unselect-buttons?  :required false :default false          :type "boolean"                                                          :description "When true, \"X\" buttons will be added to the display of selected tags (on the right), allowing the user to directly unselect them."}
     {:name :label-fn           :required false :default ":label"       :type "map -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :description-fn     :required false :default ":description" :type "map -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable description. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :abbrev-fn          :required false                         :type "choice -> hiccup"        :validate-fn ifn?                        :description [:span "A function which can turn a choice into an abbreviated label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :abbrev-threshold   :required false                         :type "number"                  :validate-fn number?                     :description [:span "The text displayed for selected choices is obtained via either " [:code ":label-fn"] " or " [:code "abbrev-fn"] ". When the total number of characters displayed is less than this argument then " [:code ":label-fn"] " will be used, otherwise " [:code "abbrev-fn"] ". You should set this value taking into account the width of this component. If not set, only " [:code ":label-fn"] " is used."]}
     {:name :min-width          :required false                         :type "string"                  :validate-fn string?                     :description [:span "the CSS min-width, like \"100px\" or \"20em\". This is the natural display width of the Component. It prevents the width from becoming smaller than the value specified, yet allows growth horizontally if sufficient choices are selected up to " [:code ":max-width"] " or unbounded growth if " [:code ":max-width"] " is not provided."]}
     {:name :max-width          :required false                         :type "string"                  :validate-fn string?                     :description "the CSS max-width, like \"100px\" or \"20em\". It prevents the width from becoming larger than the value specified. If sufficient choices are selected to go beyond the maximum then some choices will be hidden by overflow."}
     {:name :height             :required false :default "25px"         :type "string"                  :validate-fn string?                     :description "the specific height of the component"}
     {:name :style              :required false                         :type "map"                     :validate-fn map?                        :description "CSS styles to add or override"}
     {:name :parts              :required false                         :type "map"                     :validate-fn (parts? tag-dropdown-parts) :description "See Parts section below."}
     {:name :src                :required false                         :type "map"                     :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as           :required false                         :type "map"                     :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn tag-dropdown
  [& {:as args}]
  (or
    (validate-args-macro tag-dropdown-args-desc args)
    (let [showing?      (reagent/atom false)]
      (fn tag-dropdown-render
        [& {:keys [choices model placeholder on-change unselect-buttons? required? abbrev-fn abbrev-threshold label-fn
                   description-fn min-width max-width height style disabled? parts src debug-as]
            :or   {label-fn          :label
                   description-fn    :description
                   height            "25px"}
            :as   args}]
        (or
          (validate-args-macro tag-dropdown-args-desc args)
          (let [choices            (deref-or-value choices)
                model              (deref-or-value model)
                abbrev-threshold   (deref-or-value abbrev-threshold)
                required?          (deref-or-value required?)
                disabled?          (deref-or-value disabled?)
                unselect-buttons?  (deref-or-value unselect-buttons?)

                choices-num-chars  (reduce
                                     (fn [n choice]
                                       (if (contains? model (:id choice))
                                         (+ n (count (label-fn choice)))
                                         n))
                                     0
                                     choices)
                abbrev?            (and (>= choices-num-chars abbrev-threshold)
                                        (number? abbrev-threshold)
                                        (fn? abbrev-fn))
                cmerger (merge-css tag-dropdown-css-spec args)

                placeholder-tag [text-tag
                                 :tag-data    {:id               :$placeholder$
                                               :label            ""
                                               :background-color "white"
                                               :width            (if abbrev? "20px" "40px")}
                                 :on-click    #(reset! showing? true)
                                 :tooltip     "Click to select tags"
                                 :hover-style {:background-color "#eee"}]
                tag-list-body   (add-map-to-hiccup-call
                                 (cmerger :selection-list)
                                 [selection-list/selection-list
                                  :src           (at)
                                  :disabled?     disabled?
                                  :required?     required?
                                  :parts         (->
                                                  (select-keys parts selection-list/selection-list-parts)
                                                  (assoc-in-if-empty [:list-group-item :style :border] "1px solid #ddd")
                                                  (assoc-in-if-empty [:list-group-item :style :height] "auto")
                                                  (assoc-in-if-empty [:list-group-item :style :padding] "10px 15px"))
                                  :choices       choices
                                  :hide-border?  true
                                  :label-fn      (fn [tag]
                                                   [text-tag
                                                    :label-fn       label-fn
                                                    :description-fn description-fn
                                                    :tag-data       tag
                                                    :style          style])
                                  :model         model
                                  :on-change     #(on-change %)
                                  :multi-select? true])
                tag-main        (add-map-to-hiccup-call
                                 (cmerger :main {:disabled? disabled?
                                                 :attr (when (not disabled?)
                                                         {:on-click (handler-fn (reset! showing? true))})})
                                 [h-box
                                  :src       (at)
                                  :min-width min-width
                                  :max-width max-width
                                  :height    height
                                  :align     :center
                                  :padding   "0px 6px"
                                  :children  [(add-map-to-hiccup-call
                                               (cmerger :tags)
                                               [h-box
                                                :src      (at)
                                                :size     "1" ;; This line will align the tag placeholder to the right
                                                :children (conj
                                                           (mapv (fn [tag]
                                                                   (when (contains? model (:id tag))
                                                                     [text-tag
                                                                      :label-fn    (if abbrev? abbrev-fn label-fn)
                                                                      :tag-data    tag
                                                                      :tooltip     (:label tag)
                                                                      :disabled?   disabled?
                                                                      :on-click    #(reset! showing? true)      ;; Show dropdown
                                                                      :on-unselect (when (and unselect-buttons? (not (and (= 1 (count model)) required?))) #(on-change (disj model %)))
                                                                      :hover-style {:opacity "0.8"}
                                                                      :style       style
                                                                      :parts       parts]))
                                                                 choices)
                                                           (when (not disabled?)
                                                             placeholder-tag)
                                                           [gap
                                                            :src  (at)
                                                            :size "20px"]
                                                           (when (zero? (count model))
                                                             [box
                                                              :src   (at)
                                                              :child (if placeholder placeholder "")]))])
                                              (when (and (not-empty model) (not disabled?)
                                                         (not required?))
                                                [close-button
                                                 :src       (at)
                                                 :parts     {:wrapper (cmerger :close-button-wrapper)}
                                                 :on-click  #(on-change #{})])]])]
            (add-map-to-hiccup-call
             (cmerger :popover-anchor-wrapper)
             [popover-anchor-wrapper
              :src      src
              :debug-as (or debug-as (reflect-current-component))
              :showing? showing?
              :position :below-center
              :anchor   tag-main
              :popover  [popover-content-wrapper
                         :src             (at)
                         :arrow-length    0
                         :arrow-width     0
                         :arrow-gap       1
                         :no-clip?        true
                         :on-cancel       #(reset! showing? false)
                         :padding         "19px 19px"
                         :body            tag-list-body]])))))))
