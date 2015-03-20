(ns re-demo.md-icon-button
  (:require [re-com.core                   :refer [h-box v-box box gap line md-icon-button label horizontal-bar-tabs vertical-bar-tabs]]
            [re-com.buttons                :refer [md-icon-button-args-desc]]
            [re-demo.md-circle-icon-button :refer [icons example-icons]]
            [re-demo.utils                 :refer [panel-title component-title args-table material-design-hyperlink github-hyperlink status-text paragraphs]]
            [reagent.core                  :as    reagent]))

(defn md-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[md-icon-button ... ]"
                                [github-hyperlink "Component Source" "src/re_com/buttons.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/md_icon_button.cljs"]]]

                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "Material design icons can be " [material-design-hyperlink "found here"] "."]]
                                          [args-table md-icon-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :gap "15px"
                                           :children [[example-icons selected-icon]
                                                      [gap :size "10px"]
                                                      [paragraphs
                                                       [:p "Here's what the chosen icon looks like in a Circle Icon Button."]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[box :width "90px" :child [:code ":size"]]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :smaller"
                                                                   :size         :smaller
                                                                   :on-click #()]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "No :size set. This is the default button"
                                                                   :on-click     #()]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :larger"
                                                                   :size         :larger
                                                                   :on-click #()]]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :justify  :start
                                                       :children [[box :width "90px" :child [:code ":emphasis?"]]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :emphasise?   true
                                                                   :tooltip      "This button has :emphasise? set to true"
                                                                   :on-click     #()]]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[box :width "90px" :child [:code ":disabled?"]]
                                                                  [md-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This button has :disabled? set to true"
                                                                   :disabled?    true
                                                                   :on-click     #()]]]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [md-icon-button-demo])
