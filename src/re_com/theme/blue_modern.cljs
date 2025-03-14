(ns re-com.theme.blue-modern
  (:require
   [re-com.theme :as theme]
   [re-com.text :as text]
   [re-com.dropdown :as dropdown]
   [re-com.tree-select :as tree-select]
   [re-com.error-modal :as-alias error-modal]))

(defn theme [{:as            props
              :keys          [part]
              {$ :variables} :re-com}]
  (->> (case part
         ::dropdown/anchor-wrapper
         {:style {:height "25px"}}
         ::dropdown/indicator
         {:style {:color (:light-foreground $)}}
         {})
       (theme/merge-props props)))
