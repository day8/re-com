(ns re-com.theme.blue-modern
  (:require [re-com.theme :as theme]))

(defn theme [attr {:keys [state part] $ :variables}]
  (->> {}
       (case part
         :backdrop {:style {:background-color (:primary $)}})
       (theme/merge-props attr)))
