(ns re-com.dropdown.parts
  (:require [re-com.util :as u]))

(defn indicator [{:keys [state style]}]
  [:span {:style style}
   [u/triangle {:direction (case (:openable state) :open :up :closed :down)}]])
