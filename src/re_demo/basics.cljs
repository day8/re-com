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
  (let [always-true  (reagent/atom true)
        always-false (reagent/atom false)]
    [v-box
     :children [[title "Checkboxes"]
                [h-box
                 :children [
                             [h-box
                              :gap "50px"
                              :min-width "1200px"
                              :children [#_[v-box
                                         :size "450px"
                                         :children [[gap "20px"]
                                                    [:div.h4 "Notes:"]
                                                    [:ul
                                                     [:li "."]
                                                     [:li "."]
                                                     [:li "."]]]]

                                          [v-box
                                           :size "100%"
                                           :gap     "10px"
                                           :children [
                                                      [checkbox
                                                       :model   always-true    ;; always ticked
                                                       :disabled false
                                                       :readonly false
                                                       ;; :style class
                                                       :on-change  #(reset! always-true true )
                                                       :label "Always ticked"
                                                       ]
                                                      [checkbox
                                                       :model   false    ;; never ticked
                                                      ;; :style class
                                                      ;;:on-change
                                                       :label "Untickable"
                                                       ]
                                                      #_[checkbox
                                                       :model   true    ;; always ticked
                                                       :disabled false
                                                       :readonly false
                                                                        ;; :style class
                                                                        ;;:on-change
                                                       :label "Always ticked"
                                                       ]
                                                      ]]]]


                             ]]]]))


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



