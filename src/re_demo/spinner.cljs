(ns re-demo.spinner
  (:require [re-com.core    :refer [h-box v-box box gap line button label spinner]]
            [re-com.misc :refer [spinner-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core   :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-spinner  false}))

(defn spinner-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[spinner ... ]"
                            [github-hyperlink "Component Source" "src/re_com/misc.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/spinner.cljs"]]]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Stable"]
                                      [paragraphs
                                       [:p "A CSS spinner, aka Throbber."]]
                                      [args-table spinner-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [h-box
                                       :gap "50px"
                                       :children [[v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":small"]]
                                                              [spinner
                                                               :size  :small
                                                               :color "red"]]]
                                                  [v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":regular"]]
                                                              [spinner]]]
                                                  [v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":large"]]
                                                              [spinner
                                                               :size  :large
                                                               :color "blue"]]]]]]]]]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [spinner-demo])
