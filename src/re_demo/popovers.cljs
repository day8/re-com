(ns re-demo.popovers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button spinner progress-bar]]
            [re-com.popover           :refer [popover make-button make-link]]
            [re-com.popover-form-demo :as    popover-form-demo]
            [reagent.core             :as    reagent]))



(defn panel
  []
  [:div
   [:h3.page-header "Popovers"]
   ])

