(ns re-demo.hyperlink-href
  (:require [re-com.core      :refer [label radio-button]]
            [re-com.buttons   :refer [hyperlink-href hyperlink-href-args-desc]]
            [re-com.box       :refer [h-box v-box box gap line]]
            [re-demo.utils    :refer [panel-title component-title args-table]]
            [reagent.core     :as    reagent]))


(defn hyperlink-href-demo
  []
  (let [target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [v-box
       :gap "10px"
       :children [[panel-title "[hyperlink-href ... ]"]

                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[args-table hyperlink-href-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [h-box
                                           :gap "40px"
                                           :children [[box
                                                       :width "200px"
                                                       :align :start
                                                       :child [hyperlink-href
                                                               :label     "Launch Google"
                                                               :tooltip   "You're about to launch Google"
                                                               :href      (when href? "http://google.com")
                                                               :target    (when href? target)]]
                                                      [v-box
                                                       :gap "15px"
                                                       :children [[label :label "parameters:"]
                                                                  (when @href?
                                                                    [v-box
                                                                     :children [[label :label ":target"]
                                                                                [radio-button
                                                                                 :label "_self - load link into same tab"
                                                                                 :value "_self"
                                                                                 :model @target
                                                                                 :on-change #(reset! target "_self")
                                                                                 :style {:margin-left "20px"}]
                                                                                [radio-button
                                                                                 :label "_blank - load link inot new tab"
                                                                                 :value "_blank"
                                                                                 :model @target
                                                                                 :on-change #(reset! target "_blank")
                                                                                 :style {:margin-left "20px"}]]])]]]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [hyperlink-href-demo])
