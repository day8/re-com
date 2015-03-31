(ns re-demo.h-box
  (:require [re-com.core     :refer [h-box v-box box gap line scroller border label title button hyperlink-href slider]]
            [re-com.box      :refer [h-box-args-desc v-box-args-desc box-args-desc gap-args-desc line-args-desc scroller-args-desc border-args-desc flex-child-style]]
            [re-com.util     :refer [px]]
            [re-demo.utils   :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [re-com.validate :refer [extract-arg-data string-or-hiccup? alert-type? vector-of-maps?]]
            [reagent.core    :as    reagent]))


(def rounded-panel (merge (flex-child-style "1")
                          {:background-color "#fff4f4"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :margin           "4px"
                           :padding          "8px"}))

(def h-box-style {:border-right  "dashed 1px blue" :overflow "hidden"})
(def v-box-style {:border-bottom "dashed 1px blue" :overflow "hidden"})

(defn panel
  []
  (let [container-size (reagent/atom 500)
        gap-size       (reagent/atom 0)
        box1-size      (reagent/atom 60)
        box2-size      (reagent/atom 100)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[h-box ... ]"
                                [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                                [github-hyperlink "Page Source" "src/re_demo/h_box.cljs"]]]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "h-box is a container which lays out its  " [:code ":children"] " in a single horizontal row."]
                                           [:p
                                            "To understand it fully and use it powerfully, you must have a good understanding of the "
                                            [hyperlink-href
                                             :label "CSS Flexbox"
                                             :href "https://css-tricks.com/snippets/css/a-guide-to-flexbox"
                                             :target "_blank"]
                                            " layout system."]
                                           [:p "The actual layout is a function of the " [:code ":size"] " of the container and the " [:code ":size"] " provided for each of the children."]
                                           [:p "Todo: Nestability with v-box"]
                                           ]
                                          [args-table h-box-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [paragraphs
                                           [:p "Descriptions removed for now."]
                                           ;[:p "The h-box, which is normally invisible, has been styled with a dashed red border to make it visible."]
                                           ;[:p "Each child box component (which includes an 4px magin) describes it's own settings and allows you to modify them."]
                                           #_[:p "Dashed red lines have been added between the boxes."]]
                                          [title :level :level3 :label "Container (h-box/v-box) - red border"]
                                          [h-box
                                           :gap      "10px"
                                           :children [[box :align :start :width "100px" :child [:span "h/v-box " [:code ":w/:h"]]]
                                                      [slider
                                                       :model     container-size
                                                       :min       0
                                                       :max       800
                                                       :width     "200px"
                                                       :on-change #(reset! container-size %)]
                                                      [:span @container-size "px"]]]
                                          [h-box
                                           :gap      "10px"
                                           :children [[box :align :start :width "100px" :child [:span "gap " [:code ":size"]]]
                                                      [slider
                                                       :model     gap-size
                                                       :min       0
                                                       :max       50
                                                       :width     "200px"
                                                       :on-change #(reset! gap-size %)]
                                                      [:span @gap-size "px"]]]
                                          [gap :size "10px"]
                                          [title :level :level3 :label "Children (box) - blue border at end of box"]
                                          [h-box
                                           :gap      "10px"
                                           :children [[box :align :start :width "100px" :child [:span "Box1 " [:code ":size"]]]
                                                      [slider
                                                       :model     box1-size
                                                       :min       0
                                                       :max       100
                                                       :width     "200px"
                                                       :on-change #(reset! box1-size %)]
                                                      [:span @box1-size "%" [:span {:style {:font-size "10px"}} " -- Note: This also sets Box3 to (100 - Box1)"]]]]
                                          [h-box
                                           :gap      "10px"
                                           :children [[box :align :start :width "100px" :child [:span "Box2 " [:code ":size"]]]
                                                      [slider
                                                       :model     box2-size
                                                       :min       0
                                                       :max       500
                                                       :width     "200px"
                                                       :on-change #(reset! box2-size %)]
                                                      [:span @box2-size "px"]]]
                                          [gap :size "10px"]
                                          [h-box
                                           :width    (px @container-size)
                                           :height   "100px"
                                           :gap      (px @gap-size)
                                           :style    {:border "dashed 1px red"}
                                           :children [[box
                                                       :size  (str @box1-size "%")
                                                       :style h-box-style
                                                       :child [:div {:style rounded-panel} "Box 1" [:br] ":size " (str @box1-size "%")]]
                                                      ;[line :size "0px" :color "initial" :style {:border-left "dashed 1px red"}]
                                                      [box
                                                       :size  (px @box2-size)
                                                       :style h-box-style
                                                       :child [:div {:style rounded-panel} "Box 2" [:br] ":size " (px @box2-size)]]
                                                      ;[line :size "0px" :color "initial" :style {:border-left "dashed 1px red"}]
                                                      [box
                                                       :size  (str (- 100 @box1-size) "%")
                                                       :style h-box-style
                                                       :child [:div {:style rounded-panel} "Box 3" [:br] ":size " (str (- 100 @box1-size) "%")]]]]
                                          [paragraphs
                                           [:br]
                                           [:p "Now here is a v-box with exactly the same children."]]
                                          [v-box
                                           :width    "100px"
                                           :height   (px @container-size)
                                           :gap      (px @gap-size)
                                           :style    {:border "dashed 1px red"}
                                           :children [[box
                                                       :size  (str @box1-size "%")
                                                       :style v-box-style
                                                       :child [:div {:style rounded-panel} "Box 1" [:br] ":size " (str @box1-size "%")]]
                                                      ;[line :size "0px" :color "initial" :style {:border-top "dashed 1px red"}]
                                                      [box
                                                       :size  (px @box2-size)
                                                       :style v-box-style
                                                       :child [:div {:style rounded-panel} "Box 2" [:br] ":size " (px @box2-size)]]
                                                      ;[line :size "0px" :color "initial" :style {:border-top "dashed 1px red"}]
                                                      [box
                                                       :size  (str (- 100 @box1-size) "%")
                                                       :style v-box-style
                                                       :child [:div {:style rounded-panel} "Box 3" [:br] ":size " (str (- 100 @box1-size) "%")]]]]]]]]
                  [gap :size "30px"]]])))
