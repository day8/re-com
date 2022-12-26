(ns re-demo.radio-button
  (:require [re-com.core          :refer [at h-box v-box box gap checkbox title line radio-button p]]
            [re-com.radio-button  :refer [radio-button-parts-desc radio-button-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util          :refer [px]]
            [reagent.core         :as    reagent]))

(defn radios-demo
  []
  (let [disabled?   (reagent/atom false)
        color (reagent/atom "green")]
    (fn
      []
      [v-box :src (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[radio-button ... ]"
                                "src/re_com/radio_button.cljs"
                                "src/re_demo/radio_button.cljs"]
                  [h-box :src (at)
                   :gap      "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A boostrap-styled radio button, with optional label (always displayed to the right)."]
                                          [p "Clicking on the label is the same as clicking on the radio button."]
                                          [args-table radio-button-args-desc]]]
                              [v-box :src (at)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          (doall (for [c ["red" "green" "blue"]]    ;; Notice the ugly "doall"
                                                   ^{:key c}                        ;; key should be unique among siblings
                                                   [radio-button :src (at)
                                                    :disabled? disabled?
                                                    :label       c
                                                    :value       c
                                                    :model       color
                                                    :label-style (if (= c @color) {:color       c
                                                                                   :font-weight "bold"})
                                                    :on-change   #(reset! color %)]))
                                          [v-box :src (at)
                                           :gap "10px"
                                           :style {:min-width        "150px"
                                                   :padding          "15px"
                                                   :border-top       "1px solid #DDD"
                                                   :background-color "#f7f7f7"}
                                           :children [[title :src (at) :level :level3 :label "Interactive Parameters" :style {:margin-top "0"}]
                                                      [checkbox :src (at)
                                                       :label [:code ":disabled?"]
                                                       :model disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]

                  [parts-table "radio-button" radio-button-parts-desc]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [radios-demo])
