(ns re-com.tabs
   (:require [clojure.set  :refer [superset?]]
             [re-com.util  :refer [deref-or-value]]
             [reagent.core :as    reagent]))


(defn find-tab
  "utility function for finding a tab definition with a given id
  'tab-defs' in a vector of definitions
  'id' is the id of one of them
  returns nil if no definition found otherwise returns the first match found"
  [id tab-defs]
  (first (filter #(= id (:id %)) tab-defs)))


;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-tabs
;;--------------------------------------------------------------------------------------------------

(def tabs-args
  #{:model      ;; Sets/holds/returns the currently selected tab - model can be atom
    :tabs       ;; The tabs object defined as a vector of maps (can be literal/variable/atom):
                ;;   [{:id ::tab1  :label "Tab1"}
                ;;    {:id ::tab2  :label "Tab2"}
                ;;    {:id ::tab3  :label "Tab3"}]
    })


(defn horizontal-tabs
  [& {:keys [model tabs]
      :as   args}]
  {:pre [(superset? tabs-args (keys args))]}
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (:id %)) tabs)) "model not found in tabs vector")]
    [:ul
     {:class "rc-tabs nav nav-tabs"
      :style {:flex                "none"
              :-webkit-user-select "none"}}
     (for [t tabs]
       (let [id        (:id t)
             label     (:label t)
             selected? (= id current)]                   ;; must use current instead of @model to avoid reagent warnings
         [:li
          {:class  (if selected? "active")
           :key    (str id)}
          [:a
           {:style     {:cursor "pointer"}
            :on-click  #(reset! model id)}
           label]]))]))


;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-bar-tabs
;;--------------------------------------------------------------------------------------------------

(defn horizontal-bar-tabs
  [& {:keys [model tabs]
      :as   args}]
  {:pre [(superset? tabs-args (keys args))]}
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (:id %)) tabs)) "model not found in tabs vector")]
    [:div
     {:class "rc-tabs btn-group"
      :style {:flex                "none"
              :-webkit-user-select "none"}}
     (for [t tabs]
       (let [id        (:id t)
             label     (:label t)
             selected? (= id current)]                    ;; must use current instead of @model to avoid reagent warnings
         [:button.btn.btn-default
          {:type     "button"
           :key      (str id)
           :style    (if selected? {:background-color "#aaa"  :color "white"} {})
           :on-click #(reset! model id)}
          label]))]))


;;--------------------------------------------------------------------------------------------------
;; Component: pill-tabs
;;--------------------------------------------------------------------------------------------------

(defn- pill-tabs    ;; tabs-like in action
  [& {:keys [model tabs vertical?]}]
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (:id %)) tabs)) "model not found in tabs vector")]
    [:ul
     {:class (str "rc-tabs nav nav-pills" (when vertical? " nav-stacked"))
      :style {:flex                "none"
              :-webkit-user-select "none"}
      :role  "tabslist"}
     (for [t tabs]
       (let [id        (:id t)
             label     (:label t)
             selected? (= id current)]                   ;; must use 'current' instead of @model to avoid reagent warnings
         [:li
          {:class    (if selected? "active" "")
           :key      (str id)}
          [:a
           {:style     {:cursor "pointer"}
            :on-click  #(reset! model id)}
           label]]))]))

(defn horizontal-pill-tabs    ;; tabs-like in action
  [& {:keys [model tabs] :as args}]
  {:pre [(superset? tabs-args (keys args))]}
  (pill-tabs :model model :tabs tabs :vertical? false))


(defn vertical-pill-tabs
  [& {:keys [model tabs] :as args}]
  {:pre [(superset? tabs-args (keys args))]}
  (pill-tabs :model model :tabs tabs :vertical? true))
