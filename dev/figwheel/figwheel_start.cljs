(ns figwheel-start.core
  (:require [figwheel.client :as    fw :include-macros true]
            [reagent.core    :refer [force-update-all]]
            [re-demo.core    :as    core]))

(defn start []
  ;; was (fn [] (core/mount-demo)) but this resets the app each time figwheel loads
  ;; disadvantage with force-update-all is that the core component does not update when figwheel reloads
  (fw/start {:jsload-callback force-update-all})
  (core/mount-demo))
