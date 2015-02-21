(ns re-demo.layouts
  (:require [re-com.box     :refer [h-box v-box box gap line scroller border]]
            [re-com.layout  :refer [h-layout h-layout-args-desc
                                    v-layout v-layout-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table]]))


(defn arg-lists
  []
  [v-box
   :gap      "10px"
   :children [[panel-title "Layout Components"]

              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "NOTE: Might want to split these into separate pages like buttton and basics" {:font-style "italic"}]
                                      [component-title "[h-layout ...]"]
                                      [:span "The h-layout is used to..."]
                                      [args-table h-layout-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "[v-layout ...]"]
                                      [:span "The v-layout is used to..."]
                                      [args-table v-layout-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])

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
