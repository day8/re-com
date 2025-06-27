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
            [re-com.text :as text]))

;; ----------------------------------------------------------------------------
;; Helpers
;; ----------------------------------------------------------------------------

(def number-re #"^-?\d+(?:\.\d+)?$")

(defn valid-number?
  "True if `s` is a plain numeric string."
  [s]
  (boolean (re-matches number-re (str s))))

(def comp-ops #{">" "<" ">=" "<=" "=" "!="})
(def arith-ops #{"+" "-" "*" "/" "%" "^"})
(def token-re #"(<=|>=|!=|[><]=?|=|\d+(?:\.\d+)?|x|[+\-*/%^()])")

(defn tokenize [s]
  (let [s (str/replace (or s "") #"\s+" "")
        m (re-seq token-re s)]
    (when (= s (apply str m)) (vec m))))

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

(defn valid-date?
  "True if `d` can be converted to a Date."
  [d]
  (try
    (cond
      (instance? js/Date d) true
      (string? d) (not (js/isNaN (js/Date.parse d)))
      :else false)
    (catch :default _ false)))

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

(defn column-by-id [table-spec id]
  (some #(when (= (:id %) id) %) table-spec))

;; ----------------------------------------------------------------------------
;; Data Structure - Hierarchical Groups
;; ----------------------------------------------------------------------------

(defn empty-filter [table-spec]
  (let [first-col (first table-spec)
        first-op (first (ops-by-type (:type first-col)))]
    {:type :filter :col (:id first-col) :op first-op :val nil}))

(defn empty-group [table-spec]
  {:type :group :operator :and :children [(empty-filter table-spec)]})

(defn external->internal
  "Convert external model to hierarchical internal structure"
  [external table-spec]
  (letfn [(convert-item [item]
            (cond
              (:col item) (assoc item :type :filter)
              (:and item) {:type :group :operator :and :children (mapv convert-item (:and item))}
              (:or item) {:type :group :operator :or :children (mapv convert-item (:or item))}
              :else nil))]
    (cond
      (nil? external) (empty-group table-spec)
      (:col external) {:type :group :operator :and :children [(assoc external :type :filter)]}
      (:and external) {:type :group :operator :and :children (mapv convert-item (:and external))}
      (:or external) {:type :group :operator :or :children (mapv convert-item (:or external))}
      :else (empty-group table-spec))))

(defn internal->external
  "Convert hierarchical internal structure back to external model"
  [internal]
  (letfn [(convert-item [item]
            (case (:type item)
              :filter (dissoc item :type)
              :group (let [children (mapv convert-item (:children item))]
                       (when (seq children)
                         (if (= 1 (count children))
                           (first children)
                           {(:operator item) children})))
              nil))]
    (when internal
      (let [result (convert-item internal)]
        (cond
          (nil? result) nil
          (and (map? result) (or (:and result) (:or result))) result
          (map? result) result
          :else nil)))))

;; Path-based operations for nested updates
(defn update-at-path [state path update-fn]
  (if (empty? path)
    (update-fn state)
    (update-in state path update-fn)))

(defn remove-at-path [state path]
  (let [parent-path (butlast path)
        index (last path)]
    (if (empty? parent-path)
      state
      (update-in state (concat parent-path [:children])
                 (fn [children] (vec (concat (subvec children 0 index)
                                           (subvec children (inc index)))))))))

(defn add-at-path [state path item]
  (if (empty? path)
    state
    (update-in state path (fn [children] (conj (or children []) item)))))

;; ----------------------------------------------------------------------------
;; Validation  
;; ----------------------------------------------------------------------------

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

(defn value-editor [spec rule on-change]
  (let [{:keys [type options]} spec
        op  (:op rule)
        val (:val rule)]
    (case type
      :text [input-text/input-text
             :model val
             :on-change #(on-change (assoc rule :val %))
             :width "120px"]
      :number (cond
                (= op :between)
                [box/h-box
                 :gap "4px"
                 :children [[input-text/input-text :model (first val) :width "60px"
                             :on-change #(on-change (assoc rule :val [% (second val)]))]
                            [input-text/input-text :model (second val) :width "60px"
                             :on-change #(on-change (assoc rule :val [(first val) %]))]]]
                (= op :expr)
                [input-text/input-text :model val :width "120px"
                 :on-change #(on-change (assoc rule :val %))]
                :else [input-text/input-text :model val :width "80px"
                       :on-change #(on-change (assoc rule :val %))])
      :date (if (= op :between)
              [daterange/daterange-dropdown
               :model val
               :width "200px"
               :on-change #(on-change (assoc rule :val %))]
              [datepicker/datepicker-dropdown 
               :model val 
               :width "120px"
               :on-change #(on-change (assoc rule :val %))])
      :boolean [checkbox/checkbox :model (boolean val)
                :on-change #(on-change (assoc rule :val %))]
      :select (if (#{:is-any-of :is-none-of :contains :not-contains} op)
                ;; Multi-value selection for these operators
                [multi-select/multi-select
                 :model (or val #{})
                 :choices options
                 :placeholder "Select values..."
                 :width "200px"
                 :on-change #(on-change (assoc rule :val %))]
                ;; Single value selection for equals/not-equals
                [dropdown/single-dropdown
                 :model val
                 :choices options
                 :width "120px"
                 :on-change #(on-change (assoc rule :val %))])
      :multi-select [multi-select/multi-select
                     :model (or val #{})
                     :choices options
                     :on-change #(on-change (assoc rule :val %))]
      [text/label :label ""])))

(defn add-filter-dropdown [path update-fn table-spec]
  (let [show-menu? (r/atom false)]
    (fn [path update-fn table-spec]
      [box/h-box
       :align :center
       :gap "4px"
       :style {:position "relative"}
       :children [
         [buttons/button
          :label "+ Add filter"
          :class "btn-outline"
          :style {:font-size "13px" :padding "8px 14px" :color "#475569" :font-weight "500" :border "1px solid #e2e8f0" :border-radius "6px" :background-color "#ffffff" :hover {:background-color "#f8fafc"}}
          :on-click #(swap! show-menu? not)]
         (when @show-menu?
           [box/v-box
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
            :children [
              [buttons/button
               :label "Add a filter"
               :class "btn-link"
               :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151" :hover {:background-color "#f8fafc"}}
               :on-click #(do (reset! show-menu? false)
                              (update-fn (fn [state]
                                           (let [current-children (get-in state (conj path :children))
                                                 is-root? (empty? path)
                                                 should-auto-group? (and is-root? (= (count current-children) 1))]
                                             (if should-auto-group?
                                               ;; Auto-create group when adding second rule at root
                                               (update-at-path state path 
                                                             (fn [group] 
                                                               (assoc group :children 
                                                                     [(assoc (first current-children) :type :filter)
                                                                      (empty-filter table-spec)])))
                                               ;; Normal add
                                               (add-at-path state (conj path :children) (empty-filter table-spec)))))))]
              [buttons/button
               :label "Add a filter group"
               :class "btn-link"
               :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151" :hover {:background-color "#f8fafc"}}
               :on-click #(do (reset! show-menu? false)
                              (update-fn (fn [state] (add-at-path state (conj path :children) (empty-group table-spec)))))]]])]])))

(defn filter-context-menu [path update-fn filter-item]
  (let [show-menu? (r/atom false)]
    (fn [path update-fn filter-item]
      [box/h-box
       :style {:position "relative"}
       :children [
         [buttons/button
          :label "⋯"
          :class "btn-link"
          :style {:color "#9ca3af" :font-size "16px" :padding "6px 8px" :border "none" :background "transparent" :border-radius "4px" :hover {:background-color "#f1f5f9" :color "#64748b"}}
          :on-click #(swap! show-menu? not)]
         (when @show-menu?
           [box/v-box
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
               :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#dc2626" :hover {:background-color "#fef2f2"}}
               :on-click #(do (reset! show-menu? false)
                              (update-fn (fn [state] (remove-at-path state path))))]
              [buttons/button
               :label "Duplicate"
               :class "btn-link"
               :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151" :hover {:background-color "#f8fafc"}}
               :on-click #(do (reset! show-menu? false)
                              (let [parent-path (butlast path)
                                    index (last path)]
                                (update-fn (fn [state] (add-at-path state (conj parent-path :children) filter-item)))))]
              [buttons/button
               :label "Turn into group"
               :class "btn-link"
               :style {:text-align "left" :padding "10px 16px" :border "none" :width "100%" :font-size "13px" :font-weight "500" :color "#374151" :hover {:background-color "#f8fafc"}}
               :on-click #(do (reset! show-menu? false)
                              (update-fn (fn [state] (update-at-path state path (fn [_] {:type :group :operator :and :children [filter-item]})))))]]])]])))

(defn filter-component [table-spec filter-item path update-fn]
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
             :box-shadow "0 1px 2px rgba(0, 0, 0, 0.04)"}
     :children [[dropdown/single-dropdown
                   :model (:col filter-item)
                   :choices col-opts
                   :width "120px"
                   :on-change #(let [cs (column-by-id table-spec %)]
                                 (update-fn (fn [state] (update-at-path state path
                                                                      (fn [f] (assoc f :col % :op (first (ops-by-type (:type cs))) :val nil))))))]
                  [dropdown/single-dropdown
                   :model (:op filter-item)
                   :choices op-opts
                   :width "110px"
                   :on-change #(update-fn (fn [state] (update-at-path state path (fn [f] (assoc f :op % :val nil)))))]
                  [value-editor spec filter-item #(update-fn (fn [state] (update-at-path state path (constantly %))))]
                  [filter-context-menu path update-fn filter-item]
                  (when-not valid?
                    [buttons/md-icon-button 
                     :md-icon-name "zmdi-alert-triangle" 
                     :size :smaller
                     :style {:color "red" :pointer-events "none"}
                     :tooltip "Invalid rule"])]]))


(declare group-component)

(defn group-component [table-spec group path update-fn depth]
  (let [children (:children group)
        is-root? (zero? depth)
        show-group-ui? (> (count children) 1)
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
       [box/v-box
        :gap "4px"
        :children (concat
                    (map-indexed
                      (fn [idx child]
                        (let [child-path (conj path :children idx)
                              show-operator? (> idx 0)
                              operator-btn (when show-operator?
                                            [buttons/button
                                             :label (case (:operator group) :and "and" :or "or")
                                             :class "btn-link"
                                             :style {:font-size "12px" :font-weight "500" :color "#6b7280" 
                                                    :padding "4px 8px" :margin-right "8px" :margin-left "0px"
                                                    :background-color "#f8fafc" :border-radius "4px" 
                                                    :border "1px solid #e2e8f0" :min-width "50px"}
                                             :on-click #(update-fn (fn [state] (update-at-path state path (fn [g] (assoc g :operator (if (= (:operator g) :and) :or :and))))))])]
                          [box/h-box
                           :align :center
                           :gap "8px"
                           :children (concat
                                      (when operator-btn [operator-btn])
                                      [(case (:type child)
                                         :filter [filter-component table-spec child child-path update-fn]
                                         :group [group-component table-spec child child-path update-fn (inc depth)])])]))
                      children)
                    [[add-filter-dropdown path update-fn table-spec]])]]]))

(defn table-filter
  "Notion-style hierarchical table filter with true group-based filtering."
  [table-spec model callback & {:keys [class style attr]}]
  (let [ext-model (r/atom model)
        state (r/atom (external->internal model table-spec))]
    (fn table-filter-render
      [table-spec model callback & {:keys [class style attr]}]
      (when (not= model @ext-model)
        (reset! ext-model model)
        (reset! state (external->internal model table-spec)))
      (letfn [(update-state! [update-fn]
                (swap! state update-fn)
                (let [new-external (internal->external @state)]
                  (reset! ext-model new-external)
                  (callback new-external)))
              (clear-filters! []
                (reset! state (empty-group table-spec))
                (reset! ext-model nil)
                (callback nil))]
        
        [box/v-box
         :class class
         :style (merge {:border "1px solid #e1e5e9" 
                        :border-radius "8px" 
                        :padding "20px" 
                        :background-color "#ffffff"
                        :box-shadow "0 2px 4px rgba(0, 0, 0, 0.04)"} 
                       style)
         :attr attr
         :gap "12px"
         :children [
           [group-component table-spec @state [] update-state! 0]
           [box/h-box
            :gap "16px"
            :align :center
            :justify :between
            :children [
              [buttons/button
               :label "Clear filters"
               :class "btn-outline"
               :style {:font-size "13px" :color "#64748b" :font-weight "500" :padding "8px 16px" :border "1px solid #e2e8f0" :border-radius "6px" :background-color "#ffffff"}
               :on-click clear-filters!]]]]]))))