(ns re-demo.debug
  (:require-macros
    [re-com.core       :refer [src-coordinates]])
  (:require
    [re-com.core       :refer [h-box v-box box gap line label title checkbox hyperlink p]]
    [re-com.datepicker :refer [datepicker]]
    [re-com.debug      :refer [component-stack-spy]]
    [re-com.buttons    :refer [hyperlink-parts-desc hyperlink-args-desc]]
    [re-demo.utils     :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [re-com.util       :refer [px]]
    [reagent.core      :as    reagent]))

(defn debug-demo
  []
  (let [bogus-param-name?        (reagent/atom false)
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
                   :children [[component-stack-spy
                               :child [v-box
                                       :src      (src-coordinates)
                                       :gap      "10px"
                                       :width    "450px"
                                       :children [[title2 ":src"]
                                                  [line]
                                                  [p "All re-com components optionally accept an " [:code ":src"] "parameter which can assist with debugging at development time."]
                                                  [p "re-com also supplies a companion macro " [:code "re-com.core/src-coordinates"] "which captures source code coordinates at compile time."]
                                                  [p "The two are normally combined in the following way:"]
                                                  [:pre
                                                   "[datepicker\n  :src (re-com.core/src-coordinates)\n  ...]"]
                                                  [p "To get access to the macro you can refer it in the following way:"]
                                                  [:pre
                                                   "(ns my-app\n  (:require-macros [re-com.core :refer [src-coordinates]]))"]
                                                  [p "We strongly recommend that all your re-com code is permanently instrumented with " [:code ":src"] ". Production builds will elide this overhead."]
                                                  [p "When " [:code ":src"] "is supplied, all DOM nodes are annotated with two data attributes " [:code "data-rc-src"] " and " [:code "data-rc-component-name"] ". This is especially useful when you are understanding the structure of an unfamiliar codebase."]
                                                  [p "For a demonstration of this feature, right click and inspect the datepicker in the column to the right."]
                                                  [title2 "Closure Defines"]
                                                  [line]
                                                  [p "[IJ] TODO"]
                                                  [:code "re-com.config/root-url-for-compiler-output"]]]]

                              [v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :children [[title2 "Parameters Errors"]
                                          [line]
                                          [p "All re-com components validate their parameters."]
                                          [p "When you supply incorrect parameters to a component, re-com will render
                                              the component as a red box and log details to the DevTools console."]
                                          [p "This demo allows you to make certain kinds of mistakes with a "
                                           [:code "[datepicker ...]"] "component and to observe the output in DevTools console."]
                                          [p "Pay particular attention to the \"component stack\" section of the output. As you mouse over the components in this section, DevTools will highlight them in the running app."]
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
                                                       :label "Simulate Mistakes"
                                                       :style {:margin-top "0"}]
                                                      [checkbox
                                                       :src (src-coordinates)
                                                       :label [:span "Provide " [:code ":bogus-param-name"]]
                                                       :model bogus-param-name?
                                                       :on-change (fn [val]
                                                                    (reset! bogus-param-name? val))]
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
                                                                    (reset! unknown-part? val))]]]
                                          (cond->
                                            [datepicker
                                             :src (src-coordinates)]
                                            (not @missing-on-change?)
                                            (into [:on-change #()])
                                            @bogus-param-name?
                                            (into [:bogus-arg-name :bogus])
                                            @boolean-selectable-fn?
                                            (into [:selectable-fn true])
                                            @unknown-part?
                                            (into [:parts {:bogus-part-name {:style {:border "1px solid #ccc"}}}]))]]]]]])))




;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [debug-demo])
