(ns re-demo.basics
  (:require [re-com.core  :refer [input-text button hyperlink label spinner progress-bar checkbox radio-button title slider]]
            [re-com.box   :refer [h-box v-box box gap line]]
            [reagent.core :as    reagent]))


(def click-outcomes
  [""   ;; start blank
   "Nuclear warhead launched."
   "Oops. Priceless Ming Vase smashed!!"
   "Diamonds accidentally flushed."
   "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""
   "Now it's real. Once more & you'll get a page-freezing exception."])


(def state (reagent/atom
             {:outcome-index 0
              :see-spinner  false}))


(defn buttons-demo
  []
  (fn
    []
    [v-box
     :children [[title :label "[button ... ]"]
                [gap :size "20px"]
                [h-box
                 :children [[button
                             :label    "No Clicking!"
                             ;:disabled? true
                             :on-click #(swap! state update-in [:outcome-index] inc)
                             :class    "btn-danger"]
                            [box
                             :align :center      ;; note: centered text wrt the button
                             :child  [label
                                      :label (nth click-outcomes (:outcome-index @state))
                                      :style {:margin-left "15px"}]]]]

                [gap :size "20px"]
                [h-box
                 :gap "50px"
                 :children [[button
                             :label    (if (:see-spinner @state)  "Stop it!" "See Spinner")
                             ;:disabled? true
                             :on-click #(swap! state update-in [:see-spinner] not)]
                            (when (:see-spinner @state)  [spinner])]]]]))


(defn right-arrow
  []
  [:svg
   {:height 20  :width 25  :style {:display "flex" :align-self "center"} }
   [:line {:x1 "0" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "20,6 20,14 25,10" :style {:stroke "#888" :fill "#888"}}]])


(defn left-arrow
  []
  [:svg
   {:height 20  :width 25  :style {:display "flex" :align-self "center"} }
   [:line {:x1 "5" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "5,6 5,14 0,10" :style {:stroke "#888" :fill "#888"}}]])


(defn checkboxes-demo
  []
  (let [always-false (reagent/atom false)
        disabled?    (reagent/atom false)
        ticked?      (reagent/atom false)
        something1?  (reagent/atom false)
        something2?  (reagent/atom true)
        all-for-one? (reagent/atom true)]
    (fn
      []
      [v-box
       :gap "15px"
       :children [[title :label "[checkbox ... ]"]
                  [gap :size "0px"]                         ;; Double the 15px gap from the parent v-box
                  [checkbox
                   :label "always ticked (state stays true when you click)"
                   :model (= 1 1)]    ;; true means always ticked

                  [checkbox
                   :label "untickable (state stays false when you click)"
                   :model always-false]

                  [h-box
                   :gap "10px"
                   :children [[checkbox
                               :label     "tick me  "
                               :model     ticked?
                               :on-change #(reset! ticked? %)]
                              (when @ticked? [left-arrow])
                              (when @ticked? [label :label " is ticked"])]]

                  [h-box
                   :gap "1px"
                   :children [[checkbox  :model all-for-one?   :on-change #(reset! all-for-one? %)]
                              [checkbox  :model all-for-one?   :on-change #(reset! all-for-one? %)]
                              [checkbox  :model all-for-one?   :on-change #(reset! all-for-one? %)  :label  "all for one, and one for all.  "]]]

                  [h-box
                   :gap "15px"
                   :children [[checkbox
                               :label     "when you tick this one, this other one is \"disabled\""
                               :model     disabled?
                               :on-change #(reset! disabled? %)]
                              [right-arrow]
                              [checkbox
                               :label       (if @disabled? "now disabled" "enabled")
                               :model       something1?
                               :disabled?   disabled?
                               :label-style (if @disabled?  {:color "#888"})
                               :on-change   #(reset! something1? %)]]]

                  [h-box
                   :gap "1px"
                   :children [[checkbox
                               :model     something2?
                               :on-change #(reset! something2? %)]
                              [gap :width "50px"]
                              [left-arrow]
                              [gap :width "5px"]
                              [label
                               :label "no label on this one"]]]]])))


(defn radios-demo
  []
  (let [colour (reagent/atom "green")]
    (fn
      []
      [v-box
       :gap "15px"
       :children [[title :label "[radio-button ... ]"]
                  [gap :size "0px"]                         ;; Double the 15px gap from the parent v-box
                  [v-box
                   :children [(doall (for [c ["red" "green" "blue"]]    ;; Notice the ugly "doall"
                                       ^{:key c}                        ;; key should be unique within this compenent
                                       [radio-button
                                        :label       c
                                        :value       c
                                        :model       colour
                                        :label-style (if (= c @colour) {:background-color c  :color "white"})
                                        :on-change   #(reset! colour c)]))]]]])))


(defn inputs-demo
  []
  (let [text-val        (reagent/atom nil)
        disabled?       (reagent/atom false)
        change-on-blur? (reagent/atom true)]
    (fn
      []
      [v-box
       :gap      "15px"
       :children [[title :label "[input-text ... ]"]
                  [gap :size "0px"]
                  [h-box
                   :gap      "40px"
                   :children [[input-text
                               :model           text-val
                               :width           "200px"
                               :placeholder     "placeholder message"
                               :on-change       #(reset! text-val %)
                               :change-on-blur? change-on-blur?
                               :disabled?       disabled?]
                              [v-box
                               :gap      "15px"
                               :children [[label
                                           :label (str "external :model is currently: '" (if @text-val @text-val "nil") "'")
                                           :style {:margin-top "8px"}]
                                          [label :label "parameters:"]
                                          [v-box
                                           :children [[label :label ":change-on-blur?"]
                                                      [radio-button
                                                       :label     "false - Call on-change on every keystroke"
                                                       :value     false
                                                       :model     @change-on-blur?
                                                       :on-change #(reset! change-on-blur? false)
                                                       :style     {:margin-left "20px"}]
                                                      [radio-button
                                                       :label     "true - Call on-change only on blur or Enter key (Esc key resets text)"
                                                       :value     true
                                                       :model     @change-on-blur?
                                                       :on-change #(reset! change-on-blur? true)
                                                       :style     {:margin-left "20px"}]]]
                                          [checkbox
                                           :label     ":disabled?"
                                           :model     disabled?
                                           :on-change (fn [val]
                                                        (reset! disabled? val))]
                                          [button
                                           :label    "Set external model to 'blah'"
                                           :on-click #(reset! text-val "blah")]]]]]]])))


(defn hyperlink-demo
  []
  (let [disabled? (reagent/atom false)
        target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [v-box
       :gap "15px"
       :children [[title :label "[hyperlink ... ]"]
                  [gap :size "0px"]
                  [h-box
                   :gap "40px"
                   :children [[box
                               :width "200px"
                               :child [hyperlink
                                       :label     (if @disabled? "now disabled" (if @href? "Launch Google" "Call back"))
                                       :href      (when href? "http://google.com")
                                       :target    (when href? target)
                                       :on-click  #(when-not href? println "CLICKED!")
                                       :disabled? disabled?]]
                              [v-box
                               :gap "15px"
                               :children [[label :label "parameters:"]
                                          [v-box
                                           :children [[label :label "When clicked:"]
                                                      [radio-button
                                                       :label "href - load a URL"
                                                       :value true
                                                       :model @href?
                                                       :on-change #(reset! href? true)
                                                       :style {:margin-left "20px"}]
                                                      [radio-button
                                                       :label "on-click - call back"
                                                       :value false
                                                       :model @href?
                                                       :on-change #(reset! href? false)
                                                       :style {:margin-left "20px"}]]]
                                          (when @href?
                                            [v-box
                                             :children [[label :label ":target"]
                                                        [radio-button
                                                         :label "_self - load link into same tab"
                                                         :value "_self"
                                                         :model @target
                                                         :on-change #(reset! target "_self")
                                                         :style {:margin-left "20px"}]
                                                        [radio-button
                                                         :label "_blank - load link inot new tab"
                                                         :value "_blank"
                                                         :model @target
                                                         :on-change #(reset! target "_blank")
                                                         :style {:margin-left "20px"}]]])
                                          [checkbox
                                           :label ":disabled?"
                                           :model disabled?
                                           :on-change (fn [val]
                                                        (reset! disabled? val))]]]]]]])))


(defn slider-demo
  []
  (let [slider-val (reagent/atom 0)
        slider-min (reagent/atom 0)
        slider-max (reagent/atom 100)
        disabled?  (reagent/atom false)]
    (fn
      []
      [v-box
       :gap "15px"
       :children [[title :label "[slider ... ]"]
                  [gap :size "0px"]
                  [h-box
                   :gap "40px"
                   :children [[v-box
                               :gap      "10px"
                               :children [[slider
                                           :model     slider-val
                                           :min       slider-min
                                           :max       slider-max
                                           :width     "200px"
                                           :on-change #(reset! slider-val %)
                                           :disabled? disabled?]]]
                              [v-box
                               :gap      "15px"
                               :children [[label :label "parameters:"]
                                          [h-box
                                           :gap      "10px"
                                           :align    :center
                                           :children [[label
                                                       :label ":model"
                                                       :style {:width "60px"}]
                                                      [input-text
                                                       :model           slider-val
                                                       :width           "70px"
                                                       :height          "26px"
                                                       :on-change       #(reset! slider-val %)
                                                       :change-on-blur? false]]]
                                          [h-box
                                           :gap      "10px"
                                           :align    :center
                                           :children [[label
                                                       :label ":min"
                                                       :style {:width "60px"}]
                                                      [input-text
                                                       :model           slider-min
                                                       :width           "70px"
                                                       :height          "26px"
                                                       :on-change       #(reset! slider-min %)
                                                       :change-on-blur? false]]]
                                          [h-box
                                           :gap      "10px"
                                           :align    :center
                                           :children [[label
                                                       :label ":max"
                                                       :style {:width "60px"}]
                                                      [input-text
                                                       :model           slider-max
                                                       :width           "70px"
                                                       :height          "26px"
                                                       :on-change       #(reset! slider-max %)
                                                       :change-on-blur? false]]]
                                          [checkbox
                                           :label ":disabled?"
                                           :model disabled?
                                           :on-change (fn [val]
                                                        (reset! disabled? val))]]]]]]])))


(defn panel
  []
  [v-box
   :gap      "30px"
   :children [[buttons-demo]
              [checkboxes-demo]
              [radios-demo]
              [inputs-demo]
              [hyperlink-demo]
              [slider-demo]
              [gap :size "100px"]]])
