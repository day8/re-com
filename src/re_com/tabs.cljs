(ns re-com.tabs
   (:require [clojure.set  :refer [superset?]]
             [re-com.util  :refer [deref-or-value]]
             [reagent.core :as    reagent]))

;; Below there are three different stylings for horizontal tabs
;; In each case they take two parameters.
;; 'model' is expected to be an atom containing a keyword identifier. Eg.  ::first-tab
;; 'tabs' can be either:
;;     -  a map OR
;;     - an atom containing a map
;; Either way, the map should look like this:
;;        { ::id1  {:label "1" } ::id2  {:label "2" } ::anotherId  {:label "3" }

;; utility function for finding a tab definition with a given id
;; 'tab-defs' is a vector of definitions
;; 'id' is the id of one of them
;; returns nil if no definition found
;; otherwise returns the first match found
(defn find-tab
  [id tab-defs]
  (first (filter #(= id (:id %)) tab-defs)))


;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-bar-tabs
;;--------------------------------------------------------------------------------------------------

(def tabs-args
  #{:model      ;; Sets/holds/returns the currently selected tab - model can be atom
    :tabs       ;; The tabs object defined as a vector of maps:   TODO: document that it can be atom or not
                ;;   [{:id ::tab1  :label "Tab1" :disabled false}
                ;;    {:id ::tab2  :label "Tab2" :disabled false}
                ;;    {:id ::tab3  :label "Tab3" :disabled false}]
    :disabled   ;; A boolean indicating whether the control should be disabled. false if not specified.
    })


(defn horizontal-bar-tabs
  [& {:keys [model tabs disabled]
      :as   args}]
  {:pre [(superset? tabs-args (keys args))]}
  (let [current  (deref-or-value model)
        tabs     (deref-or-value tabs)
        _        (assert (not-empty (filter #(= current (:id %)) tabs)) "model not found in tabs vector")]
    [:div
     {:class "rc-tabs btn-group"
      :style {:flex "none"}}
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
;; Component: horizontal-pills
;;--------------------------------------------------------------------------------------------------

(defn horizontal-pills    ;; tabs-like in action
  [& {:keys [model tabs disabled]
      :as   args}]
  {:pre [(superset? tabs-args (keys args))]}
  (let [current  @model
        tabs     (deref-or-value tabs)]
    [:ul
     {:class "rc-tabs nav nav-pills"
      :style {:flex "none"}
      :role  "tabslist"}
     (for [t tabs]
       (let [id        (:id t)
             label     (:label t)
             selected? (= id current)]                   ;; must use 'current' instead of @model to avoid reagent warnings
         [:li
          {:class    (if selected? "active" "")
           :key      (str id)}
          [:a
           {:on-click  #(reset! model id)}
           label]]))]))


;;--------------------------------------------------------------------------------------------------
;; Component: horizontal-tabs
;;--------------------------------------------------------------------------------------------------

(defn horizontal-tabs
  [& {:keys [model tabs disabled]
      :as   args}]
  {:pre [(superset? tabs-args (keys args))]}
  (let [current  @model
        tabs     (deref-or-value tabs)]
    [:ul
     {:class "rc-tabs nav nav-tabs"
      :style {:flex "none"}}
     (for [t tabs]
       (let [id        (:id t)
             label     (:label t)
             selected? (= id current)]                   ;; must use current instead of @model to avoid reagent warnings
         [:li
          {:class  (if selected? "active")
           :key    (str id)}
          [:a
           {:on-click  #(reset! model id)}
           label]]))]))
