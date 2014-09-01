(ns re-demo.basics
  (:require [re-demo.util   :refer  [title]]
            [re-com.core    :refer  [button label spinner progress-bar checkbox ]]
            [re-com.box     :refer  [h-box v-box box gap line]]
            [reagent.core   :as     reagent]))


(def click-outcomes
  [""   ;; start blank
   "Nuclear warhead launched."
   "Oops. Priceless Ming Vase smashed!!"
   "Diamonds accidentally flushed."
   "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" ""
   "Now it's real. Once more & you'll get a page-freezing exception."])


(def combo-options
  ["Do this"
   "Do that"
   "Do somthing else"])



(def state (reagent/atom
             {:outcome-index 0
              :see-spinner  false}))


(defn buttons-demo
  []
  (fn []
    [v-box
     :children [[title "Buttons"]
                [gap "20px"]
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

                [gap "20px"]
                [h-box             ;; I had to put the button in an h-box or else it streached out horizontally
                 :gap "50px"
                 :children [[button
                             :label    (if (:see-spinner @state)  "Stop it!" "See Spinner")
                             :on-click #(swap! state update-in [:see-spinner] not)]
                            (when (:see-spinner @state)  [spinner])]]]]))


#_(defn combo-box-demo
  []
  (fn []
    [v-box
     :children [[title "ComboBox"]
                [gap "20px"]
                [h-box
                 :children [[:select#MwiSelect.form-control {:style {:display "inline" :width "auto"}}
                             nil
                             (for [o combo-options]
                               ^{:key o}
                               [:option o])]]]]]))

(defn checkboxes-demo
  []
  (let [always-false (reagent/atom false)
        disabled?    (reagent/atom false)
        readonly?    (reagent/atom false)
        something1?   (reagent/atom false)
        something2?   (reagent/atom true)]

    (fn
      []
      [v-box
       :children [[title "Checkboxes"]
                                [checkbox
                                 :label "always ticked (state stays true when you click)"
                                 :model   (= 1 1)]    ;; always ticked

                                [checkbox
                                 :label "untickable (state stays false when you click)"
                                 :model   always-false
                                 ]
                                [h-box
                                 :gap "25px"
                                 :children [
                                             [checkbox
                                              :label "when ticked the one to the right is \"disabled\""
                                              :model  disabled?
                                              :on-change  #(reset! disabled? %)]
                                             [checkbox
                                              ;; :label "no label"
                                              :model  something1?
                                              :disabled disabled?
                                              :on-change  #(reset! something1? %)
                                              ;; :readonly false
                                              ;; :style class
                                              ;; :on-change  #(reset! always-true true)
                                              ]]]
                                [h-box
                                 :gap "25px"
                                 :children [
                                             [checkbox
                                              :label "when ticked the one to the right is \"readonly\" (not working)"
                                              :model  readonly?
                                              :on-change  #(reset! readonly? %)]
                                             [checkbox
                                              ;; :label "no label"
                                              :model  something2?
                                              :readonly readonly?
                                              :on-change  #(reset! something2? %)
                                              ;; :readonly false
                                              ;; :style class
                                              ;; :on-change  #(reset! always-true true)
                                              ]]]]])))


(defn inputs-demo
  []
  (fn []
    [v-box
     :children [[:h3.page-header "Inputs"]
                 [:p]
                 [:p "Should show buttons and input fields in here"]
                 [:p "Perhaps typography"]
                 [:p "XXX Explain that bootstrap has to be included into the html"]]]))

(defn panel
  []
  [v-box
   :children [[buttons-demo]
              [gap "30px"]
              [checkboxes-demo]
              [gap "30px"]
              [inputs-demo]]])



