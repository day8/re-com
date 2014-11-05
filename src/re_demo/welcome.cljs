(ns re-demo.welcome
  (:require [reagent.core :as reagent]
            [re-com.core  :refer [title hyperlink]]
            [re-com.box   :refer [h-box v-box box gap]]))


(defn panel
  []
  [v-box
   :children [[title :label "Re-Com"]
              [gap :size "15px"]
              [:p "Re-com is a library of ClojureScript UI components, built on top of "
               [hyperlink
                :label  "Reagent"
                :href   "https://github.com/holmsand/reagent"
                :target "_blank"]
               ", which is itself built atop "
               [hyperlink
                :label  "React"
                :href   "http://facebook.github.io/react/"
                :target "_blank"]
               "."]
              [:p "In a nutshell, Re-com contains the kind of layout and widgetry you'd need to build a desktop-class app
                   in an environment like "
               [hyperlink
                :label  "node-webkit"
                :href   "https://github.com/rogerwang/node-webkit"
                :target "_blank"]
               " or "
               [hyperlink
                :label  "atom-shell"
                :href   "https://github.com/atom/atom-shell"
                :target "_blank"]
               ". It may even be useful for Chrome app developement."]

              [:p "What Re-com Is currently missing:"]
              [:ul
               [:li
                "A good \"combobox\" widget - think  "
                [hyperlink
                 :label  "Bootstrap + Choosen"
                 :href   "http://alxlit.name/bootstrap-chosen/"
                 :target "_blank"]]
               [:li "A tree widget"]
               [:li
                "A grid.  HTML is excellent at small grids, so no problem there. "
                "But when the number of rows gets huge, you need a widget that does virtual rows. "
                "Otherwise there's just too many DOM noes in the page, and performance suffers."]
               [:li
                "Focus managment? When the user presses tab, to what field does focus move?  Not 100% sure about this."]
               [:li
                "A framework for tying together views to underlying models, and some way to \"conveyer belt\" UI events back to some sort of controller/state-management logic."]
               [:li
                "drag and drop"]
               [:li
                "annimations and transitions"]
               [:li
                "date & date time"]
               [:li
                "time"]
               [:li
                "how do we do testing?  Look in inspiration in "
                [hyperlink
                 :label  "omchaya"
                 :href   "https://github.com/sgrove/omchaya/"
                 :target "_blank"]]]

              [:p "What Re-com Is Not:"]
              [:ul
               [:li
                "Because it rellies heavily on "
                [hyperlink
                 :label  "Flex Box"
                 :href   "https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Flexible_boxes"
                 :target "_blank"]
                " , re-com currently only works on Chrome, Firefox or IE11. So, for the next few years, that makes it a poor fit for apps targeting"
                " the retail web which is rife with flexbox-less wastelands like IE10 and IE9."]

               [:li "None of the components have been designed with mobile in mind and there's certainly no attempt to morph based on media queries etc."]]

              [gap :size "15px"]
              [:p "It makes use of:"]
              [:ul
               [:li "CSS from " [hyperlink
                                 :label  "Bootstrap"
                                 :href   "http://getbootstrap.com/"
                                 :target "_blank"]]
               [:li "CSS from " [hyperlink
                                 :label  "Bootstrap + Choosen"
                                 :href   "http://alxlit.name/bootstrap-chosen/"
                                 :target "_blank"]]]

              [gap :size "15px"]
              [:p "Reagent tutorials and further explanation:"]

              [:ul
               [:li [hyperlink
                     :label  "The official examples"
                     :href   "https://github.com/holmsand/reagent/tree/master/examples"
                     :target "_blank"]]
               [:li [hyperlink
                     :label  "The om tutorial port - By Jonas Enlund"
                     :href   "https://github.com/jonase/reagent-tutorial"
                     :target "_blank"]]]]])
