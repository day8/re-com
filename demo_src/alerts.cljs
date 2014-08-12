(ns re-com.demo.alerts
  (:require [re-com.util    :as     util]
            [re-com.core    :refer  [button]]
            [re-com.alert   :as     alert]
            [reagent.core   :as     reagent]))


(comment
  (add-alert "danger" {:heading "Unfortunately something bad happened" :body "Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care!"})
  (add-alert "info" {:heading "Here's some info for you" :body "The rain in Spain falls mainly on the plain"})
  (add-alert "warning" {:heading "Hmmm, something might go wrong" :body "There be dragons!"})
  (add-alert "info" {:heading "Here's some info for you" :body "The rain in Spain falls mainly on the plain"})
  )

(defn panel
  []
  [:div
   [:h2.page-header "Alerts"]
   ])
