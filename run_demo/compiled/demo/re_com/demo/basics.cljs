(ns re-com.demo.basics
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button spinner progress-bar]]
            [reagent.core             :as    reagent]))



(defn panel
  []
  [:div "Basics"]
  )

