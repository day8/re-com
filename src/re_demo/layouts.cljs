(ns re-demo.layouts
  (:require [re-com.util              :as    util]
            [re-com.box               :refer [h-box v-box box
                                              gap line]]
            [re-com.core              :refer [button]]
            [reagent.core             :as    reagent]))


(defn panel
  []
  [v-box
   :children [[h-box
               :f-child false
               :align :center
               :children [[button :label "Button 1"]
                          [gap :size "10px"]
                          [:h3 "Layouts"]
                          [gap :size "10px"]
                          [button :label "Button 2"]]]
              [gap :size "10px"]
              [line :size "2px"]
              [h-box
               :children [[box
                           :b-color "aquamarine"
                           :child   [:p "This is the left side div. The red lines are [line] components, the white gap is a [gap] component."]]
                          [line :size "5px"]
                          [gap  :size "100px"]
                          [line :size "5px"]
                          [box
                           :b-color "darkkhaki"
                           :child   [:p "This is the right side div."]]]]]])
