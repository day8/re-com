(ns re-com.theme.blue-modern
  (:require
   [re-com.theme :as theme]
   [re-com.dropdown :as dropdown]
   [re-com.tree-select :as tree-select]))

(defn theme [attr {:keys [state part] $ :variables}]
  (->> {}
       (case part

         #_#_::dropdown/backdrop
           {:style {:background-color (:primary $)}}

         ::dropdown/anchor-wrapper
         {:style {:box-shadow       "0 0.5px 0.5px rgba(0, 0, 0, .2) inset"
                  :background-color "white"
                  :height           "25px"
                  :line-height      "25px"}})
       (theme/merge-props attr)))
