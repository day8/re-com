(ns re-demo.theme-and-style
  (:require
   [re-com.core :as rc]
   [re-demo.utils :as rdu]))

(defn basics []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/title {:src   (rc/at)
                :level :level2
                :label "Basics: Customizing a Component"}]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "Most re-com components can be passed these four named arguments:"]
         [rc/p
          [:ul
           [:li [:code [:strong ":style"]] " must be a map, representing inline CSS style rules."]
           [:li  [:code [:strong ":class"]] " can be a string with CSS class-names "
            "separated by spaces, or a vector of strings."]
           [:li [:code [:strong ":attr"]] " must be a map. It stands for " [:i "Html attributes"] ". "
            "These represent extra attributes, alongside class and style. "
            "For instance, " [:code ":on-click"] " or " [:code ":data--my-attribute"]]
           [:li [:code [:strong ":children"]] " must be a sequence of hiccups. "
            "Some components expect a single " [:code ":child"] ", instead."]]]]]
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
          "The above is enough in most cases, but sometimes we want to express UI with more: "
          [:ul
           [:li [:i "parsimony"] ": such as, applying a design system over an entire app, "]
           [:li [:i "specificity"] ": such as, building custom view logic into one instance of a component. "]
           [:li [:i "flexibility"] ": such as, applying design conditionally, based on some dynamic state."]]]
         [rc/p
          "Given a re-com component, which returns a hiccup tree, we need deeper mechanics we can use, "
          " both individually and in combination. We need ways to: "
          [:ul
           [:li "Replace an entire subtree (a " [:code ":parts"] " hiccup)"]
           [:li "Override a certain hiccup's arguments (a " [:code ":parts"] " map)"]
           [:li "Re-write a hiccup's component function (a " [:code ":parts"] " function)"]
           [:li "Wrap a hiccup's arguments with a function (a " [:code ":theme"] ")"]
           [:li "Register a function that wraps the arguments of hiccups everywhere (a global " [:code ":theme"] ")"]]]]]
       [rc/box
        :align :center
        :child [:img {:src   "demo/architecture-LOD.jpg"
                      :style {:width  "400px"
                              :height "auto"}}]]]]]}])

(defn default-part []
  [rc/v-box
   {:src (rc/at)
    :gap "12px"
    :children
    [[rc/title {:src   (rc/at)
                :level :level2
                :label [:span "The default part"]}]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "More abstractly, here is a re-com component which implements the "
          "customization described above."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          (defn my-component [{:keys [class style attr children]}]
            (into [:div (merge {:class class :style style}
                               attr)]
                  children)))]]]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/p "It puts " [:code ":style"] " and " [:code ":class"] " into a map, "
        "merges in " [:code ":attr"] " and follows with the "
        [:code ":children"] "."]
       [rc/h-box
        :gap "12px"
        :children
        (rdu/with-src
          [my-component
           {:style    {:background :gold :padding 4}
            :class    ["bold"]
            :attr     {:on-click #(js/alert "That's my component, hands off!")}
            :children [[:div "My"] [:div "perfect"] [:div "component."]]}])]]]
     [rc/gap :size "12px"]]}])

(defn parts []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/title {:src   (rc/at)
                :level :level2
                :label [:code ":parts"]}]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "While a basic reagent component simply returns a tree of hiccups, "
          "a re-com component is composed of " [:code ":parts"]
          ". A part decomposes each hiccup, giving you control over "
          "its details: its component function, its props and its children."]
         [rc/p
          "Re-com also names each part, within the namespace of its parent component. "
          "These names are listed at the bottom of that component's documentation. "
          "By passing in a " [:code ":parts"] " map, you can target individual parts by name. "
          "Depending on the type of value, re-com will customize each part in different ways. "]
         [rc/p {:style {:background-color "#eee" :padding 7}}
          [:strong "Note"] ": Some re-com components only support map " [:code ":parts"] ". "
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
        [[rc/p "A part can be a string or a hiccup. In this case, "
          "the value is placed directly into the component tree."]]]
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
        [[rc/p "A part can be a component function. It is placed into a hiccup "
          "within the component tree, alongside a map of keyword arguments. "
          "These can include all the props listed in the " [:strong "Basics"]
          " section. There are also a few others, to give context:"]
         [rc/p
          [:ul
           [:li [:strong [:code ":parts"]]
            " is a keyword, with the namespace of the component and the name of the part. "
            " For instance, " [:code ":re-com.dropdown/anchor"] "."]
           [:li [:strong [:code ":re-com"]] " is a map with more context for the part, such as its "
            [:code ":state"] "."]]]]]
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
          "A part can be a map, letting you control some visual characteristics"
          " without needing to re-implement the whole component. "]
         [rc/p
          "Every part has a default component function (i.e. part-function), used when "
          "you don't pass a function of your own. "
          "Here, the default part for " [:code ":re-com.dropdown/anchor"] " "
          "is responsible for adding the text " [:code "\"Select an item\""] ". "
          "The props which re-com passes into this default part have been "
          "overridden by the pink " [:code ":style"]
          " and italic " [:code ":class"] " we passed in."]
         [rc/p
          "Just like in the " [:strong "Basics"] " section, "
          [:code ":style"] " is merged, " [:code ":class"] " is concatenated "
          " and " [:code ":attr"] " is merged at the top level. After that, it's up "
          "to the part-function to build the final hiccup."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/dropdown
           {:parts {:anchor {:style {:background-color "pink"}
                             :class "italic"}}}])]]]]}])

(defn theme []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/title {:src   (rc/at)
                :level :level2
                :label "Theme"}]
     [rc/p "A theme is a pattern for " [:i "how"] " to draw UI, independent of "
      [:i "what"] " and " [:i "where"] ". "
      "In clojure tradition, we used a functional indirection "
      "to implement re-com's theme system. You can pass a " [:code ":theme"] " "
      "argument to any component, a function which broadly controls the "
      "arguments to all that component's parts. You can also register a "
      [:code ":theme"] " function globally, and re-com will apply it to every "
      "re-com component instance in your app."]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/p "Themes synergize well with both map and function parts. "
        "Hiccup and string parts are not affected by the theme, since re-com "
        "can't pass arguments to them. In light of this, let's define some functional "
        "parts for this example."]
       [rc/v-box
        :gap "12px"
        :children
        [[rc/h-box
          :children
          (rdu/with-src
            (defn my-anchor [{:keys [style class attr]}]
              [rc/box (merge {:style style :class class :child "Open"} attr)]))]
         [rc/h-box
          :children
          (rdu/with-src
            (defn my-body [{:keys [style class attr]}]
              [rc/box (merge {:style style :class class :child "Sesame"} attr)]))]]]]]
     [rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p
          "Here is a " [:code ":theme"] " function which adds a background color. "
          "Note that both the anchor and body are orange, since the " [:code ":theme"] " "
          "function applies to every part."]]]
       [rc/v-box
        :gap "19px"
        :children
        [[rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          (rdu/with-src
            (defn orange-theme [props]
              (update props :style merge {:background "orange"})))]
         [rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          (rdu/with-src
            [rc/dropdown
             {:parts {:anchor my-anchor :body my-body}
              :theme orange-theme}])]]]]]
     [rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p
          "Re-com passes a " [:code ":part"] " argument to each part, naming it. "
          "Here is a " [:code ":theme"] " function that only adds background-color to the "
          [:code ":body"] " part, and adds a class to the " [:code ":anchor"] " part."]]]
       [rc/v-box
        :gap "19px"
        :children
        [[rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          (rdu/with-src
            (defn my-theme [props]
              (case (:part props)
                :re-com.dropdown/body
                (update props :style merge {:background "orange"})
                :re-com.dropdown/anchor
                (update props :class conj "italic")
                props)))]
         [rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          (rdu/with-src
            [rc/dropdown
             {:parts {:anchor my-anchor :body my-body}
              :theme my-theme}])]]]]]]}])

(defn panel* []
  [rc/v-box
   {:gap [rc/line]
    :children
    [[rdu/panel-title "Theme & Style"
      "src/re_com/core.cljs"
      "src/re_demo/theme_and_style.cljs"]
     [basics]
     [composition]
     [default-part]
     [parts]
     [theme]]}])

(defn panel []
  [panel*])


