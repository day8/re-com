(ns re-com.demo.welcome
   (:require [reagent.core :as reagent]
             [re-com.core  :refer [gap]]))




(defn panel
  []
  [:div
   [:h2.page-header "Re-Com"]
   [:p "Re-com is a component library for Reagent. "]

   [gap :height 30]

   [:p "It makes use of:"]
   [:ul
    [:li [:a
          {:href "http://getbootstrap.com/"
           :target "_blank"}
          "Bootstrap"]]
    [:li [:p
          "and, in some cases, "
          [:a
           {:href "https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Flexible_boxes"
            :target "_blank"}
           "Flex Box"]
          " so that's Chrome, Firefox, and > IE 10."]]]

   (gap :height 40)

   [:p "Reagent tutorials and further explanation:"]

   [:ul
    [:li [:a
          {:href "https://github.com/holmsand/reagent/tree/master/examples"
           :target "_blank"}
          "The official examples"]]
    [:li [:p
          [:a
           {:href "https://github.com/jonase/reagent-tutorial"
            :target "_blank"}
           "The Om Tutorial Port - By Jonas Enlund"]]]]])
