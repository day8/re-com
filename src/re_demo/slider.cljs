(ns re-demo.slider
  (:require [re-com.core   :refer [h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs]]
            [re-com.misc   :refer [slider-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core  :as    reagent]))


(defn slider-demo
  []
  (let [slider-val  (reagent/atom "0")
        slider-min  (reagent/atom "0")
        slider-max  (reagent/atom "100")
        slider-step (reagent/atom "1")
        disabled?   (reagent/atom false)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[slider ... ]"
                                [github-hyperlink "Component Source" "src/re_com/misc.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/slider.cljs"]]]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "Standard HTML5 slider control."]]
                                          [args-table slider-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :gap      "20px"
                                           :children [[slider
                                                       :model     slider-val
                                                       :min       slider-min
                                                       :max       slider-max
                                                       :step      slider-step
                                                       :width     "300px"
                                                       :on-change #(reset! slider-val (str %))
                                                       :disabled? disabled?]
                                                      [gap :size "0px"]
                                                      [title :level :level3 :label "Parameters"]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":model"]]
                                                                  [input-text
                                                                   :model           slider-val
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-val %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":min"]]
                                                                  [input-text
                                                                   :model           slider-min
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-min %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":max"]]
                                                                  [input-text
                                                                   :model           slider-max
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-max %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":step"]]
                                                                  [input-text
                                                                   :model           slider-step
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-step %)
                                                                   :change-on-blur? false]]]
                                                      [checkbox
                                                       :label [box :align :start :child [:code ":disabled?"]]
                                                       :model disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [slider-demo])
