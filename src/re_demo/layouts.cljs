(ns re-demo.layouts
  (:require [re-com.core    :refer [h-box v-box box gap line scroller border h-layout v-layout]]
            [re-com.layout  :refer [h-layout-args-desc v-layout-args-desc]]
            ;[re-com.box     :refer [h-box v-box box gap line scroller border]]
            ;[re-com.layout  :refer [h-layout h-layout-args-desc
            ;                        v-layout v-layout-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table github-hyperlink status-text]]))


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


#_(defn right-panel
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


(defn right-panel
  []
  [v-layout
   :top-panel     top-panel
   :bottom-panel  bottom-panel
   :margin        "0px"
   :initial-split "25%"])


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "Layout Components"
                            [github-hyperlink "Component Source" "src/re_com/layout.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/layouts.cljs"]
                            [status-text "Alpha"]]]

              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "[h-layout ...]"]
                                      [:span "The h-layout is used to..."]
                                      [args-table h-layout-args-desc]]]
                          [v-box
                           :size     "auto"
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [h-layout
                                       :left-panel    left-panel
                                       :right-panel   right-panel]]]]]
              [line :style {:margin-top "20px"}]
              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "[v-layout ...]"]
                                      [:span "The v-layout is used to..."]
                                      [args-table v-layout-args-desc]]]
                          [v-box
                           :size     "auto"
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [v-layout
                                       :top-panel     top-panel
                                       :bottom-panel  bottom-panel
                                       :initial-split "25%"]]]]]
              [gap :size "30px"]]])
