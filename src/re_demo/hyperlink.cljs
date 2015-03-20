(ns re-demo.hyperlink
  (:require [re-com.core    :refer [h-box v-box box gap line label title checkbox hyperlink]]
            [re-com.buttons :refer [hyperlink-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
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
       :children [[panel-title [:span "[hyperlink ... ]"
                                [github-hyperlink "Component Source" "src/re_com/buttons.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/hyperlink.cljs"]]]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "A blue, clickable hyperlink to which you can attach a click handler."]
                                           [:p "If you want to launch external URLs, use the [hyperlink-href] component."]]
                                          [args-table hyperlink-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
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
