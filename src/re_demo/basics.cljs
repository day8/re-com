(ns re-demo.basics
  (:require [re-com.core    :refer  [input-text button label spinner progress-bar checkbox radio-button title]]
            [re-com.box     :refer  [h-box v-box box gap line]]
            [reagent.core   :as     reagent]))


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
                             :on-click #(swap! state update-in [:outcome-index] inc)
                             :class    "btn-danger"]
                            [box
                             :align :center      ;; note: centered text wrt the button
                             :child  [label
                                      :label (nth click-outcomes (:outcome-index @state))
                                      :style {:margin-left "15px"}]]]]

                [gap :size "20px"]
                [h-box             ;; I had to put the button in an h-box or else it streached out horizontally
                 :gap "50px"
                 :children [[button
                             :label    (if (:see-spinner @state)  "Stop it!" "See Spinner")
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
                   :children [(doall (for [c ["red" "green" "blue"]]       ;; Notice the ugly "doall"
                                       ^{:key c}                 ;; key should be unique within this compenent
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
        change-on-blur? (reagent/atom false)
        ]
    (fn
      []
      [v-box
       :gap      "15px"
       :children [[title :label "[input-text ... ]"]
                  [gap :size "0px"]                         ;; Double the 15px gap from the parent v-box
                  [h-box
                   :gap      "20px"

                   :children [[input-text
                               :model           text-val
                               :placeholder     "placeholder message"
                               :on-change       #(reset! text-val %)
                               :change-on-blur? change-on-blur?
                               :disabled?       disabled?]

                              [v-box
                               :gap "15px"
                               :children [[label :label (str ":model is currently: '" (if @text-val @text-val "nil") "'")]
                                          [checkbox
                                            :label     ":change-on-blur? (when should on-change be called?  On each key press OR on-blur)"
                                            :model     change-on-blur?
                                            :on-change (fn [val]
                                                         (reset! change-on-blur? val))]
                                           [checkbox
                                            :label     ":disabled?"
                                            :model     disabled?
                                            :on-change (fn [val]
                                                         (reset! disabled? val))]
                                           [button
                                            :label    "Set :model to 'blah'"
                                            :on-click #(reset! text-val "blah")]
                                           ]]]]]])))

(defn panel
  []
  [v-box
   :gap      "30px"
   :children [[buttons-demo]
              [checkboxes-demo]
              [radios-demo]
              [inputs-demo]
              [gap :size "50px"]]])
