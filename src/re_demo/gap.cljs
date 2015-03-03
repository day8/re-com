(ns re-demo.gap
  (:require [re-com.box      :refer [h-box v-box gap gap-args-desc]]
            [re-demo.utils   :refer [panel-title component-title args-table]]))


(defn panel
  []
  [v-box
   :gap      "10px"
   :children [[panel-title "[gap ... ]"]

              [h-box
              :gap      "50px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [:span "The gap is used to..."]
                                      [args-table gap-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
