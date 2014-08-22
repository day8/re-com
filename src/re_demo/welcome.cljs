(ns re-demo.welcome
  (:require [reagent.core :as reagent]
            [re-demo.util :refer [title]]
            [re-com.box   :refer [h-box v-box box gap]]
            [re-com.core  :refer [gap-old]]))


(defn panel
  []
  [v-box
   :children [[title "Re-Com"]

              [:p "Re-com is a component library for Reagent. "]
              [gap-old :height 15]
              [:p "It makes use of:"]
              [:ul
               [:li [:a
                     {:href "http://getbootstrap.com/"
                      :target "_blank"}
                     "Bootstrap"]]
               [:li [:p

                     [:a
                      {:href "https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Flexible_boxes"
                       :target "_blank"}
                      "Flex Box"]]]]

              [:p
               "Use of Flexbox means Chrome, Firefox or IE11."]
              [:p
               "So that's not the retail web which is full of IE10 and IE9. "]
              [:p
               "But it does include chrome apps, or desktop grade apps written in atom-shell or nodewebkit,  etc."]


              [gap-old :height 20]

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
                      "The om tutorial port - By Jonas Enlund"]]]]]])
