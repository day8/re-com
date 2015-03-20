(ns re-demo.radio-button
  (:require [re-com.core   :refer [h-box v-box box gap line radio-button]]
            [re-com.misc   :refer [radio-button-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core  :as    reagent]))


(defn radios-demo
  []
  (let [colour (reagent/atom "green")]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[radio-button ... ]"
                                [github-hyperlink "Component Source" "src/re_com/misc.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/radio_button.cljs"]]]
                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "A boostrap-styled radio button, with optional label (always displayed to the right)."]
                                           [:p "Clicking on the label is the same as clicking on the radio button."]]
                                          [args-table radio-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :children [(doall (for [c ["red" "green" "blue"]]    ;; Notice the ugly "doall"
                                                               ^{:key c}                        ;; key should be unique among siblings
                                                               [radio-button
                                                                :label       c
                                                                :value       c
                                                                :model       colour
                                                                :label-style (if (= c @colour) {:color       c
                                                                                                :font-weight "bold"})
                                                                :on-change   #(reset! colour c)]))]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [radios-demo])
