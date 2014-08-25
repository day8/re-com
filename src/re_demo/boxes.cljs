(ns re-demo.boxes
  (:require [re-com.util              :as    util]
            [re-com.box               :refer [h-box v-box box
                                              gap line scroller border]]
            [re-com.core              :refer [button]]
            [reagent.core             :as    reagent]))


(def rounded-panel {:background-color "fff4f4"
                    :border           "1px solid lightgray"
                    :border-radius    "8px"
                    :margin           "8px"
                    :padding          "8px"
                    :flex             "1"})

(def side-bar {:background-color "f0f0ff"})


(defn panel1
  []
  [v-box
   :size "auto"
   :children [[box
               :child [h-box
                       :justify :center
                       :children [[:h3 "Boxes (simple)"]]]]
              [gap  :size "30px"]
              [line :size "2px"]
              [h-box
               :size "100%"
               :children [[box
                           :size "100px"
                           :padding "4px"
                           :child [:div {:style side-bar}
                                   "Fixed Left Side Bar (100px)"]]
                          [line :size "2px"]
                          [box
                           :size    "60%"
                           :child   [:div {:style rounded-panel}
                                     [:h4 "Left Panel (60%)"]
                                     [:p "The red lines are [line] components."]
                                     [:p "The white space between the heading above and the top red line is a [gap] component of 30px size."]]]
                          [line :size "2px"]
                          [box
                           :size    "40%"
                           :child   [:div {:style rounded-panel}
                                     [:h4 "Right Panel (40%)"]
                                     [:p "The width of this panel and the left panel is 40%/60% of the pixels remaining after
                                          accounting for all the fixed pixels, including boxes, lines, gaps and horizontal margin values."]]]
                          [line :size "2px"]
                          [box
                           :size "100px"
                           :padding "4px"
                           :child [:div {:style side-bar}
                                   "Fixed Right Side Bar (100px)"]]
                          ]]
              ]])


(defn panel2
  []
  [v-box
   :size     "auto"
   :children [[box
               :child [h-box
                       :justify :center
                       :children [[:h3 "Boxes (min-width/height)"]]]]
              [gap  :size "10px"]
              [line :size "2px"]
              [v-box
               :size     "auto"
               :children [[h-box
                           :size      "50%"
                           :min-width "900px"
                           :children [[box
                                       :size "100px"
                                       :min-width "100px"
                                       :padding "4px"
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Left Side Bar"]
                                               [:p "size=100px"]
                                               [:p "min-w=100px"]]]
                                      [line :size "2px"]
                                      [box
                                       :size       "600px"
                                       :min-width  "300px"
                                       :min-height "300px"
                                       :child   [:div {:style rounded-panel}
                                                 [:h4 "Top Left Panel"]
                                                 [:p "size=600px, min-width=300px, min-height=300px"]
                                                 [:p "Note: There is a min-width of 900px around this entire h-box"]]]
                                      [line :size "2px"]
                                      [box
                                       :size       "40%"
                                       :min-width  "300px"
                                       :min-height "200px"
                                       :child   [:div {:style rounded-panel}
                                                 [:h4 "Top Right Panel"]
                                                 [:p  "size=40%, min-width=300px, min-height=200px"]]]
                                      [line :size "2px"]
                                      [box
                                       :size "100px"
                                       :padding "4px"
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Right Side Bar"]
                                               [:p "size=100px"]]]
                                      ]]
                          [h-box
                           :size      "50%"
                           :min-width "1000px"
                           :children [[box
                                       :size "100px"
                                       :min-width "100px"
                                       :padding "4px"
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Left Side Bar"]
                                               [:p "size=100px"]
                                               [:p "min-w=100px"]]]
                                      [line :size "2px"]
                                      [box
                                       :size    "600px"
                                       :min-width "400px"
                                       :child   [:div {:style rounded-panel}
                                                 [:h4 "Bottom Left Panel"]
                                                 [:p  "Note: There is a min-width of 1000px around this entire h-box"]
                                                 [:p  "size=600px, min-width=400px"]]]
                                      [line :size "2px"]
                                      [box
                                       :size    "40%"
                                       :min-width "400px"
                                       :child   [:div {:style rounded-panel}
                                                 [:h4 "Bottom Right Panel"]
                                                 [:p  "size=40%, min-width=400px"]]]
                                      [line :size "2px"]
                                      [box
                                       :size "100px"
                                       :padding "4px"
                                       :child [:div {:style side-bar}
                                               [:p "Fixed Right Side Bar"]
                                               [:p "size=100px"]]]
                                      ]]
                          ]]
              ]])

(defn panel3
  []
  [h-box
   :size     "auto"
   :children [[box
               :size "100px"
               :padding "10px"
               ;:b-color "coral"
               :child [:div
                       {:style {:background-color "coral"}}
                       "LAYOUT SIDE BAR fixed to 100px"]]
              [v-box
               :size     "auto"
               :children [[scroller
                           :child [box
                                   ;:f-container true
                                   :size      "60%"
                                   :padding   "0px 10px"
                                   :child     [v-box
                                               ;:size     "auto"
                                               :children [[box
                                                           :size "none"
                                                           :child [h-box
                                                                   :justify :center
                                                                   :children [[button :label "Button 1"]
                                                                              [gap :size "10px"]
                                                                              [:h3 "Boxes (deep nesting and alignment)"]
                                                                              [gap :size "10px"]
                                                                              [button :label "Button 2"]]]]
                                                          [gap  :size "10px"]
                                                          [line :size "2px"]
                                                          [h-box
                                                           :size     "auto"
                                                           :children [[box
                                                                       :size "60px"
                                                                       ;:b-color "f0f0ff"
                                                                       :child [:div
                                                                               {:style {:background-color "f0f0ff"}}
                                                                               "Left Side Bar"]]
                                                                      [box
                                                                       :size    "50%"
                                                                       ;:b-color "#fff4f4"
                                                                       :child   [v-box
                                                                                 :size    "auto"
                                                                                 :justify :start
                                                                                 :children [[box
                                                                                             :child [:div.rounded-panel
                                                                                                     {:style {:background-color "fff4f4"}}
                                                                                                     [:h4 "Left Panel"]
                                                                                                     [:p "This is the left side div. The red lines are [line] components, the white gap is a [gap] component."]]]
                                                                                            [box
                                                                                             :child [:div {:style {:width "100px" :height "80px" :background-color "red"}}]]
                                                                                            [box
                                                                                             :align-self :center
                                                                                             :child [:div {:style {:width "100px" :height "80px" :background-color "green"}}]]
                                                                                            [box
                                                                                             :align-self :end
                                                                                             :child [:div {:style {:width "100px" :height "80px" :background-color "blue"}}]]
                                                                                            [box
                                                                                             :child [:div {:style {:width "100px" :height "80px" :background-color "yellow"}}]]
                                                                                            ]]]
                                                                      [line :size "2px"]
                                                                      [gap  :size "80px"]
                                                                      [line :size "2px"]
                                                                      [box
                                                                       :size    "50%"
                                                                       ;:f-container true
                                                                       ;:b-color "#fff4f4"
                                                                       :child   [:div.rounded-panel
                                                                                 {:style {:flex "1"
                                                                                          :background-color "fff4f4"}}
                                                                                 [:h4 "Right Panel"]
                                                                                 [:p "This is the right side div."]]]
                                                                      [box
                                                                       :size "60px"
                                                                       ;:b-color "f0f0ff"
                                                                       :child [:div
                                                                               {:style {:background-color "f0f0ff"}}
                                                                               "Right Side Bar"]]]]
                                                          ]]]]

                          [box
                           :size      "20%"
                           ;:b-color   "teal"
                           :padding   "0px 10px"
                           :child     [:div
                                       {:style {:background-color "teal"}}
                                       "VERTICAL PANEL #2 (20% high). The one above is 60% high"]]
                          [gap :size "10px"]
                          [box
                           ;:f-container true
                           :size      "20%"
                           ;; :b-color "plum"
                           :padding   "0px 10px"
                           :child   [h-box
                                     :children [[box
                                                 ;:b-color "khaki"
                                                 :padding "10px"
                                                 :child [:div
                                                         {:style {:background-color "khaki"}}
                                                         "VERTICAL PANEL #3 (20% high, 10px padding)"]]
                                                [box
                                                 ;:b-color "tan"
                                                 :child [:div
                                                         {:style {:background-color "tan"}}
                                                         [:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"]]]
                                                [gap :size "40px"]
                                                [box
                                                 ;:b-color "orange"
                                                 :child [:div
                                                         {:style {:background-color "orange"}}
                                                         "horizontal panel #3 (50px gap between this and horizontal panel #2"]]
                                                ]
                                     ]
                           ]]
               ]]
   ])
