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

(def tree-select-parts-desc nil)

(def tree-select-args-desc
  (when include-args-desc?
    [{:name :choices            :required true                          :type "vector of maps | r/atom" :validate-fn validate/vector-of-maps?    :description [:span "Each map represents a choice. Values corresponding to id, & label are extracted by the functions " [:code ":id-fn"] " & " [:code ":label-fn"] ". See below."]}
     {:name :model              :required true                          :type "a set of ids | r/atom"                                            :description [:span "The set of the ids for currently selected choices. If nil or empty, see " [:code ":placeholder"] "."]}
     {:name :groups             :required false :default "(reagent/atom nil)" :type "a set of paths | r/atom"                            :description [:span "The set of currently expanded group paths."]}
     {:name :initial-expanded-groups :required false                         :type "keyword | set of paths"                                               :description [:span "How to expand groups when the component first mounts."]}
     {:name :on-change          :required true                          :type "set of ids -> nil"       :validate-fn fn?                         :description [:span "This function is called whenever the selection changes. Called with one argument, the set of selected ids. See " [:code ":model"] "."]}
     {:name :on-groups-change   :required false :default "#(reset! groups %)" :type "set of ids -> nil"       :validate-fn fn?           :description [:span "This function is called whenever a group expands or collapses. Called with one argument, the set of expanded groups. See " [:code ":groups"] "."]}
     {:name :disabled?          :required false :default false          :type "boolean"                                                          :description "if true, no user selection is allowed"}
     {:name :label-fn           :required false :default ":label"       :type "map -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :group-label-fn     :required false :default "(comp name last)"       :type "vector -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a group vector into a displayable label. Will be called for each element in " [:code ":groups"] ". Given one argument, a group vector, it returns a string or hiccup."]}]))

(def tree-select-dropdown-parts-desc nil)

(def tree-select-dropdown-args-desc
  (when include-args-desc?
    [{:name :placeholder        :required false                         :type "string"                  :validate-fn string?                     :description "Background text shown when there's no selection."}]))

(defn backdrop
  [& {:keys [opacity on-click class]}]
  [:div
   (merge
    {:class    (str "noselect rc-backdrop " class)
     :style    {:position         "fixed"
                :left             "0px"
                :top              "0px"
                :width            "100%"
                :height           "100%"
                :background-color "black"
                :opacity          (or opacity 0.0)}
     :on-click (when on-click (handler-fn (on-click)))})])

(defn choice-item [{:keys [label checked? toggle! level showing? disabled?]}]
  (when showing?
    [h-box
     :children
     [[box
       :style {:visibility "hidden"}
       :child (apply str (repeat level "⯈"))]
      [checkbox
       :model checked?
       :on-change toggle!
       :label label
       :disabled? disabled?]]]))

(defn group-item [{:keys [label checked? toggle! hide-show! level showing? open? disabled?]}]
  (when showing?
    [h-box :class "chosen-container chosen-container-single chosen-container-active"
     :children
     [[box
       :style {:visibility "hidden"}
       :child (apply str (repeat (dec level) "⯈"))]
      [box
       :attr {:on-click hide-show!}
       :style {:cursor "pointer"}
       :child
       (if open? "⯆" "⯈")]
      " "
      [checkbox
       :attr {:ref #(when % (set! (.-indeterminate %) (= :some checked?)))}
       :model checked?
       :on-change toggle!
       :label label
       :disabled? disabled?]]]))

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
      [& {:keys [choices group-label-fn disabled? min-width max-width min-height max-height on-change label-fn]}]
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
                                  (let [descendant-ids (map :id (filter-descendants* group choices))
                                        checked?       (cond
                                                         (every? model descendant-ids) :all
                                                         (some   model descendant-ids) :some)
                                        new-model (set ((if (= :all checked?) set/difference set/union)
                                                        model descendant-ids))
                                        new-groups (into #{} (map :group) (full-groups new-model choices))]
                                    {:group      item-props
                                     :label      (group-label-fn item-props)
                                     :hide-show! #(swap! expanded-groups toggle group)
                                     :toggle!    (handler-fn (on-change new-model new-groups))
                                     :open?      (contains? @expanded-groups group)
                                     :checked?   checked?
                                     :model      model
                                     :disabled?  disabled?
                                     :showing?   (every? (set @expanded-groups) (rest (ancestor-paths group)))
                                     :level      (count group)})]
                                 [choice-item
                                  {:choice    item-props
                                   :model     model
                                   :label     (label-fn item-props)
                                   :showing?  (if-not group
                                                true
                                                (every? (set @expanded-groups) (ancestor-paths group)))
                                   :disabled? disabled?
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
         :style {:overflow-y "scroll"}
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

(defn overflowing? [el] (> (.-scrollWidth el) (.-offsetWidth el)))

(defn tree-select-dropdown [{:keys [expanded-groups]
                             :or   {expanded-groups (r/atom nil)}}]
  (let [showing?       (r/atom false)
        !anchor-span   (r/atom nil)
        !anchor-label  (r/atom "")
        !visible-items (r/atom [])]
    (r/create-class
     {:component-did-update
      (fn calculate-visible-items
        [_ [_ & {:keys [field-label-fn model choices group-label-fn label-fn]}]]
        (when-let [anchor-span @!anchor-span]
          (let [model          (deref-or-value model)
                items          (labelable-items (deref-or-value model) choices)
                label-fn       (or label-fn :label)
                group-label-fn (or group-label-fn group-label)
                field-label-fn (or field-label-fn field-label)
                abbreviate?    #(> (count %) 3)
                abbrev-fn      #(apply str (take 3 %))]
            (loop [items items]
              (set! (.-textContent anchor-span) (field-label-fn {:items items :label-fn label-fn :group-label-fn group-label-fn}))
              (if (and (seq items) (overflowing? anchor-span))
                (recur (butlast items))
                (do (reset! !visible-items (vec items))
                    (set! (.-textContent anchor-span) @!anchor-label)))))))
      :reagent-render
      (fn tree-select-dropdown-render
        [& {:keys [choices group-label-fn disabled? min-width max-width min-height max-height on-change on-groups-change
                   label-fn height parts style model expanded-groups placeholder id-fn alt-text-fn field-label-fn]
            :or   {on-groups-change #(reset! expanded-groups %)
                   expanded-groups  (r/atom #{})
                   placeholder      "Select an item..."
                   id-fn            :id
                   label-fn         :label
                   alt-text-fn      #(->> % (map (or label-fn :label)) (str/join ", "))}}]
        (let [label-fn        (or label-fn :label)
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
                      :style (merge {:position         "absolute"
                                     :background-color "white"
                                     :border-radius    "4px"
                                     :border           "1px solid #ccc"
                                     :padding          "5px 10px 5px 5px"
                                     :box-shadow       "0 5px 10px rgba(0, 0, 0, .2)"})
                      :children [[tree-select
                                  :choices choices
                                  :group-label-fn group-label-fn
                                  :disabled? disabled?
                                  :min-width min-width
                                  :max-width max-width
                                  :min-height min-height
                                  :max-height max-height
                                  :on-change on-change
                                  :groups expanded-groups
                                  :on-groups-change on-groups-change
                                  :label-fn label-fn
                                  :model model]]]
              anchor (fn []
                       (let [model     (deref-or-value model)
                             disabled? (deref-or-value disabled?)]
                         [h-box
                          :src       (at)
                          :height    height
                          :padding   "0px 6px"
                          :class     (str "rc-multi-select-dropdown " (get-in parts [:main :class]))
                          :style     (merge {:min-width        min-width
                                             :max-width        max-width
                                             :background-color (if disabled? "#EEE" "white")
                                             :border           "1px solid lightgrey"
                                             :border-radius    "2px"
                                             :overflow         "hidden"
                                             :cursor           (if disabled? "default" "pointer")}
                                            style
                                            (get-in parts [:main :style]))
                          :attr      (merge {}
                                            (when (not disabled?) {:on-click #(swap! showing? not)})
                                            (get-in parts [:main :attr]))
                          :children  [(if (empty? model)
                                        placeholder
                                        (let [selections (filter (comp (set model) id-fn) choices)
                                              _          (reset! !anchor-label anchor-label)]
                                          [:span {:ref   #(reset! !anchor-span %)
                                                  :title (alt-text-fn selections)
                                                  :style {:max-width     max-width
                                                          :white-space   "nowrap"
                                                          :overflow      "hidden"
                                                          :text-overflow "ellipsis"}}
                                           anchor-label]))
                                      (let [hidden-ct (- (count labelable-items) (count @!visible-items))]
                                        [box
                                         :style {:visibility (when (< hidden-ct 2) "hidden")}
                                         :child (str "+" (dec hidden-ct))])
                                      [gap :src (at)
                                       :size "1"]
                                      (when-not disabled?
                                        [box
                                         :style {:margin-left "15px"}
                                         :child
                                         (if @showing? "▲" "▼")])]]))]
          [:div {:style {:display  "inline-block"
                         :maxWidth max-width
                         :minWidth min-width}}
           [anchor]
           (when @showing?
             [:div {:class "fade in"
                    :style {:position "relative"}}
              [backdrop :on-click #(reset! showing? false)]
              body])]))})))
