(ns re-demo.boxes
  (:require [re-com.util              :as    util]
            [re-com.box               :refer [h-box v-box box
                                              gap line]]
            [re-com.core              :refer [button]]
            [reagent.core             :as    reagent]))


(defn panel
  []
  [v-box
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
                           :child   [:div.rounded-panel
                                     {:style {:background-color "fff4f4"}}
                                     [:h4 "Left Panel"]
                                     [:p "This is the left side div. The red lines are [line] components, the white gap is a [gap] component."]]]
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
              ]])


#_(defn panel
  []
  [v-box
   :children [[:div
               ;;{:style {:display "flex" :flex-flow "inherit" :flex 1}}
               [h-box
                :f-child false
                :justify :center
                :children [[button :label "Button 1"]
                           [gap :size "10px"]
                           [:h3 "Boxes"]
                           [gap :size "10px"]
                           [button :label "Button 2"]]]
               [gap :size "10px"]
               [line :size "2px"]
               [h-box
                :children [[box
                            :size "60px"
                            :b-color "f0f0ff"
                            :child [:div "Left Side Bar"]]
                           [box
                            :b-color "#fff4f4"
                            :child   [:div.rounded-panel
                                      [:h4 "Left Panel"]
                                      [:p "This is the left side div. The red lines are [line] components, the white gap is a [gap] component."]]]
                           [line :size "2px"]
                           [gap  :size "80px"]
                           [line :size "2px"]
                           [box
                            ;:f-container true
                            :b-color "#fff4f4"
                            :child   [:div.rounded-panel {:style {:flex "1"}}
                                      [:h4 "Right Panel"]
                                      [:p "This is the right side div."]]]
                           [box
                            :size "60px"
                            :b-color "f0f0ff"
                            :child [:div "Right Side Bar"]]]]]
              ]])
