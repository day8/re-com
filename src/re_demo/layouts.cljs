(ns re-demo.layouts
  (:require [re-com.util              :as    util]
            [re-com.box               :refer [h-box v-box box
                                              gap line]]
            [re-com.core              :refer [button]]
            [reagent.core             :as    reagent]))


(defn panel
  []
  [:div
   [:h3.page-header "Layout"]
   ])
