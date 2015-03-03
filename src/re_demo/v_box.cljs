(ns re-demo.v-box
  (:require [re-com.box      :refer [h-box gap v-box v-box-args-desc]]
            [re-com.buttons  :refer [hyperlink-href]]
            [re-demo.utils   :refer [panel-title component-title args-table]]))


(defn panel
  []
  [v-box
   :gap      "10px"
   :children [[panel-title "[v-box ... ]"]

              [h-box
              :gap      "50px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title [:span "Notes" [:span
                                                                       {:style {:font-size    "13px"
                                                                                :font-variant "small-caps"
                                                                                :margin-left  "8px"}}
                                                                       [hyperlink-href
                                                                        :label  "(Github Source)"
                                                                        :href   "https://github.com/Day8/re-com/blob/091ab19b5c7b79b2935aaea82990123d593fa936/src/re_com/box.cljs"
                                                                        :target "_blank"]]]]
                                      [:span "The v-box is used to..."]
                                      [args-table v-box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
