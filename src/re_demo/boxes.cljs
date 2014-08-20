(ns re-demo.boxes
  (:require [re-com.util              :as    util]
            [re-com.box               :refer [h-box v-box box
                                              gap line]]
            [re-com.core              :refer [button]]
            [reagent.core             :as    reagent]))


(defn panel1
  []
  [v-box
   :children [[box
               :size "auto"
               :child [h-box
                       :justify :center
                       :children [[:h3 "Boxes"]]]]
              [gap  :size "10px"]
              [line :size "2px"]
              [h-box
               :children [[box
                           :size "100px"
                           :padding "4px"
                           :child [:div
                                   {:style {:background-color "f0f0ff"}}
                                   "Fixed Left Side Bar (100px)"]]
                          [line :size "2px"]
                          [box
                           :size    "60%"
                           :child   [:div.rounded-panel
                                     [:h4 "Left Panel (60%)"]
                                     [:p "This is the left side div."]
                                     [:p "The red lines are [line] components.
                                          The white space between the heading and the top red line is a [gap] component."]]]
                          [line :size "2px"]
                          [box
                           :size    "40%"
                           :child   [:div.rounded-panel
                                     [:h4 "Right Panel (40%)"]
                                     [:p "This is the right side div."]
                                     [:p "The width of this and the left side div is 40%/60% of the pixels remaining after
                                          removing all the fixed pixels, including boxes, lines, gaps and horizontal margin/padding values."]]]
                          [line :size "2px"]
                          [box
                           :size "100px"
                           :padding "4px"
                           :child [:div
                                   {:style {:background-color "f0f0ff"}}
                                   "Fixed Right Side Bar (100px)"]]]]
              ]])


(defn panel2
  []
  [h-box
   :children [[box
               :size "100px"
               :padding "10px"
               :b-color "coral"
               :child [:div
                       {:style {:background-color "coral"}}
                       "LAYOUT SIDE BAR fixed to 100px"]]
              [v-box
               :children [[box
                           ;:f-container true
                           :size      "60%"
                           :padding   "0px 10px"
                           :child     [v-box
                                       :children [[box
                                                   :size "auto"
                                                   :child [h-box
                                                           :justify :center
                                                           :children [[button :label "Button 1"]
                                                                      [gap :size "10px"]
                                                                      [:h3 "Boxes"]
                                                                      [gap :size "10px"]
                                                                      [button :label "Button 2"]]]]
                                                  [gap  :size "10px"]
                                                  [line :size "2px"]
                                                  [h-box
                                                   :children [[box
                                                               :size "60px"
                                                               :b-color "f0f0ff"
                                                               :child [:div
                                                                       {:style {:background-color "f0f0ff"}}
                                                                       "Left Side Bar"]]
                                                              [box
                                                               :b-color "#fff4f4"
                                                               :child   [v-box
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
                                                               ;:f-container true
                                                               :b-color "#fff4f4"
                                                               :child   [:div.rounded-panel
                                                                         {:style {:flex "1"
                                                                                  :background-color "fff4f4"}}
                                                                         [:h4 "Right Panel"]
                                                                         [:p "This is the right side div."]]]
                                                              [box
                                                               :size "60px"
                                                               :b-color "f0f0ff"
                                                               :child [:div
                                                                       {:style {:background-color "f0f0ff"}}
                                                                       "Right Side Bar"]]]]
                                                  ]]]
                          [box
                           :size      "20%"
                           :b-color   "teal"
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
                                                 :b-color "khaki"
                                                 :padding "10px"
                                                 :child [:div
                                                         {:style {:background-color "khaki"}}
                                                         "VERTICAL PANEL #3 (20% high, 10px padding)"]]
                                                [box
                                                 :b-color "tan"
                                                 :child [:div
                                                         {:style {:background-color "tan"}}
                                                         [:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"][:p "The quick brown fox"]]]
                                                [gap :size "40px"]
                                                [box
                                                 :b-color "orange"
                                                 :child [:div
                                                         {:style {:background-color "orange"}}
                                                         "horizontal panel #3 (50px gap between this and horizontal panel #2"]]
                                                ]
                                     ]
                           ]]
               ]]
   ])
