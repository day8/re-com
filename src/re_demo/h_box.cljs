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

(def box-state (reagent/atom
              {:hbox {:width   "500px"
                     :height  "100px"
                     :justify :start
                     :gap     "4px"}
               :box1 {:size "none"
                      ; :min-width "200px"
                      ; :height  "200px"
                      :text "Box1"}
               :box2 {:size "0 1 50px"
                      :text "Box2"
                      :align-self :center}
               :box3 {:size "0 1 100px"
                      :text "Box3"}}))

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
      (merge-named-params (:hbox @box-state))
      (conj :children)
      (conj [(make-box (:box1 @box-state))
             (make-box (:box2 @box-state))
             (make-box (:box3 @box-state)) ])))


(defn gap-editor
  [overrow? path]
  (let [open (reagent/atom false)]

    ))

(defn editor-button
  [mouse-over-row? open?]

  [box
   :size "100px"
   :align-self  :center
   :justify :center
   :child  [:div {:class "md-play-arrow rc-icon-smaller"
                  :style {:color "lightgrey"
                          :XXXX 1}}]])

(defn indent-px
  [ident]
  (ident {:0  "0px"
          :1  "15px"
          :2  "25px"
          :3  "35px"}))

(defn code-row
  "A code row consists of:
    - up to three pieces of text (all optional), typically:
      1. parameter name
      2. parameter value
      3. rarely, a closing ']'
    - an editor openn"
  [indent text1 text2 text3 on-over editor]

  (let  [mouse-over-row?  (reagent/atom false)
         mouse-over-fn    (fn [val]
                            (reset! mouse-over-row? val)
                            (if on-over (on-over val))
                            nil)
         editor-open?     (reagent/atom false)
         ]
    (fn [indent text1 text2 text3 on-over editor]
      [h-box
       :attr  {:on-mouse-over  #(mouse-over-fn true)
               :on-mouse-out   #(mouse-over-fn false)}
       :style {:background-color (if @mouse-over-row? "#f0f0f0")}
       :children [[gap :size (indent-px indent)]          ;; leading indent
                  [box :size "100px" :child text1]        ;; often the parameter
                  [box :size "100px"  :child text2]        ;; often the parameter value
                  [box :size "5px"    :child text3]        ;; often the parameter value
                  #_(if editor  editor)
                  ]])))


(defn editable-code
  "Shows the code in a way that values can be edited, allowing for an interactive demo."
  []
  (let [over-hbox  (fn [over?] )
        over-box1  (fn [over?] )
        over-box2  (fn [over?] )]
    (fn []
      [v-box
       :children [[gap :size "20px"]

                  [v-box
                   :style {:font-family      "Consolas, \"Courier New\", monospace"
                           :background-color "#f5f5f5"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :padding          "8px"}
                   :children [[code-row :0 "[h-box"        ""          ""  over-hbox   :indent :0   :indent2 "80px"     ]
                              [code-row :1 "  :size"       "\"500px\"" ""  over-hbox   :indent :1  :indent2 "100px"   ]
                              [code-row :1 "  :gap"        "\"1px\""   ""  over-hbox   :indent :1  :indent2 "100px"    :editor [gap-editor]]
                              [code-row :1 "  :children"   " ["        ""  over-hbox   :indent :1  :indent2 "100px"   ]

                              [code-row :2 "[box "          ""          ""  over-box1]
                              [code-row :3 "  :child"       "\"Box1\""  ""  over-box1]
                              [code-row :3 "  :size"        "\"auto\""  ""  over-box1]
                              [code-row :3 "  :align-self"  ":center"   ""  over-box1]
                              [code-row :3 "  :height"      "\"50px\""  ""  over-box1]
                              [code-row :3 "  :min-width"   "100px"     "]" over-box1]

                              [code-row :2 "[box "          ""          ""  over-box2]
                              [code-row :3 "  :child"       "\"Box2\""  ""  over-box2]
                              [code-row :3 "  :size"        "\"auto\""  ""  over-box2]
                              [code-row :3 "  :align-self"  ":center"   ""  over-box2]
                              [code-row :3 "  :height"      "\"50px\""  ""  over-box2]
                              [code-row :3 "  :min-width"   "100px"     "]" over-box2]

                              [code-row :0  "]"  "" "" over-hbox   ]
                              ]]]])))



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
                                          [p "An interactive demo is coming ..."]
                                          #_[p "This is an intereactive demo.  Edit the \"code\" (in grey) and watch the boxes change. The red-dashed box is an h-box whch contains up to four children."]
                                          #_[demo]
                                          #_[editable-code]]]
                              ]]
                  [gap :size "30px"]]]))
