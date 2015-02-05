(ns re-demo-figwheel
  (:require [figwheel.client :as fw :include-macros true]
            [re-demo.core    :as core]))

(defn main []
  (fw/start {:jsload-callback (fn [] (core/mount-demo))})
  (core/mount-demo))
