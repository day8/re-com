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
                          :padding          "0px"
                          :overflow         "hidden"}))

(def over-style   {:background-color "#fcc"})

(def editor-style {:font-size   "12px"
                   :line-height "20px"
                   :padding     "6px 8px"})

(def current-demo (reagent/atom 0))

;(def paragraph-filler "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
(def paragraph-filler [v-box
                       :children [[:p.bold "Lorem Ipsum"]
                                  [:p "dolor sit amet, consectetur adipiscing elit."]
                                  [:p "Sed do " [:strong "eiusmod"] " tempor incididunt ut labore et dolore magna aliqua."]
                                  [:p [:strong "Ut enim "] " ad minim veniam " [:code "quis nostrud"] " exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."]
                                  [:p.bold "Duis aute irure:"]
                                  [:ul
                                   [:li "Dolor in reprehenderit in voluptate velit esse."]
                                   [:li "Cillum dolore eu fugiat nulla pariatur."]
                                   [:li "Excepteur sint occaecat cupidatat non proident."]]]])

(def buttons-filler [h-box
                     :children [[button
                                 :label    "Blue"
                                 :class    "btn-primary"
                                 :on-click #()]
                                [gap :size "12px" :width "12px"]
                                [button
                                 :label    "White"
                                 :on-click #()]]])

(def demos [;; Basic
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "300px"  :omit? true  :editing? (atom false) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false)}
                    :align      {:value :stretch :omit? true  :editing? (atom false)}
                    :gap        {:value "4px"    :omit? true  :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) :type :text :text "Box1"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "50px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) :type :text :text "Box2"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "100px" :ratio "2" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) :type :text :text "Box3"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "150px" :ratio "1" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}}

            ;; Justify
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "450px"  :omit? false :editing? (atom false) :range [0 1000]}
                    :justify    {:value :start   :omit? false :editing? (atom true )}
                    :align      {:value :stretch :omit? true  :editing? (atom false)}
                    :gap        {:value "4px"    :omit? true  :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) :type :text :text "Box1"}
                    :size       {:value "100px"  :omit? false :editing? (atom false) :type :px :px "100px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) :type :text :text "Box2"}
                    :size       {:value "100px"  :omit? false :editing? (atom false) :type :px :px "100px" :ratio "2" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) :type :text :text "Box3"}
                    :size       {:value "100px"  :omit? false :editing? (atom false) :type :px :px "100px" :ratio "1" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "The " [:code ":justify"] " parameter"]
                               [:p "Specifies how children (the three boxes) are arranged horizontally."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Select different " [:code ":justify"] " values and notice how the children are repositioned."]
                                [:li "Untick " [:code ":justify"] ". When not specified, the default value is :start."]]
                               [:p [:code ":justify"] " is the analog of " [hyperlink-href
                                                                            :label "Flexbox justify-content style"
                                                                            :href "https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content"
                                                                            :target "_blank"] "."]
                               [:p [:strong "Note: "] "There is a bug in Chrome. When switching from :between to :around (or visa versa), the change is not shown. To get around this, select a different value beforehand."]]]}

            ;; Align
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom true ) :range [0 200]}
                    :width      {:value "450px"  :omit? false :editing? (atom false) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false)}
                    :align      {:value :stretch :omit? false :editing? (atom true )}
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) :type :text :text "Box1"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "50px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) :type :text :text "Box2"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "100px" :ratio "2" :gsb "1 1 0px"}
                    :align-self {:value :center  :omit? false :editing? (atom true )}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) :type :text :text "Box3"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "150px" :ratio "1" :gsb "1 1 0px"}
                    :align-self {:value :end     :omit? false :editing? (atom true )}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "The " [:code ":align"] " & " [:code ":align-self"] " parameters"]
                               [:p [:code ":align"] " specifies how children are arranged vertically."]
                               [:p [:code ":align-self"] " is used on individual children to override the :align value specified by their parent."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Select different " [:code ":align"] " values for the h-box and notice how this only affects Box1 because both other boxes have an overriding :align-self setting."]
                                [:li "Change the " [:code ":align-self"] " values for Box2 and Box3 to see them adjust their vertical position."]
                                [:li "Adjust the h-box " [:code ":height"] " and notice boxes 2 and 3 sticking to their specified alignment."]]
                               [:p [:code ":align"] " is the analog of " [hyperlink-href
                                                                          :label "Flexbox align-items style"
                                                                          :href "https://developer.mozilla.org/en-US/docs/Web/CSS/align-items"
                                                                          :target "_blank"] "."]]]}

            ;; Size
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "450px"  :omit? false :editing? (atom true ) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false)}
                    :align      {:value :stretch :omit? true  :editing? (atom false)}
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) :type :text :text "Box1"}
                    :size       {:value "none"   :omit? false :editing? (atom true ) :type :none :px "50px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) :type :text :text "Box2"}
                    :size       {:value "100px"  :omit? false :editing? (atom true ) :type :px :px "100px" :ratio "2" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) :type :text :text "Box3"}
                    :size       {:value "1"      :omit? false :editing? (atom true ) :type :ratio :px "150px" :ratio "1" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "The " [:code ":size"] " parameter"]
                               [p "The "
                                [hyperlink-href
                                 :label [:span "Layout page"]
                                 :href "#/layout"
                                 :target "_blank"]
                                " describes the importance of " [:code ":size"] "."]
                               [:p [:strong "Box1"] " has a " [:code ":size"] " of \"none\" which means it will take up as much width as its content, in this case, the text \"Box1\"."]
                               [:p [:strong "Box2"] " has a fixed 100 pixel " [:code ":size"] ". Width in the case on an h-box."]
                               [:p [:strong "Box3"] " has a " [:code ":size"] " of \"1\" which means it will greedily take as much width as it can."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Adjust the h-box " [:code ":width"] " and notice how Box1 and Box2 don't change in width, and Box3 greedily takes any excess space and squeezes down to nothing as the h-box width is reduced further."]
                                [:li "Set the Box2 " [:code ":size"] " to a ratio value of \"2\" and notice how it will always take up double the width of Box3 (ratio \"1\") as you adjust the h-box " [:code ":width"] "."]
                                [:li "Set the Box2 " [:code ":size"] " to a gsb value of \"0 0 80%\". Its width is fixed to 80% of its parent h-box, with no growing or shrinking. Box1 and Box2 now have fixed widths. Box3 can grow and shrink. See this in action as you adjust the h-box " [:code ":width"] "."]]
                               [:p [:code ":size"] " is the analog of " [hyperlink-href
                                                                         :label "Flexbox flex style"
                                                                         :href "https://developer.mozilla.org/en-US/docs/Web/CSS/flex"
                                                                         :target "_blank"] "."]]]}

            ;; Size2
            {:hbox {:over?      false
                    :height     {:value "100px"     :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "500px"     :omit? false :editing? (atom true ) :range [0 1000]}
                    :justify    {:value :start      :omit? true  :editing? (atom false)}
                    :align      {:value :stretch    :omit? true  :editing? (atom false)}
                    :gap        {:value "4px"       :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"      :omit? false :editing? (atom false) :type :text :text "Box1"}
                    :size       {:value "100px"     :omit? false :editing? (atom false) :type :px :px "100px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch    :omit? true  :editing? (atom false)}
                    :height     {:value "50px"      :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"      :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"      :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"      :omit? false :editing? (atom false) :type :text :text "Box2"}
                    :size       {:value "5 1 200px" :omit? false :editing? (atom true ) :type :gsb :px "100px" :ratio "2" :gsb "5 1 200px"}
                    :align-self {:value :stretch    :omit? true  :editing? (atom false)}
                    :height     {:value "50px"      :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"      :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"      :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"      :omit? false :editing? (atom false) :type :text :text "Box3"}
                    :size       {:value "1 3 200px" :omit? false :editing? (atom true ) :type :gsb :px "150px" :ratio "1" :gsb "1 3 200px"}
                    :align-self {:value :stretch    :omit? true  :editing? (atom false)}
                    :height     {:value "50px"      :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"      :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"      :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "The " [:code ":size"] " parameter - Advanced GSB"]
                               [:p "This demonstrates a complex example of gsb."]
                               [:p [:strong "Box1"] " has a " [:code ":size"] " fixed to \"100px\"."]
                               [:p [:strong "Box2"] " has a gsb of \"5 1 200px\" so its natural width is 200px and when it grows, it will do so with a ratio of 5, compared with other growable siblings. When it shrinks, it will do so at a ratio of 1, compared with other shrinkable siblings."]
                               [:p [:strong "Box3"] " has a gsb of \"1 3 200px\" so its natural width is also 200px and when it grows, it will do so with a ratio of 1. When it shrinks, it will do so at a ratio of 3."]
                               [:p "Notice that the width of the h-box is initially set to 500px. Box1 (100px) + Box2 (200px basis) + Box3 (200px basis) = 500px so this width is the point of equilibrium. There is no shrinking or growing required so we see that Box2 and Box3 have exactly the same width of 200px."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Increase the h-box " [:code ":width"] " from the initial 500px. The extra space (excluding the initial 200px for Box2 and Box3) will be distributed between Box2 and Box3 in a ratio of 5 to 1, so Box2 will grow faster than Box3."]
                                [:li "Decrease the h-box " [:code ":width"] " from the initial 500px. The space to be removed from Box2 and Box3 will be removed in a ratio of 1 to 3, so Box3 will shrink faster than Box2 until it reaches 0px. Box2 will then continue to reduce until it too gets to 0px."]]]]}

            ;; Width
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "450px"  :omit? false :editing? (atom true ) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false)}
                    :align      {:value :stretch :omit? true  :editing? (atom false)}
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) :type :text :text "Box1"}
                    :size       {:value "auto"   :omit? false :editing? (atom true ) :type :auto :px "50px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "100px"  :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "200px"  :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) :type :text :text "Box2"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "100px" :ratio "2" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "100px"  :omit? false :editing? (atom true ) :range [0 200]}
                    :max-width  {:value "200px"  :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) :type :text :text "Box3"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "150px" :ratio "1" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "25px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "75px"   :omit? false :editing? (atom true ) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "The " [:code ":width"] " & " [:code ":min/max-width"] " parameters"]
                               [:p "It's interesting to see how child :min-width and :max-width parameters affect layout."]
                               [:p "All three boxes have " [:code ":size"] " set to :auto so they can grow and shrink as required."]
                               [:p "Box1 has no width restrictions, Box2 has a :min-width and Box3 has a :max-width."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Decrease the h-box " [:code ":width"] " and notice how Box2 shrinks only until it reaches the minimum of 100px."]
                                [:li "Increase the h-box " [:code ":width"] " and notice how Box3 grows only until it reaches the maximum of 75px."]
                                [:li "Turn on " [:code ":min-width"] " for Box3 and while adjusting the h-box " [:code ":width"] ", notice how Box3 grows & shrinks only between the min and max values."]]]]}

            ;; Height
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom true ) :range [0 200]}
                    :width      {:value "450px"  :omit? false :editing? (atom false) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false)}
                    :align      {:value :stretch :omit? false :editing? (atom false)}
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom false) :type :text :text "Box1"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "50px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "40px"   :omit? false :editing? (atom true ) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value "Box2"   :omit? false :editing? (atom false) :type :text :text "Box2"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "100px" :ratio "2" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "80px"   :omit? false :editing? (atom true ) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value "Box3"   :omit? false :editing? (atom false) :type :text :text "Box3"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "150px" :ratio "1" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "110px"  :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "The " [:code ":height"] " parameter"]
                               [:p "It's interesting to see how the child :height parameter affects layout."]
                               [:p "A specific :height has been set for Box1 and Box2, while Box3 has none, so its height is being set by the h-box :align :stretch setting."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Turn on the Box3 " [:code ":height"] " parameter and see how it can spill outside the h-box when it has a :height set."]
                                [:li "Turn off the h-box " [:code ":height"] " parameter then adjust the :height of one or more boxes. Notice how h-box :height is now determined by the height on the maximum box heights."]]]]}

            ;; Children
            {:hbox {:over?      false
                    :height     {:value "100px"  :omit? false :editing? (atom false) :range [0 200]}
                    :width      {:value "600px"  :omit? false :editing? (atom true ) :range [0 1000]}
                    :justify    {:value :start   :omit? true  :editing? (atom false)}
                    :align      {:value :stretch :omit? true  :editing? (atom false)}
                    :gap        {:value "4px"    :omit? false :editing? (atom false) :range [0 100]}}
             :box1 {:over?      false
                    :text       {:value "Box1"   :omit? false :editing? (atom true ) :type :text :text "Box1"}
                    :size       {:value "100px"  :omit? false :editing? (atom false) :type :px :px "100px" :ratio "3" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box2 {:over?      false
                    :text       {:value paragraph-filler :omit? false :editing? (atom true ) :type :paras :text "Box2"}
                    :size       {:value "auto"   :omit? false :editing? (atom false) :type :auto :px "100px" :ratio "2" :gsb "1 1 0px"}
                    :align-self {:value :stretch :omit? true  :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 300]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :box3 {:over?      false
                    :text       {:value buttons-filler :omit? false :editing? (atom true ) :type :buttons :text "Box3"}
                    :size       {:value "none"   :omit? false :editing? (atom false) :type :none :px "100px" :ratio "1" :gsb "1 1 0px"}
                    :align-self {:value :end     :omit? false :editing? (atom false)}
                    :height     {:value "50px"   :omit? true  :editing? (atom false) :range [0 400]}
                    :min-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}
                    :max-width  {:value "50px"   :omit? true  :editing? (atom false) :range [0 200]}}
             :desc [v-box
                    :children [[:p.info-subheading "The " [:code ":children"] " & " [:code ":child"] " parameters"]
                               [:p "The content of an h-box is specified by the " [:code ":children"] " parameter. It's a vector of n components."]
                               [:p "The content of a box is specified by the " [:code ":child"] " parameter. It is a single component."]
                               [:p.info-subheading "Things to try"]
                               [:ul
                                [:li "Adjust the h-box " [:code ":width"] " parameter to see how Box2 is the only one that shrinks and grows."]
                                [:li "Turn off the h-box " [:code ":height"] " parameter to see how it automatically expands to show the rest of the content in Box2."]
                                [:li "Edit the box " [:code ":child"] " parameters to change the content to see how the current layout handles more or less content."]]]]}])

(def box-state (reaction (get demos @current-demo)))

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

(defn child-editor
  "provides several options for the :child parameters, including typing your own text"
  [path]
  (let [opts         [{:id :text    :label "Text"}
                      {:id :paras   :label "Paragraphs"}
                      {:id :buttons :label "Buttons"}]
        model        (reaction (get-in @box-state (conj path :type)))
        text-model   (reaction (get-in @box-state (conj path :text)))
        update-model (fn [path item new-model]
                       (swap! box-state assoc-in (conj path item) new-model)
                       (swap! box-state assoc-in (conj path :value) (case (get-in @box-state (conj path :type))
                                                                      :text    @text-model
                                                                      :paras   paragraph-filler
                                                                      :buttons buttons-filler)))]
    (fn
      [path on-close]
      [h-box
       :align    :center
       :children [[horizontal-bar-tabs
                   :model     model
                   :tabs      opts
                   :style     editor-style
                   :on-change #(update-model path :type %)]
                  (when (= @model :text) [gap :size "8px" :width "8px"])
                  (when (= @model :text)
                    [input-text
                     :model           text-model
                     :change-on-blur? false
                     :style           editor-style
                     :on-change       #(update-model path :text %)])
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
      [v-box
       :gap      "4px"
       :children [[h-box
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
                              [close-button on-close]]]
                  [:span
                   {:style {:font-family "sans-serif"
                            :font-size   "10px"
                            :color       "#aaa"}}
                   "GSB: " (case @model
                             :inital "0 1 auto"
                             :auto   "1 1 auto"
                             :none   "0 0 auto"
                             :px     (str "0 0 " @px-model "px")
                             :ratio  (str @ratio-model " 1 0px")
                             :gsb    @gsb-model)]]])))





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
                                    (vector? val)  "[hiccup]"
                                    :else          (str "\"" val "\""))))
            row-active?       (and @mouse-over-row? active?)
            mouse-over-group? (= (nth path 0) (:over-group @box-state))
            show-checkbox?    (and editor (not (contains? (set path) :text))) ;; To only show on mouse over, use: (and row-active? (not (contains? (set path) :text)))
            allow-edit?       (and row-active? (not @omit?))
            editing?          (if editor (get-in @box-state editing?-path) (reagent/atom false))
            arg-hiccup        [h-box
                               :width     "242px"
                               :style    (merge {:overflow "hidden"}
                                                (when @editing?         {:background-color "#e8e8e8"})
                                                (when mouse-over-group? {:background-color "#e8e8e8"})
                                                (when row-active?       {:background-color "#d8d8d8"
                                                                         :cursor           "pointer"})
                                                (when @omit?            {:color            "#c0c0c0"}))
                               :attr     {:on-mouse-over #(mouse-over-fn true)
                                          :on-mouse-out  #(mouse-over-fn false)}
                               :children [[box
                                           :size "20px"
                                           :child (if show-checkbox?
                                                    [checkbox
                                                     :model     (not @omit?)
                                                     :style     {:opacity "0.6"}
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
          [popover-anchor-wrapper
           :showing? editing?
           :position :right-center
           :anchor   arg-hiccup
           :popover  [popover-content-wrapper
                      :showing? editing?
                      :position :right-center
                      :body     [editor path #(swap! box-state assoc-in editing?-path (atom false))]]]
          arg-hiccup)))))


(defn choose-a-demo
  "choose a demo to show"
  []
  (let [opts  [{:id 0 :label "Basic"}
               {:id 1 :label ":justify"}
               {:id 2 :label ":align"}
               {:id 3 :label ":size"}
               {:id 4 :label ":size2"}
               {:id 5 :label ":width"}
               {:id 6 :label ":height"}
               {:id 7 :label ":children"}
               ]]
    (fn
      []
      [h-box
       :gap      "8px"
       :align    :center
       :children [[:span.bold "Show me:"]
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
  (let [over-hbox  (fn [over?] (swap! box-state assoc-in [:hbox :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :hbox)))
        over-box1  (fn [over?] (swap! box-state assoc-in [:box1 :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :box1)))
        over-box2  (fn [over?] (swap! box-state assoc-in [:box2 :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :box2)))
        over-box3  (fn [over?] (swap! box-state assoc-in [:box3 :over?] over?) (swap! box-state assoc-in [:over-group] (when over? :box3)))]
    (fn []
      [h-box
       :align :start
       :children [(when (:desc @box-state)
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
                   :children [[code-row false :0 "[h-box"      [:hbox]             ""   over-hbox]
                              [code-row true  :1 ":height"     [:hbox :height]     ""   over-hbox px-editor]
                              [code-row true  :1 ":width"      [:hbox :width]      ""   over-hbox px-editor]
                              [code-row true  :1 ":justify"    [:hbox :justify]    ""   over-hbox justify-editor]
                              [code-row true  :1 ":align"      [:hbox :align]      ""   over-hbox align-editor]
                              [code-row true  :1 ":gap"        [:hbox :gap]        ""   over-hbox px-editor]
                              [code-row false :1 ":children [" [:hbox]             ""   over-hbox]

                              [code-row false :2 "[box "       [:box1]             ""   over-box1]
                              [code-row true  :3 ":child"      [:box1 :text]       ""   over-box1 child-editor]
                              [code-row true  :3 ":size"       [:box1 :size]       ""   over-box1 size-editor]
                              [code-row true  :3 ":align-self" [:box1 :align-self] ""   over-box1 align-editor]
                              [code-row true  :3 ":height"     [:box1 :height]     ""   over-box1 px-editor]
                              [code-row true  :3 ":min-width"  [:box1 :min-width]  ""   over-box1 px-editor]
                              [code-row true  :3 ":max-width"  [:box1 :max-width]  "]"  over-box1 px-editor]

                              [code-row false :2 "[box "       [:box2]             ""   over-box2]
                              [code-row true  :3 ":child"      [:box2 :text]       ""   over-box2 child-editor]
                              [code-row true  :3 ":size"       [:box2 :size]       ""   over-box2 size-editor]
                              [code-row true  :3 ":align-self" [:box2 :align-self] ""   over-box2 align-editor]
                              [code-row true  :3 ":height"     [:box2 :height]     ""   over-box2 px-editor]
                              [code-row true  :3 ":min-width"  [:box2 :min-width]  ""   over-box2 px-editor]
                              [code-row true  :3 ":max-width"  [:box2 :max-width]  "]"  over-box2 px-editor]

                              [code-row false :2 "[box "       [:box3]             ""   over-box3]
                              [code-row true  :3 ":child"      [:box3 :text]       ""   over-box3 child-editor]
                              [code-row true  :3 ":size"       [:box3 :size]       ""   over-box3 size-editor]
                              [code-row true  :3 ":align-self" [:box3 :align-self] ""   over-box3 align-editor]
                              [code-row true  :3 ":height"     [:box3 :height]     ""   over-box3 px-editor]
                              [code-row true  :3 ":min-width"  [:box3 :min-width]  ""   over-box3 px-editor]
                              [code-row true  :3 ":max-width"  [:box3 :max-width]  "]]" over-box3 px-editor]]]
                  (when (= @current-demo 0)
                    [:img {:src   "demo/h-box-demo-words.png"
                           :style {:flex        "none"
                                   :margin-left "20px"}}])]])))


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
                                        [p "The "
                                         [hyperlink-href
                                          :label     [:span.bold "Layout page"]
                                          :href      "#/layout"]
                                         " describes the importance of " [:span.bold ":size"] "."]
                                        [args-table h-box-args-desc]]]
                            [v-box
                             :gap      "10px"
                             :width    "650px"
                             :height   "800px"
                             ;:style    {:border "dashed 1px #ddd"} ;; Adds a slightly visible border around the h-box parent
                             :children [[title2 "Demo"]
                                        [editable-code]
                                        [gap :size "0px"]
                                        [demo]
                                        [gap :size "0px"]
                                        [choose-a-demo]]]]]]]))
