(ns re-demo-figwheel
  (:require [figwheel.client :as fw :include-macros true]
            [reagent.core    :as reagent]))

(fw/start {:jsload-callback (fn [] (reagent/force-update-all))})
