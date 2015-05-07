(ns re-com.selection-list
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.text     :refer [label]]
            [re-com.misc     :refer [checkbox radio-button]]
            [re-com.box      :refer [box border h-box v-box]]
            [re-com.validate :refer [vector-of-maps? string-or-atom? set-or-atom?] :refer-macros [validate-args-macro]]
            [re-com.util     :refer [fmap deref-or-value]]))

;; ----------------------------------------------------------------------------
(defn label-style [selected? as-exclusions?]
  ;;TODO: margin-top required because currently checkbox & radio-button don't center label
  (let [base-style {:margin-top "1px"}]
    (if (and selected? as-exclusions?)
      (merge base-style {:text-decoration "line-through"})
      base-style)))


(defn- check-clicked
  [selections item ticked? required?]
  (let [num-selected (count selections)
        only-item    (when (= 1 num-selected) (first selections))]
    (if (and required? (= only-item item))
      selections  ;; prevent unselect of last item
      (if ticked? (conj selections item) (disj selections item)))))

(defn- as-checked
  [item selections on-change disabled? label-fn required? as-exclusions?]
  ;;TODO: Do we really need an anchor now that bootstrap styles not realy being used ?
  [box
   :class "list-group-item compact"
   :attr  {:on-click    (handler-fn (when-not disabled?
                                      (on-change (check-clicked selections item (not (selections item)) required?))))}
   :child [checkbox
           :model       (selections item)
           :on-change   #() ;; handled by enclosing box
           :disabled?   disabled?
           :label-style (label-style (selections item) as-exclusions?)
           :label       (label-fn item)]])


(defn- radio-clicked
  [selections item required?]
  (if (and required? (selections item))
    selections  ;; prevent unselect of radio
    (if (selections item) #{} #{item})))

(defn- as-radio
  [item selections on-change disabled? label-fn required? as-exclusions?]
  [box
   :class "list-group-item compact"
   :attr  {:on-click    (handler-fn (when-not disabled?
                                      (on-change (radio-clicked selections item required?))))}
   :child [radio-button
           :model       (first selections)
           :value       item
           :on-change   #() ;; handled by enclosing box
           :disabled?   disabled?
           :label-style (label-style (selections item) as-exclusions?)
           :label       (label-fn item)]])


(def ^:const list-style
  ;;TODO: These should be in CSS resource
  {:overflow-x     "hidden"
   :overflow-y     "auto"}) ;;TODO this should be handled by scroller later

(def ^:const spacing-bordered
  {:padding-top         "0px"
   :padding-bottom      "0px"
   :padding-left        "5px"
   :padding-right       "5px"
   :margin-top          "5px"
   :margin-bottom       "5px"})

(def ^:const spacing-unbordered
  {:padding-left   "0px"
   :padding-right  "5px"
   :padding-top    "0px"
   :padding-bottom "0px"
   :margin-top     "0px"
   :margin-bottom  "0px"})


(def selection-list-args-desc
  [{:name :choices        :required true                 :type "vector of maps | atom"              :validate-fn vector-of-maps? :description [:span "the selectable items. Elements can be strings or more interesting data items like {:label \"some name\" :sort 5}. Also see " [:code ":label-fn"] " below (list of maps also allowed)"]}
   {:name :model          :required true                 :type "set of :ids within :choices | atom" :validate-fn set-or-atom?    :description "the currently selected items. Note: items are considered distinct"}
   {:name :on-change      :required true                 :type "set of :ids -> nil | atom"          :validate-fn fn?             :description "a callback which will be passed set of selected items"}
   {:name :multi-select?  :required false :default true  :type "boolean | atom"                                                  :description "when true, use check boxes, otherwise radio buttons"}
   {:name :as-exclusions? :required false :default false :type "boolean | atom"                                                  :description "when true, selected items are shown with struck-out labels"}
   {:name :required?      :required false :default false :type "boolean | atom"                                                  :description "when true, at least one item must be selected. Note: being able to un-select a radio button is not a common use case, so this should probably be set to true when in single select mode"}
   {:name :width          :required false                :type "string | atom"                      :validate-fn string-or-atom? :description "a CSS style e.g. \"250px\". When specified, item labels may be clipped. Otherwise based on widest label"}
   {:name :height         :required false                :type "string | atom"                      :validate-fn string-or-atom? :description "a CSS style e.g. \"150px\". Size beyond which items will scroll"}
   {:name :max-height     :required false                :type "string | atom"                      :validate-fn string-or-atom? :description "a CSS style e.g. \"150px\". If there are less items then this height, box will shrink. If there are more, items will scroll"}
   {:name :disabled?      :required false :default false :type "boolean | atom"                                                  :description "when true, the time input will be disabled. Can be atom or value"}
   {:name :hide-border?   :required false :default false :type "boolean | atom"                                                  :description "when true, the list will be displayed without a border"}
   {:name :item-renderer  :required false                :type "-> nil | atom"                      :validate-fn fn?             :description "a function which takes no params and returns nothing. Called for each element during setup, the returned component renders the element, responds to clicks etc."}
   {:name :label-fn       :required false :default 'str  :type "-> nil | atom"                      :validate-fn ifn?            :description "a function which takes no params and returns nothing. Called for each element to get label string"}])

;;TODO hide hover highlights for links when disabled
(defn- list-container
  [{:keys [choices model on-change multi-select? disabled? hide-border? label-fn required? as-exclusions? item-renderer]
    :as   args}]
  {:pre [(validate-args-macro selection-list-args-desc args "selection-list")]}
  (let [selected (if multi-select? model (-> model first vector set))
        items    (map (if item-renderer
                        #(item-renderer % selected on-change disabled? label-fn required? as-exclusions?)
                        (if multi-select?
                          #(as-checked % selected on-change disabled? label-fn required? as-exclusions?)
                          #(as-radio % selected on-change disabled? label-fn required? as-exclusions?)))
                      choices)
        bounds   (select-keys args [:width :height :max-height])
        spacing  (if hide-border? spacing-unbordered spacing-bordered)]
    ;; In single select mode force selections to one. This causes a second render
    ;; TODO: GR commented this out to fix the bug where #{nil} was being returned for an empty list. Remove when we're sure there are no ill effects.
    #_(when-not (= selected model) (on-change selected))
    [border
     :radius "4px"
     :border (when hide-border? "none")
     :child  (into [:div {:class "list-group noselect" :style (merge list-style bounds spacing)}] items)]))


(defn- configure
  "Augment passed attributes with defaults and deref any atoms"
  [attributes]
  (merge {:multi-select?  true
          :as-exclusions? false
          :required?      false
          :disabled?      false
          :hide-border?   false
          :label-fn       str}
         (fmap deref-or-value attributes)))

(defn selection-list
  "Produce a list box with items arranged vertically"
  [& {:as args}]
  {:pre [(validate-args-macro selection-list-args-desc args "selection-list")]}
  ;;NOTE: Consumer has complete control over what is selected or not. A current design tradeoff
  ;;      causes all selection changes to trigger a complete list re-render as a result of on-change callback.
  ;;      this approach may be not ideal for very large list choices.
  (fn [& {:as args}]
    [list-container (configure args)]))
