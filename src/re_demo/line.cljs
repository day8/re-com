(ns re-demo.line
  (:require [re-com.box      :refer [h-box v-box box gap line line-args-desc]]
            [re-demo.utils   :refer [panel-title component-title args-table]]))


(defn panel
  []
  [v-box
   :gap      "10px"
   :children [[panel-title "[line ... ]"]

              [h-box
              :gap      "50px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [:span "The line is used to..."]
                                      [args-table line-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
