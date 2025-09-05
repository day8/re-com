(ns re-demo.theme
  (:require
   [re-com.core :as rc]
   [re-demo.utils :as rdu]))

(defn panel []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rdu/panel-title "Theme" nil "src/re_demo/theme.cljs"]
     [rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A theme is a pattern for " [:i "how"] " to draw UI, independent of "
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
        "doesn't control their props. In light of this, let's define a functional "
        [:i "part"] "-spec for this example."]
       [rc/h-box
        :children
        (rdu/with-src
          (defn my-body [{:keys [style class attr]}]
            [rc/box (merge {:style style :class class :child "Sesame"} attr)]))]]]
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
          "Note that the " [:code ":theme"] " function applies to every part - "
          "including parts you don't specify. "
          "In this case, the anchor turns orange, even though we haven't added any "
          [:code ":anchor"] "in the parts map. Although the anchor is in its default "
          " configuration, re-com still applies your " [:i "theme"] "-function to the props "
          "it passes to the anchor's component function."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :align :start
        :children
        (rdu/with-src [rc/dropdown {:parts {:body my-body} :theme orange-theme}])]]]
     [rc/gap :size "19px"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A theme function will also compose with a map " [:i "part"] "-spec. "
          "Here, for the anchor part, re-com modifies its props twice - first with the theme, "
          "and then with your " [:code ":anchor"] " spec."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :align :start
        :children
        (rdu/with-src [rc/dropdown {:parts {:body   my-body
                                            :anchor {:style {:color "white"}}}
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
          [:code ":body"] ", and font-style only to the " [:code ":anchor"] ". "]
         [rc/p
          "Hint: you could express a " [:i "theme"] "-function using a multimethod: "
          [:code "(defmulti my-theme :part)"]]]]
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
             {:parts {:body my-body}
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
              {:parts {:body my-body}}]]
           [rc/dropdown
            {:parts {:body my-body} :theme precise-theme}]]]]]]]
     [rc/gap :size "19px"]
     [rc/title :level :level3 :label "Themes are layered"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "To fully determine the props for a " [:i "part"] ", "
          "re-com composes a handful of " [:i "theme"] "-functions, "
          "including those you pass in or register. "
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
        [[rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          (rdu/with-src
            (defn dark-mode [props]
              (update-in props [:re-com :variables] merge
                         {:background "black"
                          :foreground "white"})))]
         [rc/h-box
          :style {:height :fit-content :gap "12px"}
          :align :start
          :children
          (rdu/with-src
            [rc/dropdown {:parts     {:body my-body}
                          :pre-theme dark-mode}])]]]]]
     [rc/gap :size "19px"]
     [rc/title :level :level3 :label "Themes are (not) reactive"]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A re-com component composes the " [:i "theme"] "-function "
          [:strong "once"] ", when it mounts. That means it will not react to changes in the passed-in "
          [:code ":theme"] " or " [:code ":pre-theme"] " arguments. This makes themes more performant. "
          "If you do need to do reactive programming, consider doing it within the theme function."]]]
       [rc/v-box
        :children
        ["For instance, instead of this:"
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
                                  (light-theme props)))}]]]]]]]}])
