(ns re-demo.md-circle-icon-button
  (:require [re-com.core    :refer [h-box v-box box gap line label md-circle-icon-button horizontal-bar-tabs vertical-bar-tabs #_round-button]]
            [re-com.buttons :refer [md-circle-icon-button-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table material-design-hyperlink github-hyperlink status-text paragraphs]]
            [reagent.core   :as    reagent]))


(def icons
  [{:id "md-add"    :label [:i {:class "md-add"}]}
   {:id "md-delete" :label [:i {:class "md-delete"}]}
   {:id "md-undo"   :label [:i {:class "md-undo"}]}
   {:id "md-home"   :label [:i {:class "md-home"}]}
   {:id "md-person" :label [:i {:class "md-person"}]}
   {:id "md-info"   :label [:i {:class "md-info"}]}])


(defn example-icons
  [selected-icon]
  [h-box
   :align :center
   :gap "8px"
   :children [[label :label "Choose an icon:"]
              [horizontal-bar-tabs
               :model     selected-icon
               :tabs      icons
               :on-change #(reset! selected-icon %)]
              [label :label @selected-icon]]])


(defn md-circle-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[md-circle-icon-button ... ]"
                                [github-hyperlink "Component Source" "src/re_com/buttons.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/md_circle_icon_button.cljs"]]]
                  [h-box
                   :gap "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "Material design icons can be " [material-design-hyperlink "found here"] "."]]
                                          [args-table md-circle-icon-button-args-desc]]]
                              [v-box
                               :gap "10px"
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
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :smaller"
                                                                   :size         :smaller
                                                                   :on-click #()]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "No :size set. This is the default button"
                                                                   :on-click     #()]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :larger"
                                                                   :size         :larger
                                                                   :on-click #()]]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :justify  :start
                                                       :children [[box :width "90px" :child [:code ":emphasis?"]]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :emphasise?   true
                                                                   :tooltip      "This button has :emphasise? set to true"
                                                                   :on-click     #()]]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[box :width "90px" :child [:code ":disabled?"]]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This button has :disabled? set to true"
                                                                   :disabled?    true
                                                                   :on-click     #()]]]]]]]]]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [md-circle-icon-button-demo])
