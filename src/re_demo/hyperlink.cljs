(ns re-demo.hyperlink
  (:require [re-com.core      :refer [h-box v-box box gap line label checkbox hyperlink]]
            [re-com.buttons   :refer [hyperlink-args-desc]]
            ;[re-com.text      :refer [label]]
            ;[re-com.misc      :refer [checkbox]]
            ;[re-com.buttons   :refer [hyperlink hyperlink-args-desc]]
            ;[re-com.box       :refer [h-box v-box box gap line]]
            [re-demo.utils    :refer [panel-title component-title args-table github-hyperlink status-text]]
            [reagent.core     :as    reagent]))


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
                                [github-hyperlink "Page Source"      "src/re_demo/hyperlink.cljs"]
                                [status-text "Beta"]]]

                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [:span "The hyperlink is used to..."]
                                          [args-table hyperlink-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [h-box
                                           :gap "30px"
                                           :children [[box
                                                       :width "200px"
                                                       :align :start
                                                       :child [hyperlink
                                                               :label     (if @disabled? "Now disabled" "Call back")
                                                               :tooltip   "Click here to increase the click count"
                                                               :on-click  #(swap! click-count inc)
                                                               :disabled? disabled?]]
                                                      [v-box
                                                       :gap "15px"
                                                       :children [[label :label (str "click count = " @click-count)]
                                                                  [label :label "parameters:"]
                                                                  [checkbox
                                                                   :label ":disabled?"
                                                                   :model disabled?
                                                                   :on-change (fn [val]
                                                                                (reset! disabled? val))]]]]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [hyperlink-demo])
