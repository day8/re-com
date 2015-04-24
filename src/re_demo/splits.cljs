(ns re-demo.splits
  (:require [re-com.core   :refer [h-box v-box box gap line scroller border h-split v-split title flex-child-style p]]
            [re-com.splits :refer [hv-split-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(def rounded-panel (merge (flex-child-style "1")
                          {:background-color "#fff4f4"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :padding          "0px 20px 0px 20px"}))

(defn splitter-panel-title
  [text]
  [title
   :label text
   :level :level3
   :style {:margin-top "20px"}])

(defn left-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-1"]]]])


(defn right-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-2"]]]])


(defn top-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-1"]]]])


(defn bottom-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-2"]]]])


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "Splitter Components"
                            "src/re_com/splits.cljs"
                            "src/re_demo/splits.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[h-split ... ] & [v-split ... ]"]
                                      [status-text "Stable"]
                                       [p "Arranges two components horizontally (or vertically) and provides a splitter bar between them."]
                                       [p "By dragging the splitter bar, a user can change the width (or height) allocated to each."]
                                       [p "Can contain further nested layout components."]
                                      [args-table hv-split-args-desc]]]
                          [v-box
                           :size     "auto"
                           :gap      "10px"
                           :height   "800px"
                           :children [[title2 "Demo"]
                                      [title :level :level3 :label "[h-split]"]
                                      [h-split
                                       :panel-1 [left-panel]
                                       :panel-2 [right-panel]
                                       :size    "300px"]
                                      [title :level :level3 :label "[v-split]"]
                                      [v-split
                                       :panel-1       [top-panel]
                                       :panel-2       [bottom-panel]
                                       :size          "300px"
                                       :initial-split "25%"]]]]]]])
