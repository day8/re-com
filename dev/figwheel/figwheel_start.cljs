(ns figwheel-start.core
  (:require [figwheel.client :as fw :include-macros true]
            [re-demo.core    :as core]))

(defn start []
  (fw/start {:jsload-callback (fn [] (core/mount-demo))})
  (core/mount-demo))
