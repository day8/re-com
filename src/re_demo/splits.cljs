(ns re-demo.splits
  (:require [re-com.core   :refer [h-box v-box box gap line scroller border h-split v-split title flex-child-style p]]
            [re-com.splits :refer [h-split-args-desc v-split-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(def rounded-panel (merge (flex-child-style "1")
                          {:background-color "#fff4f4"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :padding          "0px 20px 0px 20px"}))

(defn spliter-panel-title
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
           [spliter-panel-title [:code ":panel-1"]]]])


(defn right-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [spliter-panel-title [:code ":panel-2"]]]])


(defn top-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [spliter-panel-title [:code ":panel-1"]]]])


(defn bottom-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [spliter-panel-title [:code ":panel-2"]]]])


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "Spliter Components"
                            "src/re_com/splits.cljs"
                            "src/re_demo/splits.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[h-split ...]"]
                                      [status-text "Stable"]
                                       [p "Arranges two components horizontally and provides a splitter bar between them."]
                                       [p "By dragging the splitter bar, a user can change the width allocated to each."]
                                       [p "Can contain further nested layout components."]
                                      [args-table h-split-args-desc]]]
                          [v-box
                           :size     "auto"
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [h-split
                                       :panel-1 [left-panel]
                                       :panel-2 [right-panel]]]]]]
              [line :style {:margin-top "20px"}]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[v-split ...]"]
                                      [p "Same as above, but vertical."]
                                      [args-table v-split-args-desc]]]
                          [v-box
                           :size     "auto"
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [v-split
                                       :panel-1       [top-panel]
                                       :panel-2       [bottom-panel]
                                       :initial-split "25%"]]]]]
              [gap :size "30px"]]])
