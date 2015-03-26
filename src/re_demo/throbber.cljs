(ns re-demo.throbber
  (:require [re-com.core    :refer [h-box v-box box gap line button label throbber]]
            [re-com.misc    :refer [throbber-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core   :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-throbber  false}))

(defn throbber-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[throbber ... ]"
                            [github-hyperlink "Component Source" "src/re_com/misc.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/throbber.cljs"]]]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Stable"]
                                      [paragraphs
                                       [:p "A CSS Throbber."]]
                                      [args-table throbber-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [h-box
                                       :gap "50px"
                                       :children [[v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":small"]]
                                                              [throbber
                                                               :size  :small
                                                               :color "red"]]]
                                                  [v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":regular"]]
                                                              [throbber]]]
                                                  [v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":large"]]
                                                              [throbber
                                                               :size  :large
                                                               :color "blue"]]]]]]]]]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [throbber-demo])
