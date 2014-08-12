(ns re-com.demo.basics
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button spinner progress-bar]]
            [reagent.core             :as    reagent]))



(defn panel
  []
  [:div
   [:h1.page-header "Basics"]
   [:p "Should show buttons and input fields in here"]
   [:p "Perhaps typography"]
   [:p "XXX Explain that bootstrap has to be included into the html"]])

