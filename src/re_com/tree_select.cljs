(ns re-com.tree-select
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [clojure.set :as set]
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
     {:name :open-to            :required false                         :type "keyword"                                                          :description [:span "How to expand groups when the component first mounts."]}
     {:name :on-change          :required true                          :type "set of ids -> nil"       :validate-fn fn?                         :description [:span "This function is called whenever the selection changes. Called with one argument, the set of selected ids. See " [:code ":model"] "."]}
     {:name :on-groups-change   :required false :default "#(reset! groups %)" :type "set of ids -> nil"       :validate-fn fn?           :description [:span "This function is called whenever a group expands or collapses. Called with one argument, the set of expanded groups. See " [:code ":groups"] "."]}
     {:name :disabled?          :required false :default false          :type "boolean"                                                          :description "if true, no user selection is allowed"}
     {:name :label-fn           :required false :default ":label"       :type "map -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :group-label-fn     :required false :default "(comp name last)"       :type "vector -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a group vector into a displayable label. Will be called for each element in " [:code ":groups"] ". Given one argument, a group vector, it returns a string or hiccup."]}
     {:name :group-renderer     :required false :default "re-com.tree-select/group"  :type [:code "{:keys [group label hide-show! toggle! open? checked? model disabled? showing? level]} -> hiccup"] :validate-fn ifn?          :description "You can provide a renderer function to override the inbuilt renderer for group headers."}
     {:name :choice-renderer    :required false :default "re-com.tree-select/choice" :type [:code "{:keys [choice model label showing? disabled? toggle! checked? level]} -> hiccup"] :validate-fn ifn?          :description "You can provide a renderer function to override the inbuilt renderer for group headers."}]))

(def tree-select-dropdown-parts-desc nil)

(def tree-select-dropdown-args-desc
  (when include-args-desc?
    [{:name :placeholder        :required false                         :type "string"                  :validate-fn string?                     :description "Background text shown when there's no selection."}]))

(defn choice [{:keys [label checked? toggle! level showing? disabled?]}]
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

(defn group [{:keys [label checked? toggle! hide-show! level showing? open? disabled?]}]
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
             (map #(if (vector? %) % [%]))
             (mapcat ancestor-paths)
             (map #(do {:type :group :group %}))
             (distinct))
        items))

(defn toggle [s k]
  (if (contains? s k)
    (disj s k)
    ((fnil conj #{}) s k)))

(defn tree-select
  [& {:keys [model choices choice-renderer group-renderer groups on-groups-change open-to id-fn]
      :or   {groups          (r/atom nil)
             id-fn           :id
             on-groups-change #(reset! groups %)
             choice-renderer choice
             group-renderer  group}}]
  (when-let [open-to (deref-or-value open-to)]
    (on-groups-change (case open-to
                        :all (set (map :group (infer-groups choices)))
                        :none #{}
                        :chosen
                        (into #{}
                              (comp
                               (filter #(contains? (deref-or-value model) (id-fn %)))
                               (keep :group)
                               (mapcat ancestor-paths))
                              choices))))
  (fn [& {:keys [choices group-label-fn disabled? min-width max-width on-change on-groups-change label-fn]
          :or {on-groups-change #(reset! groups %)}}]
    (let [choices (deref-or-value choices)
          disabled? (deref-or-value disabled?)
          label-fn (or label-fn :label)
          group-label-fn (or group-label-fn (comp name last))
          groups (deref-or-value groups)
          items   (->> choices infer-groups (into choices) (sort-by (juxt (comp #(apply str %) :group)
                                                                          (complement group?))))
          item    (fn [{:keys [group id] :as item-props}]
                    (let [group-v (as-v group)]
                      (if (group? item-props)
                        [group-renderer
                         (let [descendant? #(= group-v (vec (take (count group-v) (as-v (:group %)))))
                               descendants (map :id (filter descendant? choices))
                               checked?    (cond
                                             (every? (deref-or-value model) descendants) :all
                                             (some   (deref-or-value model) descendants) :some)]
                           {:group      group-v
                            :label      (group-label-fn group-v)
                            :hide-show! #(on-groups-change (toggle groups group-v))
                            :toggle!    #(swap! model
                                                (if (= :all checked?) set/difference (fnil into #{}))
                                                descendants)
                            :open?      (contains? groups group-v)
                            :checked?   checked?
                            :model      model
                            :disabled?  disabled?
                            :showing?   (every? (set groups) (rest (ancestor-paths group-v)))
                            :level      (count group-v)})]
                        [choice-renderer
                         {:choice item-props
                          :model    model
                          :label    (label-fn item-props)
                          :showing? (if-not group-v
                                      true
                                      (every? (set groups) (ancestor-paths group-v)))
                          :disabled? disabled?
                          :toggle!  (handler-fn (on-change (toggle @model id)))
                          :checked? (get @model id)
                          :level    (inc (count group-v))}])))]
      [v-box
       :min-width min-width
       :max-width max-width
       :children (mapv item items)])))
