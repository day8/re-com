(ns re-demo.label
  (:require [re-com.core   :refer [h-box v-box box gap line label]]
            ;[re-com.text   :refer [label-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]))


(defn label-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[label ... ]"
                            [github-hyperlink "Component Source" "src/re_com/text.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/label.cljs"]]]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Stable"]
                                      [paragraphs
                                       [:p "A short single line of text."]]
                                      #_[args-table label-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [v-box
                                       :children [[label :label "This is a label"]]]]]]]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [label-demo])
