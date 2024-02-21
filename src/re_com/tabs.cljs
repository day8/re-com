(ns re-com.tabs
  (:require-macros
   [re-com.core     :refer [handler-fn at]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :refer [->attr]]
   [re-com.util     :refer [deref-or-value add-map-to-hiccup-call merge-css flatten-attr]]
   [re-com.box      :refer [flex-child-style]]
   [re-com.validate :refer [css-style? html-attr? parts? vector-of-maps?
                            position? position-options-list]]
   [re-com.popover  :refer [popover-tooltip]]
   [reagent.core    :as    reagent]))

;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-tabs
;;--------------------------------------------------------------------------------------------------

(def horizontal-tabs-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-tabs"       :impl "[:ul]"
      :notes [:span "Only " [:code ":style"] " is supported in " [:code ":parts :wrapper"] ". Otherwise, use top level " [:code ":class"] " and " [:code ":attr"] " arguments."]}
     {:name :tab     :level 1 :class "rc-tab"        :impl "[:li]"}
     {:name :anchor  :level 2 :class "rc-tab-anchor" :impl "[:a]"
      :notes [:span "Only " [:code ":class"] " and " [:code ":attr"] " are supported in " [:code ":parts :anchor"] ". Otherwise, use top level " [:code ":style"] " argument."]}]))

(def horizontal-tabs-parts
  (when include-args-desc?
    (-> (map :name horizontal-tabs-parts-desc) set)))

(def horizontal-tabs-css-spec
  {:wrapper {:class ["nav" "nav-tabs" "noselect" "rc-tabs"]
             :style (flex-child-style "none")
             :use-toplevel true}
   :tab {:class (fn [{:keys [selected? disabled?]}]
                  ["rc-tab" (when selected? "active") (when disabled? "disabled")])}
   :anchor {:class ["rc-tab-anchor"]
            :style {:cursor "pointer"}}})

(def horizontal-tabs-args-desc
  (when include-args-desc?
    [{:name :model            :required true                  :type "unique-id | r/atom"                                                  :description "the unique identifier of the currently selected tab"}
     {:name :disabled?        :required false                 :type "boolean | r/atom"                                                    :description "disables all tabs."}
     {:name :tabs             :required true                  :type "vector of tabs | r/atom" :validate-fn vector-of-maps?                :description "one element in the vector for each tab. Typically, each element is a map with :id and :label keys"}
     {:name :on-change        :required true                  :type "unique-id -> nil"        :validate-fn fn?                            :description "called when user alters the selection. Passed the unique identifier of the selection"}
     {:name :id-fn            :required false :default :id    :type "tab -> anything"         :validate-fn ifn?                           :description [:span "given an element of " [:code ":tabs"] ", returns its unique identifier (aka id)"]}
     {:name :label-fn         :required false :default :label :type "tab -> string | hiccup"  :validate-fn ifn?                           :description [:span "given an element of " [:code ":tabs"] ", returns its displayable label"]}
     {:name :class            :required false                 :type "string"                  :validate-fn string?                        :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style            :required false                 :type "CSS style map"           :validate-fn css-style?                     :description [:span "CSS styles to add or override (aplies to " [:span.bold "each individual tab"] " rather than the container)"]}
     {:name :attr             :required false                 :type "HTML attr map"           :validate-fn html-attr?                     :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts            :required false                 :type "map"                     :validate-fn (parts? horizontal-tabs-parts) :description "See Parts section below."}
     {:name :src              :required false                 :type "map"                     :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as         :required false                 :type "map"                     :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn horizontal-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts disabled?]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
   (validate-args-macro horizontal-tabs-args-desc args)
   (let [current  (deref-or-value model)
         tabs     (deref-or-value tabs)
         disabled? (deref-or-value disabled?)
         _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")
         cmerger (merge-css horizontal-tabs-css-spec args)]
     [:ul
      (merge (flatten-attr
              (cmerger :wrapper))
             (->attr args))
      (for [t tabs]
        (let [id        (id-fn  t)
              label     (label-fn  t)
              disabled? (or disabled? (:disabled? t))
              selected? (= id current)]                   ;; must use current instead of @model to avoid reagent warnings
          [:li
           (flatten-attr
            (cmerger :tab {:selected? selected?
                           :disabled? disabled?
                           :attr {:key (str id)}}))
           [:a
            (flatten-attr
             (cmerger :anchor {:selected? selected?
                               :disabled? disabled?
                               :attr
                               {:on-click (when (and on-change (not selected?) (not disabled?))
                                            (handler-fn (on-change id)))}}))
            label]]))])))

;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-bar-tabs
;;--------------------------------------------------------------------------------------------------

(def bar-tabs-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-tabs"         :impl "[:div]"
      :notes [:span "Only " [:code ":style"] " is supported in " [:code ":parts :wrapper"] ". Otherwise, use top level " [:code ":class"] " and " [:code ":attr"] " arguments."]}
     {:name :tooltip :level 1 :class "rc-tabs-tooltip" :impl "[popover-tooltip]"}
     {:name :button  :level 2 :class "rc-tabs-btn"     :impl "[:button]"
      :notes [:span "Only " [:code ":class"] " and " [:code ":attr"] " are supported in " [:code ":parts :anchor"] ". Otherwise, use top level " [:code ":style"] " argument."]}]))

(def bar-tabs-parts
  (when include-args-desc?
    (-> (map :name horizontal-tabs-parts-desc) set)))

(def bar-tabs-css-spec
  {:wrapper {:class (fn [{:keys [vertical?]}]
                      ["noselect" (if vertical? "btn-group-vertical" "btn-group") "rc-tabs"])
             :style (flex-child-style "none")}
   :tooltip {:class ["rc-tabs-tooltip"]}
   :button {:class (fn [{:keys [selected? disabled?]}]
                     ["btn" "btn-default" (when selected? "active") (when disabled? "disabled") "rc-tabs-btn"])}})

(def bar-tabs-args-desc
  (when include-args-desc?
    (->
     (remove #(= :parts (:name %)) horizontal-tabs-args-desc)
     (vec)
     (conj
      {:name :tooltip-fn       :required false :default :tooltip      :type "tab -> string | hiccup" :validate-fn ifn?                    :description [:span "[horizontal-bar-tabs only] given an element of " [:code ":tabs"] ", returns its tooltip"]}
      {:name :tooltip-position :required false :default :below-center :type "keyword"                :validate-fn position?               :description [:span "[horizontal-bar-tabs only] relative to this anchor. One of " position-options-list]}
      {:name :validate?        :required false :default true          :type "boolean"                                                     :description [:span "Validate " [:code ":model"] " against " [:code ":tabs"]]}
      {:name :parts            :required false                        :type "map"                    :validate-fn (parts? bar-tabs-parts) :description "See Parts section below."}))))

(defn- bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn tooltip-fn tooltip-position vertical? class style attr parts validate? disabled?] :as args}]
  (let [showing (reagent/atom nil)]
    (fn [& {:keys [model tabs disabled?]}]
      (let [current  (deref-or-value model)
            tabs     (deref-or-value tabs)
            disabled? (deref-or-value disabled?)
            _        (assert (or (not validate?) (not-empty (filter #(= current (id-fn %)) tabs))) "model not found in tabs vector")
            cmerger (merge-css bar-tabs-css-spec args)]
        (into [:div
               (merge
                (flatten-attr
                 (cmerger :wrapper {:vertical? vertical?}))
                (->attr args)
                attr)]
              (for [t tabs]
                (let [id        (id-fn t)
                      label     (label-fn t)
                      tooltip   (when tooltip-fn (tooltip-fn t))
                      selected? (= id current)
                      disabled? (or disabled? (:disabled? t))
                      the-button [:button
                                  (merge
                                   (cmerger :button {:selected? selected?
                                                     :vertical? vertical?
                                                     :disabled? disabled?})
                                   {:type     "button"
                                    :key      (str id)
                                    :on-click (when (and on-change (not selected?) (not disabled?))
                                                (handler-fn (on-change id)))}
                                   (when tooltip
                                     {:on-mouse-over (handler-fn (reset! showing id))
                                      :on-mouse-out  (handler-fn (swap! showing #(when-not (= id %) %)))}))
                                  label]]
                  (if tooltip
                    (add-map-to-hiccup-call
                     (cmerger :tooltip)
                     [popover-tooltip
                      :src      (at)
                      :label    tooltip
                      :position (or tooltip-position :below-center)
                      :showing? (reagent/track #(= id @showing))
                      :anchor   the-button])
                    the-button))))))))

(defn horizontal-bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn tooltip-fn tooltip-position class style attr parts src debug-as validate? disabled?]
      :or   {id-fn :id label-fn :label tooltip-fn :tooltip}
      :as   args}]
  (or
   (validate-args-macro bar-tabs-args-desc args)
   (bar-tabs
    :model            model
    :tabs             tabs
    :disabled?        disabled?
    :on-change        on-change
    :id-fn            id-fn
    :label-fn         label-fn
    :tooltip-fn       tooltip-fn
    :tooltip-position tooltip-position
    :vertical?        false
    :class            class
    :style            style
    :attr             attr
    :parts            parts
    :src              src
    :debug-as         debug-as
    :validate?        validate?)))

(defn vertical-bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts src debug-as validate? disabled?]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
   (validate-args-macro bar-tabs-args-desc args)
   (bar-tabs
    :model     model
    :tabs      tabs
    :disabled? disabled?
    :on-change on-change
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? true
    :class     class
    :style     style
    :attr      attr
    :parts     parts
    :src       src
    :debug-as  debug-as
    :validate? validate?)))

;;--------------------------------------------------------------------------------------------------
;; Component: pill-tabs
;;--------------------------------------------------------------------------------------------------

(def pill-tabs-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-tabs"         :impl "[:ul]"
      :notes [:span "Only " [:code ":style"] " is supported in " [:code ":parts :wrapper"] ". Otherwise, use top level " [:code ":class"] " and " [:code ":attr"] " arguments."]}
     {:name :tab     :level 1 :class "rc-tabs-pill"    :impl "[:li]"}
     {:name :anchor  :level 2 :class "rc-tabs-anchor"  :impl "[:a]"
      :notes [:span "Only " [:code ":class"] " and " [:code ":attr"] " are supported in " [:code ":parts :anchor"] ". Otherwise, use top level " [:code ":style"] " argument."]}]))

(def pill-tabs-parts
  (when include-args-desc?
    (-> (map :name horizontal-tabs-parts-desc) set)))

(def pill-tabs-css-spec
  {:wrapper {:class (fn [{:keys [vertical?]}]
                      ["rc-tabs" "noselect" "nav" "nav-pills" (when vertical? "nav-stacked")])
             :style (flex-child-style "none")}
   :tab {:class (fn [{:keys [selected? disabled?]}]
                  ["rc-tabs-pill" (when selected? "active") (when disabled? "disabled")])}
   :anchor {:class ["rc-tabs-anchor"]
            :style {:cursor "pointer"}}})

(def pill-tabs-args-desc
  (when include-args-desc?
    (->
     (remove #(= :parts (:name %)) horizontal-tabs-args-desc)
     (vec)
     (conj
      {:name :parts            :required false                        :type "map"                      :validate-fn (parts? pill-tabs-parts) :description "See Parts section below."}))))

(defn- pill-tabs    ;; tabs-like in action
  [& {:keys [model tabs on-change id-fn label-fn vertical? class style attr parts src disabled?] :as args}]
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        disabled? (deref-or-value disabled?)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")
        cmerger (merge-css pill-tabs-css-spec args)]
    [:ul
     (merge
      (flatten-attr (cmerger :wrapper {:vertical? vertical?}))
      {:role  "tabslist"}
      (->attr args)
      attr)
     (for [t tabs]
       (let [id        (id-fn  t)
             label     (label-fn  t)
             disabled? (or disabled? (:disabled? t))
             selected? (= id current)]                   ;; must use 'current' instead of @model to avoid reagent warnings
         [:li
          (flatten-attr
           (cmerger :tab {:selected? selected? :vertical? vertical? :disabled? disabled?
                          :attr {:key (str id)}}))
          [:a
           (flatten-attr
            (cmerger :anchor
                     {:selected? selected?
                      :attr {:on-click  (when (and on-change (not selected?) (not disabled?))
                                          (handler-fn (on-change id)))}}))
           label]]))]))

(defn horizontal-pill-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts src debug-as disabled?]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
   (validate-args-macro pill-tabs-args-desc args)
   (pill-tabs
    :model     model
    :tabs      tabs
    :disabled? disabled?
    :on-change on-change
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? false
    :class     class
    :style     style
    :attr      attr
    :parts     parts
    :src       src
    :debug-as  debug-as)))

(defn vertical-pill-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts src debug-as disabled?]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
   (validate-args-macro pill-tabs-args-desc args)
   (pill-tabs
    :model     model
    :tabs      tabs
    :disabled? disabled?
    :on-change on-change
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? true
    :class     class
    :style     style
    :attr      attr
    :parts     parts
    :src       src
    :debug-as  debug-as)))
