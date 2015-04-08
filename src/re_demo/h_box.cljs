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
  [& {:keys [indent1 indent2 on-over editor]}]

  (let  [local-mouse-over  (reagent/atom false)
         mouse-over       (fn [val]
                            (reset! local-mouse-over val)
                            (if on-over (on-over val))
                            nil)
         ;indent1           [:span {:style  {:flex (str "0 0 " indent1)}}]
         ;indent2           [:span {:style  {:flex (str "0 0 " indent2)}}]
         ; value-style       {:style {:flex (str "0 0 80px")}}}
         ]
    (fn [& {:keys [text1 text2 editor]}]
    [h-box
     :attr  {:on-mouse-over  #(mouse-over true)
             :on-mouse-out   #(mouse-over false)}
     :style {:background-color (if @local-mouse-over "#f0f0f0")}
     :children [[gap :size indent1]             ;; leading indent
                [box :size indent2 :child text1]        ;; often the parameter
                [box :size indent2 :child text2]        ;; initial text
                (if editor  editor)
                ]])))

(defn gap-editor
  [over?-ratom path]
  (let [open (reagent/atom false)]
  [:div "hello"]
  ))

(defn editable-code
  "Shows the code in a way that values can be edited, allowing for an interactive demo."
  []
  (let [over-hbox  (fn [over?] )
        over-box1  (fn [over?] )]
    (fn []
      [v-box
       :children [[gap :size "20px"]

                  [v-box
                   :style {:font-family      "Consolas, \"Courier New\", monospace"
                           :background-color "#f5f5f5"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :padding          "8px"}
                   :children [[code-row :on-over over-hbox :indent1 "0px"   :text1 "[h-box"     :indent2 "80px"  :text2  ""  ]
                              [code-row :on-over over-hbox :indent1 "15px"  :text1  ":size"     :indent2 "80px"  :text2  "\"1\"" ]
                              [code-row :on-over over-hbox :indent1 "15px"  :text1  ":gap"      :indent2 "80px"  :text2  "\"1px\""  :editor [gap-editor]]
                              [code-row :on-over over-hbox :indent1 "15px"  :text1  ":children" :indent2 "80px"  :text2  " ["]
                              [code-row :on-over over-box1 :indent1 "25px"  :text1  "[box "     :indent2 "80px"  :text2  ""]
                              [code-row :on-over over-box1 :indent1 "35px"  :text1  ":child "   :indent2 "80px"  :text2  "\"Box1\""]
                              [code-row :on-over over-box1 :indent1 "35px"  :text1  ":size"     :indent2 "80px"  :text2  "\"auto\""]]]]])))



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
                                          [p "This is an intereactive demo.  Edit the \"code\" (in grey) and watch the boxes change."]
                                          [demo]
                                          [editable-code]]]
                              ]]
                  [gap :size "30px"]]]))
