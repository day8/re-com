(ns re-demo.basics
  (:require [re-demo.util   :refer  [title]]
            [re-com.core    :refer  [button label spinner progress-bar ]]
            [re-com.box     :refer  [h-box v-box box gap line]]
            [reagent.core   :as     reagent]))


(def click-outcomes
  [""   ;; start blank
   "Neuclear warhead launched."
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
                             :align :center   ;; Cnote: centered text wrt the button
                             :child  [label
                                      :label (nth click-outcomes (:outcome-index @state))
                                      :style {:margin-left "15px"}]]]]]]))


(defn combo-box-demo
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
              [gap "20px"]
              [combo-box-demo]
              [gap "20px"]
              [inputs-demo]]])



