(ns re-com.tree-select
  (:require-macros
   [re-com.core :refer [handler-fn at]])
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [reagent.core          :as r]
   [re-com.config         :refer [include-args-desc?]]
   [re-com.util           :refer [deref-or-value]]
   [re-com.box            :refer [h-box v-box box gap]]
   [re-com.checkbox       :refer [checkbox]]
   [re-com.validate       :as validate :refer [parts?]]))

(def tree-select-dropdown-parts-desc
  (when include-args-desc?
    [{:type :legacy             :level 0 :class "rc-dropdown"                              :impl "[tree-select-dropdown]"}
     {:name :wrapper            :level 1 :class "rc-tree-select-dropdown-wrapper"          :impl "[:div]"}
     {:name :anchor             :level 2 :class "rc-tree-select-dropdown-anchor"           :impl "[h-box]"}
     {:name :counter            :level 3 :class "rc-tree-select-dropdown-counter"          :impl "[box]"}
     {:name :anchor-expander    :level 3 :class "rc-tree-select-dropdown-anchor-expander"  :impl "[box]"}
     {:name :backdrop           :level 2 :class "rc-tree-select-dropdown-backdrop"         :impl "[div]"}
     {:name :dropdown-wrapper   :level 2 :class "rc-tree-select-dropdown-dropdown-wrapper" :impl "[v-box]"}
     {:name :body               :level 3 :class "rc-tree-select-dropdown-body"             :impl "[tree-select]"}]))

(def tree-select-parts-desc
  (when include-args-desc?
    [{:type :legacy             :level 0 :class "rc-tree-select"                 :impl "[tree-select]"}
     {:name :wrapper            :level 1 :class "rc-tree-select-wrapper"         :impl "[v-box]"}
     {:name :choice             :level 2 :class "rc-tree-select-choice"          :impl "[h-box]"}
     {:name :group              :level 2 :class "rc-tree-select-group"           :impl "[h-box]"}
     {:name :offset             :level 3 :class "rc-tree-select-offset"          :impl "[box]"}
     {:name :expander           :level 3 :class "rc-tree-select-expander"        :impl "[box]"}
     {:name :checkbox           :level 3 :class "rc-tree-select-checkbox"        :impl "[checkbox]"}]))

(def tree-select-args-desc
  (when include-args-desc?
    [{:name :choices            :required true                          :type "vector of maps | r/atom" :validate-fn validate/vector-of-maps?    :description [:span "Each map represents a choice. Values corresponding to id, & label are extracted by the functions " [:code ":id-fn"] " & " [:code ":label-fn"] ". See below."]}
     {:name :model              :required true                          :type "a set of ids | r/atom"                                            :description [:span "The set of the ids for currently selected choices. If nil or empty, see " [:code ":placeholder"] "."]}
     {:name :groups             :required false :default "(reagent/atom nil)" :type "a set of paths | r/atom"                            :description [:span "The set of currently expanded group paths."]}
     {:name :initial-expanded-groups :required false                         :type "keyword | set of paths"                                               :description [:span "How to expand groups when the component first mounts."]}
     {:name :on-change          :required true                          :type "[set of choice ids, set of group vectors]  -> nil"       :validate-fn fn?                         :description [:span "This function is called whenever the selection changes. It is also responsible for updating the value of " [:code ":model"] " as needed."]}
     {:name :disabled?          :required false :default false          :type "boolean"                                                          :description "if true, no user selection is allowed"}
     {:name :choice-disabled?   :required false :default false          :type "choice map -> boolean"    :validate-fn ifn?   :description "Predicate on the set of maps given by " [:code "choices"] ". Disables the subset of choices for which " [:code "choice-disabled?"] " returns " [:code "true"] "."}
     {:name :label-fn           :required false :default ":label"       :type "map -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :group-label-fn     :required false :default "(comp name last)"       :type "vector -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a group vector into a displayable label. Will be called for each element in " [:code ":groups"] ". Given one argument, a group vector, it returns a string or hiccup."]}]))

(def tree-select-dropdown-args-desc
  (when include-args-desc?
    (into tree-select-args-desc
          [{:name :placeholder        :required false                         :type "string"                  :validate-fn string?                     :description "(Dropdown version only). Background text shown when there's no selection."}
           {:name :field-label-fn     :required false                         :type "map -> string or hiccup" :validate-fn ifn?                     :description "(Dropdown version only). Accepts a map, including keys :items, :group-label-fn and :label-fn. Can return a string or hiccup, which will be rendered inside the dropdown anchor box."}
           {:name :alt-text-fn     :required false                         :type "map -> string" :validate-fn ifn?                     :description "(Dropdown version only). Accepts a map, including keys :items, :group-label-fn and :label-fn. Returns a string that will display in the native browser tooltip that appears on mouse hover."}])))

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

(defn offset [{:keys [parts level]}]
  [box
   :style (into {:visibility "hidden"} (get-in parts [:offset :style]))
   :class (str "rc-tree-select-offset " (get-in parts [:offset :class]))
   :attr (get-in parts [:offset :attr])
   :child (apply str (repeat level "⯈"))])

(defn choice-checkbox [{:keys [parts checked? toggle! label disabled? attr]}]
  [checkbox
   :style (get-in parts [:checkbox :style])
   :class (str "rc-tree-select-checkbox " (get-in parts [:checkbox :class]))
   :attr (merge {}
                (when attr attr)
                (get-in parts [:checkbox :attr]))
   :model checked?
   :on-change toggle!
   :label label
   :disabled? disabled?])

(defn choice-item [{:keys [level showing? parts] :as props}]
  (when showing?
    [h-box
     :style (get-in parts [:choice :style])
     :class (str "rc-tree-select-choice " (get-in parts [:choice :class]))
     :attr  (get-in parts [:choice :attr])
     :children
     [[offset {:parts parts :level level}]
      [choice-checkbox (into props {:attr {}})]]]))

(defn group-item [{:keys [label checked? toggle! hide-show! level showing? open? disabled? parts] :as props}]
  (when showing?
    [h-box :class "chosen-container chosen-container-single chosen-container-active"
     :style (get-in parts [:group :style])
     :class (str "rc-tree-select-group " (get-in parts [:group :class]))
     :attr  (get-in parts [:group :attr])
     :children
     [[offset {:parts parts :level (dec level)}]
      [box
       :attr (into {:on-click hide-show!} (get-in parts [:expander :attr]))
       :style (into {:cursor "pointer"} (get-in parts [:expander :style]))
       :class (str "rc-tree-select-expander " (get-in parts [:expander :class]))
       :child
       (if open? "⯆" "⯈")]
      " "
      [choice-checkbox (into props {:attr {:ref #(when %
                                                   (set! (.-indeterminate %)
                                                         (= :some checked?)))}})]]]))

(def group? (comp #{:group} :type))

(defn as-v [x] (when (some? x) (if (vector? x) x [x])))

(defn ancestor-paths [path]
  (some->> path as-v (iterate butlast) (take-while identity) (map vec)))

(defn infer-groups [items]
  (into #{} (comp
             (keep :group)
             (map as-v)
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
  (= group-v (vec (take (count group-v) (as-v (:group item))))))

(defn filter-descendants [group-v choices]
  (filter (partial descendant? group-v) choices))

(def filter-descendants* (memoize filter-descendants))

(defn sort-items [items] (->> items (sort-by (juxt (comp #(apply str %) :group)
                                                   (complement group?)))))

(def group-label (comp str/capitalize name last :group))

(defn current-choices [model choices] (into #{} (filter (comp model :id) choices)))

(defn current-groups [current-choices] (infer-groups* current-choices))

(defn full-groups [model choices]
  (let [current-choices           (current-choices model choices)
        current-groups            (current-groups current-choices)
        full? (fn [{:keys [group]}]
                (let [group-v        (as-v group)
                      descendant-ids (map :id (filter-descendants* group-v choices))]
                  (every? model descendant-ids)))]
    (into #{} (filter full? current-groups))))

(defn tree-select
  [& {:keys [model choices initial-expanded-groups id-fn]
      :or   {id-fn            :id}}]
  (let [expanded-groups           (r/atom nil)]
    (when-some [initial-expanded-groups (deref-or-value initial-expanded-groups)]
      (reset! expanded-groups (case initial-expanded-groups
                                :all    (set (map :group (infer-groups choices)))
                                :none   #{}
                                :chosen (into #{}
                                              (comp (filter #(contains? (deref-or-value model) (id-fn %)))
                                                    (keep :group)
                                                    (mapcat ancestor-paths))
                                              choices)
                                initial-expanded-groups)))
    (fn tree-select-render
      [& {:keys [choices group-label-fn disabled? min-width max-width min-height max-height on-change choice-disabled-fn label-fn parts class style attr]}]
      (let [choices        (deref-or-value choices)
            disabled?      (deref-or-value disabled?)
            model          (deref-or-value model)
            label-fn       (or label-fn :label)
            group-label-fn (or group-label-fn group-label)
            items          (->> choices infer-groups* (into choices) sort-items)
            item           (fn [item-props]
                             (let [{:keys [id group] :as item-props} (update item-props :group as-v)]
                               (if (group? item-props)
                                 [group-item
                                  (let [descendants (filter-descendants* group choices)
                                        descendant-ids (map :id descendants)
                                        checked?       (cond
                                                         (every? model descendant-ids) :all
                                                         (some   model descendant-ids) :some)
                                        new-model (->> (cond->> descendants choice-disabled-fn (remove choice-disabled-fn))
                                                       (map :id)
                                                       ((if (= :all checked?) set/difference set/union) model)
                                                       set)
                                        new-groups (into #{} (map :group) (full-groups new-model choices))]
                                    {:group      item-props
                                     :label      (group-label-fn item-props)
                                     :style     (get-in parts [:group :style])
                                     :class     (str "rc-tree-select-group " (get-in parts [:group :class]))
                                     :attr      (str get-in parts [:group :attr])
                                     :hide-show! #(swap! expanded-groups toggle group)
                                     :toggle!    (handler-fn (on-change new-model new-groups))
                                     :open?      (contains? @expanded-groups group)
                                     :checked?   checked?
                                     :model      model
                                     :disabled?  (or disabled? (when choice-disabled-fn (every? choice-disabled-fn descendants)))
                                     :showing?   (every? (set @expanded-groups) (rest (ancestor-paths group)))
                                     :level      (count group)})]
                                 [choice-item
                                  {:choice    item-props
                                   :model     model
                                   :label     (label-fn item-props)
                                   :style     (get-in parts [:choice :style])
                                   :class     (str "rc-tree-select-choice " (get-in parts [:choice :class]))
                                   :attr      (str get-in parts [:choice :attr])
                                   :showing?  (if-not group
                                                true
                                                (every? (set @expanded-groups) (ancestor-paths group)))
                                   :disabled? (or disabled? (when choice-disabled-fn (choice-disabled-fn item-props)))
                                   :toggle!   (handler-fn (let [new-model (toggle model id)
                                                                new-groups (into #{} (map :group) (full-groups new-model choices))]
                                                            (on-change new-model new-groups)))
                                   :checked?  (get model id)
                                   :level     (inc (count group))}])))]
        [v-box
         :min-width min-width
         :max-width max-width
         :min-height min-height
         :max-height max-height
         :class (str "rc-tree-select-wrapper " class (get-in parts [:wrapper :class]))
         :style (merge {:overflow-y "scroll"} style (get-in parts [:wrapper :style]))
         :attr (merge attr (get-in parts [:wrapper :attr]))
         :children (mapv item items)]))))

(defn field-label [{:keys [items group-label-fn label-fn]}]
  (let [item-label-fn             #((if (group? %) group-label-fn label-fn) %)]
    (str/join ", " (map item-label-fn items))))

(defn labelable-items [model choices]
  (let [current-choices           (into #{} (filter (comp model :id) choices))
        current-groups            (infer-groups* current-choices)
        full?                     (fn [{:keys [group]}]
                                    (let [group-v        (as-v group)
                                          descendant-ids (map :id (filter-descendants* group-v choices))]
                                      (every? model descendant-ids)))
        full-groups               (into #{} (filter full? current-groups))
        highest-groups            (loop [[group & remainder] (sort-by (comp count :group) full-groups)
                                         acc                 []]
                                    (if-not group
                                      acc
                                      (let [group-v (as-v (:group group))]
                                        (recur (remove (partial descendant? group-v) remainder)
                                               (conj acc group)))))
        highest-group-descendants (into #{} (mapcat #(filter-descendants* (:group %) current-choices) highest-groups))]
    (->> highest-groups
         (into current-choices)
         (remove highest-group-descendants)
         sort-items)))

(defn tree-select-dropdown [{:keys [expanded-groups]
                             :or   {expanded-groups (r/atom nil)}}]
  (let [showing?       (r/atom false)
        !anchor-span   (r/atom nil)
        !anchor-label  (r/atom "")]
    (fn tree-select-dropdown-render
      [& {:keys [choices group-label-fn disabled? min-width max-width min-height max-height on-change on-groups-change
                 label-fn alt-text-fn height parts style model expanded-groups placeholder id-fn alt-text-fn field-label-fn]
          :or   {placeholder      "Select an item..."
                 id-fn            :id
                 label-fn         :label}}]
      (let [label-fn        (or label-fn :label)
            alt-text-fn     (or alt-text-fn #(->> % :items (map (or label-fn :label)) (str/join ", ")))
            group-label-fn  (or group-label-fn (comp name last :group))
            field-label-fn  (or field-label-fn field-label)
            labelable-items (labelable-items (deref-or-value model) choices)
            abbreviate?     #(> (count %) 3)
            abbrev-fn       #(apply str (take 3 %))
            anchor-label    (field-label-fn {:items labelable-items
                                             :abbrev-fn abbrev-fn
                                             :abbrev-threshold 4
                                             :label-fn label-fn
                                             :group-label-fn group-label-fn})
            body   [v-box
                    :height "fit-content"
                    :class (str "rc-tree-select-dropdown-dropdown-wrapper " (get-in parts [:dropdown-wrapper :class]))
                    :attr (get-in parts [:dropdown-wrapper :attr])
                    :style (merge {:position         "absolute"
                                   :background-color "white"
                                   :border-radius    "4px"
                                   :border           "1px solid #ccc"
                                   :padding          "5px 10px 5px 5px"
                                   :box-shadow       "0 5px 10px rgba(0, 0, 0, .2)"}
                                  (get-in parts [:dropdown-wrapper :style]))
                    :children [[tree-select
                                :class (str "rc-tree-select-dropdown-body " (get-in parts [:body :class]))
                                :style (get-in parts [:body :style])
                                :attr  (get-in parts [:body :attr])
                                :choices choices
                                :group-label-fn group-label-fn
                                :disabled? disabled?
                                :min-width min-width
                                :max-width max-width
                                :min-height min-height
                                :max-height max-height
                                :on-change on-change
                                :label-fn label-fn
                                :model model]]]
            anchor (fn []
                     (let [model     (deref-or-value model)
                           disabled? (deref-or-value disabled?)]
                       [h-box
                        :src       (at)
                        :height    height
                        :padding   "0px 6px"
                        :class     (str "rc-multi-select-dropdown " (get-in parts [:anchor :class]))
                        :style     (merge {:min-width        min-width
                                           :max-width        max-width
                                           :background-color (if disabled? "#EEE" "white")
                                           :border           "1px solid lightgrey"
                                           :border-radius    "2px"
                                           :overflow         "hidden"
                                           :cursor           (if disabled? "default" "pointer")}
                                          style
                                          (get-in parts [:anchor :style]))
                        :attr      (merge {}
                                          (when (not disabled?) {:on-click #(swap! showing? not)})
                                          (get-in parts [:anchor :attr]))
                        :children  [(if (empty? model)
                                      placeholder
                                      (let [selections (filter (comp (set model) id-fn) choices)
                                            _          (reset! !anchor-label anchor-label)]
                                        [:span {:ref   #(reset! !anchor-span %)
                                                :title (alt-text-fn {:items labelable-items
                                                                     :abbrev-fn abbrev-fn
                                                                     :abbrev-threshold 4
                                                                     :label-fn label-fn
                                                                     :group-label-fn group-label-fn})
                                                :style {:max-width     max-width
                                                        :white-space   "nowrap"
                                                        :overflow      "hidden"
                                                        :text-overflow "ellipsis"}}
                                         anchor-label]))
                                    [gap :src (at)
                                     :size "1"]
                                    (when-let [model (seq model)]
                                      [box
                                       :class (str "rc-tree-select-dropdown-counter " (get-in parts [:counter :class]))
                                       :style (merge {:margin-left "10px"
                                                      :margin-right "10px"
                                                      :opacity "50%"}
                                                     (get-in parts [:counter :style]))
                                       :attr  (get-in parts [:counter :attr])
                                       :child (str (count model))])
                                    (when-not disabled?
                                      [box
                                       :class (str "rc-tree-select-dropdown-anchor-expander " (get-in parts [:anchor-expander :class]))
                                       :style (get-in parts [:anchor-expander :style])
                                       :attr  (get-in parts [:anchor-expander :attr])
                                       :child
                                       (if @showing? "▲" "▼")])]]))]
        [:div (into {:class (str "rc-tree-select-dropdown-wrapper " (get-in parts [:wrapper :class]))
                     :style (into {:display  "inline-block"
                                   :maxWidth max-width
                                   :minWidth min-width}
                                  (get-in parts [:wrapper :style]))}
                    (get-in parts [:wrapper :attr]))
         [anchor]
         (when @showing?
           [:div {:class "fade in"
                  :style {:position "relative"}}
            [backdrop {:on-click #(reset! showing? false)}]
            body])]))))
