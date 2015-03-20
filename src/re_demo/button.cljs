(ns re-demo.button
  (:require [re-com.core    :refer [h-box v-box box gap line button label spinner]]
            [re-com.buttons :refer [button-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table github-hyperlink status-text]]
            [reagent.core   :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-spinner  false}))

(def click-outcomes
  [""   ;; start blank
   "Nuclear warhead launched."
   "Oops. Priceless Ming Vase smashed!!"
   "Diamonds accidentally flushed."
   "Toy disabled"])


(defn button-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[button ... ]"
                            [github-hyperlink "Component Source" "src/re_com/buttons.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/button.cljs"]]]

              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[status-text "Alpha"]
                                      [component-title "Notes"]
                                      [:span "The button is used to..."]
                                      [args-table button-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [v-box
                                       :children [[h-box
                                                   :children [[button
                                                               :label            "No Clicking!"
                                                               :tooltip          "Seriously, NO CLICKING!"
                                                               :tooltip-position :below-center
                                                               :disabled?         (= (:outcome-index @state) (dec (count click-outcomes)))
                                                               :on-click          #(swap! state update-in [:outcome-index] inc)
                                                               :class             "btn-danger"]
                                                              [box
                                                               :align :center      ;; note: centered text wrt the button
                                                               :child  [label
                                                                        :label (nth click-outcomes (:outcome-index @state))
                                                                        :style {:margin-left "15px"}]]]]
                                                  [gap :size "40px"]
                                                  [h-box
                                                   :height   "50px"
                                                   :gap      "50px"
                                                   :align    :center
                                                   :children [[button
                                                               :label             (if (:see-spinner @state)  "Stop it!" "See Spinner")
                                                               :tooltip           "I'm a tooltip on the left"
                                                               :tooltip-position :left-center
                                                               :on-click          #(swap! state update-in [:see-spinner] not)]
                                                              (when (:see-spinner @state)  [spinner])]]]]]]]]]])


;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [button-demo])
