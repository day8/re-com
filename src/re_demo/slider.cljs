(ns re-demo.slider
  (:require [re-com.core   :refer [h-box v-box box gap line label title slider checkbox input-text horizontal-bar-tabs vertical-bar-tabs p]]
            [re-com.misc   :refer [slider-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]
            [reagent.core  :as    reagent]))

(defn slider-component-hierarchy
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
                [:pre "[slider\n"
                      "   ...\n"
                      "   :parts {:wrapper {:class \"blah\"\n"
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
                   [:td border-style-nw (indent-text 0 "[slider]")]
                   [:td border-style-nw "rc-slider-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the slider."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:input]")]
                   [:td border-style-nw "rc-slider"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The actual input field."]]]]]]))

(defn slider-demo
  []
  (let [slider-val  (reagent/atom "0")
        slider-min  (reagent/atom "0")
        slider-max  (reagent/atom "100")
        slider-step (reagent/atom "1")
        disabled?   (reagent/atom false)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[slider ... ]"
                                "src/re_com/misc.cljs"
                                "src/re_demo/slider.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "Standard HTML5 slider control."]
                                          [args-table slider-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [v-box
                                           :gap      "20px"
                                           :children [[slider
                                                       :model     slider-val
                                                       :min       slider-min
                                                       :max       slider-max
                                                       :step      slider-step
                                                       :width     "300px"
                                                       :on-change #(reset! slider-val (str %))
                                                       :disabled? disabled?]
                                                      [gap :size "0px"]
                                                      [title :level :level3 :label "Parameters"]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":model"]]
                                                                  [input-text
                                                                   :model           slider-val
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-val %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":min"]]
                                                                  [input-text
                                                                   :model           slider-min
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-min %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":max"]]
                                                                  [input-text
                                                                   :model           slider-max
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-max %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[box
                                                                   :align :start
                                                                   :width "60px"
                                                                   :child [:code ":step"]]
                                                                  [input-text
                                                                   :model           slider-step
                                                                   :width           "60px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-step %)
                                                                   :change-on-blur? false]]]
                                                      [checkbox
                                                       :label [box :align :start :child [:code ":disabled?"]]
                                                       :model disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]
                  [slider-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [slider-demo])
