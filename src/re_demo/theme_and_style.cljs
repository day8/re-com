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
        [[rc/p "Most re-com components can be passed these named arguments:"]
         [rc/p
          [:ul
           [:li [:code [:strong ":style"]] " must be a map, representing CSS inline style rules."]
           [:li  [:code [:strong ":class"]] " must be a string, or vector of strings, representing CSS class-names."]
           [:li [:code [:strong ":attr"]] " must be a map. It stands for " [:i "Html attributes"] ". "
            "These represent extra attributes, alongside class and style. "
            "For instance, " [:code ":on-click"] " or " [:code ":data-my-attribute"]]
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
           [:li [:i "flexibility"] ": such as, applying design conditionally, based on some dynamic state."]
           [:li [:i "specificity"] ": such as, building custom view logic into one instance of a component. "]
           [:li [:i "parsimony"] ": such as, applying a design system over an entire app, "]]]
         [rc/p
          "Given a re-com component function, which returns a tree of hiccups, "
          "we need deeper mechanics we can use, both individually and in combination. We need ways to: "
          [:ul
           [:li "Replace an entire subtree (a " [:code ":parts"] " hiccup)."]
           [:li "Override a certain hiccup's arguments (a " [:code ":parts"] " map)."]
           [:li "Re-write a hiccup's component function (a " [:code ":parts"] " function)."]
           [:li "Wrap a hiccup's arguments with a function (a " [:code ":theme"] ")."]
           [:li "Register a function that wraps the arguments of all hiccups everywhere "
            "(a global " [:code ":theme"] ")."]]]]]
       [rc/box
        :align :center
        :child [:img {:src   "demo/architecture-LOD.png"
                      :style {:width  "400px"
                              :height "auto"}}]]]]]}])

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
        [[rc/p "While an ordinary reagent component simply returns a tree of hiccups, "
          "a re-com component identifies each meaningful hiccup as a " [:i "part"] ". "
          "We describe the tree of " [:i "parts"] " at the bottom of "
          "each component's documentation page (in the left sidebar of this website). "]
         [rc/p
          "Passing a " [:code ":parts"] " map to a re-com component gives you control over the details "
          "of each hiccup in the tree: its component function, its props and its children. "
          "The keys are " [:i "part"] "-ids. The vals are " [:i "part"] "-specs. "
          "They specify how to customize each part."
          "This customization works differently, depending on the type of val you declare."]
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
           [:li [:strong [:code ":re-com"]] " is a map describing the re-com component overall, "
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
                    :anchor {:style {:background-color "pink"}
                             :class "italic"}}}])]]]]}])

(defn theme []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/title {:src   (rc/at)
                    :level :level2
                    :label "Theme"}]
         [rc/p "A theme is a pattern for " [:i "how"] " to draw UI, independent of "
          [:i "what"] " and " [:i "where"] ". "
          "You can pass a function as the " [:code ":theme"] " "
          "argument to a re-com component. This " [:i "theme-"] "function takes "
          "a map, and returns a new map. In this way, "
          "it wraps the props which re-com passes to each " [:i "part"] " "
          "(i.e. the second item of each hiccup)."]
         [rc/p
          "You can also register a "
          [:i "theme"] "-function globally, and re-com will apply it to every "
          "instance of a re-com component in your app."]
         [rc/p {:style {:background-color "#eee" :padding 7}}
          [:strong "Note"] ": Only some components support themes. "
          "Our effort to bring full support to all components is "
          [:a {:href "https://github.com/day8/re-com/issues/352"} "ongoing"] "."]]]
       [rc/box
        :align :center
        :child [:img {:style {:height "250px"} :src "demo/light-bulb-shapes.jpg"}]]]]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/p "Themes synergize well with both map and function parts. "
        "Hiccup and string parts aren't affected by themes, since re-com "
        "doesn't control their props. In light of this, let's define some functional "
        [:i "part"] "-specs for this example."]
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
          "Here is a " [:code ":theme"] " function which adds a background color. "]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :align :start
        :children
        (rdu/with-src
          (defn orange-theme [props]
            (update props :style merge {:background "orange"})))]]]
     [rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "Finally we apply the theme, "
          "simply by passing our " [:i "theme"] "-function to the component. "
          "Note that both the anchor and body are orange, since the " [:code ":theme"] " "
          "function applies to every part (including parts you don't specify)."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :align :start
        :children
        (rdu/with-src
          [rc/dropdown
           {:parts {:anchor my-anchor :body my-body}
            :theme orange-theme}])]]]
     [rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p
          "Re-com passes a " [:code ":part"] " argument to each part, identifying it "
          "with a fully-qualified keyword. "
          "Here is a " [:i "theme"] "-function that adds background-color only to the "
          [:code ":body"] ", and font-style only to the " [:code ":anchor"] "."]]]
       [rc/v-box
        :gap "19px"
        :children
        [[rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          (rdu/with-src
            (defn precise-theme [props]
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
              :theme precise-theme}])]]]]]
     [rc/gap :size "19px"]
     [rc/title :level :level3 :label "Themes can be global"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "You can pass a " [:i "theme"] "-function to " [:code "reg-theme"]
          ", and re-com will use it on every component in your application. "
          "Here, our global theme customizes the anchor and body parts, even though "
          "we haven't passed any " [:code ":theme"] " argument. In reality, this would "
          "happen to every component on every page (we're faking it here for demonstration)."]
         [rc/p "By default, this replaces the function at the " [:code ":user"] " layer "
          "(see \"themes are layered\" below). "
          "You can pass two arguments - a layer-id and a " [:i "theme"] "-function - "
          "to replace a different layer."]]]
       [rc/v-box
        :gap "19px"
        :children
        [[rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          [[rdu/zprint-code '(re-com.core/reg-theme precise-theme)]]]
         [rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          [[rdu/zprint-code
            '[rc/dropdown
              {:parts {:anchor my-anchor :body my-body}}]]
           [rc/dropdown
            {:parts {:anchor my-anchor :body my-body}
             :theme precise-theme}]]
          ]]]]]
     [rc/gap :size "19px"]
     [rc/title :level :level3 :label "Themes are layered"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "To fully determine the props for a " [:i "part"] ", "
          "re-com composes a handful of " [:i "theme"] "-functions, "
          "including those you pass in or register."
          "In order of application, these include:"]
         [rc/p
          [:ul
           [:li [:strong ":variables"]
            " - adds static data under the path "
            [:code "[:re-com :variables]"]
            ". This can include color palettes, spacing units and other standard values."]
           [:li [:strong ":pre-user"]
            " - empty by default. Here you could implement a spacing or color scheme, "
            "simply by changing the values within " [:code "[:re-com :variables]"] "."]
           [:li [:strong ":pre-theme"]
            " - cannot be registered. Contains any function you have passed "
            "as the " [:code ":pre-theme"] " argument to a re-com component."]
           [:li [:strong ":base"]
            " - contains re-com's essential functionality, such as box-model positioning "
            "and event handling. Replace at your own risk."]
           [:li [:strong ":main"]
            " - contains re-com's default visual styling for all components."]
           [:li [:strong ":user"]
            " - empty by default. Calling " [:code "reg-theme"] " "
            "will replace the " [:code ":user"] " layer, unless you specify "
            "a different layer."]
           [:li [:strong ":re-com-meta"]
            " - cannot be registered. Adds useful information, "
            "such as the " [:code "rc-component-name"] "class, and the "
            [:code "data-rc"] " html attribute."]
           [:li [:strong ":theme"]
            " - cannot be registered. Contains any function you have passed "
            "as the " [:code ":theme"] " argument to a re-com component."]]]]]
       [rc/v-box
        :gap "19px"
        :children
        []]]]
     [rc/gap :size "19px"]
     [rc/title :level :level3 :label "Themes are (not) reactive"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A re-com component composes the theme "
          [:strong "once"] ", when it mounts. That means it will not react to changes in the passed-in "
          [:code ":theme"] " or " [:code ":pre-theme"] " arguments. This makes themes more performant. "
          "If you do need to do reactive programming, consider doing it within the theme function."]]]
       [rc/v-box
        :children
        [
          "For instance, instead of this:"
          [:br]
          [rdu/zprint-code
           '[rc/dropdown
             {:theme (if (deref night-mode?) dark-theme light-theme)}]]
          [:br]
          "try this:"
          [:br]
          [rdu/zprint-code
           '[rc/dropdown
             {:theme (fn [props] (if (deref night-mode?)
                                   (dark-theme props)
                                   (light-theme props)))}]]]]]]
     #_[rc/title :level :level3 :label "Targeting the top-level hiccup"]
     #_[rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "Hello."]]]
       [rc/v-box
        :gap "19px"
        :children
        [[rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          [[rdu/zprint-code '(re-com.core/reg-theme my-theme)]]]
         [rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          [[rdu/zprint-code
            '[rc/dropdown
              {:parts {:anchor my-anchor :body my-body}}]]
           [rc/dropdown
            {:parts {:anchor my-anchor :body my-body}
             :theme my-theme}]]
          ]]]]]]}])

(defn panel* []
  [rc/v-box
   {:gap [rc/line]
    :children
    [[rdu/panel-title "Theme & Style"
      "src/re_com/core.cljs"
      "src/re_demo/theme_and_style.cljs"]
     [basics]
     [composition]
     [parts]
     [theme]]}])

(defn panel []
  [panel*])
