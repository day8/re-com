(ns re-com.tabs
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [deref-or-value]]
            [re-com.box      :refer [flex-child-style]]
            [re-com.validate :refer [css-style? vector-of-maps?] :refer-macros [validate-args-macro]]))


;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-tabs
;;--------------------------------------------------------------------------------------------------

(def tabs-args-desc
  [{:name :tabs      :required true                  :type "vector | atom"            :validate-fn vector-of-maps? :description "one element in the vector for each tab. Typically, each element is a map with :id and :label keys"}
   {:name :model     :required true                  :type "unique-id | atom"                                      :description "the unique identifier of the currently selected tab"}
   {:name :on-change :required true                  :type "unique-id -> nil"         :validate-fn fn?             :description "called when user alters the selection. Passed the unique identifier of the selection"}
   {:name :id-fn     :required false :default :id    :type "map -> anything"          :validate-fn fn?             :description [:span "given an element of " [:code ":tabs"] ", returns the unique identifier for this tab"]}
   {:name :label-fn  :required false :default :label :type "map -> string | hiccup"   :validate-fn fn?             :description [:span "given an element of " [:code ":tabs"] ", returns what should be displayed in the tab"]}
   {:name :style     :required false                 :type "CSS style map"            :validate-fn css-style?      :description "CSS styles to add or override (for each individual tab rather than the container)"}])

(defn horizontal-tabs
  [& {:keys [model tabs on-change id-fn label-fn style]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
    [:ul
     {:class "rc-tabs nav nav-tabs noselect"
      :style (flex-child-style "none")}
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
  [& {:keys [model tabs on-change id-fn label-fn style vertical?]}]
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
    [:div
     {:class (str "rc-tabs noselect btn-group" (if vertical? "-vertical"))
      :style (flex-child-style "none")}
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
  [& {:keys [model tabs on-change id-fn label-fn style]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (bar-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :style     style
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? false))

(defn vertical-bar-tabs
  [& {:keys [model tabs on-change id-fn label-fn style]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (bar-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :style     style
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? true))


;;--------------------------------------------------------------------------------------------------
;; Component: pill-tabs
;;--------------------------------------------------------------------------------------------------

(defn- pill-tabs    ;; tabs-like in action
  [& {:keys [model tabs on-change id-fn label-fn style vertical?]}]
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (id-fn %)) tabs)) "model not found in tabs vector")]
    [:ul
     {:class (str "rc-tabs noselect nav nav-pills" (when vertical? " nav-stacked"))
      :style (flex-child-style "none")
      :role  "tabslist"}
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
  [& {:keys [model tabs on-change id-fn style label-fn]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (pill-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :style     style
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? false))


(defn vertical-pill-tabs
  [& {:keys [model tabs on-change id-fn style label-fn]
      :or   {id-fn :id label-fn :label}
      :as   args}]
  {:pre [(validate-args-macro tabs-args-desc args "tabs")]}
  (pill-tabs
    :model     model
    :tabs      tabs
    :on-change on-change
    :style     style
    :id-fn     id-fn
    :label-fn  label-fn
    :vertical? true))
