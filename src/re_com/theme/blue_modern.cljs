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
  (->> (or
        (case part-path
          [:re-com.error-modal/sub-title-2
           ::text/title-label]
          {:style {:color "red"}}
          nil)
        (case part
          ::dropdown/anchor-wrapper
          {:style {:height      "25px"
                   :line-height "23px"}}
          nil)
        {})
       (theme/merge-props props)))
