(ns re-com.demo.tour
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button spinner progress-bar]]
            [re-com.v-layout          :refer [v-layout]]
            [re-com.h-layout          :refer [h-layout]]
            [re-com.alert             :refer [closeable-alert alert-list add-alert]]
            [re-com.popover           :refer [popover make-button make-link]]
            [re-com.tour              :refer [make-tour start-tour make-tour-nav]]
            [re-com.modal             :refer [modal-window
                                              cancel-button
                                              looper
                                              domino-process]]
            [re-com.tabs              :refer [horizontal-tabs
                                              horizontal-pills]]
            [re-com.popover-form-demo :as    popover-form-demo]
            [cljs.core.async          :refer [<! >! chan close! put! take! alts! timeout]]
            [reagent.core             :as    reagent]))



(defn panel
  []
  [:div "Tour"]
  )

