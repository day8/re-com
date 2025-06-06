(ns re-demo.customization
  (:require
   [re-com.core :as rc]
   [re-demo.utils :as rdu]))

(defn basics []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "Most re-com components can be passed these named arguments:"]
         [rc/p
          [:ul
           [:li [:code [:strong ":style"]] " must be a map, representing CSS inline style rules."]
           [:li [:code [:strong ":class"]] " must be a string, or vector of strings, representing CSS class-names."]
           [:li [:code [:strong ":attr"]] " must be a map. It stands for " [:i "Html attributes"] ". "
            "These represent extra attributes, alongside class and style. "
            "For instance, " [:code ":on-click"] " or " [:code ":data-my-attribute"]]
           [:li [:code [:strong ":children"]] " must be a sequence of hiccups. "
            "Some components expect a single " [:code ":child"] ", instead."]]]
         [rc/p "When a re-com component returns a tree of hiccups, these arguments tend to apply to the outermost hiccup."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/h-box {:class    "italic"
                     :style    {:border "thin dashed grey"
                                :padding "2px"}
                     :children ["Hello" "World"]
                     :attr     {:title "Hello"}}])]]]]}])
(defn composition []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/title {:src (rc/at) :level :level2 :label "Composition"}]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p
          "The above is enough in most cases, but sometimes we want to express UI with more: "]
         [rc/p
          [:ul
           [:li [:i "flexibility"] ": such as, applying design conditionally, based on some dynamic state."]
           [:li [:i "specificity"] ": such as, building custom view logic into one instance of a component. "]
           [:li [:i "parsimony"] ": such as, applying a design system over an entire app, "]]]
         [rc/p
          "Given a re-com component function, which returns a tree of hiccups, "
          "we need deeper mechanics we can use, both individually and in combination. "
          "The following pages describe ways to: "]
         [rc/p
          [:ul
           [:li "Replace an entire subtree (a " [:code ":parts"] " hiccup)."]
           [:li "Override a certain hiccup's arguments (a " [:code ":parts"] " map)."]
           [:li "Re-write a hiccup's component function (a " [:code ":parts"] " function)."]
           [:li "Wrap a hiccup's arguments with a function (a " [:code ":theme"] ")."]
           [:li "Register a function that wraps the arguments of all hiccups everywhere "
            "(a global " [:code ":theme"] ")."]]]
         [rc/p {:style {:background-color "#eee" :padding 7}}
          [:strong "Note"] ": Re-com has used "
          [:a {:href "https://bootstrapdocs.com/v3.3.5/docs/getting-started/"} "bootstrap 3.3.5"]
          " as a design system. A complete implementation of our theme system will make bootstrap optional. "
          "It should then be possible to plug in a different system, such as "
          [:a {:href "https://tailwindcss.com/"} "tailwind"]
          " or "
          [:a {:href "https://github.com/thheller/shadow-css"} "shadow-css."]]]]
       [rc/box
        :align :center
        :child [:img {:src   "demo/architecture-LOD.jpg"
                      :style {:width  "400px"
                              :height "auto"}}]]]]]}])

(defn panel* []
  [rc/v-box
   {:children
    [[rdu/panel-title "Customization" nil "src/re_demo/customization.cljs"]
     [basics]
     [rc/line]
     [composition]]}])

(defn panel []
  [panel*])
