(ns re-demo.label
  (:require [re-com.core   :refer [h-box v-box box gap line label p]]
            [re-com.text   :refer [label-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text ]]))


(defn label-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[label ... ]"
                            "src/re_com/text.cljs"
                            "src/re_demo/label.cljs"]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [:p "A short single line of text."]
                                      [args-table label-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [v-box
                                       :children [[label :label "This is a label"]]]]]]]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [label-demo])
