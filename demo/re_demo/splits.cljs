(ns re-demo.splits
  (:require [re-com.core   :refer [at h-box v-box box gap line scroller border h-split v-split title flex-child-style p]]
            [re-com.splits :refer [hv-split-parts-desc hv-split-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]))

(def rounded-panel (merge (flex-child-style "1")
                          {:background-color "#fff4f4"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :padding          "0px 20px 0px 20px"}))

(defn splitter-panel-title
  [text]
  [title :src (at)
   :label text
   :level :level3
   :style {:margin-top "20px"}])

(defn left-panel
  []
  [box :src (at)
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-1"]]]])

(defn right-panel
  []
  [box :src (at)
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-2"]]]])

(defn top-panel
  []
  [box :src (at)
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-1"]]]])

(defn bottom-panel
  []
  [box :src (at)
   :size "auto"
   :child [:div {:style rounded-panel}
           [splitter-panel-title [:code ":panel-2"]]]])

(defn panel
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "Splitter Components"
               "src/re_com/splits.cljs"
               "src/re_demo/splits.cljs"]

              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "[h-split ... ] & [v-split ... ]"]
                                      [status-text "Stable"]
                                      [p "Arranges two components horizontally (or vertically) and provides a splitter bar between them."]
                                      [p "By dragging the splitter bar, a user can change the width (or height) allocated to each."]
                                      [p "Can contain further nested layout components."]
                                      [args-table hv-split-args-desc]]]
                          [v-box :src (at)
                           :size     "auto"
                           :gap      "10px"
                           :height   "800px"
                           :children [[title2 "Demo"]
                                      [title :src (at) :level :level3 :label "[h-split]"]
                                      [h-split :src (at)
                                       :panel-1 [left-panel]
                                       :panel-2 [right-panel]
                                       :size    "300px"
                                       :parts   {:top    {:style {:overflow :hidden}}
                                                 :bottom {:style {:overflow :hidden}}}]
                                      [title :src (at) :level :level3 :label "[v-split]"]
                                      [v-split :src (at)
                                       :panel-1       [top-panel]
                                       :panel-2       [bottom-panel]
                                       :size          "300px"
                                       :initial-split "25%"
                                       :parts         {:top    {:style {:overflow :hidden}}
                                                       :bottom {:style {:overflow :hidden}}}]]]]]
              [parts-table "h-split" hv-split-parts-desc]]])
