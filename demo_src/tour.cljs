(ns re-com.demo.tour
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button]]
            [reagent.core             :as    reagent]))



(defn panel
  []

  [:div
   [:h3.page-header "Tour"]
   ])
