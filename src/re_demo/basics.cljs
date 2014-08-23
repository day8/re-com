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
  (let [popover-showing?  (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Buttons"]
                  [button
                   :label    "No Clicking!"
                   :on-click #(swap! state update-in [:outcome-index] inc)
                   :class    "btn-danger"]
                  [label
                   :label (nth click-outcomes (:outcome-index @state))
                   :style {:margin-left "15px"}]]])))


(defn combo-box-demo
  []
  (let [popover-showing?  (reagent/atom false)]
    (fn []
      [v-box
       :children [[title "Buttons"]
                  [:select#MwiSelect.form-control {:style {:display "inline" :width "auto"}}
                   nil
                   (for [o combo-options]
                     ^{:key o}
                     [:option nil o])]]])))

(defn inputs-demo
[]
(let [popover-showing?  (reagent/atom false)]
  (fn []
    [v-box
     :children [[:h3.page-header "Inputs"]
                 [:p]
                 [:p "Should show buttons and input fields in here"]
                 [:p "Perhaps typography"]
                 [:p "XXX Explain that bootstrap has to be included into the html"]]])))

(defn panel
  []
  [v-box
   :children [_[buttons-demo]
              #_[combo-box-demo]
              [inputs-demo]
              ]])



