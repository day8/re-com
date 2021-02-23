(ns re-com.selection-list
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [re-com.config       :refer [include-args-desc?]]
    [re-com.debug        :refer [->attr]]
    [re-com.text         :refer [label]]
    [re-com.checkbox     :refer [checkbox]]
    [re-com.radio-button :refer [radio-button]]
    [re-com.box          :refer [box border h-box v-box]]
    [re-com.validate     :refer [vector-of-maps? string-or-atom? set-or-atom? css-style? html-attr? parts?]]
    [re-com.util         :refer [fmap deref-or-value]]))

;; ----------------------------------------------------------------------------
(defn label-style
  ([selected? as-exclusions?]
   (label-style selected? as-exclusions? nil))

  ([selected? as-exclusions? selected-color]
    ;;TODO: margin-top required because currently checkbox & radio-button don't center label
   (let [base-style {:margin-top "1px"}
          base-style (if (and selected? as-exclusions?)
                       (merge base-style {:text-decoration "line-through"})
                       base-style)
          base-style (if (and selected? selected-color)
                       (merge base-style {:color selected-color})
                       base-style)]
     base-style)))


(defn- check-clicked
  [selections item-id ticked? required?]
  (let [num-selected (count selections)
        only-item    (when (= 1 num-selected) (first selections))]
    (if (and required? (= only-item item-id))
      selections  ;; prevent unselect of last item
      (if ticked? (conj selections item-id) (disj selections item-id)))))

(defn- as-checked
  [item id-fn selections on-change disabled? label-fn required? as-exclusions? parts]
  ;;TODO: Do we really need an anchor now that bootstrap styles not realy being used ?
  (let [item-id (id-fn item)]
    [box
     :class (str "list-group-item compact rc-selection-list-group-item " (get-in parts [:list-group-item :class]))
     :style (get-in parts [:list-group-item :style] {})
     :attr  (merge
              {:on-click (handler-fn (when-not disabled?
                                       (on-change (check-clicked selections item-id (not (selections item-id)) required?))))}
              (get-in parts [:list-group-item :attr]))
     :child [checkbox
             :src         (at)
             :class       (str "rc-selection-list-checkbox " (get-in parts [:checkbox :class]))
             :style       (get-in parts [:checkbox :style])
             :attr        (get-in parts [:checkbox :attr])
             :model       (some? (selections item-id))
             :on-change   #()                                 ;; handled by enclosing box
             :disabled?   disabled?
             :label-style (label-style (selections item-id) as-exclusions?)
             :label       (label-fn item)]]))


(defn- radio-clicked
  [selections item-id]
  (if (selections item-id) #{} #{item-id}))

(defn- as-radio
  [item id-fn selections on-change disabled? label-fn required? as-exclusions? parts]
  (let [item-id (id-fn item)]
    [box
     :class (str "list-group-item compact rc-selection-list-group-item " (get-in parts [:list-group-item :class]))
     :style (get-in parts [:list-group-item :style] {})
     :attr  (merge {:on-click (handler-fn (when-not disabled?
                                            ;; prevents on-change from firing if unselect is disabled (required?)
                                            ;; and the item clicked is already selected.
                                            (when-not (and required? (selections item-id))
                                              (on-change (radio-clicked selections item-id)))))}
                   (get-in parts [:list-group-item :attr]))
     :child [radio-button
             :src         (at)
             :class       (str "rc-selection-list-radio-button " (get-in parts [:radio-button :class]))
             :style       (merge (get-in parts [:radio-button :style] {})
                                 (when disabled?
                                       {:pointer-events "none"}))
             :attr        (get-in parts [:radio-button :attr])
             :model       (first selections)
             :value       item-id
             :on-change   #()                                 ;; handled by enclosing box
             :disabled?   disabled?
             :label-style (label-style (selections item-id) as-exclusions?)
             :label       (label-fn item)]]))


(def list-style
  ;;TODO: These should be in CSS resource
  {:overflow-x     "hidden"
   :overflow-y     "auto"}) ;;TODO this should be handled by scroller later

(def spacing-bordered
  {:padding-top    "0px"
   :padding-bottom "0px"
   :padding-left   "5px"
   :padding-right  "5px"
   :margin-top     "5px"
   :margin-bottom  "5px"})

(def spacing-unbordered
  {:padding-left   "0px"
   :padding-right  "5px"
   :padding-top    "0px"
   :padding-bottom "0px"
   :margin-top     "0px"
   :margin-bottom  "0px"})

(def selection-list-parts-desc
  (when include-args-desc?
    [{:type :legacy          :level 0 :class "rc-selection-list"              :impl "[selection-list]" :notes "Outer wrapper for the selection list."}
     {:name :list-group      :level 1 :class "rc-selection-list-group"        :impl "[:div]"           :notes "Container for the selection list items."}
     {:name :list-group-item :level 2 :class "rc-selection-list-group-item"   :impl "[box]"}
     {:name :checkbox        :level 3 :class "rc-selection-list-checkbox"     :impl "[checkbox]"}
     {:name :radio-button    :level 3 :class "rc-selection-list-radio-button" :impl "[radio-button]"}]))

(def selection-list-parts
  (when include-args-desc?
    (-> (map :name selection-list-parts-desc) set)))

(def selection-list-args-desc
  (when include-args-desc?
    [{:name :choices        :required true                  :type "vector of choices | r/atom"           :validate-fn vector-of-maps?               :description [:span "the selectable items. Elements can be strings or more interesting data items like {:label \"some name\" :sort 5}. Also see " [:code ":label-fn"] " below (list of maps also allowed)"]}
     {:name :model          :required true                  :type "set of :ids within :choices | r/atom" :validate-fn set-or-atom?                  :description "the currently selected items. Note: items are considered distinct"}
     {:name :on-change      :required true                  :type "set of :ids -> nil | r/atom"          :validate-fn fn?                           :description [:span "a callback which will be passed set of the ids (as defined by " [:code ":id-fn"] ") of the selected items"]}
     {:name :id-fn          :required false :default :id    :type "choice -> anything"                   :validate-fn ifn?                          :description [:span "given an element of " [:code ":choices"] ", returns its unique identifier (aka id)"]}
     {:name :label-fn       :required false :default :label :type "choice -> anything"                   :validate-fn ifn?                          :description [:span "given an element of " [:code ":choices"] ", returns its displayable label"]}
     {:name :multi-select?  :required false :default true   :type "boolean | r/atom"                                                                :description "when true, use check boxes, otherwise radio buttons"}
     {:name :as-exclusions? :required false :default false  :type "boolean | r/atom"                                                                :description "when true, selected items are shown with struck-out labels"}
     {:name :required?      :required false :default false  :type "boolean | r/atom"                                                                :description "when true, at least one item must be selected. Note: being able to un-select a radio button is not a common use case, so this should probably be set to true when in single select mode"}
     {:name :width          :required false                 :type "string | r/atom"                      :validate-fn string-or-atom?               :description "a CSS style e.g. \"250px\". When specified, item labels may be clipped. Otherwise based on widest label"}
     {:name :height         :required false                 :type "string | r/atom"                      :validate-fn string-or-atom?               :description "a CSS style e.g. \"150px\". Size beyond which items will scroll"}
     {:name :max-height     :required false                 :type "string | r/atom"                      :validate-fn string-or-atom?               :description "a CSS style e.g. \"150px\". If there are less items then this height, box will shrink. If there are more, items will scroll"}
     {:name :disabled?      :required false :default false  :type "boolean | r/atom"                                                                :description "when true, the time input will be disabled. Can be atom or value"}
     {:name :hide-border?   :required false :default false  :type "boolean | r/atom"                                                                :description "when true, the list will be displayed without a border"}
     {:name :item-renderer  :required false                 :type "-> nil | r/atom"                      :validate-fn fn?                           :description "a function which takes no params and returns nothing. Called for each element during setup, the returned component renders the element, responds to clicks etc."}
     {:name :class          :required false                 :type "string"                               :validate-fn string?                       :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style          :required false                 :type "CSS style map"                        :validate-fn css-style?                    :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr           :required false                 :type "HTML attr map"                        :validate-fn html-attr?                    :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts          :required false                 :type "map"                                  :validate-fn (parts? selection-list-parts) :description "See Parts section below."}
     {:name :src            :required false                 :type "map"                                  :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as       :required false                 :type "map"                                  :validate-fn map?                          :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

;;NOTE: Consumer has complete control over what is selected or not. A current design tradeoff
;;      causes all selection changes to trigger a complete list re-render as a result of on-change callback.
;;      this approach may be not ideal for very large list choices.
(defn selection-list
  "Produce a list box with items arranged vertically"
  [& {:keys [choices model on-change id-fn label-fn multi-select? as-exclusions? required? width height max-height disabled? hide-border? item-renderer class style attr parts src debug-as]
      :or   {multi-select?  true
             as-exclusions? false
             required?      false
             disabled?      false
             hide-border?   false
             id-fn          :id
             label-fn       :label}
      :as   args}]
  (or
    (validate-args-macro selection-list-args-desc args)
    (let [choices        (deref-or-value choices)
          model          (deref-or-value model)
          on-change      (deref-or-value on-change)
          multi-select?  (deref-or-value multi-select?)
          as-exclusions? (deref-or-value as-exclusions?)
          required?      (deref-or-value required?)
          width          (deref-or-value width)
          height         (deref-or-value height)
          max-height     (deref-or-value max-height)
          disabled?      (deref-or-value disabled?)
          hide-border?   (deref-or-value hide-border?)
          item-renderer  (deref-or-value item-renderer)
          selected       (if multi-select? model (-> model first vector set))
          items          (map (if item-renderer
                                #(item-renderer % id-fn selected on-change disabled? label-fn required? as-exclusions?)  ;; TODO do we need to pass id-fn?
                                (if multi-select?
                                  #(as-checked % id-fn selected on-change disabled? label-fn required? as-exclusions? parts)
                                  #(as-radio % id-fn selected on-change disabled? label-fn required? as-exclusions? parts)))
                              choices)
          bounds         (select-keys args [:width :height :max-height])
          spacing        (if hide-border? spacing-unbordered spacing-bordered)]
      ;; In single select mode force selections to one. This causes a second render
      ;; TODO: GR commented this out to fix the bug where #{nil} was being returned for an empty list. Remove when we're sure there are no ill effects.
      #_(when-not (= selected model) (on-change selected))
      [border
       :src      src
       :debug-as (or debug-as (reflect-current-component))
       :class    (str "rc-selection-list "
                      (when (deref-or-value disabled?) "rc-disabled")
                      class)
       :style    style
       :attr     attr
       :radius   "4px"
       :border   (when hide-border? "none")
       :child    (into [:div
                        (merge
                          {:class (str "list-group noselect rc-selection-list-group " (get-in parts [:list-group :class]))
                           :style (merge
                                    list-style
                                    bounds
                                    spacing
                                    (get-in parts [:list-group :style]))}
                          (get-in parts [:list-group :attr]))]
                       items)])))


