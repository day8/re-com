(ns re-demo.throbber
  (:require [re-com.core     :refer [at h-box v-box box gap line button label throbber p]]
            [re-com.throbber :refer [throbber-parts-desc throbber-args-desc]]
            [re-demo.utils   :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util     :refer [px]]
            [reagent.core    :as    reagent]))

(def state (reagent/atom
            {:outcome-index 0
             :see-throbber  false}))

(defn throbber-demo
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[throbber ... ]"
               "src/re_com/throbber.cljs"
               "demo/re_demo/throbber.cljs"]

              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "A CSS Throbber."]
                                      [args-table throbber-args-desc]]]
                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [h-box :src (at)
                                       :gap "50px"
                                       :children [[v-box :src (at)
                                                   :align :center
                                                   :children [[box :src (at) :align :start :child [:code ":smaller"]]
                                                              [throbber :src (at)
                                                               :size :smaller
                                                               :color "green"]]]
                                                  [v-box :src (at)
                                                   :align :center
                                                   :children [[box :src (at) :align :start :child [:code ":small"]]
                                                              [throbber :src (at)
                                                               :size  :small
                                                               :color "red"]]]
                                                  [v-box :src (at)
                                                   :align :center
                                                   :children [[box :src (at) :align :start :child [:code ":regular"]]
                                                              [throbber :src (at)]]]
                                                  [v-box :src (at)
                                                   :align :center
                                                   :children [[box :src (at) :align :start :child [:code ":large"]]
                                                              [throbber :src (at)
                                                               :size  :large
                                                               :color "blue"]]]]]]]]]
              [parts-table "throbber" throbber-parts-desc]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [throbber-demo])
