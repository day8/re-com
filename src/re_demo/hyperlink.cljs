(ns re-demo.hyperlink
  (:require [re-com.core    :refer [h-box v-box box gap line label title checkbox hyperlink p]]
            [re-com.buttons :refer [hyperlink-args-desc]]
            [re-demo.utils  :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core   :as    reagent]))


(defn hyperlink-demo
  []
  (let [disabled?   (reagent/atom false)
        click-count (reagent/atom 0)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title  "[hyperlink ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/hyperlink.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A blue, clickable hyperlink to which you can attach a click handler."]
                                          [p "If you want to launch external URLs, use the [hyperlink-href] component."]
                                          [args-table hyperlink-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :gap "30px"
                                           :children [[v-box
                                                       :width    "200px"
                                                       :gap      "10px"
                                                       :align    :start
                                                       :children [[hyperlink
                                                                   :label            "Click me"
                                                                   :tooltip          "Click here to increase the click count"
                                                                   :tooltip-position :left-center
                                                                   :on-click         #(swap! click-count inc)
                                                                   :disabled?        disabled?]
                                                                  [label :label (str "click count = " @click-count)]]]
                                                      [v-box
                                                       :gap "15px"
                                                       :children [[title :level :level3 :label "Parameters"]
                                                                  [checkbox
                                                                   :label [:code ":disabled?"]
                                                                   :model disabled?
                                                                   :on-change (fn [val]
                                                                                (reset! disabled? val))]]]]]]]]]]])))



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [hyperlink-demo])
