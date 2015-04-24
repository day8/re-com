(ns re-demo.h-box
  (:require [clojure.string  :as    string]
            [re-com.core     :refer [p h-box v-box box gap line scroller border label title button checkbox hyperlink-href
                                     slider horizontal-bar-tabs info-button input-text input-textarea
                                     popover-anchor-wrapper popover-content-wrapper popover-tooltip px] :refer-macros [handler-fn]]
            [re-com.box      :refer [h-box-args-desc v-box-args-desc box-args-desc gap-args-desc line-args-desc scroller-args-desc border-args-desc flex-child-style]]
            [re-com.util     :refer [px]]
            [re-demo.utils   :refer [panel-title title2 args-table github-hyperlink status-text]]
            [re-com.validate :refer [string-or-hiccup? alert-type? vector-of-maps?]]
            [reagent.core    :as    reagent]
            [reagent.ratom   :refer-macros [reaction]]))


(def h-box-style  {;:background-color "yellow"
                   ;:padding          "4px"
                   ;:overflow         "hidden"
                   })

(def panel-style  (merge (flex-child-style "1")
                         {:background-color "#fff4f4"
                          :border           "1px solid lightgray"
                          :border-radius    "4px"
                          :padding          "4px"}))

(def over-style   {:background-color "#fcc"})

(def editor-style {:font-size   "12px"
                   :line-height "20px"
                   :padding     "6px 8px"})

(def current-demo (reagent/atom 0))

(def demos [;; Demo 0
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? true  :editing? (atom false) :range [0 200]}
                    :width      {:value "300px"  :omit? false :editing? (atom false) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false) }
                    :align      {:value :stretch :omit? true  :editing? (atom false) }
                    :gap        {:value "4px"    :omit? true  :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) }
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "50px" :ratio "3" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) }
                    :size       {:value "auto"   :omit? false :editing? (atom false)  :type :auto :px "100px" :ratio "2" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) }
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "150px" :ratio "1" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}}

            ;; Demo 1
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "450px"  :omit? false :editing? (atom true ) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false) }
                    :align      {:value :stretch :omit? true  :editing? (atom false) }
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) }
                    :size       {:value "none"   :omit? false :editing? (atom false) :type :none :px "50px" :ratio "3" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) }
                    :size       {:value "100px"  :omit? false :editing? (atom true )  :type :px :px "100px" :ratio "2" :gsb "1 1 0"}
                    :align-self {:value :center  :omit? false :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) }
                    :size       {:value "1"      :omit? false :editing? (atom false) :type :ratio :px "150px" :ratio "1" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "Simple Demo"]
                               [:p "Demonstrates some of the basics of h-box (and Flexbox)"]
                               [:p "The " [:strong "h-box"] " container has a specific width and height with a gap of 4px, meaning 4px space will be placed between each child. The " [:code ":justify"] " and " [:code ":align"] " parameters are left to their defaults, meaning the boxes will be stretch from top to bottom (unless individually overridden)."]
                               [:p [:strong "Box1"] " has a " [:code ":size"] " of \"none\" which means it will take up as much space as it's content, in this case, the text \"Box1\"."]
                               [:p [:strong "Box2"] " has a specific 100 pixel " [:code ":size"] " so that's exactly how much space (width in this case) it will take up. Notice how the default :align of :stretch is overridden so that this box is vertically centered."]
                               [:p [:strong "Box3"] " has a " [:code ":size"] " of \"1\" which defines how much empty space within the container to consume. It's greedy so it takes everything that's left. Because there are no other siblings with a 'ratio'  :size, it will always take up all available space so you could put any ratio value here and you'll get the same result."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Set the Box2 :size to \"2\" and notice how it will always take up double the width of Box3 as you adjust the h-box :width parameter. " ]
                                [:li "Another fascinating thing to try." ]
                                [:li "More to come. Will need to add a scroller to get more stuff in here!" ]
                                ]]]}

            ;; Demo 2
            {:hbox {:over?      false
                    :height     {:value "200px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "300px"  :omit? false :editing? (atom false) :range [0 1000]}
                    :justify    {:value :center  :omit? false :editing? (atom false) }
                    :align      {:value :stretch :omit? true  :editing? (atom false) }
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) }
                    :size       {:value "none"   :omit? false :editing? (atom false) :type :none :px "50px" :ratio "3" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) }
                    :size       {:value "500px"  :omit? false :editing? (atom false) :type :px :px "100px" :ratio "2" :gsb "1 1 0"}
                    :align-self {:value :center  :omit? false :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) }
                    :size       {:value "none"   :omit? false :editing? (atom false) :type :none :px "150px" :ratio "1" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p "This is box-state 2"]
                               [:p "More " [:strong "incredible"] " descriptions coming soon to a screen near you. Well, this one actually."]]]}

            ;; Demo 3
            {:hbox {:over?      false
                    :height     {:value "150px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "1000px" :omit? false :editing? (atom false) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false) }
                    :align      {:value :stretch :omit? true  :editing? (atom false) }
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1 Box1 Box1 Box1 Box1 Box1 Box1 Box1 Box1 " :omit? false :editing? (atom false) }
                    :size       {:value "none"   :omit? false :editing? (atom false) :type :none :px "50px" :ratio "3" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 Box2 " :omit? false :editing? (atom false) }
                    :size       {:value "100px"  :omit? false :editing? (atom false) :type :px :px "100px" :ratio "2" :gsb "1 1 0"}
                    :align-self {:value :center  :omit? false :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3 Box3 Box3 Box3 Box3 Box3 " :omit? false :editing? (atom false) }
                    :size       {:value "1"      :omit? false :editing? (atom false) :type :ratio :px "150px" :ratio "1" :gsb "1 1 0"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false) }
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p "This is box-state 3"]
                               [:p "More " [:strong "incredible"] " descriptions coming soon to a screen near you. Well, this one actually."]]]}])

(def box-state  (reaction (get demos @current-demo)))

(def show-desc? (reagent/atom true))

(defn merge-named-params
  "given a hiccup vector v, and a map m containing named parameters, add the named parameters to v
   the values in the map, are in turn maps
   if, in the value map, :omit? is true, omit this parameter altogether
   otherwise use :value for the value of the parameter
   Example:
      (merge-named-params [box :a 1] {:b {:value 2} :c {:value 3 :omit? true}})
      ;; =>  [box :a 1 :b 2]
  "
  [v m]
  (let [m      (remove (comp :omit? second) m)
        names  (keys m)
        values (map :value (vals m))]
    (into v (interleave names values))))

(defn make-box
  "produces something like:
     [box
        :size      \"0 1 100px\"
        :style     h-box-style
        :min-width \"200px\"
        :child [:div {:style rounded-panel} \"Box 1\"]]
  "
  [box-parameters]
  (let [over? (:over? box-parameters)]
    (-> [box :style h-box-style]
        (merge-named-params (dissoc box-parameters :over? :text))
        (conj :child)
        (conj [:div {:style (merge panel-style
                                   (when over? over-style))} (get-in box-parameters [:text :value])]))))

(defn close-button
  "close button used for all the editors"
  [on-close]
  [button
   :label    [:i {:class "md-close"
                  :style {:font-size "20px"
                          :margin-left "8px"}}]
   :on-click #(on-close)
   :class    "close"])

(defn px-editor
  "provides a single slider to edit pixel value in the state atom"
  [path]
  (let [model     (reaction (js/parseInt (get-in @box-state (conj path :value))))
        [min max] (get-in @box-state (conj path :range))]
    (fn [path on-close]
      [h-box
       :align    :center
       :children [[slider
                   :model     model
                   :min       min
                   :max       max
                   :width     "200px"
                   :on-change #(swap! box-state assoc-in (conj path :value) (px %))]
                  [close-button on-close]]])))

(defn justify-editor
  "provides horizontal bar tabs to set the :justify value in the state atom"
  [path]
  (let [opts  [{:id :start   :label ":start"}
               {:id :end     :label ":end"}
               {:id :center  :label ":center"}
               {:id :between :label ":between"}
               {:id :around  :label ":around"}]
        model (reaction (get-in @box-state (conj path :value)))]
    (fn
      [path on-close]
      [h-box
       :align    :center
       :children [[horizontal-bar-tabs
                   :model     model
                   :tabs      opts
                   :style     editor-style
                   :on-change #(swap! box-state assoc-in (conj path :value) %)]
                  [close-button on-close]]])))

(defn align-editor
  "provides horizontal bar tabs to set the :align OR :align-self values in the state atom"
  [path]
  (let [opts  [{:id :start    :label ":start"}
               {:id :end      :label ":end"}
               {:id :center   :label ":center"}
               {:id :baseline :label ":baseline"}
               {:id :stretch  :label ":stretch"}]
        model (reaction (get-in @box-state (conj path :value)))]
    (fn
      [path on-close]
      [h-box
       :align    :center
       :children [[horizontal-bar-tabs
                   :model     model
                   :tabs      opts
                   :style     editor-style
                   :on-change #(swap! box-state assoc-in (conj path :value) %)]
                  [close-button on-close]]])))

(defn text-editor
  "provides an input-text to edit strings in the state atom"
  [path]
  (let [model (reaction (get-in @box-state (conj path :value)))]
    (fn
      [path on-close]
      [h-box
       :align    :center
       :children [[input-text
                   :model           model
                   :change-on-blur? false
                   :style           editor-style
                   :on-change       #(swap! box-state assoc-in (conj path :value) %)]
                  [close-button on-close]]])))

(defn box-size
  "works out what to pass to :size from a map like {:value \"none\" :omit? false :type :none :px \"\100px\" :ratio \"1\" :gsb \"\"}"
  [size-spec]
  (cond (= (:type size-spec) :px)    (:px size-spec)
        (= (:type size-spec) :ratio) (:ratio size-spec)
        (= (:type size-spec) :gsb)   (:gsb size-spec)
        :else                        (name (:type size-spec))))

(defn size-editor
  "a more complex component to edit :size values in the state atom"
  [path]
  (let [opts         [{:id :inital :label "initial"}
                      {:id :auto   :label "auto"}
                      {:id :none   :label "none"}
                      {:id :px     :label "px"}
                      {:id :ratio  :label "ratio"}
                      {:id :gsb    :label "g s b"}]
        model        (reaction (get-in @box-state (conj path :type)))
        px-model     (reaction (js/parseInt (get-in @box-state (conj path :px))))
        ratio-model  (reaction (js/parseInt (get-in @box-state (conj path :ratio))))
        gsb-model    (reaction (get-in @box-state (conj path :gsb)))
        update-model (fn [path item new-model]
                       (swap! box-state assoc-in (conj path item) new-model)
                       (swap! box-state assoc-in (conj path :value) (box-size (get-in @box-state path))))
        size-status  (reagent/atom nil)]
    (fn
      [path on-close]
      [h-box
       :align    :center
       :children [[horizontal-bar-tabs
                   :model     model
                   :tabs      opts
                   :style     editor-style
                   :on-change #(do (update-model path :type %)
                                   (reset! size-status nil))]
                  (when (contains? #{:px :ratio :gsb} @model)
                    [gap :size "8px" :width "8px"])
                  (when (= @model :px)
                    [slider
                     :model     px-model
                     :min       0
                     :max       800
                     :width     "200px"
                     :style     editor-style
                     :on-change #(update-model path :px (px %))])
                  (when (= @model :ratio)
                    [slider
                     :model     ratio-model
                     :min       0
                     :max       10
                     :width     "200px"
                     :style     editor-style
                     :on-change #(update-model path :ratio (str %))])
                  (when (= @model :gsb)
                    [input-text
                     :model           gsb-model
                     :change-on-blur? false
                     :status          @size-status
                     :status-icon?    true
                     :status-tooltip  "Ignored - please enter 1 or 3 values"
                     :width           "200px"
                     :style           editor-style
                     :on-change       #(let [valid? (contains? #{1 3} (count (string/split (string/trim %) #"\s+")))]
                                        (if valid?
                                          (do (reset! size-status nil)
                                              (update-model path :gsb %))
                                          (reset! size-status :warning)))])
                  [close-button on-close]]])))

(defn indent-px
  [ident]
  (ident {:0  "0px"
          :1  "14px"
          :2  "28px"
          :3  "42px"}))

(defn code-row
  "Render a single code row consisting of:
    - up to three pieces of text, typically:
      1. parameter name
      2. parameter value, specified as a path into the state atom
      3. end text, usually, a closing ']'
    - an editor to open"
  [active? indent text1 path text3 on-over editor]
  (let  [editing?-path    (when editor (conj path :editing?))
         toggle-editor    (handler-fn (swap! box-state update-in editing?-path (fn [v] (atom (not @v)))))
         mouse-over-row?  (reagent/atom false)
         mouse-over-fn    (fn [val]
                            (reset! mouse-over-row? val)
                            (if on-over (on-over val))
                            nil)
         omit?            (reaction (and (vector? path)
                                         (map? (get-in @box-state path))
                                         (get-in @box-state (conj path :omit?))))]
    (fn [active? indent text1 path text2 on-over editor]
      (let [arg-val           (when editor
                                (let [val (get-in @box-state (conj path :value))]
                                  (cond
                                    (nil? val)     "-"
                                    (keyword? val) (str val)
                                    :else          (str "\"" val "\""))))
            row-active?       (and @mouse-over-row? active?)
            mouse-over-group? (= (nth path 0) (:over-group @box-state))
            show-checkbox?    (and row-active? (not (contains? (set path) :text)))
            allow-edit?       (and row-active? (not @omit?))
            arg-hiccup        [h-box
                               :width     "242px"
                               :style    (merge {:overflow "hidden"}
                                                (when mouse-over-group? {:background-color "#e8e8e8"})
                                                (when row-active?       {:background-color "#d8d8d8"
                                                                         :cursor           "pointer"})
                                                (when @omit?            {:color            "#c0c0c0"}))
                               :attr     {:on-mouse-over #(do (println "|" path "|") (mouse-over-fn true))
                                          :on-mouse-out  #(mouse-over-fn false)}
                               :children [[box
                                           :size "20px"
                                           :child (if show-checkbox?
                                                    [checkbox
                                                     :model     (not @omit?)
                                                     :on-change #(do (swap! box-state assoc-in (conj path :omit?) (not %))
                                                                     (swap! box-state assoc-in editing?-path (atom %)))]
                                                    [:span])] ;; when no checkbox, use a filler
                                          [gap :size (indent-px indent)]
                                          [box
                                           :size "100px"
                                           :attr  (when allow-edit? {:on-click toggle-editor})
                                           :style (when @omit? {:text-decoration "line-through"})
                                           :child text1]
                                          [box
                                           :attr  (when allow-edit? {:on-click toggle-editor})
                                           :style (when @omit? {:text-decoration "line-through"})
                                           :child [:span
                                                   [:span {:style (when allow-edit? {:color "blue"})} arg-val]
                                                   text2]]]]]
        (if editor
          (let [editing? (get-in @box-state editing?-path)]
            [popover-anchor-wrapper
             :showing? editing?
             :position :right-center
             :anchor   arg-hiccup
             :popover  [popover-content-wrapper
                        :showing? editing?
                        :position :right-center
                        :body     [editor path #(swap! box-state assoc-in editing?-path (atom false))]]])
          arg-hiccup)))))


(defn choose-a-demo
  "choose a demo to show"
  []
  (let [opts  [{:id 0 :label "1"}
               {:id 1 :label "2"}
               {:id 2 :label "3"}
               {:id 3 :label "4"}]]
    (fn
      []
      [h-box
       :gap      "8px"
       :align    :center
       :children [[:span "Demos:"]
                  [horizontal-bar-tabs
                   :model     current-demo
                   :tabs      opts
                   :on-change #(do (reset! current-demo %)
                                   (reset! show-desc? true))]]])))

(defn demo
  "creates the hiccup for the actual demo, with its child boxes and all"
  []
  (let [over? (:over? (:hbox @box-state))]
    (-> [h-box
         :padding "4px"
         :style (merge {:border "dashed 1px red"}
                       (when over? over-style))]
        (merge-named-params (dissoc (:hbox @box-state) :over?))
        (conj :children)
        (conj [(make-box (:box1 @box-state))
               (make-box (:box2 @box-state))
               (make-box (:box3 @box-state))]))))

(defn editable-code
  "Shows the code in a way that values can be edited, allowing for an interactive demo"
  []
  (let [over-hbox  (fn [over?] (println "hbox" over?) (swap! box-state assoc-in [:hbox :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :hbox)))
        over-box1  (fn [over?] (println "box1" over?) (swap! box-state assoc-in [:box1 :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :box1)))
        over-box2  (fn [over?] (println "box2" over?) (swap! box-state assoc-in [:box2 :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :box2)))
        over-box3  (fn [over?] (println "box3" over?) (swap! box-state assoc-in [:box3 :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :box3)))]
    (fn []
      [h-box
       :align :start
       :children [(when (:desc @box-state)
                    #_[popover-anchor-wrapper  ;; TODO: REMOVE
                     :showing? show-desc?
                     :position :right-below
                     :anchor   [:div {:style {:height "60px"}}] ;; Position the popover down the page a little
                     :popover  [popover-content-wrapper
                                :showing?       show-desc?
                                :position       :right-below
                                :width          "460px"

                                ;:tooltip-style? true
                                ;:popover-color "#333333"
                                ;:padding        "3px 8px"
                                ;:arrow-length   6
                                ;:arrow-width    12

                                :title          "Demo Description"
                                :close-button?  true
                                :body           (:desc @box-state)]]
                    [popover-tooltip
                     :showing?      show-desc?
                     :position      :left-below
                     :width         "460px"
                     :status        :info
                     :close-button? true
                     :anchor        [:div {:style {:height "42px"}}] ;; Position the popover down the page a little
                     :label         (:desc @box-state)])
                  [v-box
                   :width "260px"
                   :style {:font-family      "Consolas, \"Courier New\", monospace"
                           :font-size        "12px"
                           :background-color "#f5f5f5"
                           :border           "1px solid lightgray"
                           :border-radius    "4px"
                           :padding          "8px"}
                   :children [[code-row false :0 "[h-box"      [:hbox]                  ""   over-hbox]
                              [code-row true  :1 ":height"     [:hbox :height]     ""   over-hbox px-editor]
                              [code-row true  :1 ":width"      [:hbox :width]      ""   over-hbox px-editor]
                              [code-row true  :1 ":justify"    [:hbox :justify]    ""   over-hbox justify-editor]
                              [code-row true  :1 ":align"      [:hbox :align]      ""   over-hbox align-editor]
                              [code-row true  :1 ":gap"        [:hbox :gap]        ""   over-hbox px-editor]
                              [code-row false :1 ":children [" [:hbox]                  ""   over-hbox]

                              [code-row false :2 "[box "       [:box1]                  ""   over-box1]
                              [code-row true  :3 ":child"      [:box1 :text]       ""   over-box1 text-editor]
                              [code-row true  :3 ":size"       [:box1 :size]       ""   over-box1 size-editor]
                              [code-row true  :3 ":align-self" [:box1 :align-self] ""   over-box1 align-editor]
                              [code-row true  :3 ":height"     [:box1 :height]     ""   over-box1 px-editor]
                              [code-row true  :3 ":min-width"  [:box1 :min-width]  ""   over-box1 px-editor]
                              [code-row true  :3 ":max-width"  [:box1 :max-width]  "]"  over-box1 px-editor]

                              [code-row false :2 "[box "       [:box2]                  ""   over-box2]
                              [code-row true  :3 ":child"      [:box2 :text]       ""   over-box2 text-editor]
                              [code-row true  :3 ":size"       [:box2 :size]       ""   over-box2 size-editor]
                              [code-row true  :3 ":align-self" [:box2 :align-self] ""   over-box2 align-editor]
                              [code-row true  :3 ":height"     [:box2 :height]     ""   over-box2 px-editor]
                              [code-row true  :3 ":min-width"  [:box2 :min-width]  ""   over-box2 px-editor]
                              [code-row true  :3 ":max-width"  [:box2 :max-width]  "]"  over-box2 px-editor]

                              [code-row false :2 "[box "       [:box3]                  ""   over-box3]
                              [code-row true  :3 ":child"      [:box3 :text]       ""   over-box3 text-editor]
                              [code-row true  :3 ":size"       [:box3 :size]       ""   over-box3 size-editor]
                              [code-row true  :3 ":align-self" [:box3 :align-self] ""   over-box3 align-editor]
                              [code-row true  :3 ":height"     [:box3 :height]     ""   over-box3 px-editor]
                              [code-row true  :3 ":min-width"  [:box3 :min-width]  ""   over-box3 px-editor]
                              [code-row true  :3 ":max-width"  [:box3 :max-width]  "]]" over-box3 px-editor]]]]])))


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
                                        [p "The " [:span.bold "Layout"] " page (look LHS) describes the importance of " [:span.bold ":size"] ". The actual layout is a function of the " [:code ":size"] " of the container and the " [:code ":size"] " provided for each of the children."]
                                        [p [:strong "Todo: Nestability with v-box"]]
                                        [args-table h-box-args-desc]]]
                            [v-box
                             :gap      "10px"
                             :width    "500px"
                             :height   "800px"
                             ;:style    {:border "dashed 1px #ddd"}
                             :children [[title2 "Demo"]
                                        [p "In this interactive demo, you can edit the hiccup in the grey box to see the effects on the h-box and three children in the red border."]
                                        [choose-a-demo]
                                        [gap :size "0px"]
                                        [demo]
                                        [gap :size "0px"]
                                        [editable-code]]]]]]]))
