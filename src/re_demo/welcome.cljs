(ns re-demo.welcome
  (:require [reagent.core :as reagent]
            [re-com.core  :refer [title]]
            [re-com.box   :refer [h-box v-box box gap]]))

(defn hyperlink
  [text href]
  [:a {:href href :target "_blank"} text])


(defn panel
  []
  [v-box
   :children [[title "Re-Com"]

              [:p "Re-com is a high level ClojureScript UI framework. It builts on top of "
               [hyperlink "Reagent" "https://github.com/holmsand/reagent"]
               ", which is itself built atop "
               [hyperlink "React" "http://facebook.github.io/react/"]
               "."]
              [:p "In a nutshell, Re-com contains the kind of layout and widgetry you'd need to build a desktop-class app
               in an environment like "
               [hyperlink "node-webkit" "https://github.com/rogerwang/node-webkit"]
               " or "
               [hyperlink "atom-shell" "https://github.com/atom/atom-shell" ]
               ". It may even be useful for Chrome app developement."]

              [:p "What Re-com Is currently missing:"]
              [:ul
               [:li
                "A good \"combobox\" widget - think  "
                [hyperlink "Bootstrap + Choosen" "http://alxlit.name/bootstrap-chosen/" ]]
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
                [hyperlink "omchaya" "https://github.com/sgrove/omchaya/"]
                ]]


               [:p "What Re-com Is Not:"]
              [:ul
               [:li
                "Because it rellies heavily on "
                [hyperlink "Flex Box" "https://developer.mozilla.org/en-US/docs/Web/Guide/CSS/Flexible_boxes" ]
                " , re-com currently only works on Chrome, Firefox or IE11. So, for the next few years, that makes it a poor fit for apps targeting"
                " the retail web which is rife with flexbox-less wastelands like IE10 and IE9."]

               [:li "None of the components have been designed with mobile in mind and there's certainly no attempt to morph based on media queries etc."]]

              [gap :height 15]
              [:p "It makes use of:"]
              [:ul
               [:li "CSS from " [hyperlink "Bootstrap" "http://getbootstrap.com/" ]]
               [:li "CSS from " [hyperlink "Bootstrap + Choosen" "http://alxlit.name/bootstrap-chosen/" ]]]



              [gap :height 20]

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
