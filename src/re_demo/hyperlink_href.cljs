(ns re-demo.hyperlink-href
  (:require-macros
    [re-com.debug   :refer [src-coordinates]])
  (:require
    [re-com.core    :refer [h-box v-box box gap line label title radio-button hyperlink-href p checkbox]]
    [re-com.buttons :refer [hyperlink-href-parts-desc hyperlink-href-args-desc]]
    [re-demo.utils  :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [re-com.util    :refer [px]]
    [reagent.core   :as    reagent]))

(defn hyperlink-href-demo
  []
  (let [disabled? (reagent/atom false)
        target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [v-box
       :src      (src-coordinates)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[hyperlink-href ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/hyperlink_href.cljs"]

                  [h-box
                   :src      (src-coordinates)
                   :gap      "100px"
                   :children [[v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A blue, clickable hyperlink which launches external URLs."]
                                          [p "If you want a hyperlink with a click handler, use the [hyperlink] component."]
                                          [args-table hyperlink-href-args-desc]]]
                              [v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [hyperlink-href
                                           :label     "Launch Google"
                                           :tooltip   "You're about to launch Google"
                                           :href      (when href? "http://google.com")
                                           :target    (when href? target)
                                           :disabled?        disabled?]
                                          [v-box
                                           :src      (src-coordinates)
                                           :gap "10px"
                                           :style    {:min-width        "150px"
                                                      :padding          "15px"
                                                      :border-top       "1px solid #DDD"
                                                      :background-color "#f7f7f7"}
                                           :children [[title
                                                       :src      (src-coordinates)
                                                       :level :level3 :label "Interactive Parameters" :style {:margin-top "0"}]
                                                      (when @href?
                                                        [v-box
                                                         :src      (src-coordinates)
                                                         :gap      "15px"
                                                         :children [[box
                                                                     :src      (src-coordinates)
                                                                     :align :start :child [:code ":target"]]
                                                                    [radio-button
                                                                     :src      (src-coordinates)
                                                                     :label "_self - load link into same tab"
                                                                     :value "_self"
                                                                     :model @target
                                                                     :on-change #(reset! target %)
                                                                     :style {:margin-left "20px"}]
                                                                    [radio-button
                                                                     :src      (src-coordinates)
                                                                     :label "_blank - load link into new tab"
                                                                     :value "_blank"
                                                                     :model @target
                                                                     :on-change #(reset! target %)
                                                                     :style {:margin-left "20px"}]
                                                                    [checkbox
                                                                     :src      (src-coordinates)
                                                                     :label [:code ":disabled?"]
                                                                     :model disabled?
                                                                     :on-change (fn [val]
                                                                                  (reset! disabled? val))]]])]]]]]]
                  [parts-table "hyperlink-href" hyperlink-href-parts-desc]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [hyperlink-href-demo])
