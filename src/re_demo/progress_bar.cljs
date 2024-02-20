(ns re-demo.progress-bar
  (:require [re-com.core         :refer [at h-box v-box box gap line label title progress-bar slider checkbox p]]
            [re-com.progress-bar :refer [progress-bar-parts-desc progress-bar-args-desc]]
            [re-demo.utils       :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util         :refer [px]]
            [reagent.core        :as    reagent]))

(defn progress-bar-demo
  []
  (let [progress (reagent/atom 75)
        striped? (reagent/atom false)]
    (fn
      []
      [v-box :src (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[progress-bar ... ]"
                   "src/re_com/progress_bar.cljs"
                   "src/re_demo/progress_bar.cljs"]
                  [h-box :src (at)
                   :gap      "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A Bootstrap styled progress bar."]
                                          [args-table progress-bar-args-desc]]]
                              [v-box :src (at)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [v-box :src (at)
                                           :gap      "20px"
                                           :children [[progress-bar :src (at)
                                                       :model    progress
                                                       :width    "350px"
                                                       :striped? @striped?]
                                                      [v-box :src (at)
                                                       :gap "10px"
                                                       :style    {:min-width        "150px"
                                                                  :padding          "15px"
                                                                  :border-top       "1px solid #DDD"
                                                                  :background-color "#f7f7f7"}
                                                       :children [[title :src (at) :level :level3 :label "Interactive Parameters" :style {:margin-top "0"}]
                                                                  [h-box :src (at)
                                                                   :gap "10px"
                                                                   :children [[box :src (at) :align :start :child [:code ":model"]]
                                                                              [slider :src (at)
                                                                               :model     progress
                                                                               :min       0
                                                                               :max       100
                                                                               :width     "200px"
                                                                               :on-change #(reset! progress %)]
                                                                              [label :src (at) :label @progress]]]
                                                                  [checkbox :src (at)
                                                                   :label     [box :src (at) :align :start :child [:code ":striped?"]]
                                                                   :model     striped?
                                                                   :on-change #(reset! striped? %)]]]]]]]]]
                  [parts-table "progress-bar" progress-bar-parts-desc]]])))

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [progress-bar-demo])
