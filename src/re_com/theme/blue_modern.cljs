(ns re-com.theme.blue-modern
  (:require
   [re-com.theme :as theme]
   [re-com.text :as text]
   [re-com.dropdown :as dropdown]
   [re-com.tree-select :as tree-select]
   [re-com.error-modal :as-alias error-modal]))

(defn theme [props {:keys [state part part-path]
                    $     :variables
                    :as   ctx}]
  (->> (case part
         ::dropdown/anchor-wrapper
         {:style {:height      "25px"}}
         {})
       (theme/merge-props props)))
