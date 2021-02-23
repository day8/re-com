(ns re-com.tabs
  (:require-macros
    [re-com.core     :refer [handler-fn at]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.util     :refer [deref-or-value]]
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

(def horizontal-tabs-args-desc
  (when include-args-desc?
    [{:name :model            :required true                  :type "unique-id | r/atom"                                                  :description "the unique identifier of the currently selected tab"}
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
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
    (validate-args-macro horizontal-tabs-args-desc args)
    (let [current  (deref-or-value model)
          tabs     (deref-or-value tabs)
          _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
      [:ul
       (merge {:class (str "nav nav-tabs noselect rc-tabs " class)
               :style (merge (flex-child-style "none")
                             (get-in parts [:wrapper :style]))}
              (->attr args)
              attr)
       (for [t tabs]
         (let [id        (id-fn  t)
               label     (label-fn  t)
               selected? (= id current)]                   ;; must use current instead of @model to avoid reagent warnings
           [:li
            (merge
              {:class (str (if selected? "active rc-tab ") (get-in parts [:tab :class]))
               :style (get-in parts [:tab :style])
               :key   (str id)}
              (get-in parts [:tab :attr]))
            [:a
             (merge
               {:class    (str "rc-tab-anchor " (get-in parts [:anchor :class]))
                :style    (merge {:cursor "pointer"}
                                 style)
                :on-click (when on-change (handler-fn (on-change id)))}
               (get-in parts [:anchor :attr]))
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
  [& {:keys [model tabs on-change id-fn label-fn tooltip-fn tooltip-position vertical? class style attr parts validate?] :as args}]
  (let [showing (reagent/atom nil)]
    (fn [& {:keys [model tabs]}]
      (let [current  (deref-or-value model)
            tabs     (deref-or-value tabs)
            _        (assert (or (not validate?) (not-empty (filter #(= current (id-fn %)) tabs))) "model not found in tabs vector")]
        (into [:div
               (merge
                 {:class (str "noselect btn-group" (if vertical? "-vertical") " rc-tabs " class)
                  :style (merge (flex-child-style "none")
                                (get-in parts [:wrapper :style]))}
                 (->attr args)
                 attr)]
         (for [t tabs]
           (let [id        (id-fn t)
                 label     (label-fn t)
                 tooltip   (when tooltip-fn (tooltip-fn t))
                 selected? (= id current)
                 the-button [:button
                             (merge
                               {:type     "button"
                                :key      (str id)
                                :class    (str "btn btn-default " (if selected? "active ") "rc-tabs-btn " (get-in parts [:button :class]))
                                :style    style
                                :on-click (when on-change (handler-fn (on-change id)))}
                               (when tooltip
                                 {:on-mouse-over (handler-fn (reset! showing id))
                                  :on-mouse-out  (handler-fn (swap! showing #(when-not (= id %) %)))})
                               (get-in parts [:button :attr]))
                             label]]
             (if tooltip
               [popover-tooltip
                :src      (at)
                :label    tooltip
                :position (or tooltip-position :below-center)
                :showing? (reagent/track #(= id @showing))
                :anchor   the-button
                :class    (str "rc-tabs-tooltip " (get-in parts [:tooltip :class]))
                :style    (get-in parts [:tooltip :style])
                :attr     (get-in parts [:tooltip :attr])]
               the-button))))))))


(defn horizontal-bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn tooltip-fn tooltip-position class style attr parts src debug-as validate?]
      :or   {id-fn :id label-fn :label tooltip-fn :tooltip}
      :as   args}]
  (or
    (validate-args-macro bar-tabs-args-desc args)
    (bar-tabs
      :model            model
      :tabs             tabs
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
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts src debug-as validate?]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
    (validate-args-macro bar-tabs-args-desc args)
    (bar-tabs
      :model     model
      :tabs      tabs
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

(def pill-tabs-args-desc
  (when include-args-desc?
    (->
      (remove #(= :parts (:name %)) horizontal-tabs-args-desc)
      (vec)
      (conj
        {:name :parts            :required false                        :type "map"                      :validate-fn (parts? pill-tabs-parts) :description "See Parts section below."}))))


(defn- pill-tabs    ;; tabs-like in action
  [& {:keys [model tabs on-change id-fn label-fn vertical? class style attr parts src] :as args}]
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
    [:ul
     (merge
       {:class (str "rc-tabs noselect nav nav-pills" (when vertical? " nav-stacked") " " class)
        :style (merge (flex-child-style "none")
                      (get-in parts [:wrapper :style]))
        :role  "tabslist"}
       (->attr args)
       attr)
     (for [t tabs]
       (let [id        (id-fn  t)
             label     (label-fn  t)
             selected? (= id current)]                   ;; must use 'current' instead of @model to avoid reagent warnings
         [:li
          (merge
            {:class    (str "rc-tabs-pill " (if selected? "active " "") (get-in parts [:tab :class]))
             :style    (get-in parts [:tab :style])
             :key      (str id)}
            (get-in parts [:tab :attr]))
          [:a
           (merge
             {:class     (str "rc-tabs-anchor " (get-in parts [:anchor :class]))
              :style     (merge {:cursor "pointer"}
                                style)
              :on-click  (when on-change (handler-fn (on-change id)))}
             (get-in parts [:anchor :attr]))
           label]]))]))


(defn horizontal-pill-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts src debug-as]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
    (validate-args-macro pill-tabs-args-desc args)
    (pill-tabs
      :model     model
      :tabs      tabs
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
  [& {:keys [model tabs on-change id-fn label-fn class style attr parts src debug-as]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  (or
    (validate-args-macro pill-tabs-args-desc args)
    (pill-tabs
      :model     model
      :tabs      tabs
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
