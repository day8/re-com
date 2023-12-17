(ns re-com.tree-select
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [clojure.set :as set]
   [reagent.core          :as r]
   [re-com.config         :refer [include-args-desc?]]
   [re-com.util           :refer [deref-or-value]]
   [re-com.box            :refer [h-box v-box box gap]]
   [re-com.validate       :as validate :refer [parts?]]))

(def tree-select-parts-desc nil)

(def tree-select-args-desc
  (when include-args-desc?
    [{:name :choices            :required true                          :type "vector of maps | r/atom" :validate-fn validate/vector-of-maps?    :description [:span "Each map represents a choice. Values corresponding to id, & label are extracted by the functions " [:code ":id-fn"] " & " [:code ":label-fn"] ". See below."]}
     {:name :model              :required true                          :type "a set of ids | r/atom"                                            :description [:span "The set of the ids for currently selected choices. If nil or empty, see " [:code ":placeholder"] "."]}]))

(def tree-select-dropdown-parts-desc nil)

(def tree-select-dropdown-args-desc
  (when include-args-desc?
    [{:name :placeholder        :required false                         :type "string"                  :validate-fn string?                     :description "Background text shown when there's no selection."}]))

(defn choice [{:keys [label checked? toggle! level showing? disabled?]}]
  (when showing?
    [h-box
     :children
     [[gap :size (str level "rem")]
      [box
       :attr {:on-click (when-not disabled? toggle!)}
       :style {:cursor (if disabled? "default" "pointer")}
       :child
       (if checked? "☑" "☐")]
      " "
      label]]))

(defn group [{:keys [label checked? toggle! hide-show! level showing? open? disabled?]}]
  (when showing?
    [h-box :class "chosen-container chosen-container-single chosen-container-active"
     :style {:margin-left (str (dec level) "rem")}
     :children
     [[box
       :attr {:on-click (when-not disabled? hide-show!)}
       :style {:cursor "pointer"}
       :child
       (if open? "⯆" "⯈")]
      " "
      #_[checkbox :src (at)
         :model checked?
         :on-change toggle!
         :label label
         :disabled? disabled?
         :style {}]
      [box
       :attr {:on-click (when-not disabled? toggle!)}
       :style {:cursor "default"}
       :child
       (case checked? :all "☑" :some "◽" "☐")]
      " "
      [:span
       {:style {:overflow "none"
                :word-wrap "break-word"}}
       label]]]))

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
  [& {:as props
      :keys [model choices choice-renderer group-renderer groups open-to id-fn]
      :or   {open-to         :chosen
             groups          (r/atom nil)
             id-fn           :id
             choice-renderer choice
             group-renderer  group}}]
  (let [open-to (deref-or-value open-to)]
    (println open-to)
    (when-not (= :none open-to)
      (reset! groups (case open-to
                       :all (infer-groups choices)
                       (:chosen nil)
                       (into #{}
                             (comp
                              (filter #(contains? (deref-or-value model) (id-fn %)))
                              (keep :group)
                              (mapcat ancestor-paths))
                             choices)))))
  (fn [& {:keys [choices group-label-fn disabled? min-width max-width on-change]
          :or {group-label-fn {}}}]
    (let [choices (deref-or-value choices)
          disabled? (deref-or-value disabled?)
          items   (->> choices infer-groups (into choices) (sort-by (juxt (comp #(apply str %) :group)
                                                                          (complement group?))))
          item    (fn [{:keys [group id] :as item}]
                    (let [group-v (as-v group)]
                      (if (group? item)
                        [group-renderer
                         (let [descendant? #(= group-v (vec (take (count group-v) (as-v (:group %)))))
                               descendants (map :id (filter descendant? choices))
                               checked?    (cond
                                             (every? (deref-or-value model) descendants) :all
                                             (some   (deref-or-value model) descendants) :some)]
                           (merge item
                                  {:label      (or (group-label-fn (peek group-v)) (peek group-v))
                                   :hide-show! #(swap! groups toggle group-v)
                                   :toggle!    #(swap! model
                                                       (if (= :all checked?) set/difference (fnil into #{}))
                                                       descendants)
                                   :open?      (contains? @groups group-v)
                                   :checked?   checked?
                                   :model      model
                                   :disabled?  disabled?
                                   :showing?   (every? (set @groups) (rest (ancestor-paths group-v)))
                                   :level      (count group-v)}))]
                        [choice-renderer
                         (merge item
                                {:model    model
                                 :showing? (if-not group-v
                                             true
                                             (every? (set @groups) (ancestor-paths group-v)))
                                 :disabled? disabled?
                                 :toggle!  (handler-fn (on-change (toggle @model id)))
                                 :checked? (get @model id)
                                 :level    (inc (count group-v))})])))]
      [:<>
       [v-box
        :min-width min-width
        :max-width max-width
        :children
        (mapv item items)]])))
