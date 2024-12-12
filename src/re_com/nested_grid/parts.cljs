(ns re-com.nested-grid.parts
  (:require [re-com.nested-grid.util :as ngu]
            [re-com.nested-grid :as-alias ng]))

(defn cell-wrapper [{:keys [style class row-path column-path]}]
  [:div {:style (merge {:grid-row-start    (ngu/path->grid-line-name row-path)
                        :grid-column-start (ngu/path->grid-line-name column-path)}
                       style)
         :class class}
   (str (gensym))])
