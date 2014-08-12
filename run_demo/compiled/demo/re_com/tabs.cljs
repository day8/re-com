(ns re-com.tabs
   (:require [reagent.core :as reagent]))



(defn horizontal-bar-tabs
  [selected-id tab-defs]
  (let [sid  @selected-id]
    [:div.btn-group
     (for [[this-id {:keys [label]}] tab-defs]
       (let [selected? (= this-id sid)]
         [:button.btn.btn-default
          {:type     "button"
           :key      (str this-id)
           :style    (if selected? {:background-color "#AAA"  :color "white"} {})
           :on-click #(reset! selected-id this-id)}
          label]))]))


(defn horizontal-pills    ;; tabs-like in action
  [selected-id tab-defs]
  (let [sid  @selected-id]
    [:ul.nav.nav-pills
     {:role "tabslist"}
     (for [[this-id {:keys [label]}] tab-defs]
       (let [selected? (= this-id sid)]
         [:li
          {:class    (if selected? "active" "")
           :key      (str this-id)}
          [:a
           {:on-click  #(reset! selected-id this-id)}
           label]]))]))


(defn horizontal-tabs
  [selected-id tab-defs]
  (let [sid  @selected-id]
    [:ul.nav.nav-tabs
     (for [[this-id {:keys [label]}] tab-defs]
       (let [selected? (= this-id sid)]
         [:li
          {:class  (if selected? "active")
           :key    (str this-id)}
          [:a
           {:on-click  #(reset! selected-id this-id)}
           label]]))]))
