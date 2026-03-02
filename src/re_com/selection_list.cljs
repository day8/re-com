(ns re-com.selection-list
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   [re-com.args         :as args]
   [re-com.config       :refer [include-args-desc?]]
   [re-com.checkbox     :refer [checkbox]]
   [re-com.radio-button :refer [radio-button]]
   [re-com.part         :as p]
   [re-com.box          :refer [border h-box]]
   [re-com.validate     :refer [vector-of-maps? string-or-atom? set-or-atom?]]
   [re-com.theme        :as theme]
   [re-com.util         :refer [deref-or-value]]
   re-com.selection-list.theme
   [re-com.selection-list :as-alias sl]))

(defn- label-style [selected? as-exclusions?]
  (cond-> {:margin-top "1px"}
    (and selected? as-exclusions?) (assoc :text-decoration "line-through")))

(def part-structure
  [::sl/wrapper {:impl 're-com.box/border :notes "Outer wrapper for the selection list."}
   [::sl/list-group {:tag :div :notes "Container for the selection list items."}
    [::sl/list-group-item {:impl 're-com.box/h-box}
     [::sl/item-content
      [::sl/label]
      [::sl/checkbox {:impl 're-com.checkbox/checkbox}]
      [::sl/radio-button {:impl 're-com.radio-button/radio-button}]]
     [::sl/only-button
      {:tag :button
       :notes "The 'only' button displayed next to each item when :show-only-button? is true"}]]]])

(defn only-button-part
  [{:keys [on-change id-fn choice disabled?]
    {part :part-fn theme :theme} :re-com}]
  (let [item-id (id-fn choice)]
    (part ::sl/only-button
      {:impl  p/default
       :theme theme
       :props {:tag      :button
               :attr     {:disabled disabled?
                          :on-click (handler-fn
                                    (.stopPropagation event)
                                    (when (not disabled?)
                                      (on-change #{item-id})))}
               :children ["only"]}})))

(defn checkbox-part
  [{:keys [choice id-fn selected disabled? label-content as-exclusions?]
    {part :part-fn theme :theme} :re-com}]
  (let [item-id (id-fn choice)]
    (part ::sl/checkbox
      {:impl  checkbox
       :theme theme
       :props {:src         (at)
               :model       (some? (selected item-id))
               :on-change   #()
               :disabled?   disabled?
               :label-style (label-style (selected item-id) as-exclusions?)
               :label       {:children [label-content]}}})))

(defn radio-part
  [{:keys [choice id-fn selected disabled? label-content as-exclusions?]
    {part :part-fn theme :theme} :re-com}]
  (let [item-id   (id-fn choice)
        selected? (selected item-id)]
    (part ::sl/radio-button
      {:impl  radio-button
       :theme theme
       :props {:src         (at)
               :style       (when disabled? {:pointer-events "none"})
               :model       (first selected)
               :value       item-id
               :on-change   #()
               :disabled?   disabled?
               :label-style (label-style selected? as-exclusions?)
               :label       {:children [label-content]}}})))

(def parts-desc
  (when include-args-desc?
    (p/describe part-structure)))

(def parts
  (when include-args-desc?
    (-> (map :name parts-desc) set)))

(def args-desc
  (when include-args-desc?
    (into
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
      {:name :disabled?      :required false :default false  :type "boolean | r/atom"                                                                :description "when true, the selection list will be disabled. Can be atom or value"}
      {:name :hide-border?   :required false :default false  :type "boolean | r/atom"                                                                :description "when true, the list will be displayed without a border"}
      {:name :item-renderer  :required false                 :type "choice, id-fn, selected, on-change, disabled?, label-fn, required?, as-exclusions? -> hiccup | r/atom"                      :validate-fn fn?                           :description "a function which takes choice, id-fn, selected, on-change, disabled?, label-fn, required? and as-exclusions? and returns hiccup. Called for each element, the returned component renders the element and responds to clicks"}
      {:name :show-only-button?   :required false :default false  :type "boolean | r/atom"                                                                :description "when true, an 'only' button will be displayed next to each item, allowing the user to select only that item"}]
     (concat
      args/std
      (p/describe-args part-structure)))))

;;NOTE: Consumer has complete control over what is selected or not. A current design tradeoff
;;      causes all selection changes to trigger a complete list re-render as a result of on-change callback.
;;      this approach may be not ideal for very large list choices.
(defn selection-list
  "Produce a list box with items arranged vertically"
  [& {:keys [pre-theme theme]
      :as   args}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [choices model on-change id-fn label-fn multi-select? as-exclusions? required?
                  width height max-height disabled? hide-border? item-renderer show-only-button?
                  class style attr parts src debug-as]
           :or   {multi-select?     true
                  as-exclusions?    false
                  required?         false
                  disabled?         false
                  hide-border?      false
                  show-only-button? false
                  id-fn             :id
                  label-fn          :label}
           :as args}]
      (or
       (validate-args-macro args-desc args)
       (let [choices           (deref-or-value choices)
             model             (deref-or-value model)
             on-change         (deref-or-value on-change)
             multi-select?     (deref-or-value multi-select?)
             as-exclusions?    (deref-or-value as-exclusions?)
             required?         (deref-or-value required?)
             disabled?         (deref-or-value disabled?)
             hide-border?      (deref-or-value hide-border?)
             item-renderer     (deref-or-value item-renderer)
             show-only-button? (deref-or-value show-only-button?)
             part              (partial p/part part-structure args)
             selected          (if multi-select? model (-> model first vector set))
             re-com-ctx        {:part-fn part
                                :theme   theme
                                :state   {:border      (if hide-border? :hidden :shown)
                                          :interaction (if disabled? :disabled :enabled)}}
             list-group-items
             (for [choice choices
                   :let   [item-id (id-fn choice)
                           selected? (selected item-id)
                           label-content
                           (p/part (or (p/get-part part-structure args ::sl/label)
                                       (label-fn choice))
                             {:theme theme
                              :part  ::sl/label
                              :props (assoc choice :children [(label-fn choice)])})
                           on-change-checkbox
                           (handler-fn
                            (when-not disabled?
                              (let [only-item? (and (= 1 (count selected))
                                                    (= item-id (first selected)))]
                                (when-not (and required? only-item?)
                                  (if (selected item-id)
                                    (on-change (disj selected item-id))
                                    (on-change (conj selected item-id)))))))
                           on-change-radio
                           (handler-fn (when-not disabled?
                                         (when-not (and required? selected?)
                                           (on-change (if selected? #{} #{item-id})))))
                           props
                           {:choice         choice
                            :id-fn          id-fn
                            :selected       selected
                            :on-change      on-change
                            :disabled?      disabled?
                            :label-fn       label-fn
                            :label-content  label-content
                            :required?      required?
                            :as-exclusions? as-exclusions?
                            :re-com         re-com-ctx}]]
               (part ::sl/list-group-item
                 {:theme theme
                  :key   (id-fn choice)
                  :impl  h-box
                  :props {:re-com re-com-ctx
                          :attr {:on-click (if multi-select? on-change-checkbox on-change-radio)}
                          :children
                          [(part ::sl/item-content
                             {:props props
                              :theme theme
                              :impl  (or (when item-renderer
                                           (fn [{:keys [choice id-fn selected on-change disabled?
                                                        label-fn required? as-exclusions?]}]
                                             [item-renderer choice id-fn selected on-change disabled?
                                              label-fn required? as-exclusions?]))
                                         (when multi-select? checkbox-part)
                                         radio-part)})
                           (when (and multi-select? show-only-button?)
                             [only-button-part props])]}}))]
         (part ::sl/wrapper
           {:impl       border
            :theme      theme
            :props      {:re-com re-com-ctx
                         :child  (part ::sl/list-group
                                   {:theme      theme
                                    :post-props {:style (select-keys args [:width :height :max-height])}
                                    :props      {:re-com   re-com-ctx
                                                 :children list-group-items}})}
            :post-props (merge (select-keys args [:class :style :attr])
                               {:src      src
                                :debug-as (or debug-as
                                              (reflect-current-component))})}))))))
