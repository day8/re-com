(ns re-demo.label
  (:require [re-com.core   :refer [at h-box v-box box gap line label p]]
            [re-com.text   :refer [label-parts-desc label-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]))

(defn label-demo
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[label ... ]"
               "src/re_com/text.cljs"
               "demo/re_demo/label.cljs"]
              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [:p "A short single line of text."]
                                      [args-table label-args-desc]]]
                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [v-box :src (at)
                                       :children [[label :src (at) :label "This is a label."]]]]]]]
              [parts-table "label" label-parts-desc]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [label-demo])
