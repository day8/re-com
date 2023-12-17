(ns re-com.tree-select
  (:require
   [clojure.set :as set]
   [reagent.core          :as r]
   [re-com.util           :refer [deref-or-value]]
   [re-com.box            :refer [h-box v-box box]]))

(def tree-select-parts-desc [])

(def tree-select-args-desc [])

(defn choice [{:keys [label checked? toggle! level showing? width style disabled?]}]
  (when showing?
    [h-box
     :children
     [[box
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
  (into items (comp
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
  [& {:keys [model choice-renderer group-renderer]
      :or   {model           (r/atom nil)
             choice-renderer choice
             group-renderer  group}}]
  (fn [& {:keys [choices group-label-fn disabled? min-width max-width]
          :or {group-label-fn {}}}]
    (let [choices (deref-or-value choices)
          disabled? (deref-or-value disabled?)
          items   (->> choices
                       infer-groups
                       (sort-by (juxt (comp #(apply str %) :group)
                                      (complement group?))))
          item    (fn [{:keys [group id] :as m}]
                    (let [group (as-v group)]
                      (if (group? m)
                        [group-renderer
                         (let [descendant? #(= group (vec (take (count group) (as-v (:group %)))))
                               descendants (->> choices
                                                (filter descendant?)
                                                (map :id))
                               checked?    (cond
                                             (every? (set (:choices @model)) descendants) :all
                                             (some   (set (:choices @model)) descendants) :some)]
                           (merge m {:label      (or (group-label-fn (peek group)) (peek group))
                                     :hide-show! #(swap! model update :groups toggle group)
                                     :toggle!    #(swap! model update :choices
                                                         (if (= :all checked?) set/difference (fnil into #{}))
                                                         descendants)
                                     :open?      (contains? (:groups @model) group)
                                     :checked?   checked?
                                     :model      model
                                     :disabled?  disabled?
                                     :showing?   (every? (set (:groups @model)) (rest (ancestor-paths group)))
                                     :level      (if (vector? group) (count group) 1)}))]
                        [choice-renderer
                         (merge m {:model    model
                                   :showing? (if-not group
                                               true
                                               (every? (set (:groups @model)) (ancestor-paths group)))
                                   :disabled? disabled?
                                   :toggle!  #(swap! model update :choices toggle id)
                                   :checked? (some-> @model :choices (get id))
                                   :level    (count group)})])))]
      [:<>
       [v-box
        :min-width min-width
        :max-width max-width
        :children
        (map item items)]])))
