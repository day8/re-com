(ns re-com.tabs
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [deref-or-value]]
            [re-com.box      :refer [flex-child-style]]
            [re-com.validate :refer [css-style? html-attr? vector-of-maps?] :refer-macros [validate-args-macro]]))


;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-tabs
;;--------------------------------------------------------------------------------------------------

(def tabs-args-desc
  [{:name :model     :required true                  :type "unique-id | atom"                                      :description "the unique identifier of the currently selected tab"}
   {:name :tabs      :required true                  :type "vector of tabs | atom"    :validate-fn vector-of-maps? :description "one element in the vector for each tab. Typically, each element is a map with :id and :label keys"}
   {:name :on-change :required true                  :type "unique-id -> nil"         :validate-fn fn?             :description "called when user alters the selection. Passed the unique identifier of the selection"}
   {:name :id-fn     :required false :default :id    :type "tab -> anything"          :validate-fn ifn?            :description [:span "given an element of " [:code ":tabs"] ", returns its unique identifier (aka id)"]}
   {:name :label-fn  :required false :default :label :type "tab -> string | hiccup"   :validate-fn ifn?            :description [:span "given an element of " [:code ":tabs"] ", returns its displayable label"]}
   {:name :class     :required false                 :type "string"                   :validate-fn string?         :description "CSS class names, space separated (applies to the outer container)"}
   {:name :style     :required false                 :type "CSS style map"            :validate-fn css-style?      :description "CSS styles to add or override (aplies to each individual tab rather than the container)"}
   {:name :attr      :required false                 :type "HTML attr map"            :validate-fn html-attr?      :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}])

(defn horizontal-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
    [:ul
     (merge {:class (str "rc-tabs nav nav-tabs noselect " class)
             :style (flex-child-style "none")}
            attr)
     (for [t tabs]
       (let [id        (id-fn  t)
             label     (label-fn  t)
             selected? (= id current)]                   ;; must use current instead of @model to avoid reagent warnings
         [:li
          {:class (if selected? "active")
           :key   (str id)}
          [:a
           {:style    (merge {:cursor "pointer"}
                             style)
            :on-click (when on-change (handler-fn (on-change id)))}
           label]]))]))


;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-bar-tabs
;;--------------------------------------------------------------------------------------------------

(defn- bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn vertical? class style attr]}]
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
    [:div
     (merge {:class (str "rc-tabs noselect btn-group" (if vertical? "-vertical") " " class)
             :style (flex-child-style "none")}
            attr)
     (for [t tabs]
       (let [id        (id-fn  t)
             label     (label-fn  t)
             selected? (= id current)]                    ;; must use current instead of @model to avoid reagent warnings
         [:button
          {:type     "button"
           :key      (str id)
           :class    (str "btn btn-default "  (if selected? "active"))
           :style    style
           :on-click (when on-change (handler-fn (on-change id)))}
          label]))]))


(defn horizontal-bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (bar-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? false
    :class     class
    :style     style
    :attr      attr))

(defn vertical-bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn class style attr]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (bar-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? true
    :class     class
    :style     style
    :attr      attr))


;;--------------------------------------------------------------------------------------------------
;; Component: pill-tabs
;;--------------------------------------------------------------------------------------------------

(defn- pill-tabs    ;; tabs-like in action
  [& {:keys [model tabs on-change id-fn label-fn vertical? class style attr]}]
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
    [:ul
     (merge {:class (str "rc-tabs noselect nav nav-pills" (when vertical? " nav-stacked") " " class)
             :style (flex-child-style "none")
             :role  "tabslist"}
            attr)
     (for [t tabs]
       (let [id        (id-fn  t)
             label     (label-fn  t)
             selected? (= id current)]                   ;; must use 'current' instead of @model to avoid reagent warnings
         [:li
          {:class    (if selected? "active" "")
           :key      (str id)}
          [:a
           {:style     (merge {:cursor "pointer"}
                              style)
            :on-click  (when on-change (handler-fn (on-change id)))}
           label]]))]))


(defn horizontal-pill-tabs
  [& {:keys [model tabs on-change id-fn class style attr label-fn]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (pill-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? false
    :class     class
    :style     style
    :attr      attr))


(defn vertical-pill-tabs
  [& {:keys [model tabs on-change id-fn class style attr label-fn]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (pill-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? true
    :class     class
    :style     style
    :attr      attr))
