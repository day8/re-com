(ns re-demo.throbber
  (:require [re-com.core    :refer [h-box v-box box gap line button label throbber p]]
            [re-com.misc    :refer [throbber-args-desc]]
            [re-demo.utils  :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core   :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-throbber  false}))

(defn throbber-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[throbber ... ]"
                            "src/re_com/misc.cljs"
                            "src/re_demo/throbber.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "A CSS Throbber."]
                                      [args-table throbber-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
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
