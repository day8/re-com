(ns re-com.table-filter
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [re-com.box :as box]
            [re-com.buttons :as buttons]
            [re-com.checkbox :as checkbox]
            [re-com.datepicker :as datepicker]
            [re-com.daterange :as daterange]
            [re-com.dropdown :as dropdown]
            [re-com.input-text :as input-text]
            [re-com.multi-select :as multi-select]
            [re-com.text :as text]
            [re-com.util :as u :refer [deref-or-value]]
            ))



;; ----------------------------------------------------------------------------
;; Helpers
;; ----------------------------------------------------------------------------

;; ID generation for filters and groups
(defn generate-id []
  (str "item-" (random-uuid)))

;; this is all for the expr option on numeric values
;; which allows for arbitrary math comparisons 
;; e.g. [Age] [expr] [x*3%4>2]
(def number-regex #"^-?\d+(?:\.\d+)?$")

(defn valid-number?
  "True if `s` is a plain numeric string."
  [s]
  (boolean (re-matches number-regex (str s))))

(def comp-ops #{">" "<" ">=" "<=" "=" "!="})
(def arith-ops #{"+" "-" "*" "/" "%" "^"})
(def token-regex #"(<=|>=|!=|[><]=?|=|\d+(?:\.\d+)?|x|[+\-*/%^()])")

(defn tokenize 
  "ensure string only contains allowed characters/tokens"
  [s] 
  (let [s (str/replace (or s "") #"\s+" "") ; remove whitespace
        m (re-seq token-regex s)] 
    (when (= s (apply str (map first m))) (vec (map first m)))))

(defn parse-arith [tokens]
  (loop [ts tokens expect-opnd? true stack []]
    (if (empty? ts)
      (and (not expect-opnd?) (empty? stack))
      (let [t (first ts)]
        (cond
          (= t "(") (recur (rest ts) true (conj stack "("))
          (= t ")") (and (not expect-opnd?) (seq stack)
                         (= (peek stack) "(")
                         (recur (rest ts) false (pop stack)))
          (arith-ops t) (and (not expect-opnd?)
                             (recur (rest ts) true stack))
          (or (= t "x") (valid-number? t)) (and expect-opnd?
                                                (recur (rest ts) false stack))
          :else false)))))

(defn parse-expr
  "Return true if `s` is a valid arithmetic expression optionally
   containing one comparison operator."
  [s]
  (when-let [tokens (tokenize s)]
    (let [pos (first (keep-indexed (fn [i t] (when (comp-ops t) i)) tokens))]
      (if pos
        (and (= 1 (count (filter comp-ops tokens)))
             (parse-arith (if (zero? pos) ["x"] (subvec tokens 0 pos)))
             (parse-arith (subvec tokens (inc pos))))
        (parse-arith tokens)))))

;; 

(defn valid-date?
  "True if `d` is a non-nil value (datepickers handle validation)."
  [d]
  (some? d))


;; dropdown options for valid operations for sql value types
(def ops-by-type
  {:text         [:contains :equals :starts-with :ends-with]
   :number       [:equals :> :>= :< :<= :between :expr]
   :date         [:before :after :on :on-or-before :on-or-after :between]
   :boolean      [:is :is-not]
   :select       [:equals :not-equals :contains :not-contains :is-any-of :is-none-of]
   :multi-select [:contains :not-contains]})

(def op-label
  {:contains "contains" :equals "equals" :starts-with "starts" :ends-with "ends"
   :> ">" :>= ">=" :< "<" :<= "<=" :between "between" :expr "expr"
   :before "before" :after "after" :on "on" :on-or-before "on/before"
   :on-or-after "on/after" :is "is" :is-not "is not" :not-equals "!="
   :not-contains "not contains" :is-any-of "is any of" :is-none-of "is none of"})

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

;; This component uses a single data format:
;; Tree structure with metadata and IDs for UI rendering
;; {:id "group-123" :type :group :operator :and 
;;  :children [{:id "filter-456" :type :filter :col :name :op :contains :val "John"}]}

;; ID-based operations for tree manipulation
(defn find-item-by-id 
  "Recursively search for an item by ID in the tree"
  [tree target-id]
  (when tree
    (if (= (:id tree) target-id)
      tree
      (when (= (:type tree) :group)
        (some #(find-item-by-id % target-id) (:children tree))))))

(defn update-item-by-id 
  "Update an item by ID in the tree"
  [tree target-id update-fn]
  (if (= (:id tree) target-id)
    (update-fn tree)
    (if (= (:type tree) :group)
      (assoc tree :children 
             (mapv #(update-item-by-id % target-id update-fn) (:children tree)))
      tree)))

(defn remove-item-by-id 
  "Remove an item by ID from the tree"
  [tree target-id]
  (if (= (:type tree) :group)
    (assoc tree :children 
           (vec (keep #(when (not= (:id %) target-id)
                         (remove-item-by-id % target-id)) 
                      (:children tree))))
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
  (when-let [item (find-item-by-id tree target-id)]
    (when-let [parent (find-parent-group tree target-id)]
      (let [new-item (assoc item :id (generate-id))]
        (add-child-to-group tree (:id parent) new-item)))))

(defn convert-filter-to-group 
  "Convert a filter to a group containing that filter"
  [tree target-id]
  (when-let [item (find-item-by-id tree target-id)]
    (when (= (:type item) :filter)
      (let [new-group {:id (generate-id) :type :group :operator :and 
                       :children [(assoc item :id (generate-id))]}]
        (update-item-by-id tree target-id (constantly new-group))))))

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
               :number (case op
                         :between (and (vector? val)
                                       (= 2 (count val))
                                       (every? valid-number? val))
                         :expr (and (string? val) (parse-expr val))
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
               true)))))

;; ----------------------------------------------------------------------------
;; Components
;; ----------------------------------------------------------------------------

(defn value-entry-box
  "Depending on the spec for a given column, "
  [row-spec filter-rule on-change]
  (let [{:keys [type options]} row-spec
        op  (:op filter-rule)
        val (:val filter-rule)]
    (case type
      :text [input-text/input-text
             :model val
             :on-change #(on-change (assoc filter-rule :val %))
             :width "180px"]
      :number (cond
                (= op :between)
                [box/h-box
                 :gap "4px"
                 :children [[input-text/input-text :model (first val) :width "60px"
                             :on-change #(on-change (assoc filter-rule :val [% (second val)]))]
                            [input-text/input-text :model (second val) :width "60px"
                             :on-change #(on-change (assoc filter-rule :val [(first val) %]))]]]
                (= op :expr)
                [input-text/input-text :model val :width "160px"
                 :on-change #(on-change (assoc filter-rule :val %))]
                :else [input-text/input-text :model val :width "120px"
                       :on-change #(on-change (assoc filter-rule :val %))])
      :date (if (= op :between)
              [daterange/daterange-dropdown
               :model val
               :width "220px"
               :on-change #(on-change (assoc filter-rule :val %))]
              [datepicker/datepicker-dropdown 
               :model val 
               :width "150px"
               :on-change #(on-change (assoc filter-rule :val %))])
      :boolean [checkbox/checkbox :model (boolean val)
                :on-change #(on-change (assoc filter-rule :val %))]
      :select (if (#{:is-any-of :is-none-of :contains :not-contains} op)
                ;; Multi-value selection for these operators
                [multi-select/multi-select
                 :model (or val #{})
                 :choices options
                 :placeholder "Select values..."
                 :width "350px"
                 :on-change #(on-change (assoc filter-rule :val %))]
                ;; Single value selection for equals/not-equals
                [dropdown/single-dropdown
                 :model val
                 :choices options
                 :width "180px"
                 :on-change #(on-change (assoc filter-rule :val %))])
      :multi-select [multi-select/multi-select
                     :model (or val #{})
                     :choices options
                     :width "350px"
                     :on-change #(on-change (assoc filter-rule :val %))]
      [text/label :label ""])))

(defn add-filter-dropdown [group-id update-fn table-spec depth]
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
       :children [
         [buttons/button
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

(defn group-context-menu [group-id update-fn table-spec]
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
       :children [
         [buttons/button
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
            :children [
              [buttons/button
               :label "Delete group"
               :class "btn-link"
               :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#dc2626"}
               :on-click #(do (close-menu!)
                              (update-fn (fn [state] 
                                           ;; Safety check: don't delete root group
                                           (if (= (:id state) group-id)
                                             state
                                             (remove-item-with-cleanup state group-id table-spec)))))]]])]])))

(defn filter-context-menu [item-id update-fn filter-item table-spec]
  (let [show-menu? (r/atom false)
        close-menu! #(reset! show-menu? false)]
    (fn [item-id update-fn filter-item table-spec]
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
       :children [
         [buttons/button
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
            :children [
              [buttons/button
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
              [buttons/button
               :label "Turn into group"
               :class "btn-link"
               :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151"}
               :on-click #(do (close-menu!)
                              (update-fn (fn [state] (convert-filter-to-group state item-id))))]]])]])))

(defn filter-component [table-spec filter-item update-fn]
  (let [spec (column-by-id table-spec (:col filter-item))
        ops (ops-by-type (:type spec))
        valid? (rule-valid? filter-item table-spec)
        col-opts (mapv #(hash-map :id (:id %) :label (:name %)) table-spec)
        op-opts (mapv #(hash-map :id % :label (get op-label % (name %))) ops)]
    [box/h-box
     :align :center
     :gap "8px"
     :style {:padding "10px 16px"
             :background-color "#ffffff"
             :border "1px solid #e1e5e9"
             :border-radius "8px"
             :margin "4px 0"
             :box-shadow "0 1px 2px rgba(0, 0, 0, 0.04)"
             :white-space "nowrap"}
     :children [[dropdown/single-dropdown
                 :model (:col filter-item)
                 :choices col-opts
                 :width "140px"
                 :on-change #(let [cs (column-by-id table-spec %)]
                               (update-fn (fn [state] (update-item-by-id state (:id filter-item)
                                                                        (fn [f] (assoc f :col % :op (first (ops-by-type (:type cs))) :val nil))))))]
                [dropdown/single-dropdown
                 :model (:op filter-item)
                 :choices op-opts
                 :width "130px"
                 :on-change #(update-fn (fn [state] (update-item-by-id state (:id filter-item) (fn [f] (assoc f :op % :val nil)))))]
                [value-entry-box spec filter-item #(update-fn (fn [state] (update-item-by-id state (:id filter-item) (constantly %))))]
                [filter-context-menu (:id filter-item) update-fn filter-item table-spec]
                (when-not valid?
                  [buttons/md-icon-button
                   :md-icon-name "zmdi-alert-triangle"
                   :size :smaller
                   :style {:color "red" :pointer-events "none"}
                   :tooltip "Invalid rule"])
                ]]))

(defn group-component [table-spec group update-fn depth]
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
     :children [
       ;; Group context menu for non-root groups
       (when (and show-group-ui? (not is-root?))
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
                                             :class "btn-link"
                                             :style {:font-size "12px" :font-weight "500" :color "#6b7280" 
                                                    :padding "4px 8px" :margin-right "8px" :margin-left "0px"
                                                    :background-color "#f8fafc" :border-radius "4px" 
                                                    :border "1px solid #e2e8f0" :min-width "50px"}
                                             :on-click #(update-fn (fn [state] (update-item-by-id state (:id group) (fn [g] (assoc g :operator (if (= (:operator g) :and) :or :and))))))])
                              where-label (when show-where?
                                           [text/label 
                                            :label "Where"
                                            :style {:font-size "12px" :font-weight "500" :color "#374151" 
                                                   :padding "4px 8px" :margin-right "8px" :margin-left "0px"
                                                   :min-width "50px" :text-align "center"}])]
                          [box/h-box
                           :align :center
                           :gap "8px"
                           :children (concat
                                      (when where-label [where-label])
                                      (when operator-btn [operator-btn])
                                      [(case (:type child)
                                         :filter [filter-component table-spec child update-fn]
                                         :group [group-component table-spec child update-fn (inc depth)])])]))
                      children)
                    [[add-filter-dropdown (:id group) update-fn table-spec depth]])]]]))

(defn table-filter
  "Hierarchical table filter that works directly with internal tree format.
   Model should be: {:type :group :operator :and :children [...]}"
  [table-spec model on-change & {:keys [class style attr]}]
  (let [state (r/atom (or (deref-or-value model) (empty-group table-spec)))]
    (fn table-filter-render
      [table-spec model on-change & {:keys [class style attr]}]
      (let [latest-model (deref-or-value model)]
        ;; Sync external changes to internal state
        (when (and latest-model (not= latest-model @state))
          (reset! state latest-model))
        
        (letfn [(update-state! [update-fn] 
                  (swap! state update-fn)
                  (on-change @state))

                (clear-filters! [] 
                  (reset! state (empty-group table-spec))
                  (on-change (empty-group table-spec)))]

          [box/v-box
           :class class
           :style (merge {:border "1px solid #e1e5e9"
                          :border-radius "8px"
                          :padding "20px"
                          :background-color "#ffffff"
                          :box-shadow "0 2px 4px rgba(0, 0, 0, 0.04)"
                          :width "fit-content"
                          :min-width "100%"}
                         style)
           :attr attr
           :children [[text/label 
                       :label "Select rows"
                       :style {:font-size "14px" :font-weight "600" :color "#374151" :margin-bottom "0px"}]
                      [group-component table-spec @state update-state! 0]
                      [box/h-box
                       :gap "16px"
                       :align :center
                       :justify :between
                       :children [[buttons/button
                                   :label "Clear filters"
                                   :class "btn-outline"
                                   :style {:font-size "13px" :color "#64748b" :font-weight "500" :padding "8px 16px" :border "1px solid #e2e8f0" :border-radius "6px" :background-color "#ffffff"}
                                   :on-click clear-filters!]]]]])))))