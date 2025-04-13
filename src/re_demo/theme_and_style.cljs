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
           [:li [:code [:strong ":children"]] " must be sequential."]]]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/h-box {:class    "italic"
                     :style    {:border "thin dashed grey"
                                :padding "2px"}
                     :children ["Hello" "World"]
                     :attr     {:title "Hello"}}])]]]]}])

(-> [:div]
    (conj {:a 1})
    (into [1 2]))

(defn composition []
  [rc/v-box
   {:src (rc/at)
    :gap "12px"
    :children
    [[rc/title {:src   (rc/at)
                :level :level2
                :label "Composition"}]
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
            :children [[:div "My"] [:div "perfect"] [:div "component."]]}])]]]]}])

(defn parts []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/title {:src   (rc/at)
                :level :level2
                :label "Parts"}]
     [rc/p "Some components are composed of " [:code ":parts"] "."
          "These "
          "You can pass a map to control this behavior."
          "Each part is identified by a key. The name of these "]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A part can be a string a hiccup. In this case, "
          "The part is placed directly into the component tree."]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/dropdown
           {:parts {:anchor "Open"
                    :body   [:div "Sesame"]}}])]]]
     [rc/h-box
      :gap "31px"
      :children
      [[rc/v-box
        :children
        [[rc/p "A part can be a component function. It will be placed in a hiccup "
          "within the component tree, alongside a map of props - specifically, "
          "the sort of props that a " [:i "re-com component"] " can expect: a single "
          "map, with optional keys " [:code ":style"] ", " [:code ":class"] ", "
          [:code ":attr"] " and " [:code ":children"]]]]
       [rc/h-box
        :style {:height :fit-content :gap "12px"}
        :children
        (rdu/with-src
          [rc/dropdown
           {:parts {:anchor "Open"
                    :body   [:div "Sesame"]}}])]]]]}])

(defn theme []
  [rc/v-box
   {:src (rc/at)
    :children
    [[rc/title {:src   (rc/at)
                :level :level2
                :label "Themes"}]
     [rc/p ""]]}])

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


