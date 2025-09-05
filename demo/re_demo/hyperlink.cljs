(ns re-demo.hyperlink
  (:require-macros
   [re-com.core    :refer []])
  (:require
   [re-com.core    :refer [at h-box v-box box gap line label title checkbox hyperlink p]]
   [re-com.buttons :refer [hyperlink-parts-desc hyperlink-args-desc]]
   [re-demo.utils  :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
   [re-com.util    :refer [px]]
   [reagent.core   :as    reagent]))

(defn hyperlink-demo
  []
  (let [disabled?   (reagent/atom false)
        click-count (reagent/atom 0)]
    (fn
      []
      [v-box
       :src      (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title  "[hyperlink ... ]"
                   "src/re_com/buttons.cljs"
                   "src/re_demo/hyperlink.cljs"]

                  [h-box
                   :src      (at)
                   :gap      "100px"
                   :children [[v-box
                               :src      (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A blue, clickable hyperlink to which you can attach a click handler."]
                                          [p "If you want to launch external URLs, use the [hyperlink-href] component."]
                                          [args-table hyperlink-args-desc]]]
                              [v-box
                               :src      (at)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [hyperlink
                                           :src              (at)
                                           :label            "Click me"
                                           :tooltip          "Click here to increase the click count"
                                           :tooltip-position :left-center
                                           :on-click         #(swap! click-count inc)
                                           :disabled?        disabled?]
                                          [label
                                           :src   (at)
                                           :label (str "click count = " @click-count)]
                                          [v-box
                                           :src      (at)
                                           :gap      "10px"
                                           :style    {:min-width        "150px"
                                                      :padding          "15px"
                                                      :border-top       "1px solid #DDD"
                                                      :background-color "#f7f7f7"}
                                           :children [[title
                                                       :src   (at)
                                                       :level :level3
                                                       :label "Interactive Parameters"
                                                       :style {:margin-top "0"}]
                                                      [checkbox
                                                       :src       (at)
                                                       :label     [:code ":disabled?"]
                                                       :model     disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]
                  [parts-table "hyperlink" hyperlink-parts-desc]]])))

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [hyperlink-demo])
