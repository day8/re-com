(ns re-com.table-filter
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [re-com.box :as box]
            [re-com.buttons :as buttons]
            [re-com.checkbox :as checkbox]
            [re-com.datepicker :as datepicker]
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
   :select       [:equals :not-equals]
   :multi-select [:contains :not-contains]})

(def op-label
  {:contains "contains" :equals "equals" :starts-with "starts" :ends-with "ends"
   :> ">" :>= ">=" :< "<" :<= "<=" :between "between" :expr "expr"
   :before "before" :after "after" :on "on" :on-or-before "on/before"
   :on-or-after "on/after" :is "is" :is-not "is not" :not-equals "!="
   :not-contains "not contains"})

(defn column-by-id [table-spec id]
  (some #(when (= (:id %) id) %) table-spec))

;; ----------------------------------------------------------------------------
;; Data Structure - Flat List with Logical Nesting
;; ----------------------------------------------------------------------------

(defn empty-rule [table-spec]
  (let [first-col (first table-spec)
        first-op (first (ops-by-type (:type first-col)))]
    {:type :rule :col (:id first-col) :op first-op :val nil :level 0}))

(defn model->internal 
  "Convert external model to flat internal structure"
  [m table-spec]
  (letfn [(flatten-rules [items level operator]
            (mapcat (fn [item]
                      (cond
                        (:col item) [(assoc item :type :rule :level level :operator operator)]
                        (:and item) (flatten-rules (:and item) level :and)
                        (:or item) (flatten-rules (:or item) level :or)
                        :else [])) items))]
    (cond
      (nil? m) [(empty-rule table-spec)]
      (:col m) [(assoc m :type :rule :level 0 :operator nil)]
      (:and m) (let [rules (flatten-rules (:and m) 0 :and)]
                 (if (empty? rules) [(empty-rule table-spec)]
                     (assoc-in (vec rules) [0 :operator] nil)))
      (:or m) (let [rules (flatten-rules (:or m) 0 :or)]
                (if (empty? rules) [(empty-rule table-spec)]
                    (assoc-in (vec rules) [0 :operator] nil)))
      :else [(empty-rule table-spec)])))

(defn internal->model 
  "Convert flat internal structure back to external model"
  [rules]
  (letfn [(group-by-level [rules]
            (if (= 1 (count rules))
              (let [rule (first rules)]
                (dissoc rule :type :level :operator))
              (let [grouped (group-by :operator rules)
                    and-rules (get grouped :and [])
                    or-rules (get grouped :or [])
                    nil-rules (get grouped nil [])]
                (cond
                  (and (seq and-rules) (empty? or-rules))
                  {:and (mapv #(dissoc % :type :level :operator) (concat nil-rules and-rules))}
                  
                  (and (seq or-rules) (empty? and-rules))
                  {:or (mapv #(dissoc % :type :level :operator) (concat nil-rules or-rules))}
                  
                  :else
                  {:and (mapv #(dissoc % :type :level :operator) rules)}))))]
    (when (seq rules)
      (let [clean-rules (remove #(nil? (:col %)) rules)]
        (when (seq clean-rules)
          (if (= 1 (count clean-rules))
            (dissoc (first clean-rules) :type :level :operator)
            (group-by-level clean-rules)))))))

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
              [box/h-box
               :gap "4px"
               :children [[datepicker/datepicker :model (first val)
                           :on-change #(on-change (assoc rule :val [% (second val)]))]
                          [datepicker/datepicker :model (second val)
                           :on-change #(on-change (assoc rule :val [(first val) %]))]]]
              [datepicker/datepicker :model val :on-change #(on-change (assoc rule :val %))])
      :boolean [checkbox/checkbox :model (boolean val)
                :on-change #(on-change (assoc rule :val %))]
      :select [dropdown/single-dropdown
               :model val
               :choices options
               :on-change #(on-change (assoc rule :val %))]
      :multi-select [multi-select/multi-select
                     :model (or val #{})
                     :choices options
                     :on-change #(on-change (assoc rule :val %))]
      [text/label :label ""])))

(defn add-buttons [on-add-and on-add-or]
  [box/h-box
   :align :center
   :gap "4px"
   :children [
     [buttons/button
      :label "+ AND"
      :class "btn-outline"
      :style {:font-size "11px" :padding "2px 6px"}
      :on-click on-add-and]
     [buttons/button
      :label "+ OR" 
      :class "btn-outline"
      :style {:font-size "11px" :padding "2px 6px"}
      :on-click on-add-or]]])

(defn rule-row [table-spec rule rule-idx update-rule! remove-rule!]
  (let [spec (column-by-id table-spec (:col rule))
        ops (ops-by-type (:type spec))
        valid? (rule-valid? rule table-spec)
        col-opts (mapv #(hash-map :id (:id %) :label (:name %)) table-spec)
        op-opts (mapv #(hash-map :id % :label (get op-label % (name %))) ops)
        level (:level rule 0)
        operator (:operator rule)]
    [box/h-box
     :align :center
     :gap "6px"
     :style {:padding "8px" 
             :margin-left (str (* level 20) "px")
             :background-color (if (> level 0) "#f8f9fa" "#fff")
             :border "1px solid #e0e0e0"
             :border-radius "4px"
             :margin-bottom "4px"}
     :children [
       (when operator
         [text/label 
          :label (case operator :and "AND" :or "OR" "")
          :style {:font-weight "bold" 
                  :color (case operator :and "#1976d2" :or "#388e3c" "#666")
                  :margin-right "8px"
                  :font-size "12px"}])
       [dropdown/single-dropdown
        :model (:col rule)
        :choices col-opts
        :width "120px"
        :on-change #(let [cs (column-by-id table-spec %)]
                      (update-rule! rule-idx
                                    (assoc rule :col % :op (first (ops-by-type (:type cs))) :val nil)))]
       [dropdown/single-dropdown
        :model (:op rule)
        :choices op-opts
        :width "110px"
        :on-change #(update-rule! rule-idx (assoc rule :op % :val nil))]
       [value-editor spec rule #(update-rule! rule-idx %)]
       [buttons/md-icon-button 
        :md-icon-name "zmdi-delete" 
        :size :smaller
        :style {:color "#d32f2f"}
        :tooltip "Remove rule"
        :on-click #(remove-rule! rule-idx)]
       (when-not valid?
         [buttons/md-icon-button 
          :md-icon-name "zmdi-alert-triangle" 
          :size :smaller
          :style {:color "red" :pointer-events "none"}
          :tooltip "Invalid rule"])]]))

(defn table-filter
  "Intuitive table filter with non-disruptive nesting."
  [table-spec model callback & {:keys [class style attr]}]
  (let [ext-model (r/atom model)
        state (r/atom (model->internal model table-spec))]
    (fn table-filter-render
      [table-spec model callback & {:keys [class style attr]}]
      (when (not= model @ext-model)
        (reset! ext-model model)
        (reset! state (model->internal model table-spec)))
      (letfn [(update-rule! [idx new-rule]
                (swap! state assoc idx new-rule)
                (callback (internal->model @state)))
              (remove-rule! [idx]
                (swap! state #(vec (concat (subvec % 0 idx) (subvec % (inc idx)))))
                (callback (internal->model @state)))
              (add-rule-after! [idx operator]
                (let [current-rule (nth @state idx)
                      current-level (:level current-rule)
                      new-rule (assoc (empty-rule table-spec) 
                                     :level current-level 
                                     :operator operator)
                      insert-pos (inc idx)]
                  (swap! state #(vec (concat (subvec % 0 insert-pos)
                                             [new-rule]
                                             (subvec % insert-pos))))
                  (callback (internal->model @state))))
              (add-rule-nested! [idx operator]
                (let [current-rule (nth @state idx)
                      current-level (:level current-rule)
                      new-rule (assoc (empty-rule table-spec) 
                                     :level (inc current-level) 
                                     :operator operator)
                      insert-pos (inc idx)]
                  (swap! state #(vec (concat (subvec % 0 insert-pos)
                                             [new-rule]
                                             (subvec % insert-pos))))
                  (callback (internal->model @state))))
              (add-rule-end! [operator]
                (let [new-rule (assoc (empty-rule table-spec) :level 0 :operator operator)]
                  (swap! state #(conj % new-rule))
                  (callback (internal->model @state))))]
        
        [box/v-box
         :class class
         :style (merge {:border "1px solid #ddd" 
                        :border-radius "4px" 
                        :padding "12px" 
                        :background-color "#fafafa"} 
                       style)
         :attr attr
         :gap "8px"
         :children [
           [box/v-box
            :gap "2px"
            :children (concat
                        (map-indexed 
                          (fn [idx rule]
                            [box/v-box
                             :gap "4px"
                             :children [
                               [rule-row table-spec rule idx update-rule! remove-rule!]
                               [box/h-box
                                :gap "8px"
                                :style {:margin-left (str (+ (* (:level rule 0) 20) 20) "px")}
                                :children [
                                  [text/label :label "Continue:" :style {:font-size "10px" :color "#888"}]
                                  [add-buttons #(add-rule-after! idx :and) #(add-rule-after! idx :or)]
                                  [text/label :label "Group:" :style {:font-size "10px" :color "#888"}]
                                  [add-buttons #(add-rule-nested! idx :and) #(add-rule-nested! idx :or)]]]]])
                          @state)
                        (when (empty? @state)
                          [[add-buttons #(add-rule-end! :and) #(add-rule-end! :or)]]))]
           [box/h-box
            :gap "12px"
            :align :center
            :children [
              [buttons/button
               :label "Clear all"
               :class "btn-outline"
               :on-click #(do 
                            (reset! state [(empty-rule table-spec)])
                            (reset! ext-model nil)
                            (callback nil))]
              (when (seq @state)
                [box/h-box
                 :gap "4px"
                 :align :center
                 :children [
                   [text/label :label "Add to end:" :style {:font-size "11px" :color "#666"}]
                   [add-buttons #(add-rule-end! :and) #(add-rule-end! :or)]]])]]]]))))