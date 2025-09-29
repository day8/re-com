(ns re-com.table-filter
  (:require-macros
   [re-com.validate :refer [validate-args-macro]])
  (:require [clojure.walk :as walk]
            [re-com.box :as box]
            [re-com.buttons :as buttons]
            [re-com.config :refer [include-args-desc?]]
            [re-com.datepicker :as datepicker]
            [re-com.daterange :as daterange]
            [re-com.dropdown :as dropdown]
            [re-com.input-text :as input-text]
            [re-com.tag-dropdown :as tag-dropdown]
            [re-com.text :as text]
            [re-com.theme :as theme]
            [re-com.util :as u :refer [deref-or-value]]
            [re-com.validate :refer [css-class? css-style? html-attr? parts?]]
            [reagent.core :as r]))

;; ----------------------------------------------------------------------------
;; Helpers
;; ----------------------------------------------------------------------------
;; ID generation for filters and groups
(defn generate-id
  "Generates a unique ID string for filter and group nodes."
  []
  (str "item-" (random-uuid)))

(def number-regex #"^-?\d+(?:\.\d+)?$")

(defn valid-number?
  "True if `s` is a plain numeric string."
  [s]
  (boolean (re-matches number-regex (str s))))

(defn valid-date?
  "True if `d` is a non-nil value (datepickers handle validation)."
  [d]
  (some? d))

;; ----------------------------------------------------------------------------
;; State Conversion Functions for Internal/External Model Handling
;; ----------------------------------------------------------------------------

(defn add-ids
  "Recursively add unique IDs to all filter/group nodes.
   Converts external (ID-less) format to internal (ID-full) format."
  [node]
  (cond
    (nil? node) nil

    (and (map? node) (:type node))
    (let [node-with-id (assoc node :id (str (random-uuid)))]
      (if (= (:type node) :group)
        (update node-with-id :children #(mapv add-ids %))
        node-with-id))

    :else node))

(defn remove-ids
  "Recursively remove all :id keys from the model.
   Converts internal (ID-full) format to external (ID-less) format."
  [node]
  (cond
    (nil? node) nil

    (map? node)
    (let [cleaned (dissoc node :id)]
      (if (= (:type node) :group)
        (update cleaned :children #(mapv remove-ids %))
        cleaned))

    :else node))

;; ----------------------------------------------------------------------------
;; Custom Validators for re-com compliance
;; ----------------------------------------------------------------------------

(defn table-spec?
  "Validates table-spec as vector of column definition maps"
  [table-spec]
  (and (vector? table-spec)
       (seq table-spec)
       (every? #(and (map? %)
                     (keyword? (:id %))
                     (string? (:name %))
                     (keyword? (:type %))
                     (contains? #{:text :number :date :boolean :select} (:type %)))
               table-spec)))

(defn filter-node?
  "Validates a single filter node (external format - no ID required)"
  [node]
  (and (map? node)
       (= :filter (:type node))
       (or (nil? (:col node)) (keyword? (:col node)))
       (or (nil? (:op node)) (keyword? (:op node)))))

(defn group-node?
  "Validates a single group node (external format - no ID required)"
  [node]
  (and (map? node)
       (= :group (:type node))
       (contains? #{:and :or} (:logic node))
       (vector? (:children node))))

(defn model?
  "Validates the hierarchical filter model structure"
  [val]
  (or (nil? val)
      (and (map? val)
           (contains? #{:filter :group} (:type val))
           (if (= :filter (:type val))
             (filter-node? val)
             (and (group-node? val)
                  (every? model? (:children val)))))))

;; dropdown options for valid operations for sql value types
(def ops-by-type
  {:text    [:equals :not-equals :contains :not-contains :starts-with :ends-with :empty :not-empty]
   :number  [:equals :not-equals :> :>= :< :<= :empty :not-empty]
   :date    [:before :after :on :not-on :on-or-before :on-or-after :between :not-between :empty :not-empty]
   :boolean [:is :empty :not-empty]
   :select  [:is :is-not :is-any-of :is-none-of :empty :not-empty]})

(def op-label
  {:equals        "is"            :not-equals        "is not"    :contains    "contains" :not-contains "does not contain"
   :starts-with   "starts with"   :ends-with         "ends with" :empty       "is empty" :not-empty    "is not empty"
   :>             ">"             :>=                ">="        :<           "<"        :<=           "<="
   :before        "before"        :after             "after"     :on          "on"       :not-on       "not on" :on-or-before "on/before"
   :on-or-after   "on/after"      :between           "between"   :not-between "not between"
   :is            "is"            :is-not            "is not"
   :contains-text "contains text" :not-contains-text "not contains text"
   :is-any-of     "is any of"     :is-none-of        "is none of"})

;; This is what a table spec should look like, id must be unique. id is for identification
;; :name is used for displaying in UI e.g. dropdown components
;; :type tells the filter component what kind of operator options and value-entry field should be used
(def sample-table-spec
  [{:id :name :name "Name" :type :text}
   {:id :age :name "Age" :type :number}
   {:id :email :name "Email" :type :text}
   {:id :salary :name "Salary" :type :number}
   {:id      :department :name "Department" :type :select
    :options [{:id "engineering" :label "Engineering"}
              {:id "marketing" :label "Marketing"}
              {:id "sales" :label "Sales"}]}
   {:id :active :name "Active" :type :boolean}
   {:id :hire-date :name "Hire Date" :type :date}
   {:id      :skills :name "Skills" :type :select
    :options [{:id "clojure" :label "Clojure"}
              {:id "javascript" :label "JavaScript"}
              {:id "python" :label "Python"}
              {:id "java" :label "Java"}]}])

;; ----------------------------------------------------------------------------
;; Component Parts Definition for re-com compliance
;; ----------------------------------------------------------------------------

(def table-filter-parts-desc
  (when include-args-desc?
    [{:name :group              :level 1 :class "rc-table-filter-group"        :impl "[v-box]"         :notes "Container for a filter group."}
     {:name :filter             :level 2 :class "rc-table-filter-filter"       :impl "[h-box]"         :notes "Container for individual filter condition row."}
     {:name :column-dropdown    :level 3 :class "rc-table-filter-column"       :impl "[dropdown]"      :notes "Dropdown for selecting table columns."}
     {:name :operator-dropdown  :level 3 :class "rc-table-filter-operator"     :impl "[dropdown]"      :notes "Dropdown for selecting filter operators."}
     {:name :text-input         :level 3 :class "rc-table-filter-text"         :impl "[input-text]"    :notes "Text input field for text and number values."}
     {:name :date-input         :level 3 :class "rc-table-filter-date"         :impl "[datepicker]"    :notes "Date picker for single date values."}
     {:name :daterange-input    :level 3 :class "rc-table-filter-daterange"    :impl "[daterange]"     :notes "Date range picker for between/not-between operations."}
     {:name :dropdown-input     :level 3 :class "rc-table-filter-dropdown"     :impl "[dropdown]"      :notes "Dropdown for boolean and single-select values."}
     {:name :tag-dropdown-input :level 3 :class "rc-table-filter-tags"         :impl "[tag-dropdown]"  :notes "Tag-dropdown for selecting multiple values. Due to unfinished implementation of tag-dropdown, does not accept :class or :attr"}
     {:name :add-button         :level 2 :class "rc-table-filter-add"          :impl "[button]"        :notes "The '+ Add filter' button and dropdown menu."}
     {:name :context-menu       :level 3 :class "rc-table-filter-context"      :impl "[button]"        :notes "The '⋯' context menu button for groups and filters."}
     {:name :operator-button    :level 2 :class "rc-table-filter-op-button"    :impl "[button]"        :notes "The AND/OR operator button when interactive (first in group)."}
     {:name :operator-text      :level 2 :class "rc-table-filter-op-text"      :impl "[label]"         :notes "The AND/OR operator text when non-interactive (subsequent in group)."}
     {:name :where-label        :level 2 :class "rc-table-filter-where"        :impl "[label]"         :notes "The 'Where' label for first filter."}
     {:name :warning-icon       :level 3 :class "rc-table-filter-warning"      :impl "[md-icon-button]" :notes "The warning icon shown for invalid filters."}]))

(def table-filter-parts
  (when include-args-desc?
    (-> (map :name table-filter-parts-desc) set)))

(def table-filter-args-desc
  (when include-args-desc?
    [{:name :table-spec      :required true                         :type "vector"           :validate-fn table-spec?                     :description "Vector of column definition maps with :id, :name, :type keys. Example on the right"}
     {:name :model           :required true                          :type "map | r/atom"    :validate-fn model?                          :description "Hierarchical filter model with :type, :logic, and :children structure. The UI will always reflect what is in model. Should be updated by the on-change function to maintain proper data flow. If unsure, just pass an empty reagent atom."}
     {:name :on-change       :required true                         :type "-> nil"           :validate-fn fn?                             :description [:span "Callback function called when user interacts with the filter component. Receives two arguments: " [:code "[model is-valid?]"] " where " [:code "model"] " is the updated filter structure and " [:code "is-valid?"] " is a boolean indicating if all filters are complete and valid. Use this to update your application state."]}
     {:name :max-depth       :required false :default 2             :type "int"              :validate-fn int?                            :description "Set the maximum amount of nesting possible. 0 is no nesting; user only allowed to add filters. 1 allows user to add filter groups, ect"}
     {:name :disabled?       :required false :default false         :type "boolean"          :validate-fn boolean?                        :description "If true, disables all filter interactions"}
     {:name :class           :required false                        :type "string"           :validate-fn css-class?                      :description "CSS class names, space separated (applies to wrapper)"}
     {:name :style           :required false                        :type "CSS style map"    :validate-fn css-style?                      :description "CSS styles to apply to wrapper"}
     {:name :attr            :required false                        :type "HTML attr map"    :validate-fn html-attr?                      :description [:span "HTML attributes for wrapper. No " [:code ":class"] " or " [:code ":style"] " allowed"]}
     {:name :parts           :required false                        :type "map"              :validate-fn (parts? table-filter-parts)     :description "Map of part names to {:class :style :attr} for styling customization"}
     {:name :src             :required false                        :type "map"              :validate-fn map?                            :description [:span "Source code coordinates for debugging. Map with " [:code ":file"] " and " [:code ":line"] " keys"]}
     {:name :debug-as        :required false                        :type "map"              :validate-fn map?                            :description [:span "Debug output masquerading. Map with " [:code ":component"] " and " [:code ":args"] " keys"]}]))

;; ----------------------------------------------------------------------------
;; Data Structure - Hierarchical Groups
;; ----------------------------------------------------------------------------
(defn empty-filter
  "uses the information from the first row from the table spec
   to create the initial empty filter with unique ID"
  [table-spec]
  (let [first-col (first table-spec)
        first-op  (first (ops-by-type (:type first-col)))]
    {:id (generate-id) :type :filter :col (:id first-col) :op first-op :val nil}))

(defn empty-group
  "creates a group with only the empty filter in it with unique ID"
  [table-spec]
  {:id (generate-id) :type :group :logic :and :children [(empty-filter table-spec)]})

;; External format versions (no IDs) for user-facing API
(defn empty-filter-external
  "Create empty filter without IDs for external format"
  [table-spec]
  (let [first-col (first table-spec)
        first-op  (first (ops-by-type (:type first-col)))]
    {:type :filter :col (:id first-col) :op first-op :val nil}))

(defn empty-group-external
  "Create empty group without IDs for external format"
  [table-spec]
  {:type :group :logic :and :children [(empty-filter-external table-spec)]})

;; ----------------------------------------------------------------------------
;; Clojure.walk-based tree operations
;; ----------------------------------------------------------------------------
;; postwalk is a function which takes a function and a tree structure (in this case)
;; it visits each node in the tree from the bottom up (children first)
;; it also takes a function which it applies to each of the nodes
;; e.g. we visit each node, if the [node we are currently visiting] contains the [node we want to delete] as a child
;; simply remove the child node (see remove-item-by-id function)

(defn update-item-by-id
  "Update an item by ID using postwalk - bottom-up tree transformation"
  [tree target-id update-fn]
  (walk/postwalk
   (fn [node]
     (if (and (map? node) (= (:id node) target-id))
       (update-fn node)
       node))
   tree))

(defn remove-item-by-id
  "Remove an item by ID using postwalk - bottom-up tree transformation"
  [tree target-id]
  (walk/postwalk
   (fn [node]
     (if (and (map? node) (= (:type node) :group) (:children node))
       (assoc node :children
              (vec (remove #(= (:id %) target-id) (:children node))))
       node))
   tree))

(defn add-child-to-group
  "Add a new item to a group by the group's ID"
  [tree group-id new-item]
  (update-item-by-id tree group-id
                     #(assoc % :children (conj (:children %) new-item))))

(defn find-parent-group
  "Finds the parent group that contains the item with target-id.
  Returns the parent group node or nil if not found."
  [tree target-id]
  (when (= (:type tree) :group)
    (if (some #(= (:id %) target-id) (:children tree))
      tree
      (some #(find-parent-group % target-id) (:children tree)))))

(defn duplicate-item-by-id
  "Duplicates an item by ID and adds the copy to the same parent group.
  Returns the updated tree with the duplicated item added."
  [tree target-id]
  (when-let [parent (find-parent-group tree target-id)]
    (when-let [item (some #(when (= (:id %) target-id) %) (:children parent))]
      (let [new-item (assoc item :id (generate-id))]
        (add-child-to-group tree (:id parent) new-item)))))

(defn convert-filter-to-group
  "Converts a filter node to a group node containing that filter.
  Used to enable nesting when adding subgroups to existing filters."
  [tree target-id]
  (update-item-by-id tree target-id
                     (fn [item]
                       (if (= (:type item) :filter)
                         {:id       (generate-id) :type :group :logic :and
                          :children [(assoc item :id (generate-id))]}
                         item))))

(defn clean-empty-groups
  "Recursively removes groups with no children, except the root group.
  Helps maintain a clean tree structure after deletions."
  ([tree] (clean-empty-groups tree (:id tree)))  ; Start with root ID
  ([tree root-id]
   (if (= (:type tree) :group)
     (let [cleaned-children (vec (keep #(clean-empty-groups % root-id) (:children tree)))]
       (if (and (empty? cleaned-children)
                (not= (:id tree) root-id))  ; Don't remove if this is the root group
         nil  ; Remove this empty group
         (assoc tree :children cleaned-children)))  ; Keep group with cleaned children
     tree)))  ; Return filters as-is

(defn remove-item-with-cleanup
  "Remove an item by ID and clean up any resulting empty groups.
   If root group becomes empty, add a default empty filter."
  [tree target-id table-spec]
  (let [cleaned-tree (-> tree
                         (remove-item-by-id target-id)
                         clean-empty-groups)]
    ;; If root group is now empty, add a default empty filter
    (if (and (= (:type cleaned-tree) :group)
             (empty? (:children cleaned-tree)))
      (assoc cleaned-tree :children [(empty-filter table-spec)])
      cleaned-tree)))

;; ----------------------------------------------------------------------------
;; Validation  
;; ----------------------------------------------------------------------------
(defn column-by-id
  "returns the column information for a given ID from the table spec"
  [table-spec id]
  (some #(when (= (:id %) id) %) table-spec))

(defn rule-valid?
  "Return true if rule satisfies built-in and custom validators."
  [rule table-spec]
  (let [spec                  (column-by-id table-spec (:col rule))
        {:keys [type valid?]} spec
        op                    (:op rule)
        val                   (:val rule)]
    (and spec
         (or (#{:empty :not-empty} op) (some? val))  ; empty/not-empty don't need values
         (or (and (fn? valid?) (valid? val))
             (case type
               :text    (if (#{:empty :not-empty} op)
                          true  ; empty/not-empty are always valid
                          (string? val))
               :number  (if (#{:empty :not-empty} op)
                          true  ; empty/not-empty are always valid
                          (valid-number? val))
               :date    (case op
                          (:empty :not-empty)
                          true  ; empty/not-empty are always valid
                          (:between :not-between) (and (map? val)
                                                       (contains? val :start)
                                                       (contains? val :end)
                                                       (valid-date? (:start val))
                                                       (valid-date? (:end val)))
                          (valid-date? val))
               :select  (case op
                          (:empty :not-empty)
                          true  ; empty/not-empty are always valid
                          (:is-any-of :is-none-of :contains :not-contains)
                          (and (set? val) (seq val))
                          ;; For single value operators
                          (some? val))
               :boolean (if (#{:empty :not-empty} op)
                          true  ; empty/not-empty are always valid
                          (boolean? val))
               true)))))

(defn model-valid?
  "Return true if all filters in the model are valid."
  [model table-spec]
  (cond
    (nil? model) true

    (= (:type model) :filter)
    (rule-valid? model table-spec)

    (= (:type model) :group)
    (every? #(model-valid? % table-spec) (:children model))

    :else false))

;; ----------------------------------------------------------------------------
;; Components
;; ----------------------------------------------------------------------------

(defn- common-props
  "Extract common properties for input components as a props map.
   Includes model, on-change, width, disabled, and styling props."
  [{:keys [val] :as filter-spec} on-change parts part-key disabled?]
  {:model     val
   :on-change #(on-change (assoc filter-spec :val %))
   :width     "220px"
   :disabled? disabled?
   :class     (get-in parts [part-key :class])
   :style     (get-in parts [part-key :style])
   :attr      (get-in parts [part-key :attr])})

(defmulti value-entry-box
  "Depending on the spec for a given column, the value entry box behaves differently
   There are options for most sql-like types)"
  (fn [& {:keys [row-spec filter-spec]}]
    (let [{row-type :type} row-spec
          {op :op}         filter-spec]
      (if (#{:empty :not-empty} op)
        :empty-operation
        row-type))))

;; Empty operation case - same for all types
(defmethod value-entry-box :empty-operation
  [& {:keys []}]
  [text/label :label "" :style {:width "220px"}])

;; Text input case
(defmethod value-entry-box :text
  [& {:keys [filter-spec on-change parts disabled?]}]
  [input-text/input-text (common-props filter-spec on-change parts :text-input disabled?)])

;; Number input case
(defmethod value-entry-box :number
  [& {:keys [filter-spec on-change parts disabled?]}]
  [input-text/input-text (common-props filter-spec on-change parts :text-input disabled?)])

;; Date input case
(defmethod value-entry-box :date
  [& {:keys [filter-spec on-change parts disabled?]}]
  (let [op (:op filter-spec)]
    (if (#{:between :not-between} op)
      [daterange/daterange-dropdown
       (merge {:placeholder "Select date range"
               :show-today? true}
              (common-props filter-spec on-change parts :daterange-input disabled?))]

      [datepicker/datepicker-dropdown
       (merge {:placeholder "Select a date"
               :show-today? true
               :parts       {:anchor-label {:style {:height "34px"}}}}
              (common-props filter-spec on-change parts :date-input disabled?))])))

;; Boolean input case
(defmethod value-entry-box :boolean
  [& {:keys [filter-spec on-change parts disabled?]}]
  [dropdown/single-dropdown
   (merge {:choices [{:id true :label "True"}
                     {:id false :label "False"}]}
          (common-props filter-spec on-change parts :dropdown-input disabled?))])

;; Select input case
(defmethod value-entry-box :select
  [& {:keys [row-spec filter-spec on-change parts disabled?]}]
  (let [{:keys [options]} row-spec
        op                (:op filter-spec)
        val               (:val filter-spec)]
    (if (#{:is-any-of :is-none-of :contains :not-contains} op)
      ;; Multi-value selection for these operators
      [tag-dropdown/tag-dropdown
       {:model             (or val #{})
        :height            "34px"
        :choices           options
        :placeholder       "Select values..."
        :min-width         "220px"
        :max-width         "600px"
        :show-only-button? true
        :show-counter?     true
        :on-change         #(on-change (assoc filter-spec :val %))
        :style             (merge {:color            "#333333"
                                   :background-color "#ffffff"}
                                  (get-in parts [:tag-dropdown-input :style]))
        :disabled?         disabled?}]

      ;; Single value selection for equals/not-equals
      [dropdown/single-dropdown
       (merge {:choices options}
              (common-props filter-spec on-change parts :dropdown-input disabled?))])))

;; Default case for unknown types
(defmethod value-entry-box :default
  [& {:keys []}]
  [text/label :label ""])

(defn group-context-menu
  "Menu for operations on groups, currently only delete"
  [& {:keys [group-id update-state! table-spec disabled? parts]}]
  (let [choices [{:id :delete :label "Delete group" :color "#dc2626"}]]
    [dropdown/dropdown
     :model (r/atom nil)
     :disabled? disabled?
     :anchor [text/label :label "⋯"
              :style {:color     "#9ca3af"
                      :font-size "20px"
                      :padding   "6px 8px"
                      :cursor    "pointer"}]
     :attr (get-in parts [:context-menu :attr])
     :parts {:anchor-wrapper {:style (merge {:border          "none"
                                             :background      "transparent"
                                             :border-radius   "4px"
                                             :box-shadow      "none"
                                             :width           "100%"
                                             :height          "100%"
                                             :display         "flex"
                                             :align-items     "center"
                                             :justify-content "center"
                                             :cursor          "pointer"}
                                            (get-in parts [:context-menu :style]))}
             :indicator      {:style {:display "none"}}
             :body-wrapper   {:style {:background-color "#ffffff"
                                      :border           "1px solid #e1e5e9"
                                      :border-radius    "8px"
                                      :box-shadow       "0 8px 16px rgba(0, 0, 0, 0.12)"
                                      :min-width        "160px"
                                      :margin-top       "4px"}}}

     :body [box/v-box
            :style {:padding "0"}
            :children (for [choice choices]
                        [buttons/button
                         :label (:label choice)
                         :class "btn-link"
                         :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color (:color choice)}
                         :on-click (case (:id choice)
                                     :delete #(update-state! (fn [state]
                                                               ;; Safety check: don't delete root group
                                                               (if (= (:id state) group-id)
                                                                 state
                                                                 (remove-item-with-cleanup state group-id table-spec)))))])]]))

(defn and-or-dropdown
  "Dropdown to choose the logical combination of a group, e.g. AND/OR"
  [& {:keys [operator update-state! group-id depth interactable? disabled? parts]}]
  [box/h-box
   :children [(if interactable?
                [dropdown/dropdown
                 :model (r/atom nil)
                 :disabled? disabled?
                 :label (case operator
                          :and "And"
                          :or  "Or")
                 :width "50px"
                 :parts {:anchor-wrapper {:class (theme/merge-class "btn-link" (get-in parts [:operator-button :class]))
                                          :style (merge {:font-size        "14px" :font-weight "500"
                                                         :color            "#6b7280"
                                                         :margin-right     "0px"  :margin-left "0px"
                                                         :background-color (if (odd? depth) "white" "#f7f7f7")
                                                         :border-radius    "4px"
                                                         :border           "1px solid #e2e8f0"
                                                         :min-width        "50px" :height      "34px"
                                                         :padding          "4px 4px"
                                                         :cursor           "pointer"}
                                                        (get-in parts [:operator-button :style]))}
                         :anchor         {:style {:cursor "pointer"}}
                         :body-wrapper   {:style {:background-color "#ffffff"
                                                  :border           "1px solid #e1e5e9"
                                                  :border-radius    "8px"
                                                  :min-width        "200px"
                                                  :margin-top       "4px"
                                                  :padding          "8px 0"}}}
                 :body [box/v-box
                        :children [[buttons/button
                                    :label [box/v-box
                                            :gap "2px"
                                            :children [[text/label :label "And" :style {:font-weight "600" :color "#374151" :font-size "13px"}]
                                                       [text/label :label "All filters must match" :style {:color "#6b7280" :font-size "11px"}]]]
                                    :class "btn-link"
                                    :style {:text-align       "left" :padding "10px 16px" :border "none" :width "100%"
                                            :background-color (when (= operator :and) "#f3f4f6")}
                                    :on-click #(update-state! (fn [state] (update-item-by-id state group-id (fn [g] (assoc g :logic :and)))))]
                                   [buttons/button
                                    :label [box/v-box
                                            :gap "2px"
                                            :children [[text/label :label "Or" :style {:font-weight "600" :color "#374151" :font-size "13px"}]
                                                       [text/label :label "At least one filter must match" :style {:color "#6b7280" :font-size "11px"}]]]
                                    :class "btn-link"
                                    :style {:text-align       "left" :padding "10px 16px" :border "none" :width "100%"
                                            :background-color (when (= operator :or) "#f3f4f6")}
                                    :on-click #(update-state! (fn [state] (update-item-by-id state group-id (fn [g] (assoc g :logic :or)))))]]]]
                [text/label
                 :label (case operator :and "And" :or "Or")
                 :class (get-in parts [:operator-text :class])
                 :style (merge {:font-size    "14px" :font-weight "500"
                                :color        "#6b7280"
                                :margin-right "0px"  :margin-left "0px"
                                :min-width    "50px" :height      "34px"
                                :text-align   "left" :padding     "6px 6px"
                                :display      "flex" :align-items "center"}
                               (get-in parts [:operator-text :style]))
                 :attr (get-in parts [:operator-text :attr])])
              [box/gap :size "2px"]]])

(defn add-filter-dropdown
  "A dropdown that looks like a button for adding new filter-builders or filter-groups"
  [& {:keys [group-id update-state! table-spec depth disabled? parts max-depth]}]
  (let [choices (cond-> [{:id :add-filter :label "Add a filter"}]
                  (< depth max-depth)
                  (conj {:id :add-group :label "Add a filter group"}))]
    [dropdown/dropdown
     :model (r/atom nil)
     :disabled? disabled?
     ;; we have replaced JS jank with very awkwards "parts" handling
     :anchor [text/label :label "+ Add filter"
              :style {:font-size "13px" :color "#46a2da"}]
     :attr (get-in parts [:add-button :attr])
     :parts {:anchor-wrapper {:class (str "btn-outline " (get-in parts [:add-button :class]))
                              :style (merge {:font-size        "13px"
                                             :padding          "2px 4px"
                                             :font-weight      "500"
                                             :border-radius    "8px"
                                             :background-color "transparent"
                                             :width            "75px"
                                             :border           "none"
                                             :box-shadow       "none"
                                             :cursor           "pointer"}
                                            (get-in parts [:add-button :style]))}
             :indicator      {:style {:display "none"}}
             :body-wrapper   {:style {:background-color "#ffffff"
                                      :border           "1px solid #e1e5e9"
                                      :border-radius    "8px"
                                      :box-shadow       "0 8px 16px rgba(0, 0, 0, 0.12)"
                                      :min-width        "160px"
                                      :margin-top       "4px"}}}

     :body [box/v-box
            :style {:padding "0"}
            :children (for [choice choices]
                        [buttons/button
                         :label (:label choice)
                         :class "btn-link"
                         :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151"}
                         :on-click (case (:id choice)
                                     :add-filter #(update-state! (fn [state] (add-child-to-group state group-id (empty-filter table-spec))))
                                     :add-group  #(update-state! (fn [state] (add-child-to-group state group-id (empty-group table-spec)))))])]]))

(defn filter-context-menu
  "A dropdown which exposes the options you can take on a filter, e.g. delete, dupe or promote to group"
  [& {:keys [item-id update-state! table-spec depth disabled? parts max-depth]}]
  (let [choices (cond-> [{:id :delete :label "Delete Filter" :color "#dc2626"}
                         {:id :duplicate :label "Duplicate" :color "#374151"}]
                  (< depth max-depth)
                  (conj {:id :convert :label "Turn into group" :color "#374151"}))]
    [dropdown/dropdown
     :model (r/atom nil)
     :disabled? disabled?
     :direction :toward-center
     :anchor [text/label :label "⋯"
              :style {:color       "#9ca3af"
                      :font-size   "20px"
                      :line-height "18px"
                      :padding     "0px 8px"
                      :cursor      "pointer"}]
     :attr (get-in parts [:context-menu :attr])
     :parts {:anchor-wrapper {:style (merge {:border        "none"
                                             :background    "transparent"
                                             :border-radius "4px"
                                             :box-shadow    "none"
                                             :height        "20px"
                                             :cursor        "pointer"}
                                            (get-in parts [:context-menu :style]))}
             :indicator      {:style {:display "none"}}
             :body-wrapper   {:style {:background-color "#ffffff"
                                      :border           "1px solid #e1e5e9"
                                      :border-radius    "8px"
                                      :box-shadow       "0 8px 16px rgba(0, 0, 0, 0.12)"
                                      :min-width        "160px"
                                      :margin-top       "4px"}}}

     :body [box/v-box
            :children (for [choice choices]
                        [buttons/button
                         :label (:label choice)
                         :class "btn-link"
                         :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color (:color choice)}
                         :on-click (case (:id choice)
                                     :delete    #(update-state! (fn [state] (remove-item-with-cleanup state item-id table-spec)))
                                     :duplicate #(update-state! (fn [state] (duplicate-item-by-id state item-id)))
                                     :convert   #(update-state! (fn [state] (convert-filter-to-group state item-id))))])]]))

(defn filter-builder
  "A single filter, contains a row selection box, an operator selection box, a value entry box and a context button"
  [& {:keys [table-spec filter-spec update-state! parts disabled?] :as args}]
  (let [spec     (column-by-id table-spec (:col filter-spec))
        ops      (ops-by-type (:type spec))
        valid?   (rule-valid? filter-spec table-spec)
        col-opts (mapv #(hash-map :id (:id %) :label (:name %)) table-spec)
        op-opts  (mapv #(hash-map :id % :label (get op-label % (name %))) ops)]
    [box/h-box
     :align :center
     :gap "4px"
     :class (get-in parts [:filter :class])
     :style (merge {:background-color "transparent"
                    :white-space      "nowrap"}
                   (get-in parts [:filter :style]))
     :attr (get-in parts [:filter :attr])
     :children [[dropdown/single-dropdown
                 :model (:col filter-spec)
                 :choices col-opts
                 :width "140px"
                 :class (get-in parts [:column-dropdown :class])
                 :style (get-in parts [:column-dropdown :style])
                 :attr (get-in parts [:column-dropdown :attr])
                 :disabled? disabled?
                 :on-change #(let [cs (column-by-id table-spec %)]
                               (update-state! (fn [state] (update-item-by-id state (:id filter-spec)
                                                                             (fn [f] (assoc f :col % :op (first (ops-by-type (:type cs))) :val nil))))))]
                [dropdown/single-dropdown
                 :model (:op filter-spec)
                 :choices op-opts
                 :width "130px"
                 :class (get-in parts [:operator-dropdown :class])
                 :style (get-in parts [:operator-dropdown :style])
                 :attr (get-in parts [:operator-dropdown :attr])
                 :disabled? disabled?
                 :on-change #(update-state! (fn [state] (update-item-by-id state (:id filter-spec) (fn [f] (assoc f :op % :val nil)))))]
                [value-entry-box (merge args {:row-spec    spec
                                              :filter-spec filter-spec
                                              :on-change   #(update-state! (fn [state] (update-item-by-id state (:id filter-spec) (constantly %))))})]
                [filter-context-menu (merge args {:item-id     (:id filter-spec)
                                                  :filter-spec filter-spec})]
                (when-not valid?
                  [buttons/md-icon-button
                   :md-icon-name "zmdi-alert-triangle"
                   :size :smaller
                   :style (merge {:color "red" :pointer-events "none"}
                                 (get-in parts [:warning-icon :style]))
                   :class (get-in parts [:warning-icon :class])
                   :attr (get-in parts [:warning-icon :attr])
                   :tooltip "Invalid rule"])]]))

(defn filter-group
  "Contains 1 or more filter-builders and has an associated context menu"
  [& {:keys [group depth parts] :as args}]
  (let [group-deref    (deref-or-value group)
        children       (:children group-deref)
        is-root?       (zero? depth)
        show-group-ui? (or (not is-root?) (> (count children) 1))]                    ; Non-root groups always show UI ; Root group only shows UI when 2+ children
    [box/h-box
     :align :start
     :children [[box/v-box
                 :class (get-in parts [:group :class])
                 :style (merge {:padding  0
                                :margin   "0px 0px"
                                :position "relative"}
                               (when (and show-group-ui? (not is-root?))
                                 {:padding          8
                                  :background-color (if (odd? depth) "#f7f7f7" "white")
                                  :border           "1px solid #e1e5e9"
                                  :border-radius    "4px"})
                               (get-in parts [:group :style]))
                 :attr (get-in parts [:group :attr])
                 :children [[box/v-box
                             :gap "6px"
                             :children (concat
                                        (map-indexed
                                         (fn [idx child]
                                           (let [child-is-group? (= :group (:type (nth (:children group-deref) idx)))
                                                 show-operator?  (> idx 0)
                                                 show-where?     (= idx 0)  ; Show "Where" for first item
                                                 operator-btn    (when show-operator? ;if the child is a group comonent, the self-align should be :start
                                                                   [box/v-box
                                                                    :align-self (if child-is-group? :start :center)
                                                                    :children [[box/gap :size "0px"] ; TODO ADD PARAMTER BOX TOP GAP
                                                                               [and-or-dropdown (merge args {:operator (or (:logic group-deref) :and) :group-id (:id group-deref) :depth depth :interactable? (= idx 1)})]]])
                                                 where-label     (when show-where?
                                                                   [text/label
                                                                    :label "Where"
                                                                    :class (get-in parts [:where-label :class])
                                                                    :style (merge {:font-size  "14px" :font-weight "500" :color "#374151"
                                                                                   :min-width  "52px"
                                                                                   :text-align "center"}
                                                                                  (get-in parts [:where-label :style]))
                                                                    :attr (get-in parts [:where-label :attr])])]
                                             [box/h-box
                                              :align :center
                                              :gap "4px"
                                              :children (concat
                                                         (when where-label [where-label])
                                                         (when operator-btn [operator-btn])
                                                         [(case (:type child)
                                                            :filter [filter-builder (merge args {:filter-spec child})]
                                                            :group  [filter-group (merge args {:group child :depth (inc depth)})])])]))
                                         children)
                                        [;[box/gap :size "4px"]
                                         [add-filter-dropdown (merge args {:group-id (:id group-deref)})]])]]]
                (when (and show-group-ui? (not is-root?)) ;; Group context menu for non-root groups
                  [box/h-box
                   :children [[group-context-menu (merge args {:group-id (:id group-deref)})]]])]]))

(defn table-filter
  "Hierarchical table filter component with external state as single source of truth.
   External model (user-facing): {:type :group :logic :and :children [...]} - no IDs required
   Internal model automatically adds IDs for component state management."
  []
  ;; Create stable atom once for child component identity
  (let [internal-model (r/atom nil)]
    ;; Render function called on every re-render
    (fn [& {:keys [table-spec model on-change max-depth] ;; Pull out passed in model
            :or   {max-depth 2}
            :as   args}]
      (or
       (validate-args-macro table-filter-args-desc args)
       (let [;; Always derive canonical state from props
             external-model-val (or (deref-or-value model) (empty-group-external table-spec)) ;; Passed in model, if nil we still want to display one empty filter
             ;; Check if external structure changed (ignoring IDs)
             current-external   (when @internal-model (remove-ids @internal-model))] ;; User shouldn't have to worry about ID's

          ;; Only regenerate IDs if external structure actually changed
         (when (not= current-external external-model-val)
           (reset! internal-model (add-ids external-model-val)))

         (let [model-with-ids (or @internal-model (add-ids external-model-val))
               ;; Pure function - calculates new state and notifies parent
               ;; Does NOT mutate local state - external state is source of truth
               update-state!  (fn [update-fn]
                                (let [new-internal-model (update-fn model-with-ids)
                                      new-external-model (remove-ids new-internal-model)
                                      is-valid?          (model-valid? new-external-model table-spec)]
                                  (when on-change
                                    (on-change new-external-model is-valid?))))]

           [filter-group (merge args {:group         internal-model
                                      :update-state! update-state!
                                      :depth         0
                                      :max-depth     max-depth})]))))))
