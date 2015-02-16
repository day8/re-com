(ns re-demo.radio_button
  (:require [re-com.core      :refer [radio-button radio-button-args-desc]]
            [re-com.box       :refer [h-box v-box box gap line]]
            [re-demo.utils    :refer [panel-title component-title args-table]]
            [reagent.core     :as    reagent]))


(defn radios-demo
  []
  (let [colour (reagent/atom "green")]
    (fn
      []
      [v-box
       :gap "10px"
       :children [[panel-title "[radio-button ... ]"]

                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :style    {:font-size "small"}
                               :width    "450px"
                               :children [[args-table radio-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :children [(doall (for [c ["red" "green" "blue"]]    ;; Notice the ugly "doall"
                                                               ^{:key c}                        ;; key should be unique within this compenent
                                                               [radio-button
                                                                :label       c
                                                                :value       c
                                                                :model       colour
                                                                :label-style (if (= c @colour) {:background-color c  :color "white"})
                                                                :on-change   #(reset! colour c)]))]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [radios-demo])
