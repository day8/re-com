(ns re-demo.layouts
  (:require [re-com.util              :as    util]
            [re-com.box               :refer [h-box v-box box
                                              h-gap v-gap
                                              h-line v-line]]
            [re-com.core              :refer [button]]
            [reagent.core             :as    reagent]))


(defn panel
  []
  [v-box
   :padding  "5px"
   :gap      "10px"
   :align    :around
   :children [[h-box
               :height   "60px"
               :children [[box
                           :align :start
                           :child [:h3 {:style {:display: "inline"}} "Layouts"]]]]
              [h-line :height "2px"]
              [h-box
               :padding  "5px"         ;; also: padding-top  padding-bottom padding-right
               :gap      "10px"        ;; between children
               :children [[v-gap :width "100px"]     ;; basics a bix with no centent a fixed size.
                          [box
                           ;; :size     "50%"        ;; remember that size is optional. When absent, child takes on natual size.
                           ;; :max-size "300px"
                           ;; :min-size "200px"
                           :child [:div {:style {:width "400px"
                                                 :height "100%"
                                                 :background-color "red"}}
                                   "This is a red div"]]
                          [v-line :width "5px"]      ;; in an hbox means one thing, vbox another ??
                          [v-gap  :width "100px"]
                          [v-line :width "5px"]
                          [box
                           ;; :size  "100px"
                           :child    [:div {:style {:width "400px"
                                                    :height "100%"
                                                    :background-color "green"}}
                                      "This is a green div"]]
                          ]]
              ]])
