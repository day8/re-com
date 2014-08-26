(ns re-demo.tour
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util   :as     util]
            [re-com.core   :refer  [button]]

            [re-demo.util      :refer [title]]
            [re-com.box    :refer  [h-box v-box box
                                    gap line scroller border]]
            [reagent.core  :as     reagent]))


(defn simple-demo
  []
  [title "Tour"])

(defn panel
  []
  [v-box
   :children [[simple-demo]
              #_[hyperlink-popover-demo]
              #_[proximity-popover-demo]]])
