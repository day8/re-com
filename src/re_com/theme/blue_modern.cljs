(ns re-com.theme.blue-modern
  (:require
   [re-com.theme :as theme]
   [re-com.dropdown :as dropdown]
   [re-com.tree-select :as tree-select]))

(defn theme [attr {:keys [state part] $ :variables}]
  (->> {}
       (case part

         ::dropdown/anchor-wrapper
         {:style {:height           "25px"
                  :line-height      "23px"}})
       (theme/merge-props attr)))
