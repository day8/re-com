(ns re-demo.utils
  (:require [re-com.core     :refer [title]]
            [reagent.core    :as    reagent]))


(def panel-title-style {
    :font-family "Ubuntu"
    :font-weight "300"
    ;; :font-style  "bold"
    :font-size   "24px"
    :color       "#FF6522"    ;; orange
    :margin-top  "7px"
   })

(defn panel-title
  "The text shown at the top of each Tab Panel"
  [text]
  [title
    :label      text
    :underline? false
    :style      panel-title-style
    ])




(defn component-title
  "Used when you want a notes title like [something ... ]"
  [component-name style]
  [title
   :h          :h4   ;; [:h4 .. ]
   :label      component-name
   :style      (merge style {:color "#555"})
   :underline? false
   ])