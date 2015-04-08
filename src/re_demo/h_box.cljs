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
                   {:id :%      :label "num"}])

(def demo-state (reagent/atom
              {:hbox {:width   "500px"
                     :height  "100px"
                     :justify :start
                     :gap     "4px"}
               :box1 {:size "none"
                      ; :min-width "200px"
                      ; :height  "200px"
                      :align-self :center
                      :text "Box1"}
               :box2 {:size "0 1 50px"
                      :text "Box2"}
               :box3 {:size "0 1 100px"
                      :text "Box3"}
               :box4 {:size "1 1 100px"
                     :text "Box4"}}))

(defn merge-named-params
  "given a hiccup vector v, and a map m containing named parameters, add the named parameters to v
      (merge-named-params [box :a 1] {:b 2  :c 3})
      ;; =>  [box :a 1 :b 2  :c 3]
  "
  [v m]
  (->> m
       (filter second)        ;; remove nil valued members
       (reduce concat [])
       (into v)
       vec))


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
  "creates the hiccup for the real demo, with its child boxes and all"
  []
  (-> [h-box
       :padding  "4px"
       :style {:border "dashed 1px red"}]
      (merge-named-params (:hbox @demo-state))
      (conj :children)
      (conj [(make-box (:box1 @demo-state))
             (make-box (:box2 @demo-state))
             (make-box (:box3 @demo-state))
             (make-box (:box4 @demo-state))])))

(defn code-row
  [indent text & args]
  (let  [mouse-over   (reagent/atom false)]
    (fn [indent text & args]
    [h-box
     :attr  {:on-mouse-over  #(do (reset! mouse-over true) (println "over") nil)
             :on-mouse-out   #(do (reset! mouse-over false) nil)
             }
     :style {:background-color (if @mouse-over "#f0f0f0")}
     :children [[gap :size (str (* indent 10) "px")]   ;; leading indent
                [:span {:style {:flex "0 0 100px"}} text]
                args]])))


(defn editable-code
  "Shows the code in a way that values can be edited, allowing for an interactive demo."
  []
  [v-box
   :children [[gap :size "20px"]
              [p "the demo above is produced by the code below"]
              [v-box
               :style {:font-family      "Consolas, \"Courier New\", monospace"
                       :background-color "#f5f5f5"
                       :border           "1px solid lightgray"
                       :border-radius    "4px"
                       :padding          "8px"}
               :children [[code-row 0 "[h-box"        ]
                          [code-row 2 "  :size"      "\"1\"" ]
                          [code-row 2 "  :gap"     "\"1px\"" ]
                          [code-row 2 "  :children"  " ["]]]]])



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

                                          [p "The " [:span.bold "Layout"] " page (look LHS) describes the importance of " [:span.bold ":size"] "The actual layout is a function of the " [:code ":size"] " of the container and the " [:code ":size"] " provided for each of the children."]
                                          [p "Todo: Nestability with v-box"]

                                          [args-table h-box-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :width    "500px"
                               :children [[title2 "Demo"]
                                          [demo]
                                          [editable-code]]]
                              ]]
                  [gap :size "30px"]]]))
