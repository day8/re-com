(ns re-demo.h-box
  (:require [re-com.core     :refer [p h-box v-box box gap line scroller border label title button checkbox hyperlink-href slider horizontal-bar-tabs]]
            [re-com.box      :refer [h-box-args-desc v-box-args-desc box-args-desc gap-args-desc line-args-desc scroller-args-desc border-args-desc flex-child-style]]
            [re-com.util     :refer [px]]
            [re-demo.utils   :refer [panel-title title2 args-table github-hyperlink status-text]]
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

(def size-options [{:id :inital :label "initial"}
                   {:id :auto   :label "auto"}
                   {:id :none   :label "none"}
                   {:id :px     :label "px"}
                   {:id :%      :label "%"}])

(defn box-controls
  [box-name control]
  [h-box
   :align    :center
   :width    "630px"
   :gap      "8px"
   :padding  "4px"
   :style    {:background-color "#f8f8f8"}
   :children [[box :align :start :child [:span box-name " " [:code ":size"]]]
              [checkbox
               :model     (:show? @control)
               :on-change #(swap! control assoc :show? %)]
              [horizontal-bar-tabs
               :model     (:size @control)
               :tabs      size-options
               :on-change #(swap! control assoc :size %)]
              (when (= (:size @control) :%)
                [h-box
                 :gap "5px"
                 :children [[slider
                             :model     (:% @control)
                             :min       0
                             :max       100
                             :width     "200px"
                             :on-change #(swap! control assoc :% %)]
                            [:span (str (:% @control) "%")]]])
              (when (= (:size @control) :px)
                [h-box
                 :gap "5px"
                 :children [[slider
                             :model     (:px @control)
                             :min       0
                             :max       500
                             :width     "200px"
                             :on-change #(swap! control assoc :px %)]
                            [:span (str (:px @control) "px")]]])]])

(defn box-size
  "Works out what to pass to :size from a map like: {:size :%  :px 100 :% 60 :show? true}"
  [control]
  (cond (= (:size control) :%)  (str (:% control) "%")
        (= (:size control) :px) (str (:px control) "px")
        :else                   (name (:size control))))

  (defn panel
  []
  (let [container-size (reagent/atom 500)
        gap-size       (reagent/atom 0)
        box1-db        (reagent/atom {:size :%  :px 100 :% 60 :show? true})
        box2-db        (reagent/atom {:size :px :px 100 :% 50 :show? true})
        box3-db        (reagent/atom {:size :%  :px 100 :% 40 :show? true})]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[h-box ... ]"
                                "src/re_com/box.cljs"
                                "src/re_demo/h_box.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "h-box is a container which lays out its  " [:code ":children"] " in a single horizontal row."]
                                          [p
                                           "To understand it fully and use it powerfully, you must have a good understanding of the "
                                           [hyperlink-href
                                            :label "CSS Flexbox"
                                            :href "https://css-tricks.com/snippets/css/a-guide-to-flexbox"
                                            :target "_blank"]
                                           " layout system."]
                                          [p "The actual layout is a function of the " [:code ":size"] " of the container and the " [:code ":size"] " provided for each of the children."]
                                          [p "Todo: Nestability with v-box"]

                                          [args-table h-box-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [p "Descriptions removed for now."]
                                          ;[p "The h-box, which is normally invisible, has been styled with a dashed red border to make it visible."]
                                          ;[p "Each child box component (which includes an 4px magin) describes it's own settings and allows you to modify them."]
                                          #_[p "Dashed red lines have been added between the boxes."]
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
                                          [box-controls "Box1" box1-db]
                                          [box-controls "Box2" box2-db]
                                          [box-controls "Box3" box3-db]
                                          [gap :size "10px"]
                                          [h-box
                                           :width    (px @container-size)
                                           :height   "100px"
                                           :gap      (px @gap-size)
                                           :style    {:border "dashed 1px red"}
                                           :children [(when (:show? @box1-db)
                                                        [box
                                                         :size (box-size @box1-db)
                                                         :style h-box-style
                                                         :child [:div {:style rounded-panel} "Box 1" [:br] ":size " (box-size @box1-db)]])
                                                      (when (:show? @box2-db)
                                                        [box
                                                         :size (box-size @box2-db)
                                                         :style h-box-style
                                                         :child [:div {:style rounded-panel} "Box 2" [:br] ":size " (box-size @box2-db)]])
                                                      (when (:show? @box3-db)
                                                        [box
                                                         :size (box-size @box3-db)
                                                         :style h-box-style
                                                         :child [:div {:style rounded-panel} "Box 3" [:br] ":size " (box-size @box3-db)]])]]

                                          [:br]
                                          [p "Now here is a v-box with exactly the same children."]
                                          [v-box
                                           :width    "100px"
                                           :height   (px @container-size)
                                           :gap      (px @gap-size)
                                           :style    {:border "dashed 1px red"}
                                           :children [
                                                      (when (:show? @box1-db)
                                                        [box
                                                         :size (box-size @box1-db)
                                                         :style v-box-style
                                                         :child [:div {:style rounded-panel} "Box 1" [:br] ":size " (box-size @box1-db)]])
                                                      (when (:show? @box2-db)
                                                        [box
                                                         :size (box-size @box2-db)
                                                         :style v-box-style
                                                         :child [:div {:style rounded-panel} "Box 2" [:br] ":size " (box-size @box2-db)]])
                                                      (when (:show? @box3-db)
                                                        [box
                                                         :size (box-size @box3-db)
                                                         :style v-box-style
                                                         :child [:div {:style rounded-panel} "Box 3" [:br] ":size " (box-size @box3-db)]])]]]]]]
                  [gap :size "30px"]]])))
