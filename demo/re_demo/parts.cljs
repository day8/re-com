(ns re-demo.parts
  (:require
   [re-com.core :as rc]
   [re-demo.utils :as rdu]))

(defn panel []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rdu/panel-title "Parts" nil "src/re_demo/parts.cljs"]
     [rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "While an ordinary reagent component simply returns a tree of hiccups, "
          "a re-com component identifies each meaningful hiccup as a " [:i "part"] ". "
          "We describe the tree of " [:i "parts"] " at the bottom of "
          "each component's documentation page (in the left sidebar of this website). "]
         [rc/p
          "Passing a " [:code ":parts"] " map to a re-com component gives you control over the details "
          "of each hiccup in the tree: its component function, its props and its children. "
          "The keys are " [:i "part"] "-ids. The vals are " [:i "part"] "-specs. "
          "They specify how to customize each part. "
          "This customization works differently, depending on what type of data structure "
          "you declare for each " [:i "part"] "-spec."]
         [rc/p {:style {:background-color "#eee" :padding 7}}
          [:strong "Note"] ": Some re-com components only support the " [:i "map"] " type. "
          "Our effort to bring full support to all components is "
          [:a {:href "https://github.com/day8/re-com/issues/352"} "ongoing"] "."]]]
       [rc/box
        :align :center
        :child [:img {:style {:height "250px"} :src "demo/heart-parts.jpg"}]]]]
     [rc/title :level :level3 :label [:span "hiccup " [:code ":parts"]]]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A " [:i "part"] "-spec can be a string or a hiccup. In this case, "
          "the value is placed directly into the hiccup tree."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/dropdown
           {:parts {:anchor "Open"
                    :body   [:div "Sesame"]}}])]]]
     [rc/gap :size "31px"]
     [rc/title :level :level3 :label [:span "function " [:code ":parts"]]]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A " [:i "part"] "-spec can be a component function. "
          "It is placed into a hiccup, "
          "replacing re-com's default component function for that " [:i "part"] ". "
          "Re-com passes the same props to your new part function, "
          "including all the props listed in the " [:strong "Basics"] " "
          "section. There are a few more props, giving context to the part:"]
         [rc/p
          [:ul
           [:li [:strong [:code ":part"]]
            " is a keyword, with the namespace of the component and the name of the part. "
            " For instance, " [:code ":re-com.dropdown/anchor"] "."]
           [:li [:strong [:code ":re-com"]] " is a map describing the component overall, "
            "such as its " [:code ":state"] ", and " [:code "theme"] " information (see the "
            [:code "theme"] " section below)."]]]
         [rc/p {:style {:background-color "#eee" :padding 7}}
          [:strong "Note"] ": " [:code ":state"] " is an experimental feature, "
          "only supported by some components."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/dropdown
           {:parts {:anchor (fn [props]
                              [:span "I am " (get-in props [:re-com :state :openable])])
                    :body   (fn [props]
                              [:ul
                               [:li "I am a " [:code (str (get props :part))]]
                               [:li "Class: " [:code (str (get props :class))]]])}}])]]]
     [rc/gap :size "31px"]
     [rc/title :level :level3 :label [:span "map " [:code ":parts"]]]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p
          "A " [:i "part"] "-spec can be a map, letting you control some visual characteristics"
          " without needing to re-implement the whole component. "]
         [rc/p
          "Every part has a default component function, used when "
          "you don't pass a function of your own. "
          "Here, the default component function for " [:code ":re-com.dropdown/anchor"] " "
          "is responsible for the text " [:code "\"Select an item\""] ". "
          "The props which re-com passes into this function have been "
          "overridden by the pink " [:code ":style"]
          " and italic " [:code ":class"] " we passed in."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/dropdown
           {:parts {:body   "Sesame"
                    :anchor {:style {:color "pink"}
                             :class "italic"}}}])]]]]}])
