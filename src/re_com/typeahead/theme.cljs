(ns re-com.typeahead.theme
  (:require
   [re-com.typeahead :as-alias ta]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]))

(defmethod bootstrap ::ta/wrapper [props]
  (tu/class props "rc-typeahead"))

(defmethod base ::ta/suggestions-wrapper [props]
  (tu/style props {:position :relative}))

(defmethod bootstrap ::ta/suggestions-container [props]
  (tu/class props "rc-typeahead-suggestions-container"))

(defmethod bootstrap ::ta/throbber [props]
  (tu/class props "rc-typeahead-throbber"))

(defmethod bootstrap ::ta/suggestion [props]
  (let [{:keys [selected?]} (get-in props [:re-com :state])]
    (tu/class props "rc-typeahead-suggestion"
              (when selected? "active"))))
