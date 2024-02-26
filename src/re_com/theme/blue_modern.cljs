(ns re-com.theme.blue-modern
  (:require
   [re-com.theme :as theme]
   [re-com.dropdown :as dropdown]))

(defn theme [attr {:keys [state part] $ :variables}]
  (->> {}
       (case part
         ::dropdown/backdrop {:style {:background-color (:primary $)}})
       (theme/merge-props attr)))
