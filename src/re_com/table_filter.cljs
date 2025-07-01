(ns re-com.table-filter
  (:require-macros [re-com.core :refer [handler-fn at reflect-current-component]]
                   [re-com.validate :refer [validate-args-macro]])
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [reagent.core :as r]
            [re-com.box :as box]
            [re-com.buttons :as buttons]
            [re-com.datepicker :as datepicker]
            [re-com.daterange :as daterange]
            [re-com.dropdown :as dropdown]
            [re-com.input-text :as input-text]
            [re-com.multi-select :as multi-select]
            [re-com.text :as text]
            [re-com.debug :refer [->attr]]
            [re-com.theme :as theme]
            [re-com.util :as u :refer [deref-or-value]]
            [re-com.config :refer [include-args-desc?]]
            [re-com.validate :refer [string-or-hiccup? css-class? css-style? html-attr? parts?]]))

;; ----------------------------------------------------------------------------
;; Helpers
;; ----------------------------------------------------------------------------

;; ID generation for filters and groups
(defn generate-id []
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
                     (contains? #{:text :number :date :boolean :select :multi-select} (:type %)))
               table-spec)))

(defn filter-node?
  "Validates a single filter node"
  [node]
  (and (map? node)
       (string? (:id node))
       (= :filter (:type node))
       (or (nil? (:col node)) (keyword? (:col node)))
       (or (nil? (:op node)) (keyword? (:op node)))))

(defn group-node?
  "Validates a single group node"
  [node]
  (and (map? node)
       (string? (:id node))
       (= :group (:type node))
       (contains? #{:and :or} (:operator node))
       (vector? (:children node))))

(defn model?
  "Validates the hierarchical filter model structure"
  [val]
  (or (nil? val)
      (and (map? val)
           (string? (:id val))
           (contains? #{:filter :group} (:type val))
           (if (= :filter (:type val))
             (filter-node? val)
             (and (group-node? val)
                  (every? model? (:children val)))))))

;; dropdown options for valid operations for sql value types
(def ops-by-type
  {:text         [:contains :equals :starts-with :ends-with]
   :number       [:equals :> :>= :< :<= :between]
   :date         [:before :after :on :on-or-before :on-or-after :between]
   :boolean      [:is :is-not]
   :select       [:is :is-not :contains :not-contains :is-any-of :is-none-of]
   :multi-select [:contains :not-contains]})

(def op-label
  {:contains "contains" :equals "equals" :starts-with "starts" :ends-with "ends"
   :> ">" :>= ">=" :< "<" :<= "<=" :between "between"
   :before "before" :after "after" :on "on" :on-or-before "on/before"
   :on-or-after "on/after" :is "is" :is-not "is not" :not-equals "!="
   :not-contains "not contains" :is-any-of "is any of" :is-none-of "is none of"})

;; This is what a table spec should look like, id must be unique. id is for identification
;; :name is used for displaying in UI e.g. dropdown components
;; :type tells the filter component what kind of operator options and value-entry field should be used
(def sample-table-spec
  [{:id :name :name "Name" :type :text}
   {:id :age :name "Age" :type :number}
   {:id :email :name "Email" :type :text}
   {:id :salary :name "Salary" :type :number}
   {:id :department :name "Department" :type :select
    :options [{:id "engineering" :label "Engineering"}
              {:id "marketing" :label "Marketing"}
              {:id "sales" :label "Sales"}]}
   {:id :active :name "Active" :type :boolean}
   {:id :hire-date :name "Hire Date" :type :date}
   {:id :skills :name "Skills" :type :multi-select
    :options [{:id "clojure" :label "Clojure"}
              {:id "javascript" :label "JavaScript"}
              {:id "python" :label "Python"}
              {:id "java" :label "Java"}]}])

;; ----------------------------------------------------------------------------
;; Component Parts Definition for re-com compliance
;; ----------------------------------------------------------------------------

(def table-filter-parts-desc
  (when include-args-desc?
    [{:name :wrapper            :level 0 :class "rc-table-filter-wrapper"      :impl "[v-box]"         :notes "Outer wrapper of the entire table filter component"}
     {:name :header             :level 1 :class "rc-table-filter-header"       :impl "[label]"         :notes "The 'Select rows' header label"}
     {:name :group              :level 1 :class "rc-table-filter-group"        :impl "[v-box]"         :notes "Container for a filter group with blue left border"}
     {:name :filter             :level 2 :class "rc-table-filter-filter"       :impl "[h-box]"         :notes "Container for individual filter condition row"}
     {:name :column-dropdown    :level 3 :class "rc-table-filter-column"       :impl "[dropdown]"      :notes "Dropdown for selecting table columns"}
     {:name :operator-dropdown  :level 3 :class "rc-table-filter-operator"     :impl "[dropdown]"      :notes "Dropdown for selecting filter operators"}
     {:name :value-input        :level 3 :class "rc-table-filter-value"        :impl "[input-text]"    :notes "Input field for filter values (text, date, etc.)"}
     {:name :add-button         :level 2 :class "rc-table-filter-add"          :impl "[button]"        :notes "The '+ Add filter' button and dropdown menu"}
     {:name :clear-button       :level 1 :class "rc-table-filter-clear"        :impl "[button]"        :notes "The 'Clear filters' button"}
     {:name :context-menu       :level 3 :class "rc-table-filter-context"      :impl "[v-box]"         :notes "The '⋯' context menu container"}
     {:name :operator-label     :level 2 :class "rc-table-filter-op-label"     :impl "[button]"        :notes "The 'and'/'or' operator toggle buttons"}
     {:name :where-label        :level 2 :class "rc-table-filter-where"        :impl "[label]"         :notes "The 'Where' label for first filter"}]))

(def table-filter-parts
  (when include-args-desc?
    (-> (map :name table-filter-parts-desc) set)))

(def table-filter-args-desc
  (when include-args-desc?
    [{:name :table-spec      :required true                         :type "vector"           :validate-fn table-spec?                     :description "Vector of column definition maps with :id, :name, :type keys"}
     {:name :model           :required false :default nil           :type "map"              :validate-fn model?                          :description "Hierarchical filter model with :id, :type, and :children structure. If nil, starts with empty filter."}
     {:name :on-change       :required true                         :type "-> nil"           :validate-fn fn?                             :description "Callback function called when filter model changes"}
     {:name :disabled?       :required false :default false         :type "boolean"          :validate-fn boolean?                        :description "If true, disables all filter interactions"}
     {:name :class           :required false                        :type "string"           :validate-fn css-class?                      :description "CSS class names, space separated (applies to wrapper)"}
     {:name :style           :required false                        :type "CSS style map"    :validate-fn css-style?                      :description "CSS styles to apply to wrapper"}
     {:name :attr            :required false                        :type "HTML attr map"    :validate-fn html-attr?                      :description [:span "HTML attributes for wrapper. No " [:code ":class"] " or " [:code ":style"] " allowed"]}
     {:name :parts           :required false                        :type "map"              :validate-fn (parts? table-filter-parts)    :description "Map of part names to {:class :style :attr} for styling customization"}
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
        first-op (first (ops-by-type (:type first-col)))]
    {:id (generate-id) :type :filter :col (:id first-col) :op first-op :val nil}))

(defn empty-group
  "creates a group with only the empty filter in it with unique ID"
  [table-spec]
  {:id (generate-id) :type :group :operator :and :children [(empty-filter table-spec)]})

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
  "Find the parent group that contains the item with target-id"
  [tree target-id]
  (when (= (:type tree) :group)
    (if (some #(= (:id %) target-id) (:children tree))
      tree
      (some #(find-parent-group % target-id) (:children tree)))))

(defn duplicate-item-by-id
  "Duplicate an item and add it to the same parent group"
  [tree target-id]
  (when-let [parent (find-parent-group tree target-id)]
    (when-let [item (some #(when (= (:id %) target-id) %) (:children parent))]
      (let [new-item (assoc item :id (generate-id))]
        (add-child-to-group tree (:id parent) new-item)))))

(defn convert-filter-to-group
  "Convert a filter to a group containing that filter"
  [tree target-id]
  (update-item-by-id tree target-id
    (fn [item]
      (if (= (:type item) :filter)
        {:id (generate-id) :type :group :operator :and
         :children [(assoc item :id (generate-id))]}
        item))))

(defn clean-empty-groups
  "Recursively remove groups with no children, except the root group"
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
  (let [spec (column-by-id table-spec (:col rule))
        {:keys [type valid?]} spec
        op   (:op rule)
        val  (:val rule)]
    (and spec
         (some? val)
         (or (and (fn? valid?) (valid? val))
             (case type
               :text (string? val)
               :number (case op
                         :between (and (vector? val)
                                       (= 2 (count val))
                                       (every? valid-number? val))
                         (valid-number? val))
               :date   (case op
                         :between (and (vector? val)
                                       (= 2 (count val))
                                       (every? valid-date? val))
                         (valid-date? val))
               :select (case op
                         (:is-any-of :is-none-of :contains :not-contains)
                         (and (set? val) (seq val))
                         ;; For single value operators
                         (some? val))
               :multi-select (and (set? val) (seq val))
               :boolean (boolean? val)
               true)))))

;; ----------------------------------------------------------------------------
;; Components
;; ----------------------------------------------------------------------------

(defn value-entry-box
  "Depending on the spec for a given column, the value entry box behaves differently
   There are options for most sql-like types)"
  [row-spec filter-rule on-change & {:keys [parts disabled?]}]
  (let [{:keys [type options]} row-spec
        op  (:op filter-rule)
        val (:val filter-rule)]
    (case type
      :text [input-text/input-text
             :model val
             :on-change #(on-change (assoc filter-rule :val %))
             :width "180px"
             :class (get-in parts [:value-input :class])
             :style (get-in parts [:value-input :style])
             :attr (get-in parts [:value-input :attr])
             :disabled? disabled?]
      :number (cond
                (= op :between)
                [box/h-box
                 :gap "4px"
                 :children [[input-text/input-text :model (first val) :width "60px"
                             :class (get-in parts [:value-input :class])
                             :style (get-in parts [:value-input :style])
                             :attr (get-in parts [:value-input :attr])
                             :disabled? disabled?
                             :on-change #(on-change (assoc filter-rule :val [% (second val)]))]
                            [input-text/input-text :model (second val) :width "60px"
                             :class (get-in parts [:value-input :class])
                             :style (get-in parts [:value-input :style])
                             :attr (get-in parts [:value-input :attr])
                             :disabled? disabled?
                             :on-change #(on-change (assoc filter-rule :val [(first val) %]))]]]
                (= op :expr)
                [input-text/input-text :model val :width "160px"
                 :class (get-in parts [:value-input :class])
                 :style (get-in parts [:value-input :style])
                 :attr (get-in parts [:value-input :attr])
                 :disabled? disabled?
                 :on-change #(on-change (assoc filter-rule :val %))]
                :else [input-text/input-text :model val :width "120px"
                       :class (get-in parts [:value-input :class])
                       :style (get-in parts [:value-input :style])
                       :attr (get-in parts [:value-input :attr])
                       :disabled? disabled?
                       :on-change #(on-change (assoc filter-rule :val %))])
      :date (if (= op :between)
              [daterange/daterange-dropdown
               :model val
               :width "220px"
               :class (get-in parts [:value-input :class])
               :style (get-in parts [:value-input :style])
               :attr (get-in parts [:value-input :attr])
               :disabled? disabled?
               :on-change #(on-change (assoc filter-rule :val %))]
              [datepicker/datepicker-dropdown
               :model val
               :width "150px"
               :class (get-in parts [:value-input :class])
               :style (get-in parts [:value-input :style])
               :attr (get-in parts [:value-input :attr])
               :disabled? disabled?
               :on-change #(on-change (assoc filter-rule :val %))])
      :boolean [dropdown/single-dropdown
                :model val
                :choices [{:id true :label "True"}
                          {:id false :label "False"}]
                :width "120px"
                :class (get-in parts [:value-input :class])
                :style (get-in parts [:value-input :style])
                :attr (get-in parts [:value-input :attr])
                :disabled? disabled?
                :on-change #(on-change (assoc filter-rule :val %))]
      :select (if (#{:is-any-of :is-none-of :contains :not-contains} op)
                ;; Multi-value selection for these operators
                [multi-select/multi-select
                 :model (or val #{})
                 :choices options
                 :placeholder "Select values..."
                 :width "350px"
                 :class (get-in parts [:value-input :class])
                 :style (get-in parts [:value-input :style])
                 :attr (get-in parts [:value-input :attr])
                 :disabled? disabled?
                 :on-change #(on-change (assoc filter-rule :val %))]
                ;; Single value selection for equals/not-equals
                [dropdown/single-dropdown
                 :model val
                 :choices options
                 :width "180px"
                 :class (get-in parts [:value-input :class])
                 :style (get-in parts [:value-input :style])
                 :attr (get-in parts [:value-input :attr])
                 :disabled? disabled?
                 :on-change #(on-change (assoc filter-rule :val %))])
      :multi-select [multi-select/multi-select
                     :model (or val #{})
                     :choices options
                     :width "350px"
                     :class (get-in parts [:value-input :class])
                     :style (get-in parts [:value-input :style])
                     :attr (get-in parts [:value-input :attr])
                     :disabled? disabled?
                     :on-change #(on-change (assoc filter-rule :val %))]
      [text/label :label ""])))

(defn add-filter-dropdown
  "The button to add filters, not an actual dropdown component for visual reason
   Has some JS stuff to add expected click away behaviour"
  [group-id update-fn table-spec depth]
  (let [show-menu? (r/atom false)
        close-menu! #(reset! show-menu? false)]
    (fn [group-id update-fn table-spec depth]
      ;; Add click-away listener when menu is open
      (when @show-menu?
        (js/setTimeout
         #(.addEventListener js/document "click"
                             (fn [e]
                               (when-not (.. e -target (closest ".add-filter-menu, .add-filter-button"))
                                 (close-menu!)))
                             #js {:once true}) 0))
      [box/h-box
       :align :center
       :gap "4px"
       :style {:position "relative"}
       :children [[buttons/button
                   :label "+ Add filter"
                   :class "btn-outline add-filter-button"
                   :style {:font-size "13px" :padding "8px 14px" :color "#475569" :font-weight "500" :border "1px solid #e2e8f0" :border-radius "6px" :background-color "#ffffff"}
                   :on-click #(swap! show-menu? not)]
                  (when @show-menu?
                    [box/v-box
                     :class "add-filter-menu"
                     :style {:position "absolute"
                             :top "100%"
                             :left "0"
                             :z-index "1000"
                             :background-color "#ffffff"
                             :border "1px solid #e1e5e9"
                             :border-radius "8px"
                             :box-shadow "0 8px 16px rgba(0, 0, 0, 0.12)"
                             :min-width "160px"
                             :margin-top "4px"}
                     :children (concat
                                [[buttons/button
                                  :label "Add a filter"
                                  :class "btn-link"
                                  :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151"}
                                  :on-click #(do (close-menu!)
                                                 (update-fn (fn [state]
                                                              (add-child-to-group state group-id (empty-filter table-spec)))))]]
              ;; Only show "Add a filter group" if depth < 2 (max 3 levels: 0, 1, 2)
                                (when (< depth 2)
                                  [[buttons/button
                                    :label "Add a filter group"
                                    :class "btn-link"
                                    :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151"}
                                    :on-click #(do (close-menu!)
                                                   (update-fn (fn [state]
                                                                (add-child-to-group state group-id (empty-group table-spec)))))]]))])]])))

(defn group-context-menu
  "The little ... button for a group
   Also not a re-com/dropdown for UI reasons
   Also has JS clickaway behaviour"
  [group-id update-fn table-spec]
  (let [show-menu? (r/atom false)
        close-menu! #(reset! show-menu? false)]
    (fn [group-id update-fn table-spec]
      ;; Add click-away listener when menu is open
      (when @show-menu?
        (js/setTimeout
         #(.addEventListener js/document "click"
                             (fn [e]
                               (when-not (.. e -target (closest ".group-context-menu, .group-context-button"))
                                 (close-menu!)))
                             #js {:once true}) 0))
      [box/h-box
       :style {:position "relative"}
       :children [[buttons/button
                   :label "⋯"
                   :class "btn-link group-context-button"
                   :style {:color "#9ca3af" :font-size "16px" :padding "6px 8px" :border "none" :background "transparent" :border-radius "4px"}
                   :on-click #(swap! show-menu? not)]
                  (when @show-menu?
                    [box/v-box
                     :class "group-context-menu"
                     :style {:position "absolute"
                             :top "100%"
                             :right "0"
                             :z-index "1000"
                             :background-color "#ffffff"
                             :border "1px solid #e1e5e9"
                             :border-radius "8px"
                             :box-shadow "0 8px 16px rgba(0, 0, 0, 0.12)"
                             :min-width "160px"
                             :margin-top "4px"}
                     :children [[buttons/button
                                 :label "Delete group"
                                 :class "btn-link"
                                 :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#dc2626"}
                                 :on-click #(do (close-menu!)
                                                (update-fn (fn [state]
                                           ;; Safety check: don't delete root group
                                                             (if (= (:id state) group-id)
                                                               state
                                                               (remove-item-with-cleanup state group-id table-spec)))))]]])]])))

(defn filter-context-menu
  "The ... button associated with a single filter
   Also not a re-com/dropdown for UI reasons
   Also has JS clickaway behaviour"
  [item-id update-fn filter-item table-spec depth]
  (let [show-menu? (r/atom false)
        close-menu! #(reset! show-menu? false)]
    (fn [item-id update-fn filter-item table-spec depth]
      ;; Add click-away listener when menu is open
      (when @show-menu?
        (js/setTimeout
         #(.addEventListener js/document "click"
                             (fn [e]
                               (when-not (.. e -target (closest ".filter-context-menu, .filter-context-button"))
                                 (close-menu!)))
                             #js {:once true}) 0))
      [box/h-box
       :style {:position "relative"}
       :children [[buttons/button
                   :label "⋯"
                   :class "btn-link filter-context-button"
                   :style {:color "#9ca3af" :font-size "16px" :padding "6px 8px" :border "none" :background "transparent" :border-radius "4px"}
                   :on-click #(swap! show-menu? not)]
                  (when @show-menu?
                    [box/v-box
                     :class "filter-context-menu"
                     :style {:position "absolute"
                             :top "100%"
                             :right "0"
                             :z-index "1000"
                             :background-color "#ffffff"
                             :border "1px solid #e1e5e9"
                             :border-radius "8px"
                             :box-shadow "0 8px 16px rgba(0, 0, 0, 0.12)"
                             :min-width "160px"
                             :margin-top "4px"}
                     :children [[buttons/button
                                 :label "Remove"
                                 :class "btn-link"
                                 :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#dc2626"}
                                 :on-click #(do (close-menu!)
                                                (update-fn (fn [state] (remove-item-with-cleanup state item-id table-spec))))]
                                [buttons/button
                                 :label "Duplicate"
                                 :class "btn-link"
                                 :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151"}
                                 :on-click #(do (close-menu!)
                                                (update-fn (fn [state] (duplicate-item-by-id state item-id))))]
                                (when (< depth 2) [buttons/button
                                                   :label "Turn into group"
                                                   :class "btn-link"
                                                   :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151"}
                                                   :on-click #(do (close-menu!)
                                                                  (update-fn (fn [state] (convert-filter-to-group state item-id))))])]])]])))

(defn filter-component
  "A single filter, contains a row selection box, an operator selection box, a value entry box and a ... button"
  [table-spec filter-item update-fn depth & {:keys [parts disabled?]}]
  (let [spec (column-by-id table-spec (:col filter-item))
        ops (ops-by-type (:type spec))
        valid? (rule-valid? filter-item table-spec)
        col-opts (mapv #(hash-map :id (:id %) :label (:name %)) table-spec)
        op-opts (mapv #(hash-map :id % :label (get op-label % (name %))) ops)]
    [box/h-box
     :align :center
     :gap "8px"
     :class (get-in parts [:filter :class])
     :style (merge {:padding "10px 16px"
                    :background-color "#ffffff"
                    :border "1px solid #e1e5e9"
                    :border-radius "8px"
                    :margin "4px 0"
                    :box-shadow "0 1px 2px rgba(0, 0, 0, 0.04)"
                    :white-space "nowrap"}
                   (get-in parts [:filter :style]))
     :attr (get-in parts [:filter :attr])
     :children [[dropdown/single-dropdown
                 :model (:col filter-item)
                 :choices col-opts
                 :width "140px"
                 :class (get-in parts [:column-dropdown :class])
                 :style (get-in parts [:column-dropdown :style])
                 :attr (get-in parts [:column-dropdown :attr])
                 :disabled? disabled?
                 :on-change #(let [cs (column-by-id table-spec %)]
                               (update-fn (fn [state] (update-item-by-id state (:id filter-item)
                                                                         (fn [f] (assoc f :col % :op (first (ops-by-type (:type cs))) :val nil))))))]
                [dropdown/single-dropdown
                 :model (:op filter-item)
                 :choices op-opts
                 :width "130px"
                 :class (get-in parts [:operator-dropdown :class])
                 :style (get-in parts [:operator-dropdown :style])
                 :attr (get-in parts [:operator-dropdown :attr])
                 :disabled? disabled?
                 :on-change #(update-fn (fn [state] (update-item-by-id state (:id filter-item) (fn [f] (assoc f :op % :val nil)))))]
                [value-entry-box spec filter-item #(update-fn (fn [state] (update-item-by-id state (:id filter-item) (constantly %)))) :parts parts :disabled? disabled?]
                [filter-context-menu (:id filter-item) update-fn filter-item table-spec depth]
                (when-not valid?
                  [buttons/md-icon-button
                   :md-icon-name "zmdi-alert-triangle"
                   :size :smaller
                   :style {:color "red" :pointer-events "none"}
                   :tooltip "Invalid rule"])]]))

(defn group-component
  "component to group filters together"
  [table-spec group update-fn depth & {:keys [parts disabled?]}]
  (let [children (:children group)
        is-root? (zero? depth)
        show-group-ui? (if is-root?
                         (> (count children) 1)  ; Root group only shows UI when 2+ children
                         true)                    ; Non-root groups always show UI
        indent-px (* depth 60)] ; 60px per nesting level to account for operator space
    [box/v-box
     :style (merge {:padding (if (and show-group-ui? (not is-root?)) "16px" "0")
                    :margin "8px 0"
                    :margin-left (str indent-px "px")
                    :position "relative"}
                   (when (and show-group-ui? (not is-root?))
                     {:background-color "#ffffff"
                      :border "1px solid #e1e5e9"
                      :border-radius "8px"
                      :box-shadow "0 1px 3px rgba(0, 0, 0, 0.06)"
                      :border-left "3px solid #3b82f6"}))
     :gap "4px"
     :children [(when (and show-group-ui? (not is-root?)) ;; Group context menu for non-root groups
                  [box/h-box
                   :style {:position "absolute" :top "0px" :right "8px" :z-index "10"}
                   :children [[group-context-menu (:id group) update-fn table-spec]]])
                [box/v-box
                 :gap "4px"
                 :children (concat
                            (map-indexed
                             (fn [idx child]
                               (let [show-operator? (> idx 0)
                                     show-where? (= idx 0)  ; Show "Where" for first item
                                     operator-btn (when show-operator?
                                                    [buttons/button
                                                     :label (case (:operator group) :and "and" :or "or")
                                                     :class (str "btn-link " (get-in parts [:operator-label :class]))
                                                     :style (merge {:font-size "12px" :font-weight "500" :color "#6b7280"
                                                                    :padding "4px 8px" :margin-right "8px" :margin-left "0px"
                                                                    :background-color "#f8fafc" :border-radius "4px"
                                                                    :border "1px solid #e2e8f0" :min-width "50px"}
                                                                   (get-in parts [:operator-label :style]))
                                                     :attr (get-in parts [:operator-label :attr])
                                                     :disabled? disabled?
                                                     :on-click #(update-fn (fn [state] (update-item-by-id state (:id group) (fn [g] (assoc g :operator (if (= (:operator g) :and) :or :and))))))])
                                     where-label (when show-where?
                                                   [text/label
                                                    :label "Where"
                                                    :class (get-in parts [:where-label :class])
                                                    :style (merge {:font-size "12px" :font-weight "500" :color "#374151"
                                                                   :padding "4px 8px" :margin-right "8px" :margin-left "0px"
                                                                   :min-width "50px" :text-align "center"}
                                                                  (get-in parts [:where-label :style]))
                                                    :attr (get-in parts [:where-label :attr])])]
                                 [box/h-box
                                  :align :center
                                  :gap "8px"
                                  :children (concat
                                             (when where-label [where-label])
                                             (when operator-btn [operator-btn])
                                             [(case (:type child)
                                                :filter [filter-component table-spec child update-fn depth :parts parts :disabled? disabled?]
                                                :group [group-component table-spec child update-fn (inc depth) :parts parts :disabled? disabled?])])]))
                             children)
                            [[add-filter-dropdown (:id group) update-fn table-spec depth]])]]]))

(defn table-filter
  "Hierarchical table filter that works directly with internal tree format.
   Model should be: {:type :group :operator :and :children [...]}"
  [& {:keys [table-spec model] :as args}]
  (or
   (validate-args-macro table-filter-args-desc args)
   (let [internal-model (r/atom (or (deref-or-value model) (empty-group table-spec)))]
     (fn table-filter-render
       [& {:keys [table-spec model on-change disabled? class style attr parts src debug-as]
           :or   {disabled? false}
           :as   args}]
       (or
        (validate-args-macro table-filter-args-desc args)
        (let [current-ext-model (deref-or-value model)]
          ;; Sync external changes to internal state
          (when (not= @internal-model current-ext-model)
            (reset! internal-model (or current-ext-model (empty-group table-spec))))

          (letfn [(update-state! [update-fn]
                    ;; Apply update to current internal model and call user's on-change
                    ;; User's on-change will update external model, sync will handle internal update
                    (let [new-model (update-fn @internal-model)]
                      (when on-change (on-change new-model))))

                  (clear-filters! []
                    ;; Call user's on-change with empty group
                    ;; User's on-change will update external model, sync will handle internal update  
                    (when on-change (on-change (empty-group table-spec))))]

            [box/v-box
             :src      src
             :debug-as (or debug-as (reflect-current-component))
             :class    (str "rc-table-filter-wrapper " (get-in parts [:wrapper :class]) " " class)
             :style    (merge {:border "1px solid #e1e5e9"
                               :border-radius "8px"
                               :padding "20px"
                               :background-color "#ffffff"
                               :box-shadow "0 2px 4px rgba(0, 0, 0, 0.04)"
                               :width "fit-content"
                               :min-width "100%"}
                              (get-in parts [:wrapper :style])
                              style)
             :attr     (merge (->attr args)
                              (get-in parts [:wrapper :attr])
                              attr)
             :children [[text/label
                         :label "Select rows"
                         :class (get-in parts [:header :class])
                         :style (merge {:font-size "14px" :font-weight "600" :color "#374151" :margin-bottom "0px"}
                                       (get-in parts [:header :style]))
                         :attr (get-in parts [:header :attr])]
                        [group-component table-spec @internal-model update-state! 0 :parts parts :disabled? disabled?]
                        [box/h-box
                         :gap "16px"
                         :align :center
                         :justify :between
                         :children [[buttons/button
                                     :label "Clear filters"
                                     :class (str "btn-outline " (get-in parts [:clear-button :class]))
                                     :style (merge {:font-size "13px" :color "#64748b" :font-weight "500" :padding "8px 16px" :border "1px solid #e2e8f0" :border-radius "6px" :background-color "#ffffff"}
                                                   (get-in parts [:clear-button :style]))
                                     :attr (get-in parts [:clear-button :attr])
                                     :disabled? disabled?
                                     :on-click #(clear-filters!)]]]]])))))))