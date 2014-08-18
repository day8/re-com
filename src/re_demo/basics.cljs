(ns re-demo.basics
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button
                                              label
                                              spinner
                                              progress-bar
                                              gap]]
            [reagent.core             :as    reagent]))


(def click-outcomes
  [""   ;; start blank
   "Neuclear warhead launched."
   "Oops. Priceless Ming Vase smashed!!"
   "Diamonds accidentally flushed."
   "" "" "" "" "" ""
   "Now it's real. Once more & you'll get a page-freezing exception."])


(def combo-options
  ["Do this"
   "Do that"
   "Do somthing else"])



(def state (reagent/atom
            {:outcome-index 0
             :see-spinner  false
             }))


(defn panel
  []
  [:div
   [:h3.page-header "Buttons"]

   [:div                                                    ;;.row
    [:div                                                   ;;.col-md-6
     [button
      :label    "No Clicking!"
      :on-click #(swap! state update-in [:outcome-index] inc)
      :class    "btn-danger"]
     [label
      :label (nth click-outcomes (:outcome-index @state))
      :style {:margin-left "15px"}]]]

   [gap :height 20]

   [:div                                                    ;;.row
    [:div                                                   ;;.col-md-4
     [button
      :label    (if (:see-spinner @state)  "Stop it!" "See Spinner")
      :on-click #(swap! state update-in [:see-spinner] not)]
     (when (:see-spinner @state)
       [spinner])]]


   [:h3.page-header "Combobox"]

   ;; [combobox ]

   [:select#MwiSelect.form-control {:style {:display "inline" :width "auto"}}
    nil
    (for [o combo-options]
      ^{:key o}
      [:option nil o])]

   [:h3.page-header "Inputs"]

   [:p]
   [:p "Should show buttons and input fields in here"]
   [:p "Perhaps typography"]
   [:p "XXX Explain that bootstrap has to be included into the html"]
   ])

