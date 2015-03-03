(ns re-demo.box
  (:require [re-com.box      :refer [h-box v-box box box-args-desc gap]]
            [re-com.buttons  :refer [hyperlink-href]]
            [re-demo.utils   :refer [panel-title component-title args-table]]))


(defn panel
  []
  [v-box
   :gap      "10px"
   :children [[panel-title "[box ... ]"]

              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [:span
                                       "Click "
                                       [:span [hyperlink-href
                                               :label  "here"
                                               :href   "https://github.com/Day8/re-com/blob/091ab19b5c7b79b2935aaea82990123d593fa936/src/re_com/box.cljs"
                                               :target "_blank"]]
                                       " for Github Source"]
                                      [args-table box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span {:style {:font-variant "small-caps"}} "This Space Intentionally Left Blank"]]]]]
              [gap :size "30px"]]])
