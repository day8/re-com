(ns re-com.pill-tabs
  (:require-macros
   [re-com.core     :refer [at handler-fn]]
   [re-com.validate :as v])
  (:require
   [re-com.args :as args]
   [re-com.validate :as v]
   [reagent.core :as r]
   [re-com.config :as conf]
   [re-com.util :as u]
   [re-com.part :as p]
   [re-com.popover :as po]
   [re-com.debug :as debug]
   [re-com.theme :as theme]
   [re-com.horizontal-tabs :as horizontal-tabs]
   [re-com.pill-tabs.theme]))

(def part-structure
  [::wrapper {:tag :ul}
   [::tab {:tag       :li
           :multiple? true}
    [::anchor {:top-level-arg? true}]]])

(def parts-desc
  (when conf/include-args-desc? (p/describe part-structure)))

(def part-names
  (when conf/include-args-desc? (set (map :name parts-desc))))

(def args-desc
  (when conf/include-args-desc?
    (vec
     (concat
      ;; Core pill-tabs specific args
      [{:name :model        :required true                             :type "atom | any"             :validate-fn #(or (satisfies? IAtom %) (some? %))  :description "selection atom or value"}
       {:name :tabs         :required true                             :type "vector | atom"          :validate-fn #(or (vector? %) (satisfies? IAtom %)) :description "vector of tabs or atom"}
       {:name :on-change    :required true                             :type "fn"                     :validate-fn fn?                       :description "called when selection changes"}
       {:name :id-fn        :required false :default :id               :type "keyword | fn"           :validate-fn #(or (keyword? %) (fn? %)) :description "function to extract id from tab"}
       {:name :label-fn     :required false :default :label            :type "keyword | fn"           :validate-fn #(or (keyword? %) (fn? %)) :description "function to extract label from tab"}
       {:name :disabled?    :required false                             :type "boolean | atom"         :validate-fn #(or (boolean? %) (satisfies? IAtom %)) :description "disable all tabs"}
       {:name :tab-type     :required false :default :horizontal       :type "keyword"                :validate-fn keyword?                  :description "tab type"}
       {:name :vertical?    :required false :default false             :type "boolean"                :validate-fn boolean?                  :description "vertical layout"}
       args/class
       args/attr
       (args/parts part-names)
       args/src
       args/debug-as]
      theme/args-desc
      (p/describe-args part-structure)))))

(defn pill-tabs [& {:keys [theme pre-theme]}]
  (let [theme (theme/comp theme pre-theme)]
    (fn [& {:keys [model tabs on-change id-fn label-fn disabled? tab-type vertical?]
            :or   {id-fn    :id
                   label-fn :label
                   tab-type :horizontal}
            :as   props}]
      (or
       (v/validate-args-macro args-desc props)
       (let [model     (u/deref-or-value model)
             tabs      (u/deref-or-value tabs)
             disabled? (u/deref-or-value disabled?)
             _         (assert (not-empty (filter #(= model (id-fn %)) tabs)) "model not found in tabs vector")
             part      (partial p/part part-structure props)
             re-com    {:state {:disabled?  disabled?
                                :vertical?  vertical?
                                :tab-type   tab-type}}]
         (part ::wrapper
           {:theme      theme
            :post-props (-> (select-keys props [:class :attr])
                            (update :attr merge (debug/->attr props)))
            :props      {:re-com    re-com
                         :on-change on-change
                         :tag       :ul
                         :children
                         (for [t    tabs
                               :let [{:keys [disabled?]
                                      :or   {disabled? disabled?}}
                                     t
                                     id        (id-fn t)
                                     label     (label-fn t)
                                     selected? (= id model)
                                     tab-state {:enable     (if disabled? :disabled :enabled)
                                                :selectable (if selected? :selected :unselected)}
                                     tab-re-com {:state (merge (:state re-com) tab-state)}
                                     tab-props {:id        id
                                                :tab-type  tab-type
                                                :label     label
                                                :on-change on-change}]]
                           (part ::tab
                             {:key   t
                              :theme theme
                              :props (merge tab-props
                                            {:re-com   tab-re-com
                                             :tag      :li
                                             :children
                                             [(part ::anchor
                                                {:theme      theme
                                                 :post-props (select-keys props [:style])
                                                 :props      (merge tab-props
                                                                    {:re-com   tab-re-com
                                                                     :tag      :a
                                                                     :children [label]})})]})}))}}))))))

(defn vertical-pill-tabs [& {:as props}]
  [pill-tabs (assoc props :vertical? true)])

(def horizontal-pill-tabs pill-tabs)
