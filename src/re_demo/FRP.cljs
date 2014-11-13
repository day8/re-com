(ns re-demo.alerts

  (:require [re-com.util     :refer [insert-nth remove-nth position-for-id]]
            [re-com.core     :refer [button label title]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown find-choice filter-choices-by-keyword]]
            [re-com.alert    :refer [alert-box alert-list]]
            [reagent.core    :as    reagent]))
