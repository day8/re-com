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
                           :padding          "4px"}))

(def h-box-style {}) ;{ :overflow "hidden"})

(def size-options [{:id :inital :label "initial"}
                   {:id :auto   :label "auto"}
                   {:id :none   :label "none"}
                   {:id :px     :label "px"}
                   {:id :%      :label "%"}])

(def config (reagent/atom
              {:hbox {:width   "500px"
                     :height  "100px"
                     :justify :start
                     :gap     "4px"}
               :box1 {:size "auto"
                      :min-width "200px"
                      ; :height  "20px"
                      :align-self :end
                      :text "Box1"}
               :box2 {:size "0 1 100px"

                      :text "Box2"}
               :box3 {:size "0 1 100px"
                      :text "Box3"}
               :box4 {:size "0 1 100px"
                     :text "Box4"}}))

(defn merge-named-params
  "given a hiccup vector v, and a map m containing named parameters, add the named parameters to v
      (merge-named-params [box :a 1] {:b 2  :c 3})
      ;; =>  [box :a 1 :b 2  :c 3]
  "
  [v m]
  (let [not-nil-value #(identity (second %))]
    (->> m
         (filter not-nil-value)
         (reduce concat [])
         (into v)
         vec)))


(defn make-box
  "produces something like:
     [box
        :size      \"0 1 100px\"
        :style     h-box-style
        :min-width \"200px\"
        :child [:div {:style rounded-panel} \"Box 1\"]]
  "
  [box-parameters]
  (-> [box :style h-box-style]
      (merge-named-params  (dissoc box-parameters :text))
      (conj :child)
      (conj [:div {:style rounded-panel} (:text box-parameters)])))


(defn demo
  []
  [v-box
   :gap      "10px"
   :children [[title2 "Demo"]
              (-> [h-box
                    :padding  "4px"
                    :style {:border "dashed 1px red"}]
                  (merge-named-params (:hbox @config))
                  (conj :children)
                  (conj [(make-box (:box1 @config))
                         (make-box (:box2 @config))
                         (make-box (:box3 @config))
                         (make-box (:box4 @config))]))]])



#_(defn demo
  []
  (let [container-size (reagent/atom 500)
        gap-size       (reagent/atom 0)
        box1-db        (reagent/atom {:size :%  :px 100 :% 60 :show? true})
        box2-db        (reagent/atom {:size :px :px 100 :% 50 :show? true})
        box3-db        (reagent/atom {:size :%  :px 100 :% 40 :show? true})]
    [v-box
     :gap      "10px"
     :children [[title2 "Demo"]
                #_[p "Descriptions removed for now."]
                [p "An h-box is normally invisible, but for this demo we've styled it with a dashed red border."]
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
                ]]))


  (defn panel
    []

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
                              [demo]]]
                  [gap :size "30px"]]]))
