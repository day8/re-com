(ns re-demo.utils
  (:require [re-com.core     :refer [title label]]
            [reagent.core    :as    reagent]))


#_(def panel-title-style {
    :font-family "Ubuntu"
    :font-weight "300"
    :font-size   "24px"
    :color       "#FF6522"    ;; orange
    :margin-top  "7px"
   })

(def ubuntu-font {
  :font-family "Ubuntu"
  :font-weight "300" })


(def panel-title-style {
    :font-size   "24px"
    :color       "#FFF"
    :background-color  "#888"
    :height      "50px"
   })

(defn panel-title
  "The text shown at the top of each Tab Panel"
  [text style]
  [title
   :label    text
   :style    (merge ubuntu-font  style)])




(defn component-title
  "Used when you want a notes title like [something ... ]"
  [component-name style]
  [title
   :h          :h4   ;; [:h4 .. ]
   :label      component-name
   :style      (merge {:color "#555"}  style)
   :underline? false
   ])