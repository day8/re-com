(ns re-demo.debug
  (:require-macros
    [re-com.core       :refer [src-coordinates]])
  (:require
    [re-com.core       :refer [h-box v-box box gap line label title checkbox hyperlink p]]
    [re-com.debug      :refer [component-stack-spy]]
    [re-com.datepicker :refer [datepicker]]
    [reagent.core      :as    reagent]
    [re-demo.utils     :refer [panel-title title2]]))

(defn debug-demo
  []
  (let [bogus-param-name?      (reagent/atom false)
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
                               :children [[title2 [:span "The " [:code ":src"] " Parameter"]]
                                          [line]
                                          [p "All re-com components accept a " [:code ":src"] " parameter which provides the source code coordinates of your usage. "
                                           "The value must be a map with two keys " [:code ":file"] " and " [:code ":line"] "."]
                                          [p "re-com reflects these coordinates back to you when reporting errors and showing component stacks, and this can greatly improve the debugging experience. "]
                                          [p "re-com also supplies the companion macro " [:code "src-coordinates"] " which returns a correctly populated map."
                                           " The two should be combined like this:"]
                                          [:pre
                                           "[button\n  :src   (src-coordinates)    ;; <-- here\n  :label \"click me\"\n  ...]"]
                                          [p "To use this macro, your " [:code "ns"] "will need to refer it as follows:"]
                                          [:pre
                                           "(ns my.app\n  (:require-macros\n    [re-com.core :refer [src-coordinates]])  ;; <-- here\n  (:require\n    [re-com.core :refer [h-box v-box ...]])"]
                                          [p "But wait, there's more. When " [:code ":src"] "is provided, re-com will decorate the DOM node representing a component with a " [:code "data-rc-src"] "  attribute containing the source code coordinates provided. "]
                                          [p "As a result, there's a direct link between DOM and the code which created it, which is particularly useful when you are understanding the structure of an unfamiliar codebase (including your own, after an absence)."]
                                          [p "This feature is sufficiently useful that we recommend you leave your re-com code permanently instrumented with " [:code ":src"] ". Every single component, all the time. In production builds the macro returns " [:code "nil"] " eliding any overhead."]
                                          [title2 "Project Closure Defines"]
                                          [line]
                                          [p "[IJ] TODO"]
                                          [:code "re-com.config/root-url-for-compiler-output"]]]
                              [v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :children [[title2 "Parameter Errors"]
                                          [line]
                                          [p "All re-com components validate their parameters (in debug builds)."]
                                          [p "If they detect a mistake, they render themselves as a red, placeholder box and report details of the error to the devtools console."]

                                          
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
                                                       :label "Simulate Parameter Errors"
                                                       :style {:margin-top "0"}]
                                                      [p "This demo lets you to simulate making parameter errors with the "
                                                       [:code "[datepicker ...]"] "component below and to then observe the outcomes."]
                                                      [checkbox
                                                       :src (src-coordinates)
                                                       :label [:span "Provide " [:code ":bogus-param-name"]]
                                                       :model bogus-param-name?
                                                       :on-change (fn [val]
                                                                    (reset! bogus-param-name? val))]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Do not provide the required " [:code ":on-change"]]
                                                       :model     missing-on-change?
                                                       :on-change (fn [val]
                                                                    (reset! missing-on-change? val))]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Provide a bad value (a boolean for " [:code ":selectable-fn"] ")"]
                                                       :model     boolean-selectable-fn?
                                                       :on-change (fn [val]
                                                                    (reset! boolean-selectable-fn? val))]
                                                      [checkbox
                                                       :src       (src-coordinates)
                                                       :label     [:span "Provide an unknown id in " [:code ":parts"]]
                                                       :model     unknown-part?
                                                       :on-change (fn [val]
                                                                    (reset! unknown-part? val))]
                                                      [p "Pay attention to the \"component stack\" section output to devtools console. mouseover the components in this section, and notice how devtools highlights them in the running app."]]]
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
