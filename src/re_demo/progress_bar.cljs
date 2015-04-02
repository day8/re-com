(ns re-demo.progress-bar
  (:require [re-com.core   :refer [h-box v-box box gap line label title progress-bar slider checkbox p]]
            [re-com.misc   :refer [progress-bar-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core  :as    reagent]))


(defn progress-bar-demo
  []
  (let [progress (reagent/atom 75)
        striped? (reagent/atom false)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[progress-bar ... ]"
                                "src/re_com/misc.cljs"
                                "src/re_demo/progress_bar.cljs"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A Bootstrap styled progress bar."]
                                          [args-table progress-bar-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [v-box
                                           :gap      "20px"
                                           :children [[progress-bar
                                                       :model    progress
                                                       :width    "350px"
                                                       :striped? @striped?]
                                                      [title :level :level3 :label "Parameters"]
                                                      [h-box
                                                       :gap "10px"
                                                       :children [[box :align :start :child [:code ":model"]]
                                                                  [slider
                                                                   :model     progress
                                                                   :min       0
                                                                   :max       100
                                                                   :width     "200px"
                                                                   :on-change #(reset! progress %)]
                                                                  [label :label @progress]]]
                                                      [checkbox
                                                       :label     [box :align :start :child [:code ":striped?"]]
                                                       :model     striped?
                                                       :on-change #(reset! striped? %)]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [progress-bar-demo])
