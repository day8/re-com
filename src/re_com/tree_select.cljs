(ns re-com.tree-select
  (:require-macros
   [re-com.core     :refer [handler-fn at]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   [clojure.string :as str]
   [reagent.core          :as r]
   [re-com.multi-select   :refer [multi-select]]
   [re-com.config         :refer [include-args-desc?]]
   [re-com.util           :refer [deref-or-value]]
   [re-com.validate       :as validate :refer [parts? string-or-hiccup?]]
   [re-com.box            :refer [h-box v-box gap box]]
   [re-com.text           :as text]
   [re-com.selection-list :as    selection-list]))

(defn choice [{:keys [label checked? toggle! level showing?]}]
  (when showing?
    [:div {:style {:margin-left (str (inc level) "rem")}}
     [:span {:on-click toggle!
             :style {:cursor "pointer"}}
      (if checked? "☑" "☐")]
     " "
     [:i (when showing? label)]]))

(defn group [{:keys [label checked? toggle! hide-show! level showing? open?]}]
  (when showing?
    [:div {:style {:margin-left (str (dec level) "rem")}}
     [:span {:on-click hide-show!
             :style {:cursor "pointer"}}
      (if open? "⯆" "⯈")]
     " "
     [:span {:on-click toggle!
             :style {:cursor "pointer"}}
      (case checked? :all "☑" :some "◽" "☐")]
     " "
     [:strong label]]))

(def group? (comp #{:group} :type))

(defn ancestor-paths [path]
  (->> path (iterate butlast) (take-while identity) (map vec)))

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
  [& {:keys [model choices choice-renderer group-renderer group-label]
      :or   {model           (r/atom nil)
             choice-renderer choice
             group-renderer  group}}]
  (fn [& {:keys [choices group-label]}]
    (let [choices (deref-or-value choices)
          items   (->> choices
                       infer-groups
                       (sort-by (juxt (comp #(apply str %) :group)
                                      (complement group?))))
          item    (fn [{:keys [group id] :as m}]
                    (if (group? m)
                      [group-renderer
                       (let [ancestors   (->> group ancestor-paths set)
                             descendant? #(= group (vec (take (count group) (:group %))))
                             descendants (->> choices
                                              (filter descendant?)
                                              (map :id))
                             checked?    (cond
                                           (every? (set (:choices @model)) descendants) :all
                                           (some   (set (:choices @model)) descendants) :some)]
                         (merge m {:label      (group-label (peek group))
                                   :hide-show! #(swap! model update :groups toggle group)
                                   :toggle!    #(swap! model update :choices (if (= :all checked?) clojure.set/difference (fnil into #{})) descendants)
                                   :open?      (contains? (:groups @model) group)
                                   :checked?   checked?
                                   :model      model
                                   :showing?   (every? (set (:groups @model)) (rest (ancestor-paths group)))
                                   :level      (if (vector? group) (count group) 1)}))]
                      [choice-renderer
                       (merge m {:model    model
                                 :showing? (every? (set (:groups @model)) (ancestor-paths group))
                                 :toggle!  #(swap! model update :choices toggle id)
                                 :checked? (some-> @model :choices (get id))
                                 :level    (if (vector? group) (count group) 1)})]))]
      [:<>
       (pr-str @model)
       [v-box
        :children
        (map item items)]])))

