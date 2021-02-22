(ns re-demo.debug
  (:require-macros
    [re-com.core       :refer [src-coordinates]])
  (:require
    [re-com.core       :refer [h-box v-box box gap line  title checkbox p]]
    [re-com.datepicker :refer [datepicker]]
    [reagent.core      :as    reagent]
    [re-demo.utils     :refer [panel-title title2]]))




(defn params-validation-column
  []  
  (let [bogus-param-name?      (reagent/atom false)
        missing-on-change?     (reagent/atom false)
        boolean-selectable-fn? (reagent/atom false)
        unknown-part?          (reagent/atom false)]
    (fn []
      [v-box
       :children [[title2 "Parameter Validation"]
                  [line]
                  [gap :size "10px"]

                  [p "Now, our sausage fingers sometimes type " [:code "onmouseover"] " instead of " [:code "on-mouse-over"] ", "
                   " or " [:code "centre"] " rather than " [:code "center"] ", and sometimes we pass in a string where a keyword is required."]
                  [p "re-com catches these errors early by validating both parameter names and values."]


                  [p "If a components detects a mistake in its parameters, it will render itself as a red, "
                   "placeholder box and report details of the error to the DevTools console."]

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
                              [p "Pay attention to the \"component stack\" section output to DevTools console. mouseover the components in this section, and notice how DevTools highlights them in the running app."]]]
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
                    (into [:parts {:bogus-part-name {:style {:border "1px solid #ccc"}}}]))
                  ]])))


(defn the-src-parameter-column
  []
  [v-box
   :src      (src-coordinates)
   :children [[title2 [:span "The " [:code ":src"] " Parameter"]]
              [line]
              [gap :size "10px"]
              [p "All re-com components accept a " [:code ":src"] " parameter through which you can provide the source code coordinates of your usage. "
               "The value must be a map with two keys " [:code ":file"] " and " [:code ":line"] "."]
              [p "re-com reflects these coordinates back to you when reporting errors and showing component stacks, and this will greatly improve your debugging experience. "]
              [p "re-com also supplies the companion macro " [:code "src-coordinates"] " which returns a correctly populated map."
               " The two should be combined like this:"]
              [:pre
               "[button\n  :src   (src-coordinates)    ;; <-- note\n  :label \"click me\"\n  ...]"]
              [p "To use it, your " [:code "ns"] " will need to  " [:code ":require-macros"] " it as follows:"]
              [:pre
               "(ns my.app\n  (:require-macros\n    [re-com.core :refer [src-coordinates]])  ;; <-- note\n  (:require\n    [re-com.core :refer [h-box v-box ...]])"]
              [p "But wait, you get more. "]
              [p "When " [:code ":src"] " is provided, re-com will add a \"data\" attribute to the DOM "
               "node representing a component. This attribute, called " [:code "data-rc-src"] ",  will contain any source code coordinates provided. "]
              [p "This links any DOM node you inspect in DevTool's \"Elements\" tab to the code which created it. "
               "When you are exploring an unfamiliar codebase (including your own, after an absence) this is invaluable. "
               "Just right-click and \"Inspect\" on any part of the apps UI and you can instantly see the coordinates for the underlying code."]
              [p [:b [:i "This feature is sufficiently useful that we recommend you leave your re-com code permanently instrumented with " [:code ":src"]]] ". Every single component, all the time. In production builds, the macro returns " [:code "nil"] " eliding any overhead. "]
              [p "And, if you have a legacy codebase, which does not yet use " [:code ":src"]
               ", using search/replace will get you a long way. "
               "For example, do a global search/replace of " [:code "[h-box"] " for " [:code "[h-box :src (src-coordinates)"]
               " and then do the same of " [:code " v-box"] " etc. You'll still need to work on the ns to require the macro. "
               "Just a thought."]]])


(defn stack-spy-column
  []
  [v-box
   :src      (src-coordinates)
   :children [[title2 [:span [:code "stack-spy"]]]
              [line]
              [gap :size "10px"]
              [p [:code "h-box"] " and " [:code "v-box"] " are usually simple to use. 
                 But, in deeply nested structures, where the component is an \"elastic\" table, it can sometimes
                 be a chore to work out what part of a hierarchy is driving height and width.
                 Is a certain child driving the width of a parent, or the other way around? Or is it the grandparent?"]
              [p "To work it out, it is very useful to gather together, in one place, the hierarchy of size/heights/widths etc
                  from the component right through to the root. Your review then becomes easy because you don't need jump
                  around different parts of the codebase, as you work your way up the component stack. "]
              [p [:code "stack-spy"] " is built to help in exactly this situation. You wrap it around a single component
                 and it will dump a detailed component stack report to DevTools console."]
              [p "Use it like this:"]
              [:pre
               "(ns my.app\n  (:require\n    [re-com.debug :refer [stack-spy]])"
               "\n\n"
               "[stack-spy\n  :component [simple-v-table ...]]"]]])

(defn debug-demo
  []
  [v-box
   :src      (src-coordinates)
   :gap      "10px"
   :children [[panel-title  
               "Debugging"
               "src/re_com/debug.cljs"
               "src/re_demo/debug.cljs"]

              [h-box
               :src      (src-coordinates)
               :gap      "100px"
               :children [[the-src-parameter-column]
                          [params-validation-column]
                          [stack-spy-column]]]]])




;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [debug-demo])
