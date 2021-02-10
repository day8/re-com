(ns re-demo.slider
  (:require [re-com.core   :refer [h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]]
            [re-com.slider :refer [slider-parts-desc slider-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]
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
       :children [[panel-title "[slider ... ]"
                                "src/re_com/slider.cljs"
                                "src/re_demo/slider.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "Standard HTML5 slider control."]
                                          [args-table slider-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
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
                                                      [v-box
                                                       :gap "10px"
                                                       :style    {:min-width        "150px"
                                                                  :padding          "15px"
                                                                  :border-top       "1px solid #DDD"
                                                                  :background-color "#f7f7f7"}
                                                       :children [[title :level :level3 :label "Interactive Parameters" :style {:margin-top "0"}]
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
                                                                                (reset! disabled? val))]]]]]]]]]
                  [parts-table "slider" slider-parts-desc]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [slider-demo])
