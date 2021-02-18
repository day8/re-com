(ns re-demo.debug
  (:require-macros
    [re-com.debug      :refer [src-coordinates]])
  (:require
    [re-com.core       :refer [h-box v-box box gap line label title checkbox hyperlink p]]
    [re-com.datepicker :refer [datepicker]]
    [re-com.buttons    :refer [hyperlink-parts-desc hyperlink-args-desc]]
    [re-demo.utils     :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [re-com.util       :refer [px]]
    [reagent.core      :as    reagent]))

(defn debug-demo
  []
  (let [unknown-arg1?          (reagent/atom false)
        unknown-arg2?          (reagent/atom false)
        missing-on-change?     (reagent/atom false)
        boolean-selectable-fn? (reagent/atom false)
        unknown-part?          (reagent/atom false)]
    (fn
      []
      [v-box
       :src      (src-coordinates)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title  "Debugging"
                   "src/re_com/debug.cljs"
                   "src/re_demo/debug.cljs"]

                  [h-box
                   :src      (src-coordinates)
                   :gap      "100px"
                   :children [[v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :width    "450px"
                               :children [[p "All re-com components validate arguments. When there is a validation failure the component will render as a red box."]
                                          [p "To see examples of validation failure logs open Chrome DevTools and click options on the right."]]]
                              [v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          (cond->
                                            [datepicker
                                             :src (src-coordinates)]
                                            (not @missing-on-change?)
                                            (into [:on-change #()])
                                            @unknown-arg1?
                                            (into [:arg1-bogus-name :bogus])
                                            @unknown-arg2?
                                            (into [:arg2-bogus-name :bogus])
                                            @boolean-selectable-fn?
                                            (into [:selectable-fn true])
                                            @unknown-part?
                                            (into [:parts {:bogus-part-name {:style {:border "1px solid #ccc"}}}]))


                                          #_[hyperlink
                                             :src              (src-coordinates)
                                             :label            "Click me"
                                             :tooltip          "Click here to increase the click count"
                                             :tooltip-position :left-center
                                             :on-click         #(swap! click-count inc)
                                             :disabled?        disabled?]

                                          [v-box
                                           :src      (src-coordinates)
                                           :gap      "10px"
                                           :style    {:min-width        "150px"
                                                      :padding          "15px"
                                                      :border-top       "1px solid #DDD"
                                                      :background-color "#f7f7f7"}
                                           :children [[title
                                                       :src   (src-coordinates)
                                                       :level :level3
                                                       :label "Interactive Parameters"
                                                       :style {:margin-top "0"}]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Provide " [:code ":unknown-arg1?"]]
                                                       :model     unknown-arg1?
                                                       :on-change (fn [val]
                                                                    (reset! unknown-arg1? val))]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Provide " [:code ":unknown-arg2?"]]
                                                       :model     unknown-arg2?
                                                       :on-change (fn [val]
                                                                    (reset! unknown-arg2? val))]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Do not provide required " [:code ":on-change"]]
                                                       :model     missing-on-change?
                                                       :on-change (fn [val]
                                                                    (reset! missing-on-change? val))]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Provide boolean as " [:code ":selectable-fn"]]
                                                       :model     boolean-selectable-fn?
                                                       :on-change (fn [val]
                                                                    (reset! boolean-selectable-fn? val))]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Unknown part in " [:code ":parts"]]
                                                       :model     unknown-part?
                                                       :on-change (fn [val]
                                                                    (reset! unknown-part? val))]]]]]]]]])))



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [debug-demo])
