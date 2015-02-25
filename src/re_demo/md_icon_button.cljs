(ns re-demo.md-icon-button
  (:require [re-com.core                   :refer [label]]
            [re-com.buttons                :refer [md-icon-button md-icon-button-args-desc]]
            [re-demo.md-circle-icon-button :refer [icons example-icons]]
            [re-com.box                    :refer [h-box v-box box gap line]]
            [re-com.tabs                   :refer [horizontal-bar-tabs vertical-bar-tabs]]
            [re-demo.utils                 :refer [panel-title component-title args-table material-design-hyperlink]]
            [reagent.core                  :as    reagent]))

(defn md-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [v-box
       :gap "10px"
       :children [[panel-title "[md-icon-button ... ]"]

                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [:span "Material design icons can be " [material-design-hyperlink "found here"] "."]
                                          [args-table md-icon-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :gap "15px"
                                           :children [[example-icons selected-icon]
                                                      [gap :size "10px"]
                                                      [label :label "Hover over the buttons below to see a tooltip."]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[label :label "States:"]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :emphasise?   true
                                                                   :tooltip      "This button has :emphasise? set to true"
                                                                   :on-click     #()]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This is the default button"
                                                                   :on-click     #()]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This button has :disabled? set to true"
                                                                   :disabled?    true
                                                                   :on-click     #()]]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[label :label "Sizes:"]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This is a :smaller button"
                                                                   :size         :smaller
                                                                   :on-click #()]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This button does not specify a :size"
                                                                   :on-click     #()]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This is a :larger button"
                                                                   :size         :larger
                                                                   :on-click #()]]]]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [md-icon-button-demo])
