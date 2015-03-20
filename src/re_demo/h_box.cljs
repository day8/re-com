(ns re-demo.h-box
  (:require [re-com.core     :refer [h-box v-box box gap line scroller border button hyperlink-href]]
            [re-com.box      :refer [h-box-args-desc v-box-args-desc box-args-desc gap-args-desc line-args-desc scroller-args-desc border-args-desc]]
            [re-demo.utils   :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [re-com.validate :refer [extract-arg-data validate-args string-or-hiccup? alert-type? vector-of-maps?]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[h-box ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/h_box.cljs"]]]

              [h-box
               :gap      "50px"
              :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Stable"]
                                      [paragraphs
                                       [:p "TBA..."]]
                                      [args-table h-box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span.all-small-caps "TBA..."]]]]]
              [gap :size "30px"]]])


;;====================================================================================


(def rounded-panel {:background-color "#fff4f4"
                    :border           "1px solid lightgray"
                    :border-radius    "8px"
                    :margin           "8px"
                    :padding          "8px"
                    :flex             "1"
                    ;:overflow-x       "hidden"
                    ;:overflow-y       "auto"
                    })

(def side-bar {:background-color "#f0f0ff"
               :width            "100%"
               ;:overflow         "auto"
               })


(defn panelA2
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "Boxes (simple, with scrollers)"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/h_box.cljs"]]]
              [status-text "Stable"]
              [gap :size "15px"]
              [line :size "2px"]
              [h-box
               :size "auto"
               :children [[scroller
                           :child [box
                                   :min-width "250px"
                                   :child     [:div {:style side-bar}
                                               [:p "Fixed Left Side Bar"]
                                               [:p "scroller auto"]
                                               [:p "size=250px"]]]]
                          [line :size "2px"]
                          [scroller
                           :size "60%"
                           :h-scroll :off
                           :child [box
                                   :size  "60%"
                                   :child [:div {:style rounded-panel}
                                           [:h4 "Left Panel"]
                                           [:p "Surrounded by a scroller with h-scroll off"]
                                           [:p "size=60%"]
                                           [:p "The red lines are [line] components."]
                                           [:p "The white space between the heading above and the top red line is a [gap] component of 30px size."]]]]
                          [line :size "2px"]
                          [scroller
                           :size "40%"
                           :v-scroll :off
                           :child [box
                                   :size  "40%"
                                   :child [:div {:style rounded-panel}
                                           [:h4 "Right Panel"]
                                           [:p "Surrounded by a scroller with v-scroll off"]
                                           [:p "size=40%"]
                                           [:p "The width of this panel and the left panel is 40%/60% of the pixels remaining after
                                                accounting for all the fixed pixels, including boxes, lines, gaps and horizontal margin values."]]]]
                          [line :size "2px"]
                          [scroller
                           :size "initial"
                           :width "250px"
                           :child [box
                                   :size  "initial"
                                   :width "250px"
                                   :child [:div {:style side-bar}
                                           [:p "Fixed (but shrinkable) Right Side Bar"]
                                           [:p "scroller auto"]
                                           [:p "size=initial width=250px"]]]]]]]])


(defn panelA   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panelA2])


(defn panelB2
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "Boxes (min-width/height)"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/h_box.cljs"]]]
              [status-text "Stable"]
              [gap :size "15px"]
              [line :size "2px"]
              [v-box
               :size     "auto"
               :children [[h-box
                           :size      "50%"
                           ;:min-width "900px"
                           :children [[box
                                       :size "100px"
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Left Side Bar"]
                                               [:p "size=100px"]]]
                                      [line :size "2px"]
                                      [scroller
                                       :size       "initial"
                                       :width      "600px"
                                       :min-width  "300px"
                                       ;:min-height "300px" ;; Must remove or it will never scroll
                                       :child [box
                                               :size       "initial"
                                               :width      "600px"
                                               :min-width  "300px"
                                               :min-height "300px"
                                               :child   [:div {:style rounded-panel}
                                                         [:h4 "Top Left Panel"]
                                                         [:p "Surrounded by a scroller, dimension attributes below copied to scroller"]
                                                         [:p "size=initial, width=600px, min-width=300px, min-height=300px"]
                                                         ]]]
                                      [line :size "2px"]
                                      [box
                                       :size       "auto"
                                       :min-width  "300px"
                                       :min-height "200px"
                                       :child   [:div {:style rounded-panel}
                                                 [:h4 "Top Right Panel"]
                                                 [:p  "size=auto, min-width=300px, min-height=200px"]]]
                                      [line :size "2px"]
                                      [box
                                       :size "initial"
                                       :width "100px"
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Right Side Bar"]
                                               [:p "size=initial width=100px"]]]
                                      ]]
                          [h-box
                           :size      "50%"
                           :children [[box
                                       :size "100px"
                                       ;:min-width "100px" ;; Not required now that size 100 in non-shrinking
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Left Side Bar"]
                                               [:p "size=100px"]]]
                                      [line :size "2px"]
                                      [box
                                       :size    "600px"
                                       :min-width "200px"
                                       :child   [:div {:style rounded-panel}
                                                 [:h4 "Bottom Left Panel"]
                                                 [:p  "size=600px, min-width=200 (which is ignored because of size)"]]]
                                      [line :size "2px"]
                                      [box
                                       :size    "auto"
                                       :min-width "200px"
                                       :child   [:div {:style rounded-panel}
                                                 [:h4 "Bottom Right Panel"]
                                                 [:p  "size=auto, min-width=200"]]]
                                      [line :size "2px"]
                                      [box
                                       :size "initial"
                                       :width "100px"
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Right Side Bar"]
                                               [:p "size=initial width=100px"]]]]]]]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panelB
  []
  [panelB2])
