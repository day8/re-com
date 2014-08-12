(ns re-com.demo.welcome
   (:require [reagent.core :as reagent]))



(defn panel
  []
  [:div
   [:h1.page-header "Re-Com"]
   [:p "Re-com is a component library for Reagent which makes heavy use of Bootstrap."]])
