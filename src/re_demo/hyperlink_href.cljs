(ns re-demo.hyperlink-href
  (:require [re-com.core    :refer [h-box v-box box gap line label title radio-button hyperlink-href p checkbox]]
            [re-com.buttons :refer [hyperlink-href-args-desc]]
            [re-demo.utils  :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core   :as    reagent]))


(defn hyperlink-href-demo
  []
  (let [disabled? (reagent/atom false)
        target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[hyperlink-href ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/hyperlink_href.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A blue, clickable hyperlink which launches external URLs."]
                                          [p "If you want a hyperlink with a click handler, use the [hyperlink] component."]
                                          [args-table hyperlink-href-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :gap "40px"
                                           :children [[box
                                                       :width "200px"
                                                       :align :start
                                                       :child [hyperlink-href
                                                               :label     "Launch Google"
                                                               :tooltip   "You're about to launch Google"
                                                               :href      (when href? "http://google.com")
                                                               :target    (when href? target)
                                                               :disabled?        disabled?]]
                                                      [v-box
                                                       :gap "15px"
                                                       :children [[title :level :level3 :label "Parameters"]
                                                                  (when @href?
                                                                    [v-box
                                                                     :children [[box :align :start :child [:code ":target"]]
                                                                                [radio-button
                                                                                 :label "_self - load link into same tab"
                                                                                 :value "_self"
                                                                                 :model @target
                                                                                 :on-change #(reset! target %)
                                                                                 :style {:margin-left "20px"}]
                                                                                [radio-button
                                                                                 :label "_blank - load link into new tab"
                                                                                 :value "_blank"
                                                                                 :model @target
                                                                                 :on-change #(reset! target %)
                                                                                 :style {:margin-left "20px"}]
                                                                                [checkbox
                                                                                 :label [:code ":disabled?"]
                                                                                 :model disabled?
                                                                                 :on-change (fn [val]
                                                                                              (reset! disabled? val))]]])]]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [hyperlink-href-demo])
