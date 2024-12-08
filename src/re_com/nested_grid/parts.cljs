(ns re-com.nested-grid.parts
  (:require [re-com.nested-grid.util :as ngu]
            [re-com.nested-grid :as-alias ng]))

(defn cell [{:keys [row-path column-path]}]
  [:div {:style {:border            "thin solid grey"
                 :grid-row-start    (ngu/path->grid-line-name row-path)
                 :grid-column-start (ngu/path->grid-line-name column-path)
                 :font-size         6}}
   (str (gensym))])

(defn row-header-wrapper [{:keys [style]}]
  [:div {:style (merge style {:font-size         6})}
   (gensym)])
