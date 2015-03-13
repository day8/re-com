(ns re-demo.progress-bar
  (:require [re-com.core   :refer [h-box v-box box gap line label progress-bar slider checkbox]]
            [re-com.misc   :refer [progress-bar-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text]]
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
       :children [[panel-title [:span "[progress-bar ... ]"
                                [github-hyperlink "Component Source" "src/re_com/misc.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/progress_bar.cljs"]
                                [status-text "Alpha"]]]
                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [:span "The progress-bar is used to..."]
                                          [args-table progress-bar-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :gap      "20px"
                                           :children [[progress-bar
                                                       :model    progress
                                                       :width    "350px"
                                                       :striped? @striped?]
                                                      [h-box
                                                       :gap "10px"
                                                       :children [[label :label "Percent:"]
                                                                  [slider
                                                                   :model     progress
                                                                   :min       0
                                                                   :max       100
                                                                   :width     "200px"
                                                                   :on-change #(reset! progress %)]
                                                                  [label :label @progress]]]
                                                      [checkbox
                                                       :label     ":striped?"
                                                       :model     striped?
                                                       :on-change #(reset! striped? %)]]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [progress-bar-demo])
