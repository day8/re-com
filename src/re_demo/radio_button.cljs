(ns re-demo.radio-button
  (:require [re-com.core   :refer [h-box v-box box gap checkbox title line radio-button p]]
            [re-com.misc   :refer [radio-button-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core  :as    reagent]))


(defn radios-demo
  []
  (let [disabled?   (reagent/atom false)
        color (reagent/atom "green")]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[radio-button ... ]"
                                "src/re_com/misc.cljs"
                                "src/re_demo/radio_button.cljs"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A boostrap-styled radio button, with optional label (always displayed to the right)."]
                                          [p "Clicking on the label is the same as clicking on the radio button."]
                                          [args-table radio-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :gap "30px"
                                           :children [[v-box
                                                       :children [(doall (for [c ["red" "green" "blue"]]    ;; Notice the ugly "doall"
                                                                           ^{:key c}                        ;; key should be unique among siblings
                                                                           [radio-button
                                                                            :disabled? disabled?
                                                                            :label       c
                                                                            :value       c
                                                                            :model       color
                                                                            :label-style (if (= c @color) {:color       c
                                                                                                            :font-weight "bold"})
                                                                            :on-change   #(reset! color %)]))]]
                                                      [v-box
                                                       :gap "15px"
                                                       :children [[title :level :level3 :label "Parameters"]
                                                                  [checkbox
                                                                   :label [:code ":disabled?"]
                                                                   :model disabled?
                                                                   :on-change (fn [val]
                                                                                (reset! disabled? val))]]]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [radios-demo])
