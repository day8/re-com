(ns re-com.tree-select
  (:require-macros
   [re-com.core :refer [handler-fn at]])
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [reagent.core          :as r]
   [re-com.config         :refer [include-args-desc?]]
   [re-com.dropdown       :as dd]
   [re-com.util           :refer [deref-or-value remove-id-item ->v] :as u]
   [re-com.box            :refer [h-box v-box box gap]]
   [re-com.checkbox       :refer [checkbox]]
   [re-com.validate       :as validate :refer [css-style? html-attr? parts? part?] :refer-macros [validate-args-macro]]
   [re-com.theme :as theme]))

(def tree-select-dropdown-parts-desc
  (when include-args-desc?
    [{:class "rc-dropdown"
      :impl "[tree-select-dropdown]"
      :level 0
      :type :legacy}
     {:class "rc-tree-select-dropdown-wrapper"
      :impl  "[:div]"
      :level 1
      :name  :wrapper}
     {:class "rc-tree-select-dropdown-anchor"
      :impl  "[h-box]"
      :level 2
      :name  :anchor}
     {:class "rc-tree-select-dropdown-counter"
      :impl  "[box]"
      :level 3
      :name  :counter}
     {:class "rc-tree-select-dropdown-anchor-expander"
      :impl  "[box]"
      :level 3
      :name  :anchor-expander}
     {:class "rc-tree-select-dropdown-backdrop"
      :impl  "[:div]"
      :level 2
      :name  :backdrop}
     {:class "rc-tree-select-dropdown-dropdown-wrapper"
      :impl  "[v-box]"
      :level 2
      :name  :dropdown-wrapper}
     {:class "rc-tree-select-dropdown-dropdown-indicator"
      :impl  "[v-box]"
      :level 3
      :name  :dropdown-indicator}
     {:class "rc-tree-select-dropdown-body"
      :impl  "[tree-select]"
      :level 3
      :name  :body}]))

(def tree-select-dropdown-parts
  (when include-args-desc?
    (-> (map :name tree-select-dropdown-parts-desc) set)))

(def tree-select-parts-desc
  (when include-args-desc?
    [{:type :legacy   :level 0 :class "rc-tree-select"          :impl "[tree-select]"}
     {:name :wrapper  :level 1 :class "rc-tree-select-wrapper"  :impl "[v-box]"}
     {:name :choice   :level 2 :class "rc-tree-select-choice"   :impl "[h-box]"}
     {:name :group    :level 2 :class "rc-tree-select-group"    :impl "[h-box]"}
     {:name :offset   :level 3 :class "rc-tree-select-offset"   :impl "[box]"}
     {:name :expander :level 3 :class "rc-tree-select-expander" :impl "[box]"}
     {:name :checkbox :level 3 :class "rc-tree-select-checkbox" :impl "[checkbox]"}]))

(def tree-select-parts
  (when include-args-desc?
    (-> (map :name tree-select-parts-desc) set)))

(def tree-select-args-desc
  (when include-args-desc?
    [{:name        :choices
      :required    true
      :type        "vector of maps | r/atom"
      :validate-fn validate/vector-of-maps?
      :description [:span "Each map represents a choice. Values corresponding to id, & label are extracted by the functions "
                    [:code ":id-fn"] " & " [:code ":label-fn"] ". See below."]}
     {:name        :model
      :required    true
      :type        "a set of ids | r/atom"
      :description [:span "The set of the ids for currently selected choices. If nil or empty, see "
                    [:code ":placeholder"] "."]}
     {:name        :expanded-groups
      :default     "(r/atom nil)"
      :type        "a set of vectors of ids | r/atom"
      :description "The set of currently expanded group paths."}
     {:name        :on-group-expand
      :default     "(partial reset! expanded-groups)"
      :type        "set of vectors of ids -> nil"
      :description "This function is called whenever the set of expanded groups changes. This usually happens when the user clicks one of the triangular expander icons."}
     {:name        :initial-expanded-groups
      :required    false
      :type        "keyword | set of paths"
      :description [:span "How to expand groups when the component first mounts."]}
     {:name        :on-change
      :required    true
      :type        "[set of choice ids, set of group vectors]  -> nil"
      :validate-fn fn?
      :description [:span "This function is called whenever the selection changes. It is also responsible for updating the value of "
                    [:code ":model"] " as needed."]}
     {:name        :choice
      :type        "part"
      :validate-fn part?
      :description [:span "alpha"]}
     {:name        :min-width
      :required    false
      :type        "string"
      :validate-fn string?
      :description "Minimum width of the outer wrapper."}
     {:name        :max-width
      :required    false
      :type        "string"
      :validate-fn string?
      :description "Maximum width of the outer wrapper."}
     {:name        :min-height
      :required    false
      :type        "string"
      :validate-fn string?
      :description "Minimum height of the outer wrapper."}
     {:name        :max-height
      :required    false
      :type        "string"
      :validate-fn string?
      :description "Maximum height of the outer wrapper."}
     {:name        :disabled?
      :required    false
      :default     false
      :type        "boolean"
      :description "When true, no user selection is allowed"}
     {:name        :required?
      :default     false
      :type        "boolean"
      :description [:span "When true, requires at least 1 choice to be selected. "
                    "If clicking a choice would result in 0 choices being selected (i.e. a model value of #{}), "
                    "then " [:code ":on-change"] " will not be called."]}
     {:name        :groups-first?
      :required    false
      :default     false
      :type        "boolean"
      :description "When true, puts groups at the top of the list. Ungrouped items will appear last."}
     {:name        :choice-disabled-fn
      :required    false
      :default     nil
      :type        "choice map -> boolean"
      :validate-fn ifn?
      :description [:span "Predicate on the set of maps given by "
                    [:code "choices"] ". Disables the subset of choices for which "
                    [:code "choice-disabled?"] " returns " [:code "true"] "."]}
     {:name :id-fn
      :required false
      :default :id
      :type "map -> anything"
      :validate-fn ifn?
      :description [:span "a function taking one argument (a map) and returns the unique identifier for that map. Called for each element in " [:code ":choices"]]}
     {:name        :label-fn
      :required    false
      :default     ":label"
      :type        "map -> hiccup"
      :validate-fn ifn?
      :description [:span "A function which can turn a choice into a displayable label. Will be called for each element in "
                    [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name        :group-label-fn
      :required    false
      :default     "(comp name last)"
      :type        "vector -> hiccup"
      :validate-fn ifn?
      :description [:span "A function which can turn a group vector into a displayable label. Will be called for each element in "
                    [:code ":groups"] ". Given one argument, a group vector, it returns a string or hiccup."]}
     {:name        :empty-means-full?
      :required    false
      :default     false
      :type        "boolean"
      :validate-fn boolean?
      :description [:span "By default, an empty model (i.e. #{}) means that no checkboxes are checked. When "
                    [:code ":empty-means-full?"]
                    " is true, all checkboxes appear checked when the model is empty."]}
     {:name :class
      :required false
      :type "string"
      :validate-fn string?
      :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style
      :required false
      :type "CSS style map"
      :validate-fn css-style?
      :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr
      :required false
      :type "HTML attr map"
      :validate-fn html-attr?
      :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br]
                    "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts
      :required false
      :type "map"
      :validate-fn (parts? tree-select-parts)
      :description "See Parts section below."}
     {:name :src
      :required false
      :type "map"
      :validate-fn map?
      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys"
                    [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}]))

(def tree-select-dropdown-args-desc
  (when include-args-desc?
    (into
     tree-select-args-desc
     [{:name        :placeholder
       :required    false
       :type        "string"
       :validate-fn string?
       :description "(Dropdown version only). Background text shown when there's no selection."}
      {:name        :field-label-fn
       :required    false
       :type        "map -> string or hiccup"
       :validate-fn ifn?
       :description (str "(Dropdown version only). Accepts a map, including keys :items, :group-label-fn and :label-fn. "
                         "Can return a string or hiccup, which will be rendered inside the dropdown anchor box.")}
      {:name        :show-reset-button?
       :default     :false
       :type        "boolean | r/atom"
       :description "When true, shows a small reset icon within the indicator part. By default, the icon looks like an X."}
      {:name        :on-reset
       :default     "(partial reset! model #{})"
       :type        "function"
       :validate-fn ifn?
       :description "This function is called when the user clicks the reset button."}
      {:name        :field-label-fn
       :required    false
       :type        "map -> string or hiccup"
       :validate-fn ifn?
       :description (str "(Dropdown version only). Accepts a map, including keys :items, :group-label-fn and :label-fn. "
                         "Can return a string or hiccup, which will be rendered inside the dropdown anchor box.")}
      {:name        :alt-text-fn
       :required    false
       :type        "map -> string"
       :validate-fn ifn?
       :description (str "(Dropdown version only). Accepts a map, including keys :items, :group-label-fn and :label-fn. "
                         "Returns a string that will display in the native browser tooltip that appears on mouse hover.")}])))

(defn backdrop
  [{:keys [opacity on-click parts]}]
  [:div
   (merge
    (into {:class    (str "noselect rc-backdrop " (get-in parts [:backdrop :class]))
           :style    (into {:position         "fixed"
                            :left             "0px"
                            :top              "0px"
                            :width            "100%"
                            :height           "100%"
                            :background-color "black"
                            :opacity          (or opacity 0.0)}
                           (get-in parts [:backdrop :style]))
           :on-click (when on-click (handler-fn (on-click)))}
          (get-in parts [:backdrop :attr])))])

(defn offset [& {:keys [parts level]}]
  [box
   :src (at)
   :style (into {:visibility "hidden"} (get-in parts [:offset :style]))
   :class (str "rc-tree-select-offset " (get-in parts [:offset :class]))
   :attr (get-in parts [:offset :attr])
   :child (apply str (repeat level "⯈"))])

(defn solo-button [{:keys [solo!]}]
  [:a {:href "#" :on-click solo!} "only"])

(defn choice [{:keys [parts checked? toggle! label disabled? attr]}]
  [h-box
   :justify :between
   :children
   [[checkbox
     :src (at)
     :style (get-in parts [:checkbox :style])
     :class (str "rc-tree-select-choice " (get-in parts [:checkbox :class]))
     :attr  (into attr (get-in parts [:checkbox :attr]))
     :model checked?
     :on-change toggle!
     :label [h-box :children [label]]
     :disabled? disabled?]]])

(defn choice-wrapper [_]
  (let [hover? (r/atom nil)]
    (fn [{:keys [choice level showing?] :as props}]
      (when showing?
        [h-box
         :attr {:on-mouse-enter #(reset! hover? true)
                :on-mouse-leave #(reset! hover? nil)}
         :width "100%"
         :children
         (into
          (vec (repeat level [gap :size "10px"]))
          [[u/part choice props re-com.tree-select/choice]
           [gap :size "1"]
           (when @hover? [solo-button props])])]))))

(defn group-item [& {:keys [checked? hide-show! showing? open? parts] :as props}]
  (when showing?
    [h-box
     :src (at)
     :style (get-in parts [:group :style])
     :class (str "rc-tree-select-group " (get-in parts [:group :class]))
     :attr  (get-in parts [:group :attr])
     :children
     [" "
      [choice (into props {:attr {:ref #(when %
                                          (set! (.-indeterminate %)
                                                (= :some checked?)))}})]]]))

(defn group-wrapper [_]
  (let [hover? (r/atom nil)]
    (fn [{:keys [level hide-show! parts open? showing?] :as props}]
      (when showing?
        [h-box
         :attr {:on-mouse-enter #(reset! hover? true)
                :on-mouse-leave #(reset! hover? nil)}
         :width "100%"
         :children
         (into
          (vec (repeat (dec level) [gap :size "10px"]))
          [[box
            :src (at)
            :align :center
            :justify :center
            :attr (into {:on-click hide-show!} (get-in parts [:expander :attr]))
            :style (into {:padding-top 4 :cursor "pointer" :height "100%"} (get-in parts [:expander :style]))
            :class (str "rc-tree-select-expander " (get-in parts [:expander :class]))
            :child
            [u/triangle {:direction (if open? :down :right)
                         :width     10 :height 10}]]
           [u/part re-com.tree-select/group-item props re-com.tree-select/group-item]
           [gap :size "1"]
           (when @hover? [solo-button props])])]))))

(def group? (comp #{:group} :type))

(defn ancestor-paths [path]
  (some->> path ->v (iterate butlast) (take-while identity) (map vec)))

(defn infer-groups [items]
  (into #{} (comp
             (keep :group)
             (map ->v)
             (mapcat ancestor-paths)
             (map #(do {:type :group :group %}))
             (distinct))
        items))

(def infer-groups* (memoize infer-groups))

(defn toggle [s k]
  (if (contains? s k)
    (disj s k)
    ((fnil conj #{}) s k)))

(defn descendant? [group-v item]
  (= group-v (vec (take (count group-v) (->v (:group item))))))

(defn filter-descendants [group-v choices]
  (filter (partial descendant? group-v) choices))

(def filter-descendants* (memoize filter-descendants))

(defn sort-items [items & {:keys [groups-first?]}]
  (let [groupless?          (comp nil? :group)
        lexicographic-group (comp #(apply str (->v %)) :group)
        leaf?               (complement group?)]
    (->> items (sort-by (apply juxt (cond->> [lexicographic-group
                                              leaf?]
                                      groups-first?
                                      (into [groupless?])))))))

(def group-label (comp str/capitalize name last :group))

(defn current-choices [model choices & {:keys [id-fn] :or {id-fn :id}}]
  (into #{} (filter (comp model id-fn) choices)))

(defn current-groups [current-choices]
  (infer-groups* current-choices))

(defn full-groups [model choices & {:keys [id-fn] :as opts :or {id-fn :id}}]
  (let [current-choices           (current-choices model choices opts)
        current-groups            (current-groups current-choices)
        full? (fn [{:keys [group]}]
                (let [group-v        (->v group)
                      descendant-ids (map id-fn (filter-descendants* group-v choices))]
                  (every? model descendant-ids)))]
    (into #{} (filter full? current-groups))))

(defn tree-select
  [& {:keys [model choices expanded-groups initial-expanded-groups on-group-expand id-fn]
      :or   {id-fn           :id
             expanded-groups (r/atom nil)
             on-group-expand (partial reset! expanded-groups)}}]
  (let [expanded-groups (r/atom nil)]
    (when-some [initial-expanded-groups (deref-or-value initial-expanded-groups)]
      (on-group-expand (case initial-expanded-groups
                         :all    (set (map :group (infer-groups choices)))
                         :none   #{}
                         :chosen (into #{}
                                       (comp (filter #(contains? (deref-or-value model) (id-fn %)))
                                             (keep :group)
                                             (mapcat ancestor-paths))
                                       choices)
                         initial-expanded-groups)))
    (fn tree-select-render
      [& {:keys [model choices group-label-fn disabled? groups-first?
                 min-width max-width min-height max-height
                 on-change choice-disabled-fn label-fn
                 choice on-group-expand
                 empty-means-full? required?
                 parts class style attr]
          :as   args}]
      (or
       (validate-args-macro tree-select-args-desc args)
       (let [choices         (deref-or-value choices)
             disabled?       (deref-or-value disabled?)
             model           (deref-or-value model)
             label-fn        (or label-fn :label)
             on-group-expand (or on-group-expand (partial reset! expanded-groups))
             expanded-groups (deref-or-value expanded-groups)
             group-label-fn  (or group-label-fn group-label)
             full?           (or (when empty-means-full? (empty? model))
                                 (every? model (map id-fn choices)))
             items           (sort-items (into choices (infer-groups* choices))
                                         :groups-first? groups-first?)
             item            (fn [item-props]
                               (let [{:keys [group] :as item-props} (update item-props :group ->v)]
                                 (if (group? item-props)
                                   (let [descendants    (filter-descendants* group choices)
                                         descendant-ids (map id-fn descendants)
                                         checked?       (cond
                                                          full?                         :all
                                                          (every? model descendant-ids) :all
                                                          (some   model descendant-ids) :some)
                                         toggle-group     #(->> (cond->> descendants choice-disabled-fn (remove choice-disabled-fn))
                                                                (map id-fn)
                                                                ((if (= :all checked?) set/difference set/union) %)
                                                                set)
                                         new-groups     (into #{} (map :group) (full-groups (toggle-group model)
                                                                                            choices
                                                                                            {:id-fn id-fn}))
                                         group-props    {:group      item-props
                                                         :label      (group-label-fn item-props)
                                                         :parts      parts
                                                         :hide-show! (handler-fn (on-group-expand (toggle expanded-groups group)))
                                                         :toggle!    (handler-fn
                                                                      (let [new-model (toggle-group model)]
                                                                        (when (or (not required?) (seq new-model))
                                                                          (on-change new-model new-groups))))
                                                         :solo!      (handler-fn (let [new-model  (set descendant-ids)
                                                                                       new-groups (into #{} (map :group) (full-groups new-model choices {:id-fn id-fn}))]
                                                                                   (on-change new-model new-groups)))
                                                         :open?      (contains? expanded-groups group)
                                                         :checked?   checked?
                                                         :model      model
                                                         :disabled?  (or disabled? (when choice-disabled-fn (every? choice-disabled-fn descendants)))
                                                         :showing?   (every? (set expanded-groups) (rest (ancestor-paths group)))
                                                         :level      (count group)}]
                                     [group-wrapper group-props])
                                   (let [level        (inc (count group))
                                         choice-props {:item      item-props
                                                       :choice    choice
                                                       :model     model
                                                       :label     (label-fn item-props)
                                                       :parts     parts
                                                       :showing?  (if-not group
                                                                    true
                                                                    (every? (set expanded-groups) (ancestor-paths group)))
                                                       :disabled? (or disabled? (when choice-disabled-fn (choice-disabled-fn item-props)))
                                                       :solo!     (handler-fn (let [new-model  #{(id-fn item-props)}
                                                                                    new-groups (into #{} (map :group) (full-groups new-model choices {:id-fn id-fn}))]
                                                                                (on-change new-model new-groups)))
                                                       :toggle!   (handler-fn (let [new-model  (toggle model (id-fn item-props))
                                                                                    new-groups (into #{} (map :group) (full-groups new-model choices {:id-fn id-fn}))]
                                                                                (when (or (not required?) (seq new-model))
                                                                                  (on-change new-model new-groups))))
                                                       :checked?  (or full? (get model (id-fn item-props)))
                                                       :level     level}]
                                     [choice-wrapper choice-props]))))]
         [v-box
          :src (at)
          :min-width min-width
          :max-width max-width
          :min-height min-height
          :max-height max-height
          :class (str "rc-tree-select-wrapper " class (get-in parts [:wrapper :class]))
          :style (merge {:overflow-y "auto"} style (get-in parts [:wrapper :style]))
          :attr (merge attr (get-in parts [:wrapper :attr]))
          :children (mapv item items)])))))

(defn field-label [{:keys [items group-label-fn label-fn]}]
  (when (seq items)
    (let [item-label-fn #((if (group? %) group-label-fn label-fn) %)]
      (str/join ", " (map item-label-fn items)))))

(defn labelable-items [model choices & {:keys [id-fn] :or {id-fn :id}}]
  (let [current-choices           (into #{} (filter (comp model id-fn) choices))
        current-groups            (infer-groups* current-choices)
        full?                     (fn [{:keys [group]}]
                                    (let [group-v        (->v group)
                                          descendant-ids (map id-fn (filter-descendants* group-v choices))]
                                      (every? model descendant-ids)))
        full-groups               (into #{} (filter full? current-groups))
        highest-groups            (loop [[group & remainder] (sort-by (comp count :group) full-groups)
                                         acc                 []]
                                    (if-not group
                                      acc
                                      (let [group-v (->v (:group group))]
                                        (recur (remove (partial descendant? group-v) remainder)
                                               (conj acc group)))))
        highest-group-descendants (into #{} (mapcat #(filter-descendants* (:group %) current-choices) highest-groups))]
    (->> highest-groups
         (into current-choices)
         (remove highest-group-descendants)
         sort-items)))

(defn tree-select-dropdown [_]
  (let [showing? (r/atom false)
        expanded-groups (r/atom nil)]
    (fn tree-select-dropdown-render
      [& {:keys [choices  disabled? required?
                 width min-width max-width min-height max-height on-change
                 label-fn alt-text-fn group-label-fn model placeholder id-fn field-label-fn
                 groups-first? initial-expanded-groups
                 show-reset-button? on-reset
                 label body-header body-footer choice
                 empty-means-full?
                 parts theme main-theme theme-vars base-theme]
          :or   {placeholder "Select an item..."
                 label-fn    :label
                 id-fn       :id}
          :as   args}]
      (let [state           {:enable (if-not disabled? :enabled :disabled)}
            themed          (fn [part props] (theme/apply props
                                               {:state       state
                                                :part        part
                                                :transition! #()}
                                               {:variables theme-vars
                                                :base      base-theme
                                                :main      main-theme
                                                :user      [theme (theme/parts parts) (theme/args args)]}))
            label-fn        (or label-fn :label)
            alt-text-fn     (or alt-text-fn #(->> % :items (map (or label-fn :label)) (str/join ", ")))
            group-label-fn  (or group-label-fn (comp name last :group))
            field-label-fn  (or field-label-fn field-label)
            labelable-items (labelable-items (deref-or-value model) choices {:id-fn id-fn})
            anchor-label    (field-label-fn {:items          labelable-items
                                             :label-fn       label-fn
                                             :group-label-fn group-label-fn})
            on-reset        (or on-reset (handler-fn (on-change (pr-str #{}) (pr-str @expanded-groups))))]
        [dd/dropdown
         {:label       (if label
                         [u/part label {}]
                         (when anchor-label
                           [:span {:title (alt-text-fn {:items          labelable-items
                                                        :label-fn       label-fn
                                                        :group-label-fn group-label-fn})
                                   :style {:white-space   "nowrap"
                                           :overflow      "hidden"
                                           :text-overflow "ellipsis"}}
                            anchor-label]))
          :placeholder placeholder
          :indicator   (fn [props]
                         [h-box
                          (themed ::dropdown-indicator
                            {:children
                             [[box {:child (str (count (deref-or-value model)))}]
                              [dd/indicator (themed ::dropdown-indicator-triangle props)]
                              (when (u/deref-or-value show-reset-button?)
                                [u/x-button
                                 {:on-click (when on-reset
                                              (handler-fn
                                               (.stopPropagation event)
                                               (on-reset (deref-or-value model)
                                                         (deref-or-value expanded-groups))))}])]})])
          :width       width
          :body-header body-header
          :body-footer body-footer
          :body        [tree-select
                        (themed ::dropdown-body
                          {:choices                 choices
                           :choice                  choice
                           :required?               required?
                           :group-label-fn          group-label-fn
                           :expanded-groups         expanded-groups
                           :disabled?               disabled?
                           :min-width               min-width
                           :max-width               max-width
                           :min-height              min-height
                           :max-height              max-height
                           :on-change               on-change
                           :groups-first?           groups-first?
                           :initial-expanded-groups initial-expanded-groups
                           :empty-means-full?       empty-means-full?
                           :id-fn                   id-fn
                           :label-fn                label-fn
                           :model                   model})]
          :model       showing?
          :theme       theme
          :parts       (merge parts
                              {:body-wrapper {:style {:width     (or width "221px")
                                                      :height    "212px"
                                                      :min-width min-width}}})}]))))
