(ns re-com.horizontal-tabs
  (:require-macros
   [re-com.validate :as v])
  (:require
   [re-demo.args :as args]
   [re-com.config :as conf]
   [re-com.util :as u]
   [re-com.part :as p]
   [re-com.debug :as debug]
   [re-com.theme :as theme]
   [re-com.validate :refer [vector-of-maps?]]
   [re-com.horizontal-tabs.theme]))

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
    [{:name :model     :required true                  :type "unique-id | r/atom"                                   :description "the unique identifier of the currently selected tab"}
     {:name :disabled? :required false                 :type "boolean | r/atom"                                     :description "disables all tabs."}
     {:name :tabs      :required true                  :type "vector of tabs | r/atom" :validate-fn vector-of-maps? :description "one element in the vector for each tab. Typically, each element is a map with :id and :label keys"}
     {:name :on-change :required true                  :type "unique-id -> nil"        :validate-fn fn?             :description "called when user alters the selection. Passed the unique identifier of the selection"}
     {:name :id-fn     :required false :default :id    :type "tab -> anything"         :validate-fn ifn?            :description [:span "given an element of " [:code ":tabs"] ", returns its unique identifier (aka id)"]}
     {:name :label-fn  :required false :default :label :type "tab -> string | hiccup"  :validate-fn ifn?            :description [:span "given an element of " [:code ":tabs"] ", returns its displayable label"]}
     args/class
     args/style
     args/attr
     (args/parts part-names)
     args/src
     args/debug-as]))

(defn horizontal-tabs [& {:keys [theme pre-theme]}]
  (let [theme (theme/comp theme pre-theme)]
    (fn [& {:keys [model tabs on-change id-fn label-fn disabled? tab-type]
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
            :post-props (-> (select-keys props [:class :style :attr])
                            (update :attr (merge (debug/->attr props))))
            :props      {:on-change on-change
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
                                         {:theme theme
                                          :props (merge tab-props
                                                        {:tag      :a
                                                         :children [label]})})]})}))}}))))))
