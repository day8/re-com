(ns re-demo.multi-complete
  (:require [reagent.core :as r]
            [re-com.multi-complete :refer [multi-complete]]
            [re-com.box      :refer [box border h-box v-box]]
            [re-com.core :as rc]
            ))




(defn panel []
    (let [selections (r/atom [])]
      [multi-complete {:highlight-class "multi-complete-highlight"
                       :selections selections
                       :on-delete #(swap! selections pop)
                       :save! #(swap! selections conj %)
                       :suggestions ["Reagent""Re-frame""Re-com""Reaction"]}])
    )


