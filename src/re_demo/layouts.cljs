(ns re-demo.layouts
  (:require [re-com.box     :refer [h-box v-box box gap line scroller border]]
            [re-com.layout  :refer [h-layout v-layout]]
            [re-demo.utils  :refer [panel-title component-title args-table]]))


(def rounded-panel {:background-color "#fff4f4"
                    :border           "1px solid lightgray"
                    :border-radius    "8px"
                    ;:margin           "8px"
                    :padding          "0px 20px 0px 20px"
                    :flex             "1"
                    ;:overflow-x       "hidden"
                    ;:overflow-y       "auto"
                    })

(defn left-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [component-title "Left panel"]]])


(defn right-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [component-title "Right panel"]]])


(defn top-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [component-title "Top panel"]]])


(defn bottom-panel
  []
  [box
   :size "auto"
   :child [:div {:style rounded-panel}
           [component-title "Bottom panel"]]])


(defn panel2
  []
  [h-box
   :size "auto"
   :children [[v-box
               :size     "auto"
               :children [[panel-title "Horizontal Layout"]
                          [h-layout
                           :left-panel    left-panel
                           :right-panel   right-panel]]]
              [gap :size "10px"]
              [line]
              [gap :size "10px"]
              [v-box
               :size     "auto"
               :children [[panel-title "Vertical Layout"]
                          [v-layout
                           :top-panel     top-panel
                           :bottom-panel  bottom-panel
                           :initial-split "25%"]]]]])


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panel2])
