(ns re-demo.md-circle-icon-button
  (:require [re-com.core    :refer [h-box v-box box gap line label md-circle-icon-button horizontal-bar-tabs vertical-bar-tabs p p-span]]
            [re-com.buttons :refer [md-circle-icon-button-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table material-design-hyperlink github-hyperlink status-text]]
            [re-com.util    :refer [px]]
            [reagent.core   :as    reagent]))


(def icons
  [{:id "zmdi-plus"    :label [:i {:class "zmdi zmdi-plus"}]}
   {:id "zmdi-delete"  :label [:i {:class "zmdi zmdi-delete"}]}
   {:id "zmdi-undo"    :label [:i {:class "zmdi zmdi-undo"}]}
   {:id "zmdi-home"    :label [:i {:class "zmdi zmdi-home"}]}
   {:id "zmdi-account" :label [:i {:class "zmdi zmdi-account"}]}
   {:id "zmdi-info"    :label [:i {:class "zmdi zmdi-info"}]}])


(defn example-icons
  [selected-icon]
  [h-box
   :align :center
   :gap "8px"
   :children [[label :label "Choose an icon:"]
              [horizontal-bar-tabs
               :model     selected-icon
               :tabs      icons
               :on-change #(reset! selected-icon %)]
              [label :label @selected-icon]]])


(defn md-circle-icon-button-component-hierarchy
  []
  (let [indent          20
        table-style     {:style {:border "2px solid lightgrey" :margin-right "10px"}}
        border          {:border "1px solid lightgrey" :padding "6px 12px"}
        border-style    {:style border}
        border-style-nw {:style (merge border {:white-space "nowrap"})}
        valign          {:vertical-align "top"}
        valign-style    {:style valign}
        valign-style-hd {:style (merge valign {:background-color "#e8e8e8"})}
        indent-text     (fn [level text] [:span {:style {:padding-left (px (* level indent))}} text])
        highlight-text  (fn [text & [color]] [:span {:style {:font-weight "bold" :color (or color "dodgerblue")}} text])
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[md-circle-icon-button\n"
                      "   ...\n"
                      "   :parts {:tooltip {:class \"blah\"\n"
                      "                     :style { ... }\n"
                      "                     :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[md-circle-icon-button]")]
                   [:td border-style-nw "rc-md-circle-icon-button-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the button, tooltip (if any), everything."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[popover-tooltip]")]
                   [:td border-style-nw "rc-md-circle-icon-button-tooltip"]
                   [:td border-style-nw (code-text ":tooltip")]
                   [:td border-style "Tooltip, if enabled."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-md-circle-icon-button"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The actual button."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:i]")]
                   [:td border-style-nw "rc-md-circle-icon-button-icon"]
                   [:td border-style-nw (code-text ":icon")]
                   [:td border-style "The button icon."]]]]]]))

(defn md-circle-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[md-circle-icon-button ... ]"
                                "src/re_com/buttons.cljs"
                                "src/re_demo/md_circle_icon_button.cljs"]
                  [h-box
                   :gap "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p-span "Material design icons, and their names, can be " [material-design-hyperlink "found here"] "."]
                                          [args-table md-circle-icon-button-args-desc]]]
                              [v-box
                               :gap "10px"
                               :children [[title2 "Demo"]
                                          [v-box
                                           :gap "15px"
                                           :children [[example-icons selected-icon]
                                                      [gap :size "10px"]
                                                      [p "Here's what the chosen icon looks like in a Circle Icon Button."]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[box :width "90px" :child [:code ":size"]]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :smaller"
                                                                   :size         :smaller
                                                                   :on-click #()]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "No :size set. This is the default button"
                                                                   :on-click     #()]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      ":size set to :larger"
                                                                   :size         :larger
                                                                   :on-click #()]]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :justify  :start
                                                       :children [[box :width "90px" :child [:code ":emphasise?"]]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :emphasise?   true
                                                                   :tooltip      "This button has :emphasise? set to true"
                                                                   :on-click     #()]]]
                                                      [h-box
                                                       :gap      "20px"
                                                       :align    :center
                                                       :children [[box :width "90px" :child [:code ":disabled?"]]
                                                                  [md-circle-icon-button
                                                                   :md-icon-name @selected-icon
                                                                   :tooltip      "This button has :disabled? set to true"
                                                                   :disabled?    true
                                                                   :on-click     #()]]]]]]]]]
                  [md-circle-icon-button-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [md-circle-icon-button-demo])
