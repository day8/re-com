(ns re-com.pill-tabs
  (:require-macros
   [re-com.core     :refer [at handler-fn]]
   [re-com.validate :as v])
  (:require
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
    (->
     (remove (comp #{:parts} :name) horizontal-tabs/args-desc)
     vec
     (conj
      {:name :vertical? :type "boolean" :default false}
      {:name :parts            :required false                        :type "map"                    :validate-fn (v/parts? part-names) :description "See Parts section below."}))))

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
             part      (partial p/part part-structure props)]
         (part ::wrapper
           {:theme      theme
            :post-props (-> (select-keys props [:class :attr])
                            (update :attr merge (debug/->attr props)))
            :props      {:on-change on-change
                         :vertical? vertical?
                         :tab-type  tab-type
                         :re-com    {:state {:enable (not disabled?)}}
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
                                     tab-props {:id        id
                                                :tab-type  tab-type
                                                :label     label
                                                :re-com    {:state tab-state}
                                                :on-change on-change}]]
                           (part ::tab
                             {:key   t
                              :theme theme
                              :props
                              (merge tab-props
                                     {:tag :li
                                      :children
                                      [(part ::anchor
                                         {:theme      theme
                                          :post-props (select-keys props [:style])
                                          :props      (merge tab-props
                                                             {:tag      :a
                                                              :children [label]})})]})}))}}))))))

(defn vertical-pill-tabs [& {:as props}]
  [pill-tabs (assoc props :vertical? true)])

(def horizontal-pill-tabs pill-tabs)
