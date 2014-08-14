(ns re-com.demo.modals
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button spinner progress-bar]]
            [re-com.modal             :refer [modal-window
                                              cancel-button
                                              looper
                                              domino-process]]
            [cljs.core.async          :refer [<! >! chan close! put! take! alts! timeout]]
            [reagent.core             :as    reagent]))



(defn panel
  []
  [:div
   [:h3.page-header "Modals"]])

