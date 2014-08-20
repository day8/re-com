(ns re-com.tabs
   (:require [reagent.core :as reagent]))


;; 'model' is expected to be an atom containing a keyword identifier. Eg.  ::first-tab
;; 'tabs' can be either a map OR an atom containing a map
;;        the map should look like this:
;;        { ::id1  {:label "1" } ::id2  {:label "2" } ::anotherId  {:label "3" }
;;


;; utility function for finding a tab definition with a given id
;; 'tab-defs' is a vector of definitions
;; 'id' is the id of one of them
;; returns nil if no definition found
;; otherwise returns the first match found
(defn find-tab
  [id tab-defs]
  (first (filter #(= id (:id %)) tab-defs)))

(defn horizontal-bar-tabs
  [& {:keys [model tabs]}]
  (let [current  @model
        tabs     (if (satisfies? cljs.core/IDeref tabs) @tabs tabs)]     ;; if it's an atom we want the vector inside
    [:div.btn-group
     (for [t tabs]
       (let [id        (:id t)
             label     (:label t)
             selected? (= id current)]                    ;; must use current instead of @model to avoid reagent warnings
         [:button.btn.btn-default
          {:type     "button"
           :key      (str id)
           :style    (if selected? {:background-color "#AAA"  :color "white"} {})
           :on-click #(reset! model id)}
          label]))]))


(defn horizontal-pills    ;; tabs-like in action
  [& {:keys [model tabs]}]
  (let [current  @model
        tabs     (if (satisfies? cljs.core/IDeref tabs) @tabs tabs)] ;;      ;; if it's an atom we want the map inside
    [:ul.nav.nav-pills
     {:role "tabslist"}
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


(defn horizontal-tabs
  [& {:keys [model tabs]}]
  (let [current  @model
        tabs     (if (satisfies? cljs.core/IDeref tabs) @tabs tabs)]     ;; if it's an atom we want the map inside
    [:ul.nav.nav-tabs
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
