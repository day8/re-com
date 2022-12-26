(ns re-demo.md-circle-icon-button
  (:require [re-com.core    :refer [at h-box v-box box gap line label md-circle-icon-button horizontal-bar-tabs vertical-bar-tabs p p-span]]
            [re-com.buttons :refer [md-circle-icon-button-parts-desc md-circle-icon-button-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 parts-table args-table material-design-hyperlink github-hyperlink status-text]]
            [re-com.util    :refer [px]]
            [reagent.core   :as    reagent]))


(def icons
  [{:id "zmdi-plus"    :label [:i {:class "zmdi zmdi-plus"}]}
   {:id "zmdi-delete"  :label [:i {:class "zmdi zmdi-delete"}]}
   {:id "zmdi-undo"    :label [:i {:class "zmdi zmdi-undo"}]}
   {:id "zmdi-home"    :label [:i {:class "zmdi zmdi-home"}]}
   {:id "zmdi-account" :label [:i {:class "zmdi zmdi-account"}]}
   {:id "zmdi-info"    :label [:i {:class "zmdi zmdi-info"}]}])


(defn example-icons
  [selected-icon]
  [h-box :src (at)
   :align :center
   :gap "8px"
   :children [[label :src (at) :label "Choose an icon:"]
              [horizontal-bar-tabs :src (at)
               :model     selected-icon
               :tabs      icons
               :on-change #(reset! selected-icon %)]
              [label :src (at) :label @selected-icon]]])

(defn md-circle-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [v-box :src (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[md-circle-icon-button ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/md_circle_icon_button.cljs"]
                  [h-box :src (at)
                   :gap "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p-span "Material design icons, and their names, can be " [material-design-hyperlink "found here"] "."]
                                          [args-table md-circle-icon-button-args-desc]]]
                              [v-box :src (at)
                               :gap "10px"
                               :children [[title2 "Demo"]
                                          [v-box :src (at)
                                           :gap "15px"
                                           :children [[example-icons selected-icon]
                                                      [gap :src (at) :size "10px"]
                                                      [p "Here's what the chosen icon looks like in a Circle Icon Button."]
                                                      [h-box :src (at)
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[box :src (at) :width "90px" :child [:code ":size"]]
                                                                  [md-circle-icon-button :src (at)
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :smaller"
                                                                   :size         :smaller
                                                                   :on-click #()]
                                                                  [md-circle-icon-button :src (at)
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "No :size set. This is the default button"
                                                                   :on-click     #()]
                                                                  [md-circle-icon-button :src (at)
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :larger"
                                                                   :size         :larger
                                                                   :on-click #()]]]
                                                      [h-box :src (at)
                                                       :gap      "20px"
                                                       :align    :center
                                                       :justify  :start
                                                       :children [[box :src (at) :width "90px" :child [:code ":emphasise?"]]
                                                                  [md-circle-icon-button :src (at)
                                                                   :md-icon-name @selected-icon
                                                                   :emphasise?   true
                                                                   :tooltip      "This button has :emphasise? set to true"
                                                                   :on-click     #()]]]
                                                      [h-box :src (at)
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[box :src (at) :width "90px" :child [:code ":disabled?"]]
                                                                  [md-circle-icon-button :src (at)
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This button has :disabled? set to true"
                                                                   :disabled?    true
                                                                   :on-click     #()]]]]]]]]]
                  [parts-table "md-circle-icon-button" md-circle-icon-button-parts-desc]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [md-circle-icon-button-demo])
