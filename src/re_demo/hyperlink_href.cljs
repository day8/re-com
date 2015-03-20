(ns re-demo.hyperlink-href
  (:require [re-com.core    :refer [h-box v-box box gap line label title radio-button hyperlink-href]]
            [re-com.buttons :refer [hyperlink-href-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core   :as    reagent]))


(defn hyperlink-href-demo
  []
  (let [target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[hyperlink-href ... ]"
                                [github-hyperlink "Component Source" "src/re_com/buttons.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/hyperlink_href.cljs"]]]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "A blue, clickable hyperlink which launches external URLs."]
                                           [:p "If you want a hyperlink with a click handler, use the [hyperlink] component."]]                                          [args-table hyperlink-href-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [h-box
                                           :gap "40px"
                                           :children [[box
                                                       :width "200px"
                                                       :align :start
                                                       :child [hyperlink-href
                                                               :label     "Launch Google"
                                                               :tooltip   "You're about to launch Google"
                                                               :href      (when href? "http://google.com")
                                                               :target    (when href? target)]]
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
                                                                                 :on-change #(reset! target "_self")
                                                                                 :style {:margin-left "20px"}]
                                                                                [radio-button
                                                                                 :label "_blank - load link into new tab"
                                                                                 :value "_blank"
                                                                                 :model @target
                                                                                 :on-change #(reset! target "_blank")
                                                                                 :style {:margin-left "20px"}]]])]]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [hyperlink-href-demo])
