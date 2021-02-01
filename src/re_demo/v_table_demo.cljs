(ns re-demo.v-table-demo
  (:require [re-com.core   :refer [h-box gap v-box box v-table p label]]
            [re-com.util    :refer [px]]
            [re-demo.utils :refer [title2]]
            [reagent.core  :as reagent]))



(defn demo
  []
  [v-box
   :gap      "10px"
   :children [[title2 "Demo"]
              [p "coming soon"]]])
