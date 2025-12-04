(ns re-com.bar-tabs
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
   [re-com.bar-tabs.theme]))

(def part-structure
  [::wrapper {:tag :ul}
   [::tooltip {:tag       :li
               :multiple? true}
    [::tooltip-label]
    [::button {:top-level-arg? true
               :multiple?      true}]]])

(def parts-desc
  (when conf/include-args-desc? (p/describe part-structure)))

(def part-names
  (when conf/include-args-desc? (set (map :name parts-desc))))

(def args-desc
  (when conf/include-args-desc?
    (vec
     (concat
      ;; Core bar-tabs specific args
      [{:name :model            :required true                             :type "atom | any"             :validate-fn #(or (satisfies? IAtom %) (some? %))  :description "selection atom or value"}
       {:name :tabs             :required true                             :type "vector | atom"          :validate-fn #(or (vector? %) (satisfies? IAtom %)) :description "vector of tabs or atom"}
       {:name :on-change        :required true                             :type "fn"                     :validate-fn fn?                       :description "called when selection changes"}
       {:name :id-fn            :required false :default :id               :type "keyword | fn"           :validate-fn #(or (keyword? %) (fn? %)) :description "function to extract id from tab"}
       {:name :label-fn         :required false :default :label            :type "keyword | fn"           :validate-fn #(or (keyword? %) (fn? %)) :description "function to extract label from tab"}
       {:name :disabled?        :required false                             :type "boolean | atom"         :validate-fn #(or (boolean? %) (satisfies? IAtom %)) :description "disable all tabs"}
       {:name :tab-type         :required false :default :horizontal       :type "keyword"                :validate-fn keyword?                  :description "tab type"}
       {:name :vertical?        :required false :default false             :type "boolean"                :validate-fn boolean?                  :description "vertical layout"}
       {:name :tooltip-fn       :required false :default :tooltip          :type "tab -> string | hiccup" :validate-fn ifn?                      :description [:span "[horizontal-bar-tabs only] given an element of " [:code ":tabs"] ", returns its tooltip"]}
       {:name :tooltip-position :required false :default :below-center     :type "keyword"                :validate-fn v/position?               :description [:span "[horizontal-bar-tabs only] relative to this anchor. One of " v/position-options-list]}
       {:name :validate?        :required false :default true              :type "boolean"                :validate-fn boolean?                  :description [:span "Validate " [:code ":model"] " against " [:code ":tabs"]]}
       args/class
       args/attr
       {:name :style            :required false                             :type "map"                    :validate-fn map?                      :description [:span "Applies to the " [:code ":button"] " part."]}
       (args/parts part-names)
       args/src
       args/debug-as]
      theme/args-desc
      (p/describe-args part-structure)))))

(defn tab-tooltip [{:keys [position label showing? anchor class style attr]
                    :or   {position :below-center}}]
  [po/popover-tooltip
   :src      (at)
   :position position
   :label    label
   :showing? @showing?
   :anchor   anchor
   :class    class
   :style    style
   :attr     attr])

(defn bar-tabs [& {:keys [theme pre-theme]}]
  (let [theme    (theme/comp theme pre-theme)
        showing? (r/atom nil)]
    (fn [& {:keys [model tabs on-change id-fn label-fn disabled? tab-type
                   tooltip-position tooltip-fn vertical?]
            :or   {id-fn    :id
                   label-fn :label
                   tab-type :horizontal}
            :as   props}]
      (or
       (v/validate-args-macro args-desc props)
       (let [model     (u/deref-or-value model)
             tabs      (u/deref-or-value tabs)
             disabled? (u/deref-or-value disabled?)
             _         (assert (not-empty (filter #(= model (id-fn %)) tabs))
                               "model not found in tabs vector")
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
                         :children
                         (for [t    tabs
                               :let [{:keys [disabled?]
                                      :or   {disabled? disabled?}}
                                     t
                                     id          (id-fn t)
                                     label       (label-fn t)
                                     selected?   (= id model)
                                     tab-state   {:enable     (if disabled? :disabled :enabled)
                                                  :selectable (if selected? :selected :unselected)}
                                     tab-re-com  {:state (merge (:state re-com) tab-state)}
                                     tab-props   {:id        id
                                                  :tab-type  tab-type
                                                  :label     label
                                                  :on-change on-change}
                                     tooltip-part (or
                                                   (:tooltip t)
                                                   (when tooltip-fn (tooltip-fn id))
                                                   (-> props :parts :tooltip-label))
                                     button-part (part ::button
                                                   {:key        t
                                                    :theme      theme
                                                    :props      (merge tab-props
                                                                       {:re-com   tab-re-com
                                                                        :tag      :button
                                                                        :children [label]}
                                                                       (when tooltip-part
                                                                         {:attr {:on-mouse-over
                                                                                 (handler-fn (reset! showing? id))
                                                                                 :on-mouse-out
                                                                                 (handler-fn (swap! showing? #(when-not (= id %) %)))}}))
                                                    :post-props (select-keys props [:style])})]]
                           (if-not tooltip-part
                             button-part
                             (part ::tooltip
                               {:impl  tab-tooltip
                                :key   id
                                :props {:label    (p/part tooltip-part
                                                    {:part  ::tooltip-label
                                                     :props {:id id}})
                                        :position tooltip-position
                                        :showing? (r/track #(= id @showing?))
                                        :anchor   button-part}})))}}))))))

(defn vertical-bar-tabs [& {:as props}]
  [bar-tabs (assoc props :vertical? true)])

(def horizontal-bar-tabs bar-tabs)

