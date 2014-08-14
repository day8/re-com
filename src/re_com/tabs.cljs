(ns re-com.tabs
   (:require [reagent.core :as reagent]))


;; 'model' is expected to be an atom containing a keyword identifier. Eg.  ::first-tab
;; 'tabs' can be either a map OR an atom containing a map
;; the map should be keyed by an id, and valued by a map cotnaining at least :label
;; Eg:   { ::id1  {:label "1" } ::id2  {:label "2" } ::anotherId  {:label "3" }

;; TODO:
;;   - add preconditions: 'model' is an IDeref containing a keyword  .. and tabs is either a map or an atom containing a map


(defn horizontal-bar-tabs
  [& {:keys [model tabs]}]

  (let [current  @model
        tabs     (if (satisfies? cljs.core/IDeref tabs) @tabs tabs)]     ;; if it's an atom we want the contents
    [:div.btn-group
     (for [[this-id {:keys [label]}] tabs]
       (let [selected? (= this-id current)]                    ;; must use current instead of @model to avoid reagent warnings
         [:button.btn.btn-default
          {:type     "button"
           :key      (str this-id)
           :style    (if selected? {:background-color "#AAA"  :color "white"} {})
           :on-click #(reset! model this-id)}
          label]))]))


(defn horizontal-pills    ;; tabs-like in action
  [& {:keys [model tabs]}]
  (let [current  @model
        tabs     (if (satisfies? cljs.core/IDeref tabs) @tabs tabs)] ;; (if (instance? IDeref tabs) @tabs tabs)]     ;; if it's an atom we want the contents
    [:ul.nav.nav-pills
     {:role "tabslist"}
     (for [[this-id {:keys [label]}] tabs]
       (let [selected? (= this-id current)]                   ;; must use 'current' instead of @model to avoid reagent warnings
         [:li
          {:class    (if selected? "active" "")
           :key      (str this-id)}
          [:a
           {:on-click  #(reset! model this-id)}
           label]]))]))


(defn horizontal-tabs
  [& {:keys [model tabs]}]
  (let [current  @model
        tabs     (if (satisfies? cljs.core/IDeref tabs) @tabs tabs)]     ;; if it's an atom we want the contents
    [:ul.nav.nav-tabs
     (for [[this-id {:keys [label]}] tabs]
       (let [selected? (= this-id current)]                   ;; must use current instead of @model to avoid reagent warnings
         [:li
          {:class  (if selected? "active")
           :key    (str this-id)}
          [:a
           {:on-click  #(reset! model this-id)}
           label]]))]))
